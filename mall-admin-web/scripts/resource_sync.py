#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
资源同步脚本
根据后端Controller接口同步数据库资源表
"""

import pymysql
from datetime import datetime
from typing import List, Dict, Any

# 数据库配置
DB_CONFIG = {
    'host': 'rm-7xvw6f28955onk26duo.mysql.rds.aliyuncs.com',
    'port': 3306,
    'user': 'guanghengzou',
    'password': 'Guanghengzou2025',
    'database': 'guanghengzou-mall',
    'charset': 'utf8mb4',
    'connect_timeout': 10,
    'read_timeout': 30,
    'write_timeout': 30
}

# 资源分类配置
RESOURCE_CATEGORIES = {
    1: "商品模块",
    2: "订单模块", 
    3: "营销模块",
    4: "权限模块",
    5: "内容模块",
    6: "会员模块",
    7: "其他模块",
    8: "DIY模块",
    9: "推广模块",
    10: "设置模块",
    11: "统计模块"
}

# 资源配置（按后端Controller的@RequestMapping路径整理）
# URL格式必须与Controller中的路径一致，支持Ant风格通配符：
# - * 匹配单层路径
# - ** 匹配多层路径
RESOURCE_CONFIG = [
    # ==================== 商品模块 (category_id=1) ====================
    {"name": "商品列表", "url": "/product/list", "description": "获取商品列表", "category_id": 1},
    {"name": "商品详情", "url": "/product/*", "description": "获取商品详情", "category_id": 1},
    {"name": "添加商品", "url": "/product/create", "description": "添加商品", "category_id": 1},
    {"name": "更新商品", "url": "/product/update/**", "description": "更新商品", "category_id": 1},
    {"name": "删除商品", "url": "/product/delete", "description": "删除商品", "category_id": 1},
    {"name": "商品上下架", "url": "/product/update/publishStatus", "description": "商品上下架", "category_id": 1},
    {"name": "商品推荐状态", "url": "/product/update/recommendStatus", "description": "商品推荐状态", "category_id": 1},
    {"name": "商品审核状态", "url": "/product/update/verifyStatus", "description": "商品审核状态", "category_id": 1},
    {"name": "商品分类列表", "url": "/productCategory/**", "description": "商品分类管理", "category_id": 1},
    {"name": "商品类型列表", "url": "/productAttribute/**", "description": "商品类型管理", "category_id": 1},
    {"name": "商品属性分类", "url": "/productAttribute/category/**", "description": "商品属性分类管理", "category_id": 1},
    {"name": "品牌列表", "url": "/brand/list", "description": "获取品牌列表", "category_id": 1},
    {"name": "品牌详情", "url": "/brand/*", "description": "获取品牌详情", "category_id": 1},
    {"name": "添加品牌", "url": "/brand/create", "description": "添加品牌", "category_id": 1},
    {"name": "更新品牌", "url": "/brand/update/**", "description": "更新品牌", "category_id": 1},
    {"name": "删除品牌", "url": "/brand/delete/**", "description": "删除品牌", "category_id": 1},
    {"name": "SKU库存管理", "url": "/sku/**", "description": "SKU库存管理", "category_id": 1},
    {"name": "库存管理", "url": "/stock/**", "description": "库存管理", "category_id": 1},
    {"name": "门店库存管理", "url": "/storeSkuStock/**", "description": "门店库存管理", "category_id": 1},
    {"name": "回本分析", "url": "/payback/**", "description": "商品回本分析", "category_id": 1},
    {"name": "组合商品管理", "url": "/productBundle/**", "description": "组合商品管理", "category_id": 1},
    {"name": "风格模型管理", "url": "/styleModel/**", "description": "风格模型管理", "category_id": 1},
    {"name": "商品学校关联", "url": "/productSchoolRelation/**", "description": "商品学校关联管理", "category_id": 1},
    
    # ==================== 订单模块 (category_id=2) ====================
    {"name": "订单列表", "url": "/order/list", "description": "获取订单列表", "category_id": 2},
    {"name": "订单详情", "url": "/order/*", "description": "获取订单详情", "category_id": 2},
    {"name": "订单发货", "url": "/order/update/delivery", "description": "订单发货", "category_id": 2},
    {"name": "订单关闭", "url": "/order/update/close", "description": "订单关闭", "category_id": 2},
    {"name": "订单删除", "url": "/order/delete", "description": "订单删除", "category_id": 2},
    {"name": "订单备注", "url": "/order/update/note", "description": "订单备注", "category_id": 2},
    {"name": "订单收货人信息", "url": "/order/update/receiverInfo", "description": "修改收货人信息", "category_id": 2},
    {"name": "订单费用信息", "url": "/order/update/moneyInfo", "description": "修改订单费用", "category_id": 2},
    {"name": "DIY订单列表", "url": "/orderDiy/**", "description": "DIY订单管理", "category_id": 2},
    {"name": "退货申请列表", "url": "/returnApply/list", "description": "获取退货申请列表", "category_id": 2},
    {"name": "退货申请详情", "url": "/returnApply/*", "description": "获取退货申请详情", "category_id": 2},
    {"name": "退货申请处理", "url": "/returnApply/update/**", "description": "处理退货申请", "category_id": 2},
    {"name": "退货原因管理", "url": "/returnReason/**", "description": "退货原因管理", "category_id": 2},
    {"name": "订单设置", "url": "/orderSetting/**", "description": "订单设置管理", "category_id": 2},
    {"name": "收货地址管理", "url": "/companyAddress/**", "description": "收货地址管理", "category_id": 2},
    {"name": "门店地址管理", "url": "/storeAddress/**", "description": "门店地址管理", "category_id": 2},
    {"name": "运费模板管理", "url": "/freightTemplate/**", "description": "运费模板管理", "category_id": 2},
    {"name": "其他销售录入", "url": "/nonSystemSale/**", "description": "其他销售录入管理", "category_id": 2},
    
    # ==================== 营销模块 (category_id=3) ====================
    {"name": "优惠券列表", "url": "/coupon/list", "description": "获取优惠券列表", "category_id": 3},
    {"name": "优惠券详情", "url": "/coupon/*", "description": "获取优惠券详情", "category_id": 3},
    {"name": "添加优惠券", "url": "/coupon/create", "description": "添加优惠券", "category_id": 3},
    {"name": "更新优惠券", "url": "/coupon/update/**", "description": "更新优惠券", "category_id": 3},
    {"name": "删除优惠券", "url": "/coupon/delete/**", "description": "删除优惠券", "category_id": 3},
    {"name": "优惠券领取记录", "url": "/couponHistory/**", "description": "优惠券领取记录", "category_id": 3},
    {"name": "限时优惠管理", "url": "/flash/**", "description": "限时优惠管理", "category_id": 3},
    {"name": "限时优惠场次", "url": "/flashSession/**", "description": "限时优惠场次管理", "category_id": 3},
    {"name": "限时优惠商品", "url": "/flashProductRelation/**", "description": "限时优惠商品关联", "category_id": 3},
    {"name": "品牌推荐管理", "url": "/home/brand/**", "description": "品牌推荐管理", "category_id": 3},
    {"name": "新品推荐管理", "url": "/home/newProduct/**", "description": "新品推荐管理", "category_id": 3},
    {"name": "人气推荐管理", "url": "/home/recommendProduct/**", "description": "人气推荐管理", "category_id": 3},
    {"name": "专题推荐管理", "url": "/home/recommendSubject/**", "description": "专题推荐管理", "category_id": 3},
    {"name": "广告管理", "url": "/home/advertise/**", "description": "广告管理", "category_id": 3},
    {"name": "通知弹窗管理", "url": "/notification/**", "description": "通知弹窗管理", "category_id": 3},
    {"name": "积分营销活动", "url": "/integrationPromotion/**", "description": "积分营销活动管理", "category_id": 3},
    
    # ==================== 权限模块 (category_id=4) ====================
    {"name": "用户列表", "url": "/admin/list", "description": "获取用户列表", "category_id": 4},
    {"name": "用户详情", "url": "/admin/*", "description": "获取用户详情", "category_id": 4},
    {"name": "用户注册", "url": "/admin/register", "description": "用户注册", "category_id": 4},
    {"name": "用户更新", "url": "/admin/update/**", "description": "用户更新", "category_id": 4},
    {"name": "用户删除", "url": "/admin/delete/**", "description": "用户删除", "category_id": 4},
    {"name": "用户角色分配", "url": "/admin/role/**", "description": "用户角色分配", "category_id": 4},
    {"name": "角色列表", "url": "/role/list", "description": "获取角色列表", "category_id": 4},
    {"name": "角色详情", "url": "/role/*", "description": "获取角色详情", "category_id": 4},
    {"name": "添加角色", "url": "/role/create", "description": "添加角色", "category_id": 4},
    {"name": "更新角色", "url": "/role/update/**", "description": "更新角色", "category_id": 4},
    {"name": "删除角色", "url": "/role/delete", "description": "删除角色", "category_id": 4},
    {"name": "角色菜单分配", "url": "/role/allocMenu", "description": "角色菜单分配", "category_id": 4},
    {"name": "角色资源分配", "url": "/role/allocResource", "description": "角色资源分配", "category_id": 4},
    {"name": "菜单列表", "url": "/menu/list/**", "description": "获取菜单列表", "category_id": 4},
    {"name": "菜单详情", "url": "/menu/*", "description": "获取菜单详情", "category_id": 4},
    {"name": "添加菜单", "url": "/menu/create", "description": "添加菜单", "category_id": 4},
    {"name": "更新菜单", "url": "/menu/update/**", "description": "更新菜单", "category_id": 4},
    {"name": "删除菜单", "url": "/menu/delete/**", "description": "删除菜单", "category_id": 4},
    {"name": "资源列表", "url": "/resource/list", "description": "获取资源列表", "category_id": 4},
    {"name": "资源详情", "url": "/resource/*", "description": "获取资源详情", "category_id": 4},
    {"name": "添加资源", "url": "/resource/create", "description": "添加资源", "category_id": 4},
    {"name": "更新资源", "url": "/resource/update/**", "description": "更新资源", "category_id": 4},
    {"name": "删除资源", "url": "/resource/delete/**", "description": "删除资源", "category_id": 4},
    {"name": "资源分类管理", "url": "/resourceCategory/**", "description": "资源分类管理", "category_id": 4},
    
    # ==================== 内容模块 (category_id=5) ====================
    {"name": "专题管理", "url": "/subject/**", "description": "专题管理", "category_id": 5},
    {"name": "优选区域", "url": "/prefrenceArea/**", "description": "优选区域管理", "category_id": 5},
    
    # ==================== 会员模块 (category_id=6) ====================
    {"name": "会员列表", "url": "/member/list", "description": "获取会员列表", "category_id": 6},
    {"name": "会员详情", "url": "/member/*", "description": "获取会员详情", "category_id": 6},
    {"name": "会员更新", "url": "/member/update/**", "description": "更新会员信息", "category_id": 6},
    {"name": "会员等级管理", "url": "/memberLevel/**", "description": "会员等级管理", "category_id": 6},
    {"name": "会员余额管理", "url": "/memberBalance/**", "description": "会员余额管理", "category_id": 6},
    {"name": "充值订单管理", "url": "/rechargeOrder/**", "description": "充值订单管理", "category_id": 6},
    {"name": "游客列表", "url": "/guest/**", "description": "游客管理", "category_id": 6},
    {"name": "积分换购配置", "url": "/pointsProductConfig/**", "description": "积分换购商品配置", "category_id": 6},
    {"name": "积分优惠券配置", "url": "/pointsCouponConfig/**", "description": "积分优惠券配置", "category_id": 6},
    {"name": "积分兑换记录", "url": "/pointsExchangeLog/**", "description": "积分兑换记录", "category_id": 6},
    
    # ==================== 其他模块 (category_id=7) ====================
    {"name": "OSS文件上传", "url": "/aliyun/oss/**", "description": "阿里云OSS文件上传", "category_id": 7},
    {"name": "COS文件上传", "url": "/cos/**", "description": "腾讯云COS文件上传", "category_id": 7},
    {"name": "Minio文件上传", "url": "/minio/**", "description": "Minio文件上传", "category_id": 7},
    {"name": "学校管理", "url": "/school/**", "description": "学校管理", "category_id": 7},
    {"name": "应用更新管理", "url": "/appUpdate/**", "description": "自助收银应用更新管理", "category_id": 7},
    
    # ==================== DIY模块 (category_id=8) ====================
    {"name": "DIY素材分类", "url": "/diyMaterialCategory/**", "description": "DIY素材分类管理", "category_id": 8},
    {"name": "DIY素材管理", "url": "/diyMaterial/**", "description": "DIY素材管理", "category_id": 8},
    {"name": "DIY模板管理", "url": "/diyTemplate/**", "description": "DIY模板管理", "category_id": 8},
    {"name": "DIY模板面管理", "url": "/diyTemplateSurface/**", "description": "DIY模板面管理", "category_id": 8},
    {"name": "DIY区域管理", "url": "/diyArea/**", "description": "DIY区域管理", "category_id": 8},
    {"name": "装修管理", "url": "/decorate/**", "description": "页面装修管理", "category_id": 8},
    
    # ==================== 推广模块 (category_id=9) ====================
    {"name": "邀请配置", "url": "/inviteConfig/**", "description": "邀请配置管理", "category_id": 9},
    {"name": "邀请关系", "url": "/inviteRelation/**", "description": "邀请关系管理", "category_id": 9},
    {"name": "邀请奖励", "url": "/inviteReward/**", "description": "邀请奖励管理", "category_id": 9},
    {"name": "邀请统计", "url": "/inviteStatistics/**", "description": "邀请统计数据", "category_id": 9},
    {"name": "提现管理", "url": "/inviteWithdraw/**", "description": "提现管理", "category_id": 9},
    {"name": "推广大使申请", "url": "/distributorApply/**", "description": "推广大使申请管理", "category_id": 9},
    {"name": "推广大使管理", "url": "/distributor/**", "description": "推广大使管理", "category_id": 9},
    {"name": "佣金配置", "url": "/commissionConfig/**", "description": "佣金配置管理", "category_id": 9},
    
    # ==================== 设置模块 (category_id=10) ====================
    {"name": "签到配置", "url": "/signin/config", "description": "签到配置管理", "category_id": 10},
    {"name": "签到记录", "url": "/signin/logs", "description": "签到记录查询", "category_id": 10},
    {"name": "签到统计", "url": "/signin/statistics", "description": "签到统计数据", "category_id": 10},
    {"name": "充值配置", "url": "/rechargeConfig/**", "description": "充值配置管理", "category_id": 10},
    {"name": "客服配置", "url": "/customerService/**", "description": "客服配置管理", "category_id": 10},
    
    # ==================== 统计模块 (category_id=11) ====================
    {"name": "首页统计", "url": "/home/statistics/**", "description": "首页统计数据", "category_id": 11},
    {"name": "仪表盘筛选", "url": "/dashboard/**", "description": "仪表盘筛选配置", "category_id": 11},
]


class ResourceSyncService:
    def __init__(self):
        self.connection = None
        self.cursor = None
        
    def connect_db(self):
        """连接数据库"""
        try:
            self.connection = pymysql.connect(**DB_CONFIG)
            self.cursor = self.connection.cursor()
            print("✅ 数据库连接成功")
        except Exception as e:
            print(f"❌ 数据库连接失败: {e}")
            raise
    
    def close_db(self):
        """关闭数据库连接"""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        print("✅ 数据库连接已关闭")
    
    def get_existing_resources(self) -> Dict[str, int]:
        """获取现有资源映射 {url: id}"""
        sql = "SELECT id, url FROM ums_resource"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return {url: res_id for res_id, url in results}
    
    def get_existing_categories(self) -> Dict[int, str]:
        """获取现有资源分类"""
        sql = "SELECT id, name FROM ums_resource_category"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return {cat_id: name for cat_id, name in results}
    
    def sync_categories(self):
        """同步资源分类"""
        existing = self.get_existing_categories()
        
        for cat_id, cat_name in RESOURCE_CATEGORIES.items():
            if cat_id not in existing:
                sql = """
                INSERT INTO ums_resource_category (id, create_time, name, sort)
                VALUES (%s, %s, %s, %s)
                """
                self.cursor.execute(sql, (cat_id, datetime.now(), cat_name, 0))
                print(f"➕ 新增资源分类: {cat_name}")
            else:
                print(f"✓ 资源分类已存在: {cat_name}")
    
    def insert_resource(self, resource_data: Dict[str, Any]) -> int:
        """插入资源"""
        sql = """
        INSERT INTO ums_resource (create_time, name, url, description, category_id)
        VALUES (%(create_time)s, %(name)s, %(url)s, %(description)s, %(category_id)s)
        """
        self.cursor.execute(sql, resource_data)
        return self.cursor.lastrowid

    def sync_resources(self):
        """同步资源"""
        try:
            self.connect_db()
            
            # 先同步资源分类
            print("\n📁 同步资源分类...")
            self.sync_categories()
            
            existing_resources = self.get_existing_resources()
            print(f"\n📊 当前数据库中有 {len(existing_resources)} 个资源")

            inserted_count = 0
            skipped_count = 0

            print("\n📦 同步资源...")
            for resource in RESOURCE_CONFIG:
                url = resource['url']
                
                if url in existing_resources:
                    skipped_count += 1
                    print(f"  ✓ 已存在: {resource['name']} ({url})")
                else:
                    resource_data = {
                        'create_time': datetime.now(),
                        'name': resource['name'],
                        'url': resource['url'],
                        'description': resource['description'],
                        'category_id': resource['category_id']
                    }
                    self.insert_resource(resource_data)
                    inserted_count += 1
                    print(f"  ➕ 新增: {resource['name']} ({url})")

            self.connection.commit()
            print(f"\n✅ 资源同步完成!")
            print(f"📊 统计: 新增 {inserted_count} 个, 已存在 {skipped_count} 个")
            print(f"\n⚠️  重要提示:")
            print(f"   资源同步到数据库后，需要刷新Redis缓存才能生效！")
            print(f"   方法1: 重启 mall-admin 服务")
            print(f"   方法2: 调用接口 POST /resource/initPathResourceMap")

        except Exception as e:
            if self.connection:
                self.connection.rollback()
            print(f"❌ 资源同步失败: {e}")
            raise
        finally:
            self.close_db()

    def list_resources(self):
        """列出当前所有资源"""
        try:
            self.connect_db()
            
            sql = """
            SELECT r.id, r.name, r.url, c.name as category_name
            FROM ums_resource r
            LEFT JOIN ums_resource_category c ON r.category_id = c.id
            ORDER BY r.category_id, r.id
            """
            self.cursor.execute(sql)
            results = self.cursor.fetchall()
            
            print(f"\n📋 当前资源列表 (共 {len(results)} 个):")
            print("-" * 70)
            
            current_category = None
            for row in results:
                res_id, name, url, category = row
                if category != current_category:
                    current_category = category
                    print(f"\n【{category or '未分类'}】")
                print(f"  {res_id}. {name}: {url}")
            
        except Exception as e:
            print(f"❌ 查询失败: {e}")
        finally:
            self.close_db()


def main():
    """主函数"""
    print("🚀 资源同步工具")
    print("=" * 50)

    service = ResourceSyncService()

    while True:
        print("\n请选择操作:")
        print("1. 查看当前资源列表")
        print("2. 同步资源到数据库")
        print("3. 退出")
        
        choice = input("\n请输入选项 (1-3): ").strip()
        
        if choice == '1':
            service.list_resources()
        elif choice == '2':
            confirm = input("确认同步资源? (y/n): ").lower().strip()
            if confirm == 'y':
                service.sync_resources()
        elif choice == '3':
            print("👋 再见!")
            break
        else:
            print("❌ 无效选项")


if __name__ == "__main__":
    main()
