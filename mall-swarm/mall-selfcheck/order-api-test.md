# 自助收银订单管理 API 测试文档

## 接口概述

订单管理模块提供了完整的订单生命周期管理功能，支持会员和游客两种模式，包括快速下单、购物车下单、订单查询、状态管理等功能。

## 环境信息

- **测试环境**: http://localhost:8201
- **API前缀**: `/mall-selfcheck/order`
- **文档地址**: http://localhost:8201/mall-selfcheck/doc.html

## 认证说明

### 会员认证
需要在请求头中添加 Sa-Token：
```
Sa-Token: {member_token}
```

### 游客模式
无需认证，但需要在请求参数中提供 `guestId`

## API 接口测试

### 1. 快速下单

快速下单适用于扫码后直接购买单个商品的场景。

#### 1.1 会员快速下单

```http
POST /mall-selfcheck/order/createQuick
Content-Type: application/x-www-form-urlencoded
Sa-Token: {member_token}

productId=1&quantity=2&terminalCode=SC001&deviceInfo=设备001
```

**参数说明：**
- `productId`: 商品ID（必填）
- `skuId`: SKU ID（选填）
- `quantity`: 购买数量（默认1）
- `terminalCode`: 终端编码（选填）
- `deviceInfo`: 设备信息（选填）

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": 123,
    "orderSn": "SC20250122141500001",
    "totalAmount": 99.00,
    "payAmount": 99.00,
    "status": 0,
    "statusName": "待付款"
  }
}
```

#### 1.2 游客快速下单

```http
POST /mall-selfcheck/order/createQuick
Content-Type: application/x-www-form-urlencoded

productId=1&quantity=1&guestId=GUEST_20250122_001&terminalCode=SC001
```

### 2. 购物车下单

将购物车中的商品批量下单。

#### 2.1 购物车批量下单

```http
POST /mall-selfcheck/order/createFromCart
Content-Type: application/json
Sa-Token: {member_token}

{
  "orderType": "CART",
  "orderItems": [
    {
      "productId": 1,
      "skuId": 1,
      "quantity": 2,
      "unitPrice": 49.50,
      "totalPrice": 99.00
    },
    {
      "productId": 2,
      "quantity": 1,
      "unitPrice": 29.80,
      "totalPrice": 29.80
    }
  ],
  "couponHistoryIds": [1, 2],
  "usePoints": 100,
  "note": "批量购买",
  "terminalCode": "SC001",
  "deviceInfo": "设备001"
}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": 124,
    "orderSn": "SC20250122141600002",
    "totalAmount": 128.80,
    "couponAmount": 10.00,
    "integrationAmount": 1.00,
    "payAmount": 117.80,
    "status": 0,
    "statusName": "待付款"
  }
}
```

### 3. 计算订单金额

在下单前预先计算订单总价，包含优惠券、积分等优惠。

```http
POST /mall-selfcheck/order/calculate
Content-Type: application/json
Sa-Token: {member_token}

{
  "orderType": "CART",
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 49.50
    }
  ],
  "couponHistoryIds": [1],
  "usePoints": 50
}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "originalAmount": 99.00,
    "couponAmount": 5.00,
    "integrationAmount": 0.50,
    "discountAmount": 0.00,
    "freightAmount": 0.00,
    "finalAmount": 93.50,
    "saveAmount": 5.50,
    "integration": 99,
    "growth": 99
  }
}
```

### 4. 订单详情查询

#### 4.1 会员查询订单详情

```http
GET /mall-selfcheck/order/detail/123
Sa-Token: {member_token}
```

#### 4.2 游客查询订单详情

```http
GET /mall-selfcheck/order/detail/123?guestId=GUEST_20250122_001
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": 123,
    "orderSn": "SC20250122141500001",
    "userType": "MEMBER",
    "userId": "1",
    "memberId": 1,
    "memberUsername": "test_user",
    "status": 0,
    "statusName": "待付款",
    "orderType": "QUICK",
    "totalAmount": 99.00,
    "payAmount": 99.00,
    "createTime": "2025-01-22 14:15:00",
    "orderItems": [
      {
        "productId": 1,
        "productName": "测试商品",
        "productPic": "http://xxx.jpg",
        "quantity": 2,
        "unitPrice": 49.50,
        "totalPrice": 99.00
      }
    ],
    "usedCoupons": [],
    "operateHistory": [
      {
        "operateType": "CREATE",
        "description": "创建订单",
        "createTime": "2025-01-22 14:15:00"
      }
    ]
  }
}
```

### 5. 订单状态查询

```http
GET /mall-selfcheck/order/status/123
Sa-Token: {member_token}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderId": 123,
    "orderSn": "SC20250122141500001",
    "status": 0,
    "statusName": "待付款",
    "payStatus": 0,
    "payStatusName": "未支付",
    "canPay": true,
    "canCancel": true,
    "canConfirm": false,
    "remainingTime": 1800
  }
}
```

### 6. 订单历史查询

#### 6.1 会员订单历史

```http
GET /mall-selfcheck/order/history?status=1&pageNum=1&pageSize=10
Sa-Token: {member_token}
```

#### 6.2 游客订单历史

```http
GET /mall-selfcheck/order/guestHistory?guestId=GUEST_20250122_001&pageNum=1&pageSize=10
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "totalPages": 3,
    "list": [
      {
        "orderId": 123,
        "orderSn": "SC20250122141500001",
        "status": 1,
        "statusName": "待发货",
        "totalAmount": 99.00,
        "payAmount": 99.00,
        "createTime": "2025-01-22 14:15:00",
        "payTime": "2025-01-22 14:16:30"
      }
    ]
  }
}
```

### 7. 取消订单

只能取消待付款状态的订单。

```http
POST /mall-selfcheck/order/cancel/123
Content-Type: application/x-www-form-urlencoded
Sa-Token: {member_token}

reason=不想要了
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 8. 确认收货

```http
POST /mall-selfcheck/order/confirmReceive/123
Sa-Token: {member_token}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 9. 验证库存

在下单前验证商品库存是否充足。

```http
POST /mall-selfcheck/order/validateStock
Content-Type: application/json

{
  "orderItems": [
    {
      "productId": 1,
      "skuId": 1,
      "quantity": 5
    },
    {
      "productId": 2,
      "quantity": 2
    }
  ]
}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "valid": true,
    "insufficientItems": [],
    "details": [
      {
        "productId": 1,
        "skuId": 1,
        "requestQuantity": 5,
        "availableStock": 100,
        "sufficient": true
      }
    ]
  }
}
```

**库存不足响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "valid": false,
    "insufficientItems": [
      "测试商品 库存不足，当前库存:2，需要:5"
    ],
    "details": [
      {
        "productId": 1,
        "requestQuantity": 5,
        "availableStock": 2,
        "sufficient": false
      }
    ]
  }
}
```

### 10. 支付回调接口

#### 10.1 支付成功回调

```http
POST /mall-selfcheck/order/paymentSuccess/123
Content-Type: application/x-www-form-urlencoded

payType=0&transactionId=wx20250122141700001
```

#### 10.2 支付失败回调

```http
POST /mall-selfcheck/order/paymentFailed/123
Content-Type: application/x-www-form-urlencoded

reason=余额不足
```

## 业务流程说明

### 快速下单流程

1. 扫码获取商品信息
2. 调用快速下单接口
3. 系统自动创建订单
4. 返回订单信息和支付信息
5. 进入支付流程

### 购物车下单流程

1. 添加商品到购物车
2. 调用计算订单金额接口（可选）
3. 选择优惠券和积分使用
4. 调用购物车下单接口
5. 系统创建订单并清空购物车
6. 进入支付流程

### 订单状态流转

```
待付款(0) → 待发货(1) → 已发货(2) → 已完成(3)
    ↓
 已关闭(4)
```

## 错误处理

### 常见错误码

- `400`: 参数错误
- `401`: 未授权（未登录或游客ID无效）
- `404`: 订单不存在
- `500`: 服务器内部错误

### 错误响应示例

```json
{
  "code": 400,
  "message": "商品库存不足",
  "data": null
}
```

## 测试数据

### 测试商品ID
- 商品1: ID=1, 价格=49.50
- 商品2: ID=2, 价格=29.80

### 测试会员
- 会员ID: 1
- 用户名: test_user
- 可用积分: 1000

### 测试优惠券
- 优惠券1: ID=1, 满50减5
- 优惠券2: ID=2, 满100减10

## 注意事项

1. **游客模式**：游客ID需要保持一致，建议使用格式 `GUEST_YYYYMMDD_XXX`
2. **超时处理**：待付款订单30分钟后自动关闭
3. **库存检查**：下单前建议先调用库存验证接口
4. **幂等性**：相同参数多次调用创建订单接口会返回相同结果
5. **日志记录**：所有订单操作都会记录操作历史

## 测试建议

1. 先测试库存验证接口
2. 再测试订单计算接口
3. 最后测试订单创建接口
4. 验证订单状态流转
5. 测试异常场景（库存不足、重复下单等） 