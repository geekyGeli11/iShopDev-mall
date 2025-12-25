@echo off
chcp 65001
echo ==============================================
echo Mall 自助收银系统 API 测试脚本
echo ==============================================
echo.

set BASE_URL=http://localhost:8084

echo [1] 测试服务健康状态...
curl -s -X GET "%BASE_URL%/selfcheck/test/health" && echo. || echo 失败

echo.
echo [2] 测试服务信息...
curl -s -X GET "%BASE_URL%/selfcheck/test/info" && echo. || echo 失败

echo.
echo [3] 测试验证付款码格式...
curl -s -X POST "%BASE_URL%/selfcheck/payment/validateCode?paymentCode=134567890123456789" && echo. || echo 失败

echo.
echo [4] 测试支付方式检测...
curl -s -X POST "%BASE_URL%/selfcheck/payment/detectPayType?paymentCode=25012345678901234" && echo. || echo 失败

echo.
echo [5] 测试生成收款二维码...
curl -s -X POST "%BASE_URL%/selfcheck/payment/generateQR" ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\": 1001, \"amount\": 99.90, \"payType\": \"WECHAT\", \"title\": \"自助收银付款\"}" && echo. || echo 失败

echo.
echo [6] 测试扫码支付...
curl -s -X POST "%BASE_URL%/selfcheck/payment/scanCode" ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\": 1001, \"paymentCode\": \"134567890123456789\", \"amount\": 99.90, \"payType\": \"WECHAT\"}" && echo. || echo 失败

echo.
echo [7] 测试游客登录...
curl -s -X POST "%BASE_URL%/selfcheck/member/guestLogin?deviceId=TEST001&deviceType=PC" && echo. || echo 失败

echo.
echo [8] 测试发送验证码...
curl -s -X POST "%BASE_URL%/selfcheck/member/sendVerifyCode?telephone=13800138000" && echo. || echo 失败

echo.
echo ==============================================
echo API 测试完成
echo ==============================================
pause 