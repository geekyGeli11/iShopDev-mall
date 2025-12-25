# Guanghengzhou Mall – 小程序首页 DIY 装修功能设计

> 版本：V1.0 (2025-05-XX)
>
> 作者：AI Assistant (o3)

---

## 1. 目标与范围

1. **运营人员** 无需开发即可拖拽组件，装修不同门店的小程序首页。
2. 支持 **组件库扩展**、**所见即所得预览**、**版本管理**、**一键发布**。
3. C 端小程序（uni-app）按用户所选门店实时渲染对应首页。

本期仅覆盖 **首页**；后续可扩展至营销/活动页、专题页等。

---

## 2. 数据模型

| 表 | 说明 |
| --- | --- |
| `dec_component_library` | 装修组件库定义 |
| `dec_page` | 页面主表（按门店划分） |
| `dec_page_version` | 页面版本历史 |
| `dec_publish_record` | 发布记录 |
| `dec_page_component_ref` | 页面组件静态引用（商品/优惠券/视频等） |

> **字段更新**（V1.1）
>
> * `dec_component_library` 增加 `data_fetch_mode TINYINT` – 0 纯静态 / 1 静态+动态 / 2 跳转页面
> * `dec_page` 增加 `route_path VARCHAR(128)` – 小程序端路由路径

> 详见 `mall-swarm/sql/add_decorate_tables.sql`。

### 内容 DSL 示例

```jsonc
{
  "global": { "backgroundColor": "#fff" },
  "blocks": [
    {
      "type": "banner",
      "props": {
        "images": ["https://.../a.png"]
      }
    },
    {
      "type": "product-grid",
      "props": {
        "title": "热销推荐",
        "dataSource": {
          "mode": "static",
          "ids": [1, 2, 3]
        }
      }
    }
  ]
}
```

---

## 3. 服务与接口

```
mall-admin-web (Vue2)   ←→   mall-gateway   ←→   decorate-service (Spring Boot 3.2)
                                          └→  商品、优惠券、OSS…
```

### Admin 接口

| 方法 | URL | 功能 |
| --- | --- | --- |
| GET | /decorate/component/list | 组件库 |
| GET | /decorate/page/list | 页面列表 (storeId) |
| POST | /decorate/page | 新建页面 |
| GET | /decorate/page/{id}/detail | 草稿详情 |
| PUT | /decorate/page/{id} | 保存草稿 |
| POST | /decorate/page/{id}/publish | 发布 |

### Runtime 接口（小程序）

| URL | 功能 |
| --- | --- |
| GET /decorate/runtime/{storeId}/home | 拉取已发布首页 DSL |
| GET /decorate/runtime/{storeId}/product/query | 商品查询 (dynamic) |
| GET /decorate/runtime/{storeId}/coupon/query | 优惠券查询 (dynamic) |

接口统一遵循 `{"code":<int>, "message":"<msg>", "data":<obj>}`[[memory:6752684927342041734]].

---

## 4. 前端实现

### 4.1 mall-admin-web

| 区域 | 主要库 | 描述 |
| ---- | ---- | ---- |
| 组件面板 | `vuedraggable` | 拖拽组件入画布 |
| 画布 | `vue-draggable-resizable` | 拖拽排序、实时渲染 |
| 属性面板 | `@lljj/vue-json-schema-form` | 根据 `config_schema` 生成表单 |
| 预览 | `<iframe>` | 调 runtime 接口，加载草稿版本 |

路由：`/decorate/editor?storeId=xxx&pageId=yyy`。

### 4.2 小程序 (uni-app)

* 新增 `runtime.vue`，动态解析 DSL。
* 公共 Runtime 组件放置于 `src/runtime-components/`。
* 数据源：
  * `static` → `/product/detail/batch`、`/coupon/detail/batch`
  * `dynamic` → 运行时查询接口。
* 缓存时间：5 分钟。

---

## 5. 权限 & 安全

1. 角色 `ROLE_OPERATOR` 可编辑，`ROLE_ADMIN` 可发布。
2. 图片上传走 OSS 组件，自动压缩、生成 HTTPS URL。
3. 发布版本只读；需复制为草稿后再次编辑。

---

## 6. 开发计划 (甘特概览)

| 阶段 | 负责人 | 起止 | 输出 |
| ---- | ---- | ---- | ---- |
| 0. DB & Entity | AI → Developer | Day 1 | SQL + PO + Mapper |
| 1. 后端 API | Dev | D1-D3 | Controller + Service + Test |
| 2. Admin 画布基础 | Dev | D3-D6 | 拖拽+保存 |
| 3. 属性面板 | Dev | D5-D7 | 动态表单 |
| 4. 预览/发布 | Dev | D7-D8 | runtime iframe |
| 5. 小程序 Runtime | Dev | D7-D9 | 渲染逻辑 |
| 6. 自测 & 文档 | QA | D9-D10 | 单元/E2E + CHANGELOG |

> **里程碑**：
> * **M1 (Day 3)** – 后端接口可用，SQL 通过。
> * **M2 (Day 6)** – Admin 端可拖拽保存草稿。
> * **M3 (Day 9)** – 发布成功，小程序端正常渲染。

---

## 7. 后续展望

* 页面 AB 实验、流量分流。
* 组件市场：运营可上传自定义组件包。
* 多语言 & 主题皮肤。
* SSR + Edge Render 降首屏。

---

## 变更日志

| 日期 | 版本 | 说明 |
| ---- | ---- | ---- |
| 2025-05-XX | V1.1 | • 合并新增字段至 `add_decorate_tables.sql`；新增 `dec_page_component_ref` 表<br/>• 启动后端 API & Admin 端基础页面开发（进行中）<br/>• 已实现组件/页面列表接口与页面管理列表 UI（draft） |
| 2025-05-XX | V1.2 | • 完成发布逻辑：写入 `dec_publish_record` 并更新发布版本<br/>• Admin 装修编辑器 MVP：拖拽组件→画布→属性面板（vuedraggable + VueJsonSchemaForm）<br/>• 页面列表新增保存/预览 |
| 2025-05-XX | V1.3 | • 组件属性面板可编辑：后端 `config_schema` 查询改为 *WithBLOBs*，前端动态渲染表单<br/>• 组件库初始化脚本修正，入库 JSON-Schema<br/>• 支持复制/删除块、调试日志输出 |
| 2025-05-XX | V1.4 | • 后端组件库 CRUD 完成；页面草稿持久化 & 预览接口 `/preview` <br/>• 前端新增组件库管理页（上传示例图、JSON-Schema 编辑）<br/>• 编辑器支持草稿回显、预览按钮展示 DSL |
| 2025-06-23 | V1.5 | • 引入 shopxo-diy 子应用（Vue3+Vite）以 IFrame 嵌入后台<br/>• 重新设计 11 张 `diy_` 表并生成 MBG Mapper<br/>• 新增后端 `/diy` 系列接口：init、component/list、page CRUD、publish、upload、runtime<br/>• Admin 菜单路由 `/decorate/iframe` + Loading 效果完成对接<br/>• 更新计划文档 `decorate_iframe_plan.md`，当前 6a 完成，进入 6b 阶段 |

若文档内容需调整，请指出，后续我将继续迭代。

---

## 下一步（V1.5 规划）


### 5. Admin 编辑器（editor.vue）交互增强

| 区域 | 目标 | 关键点 |
| ---- | ---- | ---- |
| 左侧组件库 | *直观可复用* | 1. 列表项显示 `icon + name`；<br>2. 组件可被拖拽多次，使用 `pull:'clone', put:false` 的 clone 机制；<br>3. 支持搜索过滤（按名称/type）。 |
| 中间画布 | *符合手机比例* | 1. 外层容器保持 430×932 (iPhone 14) 纵向比例；<br>   根据浏览器宽度 `scale()` 自动缩放；<br>2. 背景网格/虚线边框提示安全区域；<br>3. 组件拖入后渲染实时预览。 |
| 右侧属性栏 | *可配置 & 预览数据* | 1. 根据组件 `configSchema` 生成基础表单；<br>2. 当字段包含 `dataSource.mode='static'` 时出现「选择资源」按钮：<br>   • 商品 → 弹出商品选择对话框（复用商品列表接口，支持多选）；<br>   • 优惠券 → 弹出优惠券选择对话框；<br>   • 页面/视频等类似；<br>3. 选择完成后写入 `props.dataSource.ids` 并触发 **实时预览**：<br>   • 商品：请求 `/pms/product/simple/list?ids=...`，渲染缩略图 + 标题；<br>   • 优惠券：渲染优惠券名称、金额标签；<br>   • 视频：显示封面图；<br>   • 页面跳转：显示目标页面标题。 |
| 预览功能 | *所见即所得* | 1. 编辑器内提供「手机预览」按钮（快捷键 F）→ 全屏模式展示当前 DSL；<br>2. 或继续使用 `/preview` 页面，新窗口加载 Runtime 渲染结果。 |


### 1. Runtime 服务对接
* mall-portal/selfcheck 实现 `/decorate/runtime/{storeId}/home`，根据已发布版本返回解析后的渲染数据。
* 静态数据批量查询（商品、优惠券），动态模式实时查询。

### 2. 小程序端渲染
* mall-app-web / selfcheck renderer 新增 `runtime.vue`，根据 DSL 生成实际页面组件。
* 公共 Runtime 组件库放置于 `src/runtime-components`，与后台组件库 type 对齐。

### 3. 版本管理
* 页面版本列表接口 `/decorate/page/{id}/version/list`。
* 支持一键回滚、复制为草稿。

### 4. 体验优化
* 画布组件占位图预览。
* 属性校验提示、Schema 导入导出。
> 完成以上 4 点后，运营在后台即可：
> • 多次复用同一组件
> • 在接近真机大小的画布内拖拽排版
> • 便捷配置并实时看到商品/券/视频内容
> • 点击预览验证最终效果

---

> V1.4 完成后：运营可在后台自定义组件 → 拖拽装修 → 实时编辑属性 → 保存 → 预览。下一阶段（V1.5）再对接小程序 Runtime 渲染、版本回滚等功能。

---

#### 5.1 右侧属性栏资源选择 – 技术拆解

1. 公共选择弹窗 `SelectionDialog`
   * Props：`type (product|coupon|page|video)`、`multiple`、`value`
   * 内部根据 `type` 调用对应简易列表接口：
     | 资源 | 接口 |
     | ---- | ---- |
     | 商品 | `GET /pms/product/simple/list?keyword=&ids=&pageSize=` |
     | 优惠券 | `GET /coupon/simple/list?keyword=&limit=` |
     | 页面 | `GET /decorate/page/simple/list?storeId=&keyword=` |
     | 视频 | 直接上传或 `GET /media/video/simple/list` |
   * 支持搜索、分页、多选，`@confirm` 返回选中数组。  

2. editor.vue 逻辑
   ```js
   // 判断是否需要资源按钮
   showChooseResource() {
     const blk = this.currentBlock;
     if (!blk) return false;
     // rule: 组件含 dataSource 且 mode===static
     return blk.props?.dataSource?.mode==='static';
   }
   chooseResource() {
     this.$dialog({type: map[blk.type] }) // 打开 SelectionDialog
       .then(list=>{ blk.props.dataSource.ids=list.map(i=>i.id) })
   }
   ```
   实时预览：watch `blk.props.dataSource.ids` → 调 *simple/list* 接口把图片/标题写入 `blk.runtimePreview`，画布使用。  

3. 轮播图特殊处理
   * `images` 为 array，可点击 + 号上传，走 COS/MinIO；
   * 每张图可配置 `linkType/linkTarget`，点击弹 `SelectionDialog(type='product'| 'page')`。  

4. 商品列表自定义图
   * 在预览缩略图右上角放置「替换图片」图钉，上传后写入 `blk.props.override[id]=url`。  

--- 