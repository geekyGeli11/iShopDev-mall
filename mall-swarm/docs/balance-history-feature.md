# 余额历史记录功能实现文档

## 概述

本文档描述了充值历史和消费记录功能的完整实现，包括后端API接口和前端弹窗展示。

## 功能特性

- ✅ 充值历史查询：显示用户所有充值记录
- ✅ 消费记录查询：显示用户所有消费记录  
- ✅ 分页加载：支持分页查询和滚动加载更多
- ✅ 底部弹窗：符合移动端交互习惯的弹窗展示
- ✅ 数据格式化：友好的时间和金额显示格式
- ✅ 加载状态：完善的加载中和空状态提示

## 后端实现

### 1. 数据库表结构

基于现有的 `ums_member_balance_history` 表：

```sql
CREATE TABLE `ums_member_balance_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `member_id` bigint NOT NULL COMMENT '用户ID',
  `change_type` tinyint NOT NULL COMMENT '变动类型：1-充值，2-消费，3-退款，4-转入，5-转出',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额（正数为增加，负数为减少）',
  `balance_before` decimal(10,2) NOT NULL COMMENT '变动前余额',
  `balance_after` decimal(10,2) NOT NULL COMMENT '变动后余额',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型：recharge-充值，order-订单消费，refund-退款等',
  `business_id` varchar(64) DEFAULT NULL COMMENT '关联业务ID（订单号、充值单号等）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人（系统/用户/管理员）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_business_type_id` (`business_type`, `business_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户余额变动记录表';
```

### 2. 新增API接口

#### 通用历史记录接口
```http
GET /member/balance/history
```

参数：
- `changeType` (可选): 变动类型筛选
- `pageNum`: 页码，默认1
- `pageSize`: 页大小，默认20

#### 充值历史接口
```http
GET /member/balance/recharge/history
```

参数：
- `pageNum`: 页码，默认1  
- `pageSize`: 页大小，默认20

#### 消费记录接口
```http
GET /member/balance/consume/history
```

参数：
- `pageNum`: 页码，默认1
- `pageSize`: 页大小，默认20

### 3. 核心代码

#### Controller 层
```java
@Operation(summary = "获取充值历史")
@GetMapping("/recharge/history")
public CommonResult<List<UmsMemberBalanceHistory>> getRechargeHistory(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "20") Integer pageSize) {
    Long memberId = StpMemberUtil.getLoginIdAsLong();
    if (memberId == null) {
        return CommonResult.unauthorized("请先登录");
    }
    
    try {
        List<UmsMemberBalanceHistory> historyList = 
            memberBalanceService.getBalanceHistory(memberId, 1, pageNum, pageSize);
        return CommonResult.success(historyList);
    } catch (Exception e) {
        return CommonResult.failed("获取充值历史失败：" + e.getMessage());
    }
}
```

#### Service 层
```java
@Override
public List<UmsMemberBalanceHistory> getBalanceHistory(
    Long memberId, Integer changeType, Integer pageNum, Integer pageSize) {
    
    PageHelper.startPage(pageNum, pageSize);
    
    UmsMemberBalanceHistoryExample example = new UmsMemberBalanceHistoryExample();
    UmsMemberBalanceHistoryExample.Criteria criteria = example.createCriteria();
    
    criteria.andMemberIdEqualTo(memberId);
    
    if (changeType != null) {
        criteria.andChangeTypeEqualTo(changeType.byteValue());
    }
    
    example.setOrderByClause("create_time DESC");
    
    return balanceHistoryMapper.selectByExample(example);
}
```

## 前端实现

### 1. API 调用

```javascript
// wallet.js 中新增的API函数
export function getRechargeHistory(params = {}) {
  return request({
    method: 'GET',
    url: '/member/balance/recharge/history',
    params: params
  })
}

export function getConsumeHistory(params = {}) {
  return request({
    method: 'GET',
    url: '/member/balance/consume/history', 
    params: params
  })
}
```

### 2. 页面交互

#### 按钮点击事件
```javascript
// 显示充值历史
showHistory() {
  this.historyType = 'recharge';
  this.historyTitle = '充值历史';
  this.showHistoryModal = true;
  this.loadHistoryData();
},

// 显示消费记录  
showRecords() {
  this.historyType = 'consume';
  this.historyTitle = '消费记录';
  this.showHistoryModal = true;
  this.loadHistoryData();
}
```

#### 数据加载
```javascript
loadHistoryData(loadMore = false) {
  if (!loadMore) {
    this.historyPage = 1;
    this.historyList = [];
  }
  
  this.historyLoading = true;
  
  const apiFunc = this.historyType === 'recharge' ? getRechargeHistory : getConsumeHistory;
  const params = {
    pageNum: this.historyPage,
    pageSize: 20
  };
  
  apiFunc(params).then(res => {
    if (res.code === 200) {
      const newList = res.data || [];
      
      if (loadMore) {
        this.historyList = [...this.historyList, ...newList];
      } else {
        this.historyList = newList;
      }
      
      if (newList.length < params.pageSize) {
        this.historyTotal = this.historyList.length;
      } else {
        this.historyPage++;
      }
    }
  });
}
```

### 3. UI 组件

#### 弹窗结构
```vue
<view v-if="showHistoryModal" class="history-modal" @tap="closeHistoryModal">
  <view class="history-modal-content" @tap.stop="">
    <!-- 弹窗头部 -->
    <view class="history-header">
      <view class="history-title">{{ historyTitle }}</view>
      <view class="history-close" @tap="closeHistoryModal">×</view>
    </view>
    
    <!-- 历史记录列表 -->
    <scroll-view class="history-scroll" scroll-y @scrolltolower="onHistoryScrollToLower">
      <view v-for="item in historyList" :key="item.id" class="history-item">
        <!-- 记录内容 -->
      </view>
    </scroll-view>
  </view>
</view>
```

#### 数据格式化
```javascript
formatHistoryRecord(record) {
  const changeTypeMap = {
    1: '充值', 2: '消费', 3: '退款', 4: '转入', 5: '转出'
  };
  
  const businessTypeMap = {
    'recharge': '余额充值',
    'recharge_bonus': '充值奖励', 
    'order': '订单支付',
    'refund': '订单退款'
  };
  
  return {
    ...record,
    changeTypeText: changeTypeMap[record.changeType] || '未知',
    businessTypeText: businessTypeMap[record.businessType] || record.businessType,
    amountText: this.formatAmount(Math.abs(record.amount)),
    isPositive: record.amount > 0,
    createTimeText: this.formatDateTime(record.createTime)
  };
}
```

## 样式特性

### 1. 移动端优化
- 底部弹窗设计，符合移动端操作习惯
- 圆角设计，现代化UI风格
- 触摸友好的按钮和交互区域

### 2. 视觉层次
- 清晰的标题和关闭按钮
- 分组显示的记录信息
- 正负金额的颜色区分（绿色/红色）

### 3. 加载状态
- 加载中提示
- 空状态显示
- 滚动加载更多

## 测试建议

### 1. 功能测试
- [ ] 充值历史正确显示
- [ ] 消费记录正确显示  
- [ ] 分页加载正常工作
- [ ] 弹窗开关正常
- [ ] 数据格式化正确

### 2. 边界测试
- [ ] 无数据时的空状态显示
- [ ] 网络错误时的错误处理
- [ ] 大量数据的性能表现
- [ ] 权限验证（未登录状态）

### 3. 兼容性测试
- [ ] 不同手机型号的显示效果
- [ ] 横竖屏切换适配
- [ ] 微信小程序环境兼容性

## 部署注意事项

1. **数据库**：确保 `ums_member_balance_history` 表已创建并有测试数据
2. **后端服务**：重启 mall-portal 服务以加载新的API接口
3. **前端部署**：更新前端代码并重新编译发布
4. **测试验证**：使用提供的测试脚本验证API功能

## 扩展建议

1. **高级筛选**：按时间范围、金额范围筛选
2. **导出功能**：支持导出历史记录为Excel
3. **图表展示**：添加收支趋势图表
4. **详情页面**：点击记录查看详细信息
5. **缓存优化**：增加数据缓存减少数据库查询 