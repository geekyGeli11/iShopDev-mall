-- ===================================
-- 退货退款流程数据库结构优化
-- ===================================

-- 1. 为订单表添加售后状态字段
ALTER TABLE `oms_order` 
ADD COLUMN `after_sale_status` int DEFAULT '0' COMMENT '售后状态：0->无售后；1->售后申请中；2->售后处理中；3->售后完成；4->售后拒绝' 
AFTER `pickup_operator`;

-- 2. 完善售后申请表字段
ALTER TABLE `oms_order_return_apply`
ADD COLUMN `return_type` tinyint DEFAULT '2' COMMENT '售后类型：1->仅退款；2->退货退款' AFTER `refund_status`,
ADD COLUMN `refund_process_status` tinyint DEFAULT '0' COMMENT '退款处理状态：0->未退款；1->退款中；2->退款成功；3->退款失败' AFTER `return_type`,
ADD COLUMN `refund_transaction_id` varchar(64) DEFAULT NULL COMMENT '微信退款交易号' AFTER `refund_process_status`,
ADD COLUMN `refund_time` datetime DEFAULT NULL COMMENT '退款完成时间' AFTER `refund_transaction_id`,
ADD COLUMN `refund_fail_reason` varchar(500) DEFAULT NULL COMMENT '退款失败原因' AFTER `refund_time`,
ADD COLUMN `return_address_name` varchar(100) DEFAULT NULL COMMENT '退货收货人姓名' AFTER `refund_fail_reason`,
ADD COLUMN `return_address_phone` varchar(20) DEFAULT NULL COMMENT '退货收货人电话' AFTER `return_address_name`,
ADD COLUMN `return_address_detail` varchar(500) DEFAULT NULL COMMENT '退货收货地址' AFTER `return_address_phone`;

-- 3. 创建售后申请详情表（支持多商品售后）
CREATE TABLE `oms_order_return_apply_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `return_apply_id` bigint NOT NULL COMMENT '退货申请ID',
  `order_item_id` bigint NOT NULL COMMENT '订单项ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_pic` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `product_attr` varchar(500) DEFAULT NULL COMMENT '商品属性',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `product_quantity` int NOT NULL COMMENT '申请退货数量',
  `return_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_return_apply_id` (`return_apply_id`) USING BTREE,
  KEY `idx_order_item_id` (`order_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='订单退货申请详情';

-- 4. 为现有数据设置默认值
UPDATE `oms_order` SET `after_sale_status` = 0 WHERE `after_sale_status` IS NULL;
UPDATE `oms_order_return_apply` SET `return_type` = 2 WHERE `return_type` IS NULL;
UPDATE `oms_order_return_apply` SET `refund_process_status` = 0 WHERE `refund_process_status` IS NULL;

-- 5. 添加索引优化查询性能
ALTER TABLE `oms_order` ADD INDEX `idx_after_sale_status` (`after_sale_status`);
ALTER TABLE `oms_order_return_apply` ADD INDEX `idx_return_type` (`return_type`);
ALTER TABLE `oms_order_return_apply` ADD INDEX `idx_refund_process_status` (`refund_process_status`);

-- ===================================
-- 退款流程状态说明
-- ===================================
/*
完整的退货退款流程状态管理：

1. 售后申请状态 (status)：
   - 0: 待处理 (用户提交申请，等待商家处理)
   - 1: 退货中 (商家同意退货，用户需要寄回商品)
   - 2: 已完成 (退货退款全部完成)
   - 3: 已拒绝 (商家拒绝售后申请)

2. 退款处理状态 (refund_process_status)：
   - 0: 未退款 (还未开始退款流程)
   - 1: 退款中 (已调用微信支付退款接口，等待处理结果)
   - 2: 退款成功 (微信支付退款成功)
   - 3: 退款失败 (微信支付退款失败，需要人工处理)

3. 退款流程时机：
   - 仅退款：商家同意申请后立即退款 (status=1 时触发退款)
   - 退货退款：商家收到退货后才退款 (status=2 时触发退款)

4. 字段关联关系：
   - refund_status: 原有字段，0-未退款，1-已退款 (兼容性保留)
   - refund_process_status: 新增字段，详细的退款处理状态
   - refund_transaction_id: 微信退款交易号
   - refund_time: 退款完成时间
   - refund_fail_reason: 退款失败原因
*/