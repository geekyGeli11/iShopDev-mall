-- 为分销商申请表添加工作截图字段
-- 执行时间：2025年9月15日

-- 为 ums_distributor_apply 表添加工作截图字段
ALTER TABLE `ums_distributor_apply` 
ADD COLUMN `work_screenshots` text COMMENT '工作截图（JSON格式存储多张图片URL）' AFTER `other_certificates`;
