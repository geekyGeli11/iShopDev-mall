-- 资源数据备份
-- 备份时间: 2025-12-15 00:43:00.988736

-- 资源分类
INSERT INTO ums_resource_category VALUES (1, datetime.datetime(2020, 2, 5, 10, 21, 44), '商品模块', 0);
INSERT INTO ums_resource_category VALUES (2, datetime.datetime(2020, 2, 5, 10, 22, 34), '订单模块', 0);
INSERT INTO ums_resource_category VALUES (3, datetime.datetime(2020, 2, 5, 10, 22, 48), '营销模块', 0);
INSERT INTO ums_resource_category VALUES (4, datetime.datetime(2020, 2, 5, 10, 23, 4), '权限模块', 0);
INSERT INTO ums_resource_category VALUES (5, datetime.datetime(2020, 2, 7, 16, 34, 27), '内容模块', 0);
INSERT INTO ums_resource_category VALUES (7, datetime.datetime(2020, 9, 19, 15, 49, 8), '其他模块', 0);

-- 资源
INSERT INTO ums_resource VALUES (1, datetime.datetime(2020, 2, 4, 17, 4, 55), '商品品牌管理', '/brand/**', None, 1);
INSERT INTO ums_resource VALUES (2, datetime.datetime(2020, 2, 4, 17, 5, 35), '商品属性分类管理', '/productAttribute/category/**', None, 1);
INSERT INTO ums_resource VALUES (3, datetime.datetime(2020, 2, 4, 17, 6, 13), '商品属性管理', '/productAttribute/**', None, 1);
INSERT INTO ums_resource VALUES (4, datetime.datetime(2020, 2, 4, 17, 7, 15), '商品分类管理', '/productCategory/**', None, 1);
INSERT INTO ums_resource VALUES (5, datetime.datetime(2020, 2, 4, 17, 9, 16), '商品管理', '/product/**', None, 1);
INSERT INTO ums_resource VALUES (6, datetime.datetime(2020, 2, 4, 17, 9, 53), '商品库存管理', '/sku/**', None, 1);
INSERT INTO ums_resource VALUES (8, datetime.datetime(2020, 2, 5, 14, 43, 37), '订单管理', '/order/**', '', 2);
INSERT INTO ums_resource VALUES (9, datetime.datetime(2020, 2, 5, 14, 44, 22), ' 订单退货申请管理', '/returnApply/**', '', 2);
INSERT INTO ums_resource VALUES (10, datetime.datetime(2020, 2, 5, 14, 45, 8), '退货原因管理', '/returnReason/**', '', 2);
INSERT INTO ums_resource VALUES (11, datetime.datetime(2020, 2, 5, 14, 45, 43), '订单设置管理', '/orderSetting/**', '', 2);
INSERT INTO ums_resource VALUES (12, datetime.datetime(2020, 2, 5, 14, 46, 23), '收货地址管理', '/companyAddress/**', '', 2);
INSERT INTO ums_resource VALUES (13, datetime.datetime(2020, 2, 7, 16, 37, 22), '优惠券管理', '/coupon/**', '', 3);
INSERT INTO ums_resource VALUES (14, datetime.datetime(2020, 2, 7, 16, 37, 59), '优惠券领取记录管理', '/couponHistory/**', '', 3);
INSERT INTO ums_resource VALUES (15, datetime.datetime(2020, 2, 7, 16, 38, 28), '限时购活动管理', '/flash/**', '', 3);
INSERT INTO ums_resource VALUES (16, datetime.datetime(2020, 2, 7, 16, 38, 59), '限时购商品关系管理', '/flashProductRelation/**', '', 3);
INSERT INTO ums_resource VALUES (17, datetime.datetime(2020, 2, 7, 16, 39, 22), '限时购场次管理', '/flashSession/**', '', 3);
INSERT INTO ums_resource VALUES (18, datetime.datetime(2020, 2, 7, 16, 40, 7), '首页轮播广告管理', '/home/advertise/**', '', 3);
INSERT INTO ums_resource VALUES (19, datetime.datetime(2020, 2, 7, 16, 40, 34), '首页品牌管理', '/home/brand/**', '', 3);
INSERT INTO ums_resource VALUES (20, datetime.datetime(2020, 2, 7, 16, 41, 6), '首页新品管理', '/home/newProduct/**', '', 3);
INSERT INTO ums_resource VALUES (21, datetime.datetime(2020, 2, 7, 16, 42, 16), '首页人气推荐管理', '/home/recommendProduct/**', '', 3);
INSERT INTO ums_resource VALUES (22, datetime.datetime(2020, 2, 7, 16, 42, 48), '首页专题推荐管理', '/home/recommendSubject/**', '', 3);
INSERT INTO ums_resource VALUES (23, datetime.datetime(2020, 2, 7, 16, 44, 56), ' 商品优选管理', '/prefrenceArea/**', '', 5);
INSERT INTO ums_resource VALUES (24, datetime.datetime(2020, 2, 7, 16, 45, 39), '商品专题管理', '/subject/**', '', 5);
INSERT INTO ums_resource VALUES (25, datetime.datetime(2020, 2, 7, 16, 47, 34), '后台用户管理', '/admin/**', '', 4);
INSERT INTO ums_resource VALUES (26, datetime.datetime(2020, 2, 7, 16, 48, 24), '后台用户角色管理', '/role/**', '', 4);
INSERT INTO ums_resource VALUES (27, datetime.datetime(2020, 2, 7, 16, 48, 48), '后台菜单管理', '/menu/**', '', 4);
INSERT INTO ums_resource VALUES (28, datetime.datetime(2020, 2, 7, 16, 49, 18), '后台资源分类管理', '/resourceCategory/**', '', 4);
INSERT INTO ums_resource VALUES (29, datetime.datetime(2020, 2, 7, 16, 49, 45), '后台资源管理', '/resource/**', '', 4);
INSERT INTO ums_resource VALUES (30, datetime.datetime(2020, 9, 19, 15, 47, 57), '会员等级管理', '/memberLevel/**', '', 7);
INSERT INTO ums_resource VALUES (31, datetime.datetime(2020, 9, 19, 15, 51, 29), '获取登录用户信息', '/admin/info', '用户登录必配', 4);
INSERT INTO ums_resource VALUES (32, datetime.datetime(2020, 9, 19, 15, 53, 34), '用户登出', '/admin/logout', '用户登出必配', 4);
INSERT INTO ums_resource VALUES (50, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请配置查询', '/invite/config', '获取邀请功能配置', 1);
INSERT INTO ums_resource VALUES (51, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请配置更新', '/invite/config/**', '更新邀请功能配置', 1);
INSERT INTO ums_resource VALUES (52, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请配置历史', '/invite/config/history', '查询配置变更历史', 1);
INSERT INTO ums_resource VALUES (53, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请记录查询', '/invite/relations', '查询邀请关系列表', 1);
INSERT INTO ums_resource VALUES (54, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请记录详情', '/invite/relations/*', '查看邀请关系详情', 1);
INSERT INTO ums_resource VALUES (55, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请记录管理', '/invite/relations/**', '管理邀请关系状态', 1);
INSERT INTO ums_resource VALUES (56, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请记录导出', '/invite/relations/export', '导出邀请记录', 1);
INSERT INTO ums_resource VALUES (57, datetime.datetime(2025, 6, 30, 16, 9, 25), '奖励记录查询', '/invite/rewards', '查询奖励记录列表', 1);
INSERT INTO ums_resource VALUES (58, datetime.datetime(2025, 6, 30, 16, 9, 25), '奖励记录详情', '/invite/rewards/*', '查看奖励记录详情', 1);
INSERT INTO ums_resource VALUES (59, datetime.datetime(2025, 6, 30, 16, 9, 25), '奖励记录管理', '/invite/rewards/**', '管理奖励发放', 1);
INSERT INTO ums_resource VALUES (60, datetime.datetime(2025, 6, 30, 16, 9, 25), '奖励记录导出', '/invite/rewards/export', '导出奖励记录', 1);
INSERT INTO ums_resource VALUES (61, datetime.datetime(2025, 6, 30, 16, 9, 25), '提现申请查询', '/invite/withdraw', '查询提现申请列表', 1);
INSERT INTO ums_resource VALUES (62, datetime.datetime(2025, 6, 30, 16, 9, 25), '提现申请详情', '/invite/withdraw/*', '查看提现申请详情', 1);
INSERT INTO ums_resource VALUES (63, datetime.datetime(2025, 6, 30, 16, 9, 25), '提现申请审批', '/invite/withdraw/*/approve', '审批提现申请', 1);
INSERT INTO ums_resource VALUES (64, datetime.datetime(2025, 6, 30, 16, 9, 25), '提现申请导出', '/invite/withdraw/export', '导出提现记录', 1);
INSERT INTO ums_resource VALUES (65, datetime.datetime(2025, 6, 30, 16, 9, 25), '邀请数据统计', '/invite/statistics/**', '查看邀请数据统计', 1);
