-- 分销商申请表字段扩展SQL
-- 用于支持分步骤申请功能
-- 执行时间：2025年9月14日

-- 为 ums_distributor_apply 表添加新字段
ALTER TABLE `ums_distributor_apply` 
ADD COLUMN `self_introduction` text COMMENT '自我介绍' AFTER `review_comment`,
ADD COLUMN `influence_screenshots` text COMMENT '私域影响力截图（JSON格式存储多张图片URL）' AFTER `self_introduction`,
ADD COLUMN `id_card_front_img` varchar(255) DEFAULT NULL COMMENT '身份证正面图片URL' AFTER `influence_screenshots`,
ADD COLUMN `id_card_back_img` varchar(255) DEFAULT NULL COMMENT '身份证反面图片URL' AFTER `id_card_front_img`,
ADD COLUMN `other_certificates` text COMMENT '其他身份证明（JSON格式存储多张图片URL）' AFTER `id_card_back_img`;
