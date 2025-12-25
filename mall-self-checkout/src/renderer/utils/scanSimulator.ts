/**
 * 扫码模拟器 - 用于在浏览器中测试扫码功能
 */

export class ScanSimulator {
  private static instance: ScanSimulator | null = null
  private isSimulating = false

  static getInstance(): ScanSimulator {
    if (!ScanSimulator.instance) {
      ScanSimulator.instance = new ScanSimulator()
    }
    return ScanSimulator.instance
  }

  /**
   * 模拟扫码器输入
   * @param code 要模拟的条码/二维码内容
   * @param delay 字符间延迟（毫秒），默认50ms
   */
  async simulateScan(code: string, delay: number = 50): Promise<void> {
    if (this.isSimulating) {
      console.warn('⚠️ 正在模拟扫码中，请等待完成')
      return
    }

    this.isSimulating = true
    console.log(`🎯 开始模拟扫码: ${code}`)

    try {
      // 逐字符发送键盘事件
      for (let i = 0; i < code.length; i++) {
        const char = code[i]
        
        // 创建键盘事件
        const keyEvent = new KeyboardEvent('keydown', {
          key: char,
          code: `Key${char.toUpperCase()}`,
          keyCode: char.charCodeAt(0),
          which: char.charCodeAt(0),
          bubbles: true,
          cancelable: true
        })

        // 发送事件
        document.dispatchEvent(keyEvent)
        console.log(`📝 模拟输入字符: ${char}`)

        // 等待延迟
        if (i < code.length - 1) {
          await this.sleep(delay)
        }
      }

      // 发送回车键表示扫码完成
      await this.sleep(delay)
      const enterEvent = new KeyboardEvent('keydown', {
        key: 'Enter',
        code: 'Enter',
        keyCode: 13,
        which: 13,
        bubbles: true,
        cancelable: true
      })

      document.dispatchEvent(enterEvent)
      console.log('✅ 模拟扫码完成，发送Enter键')

    } catch (error) {
      console.error('❌ 模拟扫码失败:', error)
    } finally {
      this.isSimulating = false
    }
  }

  /**
   * 快速模拟商品条码扫描
   */
  async simulateProductBarcode(barcode: string = '020306001'): Promise<void> {
    console.log('🛍️ 模拟商品条码扫描')
    await this.simulateScan(barcode)
  }

  /**
   * 快速模拟会员码扫描
   */
  async simulateMemberCode(memberCode: string = 'M12345678901'): Promise<void> {
    console.log('👤 模拟会员码扫描')
    await this.simulateScan(memberCode)
  }

  /**
   * 快速模拟付款码扫描
   */
  async simulatePaymentCode(paymentCode: string = '134567890123456789'): Promise<void> {
    console.log('💳 模拟付款码扫描')
    await this.simulateScan(paymentCode)
  }

  /**
   * 睡眠函数
   */
  private sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * 检查是否正在模拟
   */
  isSimulatingNow(): boolean {
    return this.isSimulating
  }
}

// 导出单例实例
export const scanSimulator = ScanSimulator.getInstance()

// 挂载到全局对象，方便在控制台调用
if (typeof window !== 'undefined') {
  (window as any).scanSimulator = scanSimulator
}
