-- 应用版本管理表
CREATE TABLE `app_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `version_name` varchar(50) NOT NULL COMMENT '版本名称(如1.2.3)',
  `version_code` int(11) NOT NULL COMMENT '版本号(递增数字)',
  `apk_file_path` varchar(500) NOT NULL COMMENT 'APK文件路径',
  `apk_file_size` bigint(20) NOT NULL COMMENT 'APK文件大小(字节)',
  `apk_file_md5` varchar(32) NOT NULL COMMENT 'APK文件MD5校验值',
  `update_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '更新类型(0:可选更新 1:强制更新)',
  `update_content` text COMMENT '更新内容描述',
  `min_support_version` varchar(50) COMMENT '最低支持版本',
  `target_platform` varchar(20) DEFAULT 'android' COMMENT '目标平台',
  `release_time` datetime COMMENT '发布时间',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活(0:否 1:是)',
  `download_url` varchar(500) COMMENT '下载地址',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_version_name` (`version_name`),
  UNIQUE KEY `uk_version_code` (`version_code`),
  KEY `idx_version_code` (`version_code`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用版本管理表';

-- 设备信息表
CREATE TABLE `app_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `device_id` varchar(100) NOT NULL COMMENT '设备唯一标识',
  `device_name` varchar(100) COMMENT '设备名称',
  `device_model` varchar(100) COMMENT '设备型号',
  `android_version` varchar(50) COMMENT 'Android版本',
  `app_version_name` varchar(50) COMMENT '当前应用版本名称',
  `app_version_code` int(11) COMMENT '当前应用版本号',
  `store_id` bigint(20) COMMENT '门店ID',
  `store_name` varchar(100) COMMENT '门店名称',
  `last_check_time` datetime COMMENT '最后检查更新时间',
  `last_update_time` datetime COMMENT '最后更新时间',
  `update_status` tinyint(1) DEFAULT '0' COMMENT '更新状态(0:正常 1:更新中 2:更新失败)',
  `is_online` tinyint(1) DEFAULT '1' COMMENT '是否在线(0:离线 1:在线)',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_app_version_code` (`app_version_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- 更新日志表
CREATE TABLE `app_update_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `device_id` varchar(100) NOT NULL COMMENT '设备ID',
  `from_version_code` int(11) COMMENT '更新前版本号',
  `to_version_code` int(11) NOT NULL COMMENT '更新后版本号',
  `update_type` tinyint(1) NOT NULL COMMENT '更新类型(0:可选更新 1:强制更新)',
  `update_status` tinyint(1) NOT NULL COMMENT '更新状态(0:开始 1:下载中 2:下载完成 3:安装中 4:成功 5:失败)',
  `download_progress` int(3) DEFAULT '0' COMMENT '下载进度(0-100)',
  `error_message` text COMMENT '错误信息',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `duration` int(11) COMMENT '耗时(秒)',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_update_status` (`update_status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用更新日志表';

-- 插入初始版本数据
INSERT INTO `app_version` (
  `version_name`, `version_code`, `apk_file_path`, `apk_file_size`, `apk_file_md5`,
  `update_type`, `update_content`, `target_platform`, `release_time`, `is_active`
) VALUES (
  '1.0.0', 1000, '/uploads/apk/mall-self-checkout-1.0.0.apk', 25600000, 'placeholder_md5',
  0, '初始版本', 'android', NOW(), 1
);
