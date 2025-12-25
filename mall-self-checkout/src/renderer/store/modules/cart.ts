/**
 * 购物车状态管理
 */
import { defineStore } from 'pinia'
import { CartAPI } from '../../api'
import { useMemberStore } from './member'
import type { 
  CartItemParam,
  CartVO,
  CartItemVO,
  MemberCouponVO,
  ApiResponse
} from '@shared/types'

/**
 * 购物车状态
 */
interface CartState {
  // 购物车信息
  cartData: CartVO | null
  
  // 已选择的优惠券
  selectedCoupons: MemberCouponVO[]
  
  // 加载状态
  loading: boolean
  updating: boolean
  
  // 是否显示优惠券选择弹窗
  showCouponDialog: boolean
  
  // 计算结果缓存
  calculationCache: {
    timestamp: number
    totalAmount: number
    discountAmount: number
    payAmount: number
    availableCoupons: MemberCouponVO[]
  } | null
}

export const useCartStore = defineStore('cart', {
  state: (): CartState => ({
    cartData: null,
    selectedCoupons: [],
    loading: false,
    updating: false,
    showCouponDialog: false,
    calculationCache: null
  }),

  getters: {
    /**
     * 购物车商品列表
     */
    cartItems: (state): CartItemVO[] => {
      return state.cartData?.items || []
    },

    /**
     * 购物车商品数量
     */
    totalQuantity: (state): number => {
      return state.cartData?.totalQuantity || 0
    },

    /**
     * 商品总金额
     */
    totalAmount: (state): number => {
      return state.cartData?.totalAmount || 0
    },

    /**
     * 优惠券折扣金额
     */
    couponDiscount: (state): number => {
      return state.selectedCoupons.reduce((total, coupon) => {
        return total + (coupon.amount || 0)
      }, 0)
    },

    /**
     * 会员折扣金额
     */
    memberDiscount: (state): number => {
      return state.cartData?.memberDiscount || 0
    },

    /**
     * 总折扣金额
     */
    totalDiscount(): number {
      const memberDiscount = this.cartData?.discountAmount || 0
      const couponDiscount = this.couponDiscount
      return memberDiscount + couponDiscount
    },

    /**
     * 实际支付金额
     */
    payAmount(): number {
      const total = this.cartData?.totalAmount || 0
      const discount = this.totalDiscount
      return Math.max(0, total - discount)
    },

    /**
     * 是否为空购物车
     */
    isEmpty: (state): boolean => {
      return !state.cartData || state.cartData.items.length === 0
    },

    /**
     * 已选中的商品
     */
    selectedItems: (state): CartItemVO[] => {
      return state.cartData?.items.filter((item: any) => item.checked) || []
    },

    /**
     * 选中商品的总数量
     */
    selectedQuantity(): number {
      return this.selectedItems.reduce((total, item) => total + item.quantity, 0)
    },

    /**
     * 选中商品的总金额
     */
    selectedAmount(): number {
      return this.selectedItems.reduce((total, item: any) => {
        return total + ((item.price || item.currentPrice) * item.quantity)
      }, 0)
    },

    /**
     * 是否可以使用优惠券
     */
    canUseCoupon(): boolean {
      const memberStore = useMemberStore()
      return memberStore.isMemberLogin && this.selectedAmount > 0
    },

    /**
     * 购物车摘要信息
     */
    cartSummary(): {
      itemCount: number
      totalAmount: number
      discountAmount: number
      payAmount: number
      saveAmount: number
    } {
      const totalAmount = this.selectedAmount
      const discountAmount = this.totalDiscount
      const payAmount = Math.max(0, totalAmount - discountAmount)
      const saveAmount = discountAmount
      
      return {
        itemCount: this.selectedQuantity,
        totalAmount,
        discountAmount,
        payAmount,
        saveAmount
      }
    }
  },

  actions: {
    /**
     * 加载购物车
     */
    async loadCart(): Promise<void> {
      const memberStore = useMemberStore()
      
      if (!memberStore.isLoggedIn) {
        this.cartData = null
        return
      }

      this.loading = true

      try {
        const response = await CartAPI.getCart()
        
        if (response.code === 200 && response.data) {
          this.cartData = response.data
          
          // 清除过期的计算缓存
          this.clearCalculationCache()
          
          console.log('购物车加载成功:', response.data)
        } else {
          throw new Error(response.message || '获取购物车失败')
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        this.cartData = null
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * 添加商品到购物车
     */
    async addItem(param: CartItemParam): Promise<void> {
      this.updating = true

      try {
        const response = await CartAPI.addItem(param)
        
        if (response.code === 200) {
          // 重新加载购物车
          await this.loadCart()
          
          console.log('商品添加成功:', param)
        } else {
          throw new Error(response.message || '添加商品失败')
        }
      } catch (error) {
        console.error('添加商品失败:', error)
        throw error
      } finally {
        this.updating = false
      }
    },

    /**
     * 更新商品数量
     */
    async updateItemQuantity(
      productId: number, 
      skuId: number | undefined, 
      quantity: number
    ): Promise<void> {
      if (quantity <= 0) {
        await this.removeItem(productId, skuId)
        return
      }

      this.updating = true

      try {
        const param: CartItemParam = {
          productId,
          skuId,
          quantity
        }

        const response = await CartAPI.updateItem(param)
        
        if (response.code === 200) {
          // 更新本地数据
          this.updateLocalItemQuantity(productId, skuId, quantity)
          
          console.log('商品数量更新成功:', param)
        } else {
          throw new Error(response.message || '更新商品数量失败')
        }
      } catch (error) {
        console.error('更新商品数量失败:', error)
        throw error
      } finally {
        this.updating = false
      }
    },

    /**
     * 删除购物车商品
     */
    async removeItem(productId: number, skuId?: number): Promise<void> {
      this.updating = true

      try {
        const param: CartItemParam = {
          productId,
          skuId,
          quantity: 0
        }

        const response = await CartAPI.removeItem(param)
        
        if (response.code === 200) {
          // 更新本地数据
          this.removeLocalItem(productId, skuId)
          
          console.log('商品删除成功:', param)
        } else {
          throw new Error(response.message || '删除商品失败')
        }
      } catch (error) {
        console.error('删除商品失败:', error)
        throw error
      } finally {
        this.updating = false
      }
    },

    /**
     * 选择/取消选择商品
     */
    async toggleItemSelection(productId: number, skuId?: number): Promise<void> {
      if (!this.cartData) return

      // 找到对应商品
      const item = this.cartData.items.find((item: any) => 
        item.productId === productId && item.skuId === skuId
      )

      if (!item) return

      try {
        const response = await CartAPI.toggleItemCheck(productId, skuId, !item.checked)
        
        if (response.code === 200) {
          // 更新本地状态
          item.checked = !item.checked
          
          // 重新计算金额
          this.recalculateCart()
          
          console.log('商品选择状态更新:', { productId, skuId, checked: item.checked })
        } else {
          throw new Error(response.message || '更新选择状态失败')
        }
      } catch (error) {
        console.error('更新商品选择状态失败:', error)
        throw error
      }
    },

    /**
     * 全选/全不选
     */
    async toggleSelectAll(): Promise<void> {
      if (!this.cartData) return

      const allSelected = this.cartData.items.every((item: any) => item.checked)
      const newCheckedState = !allSelected

      try {
        const response = await CartAPI.toggleSelectAll(newCheckedState)
        
        if (response.code === 200) {
          // 更新本地状态
          this.cartData.items.forEach((item: any) => {
            item.checked = newCheckedState
          })
          
          // 重新计算金额
          this.recalculateCart()
          
          console.log('全选状态更新:', newCheckedState)
        } else {
          throw new Error(response.message || '更新全选状态失败')
        }
      } catch (error) {
        console.error('更新全选状态失败:', error)
        throw error
      }
    },

    /**
     * 获取可用优惠券
     */
    async getAvailableCoupons(): Promise<MemberCouponVO[]> {
      const memberStore = useMemberStore()
      
      if (!memberStore.isMemberLogin) {
        return []
      }

      try {
        const response = await CartAPI.getAvailableCoupons({
          totalAmount: this.selectedAmount,
          cartItems: this.selectedItems.map((item: any) => ({
            productId: item.productId,
            skuId: item.skuId,
            quantity: item.quantity,
            price: item.price || item.currentPrice
          }))
        })
        
        if (response.code === 200 && response.data) {
          return response.data
        } else {
          console.warn('获取可用优惠券失败:', response.message)
          return []
        }
      } catch (error) {
        console.error('获取可用优惠券失败:', error)
        return []
      }
    },

    /**
     * 应用优惠券
     */
    async applyCoupon(coupon: MemberCouponVO): Promise<void> {
      // 检查是否已选择
      const isSelected = this.selectedCoupons.some(c => c.couponId === coupon.couponId)
      
      if (isSelected) {
        // 取消选择
        this.selectedCoupons = this.selectedCoupons.filter(c => c.couponId !== coupon.couponId)
      } else {
        // 检查是否可以叠加使用（暂时默认不可叠加）
        this.selectedCoupons = [coupon]
      }

      // 重新计算购物车金额
      await this.calculateCartWithCoupons()
      
      console.log('优惠券应用状态更新:', this.selectedCoupons)
    },

    /**
     * 清除所有优惠券
     */
    clearCoupons(): void {
      this.selectedCoupons = []
      this.recalculateCart()
    },

    /**
     * 计算购物车金额（包含优惠券）
     */
    async calculateCartWithCoupons(): Promise<void> {
      if (!this.cartData || this.selectedItems.length === 0) return

      try {
        const response = await CartAPI.calculateCart({
          cartItems: this.selectedItems.map((item: any) => ({
            productId: item.productId,
            skuId: item.skuId,
            quantity: item.quantity,
            price: item.price || item.currentPrice
          })),
          couponIds: this.selectedCoupons.map(c => c.couponId)
        })
        
        if (response.code === 200 && response.data) {
          // 更新计算缓存
          this.calculationCache = {
            timestamp: Date.now(),
            totalAmount: response.data.totalAmount,
            discountAmount: response.data.discountAmount,
            payAmount: response.data.finalAmount,
            availableCoupons: response.data.appliedCoupons || []
          }
          
          console.log('购物车金额计算完成:', response.data)
        }
      } catch (error) {
        console.error('计算购物车金额失败:', error)
      }
    },

    /**
     * 清空购物车
     */
    async clearCart(): Promise<void> {
      this.updating = true

      try {
        const response = await CartAPI.clearCart()
        
        if (response.code === 200) {
          this.cartData = null
          this.selectedCoupons = []
          this.clearCalculationCache()
          
          console.log('购物车已清空')
        } else {
          throw new Error(response.message || '清空购物车失败')
        }
      } catch (error) {
        console.error('清空购物车失败:', error)
        throw error
      } finally {
        this.updating = false
      }
    },

    /**
     * 清空本地购物车数据（不调用后端API）
     */
    clearLocalCart(): void {
      // 清空store数据
      this.cartData = null
      this.selectedCoupons = []
      this.clearCalculationCache()
      
      // 清空localStorage中的购物车相关数据
      localStorage.removeItem('cart_items')
      localStorage.removeItem('cart_data')
      localStorage.removeItem('shopping_cart')
      localStorage.removeItem('guest_cart')
      
      console.log('本地购物车数据已清空')
    },

    /**
     * 本地更新商品数量（优化用户体验）
     */
    updateLocalItemQuantity(productId: number, skuId: number | undefined, quantity: number): void {
      if (!this.cartData) return

      const item = this.cartData.items.find((item: any) => 
        item.productId === productId && item.skuId === skuId
      )

      if (item) {
        item.quantity = quantity
        this.recalculateCart()
      }
    },

    /**
     * 本地删除商品（优化用户体验）
     */
    removeLocalItem(productId: number, skuId?: number): void {
      if (!this.cartData) return

      this.cartData.items = this.cartData.items.filter((item: any) => 
        !(item.productId === productId && item.skuId === skuId)
      )
      
      this.recalculateCart()
    },

    /**
     * 重新计算购物车金额
     */
    recalculateCart(): void {
      if (!this.cartData) return

      // 计算总数量
      this.cartData.totalQuantity = this.cartData.items.reduce(
        (total: number, item: any) => total + item.quantity, 0
      )

      // 计算总金额
      this.cartData.totalAmount = this.cartData.items.reduce(
        (total: number, item: any) => total + ((item.price || item.currentPrice) * item.quantity), 0
      )

      // 清除计算缓存
      this.clearCalculationCache()
    },

    /**
     * 清除计算缓存
     */
    clearCalculationCache(): void {
      this.calculationCache = null
    },

    /**
     * 显示优惠券选择弹窗
     */
    showCouponSelector(): void {
      this.showCouponDialog = true
    },

    /**
     * 隐藏优惠券选择弹窗
     */
    hideCouponSelector(): void {
      this.showCouponDialog = false
    },

    /**
     * 获取购物车商品（用于下单）
     */
    getCartItemsForOrder(): Array<{
      productId: number
      skuId?: number
      quantity: number
      price: number
    }> {
      return this.selectedItems.map((item: any) => ({
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity,
        price: item.price || item.currentPrice
      }))
    },

    /**
     * 获取选中的优惠券ID
     */
    getSelectedCouponIds(): number[] {
      return this.selectedCoupons.map(c => c.couponId)
    },

    /**
     * 购物车是否有变化（基于时间戳）
     */
    hasCartChanged(since: number): boolean {
      return !this.cartData || (this.cartData.updateTime ? new Date(this.cartData.updateTime).getTime() : 0) > since
    },

    /**
     * 验证购物车状态
     */
    async validateCart(): Promise<{
      valid: boolean
      issues: string[]
    }> {
      const issues: string[] = []

      if (!this.cartData || this.isEmpty) {
        issues.push('购物车为空')
        return { valid: false, issues }
      }

      if (this.selectedItems.length === 0) {
        issues.push('请选择需要购买的商品')
        return { valid: false, issues }
      }

      // 验证商品库存
      for (const item of this.selectedItems) {
        if (item.stock !== undefined && item.quantity > item.stock) {
          issues.push(`${item.productName} 库存不足`)
        }
      }

      // 验证优惠券
      for (const coupon of this.selectedCoupons) {
        if (coupon.minPoint && this.selectedAmount < coupon.minPoint) {
          issues.push(`${coupon.name} 使用门槛不满足`)
        }
      }

      return {
        valid: issues.length === 0,
        issues
      }
    }
  }
}) 