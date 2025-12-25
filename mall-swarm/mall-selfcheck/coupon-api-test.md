# 会员优惠券API测试文档

## 接口列表

### 1. 获取会员优惠券列表
**接口地址**: `POST /coupon/list`
**请求方式**: POST
**认证要求**: 需要会员登录

#### 请求参数
```json
{
  "useStatus": 0,           // 使用状态：0->未使用；1->已使用；2->已过期；null->全部
  "pageNum": 1,            // 页码（必填）
  "pageSize": 10,          // 每页数量（必填，最大20）
  "orderBy": "createTime", // 排序字段：createTime/endTime/amount
  "orderDirection": "desc" // 排序方向：asc/desc
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 15,
    "list": [
      {
        "historyId": 1,
        "couponId": 101,
        "name": "新用户专享券",
        "type": 0,
        "couponCode": "NEWUSER001",
        "amount": 10.00,
        "minPoint": 50.00,
        "useType": 0,
        "startTime": "2024-01-01T00:00:00",
        "endTime": "2024-12-31T23:59:59",
        "useStatus": 0,
        "useTime": null,
        "orderSn": null,
        "createTime": "2024-01-01T10:00:00",
        "isExpired": false,
        "nearExpiry": false,
        "remainingDays": 340,
        "canUse": true
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "totalPage": 2
  }
}
```

### 2. 获取优惠券统计信息
**接口地址**: `GET /coupon/statistics`
**请求方式**: GET
**认证要求**: 需要会员登录

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "availableCount": 5,    // 可用优惠券数量
    "nearExpiryCount": 2,   // 即将过期优惠券数量
    "memberId": 1001
  }
}
```

### 3. 获取优惠券详情
**接口地址**: `GET /coupon/detail/{historyId}`
**请求方式**: GET
**认证要求**: 需要会员登录

#### 路径参数
- `historyId`: 优惠券历史ID

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "historyId": 1,
    "couponId": 101,
    "name": "新用户专享券",
    "type": 0,
    "couponCode": "NEWUSER001",
    "amount": 10.00,
    "minPoint": 50.00,
    "useType": 0,
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-12-31T23:59:59",
    "useStatus": 0,
    "useTime": null,
    "orderSn": null,
    "createTime": "2024-01-01T10:00:00",
    "isExpired": false,
    "nearExpiry": false,
    "remainingDays": 340,
    "canUse": true
  }
}
```

### 4. 验证优惠券是否可用
**接口地址**: `POST /coupon/validate/{historyId}`
**请求方式**: POST
**认证要求**: 需要会员登录

#### 路径参数
- `historyId`: 优惠券历史ID

#### 请求参数
- `orderAmount`: 订单金额（必填）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "historyId": 1,
    "orderAmount": 80.00,
    "isValid": true,
    "message": "优惠券可以使用"
  }
}
```

### 5. 获取订单可用优惠券
**接口地址**: `GET /coupon/available`
**请求方式**: GET
**认证要求**: 需要会员登录

#### 请求参数
- `orderAmount`: 订单金额（必填）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 3,
    "list": [
      {
        "historyId": 1,
        "couponId": 101,
        "name": "新用户专享券",
        "amount": 10.00,
        "minPoint": 50.00,
        "canUse": true
      }
    ]
  }
}
```

### 6. 获取未使用优惠券列表
**接口地址**: `GET /coupon/unused`
**请求方式**: GET
**认证要求**: 需要会员登录

#### 请求参数
- `pageNum`: 页码（可选，默认1）
- `pageSize`: 每页数量（可选，默认10）

### 7. 获取即将过期优惠券列表
**接口地址**: `GET /coupon/nearExpiry`
**请求方式**: GET
**认证要求**: 需要会员登录

#### 请求参数
- `pageNum`: 页码（可选，默认1）
- `pageSize`: 每页数量（可选，默认10）

## 测试用例

### 前置条件
1. 确保 mall-selfcheck 服务运行在端口 8084
2. 先调用会员登录接口获取有效的 Token
3. 确保数据库中有优惠券测试数据

### 测试步骤

#### 1. 会员登录
```bash
curl -X POST "http://localhost:8084/member/login" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "authCode": "123456"
  }'
```

#### 2. 获取优惠券列表
```bash
curl -X POST "http://localhost:8084/coupon/list" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "pageNum": 1,
    "pageSize": 10,
    "orderBy": "createTime",
    "orderDirection": "desc"
  }'
```

#### 3. 获取统计信息
```bash
curl -X GET "http://localhost:8084/coupon/statistics" \
  -H "Authorization: Bearer {token}"
```

#### 4. 验证优惠券
```bash
curl -X POST "http://localhost:8084/coupon/validate/1?orderAmount=80.00" \
  -H "Authorization: Bearer {token}"
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 401 | 用户未登录 |
| 400 | 参数错误 |
| 404 | 优惠券不存在 |
| 403 | 优惠券不属于当前用户 |
| 500 | 服务器内部错误 |

## 数据字典

### 优惠券类型 (type)
- 0: 全场赠券
- 1: 会员赠券
- 2: 购物赠券
- 3: 注册赠券

### 使用状态 (useStatus)
- 0: 未使用
- 1: 已使用
- 2: 已过期

### 使用类型 (useType)
- 0: 全场通用
- 1: 指定分类
- 2: 指定商品

### 排序字段 (orderBy)
- createTime: 创建时间
- endTime: 到期时间
- amount: 优惠金额

---
**测试时间**: 2024-01-15  
**API版本**: v1.2.0 