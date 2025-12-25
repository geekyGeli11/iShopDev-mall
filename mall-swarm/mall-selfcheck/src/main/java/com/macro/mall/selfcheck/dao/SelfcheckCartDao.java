package com.macro.mall.selfcheck.dao;

import com.macro.mall.selfcheck.dto.CartItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自助收银购物车数据访问接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckCartDao {

    /**
     * 根据会员ID查询购物车商品列表
     * 
     * @param memberId 会员ID
     * @return 购物车商品列表
     */
    List<CartItemVO> getMemberCartItems(@Param("memberId") Long memberId);

    /**
     * 根据商品ID和SKU ID查询购物车项
     * 
     * @param memberId 会员ID
     * @param productId 商品ID
     * @param skuId SKU ID（可为null）
     * @return 购物车项
     */
    CartItemVO getMemberCartItem(@Param("memberId") Long memberId, 
                                @Param("productId") Long productId, 
                                @Param("skuId") Long skuId);

    /**
     * 添加商品到会员购物车
     * 
     * @param memberId 会员ID
     * @param productId 商品ID
     * @param skuId SKU ID（可为null）
     * @param quantity 数量
     * @param remark 备注
     * @return 影响行数
     */
    int addMemberCartItem(@Param("memberId") Long memberId, 
                         @Param("productId") Long productId, 
                         @Param("skuId") Long skuId, 
                         @Param("quantity") Integer quantity, 
                         @Param("remark") String remark);

    /**
     * 更新会员购物车商品数量
     * 
     * @param memberId 会员ID
     * @param productId 商品ID
     * @param skuId SKU ID（可为null）
     * @param quantity 新数量
     * @return 影响行数
     */
    int updateMemberCartItemQuantity(@Param("memberId") Long memberId, 
                                   @Param("productId") Long productId, 
                                   @Param("skuId") Long skuId, 
                                   @Param("quantity") Integer quantity);

    /**
     * 删除会员购物车商品
     * 
     * @param memberId 会员ID
     * @param productId 商品ID
     * @param skuId SKU ID（可为null）
     * @return 影响行数
     */
    int removeMemberCartItem(@Param("memberId") Long memberId, 
                           @Param("productId") Long productId, 
                           @Param("skuId") Long skuId);

    /**
     * 清空会员购物车
     * 
     * @param memberId 会员ID
     * @return 影响行数
     */
    int clearMemberCart(@Param("memberId") Long memberId);

    /**
     * 获取会员购物车商品数量统计
     * 
     * @param memberId 会员ID
     * @return 商品总数量
     */
    int getMemberCartTotalQuantity(@Param("memberId") Long memberId);

    /**
     * 批量删除会员购物车商品
     * 
     * @param memberId 会员ID
     * @param productIds 商品ID列表
     * @return 影响行数
     */
    int batchRemoveMemberCartItems(@Param("memberId") Long memberId, 
                                 @Param("productIds") List<Long> productIds);

    /**
     * 检查商品是否在购物车中
     * 
     * @param memberId 会员ID
     * @param productId 商品ID
     * @param skuId SKU ID（可为null）
     * @return 是否存在
     */
    boolean isProductInCart(@Param("memberId") Long memberId, 
                           @Param("productId") Long productId, 
                           @Param("skuId") Long skuId);

    /**
     * 获取购物车详细信息（包含商品信息、价格、库存等）
     * 
     * @param memberId 会员ID
     * @param memberLevel 会员等级（用于价格计算）
     * @return 详细购物车商品列表
     */
    List<CartItemVO> getMemberCartDetailItems(@Param("memberId") Long memberId, 
                                            @Param("memberLevel") Integer memberLevel);

    /**
     * 获取无效的购物车商品（已下架、删除、库存不足等）
     * 
     * @param memberId 会员ID
     * @return 无效商品列表
     */
    List<CartItemVO> getInvalidCartItems(@Param("memberId") Long memberId);

    /**
     * 删除无效的购物车商品
     * 
     * @param memberId 会员ID
     * @return 影响行数
     */
    int removeInvalidCartItems(@Param("memberId") Long memberId);
} 