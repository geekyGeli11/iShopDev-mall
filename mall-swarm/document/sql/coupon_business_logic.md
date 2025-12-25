# 优惠券功能扩展 - 业务逻辑说明

## 概述

本次优惠券功能扩展主要实现两个核心需求：
1. **优惠券类型扩展**：支持打折券（在原有满减券基础上）
2. **商品适用范围扩展**：支持"指定不可用的产品/分类"功能

## 数据库字段说明

### sms_coupon 主表新增字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `coupon_type` | int | 0 | 优惠券类型：0->满减券；1->打折券 |
| `discount_rate` | decimal(3,2) | NULL | 打折率（0.1-0.99），仅打折券使用 |
| `enable_exclude` | tinyint(1) | 0 | 是否启用排除逻辑：0->不启用；1->启用排除商品/分类 |

### 原有字段保持不变

| 字段名 | 说明 |
|--------|------|
| `amount` | 满减金额（满减券使用） |
| `use_type` | 使用类型：0->全场通用；1->指定分类；2->指定商品 |
| `min_point` | 使用门槛 |

## 业务逻辑判断流程

### 1. 优惠券类型判断

```sql
-- 满减券判断
IF coupon_type = 0 THEN
    -- 使用 amount 字段计算优惠金额
    -- discount_rate 字段应为 NULL
    优惠金额 = amount
END IF

-- 打折券判断  
IF coupon_type = 1 THEN
    -- 使用 discount_rate 字段计算优惠金额
    -- amount 字段应为 NULL
    优惠金额 = 订单金额 * (1 - discount_rate)
END IF
```

### 2. 商品适用范围判断（支持组合逻辑）

#### 第一步：基础适用范围检查（use_type）

```sql
CASE use_type
    WHEN 0 THEN -- 全场通用
        基础适用 = TRUE
    WHEN 1 THEN -- 指定分类
        基础适用 = EXISTS(
            SELECT 1 FROM sms_coupon_product_category_relation 
            WHERE coupon_id = ? AND product_category_id = 商品分类ID
        )
    WHEN 2 THEN -- 指定商品
        基础适用 = EXISTS(
            SELECT 1 FROM sms_coupon_product_relation 
            WHERE coupon_id = ? AND product_id = 商品ID
        )
END CASE
```

#### 第二步：排除逻辑检查（enable_exclude = 1 时）

```sql
IF enable_exclude = 1 AND 基础适用 = TRUE THEN
    -- 检查是否在排除商品列表中
    排除商品 = EXISTS(
        SELECT 1 FROM sms_coupon_product_exclude_relation 
        WHERE coupon_id = ? AND product_id = 商品ID
    )
    
    -- 检查是否在排除分类列表中
    排除分类 = EXISTS(
        SELECT 1 FROM sms_coupon_product_category_exclude_relation 
        WHERE coupon_id = ? AND product_category_id = 商品分类ID
    )
    
    最终适用 = 基础适用 AND NOT(排除商品 OR 排除分类)
ELSE
    最终适用 = 基础适用
END IF
```

## 应用场景示例

### 场景1：全场8折券，排除数码产品
```sql
coupon_type = 1          -- 打折券
discount_rate = 0.80     -- 8折
use_type = 0             -- 全场通用
enable_exclude = 1       -- 启用排除逻辑
-- 在 sms_coupon_product_category_exclude_relation 中配置数码产品分类
```

### 场景2：服装类满100减20，排除特定品牌商品
```sql
coupon_type = 0          -- 满减券
amount = 20.00           -- 减20元
min_point = 100.00       -- 满100元
use_type = 1             -- 指定分类（服装类）
enable_exclude = 1       -- 启用排除逻辑
-- 在 sms_coupon_product_category_relation 中配置服装分类
-- 在 sms_coupon_product_exclude_relation 中配置特定品牌商品
```

### 场景3：指定商品9折券，排除促销商品
```sql
coupon_type = 1          -- 打折券
discount_rate = 0.90     -- 9折
use_type = 2             -- 指定商品
enable_exclude = 1       -- 启用排除逻辑
-- 在 sms_coupon_product_relation 中配置可用商品
-- 在 sms_coupon_product_exclude_relation 中配置促销商品
```

## 代码实现要点

### 1. 后端API接口修改要点
- 优惠券创建/编辑接口需要支持新字段
- 优惠券适用性检查接口需要实现新的判断逻辑
- 优惠金额计算接口需要支持打折券计算

### 2. 前端界面修改要点
- 优惠券类型选择（满减券/打折券）
- 打折率输入框（仅打折券显示）
- 排除逻辑开关
- 排除商品/分类选择界面

### 3. 数据兼容性处理
- 现有优惠券数据自动设置为满减券类型
- 现有优惠券默认不启用排除逻辑
- API接口需要兼容旧版本数据结构

## 性能优化建议

1. **索引优化**：已为新字段添加索引
2. **查询优化**：排除逻辑查询可以使用 NOT EXISTS 优化
3. **缓存策略**：优惠券适用性结果可以缓存，减少数据库查询

## 测试用例

### 数据准备
1. 创建不同类型的优惠券（满减券、打折券）
2. 配置不同的适用范围（全场、指定分类、指定商品）
3. 配置排除逻辑（排除商品、排除分类）

### 测试场景
1. 满减券基础功能测试
2. 打折券基础功能测试
3. 排除逻辑功能测试
4. 组合场景测试（指定分类+排除商品等）
5. 边界条件测试（最小订单金额、最大折扣等）
