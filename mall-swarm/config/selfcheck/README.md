# Mall-Selfcheck Nacos 配置说明

## 配置文件结构

### 本地配置文件
- `mall-selfcheck/src/main/resources/application.yml` - 基础应用配置
- `mall-selfcheck/src/main/resources/bootstrap.yml` - Nacos 连接配置

### Nacos 配置文件
- `application-nacos.yml` - 开发环境配置 (数据ID: mall-selfcheck-dev.yaml)
- `application-nacos-prod.yml` - 生产环境配置 (数据ID: mall-selfcheck-prod.yaml)

## Nacos 配置中心设置

### 配置信息
- **服务器地址**: localhost:8848
- **命名空间**: dev (开发环境), prod (生产环境)
- **分组**: DEFAULT_GROUP
- **文件格式**: YAML

### 配置项说明

#### 数据库配置
```yaml
spring.datasource:
  url: 数据库连接地址
  username: 数据库用户名
  password: 数据库密码
```

#### Redis配置
```yaml
spring.data.redis:
  host: Redis服务器地址
  port: Redis端口
  database: 数据库索引
  password: Redis密码
```

#### RabbitMQ配置
```yaml
spring.rabbitmq:
  host: RabbitMQ服务器地址
  port: RabbitMQ端口
  username: RabbitMQ用户名
  password: RabbitMQ密码
```

#### 支付配置
```yaml
selfcheck.payment:
  wechat: 微信支付配置
  alipay: 支付宝配置
```

## 环境变量

### 生产环境需要设置的环境变量

#### 数据库
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码

#### Redis
- `REDIS_PASSWORD`: Redis密码

#### MongoDB
- `MONGO_USERNAME`: MongoDB用户名
- `MONGO_PASSWORD`: MongoDB密码

#### RabbitMQ
- `RABBITMQ_USERNAME`: RabbitMQ用户名
- `RABBITMQ_PASSWORD`: RabbitMQ密码

#### 支付宝
- `ALIPAY_APP_ID`: 支付宝应用ID
- `ALIPAY_PUBLIC_KEY`: 支付宝公钥
- `ALIPAY_PRIVATE_KEY`: 支付宝私钥
- `ALIPAY_RETURN_URL`: 支付宝回调地址
- `ALIPAY_NOTIFY_URL`: 支付宝通知地址

#### 微信支付
- `WECHAT_PAY_APP_ID`: 微信支付应用ID
- `WECHAT_PAY_MCH_ID`: 微信支付商户号
- `WECHAT_PAY_MCH_KEY`: 微信支付商户密钥
- `WECHAT_PAY_NOTIFY_URL`: 微信支付通知地址

#### 微信公众号
- `WECHAT_APP_ID`: 微信公众号AppID
- `WECHAT_APP_SECRET`: 微信公众号AppSecret

## 使用步骤

1. **启动 Nacos Server**
   ```bash
   # 下载并启动Nacos
   ./startup.sh -m standalone
   ```

2. **在 Nacos 控制台创建配置**
   - 访问 http://localhost:8848/nacos
   - 创建命名空间: dev, prod
   - 在对应命名空间中创建配置文件

3. **配置文件导入**
   - 数据ID: `mall-selfcheck-dev.yaml`
   - 分组: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 复制 `application-nacos.yml` 内容

4. **启动应用**
   ```bash
   # 开发环境
   java -jar mall-selfcheck.jar --spring.profiles.active=dev
   
   # 生产环境
   java -jar mall-selfcheck.jar --spring.profiles.active=prod
   ```

## 注意事项

1. **敏感信息**: 生产环境的密码、密钥等敏感信息请使用环境变量
2. **配置刷新**: Nacos 支持配置热更新，修改配置后无需重启应用
3. **配置备份**: 建议定期备份 Nacos 配置
4. **权限控制**: 生产环境请设置 Nacos 访问权限 