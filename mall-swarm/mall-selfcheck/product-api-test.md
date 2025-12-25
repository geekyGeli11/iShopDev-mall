# 商品扫码API测试文档

## 接口列表

### 1. 扫码查询商品信息
**接口地址**: `POST /product/scan`
**请求方式**: POST
**认证要求**: 无需认证（游客和会员都可以使用）

#### 请求参数
```json
{
  "barcode": "6901234567890",        // 条码（必填）
  "scanType": "BARCODE",            // 扫码类型：BARCODE/SKU_CODE/PRODUCT_SN
  "needStockCheck": true,           // 是否需要库存检查
  "needPromotionInfo": true         // 是否需要促销信息
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "productId": 1,
    "skuId": null,
    "productName": "苹果iPhone 15 Pro",
    "productPic": "http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/iphone14_001.jpg",
    "brandName": "苹果",
    "categoryName": "手机",
    "productSn": "7437788",
    "skuCode": null,
    "unit": "部",
    "weight": 200.00,
    "originalPrice": 8999.00,
    "currentPrice": 8999.00,
    "promotionPrice": null,
    "promotionType": 0,
    "stock": 100,
    "stockStatus": "SUFFICIENT",
    "lowStock": 10,
    "sale": 0,
    "productStatus": "NORMAL",
    "publishStatus": 1,
    "saleable": true,
    "giftPoint": 8999,
    "giftGrowth": 8999,
    "subTitle": "双十一活动，下单立减1000",
    "scanType": "BARCODE",
    "scanTime": 1642608000000
  }
}
```

### 2. 快速扫码（简化版）
**接口地址**: `GET /product/quickScan`
**请求方式**: GET
**认证要求**: 无需认证

#### 请求参数
- `barcode`: 条码（必填）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "productId": 1,
    "productName": "苹果iPhone 15 Pro",
    "currentPrice": 8999.00,
    "saleable": true,
    "stockStatus": "SUFFICIENT"
  }
}
```

### 3. 根据商品ID获取商品详情
**接口地址**: `GET /product/detail/{productId}`
**请求方式**: GET
**认证要求**: 无需认证

#### 路径参数
- `productId`: 商品ID

#### 请求参数
- `needStockCheck`: 是否需要库存检查（默认true）
- `needPromotionInfo`: 是否需要促销信息（默认true）

### 4. 检查商品是否可以销售
**接口地址**: `POST /product/checkSaleable`
**请求方式**: POST
**认证要求**: 无需认证

#### 请求参数
- `productId`: 商品ID（必填）
- `skuId`: SKU ID（可选）
- `quantity`: 购买数量（默认1）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "productId": 1,
    "skuId": null,
    "quantity": 1,
    "saleable": true,
    "message": "商品可以销售"
  }
}
```

### 5. 计算商品价格
**接口地址**: `POST /product/calculatePrice`
**请求方式**: POST
**认证要求**: 无需认证（会员登录后可享受会员价）

#### 请求参数
- `productId`: 商品ID（必填）
- `skuId`: SKU ID（可选）
- `quantity`: 购买数量（默认1）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "productId": 1,
    "originalPrice": 8999.00,
    "currentPrice": 7999.00,  // 会员价或促销价
    "promotionPrice": 7999.00,
    "promotionType": 1
  }
}
```

### 6. 批量扫码查询商品
**接口地址**: `POST /product/batchScan`
**请求方式**: POST
**认证要求**: 无需认证

#### 请求参数
```json
[
  "6901234567890",
  "6901234567891",
  "6901234567892"
]
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 3,
    "success": 2,
    "failed": 1,
    "products": [
      {
        "productId": 1,
        "productName": "苹果iPhone 15 Pro",
        "currentPrice": 8999.00,
        "saleable": true
      },
      {
        "productId": 2,
        "productName": "华为Mate 60 Pro",
        "currentPrice": 6999.00,
        "saleable": true
      }
    ]
  }
}
```

### 7. 验证条码格式
**接口地址**: `GET /product/validateBarcode`
**请求方式**: GET
**认证要求**: 无需认证

#### 请求参数
- `barcode`: 条码（必填）

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "barcode": "6901234567890",
    "type": "BARCODE",
    "valid": true,
    "description": "商品条码"
  }
}
```

## 测试用例

### 前置条件
1. 确保 mall-selfcheck 服务运行在端口 8084
2. 确保数据库中有商品测试数据

### 测试步骤

#### 1. 基础扫码测试
```bash
curl -X POST "http://localhost:8084/product/scan" \
  -H "Content-Type: application/json" \
  -d '{
    "barcode": "6936983800006",
    "scanType": "BARCODE",
    "needStockCheck": true,
    "needPromotionInfo": true
  }'
```

#### 2. 快速扫码测试
```bash
curl -X GET "http://localhost:8084/product/quickScan?barcode=6936983800006"
```

#### 3. 商品详情查询
```bash
curl -X GET "http://localhost:8084/product/detail/1?needStockCheck=true&needPromotionInfo=true"
```

#### 4. 检查商品销售状态
```bash
curl -X POST "http://localhost:8084/product/checkSaleable" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "productId=1&quantity=1"
```

#### 5. 批量扫码测试
```bash
curl -X POST "http://localhost:8084/product/batchScan" \
  -H "Content-Type: application/json" \
  -d '["6936983800006", "6901028075237", "6971234567890"]'
```

#### 6. 条码格式验证
```bash
curl -X GET "http://localhost:8084/product/validateBarcode?barcode=6936983800006"
```

#### 7. 会员价格测试（需要先登录）
```bash
# 先登录获取token
curl -X POST "http://localhost:8084/member/login" \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "13800138000",
    "verifyCode": "123456"
  }'

# 使用token计算会员价格
curl -X POST "http://localhost:8084/product/calculatePrice" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "productId=1&quantity=1"
```

## 扫码类型说明

### 条码格式支持
- **EAN-13**: 13位数字商品条码（如：6901234567890）
- **EAN-8**: 8位数字商品条码
- **UPC-A**: 12位数字条码
- **CODE128**: 字母数字混合SKU编码
- **商品货号**: 字母数字混合商品货号

### 扫码类型枚举
- `BARCODE`: 商品条码
- `SKU_CODE`: SKU编码  
- `PRODUCT_SN`: 商品货号
- `UNKNOWN`: 未知格式

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 参数错误 |
| 404 | 商品不存在 |
| 500 | 服务器内部错误 |

## 状态码说明

### 商品状态 (productStatus)
- `NORMAL`: 正常
- `OFF_SHELF`: 下架
- `DELETED`: 已删除

### 库存状态 (stockStatus)
- `SUFFICIENT`: 库存充足
- `LOW`: 库存不足
- `OUT_OF_STOCK`: 缺货
- `UNKNOWN`: 未知

### 促销类型 (promotionType)
- 0: 无促销
- 1: 促销价
- 2: 会员价
- 3: 阶梯价
- 4: 满减价
- 5: 限时购

---
**测试时间**: 2025-01-22  
**API版本**: v1.2.0 