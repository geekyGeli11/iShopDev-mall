#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
菜单同步脚本
根据前端路由配置同步数据库菜单表
"""

import pymysql
import json
from datetime import datetime
from typing import List, Dict, Any

# 数据库配置
# DB_CONFIG = {
#     'host': 'rm-7xvw6f28955onk26duo.mysql.rds.aliyuncs.com',
#     'port': 3306,
#     'user': 'guanghengzou',
#     'password': 'Guanghengzou2025',
#     'database': 'guanghengzou-mall',
#     'charset': 'utf8mb4'
# }

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '321Wssba',
    'database': 'guanghengzou_mall',
    'charset': 'utf8mb4',
    'use_unicode': True,
    'autocommit': False
}

# 前端路由配置（按前端实际显示顺序配置）
ROUTER_CONFIG = [
    {
        "name": "school",
        "title": "学校管理",
        "icon": "education",
        "sort": 1,
        "children": [
            {"name": "schoolManage", "title": "学校管理", "icon": "education", "sort": 1}
        ]
    },
    {
        "name": "home",
        "title": "仪表盘",
        "icon": "home",
        "sort": 2,
        "children": [
            {"name": "home", "title": "仪表盘", "icon": "dashboard", "sort": 1}
        ]
    },
    {
        "name": "pms",
        "title": "商品",
        "icon": "product",
        "sort": 12,
        "children": [
            {"name": "product", "title": "商品列表", "icon": "product-list", "sort": 1},
            {"name": "addProduct", "title": "添加商品", "icon": "product-add", "sort": 2, "hidden": True},
            {"name": "updateProduct", "title": "修改商品", "icon": "product-add", "sort": 3, "hidden": True},
            {"name": "productCate", "title": "商品分类", "icon": "product-cate", "sort": 4},
            {"name": "addProductCate", "title": "添加商品分类", "icon": "", "sort": 5, "hidden": True},
            {"name": "updateProductCate", "title": "修改商品分类", "icon": "", "sort": 6, "hidden": True},
            {"name": "productAttr", "title": "商品类型", "icon": "product-attr", "sort": 7},
            {"name": "productAttrList", "title": "商品属性列表", "icon": "", "sort": 8, "hidden": True},
            {"name": "addProductAttr", "title": "添加商品属性", "icon": "", "sort": 9, "hidden": True},
            {"name": "updateProductAttr", "title": "修改商品属性", "icon": "", "sort": 10, "hidden": True},
            {"name": "brand", "title": "品牌管理", "icon": "product-brand", "sort": 11},
            {"name": "addBrand", "title": "添加品牌", "icon": "", "sort": 12, "hidden": True},
            {"name": "updateBrand", "title": "编辑品牌", "icon": "", "sort": 13, "hidden": True},
            {"name": "stock", "title": "库存管理", "icon": "stock", "sort": 14},
            {"name": "payback", "title": "回本分析", "icon": "chart", "sort": 15},
            {"name": "productBundle", "title": "组合商品", "icon": "product-list", "sort": 16},
            {"name": "addProductBundle", "title": "添加组合商品", "icon": "", "sort": 17, "hidden": True},
            {"name": "updateProductBundle", "title": "编辑组合商品", "icon": "", "sort": 18, "hidden": True}
        ]
    },
    {
        "name": "diy",
        "title": "DIY管理",
        "icon": "edit",
        "sort": 11,
        "children": [
            {"name": "diyMaterialCategory", "title": "素材分类管理DIY", "icon": "folder", "sort": 1},
            {"name": "addDiyMaterialCategory", "title": "添加DIY素材分类", "icon": "", "sort": 2, "hidden": True},
            {"name": "updateDiyMaterialCategory", "title": "编辑DIY素材分类", "icon": "", "sort": 3, "hidden": True},
            {"name": "diyMaterial", "title": "DIY素材管理", "icon": "picture", "sort": 4},
            {"name": "styleModel", "title": "风格模型管理", "icon": "style", "sort": 5},
            {"name": "addDiyMaterial", "title": "添加DIY素材", "icon": "", "sort": 6, "hidden": True},
            {"name": "updateDiyMaterial", "title": "编辑DIY素材", "icon": "", "sort": 7, "hidden": True},
            {"name": "addStyleModel", "title": "添加风格模型", "icon": "", "sort": 8, "hidden": True},
            {"name": "updateStyleModel", "title": "编辑风格模型", "icon": "", "sort": 9, "hidden": True},
            {"name": "styleModelProducts", "title": "风格模型商品管理", "icon": "", "sort": 10, "hidden": True},
            {"name": "diyTemplate", "title": "DIY模板管理", "icon": "template", "sort": 11},
            {"name": "addDiyTemplate", "title": "添加DIY模板", "icon": "", "sort": 12, "hidden": True},
            {"name": "updateDiyTemplate", "title": "编辑DIY模板", "icon": "", "sort": 13, "hidden": True},
            {"name": "diyTemplateDetail", "title": "DIY模板详情", "icon": "", "sort": 14, "hidden": True},
            {"name": "diyTemplatePreview", "title": "DIY模板预览", "icon": "", "sort": 15, "hidden": True}
        ]
    },
    {
        "name": "oms",
        "title": "订单",
        "icon": "order",
        "sort": 10,
        "children": [
            {"name": "order", "title": "订单列表", "icon": "product-list", "sort": 1},
            {"name": "orderDetail", "title": "订单详情", "icon": "", "sort": 2, "hidden": True},
            {"name": "deliverOrderList", "title": "发货列表", "icon": "", "sort": 3, "hidden": True},
            {"name": "diyOrder", "title": "DIY订单列表", "icon": "edit", "sort": 4},
            {"name": "diyOrderDetail", "title": "DIY订单详情", "icon": "", "sort": 5, "hidden": True},
            {"name": "returnApply", "title": "退货申请处理", "icon": "order-return", "sort": 6},
            {"name": "returnApplyDetail", "title": "退货原因详情", "icon": "", "sort": 7, "hidden": True},
            {"name": "nonSystemSale", "title": "其他销售录入", "icon": "money", "sort": 8}
        ]
    },
    {
        "name": "member",
        "title": "会员",
        "icon": "user",
        "sort": 9,
        "children": [
            {"name": "memberList", "title": "会员列表", "icon": "user", "sort": 1},
            {"name": "guestList", "title": "游客列表", "icon": "user-solid", "sort": 2},
            {"name": "memberDetail", "title": "用户详情", "icon": "", "sort": 3, "hidden": True},
            {"name": "memberRecharge", "title": "充值记录", "icon": "money", "sort": 4},
            {"name": "memberSignin", "title": "签到记录", "icon": "list", "sort": 5},
            {"name": "memberLevel", "title": "会员等级管理", "icon": "star", "sort": 6},
            {"name": "memberPointsExchange", "title": "积分换购配置", "icon": "money", "sort": 7}
        ]
    },
    {
        "name": "promotion",
        "title": "推广",
        "icon": "user-plus",
        "sort": 8,
        "children": [
            {"name": "promotionStatistics", "title": "数据统计", "icon": "chart", "sort": 1},
            {"name": "promotionRelations", "title": "邀请记录", "icon": "list", "sort": 2},
            {"name": "promotionRewards", "title": "奖励记录", "icon": "money", "sort": 3},
            {"name": "promotionWithdraw", "title": "提现管理", "icon": "credit-card", "sort": 4},
            {"name": "ambassadorApply", "title": "推广大使申请", "icon": "user-check", "sort": 5},
            {"name": "ambassadorManage", "title": "推广大使管理", "icon": "users", "sort": 6}
        ]
    },
    {
        "name": "sms",
        "title": "营销",
        "icon": "sms",
        "sort": 7,
        "children": [
            {"name": "flash", "title": "限时优惠", "icon": "sms-flash", "sort": 1},
            {"name": "flashSession", "title": "限时优惠时间段列表", "icon": "", "sort": 2, "hidden": True},
            {"name": "selectSession", "title": "限时优惠时间段选择", "icon": "", "sort": 3, "hidden": True},
            {"name": "flashProductRelation", "title": "限时优惠商品列表", "icon": "", "sort": 4, "hidden": True},
            {"name": "coupon", "title": "优惠券", "icon": "sms-coupon", "sort": 5},
            {"name": "addCoupon", "title": "添加优惠券", "icon": "", "sort": 6, "hidden": True},
            {"name": "updateCoupon", "title": "修改优惠券", "icon": "", "sort": 7, "hidden": True},
            {"name": "couponHistory", "title": "优惠券领取详情", "icon": "", "sort": 8, "hidden": True},
            {"name": "integrationPromotion", "title": "积分营销活动", "icon": "sms-coupon", "sort": 9},
            {"name": "homeBrand", "title": "品牌推荐", "icon": "product-brand", "sort": 10},
            {"name": "homeNew", "title": "热榜推荐", "icon": "sms-new", "sort": 11},
            {"name": "homeHot", "title": "爆品榜单", "icon": "sms-hot", "sort": 12},
            {"name": "homeSubject", "title": "专题推荐", "icon": "sms-subject", "sort": 13},
            {"name": "homeAdvertise", "title": "banner列表", "icon": "sms-ad", "sort": 14},
            {"name": "addHomeAdvertise", "title": "添加广告", "icon": "", "sort": 15, "hidden": True},
            {"name": "updateHomeAdvertise", "title": "编辑广告", "icon": "", "sort": 16, "hidden": True},
            {"name": "notification", "title": "通知弹窗", "icon": "sms-ad", "sort": 17},
            {"name": "addNotification", "title": "添加通知", "icon": "", "sort": 18, "hidden": True},
            {"name": "updateNotification", "title": "编辑通知", "icon": "", "sort": 19, "hidden": True},
            {"name": "notificationReadStats", "title": "通知阅读统计", "icon": "", "sort": 20, "hidden": True}
        ]
    }
]

class MenuSyncService:
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
    
    def get_existing_menus(self) -> Dict[str, int]:
        """获取现有菜单映射 {name: id}"""
        sql = "SELECT id, name FROM ums_menu"
        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return {name: menu_id for menu_id, name in results}
    
    def get_max_menu_id(self) -> int:
        """获取当前最大菜单ID"""
        sql = "SELECT IFNULL(MAX(id), 0) FROM ums_menu"
        self.cursor.execute(sql)
        return self.cursor.fetchone()[0]

    def remove_deprecated_menus(self):
        """删除已废弃的菜单（如溯源相关）"""
        deprecated_names = ['cms', 'source']
        deprecated_titles = ['溯源']

        # 删除按名称匹配的菜单
        for name in deprecated_names:
            sql = "DELETE FROM ums_menu WHERE name = %s"
            self.cursor.execute(sql, (name,))

        # 删除按标题匹配的菜单
        for title in deprecated_titles:
            sql = "DELETE FROM ums_menu WHERE title LIKE %s"
            self.cursor.execute(sql, (f'%{title}%',))

        print("🗑️ 已删除废弃的菜单项")

    def insert_menu(self, menu_data: Dict[str, Any]) -> int:
        """插入菜单项"""
        sql = """
        INSERT INTO ums_menu (id, parent_id, create_time, title, level, sort, name, icon, hidden)
        VALUES (%(id)s, %(parent_id)s, %(create_time)s, %(title)s, %(level)s, %(sort)s, %(name)s, %(icon)s, %(hidden)s)
        """
        self.cursor.execute(sql, menu_data)
        return menu_data['id']

    def update_menu(self, menu_id: int, menu_data: Dict[str, Any]):
        """更新菜单项"""
        sql = """
        UPDATE ums_menu
        SET title=%(title)s, sort=%(sort)s, icon=%(icon)s, hidden=%(hidden)s
        WHERE id=%(id)s
        """
        menu_data['id'] = menu_id
        self.cursor.execute(sql, menu_data)

    def sync_menus(self):
        """同步菜单"""
        try:
            self.connect_db()

            # 删除溯源相关菜单
            self.remove_deprecated_menus()

            existing_menus = self.get_existing_menus()
            current_max_id = self.get_max_menu_id()
            next_id = current_max_id + 1

            print(f"📊 当前数据库中有 {len(existing_menus)} 个菜单")
            print(f"🔢 当前最大菜单ID: {current_max_id}")

            # 继续添加更多路由配置
            additional_routes = [
                {
                    "name": "ums",
                    "title": "权限",
                    "icon": "ums",
                    "sort": 6,
                    "children": [
                        {"name": "admin", "title": "员工列表", "icon": "ums-admin", "sort": 1},
                        {"name": "role", "title": "角色列表", "icon": "ums-role", "sort": 2},
                        {"name": "allocMenu", "title": "分配菜单", "icon": "", "sort": 3, "hidden": True},
                        {"name": "allocResource", "title": "分配资源", "icon": "", "sort": 4, "hidden": True},
                        {"name": "menu", "title": "菜单列表", "icon": "ums-menu", "sort": 5},
                        {"name": "addMenu", "title": "添加菜单", "icon": "", "sort": 6, "hidden": True},
                        {"name": "updateMenu", "title": "修改菜单", "icon": "", "sort": 7, "hidden": True},
                        {"name": "resource", "title": "资源列表", "icon": "ums-resource", "sort": 8},
                        {"name": "resourceCategory", "title": "资源分类", "icon": "", "sort": 9, "hidden": True}
                    ]
                },
                {
                    "name": "settings",
                    "title": "设置",
                    "icon": "setting",
                    "sort": 5,
                    "children": [
                        {"name": "storeAddress", "title": "门店地址设置", "icon": "location", "sort": 1},
                        {"name": "returnReason", "title": "售后原因设置", "icon": "order-return-reason", "sort": 2},
                        {"name": "freight", "title": "运费模板设置", "icon": "location", "sort": 3},
                        {"name": "addFreight", "title": "添加运费模板", "icon": "", "sort": 4, "hidden": True},
                        {"name": "updateFreight", "title": "编辑运费模板", "icon": "", "sort": 5, "hidden": True},
                        {"name": "signinConfig", "title": "签到设置", "icon": "setting", "sort": 6},
                        {"name": "inviteConfig", "title": "邀请设置", "icon": "setting", "sort": 7},
                        {"name": "commissionConfig", "title": "佣金设置", "icon": "percentage", "sort": 8},
                        {"name": "withdrawConfig", "title": "提现设置", "icon": "setting", "sort": 9},
                        {"name": "rechargeConfig", "title": "充值配置", "icon": "money", "sort": 10},
                        {"name": "customerService", "title": "客服配置", "icon": "service", "sort": 11}
                    ]
                },
                {
                    "name": "selfcheck",
                    "title": "自助收银",
                    "icon": "mobile",
                    "sort": 4,
                    "children": [
                        {"name": "appUpdate", "title": "应用更新管理", "icon": "upload", "sort": 1}
                    ]
                },
                {
                    "name": "decorate",
                    "title": "装修",
                    "icon": "edit",
                    "sort": 3,
                    "children": [
                        {"name": "decorateTheme", "title": "主题色搭配", "icon": "color", "sort": 1},
                        {"name": "decorateBackground", "title": "页面底图装修", "icon": "picture", "sort": 2}
                    ]
                }
            ]

            # 合并所有路由配置
            all_routes = ROUTER_CONFIG + additional_routes

            inserted_count = 0
            updated_count = 0

            for route in all_routes:
                # 处理父级菜单
                parent_name = route['name']
                if parent_name in existing_menus:
                    # 更新现有菜单
                    parent_id = existing_menus[parent_name]
                    self.update_menu(parent_id, {
                        'title': route['title'],
                        'sort': route['sort'],
                        'icon': route['icon'],
                        'hidden': 0
                    })
                    updated_count += 1
                    print(f"🔄 更新父级菜单: {route['title']}")
                else:
                    # 插入新菜单
                    parent_id = next_id
                    menu_data = {
                        'id': parent_id,
                        'parent_id': 0,
                        'create_time': datetime.now(),
                        'title': route['title'],
                        'level': 0,
                        'sort': route['sort'],
                        'name': route['name'],
                        'icon': route['icon'],
                        'hidden': 0
                    }
                    self.insert_menu(menu_data)
                    existing_menus[parent_name] = parent_id
                    next_id += 1
                    inserted_count += 1
                    print(f"➕ 新增父级菜单: {route['title']}")

                # 处理子级菜单
                for child in route.get('children', []):
                    child_name = child['name']
                    if child_name in existing_menus:
                        # 更新现有子菜单
                        child_id = existing_menus[child_name]
                        self.update_menu(child_id, {
                            'title': child['title'],
                            'sort': child['sort'],
                            'icon': child['icon'],
                            'hidden': 1 if child.get('hidden', False) else 0
                        })
                        updated_count += 1
                        print(f"  🔄 更新子菜单: {child['title']}")
                    else:
                        # 插入新子菜单
                        child_id = next_id
                        menu_data = {
                            'id': child_id,
                            'parent_id': parent_id,
                            'create_time': datetime.now(),
                            'title': child['title'],
                            'level': 1,
                            'sort': child['sort'],
                            'name': child['name'],
                            'icon': child['icon'],
                            'hidden': 1 if child.get('hidden', False) else 0
                        }
                        self.insert_menu(menu_data)
                        existing_menus[child_name] = child_id
                        next_id += 1
                        inserted_count += 1
                        print(f"  ➕ 新增子菜单: {child['title']}")

            # 提交事务
            self.connection.commit()
            print(f"\n✅ 菜单同步完成!")
            print(f"📊 统计信息:")
            print(f"   - 新增菜单: {inserted_count} 个")
            print(f"   - 更新菜单: {updated_count} 个")
            print(f"   - 总菜单数: {len(existing_menus)} 个")

        except Exception as e:
            if self.connection:
                self.connection.rollback()
            print(f"❌ 菜单同步失败: {e}")
            raise
        finally:
            self.close_db()

    def backup_menus(self):
        """备份现有菜单数据"""
        try:
            self.connect_db()
            sql = "SELECT * FROM ums_menu ORDER BY id"
            self.cursor.execute(sql)
            results = self.cursor.fetchall()

            backup_file = f"menu_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}.sql"
            with open(backup_file, 'w', encoding='utf-8') as f:
                f.write("-- 菜单数据备份\n")
                f.write(f"-- 备份时间: {datetime.now()}\n\n")

                for row in results:
                    f.write(f"INSERT INTO ums_menu VALUES {row};\n")

            print(f"✅ 菜单数据已备份到: {backup_file}")

        except Exception as e:
            print(f"❌ 备份失败: {e}")
        finally:
            self.close_db()

def main():
    """主函数"""
    print("🚀 菜单同步工具启动")
    print("=" * 50)

    service = MenuSyncService()

    # 询问是否备份
    backup = input("是否先备份现有菜单数据? (y/n): ").lower().strip()
    if backup == 'y':
        service.backup_menus()

    # 确认同步
    confirm = input("确认开始同步菜单? (y/n): ").lower().strip()
    if confirm == 'y':
        service.sync_menus()
    else:
        print("❌ 用户取消同步")

if __name__ == "__main__":
    main()
