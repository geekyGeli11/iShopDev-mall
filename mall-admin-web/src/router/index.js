import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

/* Layout */
import Layout from "../views/layout/Layout";

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
 **/
export const constantRouterMap = [
  {
    path: "/login",
    component: () => import("@/views/login/index"),
    hidden: true
  },
  { path: "/404", component: () => import("@/views/404"), hidden: true },
  {
    path: "/school",
    component: Layout,
    redirect: "/school/manage",
    name: "school",
    meta: { title: "学校管理", icon: "education" },
    children: [
      {
        path: "manage",
        name: "schoolManage",
        component: () => import("@/views/oms/school/index"),
        meta: { title: "学校管理", icon: "education" }
      }
    ]
  }
];

export const asyncRouterMap = [
  {
    path: "",
    component: Layout,
    redirect: "/home",
    meta: { title: "仪表盘", icon: "home" },
    children: [
      {
        path: "home",
        name: "home",
        component: () => import("@/views/home/index"),
        meta: { title: "仪表盘", icon: "dashboard" }
      }
    ]
  },
  {
    path: "/pms",
    component: Layout,
    redirect: "/pms/product",
    name: "pms",
    meta: { title: "商品", icon: "product" },
    children: [
      {
        path: "product",
        name: "product",
        component: () => import("@/views/pms/product/index"),
        meta: { title: "商品列表", icon: "product-list" }
      },
      {
        path: "addProduct",
        name: "addProduct",
        component: () => import("@/views/pms/product/add"),
        meta: { title: "添加商品", icon: "product-add" }
      },
      {
        path: "updateProduct",
        name: "updateProduct",
        component: () => import("@/views/pms/product/update"),
        meta: { title: "修改商品", icon: "product-add" },
        hidden: true
      },
      {
        path: "productCate",
        name: "productCate",
        component: () => import("@/views/pms/productCate/index"),
        meta: { title: "商品分类", icon: "product-cate" }
      },
      {
        path: "addProductCate",
        name: "addProductCate",
        component: () => import("@/views/pms/productCate/add"),
        meta: { title: "添加商品分类" },
        hidden: true
      },
      {
        path: "updateProductCate",
        name: "updateProductCate",
        component: () => import("@/views/pms/productCate/update"),
        meta: { title: "修改商品分类" },
        hidden: true
      },
      {
        path: "productAttr",
        name: "productAttr",
        component: () => import("@/views/pms/productAttr/index"),
        meta: { title: "商品类型", icon: "product-attr" }
      },
      {
        path: "productAttrList",
        name: "productAttrList",
        component: () => import("@/views/pms/productAttr/productAttrList"),
        meta: { title: "商品属性列表" },
        hidden: true
      },
      {
        path: "addProductAttr",
        name: "addProductAttr",
        component: () => import("@/views/pms/productAttr/addProductAttr"),
        meta: { title: "添加商品属性" },
        hidden: true
      },
      {
        path: "updateProductAttr",
        name: "updateProductAttr",
        component: () => import("@/views/pms/productAttr/updateProductAttr"),
        meta: { title: "修改商品属性" },
        hidden: true
      },
      {
        path: "brand",
        name: "brand",
        component: () => import("@/views/pms/brand/index"),
        meta: { title: "品牌管理", icon: "product-brand" }
      },
      {
        path: "addBrand",
        name: "addBrand",
        component: () => import("@/views/pms/brand/add"),
        meta: { title: "添加品牌" },
        hidden: true
      },
      {
        path: "updateBrand",
        name: "updateBrand",
        component: () => import("@/views/pms/brand/update"),
        meta: { title: "编辑品牌" },
        hidden: true
      },
      {
        path: "stock",
        name: "stock",
        component: () => import("@/views/pms/stock/index"),
        meta: { title: "库存管理", icon: "stock" }
      },
      {
        path: "payback",
        name: "payback",
        component: () => import("@/views/pms/payback/index"),
        meta: { title: "回本分析", icon: "chart" }
      },
      {
        path: "damage",
        name: "damage",
        component: () => import("@/views/pms/damage/index"),
        meta: { title: "商品报损", icon: "order-return" }
      },
      {
        path: "damageDetail",
        name: "damageDetail",
        component: () => import("@/views/pms/damage/detail"),
        meta: { title: "报损详情" },
        hidden: true
      },
      {
        path: "damageReason",
        name: "damageReason",
        component: () => import("@/views/pms/damage/reason"),
        meta: { title: "报损原因设置", icon: "order-return-reason" }
      }
    ]
  },
  {
    path: "/diy",
    component: Layout,
    redirect: "/diy/materialCategory",
    name: "diy",
    meta: { title: "DIY管理", icon: "edit" },
    children: [
      {
        path: "materialCategory",
        name: "diyMaterialCategory",
        component: () => import("@/views/pms/diy/materialCategory/index"),
        meta: { title: "素材分类管理DIY", icon: "folder" }
      },
      {
        path: "addMaterialCategory",
        name: "addDiyMaterialCategory",
        component: () => import("@/views/pms/diy/materialCategory/add"),
        meta: { title: "添加DIY素材分类" },
        hidden: true
      },
      {
        path: "updateMaterialCategory",
        name: "updateDiyMaterialCategory",
        component: () => import("@/views/pms/diy/materialCategory/update"),
        meta: { title: "编辑DIY素材分类" },
        hidden: true
      },
      {
        path: "material",
        name: "diyMaterial",
        component: () => import("@/views/pms/diy/material/index"),
        meta: { title: "DIY素材管理", icon: "picture" }
      },
      {
        path: "styleModel",
        name: "styleModel",
        component: () => import("@/views/pms/styleModel/index"),
        meta: { title: "风格模型管理", icon: "style" }
      },
      {
        path: "addMaterial",
        name: "addDiyMaterial",
        component: () => import("@/views/pms/diy/material/add"),
        meta: { title: "添加DIY素材" },
        hidden: true
      },
      {
        path: "updateMaterial",
        name: "updateDiyMaterial",
        component: () => import("@/views/pms/diy/material/update"),
        meta: { title: "编辑DIY素材" },
        hidden: true
      },
      {
        path: "styleModel/add",
        name: "addStyleModel",
        component: () => import("@/views/pms/styleModel/add"),
        meta: { title: "添加风格模型" },
        hidden: true
      },
      {
        path: "styleModel/update",
        name: "updateStyleModel",
        component: () => import("@/views/pms/styleModel/update"),
        meta: { title: "编辑风格模型" },
        hidden: true
      },
      {
        path: "styleModel/products",
        name: "styleModelProducts",
        component: () => import("@/views/pms/styleModel/products"),
        meta: { title: "风格模型商品管理" },
        hidden: true
      },
      {
        path: "template",
        name: "diyTemplate",
        component: () => import("@/views/pms/diy/template/index"),
        meta: { title: "DIY模板管理", icon: "template" }
      },
      {
        path: "addTemplate",
        name: "addDiyTemplate",
        component: () => import("@/views/pms/diy/template/add"),
        meta: { title: "添加DIY模板" },
        hidden: true
      },
      {
        path: "updateTemplate",
        name: "updateDiyTemplate",
        component: () => import("@/views/pms/diy/template/update"),
        meta: { title: "编辑DIY模板" },
        hidden: true
      },
      {
        path: "templateDetail",
        name: "diyTemplateDetail",
        component: () => import("@/views/pms/diy/template/detail"),
        meta: { title: "DIY模板详情" },
        hidden: true
      },
      {
        path: "templatePreview/:id",
        name: "diyTemplatePreview",
        component: () => import("@/views/pms/diy/template/preview"),
        meta: { title: "DIY模板预览" },
        hidden: true
      }
    ]
  },
  {
    path: "/oms",
    component: Layout,
    redirect: "/oms/order",
    name: "oms",
    meta: { title: "订单", icon: "order" },
    children: [
      {
        path: "order",
        name: "order",
        component: () => import("@/views/oms/order/index"),
        meta: { title: "订单列表", icon: "product-list" }
      },
      {
        path: "orderDetail",
        name: "orderDetail",
        component: () => import("@/views/oms/order/orderDetail"),
        meta: { title: "订单详情" },
        hidden: true
      },
      {
        path: "deliverOrderList",
        name: "deliverOrderList",
        component: () => import("@/views/oms/order/deliverOrderList"),
        meta: { title: "发货列表" },
        hidden: true
      },
      {
        path: "diyOrder",
        name: "diyOrder",
        component: () => import("@/views/oms/diy/index"),
        meta: { title: "DIY订单列表", icon: "edit" }
      },
      {
        path: "diyOrderDetail",
        name: "diyOrderDetail",
        component: () => import("@/views/oms/diy/detail"),
        meta: { title: "DIY订单详情" },
        hidden: true
      },
      {
        path: "returnApply",
        name: "returnApply",
        component: () => import("@/views/oms/apply/index"),
        meta: { title: "退货申请处理", icon: "order-return" }
      },
      {
        path: "returnApplyDetail",
        name: "returnApplyDetail",
        component: () => import("@/views/oms/apply/applyDetail"),
        meta: { title: "退货原因详情" },
        hidden: true
      },
      {
        path: "nonSystemSale",
        name: "nonSystemSale",
        component: () => import("@/views/pms/nonSystemSale/page"),
        meta: { title: "其他销售录入", icon: "money" }
      }

    ]
  },
  {
    path: "/member",
    component: Layout,
    redirect: "/member/list",
    name: "member",
    meta: { title: "会员", icon: "user" },
    children: [
      {
        path: "list",
        name: "memberList",
        component: () => import("@/views/ums/member/index"),
        meta: { title: "会员列表", icon: "user" }
      },
      {
        path: "guest",
        name: "guestList",
        component: () => import("@/views/ums/guest/index"),
        meta: { title: "游客列表", icon: "user-solid" }
      },
      {
        path: "detail/:id",
        name: "memberDetail",
        component: () => import("@/views/ums/member/detail"),
        meta: { title: "用户详情" },
        hidden: true
      },
      {
        path: "recharge",
        name: "memberRecharge",
        component: () => import("@/views/oms/rechargeOrder/index"),
        meta: { title: "充值记录", icon: "money" }
      },
      {
        path: "signin",
        name: "memberSignin",
        component: () => import("@/views/sms/signin/logs"),
        meta: { title: "签到记录", icon: "list" }
      },
      {
        path: "level",
        name: "memberLevel",
        component: () => import("@/views/ums/memberLevel/index"),
        meta: { title: "会员等级管理", icon: "star" }
      },
      {
        path: "pointsExchange",
        name: "memberPointsExchange",
        component: () => import("@/views/sms/points/exchangeConfig"),
        meta: { title: "积分换购配置", icon: "money" }
      }
    ]
  },
  {
    path: "/promotion",
    component: Layout,
    redirect: "/promotion/statistics",
    name: "promotion",
    meta: { title: "推广", icon: "user-plus" },
    children: [
      {
        path: "statistics",
        name: "promotionStatistics",
        component: () => import("@/views/pms/invite/statistics"),
        meta: { title: "数据统计", icon: "chart" }
      },
      {
        path: "relations",
        name: "promotionRelations",
        component: () => import("@/views/pms/invite/relations"),
        meta: { title: "邀请记录", icon: "list" }
      },
      {
        path: "rewards",
        name: "promotionRewards",
        component: () => import("@/views/ums/invite/rewards"),
        meta: { title: "奖励记录", icon: "money" }
      },
      {
        path: "withdraw",
        name: "promotionWithdraw",
        component: () => import("@/views/ums/invite/withdraw"),
        meta: { title: "提现管理", icon: "credit-card" }
      },
      {
        path: "ambassadorApply",
        name: "ambassadorApply",
        component: () => import("@/views/ums/distribution/apply/index"),
        meta: { title: "推广大使申请", icon: "user-check" }
      },
      {
        path: "ambassadorManage",
        name: "ambassadorManage",
        component: () => import("@/views/ums/distribution/distributor/index"),
        meta: { title: "推广大使管理", icon: "users" }
      }
    ]
  },
  {
    path: "/sms",
    component: Layout,
    redirect: "/sms/coupon",
    name: "sms",
    meta: { title: "营销", icon: "sms" },
    children: [
      {
        path: "productBundle",
        name: "productBundle",
        component: () => import("@/views/pms/productBundle/index"),
        meta: { title: "组合销售", icon: "product-list" }
      },
      {
        path: "addProductBundle",
        name: "addProductBundle",
        component: () => import("@/views/pms/productBundle/add"),
        meta: { title: "添加组合销售" },
        hidden: true
      },
      {
        path: "updateProductBundle",
        name: "updateProductBundle",
        component: () => import("@/views/pms/productBundle/update"),
        meta: { title: "编辑组合销售" },
        hidden: true
      },
      {
        path: "flash",
        name: "flash",
        component: () => import("@/views/sms/flash/index"),
        meta: { title: "限时优惠", icon: "sms-flash" }
      },
      {
        path: "flashSession",
        name: "flashSession",
        component: () => import("@/views/sms/flash/sessionList"),
        meta: { title: "限时优惠时间段列表" },
        hidden: true
      },
      {
        path: "selectSession",
        name: "selectSession",
        component: () => import("@/views/sms/flash/selectSessionList"),
        meta: { title: "限时优惠时间段选择" },
        hidden: true
      },
      {
        path: "flashProductRelation",
        name: "flashProductRelation",
        component: () => import("@/views/sms/flash/productRelationList"),
        meta: { title: "限时优惠商品列表" },
        hidden: true
      },
      {
        path: "coupon",
        name: "coupon",
        component: () => import("@/views/sms/coupon/index"),
        meta: { title: "优惠券", icon: "sms-coupon" }
      },
      {
        path: "addCoupon",
        name: "addCoupon",
        component: () => import("@/views/sms/coupon/add"),
        meta: { title: "添加优惠券" },
        hidden: true
      },
      {
        path: "updateCoupon",
        name: "updateCoupon",
        component: () => import("@/views/sms/coupon/update"),
        meta: { title: "修改优惠券" },
        hidden: true
      },
      {
        path: "couponHistory",
        name: "couponHistory",
        component: () => import("@/views/sms/coupon/history"),
        meta: { title: "优惠券领取详情" },
        hidden: true
      },
      {
        path: "integrationPromotion",
        name: "integrationPromotion",
        component: () => import("@/views/sms/integrationPromotion/index"),
        meta: { title: "积分营销活动", icon: "sms-coupon" }
      },
      {
        path: "brand",
        name: "homeBrand",
        component: () => import("@/views/sms/brand/index"),
        meta: { title: "品牌推荐", icon: "product-brand" }
      },
      {
        path: "new",
        name: "homeNew",
        component: () => import("@/views/sms/new/index"),
        meta: { title: "热榜推荐", icon: "sms-new" }
      },
      {
        path: "hot",
        name: "homeHot",
        component: () => import("@/views/sms/hot/index"),
        meta: { title: "爆品榜单", icon: "sms-hot" }
      },
      {
        path: "subject",
        name: "homeSubject",
        component: () => import("@/views/sms/subject/index"),
        meta: { title: "专题推荐", icon: "sms-subject" }
      },
      {
        path: "advertise",
        name: "homeAdvertise",
        component: () => import("@/views/sms/advertise/index"),
        meta: { title: "banner列表", icon: "sms-ad" }
      },
      {
        path: "addAdvertise",
        name: "addHomeAdvertise",
        component: () => import("@/views/sms/advertise/add"),
        meta: { title: "添加广告" },
        hidden: true
      },
      {
        path: "updateAdvertise",
        name: "updateHomeAdvertise",
        component: () => import("@/views/sms/advertise/update"),
        meta: { title: "编辑广告" },
        hidden: true
      },
      {
        path: "notification",
        name: "notification",
        component: () => import("@/views/sms/notification/index"),
        meta: { title: "通知弹窗", icon: "sms-ad" }
      },
      {
        path: "addNotification",
        name: "addNotification",
        component: () => import("@/views/sms/notification/add"),
        meta: { title: "添加通知" },
        hidden: true
      },
      {
        path: "updateNotification",
        name: "updateNotification",
        component: () => import("@/views/sms/notification/update"),
        meta: { title: "编辑通知" },
        hidden: true
      },
      {
        path: "notificationReadStats",
        name: "notificationReadStats",
        component: () => import("@/views/sms/notification/readStats"),
        meta: { title: "通知阅读统计" },
        hidden: true
      }
    ]
  },

  {
    path: "/ums",
    component: Layout,
    redirect: "/ums/admin",
    name: "ums",
    meta: { title: "权限", icon: "ums" },
    children: [
      {
        path: "admin",
        name: "admin",
        component: () => import("@/views/ums/admin/index"),
        meta: { title: "员工列表", icon: "ums-admin" }
      },
      {
        path: "role",
        name: "role",
        component: () => import("@/views/ums/role/index"),
        meta: { title: "角色列表", icon: "ums-role" }
      },
      {
        path: "allocMenu",
        name: "allocMenu",
        component: () => import("@/views/ums/role/allocMenu"),
        meta: { title: "分配菜单" },
        hidden: true
      },
      {
        path: "allocResource",
        name: "allocResource",
        component: () => import("@/views/ums/role/allocResource"),
        meta: { title: "分配资源" },
        hidden: true
      },
      {
        path: "menu",
        name: "menu",
        component: () => import("@/views/ums/menu/index"),
        meta: { title: "菜单列表", icon: "ums-menu" }
      },
      {
        path: "addMenu",
        name: "addMenu",
        component: () => import("@/views/ums/menu/add"),
        meta: { title: "添加菜单" },
        hidden: true
      },
      {
        path: "updateMenu",
        name: "updateMenu",
        component: () => import("@/views/ums/menu/update"),
        meta: { title: "修改菜单" },
        hidden: true
      },
      {
        path: "resource",
        name: "resource",
        component: () => import("@/views/ums/resource/index"),
        meta: { title: "资源列表", icon: "ums-resource" }
      },
      {
        path: "resourceCategory",
        name: "resourceCategory",
        component: () => import("@/views/ums/resource/categoryList"),
        meta: { title: "资源分类" },
        hidden: true
      }
    ]
  },
  {
    path: "/settings",
    component: Layout,
    redirect: "/settings/store",
    name: "settings",
    meta: { title: "设置", icon: "setting" },
    children: [
      {
        path: "store",
        name: "storeAddress",
        component: () => import("@/views/oms/storeAddress/index"),
        meta: { title: "门店地址设置", icon: "location" }
      },
      {
        path: "returnReason",
        name: "returnReason",
        component: () => import("@/views/oms/apply/reason"),
        meta: { title: "售后原因设置", icon: "order-return-reason" }
      },
      {
        path: "freight",
        name: "freight",
        component: () => import("@/views/oms/freight/index"),
        meta: { title: "运费模板设置", icon: "location" }
      },
      {
        path: "freight/add",
        name: "addFreight",
        component: () => import("@/views/oms/freight/add"),
        meta: { title: "添加运费模板" },
        hidden: true
      },
      {
        path: "freight/update/:id",
        name: "updateFreight",
        component: () => import("@/views/oms/freight/update"),
        meta: { title: "编辑运费模板" },
        hidden: true
      },
      {
        path: "signin",
        name: "signinConfig",
        component: () => import("@/views/sms/signin/config"),
        meta: { title: "签到设置", icon: "setting" }
      },
      {
        path: "invite",
        name: "inviteConfig",
        component: () => import("@/views/pms/invite/config"),
        meta: { title: "邀请设置", icon: "setting" }
      },
      {
        path: "commission",
        name: "commissionConfig",
        component: () => import("@/views/ums/distribution/commission/index"),
        meta: { title: "佣金设置", icon: "percentage" }
      },
      {
        path: "withdraw",
        name: "withdrawConfig",
        component: () => import("@/views/ums/invite/withdrawConfig/index"),
        meta: { title: "提现设置", icon: "setting" }
      },
      {
        path: "rechargeConfig",
        name: "rechargeConfig",
        component: () => import("@/views/settings/rechargeConfig/index"),
        meta: { title: "充值配置", icon: "money" }
      },
      {
        path: "customerService",
        name: "customerService",
        component: () => import("@/views/settings/customerService/index"),
        meta: { title: "客服配置", icon: "service" }
      }
    ]
  },
  {
    path: "/selfcheck",
    component: Layout,
    redirect: "/selfcheck/appUpdate",
    name: "selfcheck",
    meta: { title: "自助收银", icon: "mobile" },
    children: [
      {
        path: "appUpdate",
        name: "appUpdate",
        component: () => import("@/views/selfcheck/appUpdate/index"),
        meta: { title: "应用更新管理", icon: "upload" }
      }
    ]
  },
  {
    path: "/decorate",
    component: Layout,
    redirect: "/decorate/page",
    name: "decorate",
    meta: { title: "装修", icon: "edit" },
    children: [
      {
        path: "theme",
        name: "decorateTheme",
        component: () => import("@/views/decorate/theme/index"),
        meta: { title: "主题色搭配", icon: "color" }
      },
      {
        path: "background",
        name: "decorateBackground",
        component: () => import("@/views/decorate/background/index"),
        meta: { title: "页面底图装修", icon: "picture" }
      }
    ]
  },
  { path: "*", redirect: "/404", hidden: true }
];

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
});
