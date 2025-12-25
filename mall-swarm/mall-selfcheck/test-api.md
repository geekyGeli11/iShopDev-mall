# Mall自助结算系统 API测试手册

## 快速测试

### 1. 服务健康检查

```bash
# 检查服务是否正常启动
curl -X GET "http://localhost:8201/mall-selfcheck/test/health"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00.000+08:00",
    "version": "1.1.0"
  }
}
```

### 2. 发送验证码

```bash
# 发送验证码到手机号
curl -X POST "http://localhost:8201/mall-selfcheck/member/sendVerifyCode?telephone=13800138000"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "message": "验证码发送成功",
    "codeLength": 6,
    "expireTime": 300,
    "canResendAfter": 60
  }
}
```

### 3. 会员登录

```bash
# 会员登录（使用模拟验证码123456）
curl -X POST "http://localhost:8201/mall-selfcheck/member/login" \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "13800138000",
    "verifyCode": "123456"
  }'

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer ",
    "memberId": 1001,
    "memberInfo": {
      "id": 1001,
      "username": "user_8000",
      "nickname": "用户8000",
      "phone": "13800138000",
      "status": 1,
      "isGuest": false
    },
    "sessionId": "session123456789",
    "expiresIn": 3600
  }
}
```

### 4. 游客登录

```bash
# 游客登录
curl -X POST "http://localhost:8201/mall-selfcheck/member/guestLogin?deviceId=device001&deviceType=android"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "guestId": "guest_device00_123456_7890",
    "deviceId": "device001",
    "deviceType": "android",
    "isGuest": true,
    "createTime": "2024-01-01T12:00:00.000+08:00"
  }
}
```

### 5. 获取会员信息

```bash
# 使用登录获取的token
TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."

curl -X GET "http://localhost:8201/mall-selfcheck/member/info" \
  -H "Authorization: Bearer $TOKEN"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1001,
    "username": "user_8000",
    "nickname": "用户8000",
    "phone": "13800138000",
    "status": 1,
    "memberLevelName": "普通会员",
    "balance": 0.00,
    "isGuest": false,
    "lastActiveTime": "2024-01-01T12:00:00.000+08:00",
    "loginIp": "127.0.0.1"
  }
}
```

### 6. 检查会话状态

```bash
# 检查会员会话
curl -X GET "http://localhost:8201/mall-selfcheck/member/checkSession"

# 检查游客会话
curl -X GET "http://localhost:8201/mall-selfcheck/member/checkSession?guestId=guest_device00_123456_7890"

# 预期响应（会员）
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "isLogin": true,
    "isMember": true,
    "isGuest": false,
    "memberId": 1001,
    "isValid": true
  }
}

# 预期响应（游客）
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "isLogin": false,
    "isMember": false,
    "isGuest": true,
    "guestId": "guest_device00_123456_7890",
    "isValid": true
  }
}
```

### 7. 刷新活跃状态

```bash
# 刷新会员活跃状态
curl -X POST "http://localhost:8201/mall-selfcheck/member/refreshActivity" \
  -H "Authorization: Bearer $TOKEN"

# 刷新游客活跃状态
curl -X POST "http://localhost:8201/mall-selfcheck/member/refreshActivity?guestId=guest_device00_123456_7890"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": "会员活跃状态已刷新"
}
```

### 8. 退出登录

```bash
# 会员退出登录
curl -X POST "http://localhost:8201/mall-selfcheck/member/logout" \
  -H "Authorization: Bearer $TOKEN"

# 游客退出
curl -X POST "http://localhost:8201/mall-selfcheck/member/logout?guestId=guest_device00_123456_7890"

# 预期响应
{
  "code": 200,
  "message": "操作成功",
  "data": "会员退出登录成功"
}
```

## 安全功能测试

### 1. 登录频率限制测试

```bash
# 快速连续发送验证码（应该被限制）
for i in {1..5}; do
  curl -X POST "http://localhost:8201/mall-selfcheck/member/sendVerifyCode?telephone=13800138001"
  echo ""
done

# 第二次及以后应该返回：
{
  "code": 500,
  "message": "发送过于频繁，请等待 XX 秒后重试"
}
```

### 2. 登录失败锁定测试

```bash
# 连续使用错误验证码登录（应该被锁定）
for i in {1..6}; do
  curl -X POST "http://localhost:8201/mall-selfcheck/member/login" \
    -H "Content-Type: application/json" \
    -d '{
      "telephone": "13800138002",
      "verifyCode": "000000"
    }'
  echo ""
done

# 第6次应该返回：
{
  "code": 500,
  "message": "账号已被锁定，请等待 XXX 秒后重试"
}
```

## 错误场景测试

### 1. 无效手机号

```bash
curl -X POST "http://localhost:8201/mall-selfcheck/member/sendVerifyCode?telephone=1234567890"

# 预期响应
{
  "code": 404,
  "message": "手机号格式不正确"
}
```

### 2. 验证码错误

```bash
curl -X POST "http://localhost:8201/mall-selfcheck/member/login" \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "13800138000",
    "verifyCode": "000000"
  }'

# 预期响应
{
  "code": 500,
  "message": "验证码错误或已过期"
}
```

### 3. 未登录访问受保护资源

```bash
curl -X GET "http://localhost:8201/mall-selfcheck/member/info"

# 预期响应
{
  "code": 401,
  "message": "用户未登录"
}
```

### 4. 无效Token

```bash
curl -X GET "http://localhost:8201/mall-selfcheck/member/info" \
  -H "Authorization: Bearer invalid_token"

# 预期响应
{
  "code": 401,
  "message": "用户未登录"
}
```

## 性能测试

### 1. 并发登录测试

使用Apache Bench进行并发测试：

```bash
# 安装ab工具
sudo apt-get install apache2-utils

# 并发测试验证码发送
ab -n 100 -c 10 "http://localhost:8201/mall-selfcheck/member/sendVerifyCode?telephone=13800138000"

# 并发测试健康检查
ab -n 1000 -c 50 "http://localhost:8201/mall-selfcheck/test/health"
```

## 配置测试

### 1. 切换短信服务商

修改 `application.yml` 配置：

```yaml
selfcheck:
  sms:
    provider: aliyun  # 切换到阿里云
    aliyun:
      access-key-id: your_access_key
      access-key-secret: your_access_secret
      sign-name: your_sign_name
      template-code: your_template_code
```

### 2. 调整安全策略

```yaml
selfcheck:
  security:
    max-login-attempts: 3      # 调整最大失败次数
    login-lock-duration: 600   # 调整锁定时长
    rate-limit-window: 60      # 调整频率限制窗口
```

## 监控检查

### 1. 查看应用指标

```bash
# 健康状态
curl "http://localhost:8084/actuator/health"

# 应用信息
curl "http://localhost:8084/actuator/info"

# 性能指标
curl "http://localhost:8084/actuator/metrics"
```

### 2. 查看Redis缓存

```bash
# 连接Redis查看缓存数据
redis-cli -h localhost -p 6379 -n 3

# 查看验证码缓存
keys selfcheck:sms:code:*

# 查看会话缓存
keys selfcheck:session:*

# 查看安全相关缓存
keys selfcheck:security:*
```

## 注意事项

1. **测试环境**: 建议在测试环境进行测试，避免影响生产数据
2. **手机号**: 使用测试手机号，避免发送真实短信
3. **验证码**: 默认配置下可使用固定验证码 `123456`
4. **Token**: 保存登录返回的token用于后续认证请求
5. **清理**: 测试完成后清理Redis缓存数据

## 常见问题

1. **服务无法访问**: 检查网关服务是否启动
2. **验证码收不到**: 检查短信服务配置和余额
3. **Token过期**: 重新登录获取新token
4. **会话超时**: 调用刷新接口延长会话
5. **IP被锁定**: 等待锁定时间过期或清理Redis缓存 