# 优惠券冲突检查功能说明

## 功能概述

为了防止优惠券配置中出现逻辑冲突，我们在管理后台添加了完善的冲突检查机制。该功能确保可用分类/商品和排除分类/商品之间不会出现冲突，避免业务逻辑错误。

## 冲突类型

### 1. 直接冲突
- **分类直接冲突**：同一个分类既在可用列表又在排除列表中
- **商品直接冲突**：同一个商品既在可用列表又在排除列表中

### 2. 父子分类冲突
- **一级分类与二级分类冲突**：
  - 一级分类在可用列表，其子分类在排除列表
  - 一级分类在排除列表，其子分类在可用列表

### 3. 业务逻辑冲突
- 启用了排除逻辑但未配置任何排除项

## 检查时机

### 1. 实时检查（添加时）
- 添加可用分类时检查是否与排除分类冲突
- 添加排除分类时检查是否与可用分类冲突
- 选择可用商品时检查是否与排除商品冲突
- 选择排除商品时检查是否与可用商品冲突

### 2. 最终校验（提交时）
- 表单提交前进行全面的冲突检查
- 确保所有配置的合理性

## 检查逻辑详解

### 分类冲突检查

#### 直接冲突检查
```javascript
// 检查分类是否已在对立列表中
const conflictInExclude = this.coupon.productCategoryExcludeRelationList.some(item => 
  item.productCategoryId === categoryId
);
```

#### 父子分类冲突检查
```javascript
// 检查父子分类关系
checkParentChildCategoryConflict(categoryId, listType) {
  const targetList = listType === 'include' 
    ? this.coupon.productCategoryRelationList 
    : this.coupon.productCategoryExcludeRelationList;
    
  const currentCategoryInfo = this.getCategoryInfo(categoryId);
  
  for (let item of targetList) {
    const existingCategoryInfo = this.getCategoryInfo(item.productCategoryId);
    if (this.isParentChildRelation(currentCategoryInfo, existingCategoryInfo)) {
      return true; // 存在冲突
    }
  }
  return false;
}
```

### 商品冲突检查

```javascript
// 检查商品是否在对立列表中
checkProductConflict(products, type) {
  const conflictProducts = [];
  const targetList = type === 'include' 
    ? this.coupon.productExcludeRelationList 
    : this.coupon.productRelationList;
    
  for (let product of products) {
    const exists = targetList.some(item => item.productId === product.id);
    if (exists) {
      conflictProducts.push(product);
    }
  }
  return conflictProducts;
}
```

## 用户交互

### 错误提示信息

#### 分类冲突提示
- **直接冲突**：`该分类已在排除列表中，不能同时设为可用分类`
- **父子冲突**：`该分类与排除列表中的父/子分类存在冲突`

#### 商品冲突提示
- **批量选择冲突**：`以下商品已在排除列表中，请先移除：商品A、商品B`
- **重复添加**：`该商品已添加，请勿重复添加`

#### 最终校验提示
- **分类冲突**：`以下分类在可用和排除列表中存在冲突：服装、数码`
- **商品冲突**：`以下商品在可用和排除列表中存在冲突：iPhone、iPad`
- **配置不完整**：`已启用排除逻辑，但未配置任何排除商品或分类`

### 操作流程

#### 添加可用分类
1. 用户选择分类
2. 系统检查是否与排除分类冲突
3. 如有冲突，显示错误提示，阻止添加
4. 如无冲突，成功添加到可用列表

#### 添加排除分类
1. 用户选择分类
2. 系统检查是否与可用分类冲突
3. 如有冲突，显示错误提示，阻止添加
4. 如无冲突，成功添加到排除列表

#### 商品选择
1. 用户在对话框中选择商品
2. 点击确认时检查冲突
3. 如有冲突，显示具体冲突商品，不关闭对话框
4. 如无冲突，更新商品列表并关闭对话框

## 冲突场景示例

### 场景1：分类直接冲突
- **操作**：将"服装"分类同时添加到可用和排除列表
- **结果**：系统阻止操作，提示冲突
- **解决**：只能选择其中一种配置

### 场景2：父子分类冲突
- **操作**：将"服装"一级分类设为可用，将"男装"二级分类设为排除
- **结果**：系统检测到父子冲突，阻止操作
- **解决**：调整配置逻辑，避免父子冲突

### 场景3：商品冲突
- **操作**：将"iPhone"同时添加到可用和排除商品列表
- **结果**：系统阻止操作，提示商品冲突
- **解决**：只能选择其中一种配置

### 场景4：复杂组合冲突
- **配置**：
  - 可用分类：服装
  - 排除分类：男装（服装的子分类）
  - 可用商品：特定T恤
  - 排除商品：同一件T恤
- **结果**：系统检测到多重冲突，提供详细的冲突列表
- **解决**：逐项解决冲突

## 技术实现要点

### 1. 分类层级关系判断
```javascript
// 获取分类信息，包含层级和父子关系
getCategoryInfo(categoryId) {
  // 查找一级分类
  for (let group of this.productCateOptions) {
    if (group.value === categoryId) {
      return { id: categoryId, level: 1, parentId: null };
    }
    // 查找二级分类
    for (let child of group.children) {
      if (child.value === categoryId) {
        return { id: categoryId, level: 2, parentId: group.value };
      }
    }
  }
}
```

### 2. 父子关系检查
```javascript
// 判断两个分类是否存在父子关系
isParentChildRelation(category1, category2) {
  return (category1.level === 1 && category2.level === 2 && category2.parentId === category1.id) ||
         (category2.level === 1 && category1.level === 2 && category1.parentId === category2.id);
}
```

### 3. 批量冲突检查
```javascript
// 最终校验时的全面检查
validateFinalConflicts() {
  // 检查所有可能的冲突类型
  // 返回详细的冲突信息
  // 阻止有冲突的配置提交
}
```

## 业务价值

1. **防止配置错误**：避免管理员配置出逻辑矛盾的优惠券
2. **提升用户体验**：减少因配置错误导致的用户困惑
3. **保证系统稳定**：防止因逻辑冲突导致的系统异常
4. **降低维护成本**：减少因配置问题导致的客服咨询

## 扩展性

该冲突检查机制具有良好的扩展性：
- 可以轻松添加新的冲突类型检查
- 支持更复杂的业务规则验证
- 可以集成到其他类似的配置功能中

通过这套完善的冲突检查机制，我们确保了优惠券配置的逻辑一致性和业务合理性。
