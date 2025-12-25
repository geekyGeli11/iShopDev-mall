# Guanghengzhou Mall – DIY 页面装修 (IFrame 子应用方案)

> 版本：V0.1  2025-06-23  
> 作者：AI Assistant (o3)

---

## 1. 目标
1. 运营人员在后台可拖拽装修小程序首页（依赖 shopxo-diy 前端）。  
2. 复用 `shopxo-diy` 的 **UI、交互、DSL**，最小化二次开发。  
3. 数据源仍使用现有 mall-swarm 商品/优惠券等服务。  
4. 后台权限体系、网关、Token 策略保持一致[[memory:8991095934835290077]].

## 2. 技术方案选型
| 模块 | 技术 | 说明 |
| ---- | ---- | ---- |
| Admin Shell | Vue2 + Element-UI | 现有 `mall-admin-web` 不改动 |
| DIY 子应用 | **shopxo-diy** (Vue3 + Vite + Element-Plus) | 独立构建，IFrame 嵌入 |
| 运行方式 | IFrame（/diy/index.html） | 避免样式/依赖冲突；单点登录通过 JWT Query |
| 后端 | Spring Boot 3.2 (`mall-admin`) | 新增 `DecorateShopxoController` 适配接口 |
| DB | **shopxo 表结构**（适度裁剪） | 映射到现有库，新增或复用字段 |

## 3. 数据库设计（11 张全量表）
为与 shopxo-diy 完全兼容，重新设计 `diy_` 前缀 11 张表，并将建表 SQL 保存于 `mall-swarm/sql/add_diy_tables.sql`。

| # | 表名 | 核心字段 | 作用 |
|---|------|----------|------|
| 1 | diy_component_library | id,type,name,config_schema | 组件库定义与属性 Schema |
| 2 | diy_component_group | id,name,sort | 组件分组 |
| 3 | diy_component_group_rel | group_id,component_id | 组件与分组 N:M |
| 4 | diy_page | id,store_id,page_type,title,status,latest_version | 页面主表 |
| 5 | diy_page_version | id,page_id,version,dsl_json | 页面版本（JSON DSL）|
| 6 | diy_block_instance | id,page_id,version,block_key,component_type,sort | 组件实例（统计/排序）|
| 7 | diy_component_ref | id,page_id,version,block_key,ref_type,ref_id | 静态资源引用 |
| 8 | diy_publish_record | id,page_id,version,operator | 发布历史 |
| 9 | diy_ab_test | id,page_id,bucket_a,bucket_b,status | AB 测试配置 |
|10 | diy_ab_bucket | id,ab_id,user_id,bucket | AB 分桶命中 |
|11 | diy_op_log | id,page_id,operator,action,detail | 操作审计日志 |

> *旧 `dec_*` 表停止写入，后续提供迁移脚本。*

### 3.1 Mall 资源映射
| diy 字段 | 目标表 | 说明 |
|----------|--------|------|
| diy_component_ref.ref_type = 'product' `ref_id` | `pms_product.id` | 静态商品 |
| diy_component_ref.ref_type = 'coupon' `ref_id`  | `sms_coupon.id` | 静态优惠券 |
| diy_page.store_id | `ums_store.id` (可选) | 多门店 |

详细字段、索引、外键见 `add_diy_tables.sql`。

## 4. 系统架构
```mermaid
graph TD
Admin(Vue2) -->|IFrame| DIY(Vue3)
DIY -->|axios| mall-gateway
mall-gateway --> mall-admin(decorate-shopxo-api)
DIY -->|静态| /diy/assets/** (Nginx)
```

### 4.1 登录态 & 鉴权
1. Admin Vue2 在生成 IFrame `src` 时追加 `?token=<JWT>`。  
2. DIY 子应用 axios 请求头 `Authorization: Bearer <token>`。  
3. Gateway 按既有 SA-Token 规则鉴权[[memory:8991095934835290077]].

## 5. 接口列表
| 方法 | URL | 说明 |
| ---- | --- | ---- |
| GET | /diy/component/list | 组件库（透传 shopxo 默认 + 自定义） |
| GET | /diy/page/list | 页面列表（storeId） |
| GET | /diy/page/{id} | 页面详情（含 dsl_json） |
| POST | /diy/page | 新建页面 |
| PUT | /diy/page/{id} | 保存草稿 |
| POST | /diy/page/{id}/publish | 发布 |
| GET | /diy/runtime/{storeId}/home | C 端 Runtime 渲染 |

## 6. 开发计划
| 阶段 | 任务 | 负责人 | 时间 | 输出 |
| ---- | ---- | ---- | ---- | ---- |
| 0 | 需求确认、方案冻结 | AI+PM | 6-23 | 本文档 V1.0 |
| 1 | clone shopxo-diy → diy/ 构建脚手架 | FE | 0.5d | `diy/dist` 可独立访问 |
| 2 | Admin 菜单 & IFrame 嵌入 | FE | 0.5d | `/decorate/iframe` 路由 |
| 3 | 单点登录对接 | FE+BE | 0.5d | token 透传成功 |
| 4 | DB 表创建 / 数据迁移脚本 | DBA | 1d | `ddl_shopxo_diy.sql` |
| 5 | DecorateShopxoController + Service | BE | 1.5d | CRUD & 发布接口 |
| 5b | Mapper & Model 代码生成（MBG） | BE | 已完成 | 11 张表 PO/Mapper |
| 5c | 接口实现对接 shopxo-diy | BE | 1d | DiyDecorateController/DiyDecorateService |
| 6 | 商品/券批量接口复用 | BE | 0.5d | `/product/simple/list` 等 |
| ~~6a~~ | /diy/init & 上传接口 | BE | **已完成** | DiyDecorateController/DiyDecorateService |
| ~~6b~~ | shopxo-diy axios/env 对接 | FE | **已完成** | env & api 适配 |
| 6c | Runtime 整体验证 | QA | 0.5d | **进行中** |
| 7 | E2E 验证 & 文档 | QA | 0.5d | 流程文档、CHANGELOG |
| **合计** | **≈ 5 人天** |  |  |  |

## 7. 验收标准
1. 运营可在新界面拖拽、保存、发布首页。  
2. 保存后数据库 `app_home_page_version.dsl_json` 正确落库。  
3. Mall 前端（小程序）调用 `/diy/runtime/{storeId}/home` 渲染一致。  
4. 权限、Token、网关转发均与现有系统一致。  
5. 文档 & 脚本齐全，可在新环境一键部署。

## 8. 后续优化（非本期范围）
* 组件国际化、动态主题皮肤。  
* 与 BI 系统打通，支持 AB 实验与数据埋点。  
* 多端 SSR / Edge Render 提升首屏。

---

> 如对计划有任何疑问，请在 **2025-06-24 12:00** 前反馈；否则默认进入开发流程。