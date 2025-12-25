# 购物车管理API测试文档

## 接口概览

购物车管理模块提供了完整的购物车功能，支持会员和游客两种模式：
- **会员模式**：购物车数据持久化存储在数据库中
- **游客模式**：购物车数据临时存储在Redis中，30分钟过期

## 1. 添加商品到购物车

**接口地址**: `POST /cart/addItem`  
**请求方式**: POST  
**认证要求**: 支持会员和游客模式

### 请求参数
```json
{
  "productId": 1,
  "skuId": null,
  "quantity": 2,
  "operation": "ADD",
  "guestId": "guest_12345",
  "forceUpdate": false,
  "remark": "扫码添加"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "商品已添加到购物车",
  "data": true
}
```

### 测试用例
```bash
# 会员添加商品（需先登录）
curl -X POST "http://localhost:8080/cart/addItem" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "operation": "ADD",
    "remark": "手动添加"
  }'

# 游客添加商品
curl -X POST "http://localhost:8080/cart/addItem" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 1,
    "operation": "ADD",
    "guestId": "guest_12345",
    "remark": "游客添加"
  }'
```

## 2. 更新购物车商品数量

**接口地址**: `PUT /cart/updateItem`  
**请求方式**: PUT

### 请求参数
```json
{
  "productId": 1,
  "skuId": null,
  "quantity": 3,
  "operation": "UPDATE",
  "guestId": "guest_12345"
}
```

### 测试用例
```bash
curl -X PUT "http://localhost:8080/cart/updateItem" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 3,
    "operation": "UPDATE",
    "guestId": "guest_12345"
  }'
```

## 3. 删除购物车商品

**接口地址**: `DELETE /cart/removeItem`  
**请求方式**: DELETE

### 请求参数
```json
{
  "productId": 1,
  "skuId": null,
  "operation": "REMOVE",
  "guestId": "guest_12345"
}
```

### 测试用例
```bash
curl -X DELETE "http://localhost:8080/cart/removeItem" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "operation": "REMOVE",
    "guestId": "guest_12345"
  }'
```

## 4. 清空购物车

**接口地址**: `DELETE /cart/clear`  
**请求方式**: DELETE

### 请求参数
- `guestId`: 游客ID（游客模式必传）

### 测试用例
```bash
# 游客清空购物车
curl -X DELETE "http://localhost:8080/cart/clear?guestId=guest_12345"

# 会员清空购物车
curl -X DELETE "http://localhost:8080/cart/clear" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 5. 获取购物车信息

**接口地址**: `GET /cart/list`  
**请求方式**: GET

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userType": "GUEST",
    "userId": "guest_12345",
    "items": [
      {
        "itemId": null,
        "productId": 1,
        "skuId": null,
        "productName": "苹果iPhone 15 Pro",
        "productPic": "http://example.com/product1.jpg",
        "brandName": "苹果",
        "categoryName": "手机",
        "originalPrice": 8999.00,
        "currentPrice": 8999.00,
        "actualPrice": 8999.00,
        "quantity": 2,
        "subtotal": 17998.00,
        "stock": 100,
        "stockStatus": "SUFFICIENT",
        "available": true,
        "productStatus": "NORMAL"
      }
    ],
    "totalQuantity": 2,
    "availableQuantity": 2,
    "unavailableQuantity": 0,
    "totalAmount": 17998.00,
    "availableAmount": 17998.00,
    "discountAmount": 0.00,
    "finalAmount": 17998.00,
    "cartStatus": "NORMAL",
    "messages": [],
    "errors": []
  }
}
```

### 测试用例
```bash
# 获取游客购物车
curl -X GET "http://localhost:8080/cart/list?guestId=guest_12345"

# 获取会员购物车
curl -X GET "http://localhost:8080/cart/list" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 6. 计算购物车价格

**接口地址**: `POST /cart/calculate`  
**请求方式**: POST

### 请求参数
- `guestId`: 游客ID（可选）
- `couponIds`: 优惠券ID列表（可选）
- `usePoints`: 使用积分数量（可选）

### 测试用例
```bash
# 计算购物车价格（含优惠券）
curl -X POST "http://localhost:8080/cart/calculate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "couponIds=1,2&usePoints=1000"
```

## 7. 应用优惠券

**接口地址**: `POST /cart/applyCoupon`  
**请求方式**: POST  
**认证要求**: 仅会员可用

### 请求参数
- `couponId`: 优惠券ID（必填）
- `guestId`: 游客ID（会员模式不需要）

### 测试用例
```bash
curl -X POST "http://localhost:8080/cart/applyCoupon" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "couponId=1"
```

## 8. 移除优惠券

**接口地址**: `DELETE /cart/removeCoupon`  
**请求方式**: DELETE

### 测试用例
```bash
curl -X DELETE "http://localhost:8080/cart/removeCoupon" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "couponId=1"
```

## 9. 批量更新购物车

**接口地址**: `POST /cart/batchUpdate`  
**请求方式**: POST

### 请求参数
```json
[
  {
    "productId": 1,
    "quantity": 2,
    "operation": "ADD",
    "guestId": "guest_12345"
  },
  {
    "productId": 2,
    "quantity": 1,
    "operation": "ADD",
    "guestId": "guest_12345"
  },
  {
    "productId": 3,
    "quantity": 0,
    "operation": "REMOVE",
    "guestId": "guest_12345"
  }
]
```

### 测试用例
```bash
curl -X POST "http://localhost:8080/cart/batchUpdate?guestId=guest_12345" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "productId": 1,
      "quantity": 2,
      "operation": "ADD"
    },
    {
      "productId": 2,
      "quantity": 1,
      "operation": "ADD"
    }
  ]'
```

## 10. 同步购物车

**接口地址**: `POST /cart/sync`  
**请求方式**: POST

### 功能说明
验证购物车中商品的有效性，自动移除无效商品（已下架、删除、库存不足等）

### 测试用例
```bash
curl -X POST "http://localhost:8080/cart/sync?guestId=guest_12345"
```

## 11. 获取购物车商品数量

**接口地址**: `GET /cart/count`  
**请求方式**: GET

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

### 测试用例
```bash
curl -X GET "http://localhost:8080/cart/count?guestId=guest_12345"
```

## 12. 检查商品是否在购物车中

**接口地址**: `GET /cart/contains`  
**请求方式**: GET

### 请求参数
- `productId`: 商品ID（必填）
- `skuId`: SKU ID（可选）
- `guestId`: 游客ID（可选）

### 测试用例
```bash
curl -X GET "http://localhost:8080/cart/contains?productId=1&guestId=guest_12345"
```

## 13. 获取可用优惠券

**接口地址**: `GET /cart/availableCoupons`  
**请求方式**: GET  
**认证要求**: 仅会员可用

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "historyId": 1,
      "couponId": 1,
      "memberNickname": "测试用户",
      "couponCode": "WELCOME10",
      "memberCoupon": {
        "name": "新用户优惠券",
        "type": 0,
        "amount": 10.00,
        "minPoint": 100.00
      },
      "useStatus": 0,
      "getTime": "2023-12-01T10:00:00",
      "endTime": "2023-12-31T23:59:59"
    }
  ]
}
```

### 测试用例
```bash
curl -X GET "http://localhost:8080/cart/availableCoupons" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 14. 删除无效商品

**接口地址**: `DELETE /cart/removeInvalid`  
**请求方式**: DELETE

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "removedCount": 2,
    "message": "已删除 2 个无效商品"
  }
}
```

### 测试用例
```bash
curl -X DELETE "http://localhost:8080/cart/removeInvalid?guestId=guest_12345"
```

## 15. 扫码添加商品

**接口地址**: `POST /cart/scanAdd`  
**请求方式**: POST

### 请求参数
- `productId`: 商品ID（必填）
- `skuId`: SKU ID（可选）
- `quantity`: 数量（默认1）
- `guestId`: 游客ID（可选）

### 响应示例
```json
{
  "code": 200,
  "message": "商品已添加到购物车",
  "data": {
    // ... 完整的购物车信息
  }
}
```

### 测试用例
```bash
# 扫码添加商品（游客模式）
curl -X POST "http://localhost:8080/cart/scanAdd" \
  -d "productId=1&quantity=1&guestId=guest_12345"

# 扫码添加商品（会员模式）
curl -X POST "http://localhost:8080/cart/scanAdd" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "productId=1&quantity=2"
```

## 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | 参数验证失败 | 请求参数不符合要求 |
| 401 | 请先登录会员账号 | 需要会员登录的操作未登录 |
| 404 | 商品不存在 | 指定的商品ID不存在 |
| 500 | 系统内部错误 | 服务器内部错误 |

## 注意事项

1. **游客模式**：
   - 必须传递`guestId`参数
   - 购物车数据存储在Redis中，30分钟过期
   - 无法使用优惠券和积分功能

2. **会员模式**：
   - 需要在请求头中携带有效的Token
   - 购物车数据持久化存储
   - 支持优惠券和积分功能

3. **商品库存**：
   - 添加商品时会检查库存
   - 购物车同步时会验证库存状态
   - 库存不足的商品会标记为不可用

4. **价格计算**：
   - 会员享受会员价优惠
   - 支持促销价格
   - 优惠券和积分可叠加使用

5. **数据一致性**：
   - 建议定期调用同步接口验证数据
   - 结算前必须同步购物车状态 