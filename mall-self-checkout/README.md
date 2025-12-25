# mall-self-checkout

## 项目简介

`mall-self-checkout` 是一套跨平台自助收银系统，采用了 Vue3、TypeScript、Electron、Capacitor 等核心技术，支持 Windows 桌面端和 Android 移动端。系统提供完整的自助收银解决方案，包括会员登录、商品扫码、购物车管理、支付结算、小票打印等功能。

## 项目演示

### 自助收银界面

系统支持会员和非会员两种结算模式，提供现代化的用户界面设计。

### 主要功能

- 会员/非会员结算模式
- 多种登录方式（手机号验证码、会员码扫描）
- 商品条码扫描
- 购物车管理
- 多种支付方式
- 小票打印
- 会员权益管理
- TTS 语音播报

## 项目架构

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端应用层                              │
│              Vue 3 + TypeScript + Vite                      │
├─────────────────────────────────────────────────────────────┤
│                      平台适配层                              │
│    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│    │  Electron   │  │  Capacitor  │  │ Web Browser │       │
│    │  (Windows)  │  │  (Android)  │  │   (H5)      │       │
│    └─────────────┘  └─────────────┘  └─────────────┘       │
├─────────────────────────────────────────────────────────────┤
│                      后端服务层                              │
│              mall-swarm 微服务系统                          │
└─────────────────────────────────────────────────────────────┘
```

### 组织结构

```lua
mall-self-checkout
├── src/                          -- 源代码目录
│   ├── config/                   -- 环境配置
│   ├── main/                     -- Electron 主进程
│   │   ├── ipc/                  -- IPC 通信
│   │   ├── services/             -- 主进程服务
│   │   └── utils/                -- 主进程工具
│   ├── plugins/                  -- 插件目录
│   ├── renderer/                 -- 渲染进程（Vue应用）
│   │   ├── api/                  -- API 接口
│   │   ├── assets/               -- 静态资源
│   │   ├── components/           -- 组件
│   │   ├── composables/          -- 组合式函数
│   │   ├── router/               -- 路由配置
│   │   ├── store/                -- 状态管理
│   │   ├── utils/                -- 工具函数
│   │   └── views/                -- 页面视图
│   └── shared/                   -- 共享代码
├── android/                      -- Android 原生项目
├── electron/                     -- Electron 配置
├── docs/                         -- 项目文档
├── scripts/                      -- 构建脚本
└── public/                       -- 公共资源
```

### 页面结构

```lua
src/renderer/views/
├── HomePage.vue                  -- 应用主页
├── login/
│   └── LoginPage.vue             -- 登录页面
├── scan/
│   └── ScanPage.vue              -- 扫码购物页面
├── cart/
│   └── CartPage.vue              -- 购物车页面
├── payment/
│   ├── PaymentPage.vue           -- 支付页面
│   └── PaymentResultPage.vue     -- 支付结果页面
├── member/
│   └── MemberPage.vue            -- 会员中心
├── receipt/
│   └── ReceiptPage.vue           -- 小票页面
├── register/
│   └── RegisterPage.vue          -- 注册页面
├── settings/
│   └── SettingsPage.vue          -- 设置页面
└── test/
    ├── TTSTestPage.vue           -- TTS测试页面
    └── AudioTestPage.vue         -- 音频测试页面
```

## 技术选型

### 前端技术

| 技术 | 说明 | 官网 |
| --- | --- | --- |
| Vue 3 | 前端框架 | https://vuejs.org/ |
| TypeScript | JavaScript 超集 | https://www.typescriptlang.org/ |
| Vite | 构建工具 | https://vitejs.dev/ |
| Vue Router 4 | 路由框架 | https://router.vuejs.org/ |
| Pinia | 状态管理 | https://pinia.vuejs.org/ |
| Vant 4 | 移动端 UI 框架 | https://vant-ui.github.io/vant/ |
| Axios | HTTP 请求库 | https://axios-http.com/ |
| Day.js | 日期处理库 | https://day.js.org/ |
| @zxing/library | 条码扫描库 | https://github.com/nicolo-ribaudo/nicolo-ribaudo |
| simple-keyboard | 虚拟键盘 | https://virtual-keyboard.js.org/ |

### 跨平台技术

| 技术 | 说明 | 官网 |
| --- | --- | --- |
| Electron | 桌面端框架 | https://www.electronjs.org/ |
| Electron Builder | 桌面端打包工具 | https://www.electron.build/ |
| Electron Vite | Electron + Vite 集成 | https://electron-vite.org/ |
| Capacitor | 移动端框架 | https://capacitorjs.com/ |
| @capacitor/camera | 相机插件 | https://capacitorjs.com/docs/apis/camera |
| @capacitor-community/text-to-speech | TTS 语音插件 | https://github.com/nicolo-ribaudo/nicolo-ribaudo |

### 开发工具

| 技术 | 说明 | 官网 |
| --- | --- | --- |
| ESLint | 代码检查工具 | https://eslint.org/ |
| Prettier | 代码格式化工具 | https://prettier.io/ |
| Vitest | 单元测试框架 | https://vitest.dev/ |
| Sass | CSS 预处理器 | https://sass-lang.com/ |

## 环境搭建

### 开发环境

| 工具 | 版本号 | 下载 |
| --- | --- | --- |
| Node.js | 16.0+ | https://nodejs.org/ |
| npm | 8.0+ | https://www.npmjs.com/ |
| JDK | 17 | https://www.oracle.com/java/technologies/downloads/ |
| Android Studio | Latest | https://developer.android.com/studio |
| Android SDK | 33+ | https://developer.android.com/studio |

### 搭建步骤

#### 1. 克隆项目

```bash
git clone <项目地址>
cd mall-self-checkout
```

#### 2. 安装依赖

```bash
npm install
```

#### 3. 启动开发服务器

```bash
# Web 开发模式
npm run dev

# Electron 开发模式
npm run dev:electron
```

#### 4. 构建应用

```bash
# 构建 Windows 桌面端
npm run build:win

# 构建 Android 移动端
npm run build:android
```

## 运行命令

### 开发命令

```bash
# 启动 Web 开发服务器
npm run dev

# 启动 Staging 环境
npm run dev:staging

# 启动 Electron 开发模式
npm run dev:electron

# 代码检查
npm run lint

# 代码格式化
npm run format

# 类型检查
npm run type-check

# 单元测试
npm run test:unit
```

### 构建命令

```bash
# 构建渲染进程（生产环境）
npm run build:renderer

# 构建 Windows 应用
npm run build:win

# 构建 Windows 应用（Staging 环境）
npm run build:win:staging

# 构建 Android 应用
npm run build:android

# 同步 Capacitor
npm run cap:sync

# 打开 Android Studio
npm run cap:open
```

## 环境配置

项目支持多环境配置：

| 文件 | 说明 |
| --- | --- |
| .env | 默认环境变量 |
| .env.development | 开发环境 |
| .env.staging | 测试环境 |
| .env.production | 生产环境 |

## 使用流程

### 非会员购物流程

1. 点击「非会员结算」按钮
2. 直接进入扫码购物页面
3. 扫描商品条码添加到购物车
4. 完成购物后进行结算

### 会员购物流程

1. 点击「会员结算」按钮
2. 选择登录方式：
   - **手机号登录**：输入手机号 → 获取验证码 → 输入验证码 → 登录
   - **会员码登录**：扫描会员码 → 直接登录
3. 登录成功后进入扫码购物页面
4. 扫描商品条码享受会员优惠
5. 完成购物后进行会员结算

## 安全特性

1. **数据脱敏**: 手机号显示为 `138****1234` 格式
2. **自动清理**: 退出登录时清理所有会员数据
3. **验证码限制**: 60秒发送间隔限制
4. **表单验证**: 手机号格式和验证码长度验证

## 相关文档

### 架构文档

- [支付流程架构说明](./docs/PAYMENT_ARCHITECTURE.md)
- [TTS语音播报实现](./docs/TTS_IMPLEMENTATION.md)
- [跨平台适配说明](./docs/CROSS_PLATFORM.md)
- [暗黑模式说明](./docs/DARK_MODE.md)

### 开发指南

- [Android TTS 问题排查](./docs/ANDROID_TTS_TROUBLESHOOTING.md)
- [Android SDK 安装指南](./docs/android-install-sdk.md)
- [Android 显示适配修复说明](./docs/Android显示适配修复说明.md)
- [音频录制指南](./docs/AUDIO_RECORDING_GUIDE.md)

### 状态报告

- [项目状态报告](./PROJECT_STATUS.md)
- [页面分析报告](./PAGE_ANALYSIS_REPORT.md)

## 更新日志

### v1.1.0 (当前版本)

- ✅ 支持 Windows 桌面端和 Android 移动端
- ✅ 会员/非会员结算模式
- ✅ 手机号验证码登录
- ✅ 会员码扫描登录
- ✅ 商品条码扫描
- ✅ 购物车管理
- ✅ 多种支付方式
- ✅ TTS 语音播报
- ✅ 响应式设计和移动端适配

---

如有问题或建议，请联系开发团队。
