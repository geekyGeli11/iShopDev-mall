# 优惠券功能扩展 - 业务逻辑验证测试

## 测试环境准备

### 1. 数据库准备
确保已执行以下SQL脚本：
- `mall-swarm/document/sql/coupon_enhancement_ddl.sql`
- 数据库表结构已更新，包含新增字段和排除关系表

### 2. 代码编译
确保以下模块已成功编译：
- `mall-admin` - 后端管理API
- `mall-portal` - 用户端API
- `mall-admin-web` - 前端管理界面

## 测试用例

### 测试用例1：满减券基础功能
**目标**：验证满减券的创建、编辑、删除功能

**测试步骤**：
1. 创建满减券
   ```json
   POST /admin/coupon/create
   {
     "name": "满100减20优惠券",
     "type": 0,
     "couponType": 0,
     "amount": 20.00,
     "minPoint": 100.00,
     "useType": 0,
     "enableExclude": false,
     "publishCount": 100,
     "perLimit": 1,
     "startTime": "2025-09-01 00:00:00",
     "endTime": "2025-12-31 23:59:59"
   }
   ```

2. 编辑满减券
   ```json
   POST /admin/coupon/update/{id}
   {
     "amount": 30.00,
     "minPoint": 150.00
   }
   ```

3. 删除满减券
   ```
   POST /admin/coupon/delete/{id}
   ```

**预期结果**：
- 创建成功，返回优惠券ID
- 编辑成功，优惠券信息更新
- 删除成功，相关关系表数据也被删除

### 测试用例2：打折券基础功能
**目标**：验证打折券的创建和优惠金额计算

**测试步骤**：
1. 创建打折券
   ```json
   POST /admin/coupon/create
   {
     "name": "全场8折优惠券",
     "type": 0,
     "couponType": 1,
     "discountRate": 0.80,
     "minPoint": 50.00,
     "useType": 0,
     "enableExclude": false,
     "publishCount": 100,
     "perLimit": 1,
     "startTime": "2025-09-01 00:00:00",
     "endTime": "2025-12-31 23:59:59"
   }
   ```

2. 计算优惠金额
   ```
   GET /admin/coupon/calculateDiscount/{couponId}?orderAmount=100.00
   ```

**预期结果**：
- 创建成功，couponType=1，discountRate=0.80
- 计算结果：100.00 * (1 - 0.80) = 20.00元优惠

### 测试用例3：排除逻辑功能
**目标**：验证指定分类可用+排除特定商品的组合场景

**测试步骤**：
1. 创建带排除逻辑的优惠券
   ```json
   POST /admin/coupon/create
   {
     "name": "服装类满100减20（排除特定商品）",
     "type": 0,
     "couponType": 0,
     "amount": 20.00,
     "minPoint": 100.00,
     "useType": 1,
     "enableExclude": true,
     "publishCount": 100,
     "perLimit": 1,
     "productCategoryRelationList": [
       {
         "productCategoryId": 1,
         "productCategoryName": "服装",
         "parentCategoryName": "时尚"
       }
     ],
     "productExcludeRelationList": [
       {
         "productId": 101,
         "productName": "特价T恤",
         "productSn": "T001"
       }
     ]
   }
   ```

2. 检查适用性
   ```
   GET /admin/coupon/checkApplicability/{couponId}?productId=101&productCategoryId=1
   ```

**预期结果**：
- 创建成功，enableExclude=true
- 适用性检查返回false（商品101被排除）

### 测试用例4：用户端优惠券列表
**目标**：验证用户端获取可用优惠券列表的逻辑

**测试步骤**：
1. 用户领取优惠券
2. 添加商品到购物车
3. 获取可用优惠券列表
   ```
   GET /portal/member/coupon/list/cart?type=1
   ```

**预期结果**：
- 返回适用于购物车商品的优惠券列表
- 排除逻辑正确生效
- 打折券和满减券都能正确显示

### 测试用例5：订单优惠金额计算
**目标**：验证下单时优惠券金额计算的正确性

**测试步骤**：
1. 使用满减券下单
   ```json
   POST /portal/order/generateOrder
   {
     "cartIds": [1, 2, 3],
     "couponId": 1,
     "memberReceiveAddressId": 1
   }
   ```

2. 使用打折券下单
   ```json
   POST /portal/order/generateOrder
   {
     "cartIds": [1, 2, 3],
     "couponId": 2,
     "memberReceiveAddressId": 1
   }
   ```

**预期结果**：
- 满减券：订单项的couponAmount按比例分摊满减金额
- 打折券：订单项的couponAmount = 商品价格 * (1 - 折扣率)

## 验证检查点

### 1. 数据完整性检查
- [ ] 新增字段默认值正确
- [ ] 排除关系表数据正确插入
- [ ] 删除优惠券时级联删除关系数据

### 2. 业务逻辑检查
- [ ] 满减券和打折券类型区分正确
- [ ] 优惠金额计算公式正确
- [ ] 排除逻辑与可用逻辑组合正确

### 3. 前端界面检查
- [ ] 优惠券类型选择正常
- [ ] 动态表单字段显示正确
- [ ] 排除商品/分类选择功能正常

### 4. API接口检查
- [ ] 创建/编辑/删除接口正常
- [ ] 适用性检查接口正常
- [ ] 优惠金额计算接口正常

### 5. 兼容性检查
- [ ] 现有优惠券数据正常显示
- [ ] 旧版本API调用正常
- [ ] 数据迁移无异常

## 测试结果记录

### 通过的测试用例
- [ ] 测试用例1：满减券基础功能
- [ ] 测试用例2：打折券基础功能
- [ ] 测试用例3：排除逻辑功能
- [ ] 测试用例4：用户端优惠券列表
- [ ] 测试用例5：订单优惠金额计算

### 发现的问题
记录测试过程中发现的问题和解决方案：

1. **问题描述**：
   - 解决方案：

2. **问题描述**：
   - 解决方案：

### 性能测试
- [ ] 大量优惠券数据下的查询性能
- [ ] 复杂排除逻辑的计算性能
- [ ] 并发创建优惠券的稳定性

## 测试总结

### 功能完整性
- 新增功能是否完全实现
- 现有功能是否受到影响

### 代码质量
- 代码结构是否合理
- 异常处理是否完善

### 用户体验
- 前端界面是否友好
- 操作流程是否顺畅

### 建议改进
记录测试过程中发现的可优化点：

1. 
2. 
3. 
