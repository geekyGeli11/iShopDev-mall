# Mall自助结算系统服务

## 项目简介

mall-selfcheck是mall-swarm微服务架构下的自助结算系统服务模块，为自助收银终端提供后端API支持。

## 功能模块

### 核心功能
- **会员认证**: 手机验证码登录、会话管理、自动退出、自动注册
- **游客模式**: 支持非会员下单，游客会话管理
- **安全防护**: 登录频率限制、IP锁定、异常登录检测
- **短信服务**: 多厂商短信服务支持（阿里云/腾讯云/华为云）
- **商品管理**: 条码扫描、商品查询、库存检查
- **购物车**: 商品添加、数量修改、价格计算
- **支付处理**: 微信支付、支付宝支付、支付状态查询
- **订单管理**: 订单创建、状态跟踪、小票生成

### 技术特性
- **服务注册发现**: 基于Nacos的服务注册与发现
- **配置管理**: 支持Nacos配置中心
- **网关路由**: 通过mall-gateway统一路由
- **认证授权**: 集成SA-Token认证框架，支持JWT令牌
- **统一鉴权**: 与mall-auth统一鉴权，复用会员登录体系
- **API文档**: 集成Knife4j自动生成文档
- **监控管理**: 支持Spring Boot Admin监控

## 技术栈

- **框架**: Spring Boot 3.2.2 + Spring Cloud 2023.0.1
- **服务治理**: Spring Cloud Alibaba + Nacos
- **数据库**: MySQL 8.0 + MyBatis
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **文档数据库**: MongoDB
- **连接池**: Druid
- **认证**: SA-Token
- **API文档**: Knife4j (OpenAPI 3)
- **支付**: 微信支付SDK + 支付宝SDK

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
- MongoDB 4.4+
- Nacos 2.3.0+

### 启动步骤

1. **启动基础服务**
   ```bash
   # 启动MySQL、Redis、RabbitMQ、MongoDB、Nacos
   ```

2. **配置数据库**
   ```sql
   # 导入mall数据库脚本
   # 确保mall数据库存在并包含所需表结构
   ```

3. **启动依赖服务**
   ```bash
   # 启动mall-gateway（网关服务）
   # 启动mall-auth（认证服务）
   ```

4. **启动自助结算服务**
   ```bash
   mvn spring-boot:run
   ```

### 服务验证

访问以下地址验证服务状态：

- **健康检查**: http://localhost:8201/mall-selfcheck/test/health
- **服务信息**: http://localhost:8201/mall-selfcheck/test/info
- **API文档**: http://localhost:8201/doc.html

## 服务配置

### 端口配置
- **服务端口**: 8084
- **网关端口**: 8201（通过网关访问）

### 数据库配置
- **Redis数据库**: 3（避免与其他服务冲突）
- **MongoDB数据库**: mall-selfcheck

### 关键配置项

```yaml
selfcheck:
  member:
    verify-code-expire: 300    # 验证码有效期(秒)
    send-rate-limit: 60        # 验证码发送频率限制(秒)
    session-timeout: 3600      # 会话超时(秒)
    auto-logout-delay: 3       # 自动退出延迟(秒)
    auto-register: true        # 是否自动注册新会员
  security:
    max-login-attempts: 5      # 最大登录尝试次数
    login-lock-duration: 900   # 登录锁定时长(秒)
    ip-block-duration: 3600    # IP锁定时长(秒)
    rate-limit-window: 300     # 频率限制窗口期(秒)
    max-attempts-per-window: 3 # 窗口期内最大尝试次数
  guest:
    session-timeout: 1800      # 游客会话超时(秒)
    allow-order: true          # 是否允许游客下单
  sms:
    provider: mock             # 短信服务商(mock/aliyun/tencent/huawei)
    mock:
      enabled: true            # 启用模拟模式
      fixed-code: "123456"     # 固定验证码(测试用)
    aliyun:
      access-key-id: 阿里云AccessKey
      access-key-secret: 阿里云AccessSecret
      sign-name: 短信签名
      template-code: 短信模板ID
  payment:
    wechat:
      app-id: 微信AppId
      mch-id: 微信商户号
      mch-key: 微信商户密钥
    alipay:
      app-id: 支付宝AppId
      private-key: 支付宝私钥
      public-key: 支付宝公钥
  scan:
    scan-timeout: 30           # 扫码超时(秒)
    supported-formats:         # 支持的条码格式
      - EAN_13
      - EAN_8
      - CODE_128
      - QR_CODE
```

## API接口

### 主要接口分组

- **/test**: 测试接口（健康检查、服务信息、认证测试）
- **/member**: 会员管理（登录、退出、验证码、会员信息）
- **/product**: 商品管理（扫码查询、库存检查）
- **/cart**: 购物车管理（添加、修改、计算）
- **/payment**: 支付管理（微信、支付宝、状态查询）
- **/order**: 订单管理（创建、查询、状态）

### 认证说明

#### 公开接口（无需认证）
- `GET /test/health` - 健康检查
- `GET /test/info` - 服务信息
- `POST /member/login` - 会员登录
- `POST /member/sendVerifyCode` - 发送验证码
- `POST /member/guestLogin` - 游客登录
- `GET /member/guestInfo` - 获取游客信息
- `GET /member/checkSession` - 检查会话状态

#### 需要认证的接口（会员）
- `GET /test/authTest` - 认证测试
- `POST /member/logout` - 退出登录（会员/游客）
- `GET /member/info` - 获取会员信息
- `POST /member/refreshActivity` - 刷新活跃状态
- `GET /member/checkLogin` - 检查登录状态
- 其他业务接口（商品、购物车、支付、订单等）

#### 安全功能
- **登录频率限制**: 5分钟内最多3次登录尝试
- **账号锁定**: 连续5次登录失败锁定15分钟
- **IP防护**: 异常行为自动锁定IP
- **设备信任**: 可信设备管理
- **会话超时**: 会员1小时，游客30分钟自动超时

#### 认证方式
1. **登录获取Token**: 调用 `/member/login` 接口获取JWT令牌
2. **请求头认证**: 在请求头中添加 `Authorization: Bearer {token}`
3. **Token有效期**: 默认3600秒（1小时）
4. **自动续期**: 调用 `/member/refreshActivity` 接口续期

### 网关路由

所有接口通过网关访问，路径前缀：`/mall-selfcheck/`

例如：`http://localhost:8201/mall-selfcheck/test/health`

## 开发规范

### 代码结构
```
src/main/java/com/macro/mall/selfcheck/
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 服务层
│   └── impl/       # 服务实现
├── dto/            # 数据传输对象
├── domain/         # 领域对象
└── util/           # 工具类
```

### 开发指南

1. **接口设计**: 遵循RESTful规范
2. **异常处理**: 统一使用CommonResult包装响应
3. **参数校验**: 使用Bean Validation注解
4. **日志记录**: 使用SLF4J记录关键操作
5. **事务管理**: 合理使用@Transactional注解

## 部署说明

### Docker部署
```bash
# 构建镜像
mvn clean package docker:build

# 运行容器
docker run -p 8084:8084 mall/mall-selfcheck:1.0-SNAPSHOT
```

### 监控指标

通过Spring Boot Actuator暴露监控端点：
- **/actuator/health**: 健康状态
- **/actuator/metrics**: 性能指标
- **/actuator/info**: 服务信息

## 版本历史

### v1.1.1 (2024-01-20)
- 🔧 **架构重构**：修复实体类依赖问题
  - 将自定义实体类迁移到 `mall-mbg` 模块，使用标准实体类
  - 使用 `UmsMember`、`UmsMemberSession`、`UmsGuest` 替代自定义实体
  - 创建 `RequestUtil` 工具类用于获取客户端IP地址
  - 修复所有Service和Controller中的类型引用和导入
- 🗄️ **数据库优化**：
  - 创建 `ums_guest` 游客信息表
  - 创建 `ums_member_session` 会员会话表
  - 添加性能优化索引和清理存储过程
  - 提供完整的SQL脚本 `mall_selfcheck_tables.sql`
- 🔧 **代码优化**：
  - 统一使用mbg模块的实体类和Mapper
  - 优化API返回结构，分离会员信息和会话信息
  - 完善错误处理和日志记录
  - 修复编译错误，确保项目可正常构建

### v1.1.0 (2024-01-20)
- ✅ 完善会员登录逻辑（真实验证码验证、会员信息查询）
- ✅ 集成短信服务（支持阿里云/腾讯云/华为云/模拟模式）
- ✅ 会员信息管理（对接会员数据库、完整会员信息）
- ✅ Session管理（会员会话信息存储和管理）
- ✅ 安全增强（登录频率限制、异常登录检测、IP防护）
- ✅ 游客模式（支持非会员下单，游客账号管理）
- ✅ 配置管理（安全配置、短信配置类）

### v1.0.0
- 项目初始化
- 基础框架搭建
- 服务注册发现集成
- SA-Token认证集成
- 基础会员登录接口
- 网关路由配置

## 联系方式

如有问题请联系开发团队或提交Issue。 