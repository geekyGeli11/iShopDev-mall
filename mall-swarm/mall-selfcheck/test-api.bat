@echo off
chcp 65001 > nul
echo ===============================================
echo Mall 自助收银系统 API 全面测试
echo ===============================================
echo.

set BASE_URL=http://localhost:8084
set /a test_count=0
set /a success_count=0
set /a failed_count=0

:: 检查服务是否运行
echo [检查] 检测服务运行状态...
curl -s -m 5 "%BASE_URL%/selfcheck/test/health" > nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 服务未运行，请先启动 mall-selfcheck 服务
    echo    启动命令: mvn spring-boot:run -Dspring-boot.run.profiles=dev
    pause
    exit /b 1
)
echo ✅ 服务运行正常

echo.
echo ===============================================
echo 1. 系统基础测试
echo ===============================================

:: 健康检查
set /a test_count+=1
echo [%test_count%] 健康检查...
curl -s "%BASE_URL%/selfcheck/test/health"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 健康检查成功
) else (
    set /a failed_count+=1
    echo ❌ 健康检查失败
)
echo.

:: 服务信息
set /a test_count+=1
echo [%test_count%] 获取服务信息...
curl -s "%BASE_URL%/selfcheck/test/info"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 服务信息获取成功
) else (
    set /a failed_count+=1
    echo ❌ 服务信息获取失败
)
echo.

echo ===============================================
echo 2. 会员认证测试
echo ===============================================

:: 游客登录
set /a test_count+=1
echo [%test_count%] 游客登录...
curl -s -X POST "%BASE_URL%/selfcheck/member/guestLogin?deviceId=TEST_DEVICE_001&deviceType=WINDOWS"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 游客登录成功
) else (
    set /a failed_count+=1
    echo ❌ 游客登录失败
)
echo.

:: 发送验证码
set /a test_count+=1
echo [%test_count%] 发送验证码...
curl -s -X POST "%BASE_URL%/selfcheck/member/sendVerifyCode?telephone=13800138000"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 验证码发送成功
) else (
    set /a failed_count+=1
    echo ❌ 验证码发送失败
)
echo.

:: 会员登录（使用默认验证码）
set /a test_count+=1
echo [%test_count%] 会员登录...
curl -s -X POST "%BASE_URL%/selfcheck/member/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"telephone\": \"13800138000\", \"verifyCode\": \"123456\"}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 会员登录成功
) else (
    set /a failed_count+=1
    echo ❌ 会员登录失败
)
echo.

echo ===============================================
echo 3. 商品扫码测试
echo ===============================================

:: 扫码查询商品
set /a test_count+=1
echo [%test_count%] 扫码查询商品...
curl -s -X POST "%BASE_URL%/selfcheck/product/scan" ^
  -H "Content-Type: application/json" ^
  -d "{\"barcode\": \"6901234567890\", \"needStockCheck\": true, \"needPromotionInfo\": true}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 商品扫码成功
) else (
    set /a failed_count+=1
    echo ❌ 商品扫码失败
)
echo.

:: 快速扫码
set /a test_count+=1
echo [%test_count%] 快速扫码...
curl -s "%BASE_URL%/selfcheck/product/quickScan?barcode=6901234567890"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 快速扫码成功
) else (
    set /a failed_count+=1
    echo ❌ 快速扫码失败
)
echo.

:: 验证条码格式
set /a test_count+=1
echo [%test_count%] 验证条码格式...
curl -s "%BASE_URL%/selfcheck/product/validateBarcode?barcode=6901234567890"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 条码验证成功
) else (
    set /a failed_count+=1
    echo ❌ 条码验证失败
)
echo.

echo ===============================================
echo 4. 购物车测试
echo ===============================================

:: 添加商品到购物车
set /a test_count+=1
echo [%test_count%] 添加商品到购物车...
curl -s -X POST "%BASE_URL%/selfcheck/cart/addItem" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\": 1, \"skuId\": 1, \"quantity\": 2}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 商品添加成功
) else (
    set /a failed_count+=1
    echo ❌ 商品添加失败
)
echo.

:: 扫码添加商品
set /a test_count+=1
echo [%test_count%] 扫码添加商品...
curl -s -X POST "%BASE_URL%/selfcheck/cart/scanAdd" ^
  -H "Content-Type: application/json" ^
  -d "{\"barcode\": \"6901234567890\", \"quantity\": 1}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 扫码添加成功
) else (
    set /a failed_count+=1
    echo ❌ 扫码添加失败
)
echo.

:: 查看购物车
set /a test_count+=1
echo [%test_count%] 查看购物车...
curl -s "%BASE_URL%/selfcheck/cart/list"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 购物车查看成功
) else (
    set /a failed_count+=1
    echo ❌ 购物车查看失败
)
echo.

:: 计算购物车
set /a test_count+=1
echo [%test_count%] 计算购物车...
curl -s -X POST "%BASE_URL%/selfcheck/cart/calculate"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 购物车计算成功
) else (
    set /a failed_count+=1
    echo ❌ 购物车计算失败
)
echo.

echo ===============================================
echo 5. 支付功能测试
echo ===============================================

:: 验证付款码格式
set /a test_count+=1
echo [%test_count%] 验证微信付款码...
curl -s -X POST "%BASE_URL%/selfcheck/payment/validateCode?paymentCode=134567890123456789"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 微信付款码验证成功
) else (
    set /a failed_count+=1
    echo ❌ 微信付款码验证失败
)
echo.

set /a test_count+=1
echo [%test_count%] 验证支付宝付款码...
curl -s -X POST "%BASE_URL%/selfcheck/payment/validateCode?paymentCode=25012345678901234"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 支付宝付款码验证成功
) else (
    set /a failed_count+=1
    echo ❌ 支付宝付款码验证失败
)
echo.

:: 检测支付方式
set /a test_count+=1
echo [%test_count%] 检测支付方式...
curl -s -X POST "%BASE_URL%/selfcheck/payment/detectPayType?paymentCode=134567890123456789"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 支付方式检测成功
) else (
    set /a failed_count+=1
    echo ❌ 支付方式检测失败
)
echo.

:: 生成收款二维码
set /a test_count+=1
echo [%test_count%] 生成收款二维码...
curl -s -X POST "%BASE_URL%/selfcheck/payment/generateQR" ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\": 1001, \"amount\": 99.90, \"payType\": \"WECHAT\", \"title\": \"购物付款\", \"terminalCode\": \"SC001\"}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 收款二维码生成成功
) else (
    set /a failed_count+=1
    echo ❌ 收款二维码生成失败
)
echo.

:: 扫码支付
set /a test_count+=1
echo [%test_count%] 模拟扫码支付...
curl -s -X POST "%BASE_URL%/selfcheck/payment/scanCode" ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\": 1001, \"paymentCode\": \"134567890123456789\", \"amount\": 99.90, \"payType\": \"WECHAT\", \"terminalCode\": \"SC001\"}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 扫码支付成功
) else (
    set /a failed_count+=1
    echo ❌ 扫码支付失败
)
echo.

echo ===============================================
echo 6. 订单管理测试
echo ===============================================

:: 创建快速订单
set /a test_count+=1
echo [%test_count%] 创建快速订单...
curl -s -X POST "%BASE_URL%/selfcheck/order/createQuick" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\": 1, \"skuId\": 1, \"quantity\": 1, \"useIntegration\": true}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 快速订单创建成功
) else (
    set /a failed_count+=1
    echo ❌ 快速订单创建失败
)
echo.

:: 从购物车创建订单
set /a test_count+=1
echo [%test_count%] 从购物车创建订单...
curl -s -X POST "%BASE_URL%/selfcheck/order/createFromCart" ^
  -H "Content-Type: application/json" ^
  -d "{\"useIntegration\": false, \"couponId\": null}"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 购物车订单创建成功
) else (
    set /a failed_count+=1
    echo ❌ 购物车订单创建失败
)
echo.

:: 查看订单历史
set /a test_count+=1
echo [%test_count%] 查看订单历史...
curl -s "%BASE_URL%/selfcheck/order/history?pageNum=1&pageSize=10"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 订单历史查看成功
) else (
    set /a failed_count+=1
    echo ❌ 订单历史查看失败
)
echo.

echo ===============================================
echo 7. 优惠券测试
echo ===============================================

:: 获取可用优惠券
set /a test_count+=1
echo [%test_count%] 获取可用优惠券...
curl -s "%BASE_URL%/selfcheck/coupon/available"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 优惠券获取成功
) else (
    set /a failed_count+=1
    echo ❌ 优惠券获取失败
)
echo.

:: 优惠券统计
set /a test_count+=1
echo [%test_count%] 优惠券统计...
curl -s "%BASE_URL%/selfcheck/coupon/statistics"
if %errorlevel% equ 0 (
    set /a success_count+=1
    echo ✅ 优惠券统计成功
) else (
    set /a failed_count+=1
    echo ❌ 优惠券统计失败
)
echo.

echo.
echo ===============================================
echo 测试结果汇总
echo ===============================================
echo 总测试数: %test_count%
echo 成功数量: %success_count%
echo 失败数量: %failed_count%

if %failed_count% equ 0 (
    echo.
    echo 🎉 所有测试通过！系统运行正常
    echo.
    echo 📋 下一步建议:
    echo    1. 前端项目配置后端API地址
    echo    2. 在前端应用中测试实际业务流程
    echo    3. 集成真实支付接口
    echo    4. 进行端到端测试
) else (
    echo.
    echo ⚠️  有 %failed_count% 项测试失败，请检查：
    echo    1. 确认服务已正常启动
    echo    2. 检查数据库连接是否正常
    echo    3. 查看后端日志排查问题
    echo    4. 确认网络连接正常
)

echo.
echo 测试完成，按任意键退出...
pause > nul 