-- =====================================================
-- 优惠券功能扩展 - 数据迁移脚本
-- 确保现有数据向后兼容
-- 执行时间：2025-09-01
-- =====================================================

-- 1. 检查现有数据状态
SELECT 
    COUNT(*) as total_coupons,
    COUNT(CASE WHEN coupon_type IS NULL THEN 1 END) as need_migration_count
FROM sms_coupon;

-- 2. 为现有优惠券设置默认值（如果还没有设置）
UPDATE sms_coupon 
SET 
    coupon_type = 0,           -- 默认为满减券
    discount_rate = NULL,      -- 满减券不需要折扣率
    enable_exclude = 0         -- 默认不启用排除逻辑
WHERE coupon_type IS NULL;

-- 3. 验证数据迁移结果
SELECT 
    id,
    name,
    amount,
    coupon_type,
    discount_rate,
    enable_exclude,
    use_type,
    CASE 
        WHEN coupon_type = 0 AND amount IS NOT NULL THEN '满减券-正常'
        WHEN coupon_type = 1 AND discount_rate IS NOT NULL THEN '打折券-正常'
        ELSE '数据异常'
    END as migration_status
FROM sms_coupon 
ORDER BY id;

-- 4. 创建测试数据
-- 插入测试用的满减券
INSERT INTO sms_coupon (
    type, name, platform, count, amount, coupon_type, discount_rate, enable_exclude,
    per_limit, min_point, start_time, end_time, use_type, note, 
    publish_count, use_count, receive_count, enable_time
) VALUES (
    0, '测试满减券-满100减20', 0, 1000, 20.00, 0, NULL, 0,
    1, 100.00, '2025-09-01 00:00:00', '2025-12-31 23:59:59', 0, '测试用满减券', 
    1000, 0, 0, '2025-09-01 00:00:00'
);

-- 插入测试用的打折券
INSERT INTO sms_coupon (
    type, name, platform, count, amount, coupon_type, discount_rate, enable_exclude,
    per_limit, min_point, start_time, end_time, use_type, note, 
    publish_count, use_count, receive_count, enable_time
) VALUES (
    0, '测试打折券-全场8折', 0, 1000, NULL, 1, 0.80, 0,
    1, 50.00, '2025-09-01 00:00:00', '2025-12-31 23:59:59', 0, '测试用打折券', 
    1000, 0, 0, '2025-09-01 00:00:00'
);

-- 插入测试用的带排除逻辑的优惠券
INSERT INTO sms_coupon (
    type, name, platform, count, amount, coupon_type, discount_rate, enable_exclude,
    per_limit, min_point, start_time, end_time, use_type, note, 
    publish_count, use_count, receive_count, enable_time
) VALUES (
    0, '测试排除券-服装类满100减20（排除特定商品）', 0, 500, 20.00, 0, NULL, 1,
    1, 100.00, '2025-09-01 00:00:00', '2025-12-31 23:59:59', 1, '测试用排除逻辑优惠券', 
    500, 0, 0, '2025-09-01 00:00:00'
);

-- 获取刚插入的测试优惠券ID（用于后续关系表插入）
SET @test_coupon_exclude_id = LAST_INSERT_ID();

-- 为测试排除券添加可用分类关系（假设分类ID为1）
INSERT INTO sms_coupon_product_category_relation (
    coupon_id, product_category_id, product_category_name, parent_category_name
) VALUES (
    @test_coupon_exclude_id, 1, '服装', '时尚'
);

-- 为测试排除券添加排除商品关系（假设商品ID为101）
INSERT INTO sms_coupon_product_exclude_relation (
    coupon_id, product_id, product_name, product_sn
) VALUES (
    @test_coupon_exclude_id, 101, '测试排除商品', 'TEST001'
);

-- 5. 数据完整性检查
-- 检查优惠券数据完整性
SELECT 
    '优惠券数据检查' as check_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN coupon_type = 0 AND amount IS NOT NULL THEN 1 END) as valid_amount_coupons,
    COUNT(CASE WHEN coupon_type = 1 AND discount_rate IS NOT NULL THEN 1 END) as valid_discount_coupons,
    COUNT(CASE WHEN enable_exclude = 1 THEN 1 END) as exclude_enabled_coupons
FROM sms_coupon;

-- 检查排除关系表数据
SELECT 
    '排除商品关系检查' as check_type,
    COUNT(*) as total_exclude_products
FROM sms_coupon_product_exclude_relation;

SELECT 
    '排除分类关系检查' as check_type,
    COUNT(*) as total_exclude_categories
FROM sms_coupon_product_category_exclude_relation;

-- 6. 性能测试查询
-- 测试复杂查询的性能
EXPLAIN SELECT 
    c.*,
    cpr.product_id,
    cpcr.product_category_id,
    cper.product_id as exclude_product_id,
    cpcer.product_category_id as exclude_category_id
FROM sms_coupon c
LEFT JOIN sms_coupon_product_relation cpr ON c.id = cpr.coupon_id
LEFT JOIN sms_coupon_product_category_relation cpcr ON c.id = cpcr.coupon_id
LEFT JOIN sms_coupon_product_exclude_relation cper ON c.id = cper.coupon_id
LEFT JOIN sms_coupon_product_category_exclude_relation cpcer ON c.id = cpcer.coupon_id
WHERE c.enable_exclude = 1
AND c.end_time > NOW()
AND c.start_time <= NOW();

-- 7. 清理测试数据的脚本（可选执行）
/*
-- 删除测试数据
DELETE FROM sms_coupon_product_exclude_relation WHERE coupon_id IN (
    SELECT id FROM sms_coupon WHERE name LIKE '测试%'
);

DELETE FROM sms_coupon_product_category_exclude_relation WHERE coupon_id IN (
    SELECT id FROM sms_coupon WHERE name LIKE '测试%'
);

DELETE FROM sms_coupon_product_category_relation WHERE coupon_id IN (
    SELECT id FROM sms_coupon WHERE name LIKE '测试%'
);

DELETE FROM sms_coupon WHERE name LIKE '测试%';
*/

-- =====================================================
-- 迁移完成检查清单
-- =====================================================
-- [ ] 现有优惠券数据已设置默认值
-- [ ] 新增字段约束正常工作
-- [ ] 测试数据创建成功
-- [ ] 关系表数据正确插入
-- [ ] 查询性能可接受
-- [ ] 数据完整性验证通过

-- =====================================================
-- 注意事项
-- =====================================================
-- 1. 执行前请备份数据库
-- 2. 在测试环境先验证脚本
-- 3. 生产环境执行时建议分批处理大量数据
-- 4. 监控执行过程中的性能影响
-- 5. 执行后验证应用程序功能正常
