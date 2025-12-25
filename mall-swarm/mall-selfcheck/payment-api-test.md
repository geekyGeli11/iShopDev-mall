# 自助收银支付系统API测试文档

## 测试环境

- **服务地址**: http://localhost:8086
- **基础路径**: /selfcheck/payment
- **Content-Type**: application/json

## 1. 生成收款二维码

### 接口说明
生成微信或支付宝收款二维码，用户扫码支付

### 请求方式
```
POST /selfcheck/payment/generateQR
```

### 请求示例
```json
{
  "orderId": 1001,
  "amount": 99.90,
  "payType": "WECHAT",
  "title": "自助收银付款",
  "description": "自助收银系统付款",
  "terminalCode": "SC001",
  "expireMinutes": 5
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paymentId": "PAY1703123456789ABC12345",
    "orderId": 1001,
    "payType": "WECHAT",
    "payStatus": "PENDING",
    "amount": 99.90,
    "qrCodeUrl": "data:image/png;base64,iVBORw0KGgo...",
    "qrCodeText": "mock://wechat/pay?id=PAY1703123456789ABC12345&amount=99.90",
    "createTime": "2023-12-21T10:30:00.000+00:00",
    "terminalCode": "SC001"
  }
}
```

## 2. 扫描付款码支付

### 接口说明
扫描用户的微信或支付宝付款码进行支付

### 请求方式
```
POST /selfcheck/payment/scanCode
```

### 请求示例
```json
{
  "orderId": 1001,
  "paymentCode": "134567890123456789",
  "amount": 99.90,
  "payType": "WECHAT",
  "terminalCode": "SC001",
  "remark": "自助收银付款"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paymentId": "PAY1703123456789DEF67890",
    "orderId": 1001,
    "payType": "WECHAT",
    "payStatus": "SUCCESS",
    "amount": 99.90,
    "paidAmount": 99.90,
    "transactionId": "MOCK_1703123456789",
    "payTime": "2023-12-21T10:30:05.000+00:00",
    "createTime": "2023-12-21T10:30:00.000+00:00",
    "terminalCode": "SC001"
  }
}
```

## 3. 查询支付状态

### 接口说明
根据支付ID查询支付状态，用于支付状态轮询

### 请求方式
```
GET /selfcheck/payment/status/{paymentId}
```

### 请求示例
```
GET /selfcheck/payment/status/PAY1703123456789ABC12345
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paymentId": "PAY1703123456789ABC12345",
    "orderId": 1001,
    "payType": "WECHAT",
    "payStatus": "SUCCESS",
    "amount": 99.90,
    "paidAmount": 99.90,
    "transactionId": "4200001234567890123456789",
    "payTime": "2023-12-21T10:30:05.000+00:00",
    "createTime": "2023-12-21T10:30:00.000+00:00",
    "terminalCode": "SC001"
  }
}
```

## 4. 验证付款码格式

### 接口说明
验证用户输入的付款码是否符合微信/支付宝格式要求

### 请求方式
```
POST /selfcheck/payment/validateCode
```

### 请求示例
```
POST /selfcheck/payment/validateCode?paymentCode=134567890123456789
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "WECHAT"
}
```

## 5. 支付方式检测

### 接口说明
检测付款码对应的支付方式并返回中文名称

### 请求方式
```
POST /selfcheck/payment/detectPayType
```

### 请求示例
```
POST /selfcheck/payment/detectPayType?paymentCode=25012345678901234
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "支付宝"
}
```

## 6. 取消支付

### 接口说明
取消未完成的支付订单

### 请求方式
```
POST /selfcheck/payment/cancel/{paymentId}
```

### 请求示例
```
POST /selfcheck/payment/cancel/PAY1703123456789ABC12345
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 7. 退款处理

### 接口说明
处理支付退款请求

### 请求方式
```
POST /selfcheck/payment/refund/{paymentId}
```

### 请求示例
```
POST /selfcheck/payment/refund/PAY1703123456789ABC12345?refundAmount=99.90&reason=用户申请退款
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paymentId": "PAY1703123456789ABC12345",
    "orderId": 1001,
    "payType": "WECHAT",
    "payStatus": "SUCCESS",
    "amount": 99.90,
    "paidAmount": 99.90,
    "transactionId": "4200001234567890123456789"
  }
}
```

## 8. 支付回调接口

### 微信支付回调
```
POST /selfcheck/payment/notify/wechat
```

### 支付宝支付回调
```
POST /selfcheck/payment/notify/alipay
```

## 付款码格式说明

### 微信付款码
- 格式：18位数字
- 开头：1开头（10-15）
- 示例：134567890123456789

### 支付宝付款码
- 格式：18位数字
- 开头：25-30开头
- 示例：250123456789012345

## 支付状态说明

| 状态 | 说明 |
|------|------|
| PENDING | 待支付 |
| SUCCESS | 支付成功 |
| FAILED | 支付失败 |
| TIMEOUT | 支付超时 |
| CANCELLED | 已取消 |

## cURL测试命令

### 1. 生成收款二维码
```bash
curl -X POST "http://localhost:8086/selfcheck/payment/generateQR" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1001,
    "amount": 99.90,
    "payType": "WECHAT",
    "title": "自助收银付款",
    "description": "自助收银系统付款"
  }'
```

### 2. 扫描付款码支付
```bash
curl -X POST "http://localhost:8086/selfcheck/payment/scanCode" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1001,
    "paymentCode": "134567890123456789",
    "amount": 99.90,
    "payType": "WECHAT"
  }'
```

### 3. 查询支付状态
```bash
curl -X GET "http://localhost:8086/selfcheck/payment/status/PAY1703123456789ABC12345"
```

### 4. 验证付款码
```bash
curl -X POST "http://localhost:8086/selfcheck/payment/validateCode?paymentCode=134567890123456789"
```

### 5. 支付方式检测
```bash
curl -X POST "http://localhost:8086/selfcheck/payment/detectPayType?paymentCode=25012345678901234"
```

## 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | 付款码不能为空 | 付款码参数缺失 |
| 400 | 付款码格式不正确 | 付款码格式不符合规范 |
| 400 | 支付金额不能小于0.01元 | 支付金额过小 |
| 400 | 支付金额不能超过99999.99元 | 支付金额过大 |
| 400 | 不支持的支付方式 | 支付方式参数错误 |
| 404 | 支付信息不存在 | 支付ID不存在或已过期 |
| 500 | 生成支付二维码失败 | 系统内部错误 |
| 500 | 扫码支付失败 | 支付处理异常 |

## 测试注意事项

1. **模拟支付模式**：当前配置为模拟支付模式，所有支付都会立即成功
2. **付款码格式**：请使用符合格式的测试付款码
3. **支付超时**：默认5分钟超时，超时后需重新发起支付
4. **缓存过期**：支付信息缓存5分钟，过期后查询会失败
5. **并发测试**：支持并发支付，每个支付都有唯一的支付ID

## 业务流程测试

### 扫码支付流程
1. 创建订单（调用订单接口）
2. 扫描用户付款码
3. 验证付款码格式
4. 发起扫码支付
5. 查询支付状态（如需轮询）
6. 支付完成后更新订单状态

### 二维码支付流程
1. 创建订单（调用订单接口）
2. 生成收款二维码
3. 用户扫码支付
4. 轮询查询支付状态
5. 支付完成后更新订单状态

## 集成真实支付接口

当需要集成真实的微信和支付宝支付时：

1. 修改配置文件中的支付参数（appId、mchId、密钥等）
2. 将 `mockEnabled` 设置为 `false`
3. 实现 `SelfcheckPaymentServiceImpl` 中的TODO方法
4. 添加微信支付和支付宝支付的SDK依赖
5. 配置SSL证书和回调地址 