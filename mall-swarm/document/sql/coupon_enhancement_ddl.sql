-- =====================================================
-- 优惠券功能扩展 - 数据库变更脚本
-- 功能：支持打折券类型 + 指定不可用商品/分类功能
-- 创建时间：2025-09-01
-- =====================================================

-- 1. 修改优惠券主表，新增打折券支持和排除逻辑控制
ALTER TABLE `sms_coupon`
ADD COLUMN `coupon_type` int DEFAULT 0 COMMENT '优惠券类型：0->满减券；1->打折券' AFTER `amount`,
ADD COLUMN `discount_rate` decimal(3,2) DEFAULT NULL COMMENT '打折率（0.1-0.99），仅打折券使用' AFTER `coupon_type`,
ADD COLUMN `enable_exclude` tinyint(1) DEFAULT 0 COMMENT '是否启用排除逻辑：0->不启用；1->启用排除商品/分类' AFTER `discount_rate`;

-- 2. 创建优惠券不可用商品关系表
DROP TABLE IF EXISTS `sms_coupon_product_exclude_relation`;
CREATE TABLE `sms_coupon_product_exclude_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint DEFAULT NULL COMMENT '优惠券ID',
  `product_id` bigint DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(500) DEFAULT NULL COMMENT '商品名称',
  `product_sn` varchar(200) DEFAULT NULL COMMENT '商品编码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coupon_id` (`coupon_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE,
  UNIQUE KEY `uk_coupon_product` (`coupon_id`, `product_id`) COMMENT '优惠券商品唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券不可用商品关系表';

-- 3. 创建优惠券不可用分类关系表
DROP TABLE IF EXISTS `sms_coupon_product_category_exclude_relation`;
CREATE TABLE `sms_coupon_product_category_exclude_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint DEFAULT NULL COMMENT '优惠券ID',
  `product_category_id` bigint DEFAULT NULL COMMENT '商品分类ID',
  `product_category_name` varchar(200) DEFAULT NULL COMMENT '产品分类名称',
  `parent_category_name` varchar(200) DEFAULT NULL COMMENT '父分类名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coupon_id` (`coupon_id`) USING BTREE,
  KEY `idx_category_id` (`product_category_id`) USING BTREE,
  UNIQUE KEY `uk_coupon_category` (`coupon_id`, `product_category_id`) COMMENT '优惠券分类唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券不可用商品分类关系表';

-- 4. 数据迁移脚本 - 为现有优惠券设置默认值
UPDATE `sms_coupon` SET
  `coupon_type` = 0,  -- 默认为满减券
  `discount_rate` = NULL,  -- 满减券不需要打折率
  `enable_exclude` = 0  -- 默认不启用排除逻辑
WHERE `coupon_type` IS NULL;

-- 5. 添加数据约束检查
-- 确保打折券必须有打折率，满减券必须有金额
ALTER TABLE `sms_coupon` 
ADD CONSTRAINT `chk_coupon_type_amount` CHECK (
  (coupon_type = 0 AND amount IS NOT NULL AND amount > 0) OR 
  (coupon_type = 1 AND discount_rate IS NOT NULL AND discount_rate > 0 AND discount_rate < 1)
);

-- 6. 添加索引优化查询性能
ALTER TABLE `sms_coupon` ADD INDEX `idx_coupon_type` (`coupon_type`);
ALTER TABLE `sms_coupon` ADD INDEX `idx_use_type` (`use_type`);
ALTER TABLE `sms_coupon` ADD INDEX `idx_enable_exclude` (`enable_exclude`);

-- 7. 示例数据插入（用于测试）
-- 插入一个打折券示例
INSERT INTO `sms_coupon` (
  `type`, `name`, `platform`, `count`, `amount`, `coupon_type`, `discount_rate`, 
  `per_limit`, `min_point`, `start_time`, `end_time`, `use_type`, `note`, 
  `publish_count`, `use_count`, `receive_count`, `enable_time`
) VALUES (
  0, '新用户8折优惠券', 0, 1000, NULL, 1, 0.80, 
  1, 50.00, '2025-09-01 00:00:00', '2025-12-31 23:59:59', 0, '新用户专享8折优惠', 
  1000, 0, 0, '2025-09-01 00:00:00'
);

-- 插入一个指定分类可用但排除特定商品的满减券示例
INSERT INTO `sms_coupon` (
  `type`, `name`, `platform`, `count`, `amount`, `coupon_type`, `discount_rate`,
  `per_limit`, `min_point`, `start_time`, `end_time`, `use_type`, `enable_exclude`, `note`,
  `publish_count`, `use_count`, `receive_count`, `enable_time`
) VALUES (
  0, '服装类满100减20（排除特定商品）', 0, 500, 20.00, 0, NULL,
  1, 100.00, '2025-09-01 00:00:00', '2025-12-31 23:59:59', 1, 1, '服装类满100减20，排除指定商品',
  500, 0, 0, '2025-09-01 00:00:00'
);

-- =====================================================
-- 变更说明：
-- 1. 新增 coupon_type 字段区分满减券(0)和打折券(1)
-- 2. 新增 discount_rate 字段存储打折率（0.1-0.99）
-- 3. 新增 enable_exclude 字段控制是否启用排除逻辑
-- 4. 保持 use_type 字段原有逻辑：0->全场通用；1->指定分类；2->指定商品
-- 5. 创建两个新的排除关系表，用于存储不可用的商品和分类
-- 6. 添加数据约束确保数据完整性
-- 7. 添加索引优化查询性能
-- 8. 提供数据迁移脚本确保向后兼容
-- =====================================================

-- =====================================================
-- 回滚脚本（如需要回滚变更时使用）
-- =====================================================

-- 回滚脚本 - 删除新增的表
-- DROP TABLE IF EXISTS `sms_coupon_product_exclude_relation`;
-- DROP TABLE IF EXISTS `sms_coupon_product_category_exclude_relation`;

-- 回滚脚本 - 删除新增的字段和约束
-- ALTER TABLE `sms_coupon` DROP CONSTRAINT `chk_coupon_type_amount`;
-- ALTER TABLE `sms_coupon` DROP INDEX `idx_coupon_type`;
-- ALTER TABLE `sms_coupon` DROP INDEX `idx_use_type`;
-- ALTER TABLE `sms_coupon` DROP INDEX `idx_enable_exclude`;
-- ALTER TABLE `sms_coupon` DROP COLUMN `coupon_type`;
-- ALTER TABLE `sms_coupon` DROP COLUMN `discount_rate`;
-- ALTER TABLE `sms_coupon` DROP COLUMN `enable_exclude`;

-- =====================================================
-- 数据验证脚本
-- =====================================================

-- 验证现有数据迁移是否成功
-- SELECT
--   id, name, amount, coupon_type, discount_rate, use_type, enable_exclude,
--   CASE
--     WHEN coupon_type = 0 AND amount IS NOT NULL THEN '满减券-正常'
--     WHEN coupon_type = 1 AND discount_rate IS NOT NULL THEN '打折券-正常'
--     ELSE '数据异常'
--   END AS data_status
-- FROM sms_coupon
-- ORDER BY id;

-- 验证约束是否生效
-- 以下语句应该执行失败（用于测试约束）
-- INSERT INTO sms_coupon (name, coupon_type, amount, discount_rate) VALUES ('测试约束', 0, NULL, NULL); -- 应该失败
-- INSERT INTO sms_coupon (name, coupon_type, amount, discount_rate) VALUES ('测试约束', 1, 10, NULL); -- 应该失败

-- =====================================================
-- 业务逻辑说明
-- =====================================================

-- 优惠券类型判断逻辑：
-- 1. coupon_type = 0 (满减券)：使用 amount 字段，discount_rate 为 NULL
-- 2. coupon_type = 1 (打折券)：使用 discount_rate 字段，amount 为 NULL

-- 适用范围判断逻辑（支持组合使用）：
-- 1. use_type = 0：全场通用
-- 2. use_type = 1：指定分类可用（查询 sms_coupon_product_category_relation）
-- 3. use_type = 2：指定商品可用（查询 sms_coupon_product_relation）
-- 4. enable_exclude = 1 时，额外检查排除逻辑：
--    - 查询 sms_coupon_product_exclude_relation（排除指定商品）
--    - 查询 sms_coupon_product_category_exclude_relation（排除指定分类）
--
-- 组合示例：
-- - use_type=1 + enable_exclude=1：指定分类可用，但排除特定商品/分类
-- - use_type=2 + enable_exclude=1：指定商品可用，但排除特定商品/分类
-- - use_type=0 + enable_exclude=1：全场通用，但排除特定商品/分类

-- 优惠计算逻辑：
-- 满减券：订单金额 >= min_point 时，减免 amount 金额
-- 打折券：订单金额 >= min_point 时，按 discount_rate 计算折扣金额
