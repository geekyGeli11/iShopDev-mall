package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsStockOperationApprovalItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsStockOperationApprovalItemExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andApprovalIdIsNull() {
            addCriterion("approval_id is null");
            return (Criteria) this;
        }

        public Criteria andApprovalIdIsNotNull() {
            addCriterion("approval_id is not null");
            return (Criteria) this;
        }

        public Criteria andApprovalIdEqualTo(Long value) {
            addCriterion("approval_id =", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdNotEqualTo(Long value) {
            addCriterion("approval_id <>", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdGreaterThan(Long value) {
            addCriterion("approval_id >", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdGreaterThanOrEqualTo(Long value) {
            addCriterion("approval_id >=", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdLessThan(Long value) {
            addCriterion("approval_id <", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdLessThanOrEqualTo(Long value) {
            addCriterion("approval_id <=", value, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdIn(List<Long> values) {
            addCriterion("approval_id in", values, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdNotIn(List<Long> values) {
            addCriterion("approval_id not in", values, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdBetween(Long value1, Long value2) {
            addCriterion("approval_id between", value1, value2, "approvalId");
            return (Criteria) this;
        }

        public Criteria andApprovalIdNotBetween(Long value1, Long value2) {
            addCriterion("approval_id not between", value1, value2, "approvalId");
            return (Criteria) this;
        }

        public Criteria andOperationNoIsNull() {
            addCriterion("operation_no is null");
            return (Criteria) this;
        }

        public Criteria andOperationNoIsNotNull() {
            addCriterion("operation_no is not null");
            return (Criteria) this;
        }

        public Criteria andOperationNoEqualTo(String value) {
            addCriterion("operation_no =", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoNotEqualTo(String value) {
            addCriterion("operation_no <>", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoGreaterThan(String value) {
            addCriterion("operation_no >", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoGreaterThanOrEqualTo(String value) {
            addCriterion("operation_no >=", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoLessThan(String value) {
            addCriterion("operation_no <", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoLessThanOrEqualTo(String value) {
            addCriterion("operation_no <=", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoLike(String value) {
            addCriterion("operation_no like", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoNotLike(String value) {
            addCriterion("operation_no not like", value, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoIn(List<String> values) {
            addCriterion("operation_no in", values, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoNotIn(List<String> values) {
            addCriterion("operation_no not in", values, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoBetween(String value1, String value2) {
            addCriterion("operation_no between", value1, value2, "operationNo");
            return (Criteria) this;
        }

        public Criteria andOperationNoNotBetween(String value1, String value2) {
            addCriterion("operation_no not between", value1, value2, "operationNo");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNull() {
            addCriterion("product_id is null");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNotNull() {
            addCriterion("product_id is not null");
            return (Criteria) this;
        }

        public Criteria andProductIdEqualTo(Long value) {
            addCriterion("product_id =", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotEqualTo(Long value) {
            addCriterion("product_id <>", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThan(Long value) {
            addCriterion("product_id >", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThanOrEqualTo(Long value) {
            addCriterion("product_id >=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThan(Long value) {
            addCriterion("product_id <", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThanOrEqualTo(Long value) {
            addCriterion("product_id <=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdIn(List<Long> values) {
            addCriterion("product_id in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotIn(List<Long> values) {
            addCriterion("product_id not in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdBetween(Long value1, Long value2) {
            addCriterion("product_id between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotBetween(Long value1, Long value2) {
            addCriterion("product_id not between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductNameIsNull() {
            addCriterion("product_name is null");
            return (Criteria) this;
        }

        public Criteria andProductNameIsNotNull() {
            addCriterion("product_name is not null");
            return (Criteria) this;
        }

        public Criteria andProductNameEqualTo(String value) {
            addCriterion("product_name =", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotEqualTo(String value) {
            addCriterion("product_name <>", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameGreaterThan(String value) {
            addCriterion("product_name >", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameGreaterThanOrEqualTo(String value) {
            addCriterion("product_name >=", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLessThan(String value) {
            addCriterion("product_name <", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLessThanOrEqualTo(String value) {
            addCriterion("product_name <=", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLike(String value) {
            addCriterion("product_name like", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotLike(String value) {
            addCriterion("product_name not like", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameIn(List<String> values) {
            addCriterion("product_name in", values, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotIn(List<String> values) {
            addCriterion("product_name not in", values, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameBetween(String value1, String value2) {
            addCriterion("product_name between", value1, value2, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotBetween(String value1, String value2) {
            addCriterion("product_name not between", value1, value2, "productName");
            return (Criteria) this;
        }

        public Criteria andProductSnIsNull() {
            addCriterion("product_sn is null");
            return (Criteria) this;
        }

        public Criteria andProductSnIsNotNull() {
            addCriterion("product_sn is not null");
            return (Criteria) this;
        }

        public Criteria andProductSnEqualTo(String value) {
            addCriterion("product_sn =", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotEqualTo(String value) {
            addCriterion("product_sn <>", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnGreaterThan(String value) {
            addCriterion("product_sn >", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnGreaterThanOrEqualTo(String value) {
            addCriterion("product_sn >=", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLessThan(String value) {
            addCriterion("product_sn <", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLessThanOrEqualTo(String value) {
            addCriterion("product_sn <=", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLike(String value) {
            addCriterion("product_sn like", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotLike(String value) {
            addCriterion("product_sn not like", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnIn(List<String> values) {
            addCriterion("product_sn in", values, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotIn(List<String> values) {
            addCriterion("product_sn not in", values, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnBetween(String value1, String value2) {
            addCriterion("product_sn between", value1, value2, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotBetween(String value1, String value2) {
            addCriterion("product_sn not between", value1, value2, "productSn");
            return (Criteria) this;
        }

        public Criteria andSkuIdIsNull() {
            addCriterion("sku_id is null");
            return (Criteria) this;
        }

        public Criteria andSkuIdIsNotNull() {
            addCriterion("sku_id is not null");
            return (Criteria) this;
        }

        public Criteria andSkuIdEqualTo(Long value) {
            addCriterion("sku_id =", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotEqualTo(Long value) {
            addCriterion("sku_id <>", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdGreaterThan(Long value) {
            addCriterion("sku_id >", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdGreaterThanOrEqualTo(Long value) {
            addCriterion("sku_id >=", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdLessThan(Long value) {
            addCriterion("sku_id <", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdLessThanOrEqualTo(Long value) {
            addCriterion("sku_id <=", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdIn(List<Long> values) {
            addCriterion("sku_id in", values, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotIn(List<Long> values) {
            addCriterion("sku_id not in", values, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdBetween(Long value1, Long value2) {
            addCriterion("sku_id between", value1, value2, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotBetween(Long value1, Long value2) {
            addCriterion("sku_id not between", value1, value2, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNull() {
            addCriterion("sku_code is null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNotNull() {
            addCriterion("sku_code is not null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeEqualTo(String value) {
            addCriterion("sku_code =", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotEqualTo(String value) {
            addCriterion("sku_code <>", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThan(String value) {
            addCriterion("sku_code >", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThanOrEqualTo(String value) {
            addCriterion("sku_code >=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThan(String value) {
            addCriterion("sku_code <", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThanOrEqualTo(String value) {
            addCriterion("sku_code <=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLike(String value) {
            addCriterion("sku_code like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotLike(String value) {
            addCriterion("sku_code not like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIn(List<String> values) {
            addCriterion("sku_code in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotIn(List<String> values) {
            addCriterion("sku_code not in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeBetween(String value1, String value2) {
            addCriterion("sku_code between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotBetween(String value1, String value2) {
            addCriterion("sku_code not between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityIsNull() {
            addCriterion("operation_quantity is null");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityIsNotNull() {
            addCriterion("operation_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityEqualTo(Integer value) {
            addCriterion("operation_quantity =", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityNotEqualTo(Integer value) {
            addCriterion("operation_quantity <>", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityGreaterThan(Integer value) {
            addCriterion("operation_quantity >", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("operation_quantity >=", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityLessThan(Integer value) {
            addCriterion("operation_quantity <", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("operation_quantity <=", value, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityIn(List<Integer> values) {
            addCriterion("operation_quantity in", values, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityNotIn(List<Integer> values) {
            addCriterion("operation_quantity not in", values, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityBetween(Integer value1, Integer value2) {
            addCriterion("operation_quantity between", value1, value2, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andOperationQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("operation_quantity not between", value1, value2, "operationQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityIsNull() {
            addCriterion("actual_quantity is null");
            return (Criteria) this;
        }

        public Criteria andActualQuantityIsNotNull() {
            addCriterion("actual_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andActualQuantityEqualTo(Integer value) {
            addCriterion("actual_quantity =", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityNotEqualTo(Integer value) {
            addCriterion("actual_quantity <>", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityGreaterThan(Integer value) {
            addCriterion("actual_quantity >", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("actual_quantity >=", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityLessThan(Integer value) {
            addCriterion("actual_quantity <", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("actual_quantity <=", value, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityIn(List<Integer> values) {
            addCriterion("actual_quantity in", values, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityNotIn(List<Integer> values) {
            addCriterion("actual_quantity not in", values, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityBetween(Integer value1, Integer value2) {
            addCriterion("actual_quantity between", value1, value2, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andActualQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("actual_quantity not between", value1, value2, "actualQuantity");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffIsNull() {
            addCriterion("quantity_diff is null");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffIsNotNull() {
            addCriterion("quantity_diff is not null");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffEqualTo(Integer value) {
            addCriterion("quantity_diff =", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffNotEqualTo(Integer value) {
            addCriterion("quantity_diff <>", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffGreaterThan(Integer value) {
            addCriterion("quantity_diff >", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffGreaterThanOrEqualTo(Integer value) {
            addCriterion("quantity_diff >=", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffLessThan(Integer value) {
            addCriterion("quantity_diff <", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffLessThanOrEqualTo(Integer value) {
            addCriterion("quantity_diff <=", value, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffIn(List<Integer> values) {
            addCriterion("quantity_diff in", values, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffNotIn(List<Integer> values) {
            addCriterion("quantity_diff not in", values, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffBetween(Integer value1, Integer value2) {
            addCriterion("quantity_diff between", value1, value2, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andQuantityDiffNotBetween(Integer value1, Integer value2) {
            addCriterion("quantity_diff not between", value1, value2, "quantityDiff");
            return (Criteria) this;
        }

        public Criteria andDiffReasonIsNull() {
            addCriterion("diff_reason is null");
            return (Criteria) this;
        }

        public Criteria andDiffReasonIsNotNull() {
            addCriterion("diff_reason is not null");
            return (Criteria) this;
        }

        public Criteria andDiffReasonEqualTo(String value) {
            addCriterion("diff_reason =", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonNotEqualTo(String value) {
            addCriterion("diff_reason <>", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonGreaterThan(String value) {
            addCriterion("diff_reason >", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonGreaterThanOrEqualTo(String value) {
            addCriterion("diff_reason >=", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonLessThan(String value) {
            addCriterion("diff_reason <", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonLessThanOrEqualTo(String value) {
            addCriterion("diff_reason <=", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonLike(String value) {
            addCriterion("diff_reason like", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonNotLike(String value) {
            addCriterion("diff_reason not like", value, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonIn(List<String> values) {
            addCriterion("diff_reason in", values, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonNotIn(List<String> values) {
            addCriterion("diff_reason not in", values, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonBetween(String value1, String value2) {
            addCriterion("diff_reason between", value1, value2, "diffReason");
            return (Criteria) this;
        }

        public Criteria andDiffReasonNotBetween(String value1, String value2) {
            addCriterion("diff_reason not between", value1, value2, "diffReason");
            return (Criteria) this;
        }

        public Criteria andBeforeStockIsNull() {
            addCriterion("before_stock is null");
            return (Criteria) this;
        }

        public Criteria andBeforeStockIsNotNull() {
            addCriterion("before_stock is not null");
            return (Criteria) this;
        }

        public Criteria andBeforeStockEqualTo(Integer value) {
            addCriterion("before_stock =", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockNotEqualTo(Integer value) {
            addCriterion("before_stock <>", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockGreaterThan(Integer value) {
            addCriterion("before_stock >", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("before_stock >=", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockLessThan(Integer value) {
            addCriterion("before_stock <", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockLessThanOrEqualTo(Integer value) {
            addCriterion("before_stock <=", value, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockIn(List<Integer> values) {
            addCriterion("before_stock in", values, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockNotIn(List<Integer> values) {
            addCriterion("before_stock not in", values, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockBetween(Integer value1, Integer value2) {
            addCriterion("before_stock between", value1, value2, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andBeforeStockNotBetween(Integer value1, Integer value2) {
            addCriterion("before_stock not between", value1, value2, "beforeStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockIsNull() {
            addCriterion("after_stock is null");
            return (Criteria) this;
        }

        public Criteria andAfterStockIsNotNull() {
            addCriterion("after_stock is not null");
            return (Criteria) this;
        }

        public Criteria andAfterStockEqualTo(Integer value) {
            addCriterion("after_stock =", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockNotEqualTo(Integer value) {
            addCriterion("after_stock <>", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockGreaterThan(Integer value) {
            addCriterion("after_stock >", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("after_stock >=", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockLessThan(Integer value) {
            addCriterion("after_stock <", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockLessThanOrEqualTo(Integer value) {
            addCriterion("after_stock <=", value, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockIn(List<Integer> values) {
            addCriterion("after_stock in", values, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockNotIn(List<Integer> values) {
            addCriterion("after_stock not in", values, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockBetween(Integer value1, Integer value2) {
            addCriterion("after_stock between", value1, value2, "afterStock");
            return (Criteria) this;
        }

        public Criteria andAfterStockNotBetween(Integer value1, Integer value2) {
            addCriterion("after_stock not between", value1, value2, "afterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockIsNull() {
            addCriterion("actual_after_stock is null");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockIsNotNull() {
            addCriterion("actual_after_stock is not null");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockEqualTo(Integer value) {
            addCriterion("actual_after_stock =", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockNotEqualTo(Integer value) {
            addCriterion("actual_after_stock <>", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockGreaterThan(Integer value) {
            addCriterion("actual_after_stock >", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("actual_after_stock >=", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockLessThan(Integer value) {
            addCriterion("actual_after_stock <", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockLessThanOrEqualTo(Integer value) {
            addCriterion("actual_after_stock <=", value, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockIn(List<Integer> values) {
            addCriterion("actual_after_stock in", values, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockNotIn(List<Integer> values) {
            addCriterion("actual_after_stock not in", values, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockBetween(Integer value1, Integer value2) {
            addCriterion("actual_after_stock between", value1, value2, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andActualAfterStockNotBetween(Integer value1, Integer value2) {
            addCriterion("actual_after_stock not between", value1, value2, "actualAfterStock");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNull() {
            addCriterion("store_id is null");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNotNull() {
            addCriterion("store_id is not null");
            return (Criteria) this;
        }

        public Criteria andStoreIdEqualTo(Long value) {
            addCriterion("store_id =", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotEqualTo(Long value) {
            addCriterion("store_id <>", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThan(Long value) {
            addCriterion("store_id >", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("store_id >=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThan(Long value) {
            addCriterion("store_id <", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("store_id <=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdIn(List<Long> values) {
            addCriterion("store_id in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotIn(List<Long> values) {
            addCriterion("store_id not in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdBetween(Long value1, Long value2) {
            addCriterion("store_id between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("store_id not between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}