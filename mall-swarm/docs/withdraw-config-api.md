# 提现配置API文档

## 📋 概述

提现配置API提供了完整的提现参数管理功能，支持获取和更新所有配置项。

## 🔗 API端点

### 获取提现配置
- **URL**: `GET /invite/withdraw/config`
- **说明**: 获取所有提现相关配置信息

### 更新提现配置  
- **URL**: `PUT /invite/withdraw/config`
- **说明**: 更新提现配置（支持部分更新）

## 📊 完整配置参数列表

### 💰 金额配置
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `minWithdrawAmount` | Number | 最小提现金额 | 10.0 |
| `maxWithdrawAmount` | Number | 最大提现金额 | 5000.0 |
| `monthlyLimit` | Number | 每月提现限额 | 10000.0 |

### 📊 次数限制
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `maxDailyCount` | Integer | 每日提现次数限制 | 3 |

### 💳 手续费配置
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `wechatFeeRate` | Number | 微信提现手续费率 | 0.02 (2%) |
| `bankFeeRate` | Number | 银行卡提现手续费率 | 0.03 (3%) |
| `minFeeAmount` | Number | 最小手续费金额 | 0.01 |
| `maxFeeAmount` | Number | 最大手续费金额 | 50.0 |

### ⏱️ 审核配置
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `auditTimeLimit` | Integer | 审核时间限制（小时） | 24 |
| `autoAudit` | Boolean | 是否启用自动审核 | false |
| `autoAuditAmount` | Number | 自动审核金额上限 | 50.0 |

### 📅 到账时间配置
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `wechatArrivalDays` | Integer | 微信到账天数 | 3 |
| `bankArrivalDays` | Integer | 银行卡到账天数 | 5 |

### 🔔 功能配置
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `notificationEnabled` | Boolean | 是否启用通知 | true |
| `timeRestriction` | Boolean | 是否启用时间限制 | false |
| `startTime` | String | 提现开始时间 | "09:00" |
| `endTime` | String | 提现结束时间 | "18:00" |

## 📝 请求示例

### 获取配置
```http
GET /invite/withdraw/config
```

### 更新配置（完整）
```http
PUT /invite/withdraw/config
Content-Type: application/json

{
  "minWithdrawAmount": 10.0,
  "maxWithdrawAmount": 5000.0,
  "maxDailyCount": 3,
  "monthlyLimit": 10000.0,
  "wechatFeeRate": 0.02,
  "bankFeeRate": 0.03,
  "minFeeAmount": 0.01,
  "maxFeeAmount": 50.0,
  "auditTimeLimit": 24,
  "autoAudit": false,
  "autoAuditAmount": 50.0,
  "wechatArrivalDays": 3,
  "bankArrivalDays": 5,
  "notificationEnabled": true,
  "timeRestriction": false,
  "startTime": "09:00",
  "endTime": "18:00"
}
```

### 更新配置（部分）
```http
PUT /invite/withdraw/config
Content-Type: application/json

{
  "wechatFeeRate": 0.025,
  "bankFeeRate": 0.035,
  "autoAudit": true
}
```

## 📤 响应示例

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "minWithdrawAmount": 10.0,
    "maxWithdrawAmount": 5000.0,
    "maxDailyCount": 3,
    "monthlyLimit": 10000.0,
    "wechatFeeRate": 0.02,
    "bankFeeRate": 0.03,
    "minFeeAmount": 0.01,
    "maxFeeAmount": 50.0,
    "auditTimeLimit": 24,
    "autoAudit": false,
    "autoAuditAmount": 50.0,
    "wechatArrivalDays": 3,
    "bankArrivalDays": 5,
    "notificationEnabled": true,
    "timeRestriction": false,
    "startTime": "09:00",
    "endTime": "18:00"
  }
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "获取配置失败：具体错误信息",
  "data": null
}
```

## 🔧 实施步骤

1. **添加数据库配置**:
   ```powershell
   cd mall-swarm/scripts
   .\add-complete-withdraw-config.ps1
   ```

2. **重启应用**: 重启mall-admin服务

3. **测试API**: 使用上述示例测试配置获取和更新功能

## ⚠️ 注意事项

- 所有配置项都支持独立更新，无需传递完整配置
- 布尔值配置在数据库中存储为字符串（"0"/"1"）
- 时间格式为 "HH:mm"
- 手续费率为小数形式（0.02表示2%）
- 金额单位为元，支持小数

## 🎯 配置建议

- **手续费率**: 微信 1.5%-3%，银行卡 2%-5%
- **审核时间**: 1-72小时
- **到账时间**: 微信1-3天，银行卡3-7天
- **单次限额**: 10-5000元
- **月限额**: 5000-50000元 