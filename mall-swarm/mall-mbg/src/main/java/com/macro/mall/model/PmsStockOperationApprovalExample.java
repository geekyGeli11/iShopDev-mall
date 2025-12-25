package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsStockOperationApprovalExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsStockOperationApprovalExample() {
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

        public Criteria andOperationTypeIsNull() {
            addCriterion("operation_type is null");
            return (Criteria) this;
        }

        public Criteria andOperationTypeIsNotNull() {
            addCriterion("operation_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperationTypeEqualTo(Byte value) {
            addCriterion("operation_type =", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotEqualTo(Byte value) {
            addCriterion("operation_type <>", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeGreaterThan(Byte value) {
            addCriterion("operation_type >", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("operation_type >=", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeLessThan(Byte value) {
            addCriterion("operation_type <", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeLessThanOrEqualTo(Byte value) {
            addCriterion("operation_type <=", value, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeIn(List<Byte> values) {
            addCriterion("operation_type in", values, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotIn(List<Byte> values) {
            addCriterion("operation_type not in", values, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeBetween(Byte value1, Byte value2) {
            addCriterion("operation_type between", value1, value2, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("operation_type not between", value1, value2, "operationType");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeIsNull() {
            addCriterion("operation_subtype is null");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeIsNotNull() {
            addCriterion("operation_subtype is not null");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeEqualTo(Byte value) {
            addCriterion("operation_subtype =", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeNotEqualTo(Byte value) {
            addCriterion("operation_subtype <>", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeGreaterThan(Byte value) {
            addCriterion("operation_subtype >", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("operation_subtype >=", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeLessThan(Byte value) {
            addCriterion("operation_subtype <", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeLessThanOrEqualTo(Byte value) {
            addCriterion("operation_subtype <=", value, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeIn(List<Byte> values) {
            addCriterion("operation_subtype in", values, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeNotIn(List<Byte> values) {
            addCriterion("operation_subtype not in", values, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeBetween(Byte value1, Byte value2) {
            addCriterion("operation_subtype between", value1, value2, "operationSubtype");
            return (Criteria) this;
        }

        public Criteria andOperationSubtypeNotBetween(Byte value1, Byte value2) {
            addCriterion("operation_subtype not between", value1, value2, "operationSubtype");
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

        public Criteria andFromStoreIdIsNull() {
            addCriterion("from_store_id is null");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdIsNotNull() {
            addCriterion("from_store_id is not null");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdEqualTo(Long value) {
            addCriterion("from_store_id =", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdNotEqualTo(Long value) {
            addCriterion("from_store_id <>", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdGreaterThan(Long value) {
            addCriterion("from_store_id >", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("from_store_id >=", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdLessThan(Long value) {
            addCriterion("from_store_id <", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("from_store_id <=", value, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdIn(List<Long> values) {
            addCriterion("from_store_id in", values, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdNotIn(List<Long> values) {
            addCriterion("from_store_id not in", values, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdBetween(Long value1, Long value2) {
            addCriterion("from_store_id between", value1, value2, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andFromStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("from_store_id not between", value1, value2, "fromStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdIsNull() {
            addCriterion("to_store_id is null");
            return (Criteria) this;
        }

        public Criteria andToStoreIdIsNotNull() {
            addCriterion("to_store_id is not null");
            return (Criteria) this;
        }

        public Criteria andToStoreIdEqualTo(Long value) {
            addCriterion("to_store_id =", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdNotEqualTo(Long value) {
            addCriterion("to_store_id <>", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdGreaterThan(Long value) {
            addCriterion("to_store_id >", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("to_store_id >=", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdLessThan(Long value) {
            addCriterion("to_store_id <", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("to_store_id <=", value, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdIn(List<Long> values) {
            addCriterion("to_store_id in", values, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdNotIn(List<Long> values) {
            addCriterion("to_store_id not in", values, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdBetween(Long value1, Long value2) {
            addCriterion("to_store_id between", value1, value2, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andToStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("to_store_id not between", value1, value2, "toStoreId");
            return (Criteria) this;
        }

        public Criteria andTotalItemsIsNull() {
            addCriterion("total_items is null");
            return (Criteria) this;
        }

        public Criteria andTotalItemsIsNotNull() {
            addCriterion("total_items is not null");
            return (Criteria) this;
        }

        public Criteria andTotalItemsEqualTo(Integer value) {
            addCriterion("total_items =", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsNotEqualTo(Integer value) {
            addCriterion("total_items <>", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsGreaterThan(Integer value) {
            addCriterion("total_items >", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_items >=", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsLessThan(Integer value) {
            addCriterion("total_items <", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsLessThanOrEqualTo(Integer value) {
            addCriterion("total_items <=", value, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsIn(List<Integer> values) {
            addCriterion("total_items in", values, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsNotIn(List<Integer> values) {
            addCriterion("total_items not in", values, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsBetween(Integer value1, Integer value2) {
            addCriterion("total_items between", value1, value2, "totalItems");
            return (Criteria) this;
        }

        public Criteria andTotalItemsNotBetween(Integer value1, Integer value2) {
            addCriterion("total_items not between", value1, value2, "totalItems");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusIsNull() {
            addCriterion("approval_status is null");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusIsNotNull() {
            addCriterion("approval_status is not null");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusEqualTo(Byte value) {
            addCriterion("approval_status =", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusNotEqualTo(Byte value) {
            addCriterion("approval_status <>", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusGreaterThan(Byte value) {
            addCriterion("approval_status >", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("approval_status >=", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusLessThan(Byte value) {
            addCriterion("approval_status <", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusLessThanOrEqualTo(Byte value) {
            addCriterion("approval_status <=", value, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusIn(List<Byte> values) {
            addCriterion("approval_status in", values, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusNotIn(List<Byte> values) {
            addCriterion("approval_status not in", values, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusBetween(Byte value1, Byte value2) {
            addCriterion("approval_status between", value1, value2, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApprovalStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("approval_status not between", value1, value2, "approvalStatus");
            return (Criteria) this;
        }

        public Criteria andApproverIdIsNull() {
            addCriterion("approver_id is null");
            return (Criteria) this;
        }

        public Criteria andApproverIdIsNotNull() {
            addCriterion("approver_id is not null");
            return (Criteria) this;
        }

        public Criteria andApproverIdEqualTo(Long value) {
            addCriterion("approver_id =", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdNotEqualTo(Long value) {
            addCriterion("approver_id <>", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdGreaterThan(Long value) {
            addCriterion("approver_id >", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdGreaterThanOrEqualTo(Long value) {
            addCriterion("approver_id >=", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdLessThan(Long value) {
            addCriterion("approver_id <", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdLessThanOrEqualTo(Long value) {
            addCriterion("approver_id <=", value, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdIn(List<Long> values) {
            addCriterion("approver_id in", values, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdNotIn(List<Long> values) {
            addCriterion("approver_id not in", values, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdBetween(Long value1, Long value2) {
            addCriterion("approver_id between", value1, value2, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverIdNotBetween(Long value1, Long value2) {
            addCriterion("approver_id not between", value1, value2, "approverId");
            return (Criteria) this;
        }

        public Criteria andApproverNameIsNull() {
            addCriterion("approver_name is null");
            return (Criteria) this;
        }

        public Criteria andApproverNameIsNotNull() {
            addCriterion("approver_name is not null");
            return (Criteria) this;
        }

        public Criteria andApproverNameEqualTo(String value) {
            addCriterion("approver_name =", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameNotEqualTo(String value) {
            addCriterion("approver_name <>", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameGreaterThan(String value) {
            addCriterion("approver_name >", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameGreaterThanOrEqualTo(String value) {
            addCriterion("approver_name >=", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameLessThan(String value) {
            addCriterion("approver_name <", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameLessThanOrEqualTo(String value) {
            addCriterion("approver_name <=", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameLike(String value) {
            addCriterion("approver_name like", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameNotLike(String value) {
            addCriterion("approver_name not like", value, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameIn(List<String> values) {
            addCriterion("approver_name in", values, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameNotIn(List<String> values) {
            addCriterion("approver_name not in", values, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameBetween(String value1, String value2) {
            addCriterion("approver_name between", value1, value2, "approverName");
            return (Criteria) this;
        }

        public Criteria andApproverNameNotBetween(String value1, String value2) {
            addCriterion("approver_name not between", value1, value2, "approverName");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeIsNull() {
            addCriterion("approval_time is null");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeIsNotNull() {
            addCriterion("approval_time is not null");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeEqualTo(Date value) {
            addCriterion("approval_time =", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeNotEqualTo(Date value) {
            addCriterion("approval_time <>", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeGreaterThan(Date value) {
            addCriterion("approval_time >", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("approval_time >=", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeLessThan(Date value) {
            addCriterion("approval_time <", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeLessThanOrEqualTo(Date value) {
            addCriterion("approval_time <=", value, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeIn(List<Date> values) {
            addCriterion("approval_time in", values, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeNotIn(List<Date> values) {
            addCriterion("approval_time not in", values, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeBetween(Date value1, Date value2) {
            addCriterion("approval_time between", value1, value2, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andApprovalTimeNotBetween(Date value1, Date value2) {
            addCriterion("approval_time not between", value1, value2, "approvalTime");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIsNull() {
            addCriterion("operator_id is null");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIsNotNull() {
            addCriterion("operator_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorIdEqualTo(Long value) {
            addCriterion("operator_id =", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotEqualTo(Long value) {
            addCriterion("operator_id <>", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdGreaterThan(Long value) {
            addCriterion("operator_id >", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operator_id >=", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdLessThan(Long value) {
            addCriterion("operator_id <", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdLessThanOrEqualTo(Long value) {
            addCriterion("operator_id <=", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIn(List<Long> values) {
            addCriterion("operator_id in", values, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotIn(List<Long> values) {
            addCriterion("operator_id not in", values, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdBetween(Long value1, Long value2) {
            addCriterion("operator_id between", value1, value2, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotBetween(Long value1, Long value2) {
            addCriterion("operator_id not between", value1, value2, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNull() {
            addCriterion("operator_name is null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNotNull() {
            addCriterion("operator_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameEqualTo(String value) {
            addCriterion("operator_name =", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotEqualTo(String value) {
            addCriterion("operator_name <>", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThan(String value) {
            addCriterion("operator_name >", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThanOrEqualTo(String value) {
            addCriterion("operator_name >=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThan(String value) {
            addCriterion("operator_name <", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThanOrEqualTo(String value) {
            addCriterion("operator_name <=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLike(String value) {
            addCriterion("operator_name like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotLike(String value) {
            addCriterion("operator_name not like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIn(List<String> values) {
            addCriterion("operator_name in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotIn(List<String> values) {
            addCriterion("operator_name not in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameBetween(String value1, String value2) {
            addCriterion("operator_name between", value1, value2, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotBetween(String value1, String value2) {
            addCriterion("operator_name not between", value1, value2, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorIpIsNull() {
            addCriterion("operator_ip is null");
            return (Criteria) this;
        }

        public Criteria andOperatorIpIsNotNull() {
            addCriterion("operator_ip is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorIpEqualTo(String value) {
            addCriterion("operator_ip =", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpNotEqualTo(String value) {
            addCriterion("operator_ip <>", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpGreaterThan(String value) {
            addCriterion("operator_ip >", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpGreaterThanOrEqualTo(String value) {
            addCriterion("operator_ip >=", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpLessThan(String value) {
            addCriterion("operator_ip <", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpLessThanOrEqualTo(String value) {
            addCriterion("operator_ip <=", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpLike(String value) {
            addCriterion("operator_ip like", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpNotLike(String value) {
            addCriterion("operator_ip not like", value, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpIn(List<String> values) {
            addCriterion("operator_ip in", values, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpNotIn(List<String> values) {
            addCriterion("operator_ip not in", values, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpBetween(String value1, String value2) {
            addCriterion("operator_ip between", value1, value2, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andOperatorIpNotBetween(String value1, String value2) {
            addCriterion("operator_ip not between", value1, value2, "operatorIp");
            return (Criteria) this;
        }

        public Criteria andIsExecutedIsNull() {
            addCriterion("is_executed is null");
            return (Criteria) this;
        }

        public Criteria andIsExecutedIsNotNull() {
            addCriterion("is_executed is not null");
            return (Criteria) this;
        }

        public Criteria andIsExecutedEqualTo(Byte value) {
            addCriterion("is_executed =", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedNotEqualTo(Byte value) {
            addCriterion("is_executed <>", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedGreaterThan(Byte value) {
            addCriterion("is_executed >", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_executed >=", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedLessThan(Byte value) {
            addCriterion("is_executed <", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedLessThanOrEqualTo(Byte value) {
            addCriterion("is_executed <=", value, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedIn(List<Byte> values) {
            addCriterion("is_executed in", values, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedNotIn(List<Byte> values) {
            addCriterion("is_executed not in", values, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedBetween(Byte value1, Byte value2) {
            addCriterion("is_executed between", value1, value2, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andIsExecutedNotBetween(Byte value1, Byte value2) {
            addCriterion("is_executed not between", value1, value2, "isExecuted");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeIsNull() {
            addCriterion("executed_time is null");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeIsNotNull() {
            addCriterion("executed_time is not null");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeEqualTo(Date value) {
            addCriterion("executed_time =", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeNotEqualTo(Date value) {
            addCriterion("executed_time <>", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeGreaterThan(Date value) {
            addCriterion("executed_time >", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("executed_time >=", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeLessThan(Date value) {
            addCriterion("executed_time <", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeLessThanOrEqualTo(Date value) {
            addCriterion("executed_time <=", value, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeIn(List<Date> values) {
            addCriterion("executed_time in", values, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeNotIn(List<Date> values) {
            addCriterion("executed_time not in", values, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeBetween(Date value1, Date value2) {
            addCriterion("executed_time between", value1, value2, "executedTime");
            return (Criteria) this;
        }

        public Criteria andExecutedTimeNotBetween(Date value1, Date value2) {
            addCriterion("executed_time not between", value1, value2, "executedTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeIsNull() {
            addCriterion("ship_time is null");
            return (Criteria) this;
        }

        public Criteria andShipTimeIsNotNull() {
            addCriterion("ship_time is not null");
            return (Criteria) this;
        }

        public Criteria andShipTimeEqualTo(Date value) {
            addCriterion("ship_time =", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeNotEqualTo(Date value) {
            addCriterion("ship_time <>", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeGreaterThan(Date value) {
            addCriterion("ship_time >", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("ship_time >=", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeLessThan(Date value) {
            addCriterion("ship_time <", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeLessThanOrEqualTo(Date value) {
            addCriterion("ship_time <=", value, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeIn(List<Date> values) {
            addCriterion("ship_time in", values, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeNotIn(List<Date> values) {
            addCriterion("ship_time not in", values, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeBetween(Date value1, Date value2) {
            addCriterion("ship_time between", value1, value2, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipTimeNotBetween(Date value1, Date value2) {
            addCriterion("ship_time not between", value1, value2, "shipTime");
            return (Criteria) this;
        }

        public Criteria andShipperIdIsNull() {
            addCriterion("shipper_id is null");
            return (Criteria) this;
        }

        public Criteria andShipperIdIsNotNull() {
            addCriterion("shipper_id is not null");
            return (Criteria) this;
        }

        public Criteria andShipperIdEqualTo(Long value) {
            addCriterion("shipper_id =", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdNotEqualTo(Long value) {
            addCriterion("shipper_id <>", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdGreaterThan(Long value) {
            addCriterion("shipper_id >", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdGreaterThanOrEqualTo(Long value) {
            addCriterion("shipper_id >=", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdLessThan(Long value) {
            addCriterion("shipper_id <", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdLessThanOrEqualTo(Long value) {
            addCriterion("shipper_id <=", value, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdIn(List<Long> values) {
            addCriterion("shipper_id in", values, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdNotIn(List<Long> values) {
            addCriterion("shipper_id not in", values, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdBetween(Long value1, Long value2) {
            addCriterion("shipper_id between", value1, value2, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperIdNotBetween(Long value1, Long value2) {
            addCriterion("shipper_id not between", value1, value2, "shipperId");
            return (Criteria) this;
        }

        public Criteria andShipperNameIsNull() {
            addCriterion("shipper_name is null");
            return (Criteria) this;
        }

        public Criteria andShipperNameIsNotNull() {
            addCriterion("shipper_name is not null");
            return (Criteria) this;
        }

        public Criteria andShipperNameEqualTo(String value) {
            addCriterion("shipper_name =", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameNotEqualTo(String value) {
            addCriterion("shipper_name <>", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameGreaterThan(String value) {
            addCriterion("shipper_name >", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameGreaterThanOrEqualTo(String value) {
            addCriterion("shipper_name >=", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameLessThan(String value) {
            addCriterion("shipper_name <", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameLessThanOrEqualTo(String value) {
            addCriterion("shipper_name <=", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameLike(String value) {
            addCriterion("shipper_name like", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameNotLike(String value) {
            addCriterion("shipper_name not like", value, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameIn(List<String> values) {
            addCriterion("shipper_name in", values, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameNotIn(List<String> values) {
            addCriterion("shipper_name not in", values, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameBetween(String value1, String value2) {
            addCriterion("shipper_name between", value1, value2, "shipperName");
            return (Criteria) this;
        }

        public Criteria andShipperNameNotBetween(String value1, String value2) {
            addCriterion("shipper_name not between", value1, value2, "shipperName");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNull() {
            addCriterion("confirm_time is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNotNull() {
            addCriterion("confirm_time is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeEqualTo(Date value) {
            addCriterion("confirm_time =", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotEqualTo(Date value) {
            addCriterion("confirm_time <>", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThan(Date value) {
            addCriterion("confirm_time >", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("confirm_time >=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThan(Date value) {
            addCriterion("confirm_time <", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThanOrEqualTo(Date value) {
            addCriterion("confirm_time <=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIn(List<Date> values) {
            addCriterion("confirm_time in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotIn(List<Date> values) {
            addCriterion("confirm_time not in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeBetween(Date value1, Date value2) {
            addCriterion("confirm_time between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotBetween(Date value1, Date value2) {
            addCriterion("confirm_time not between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdIsNull() {
            addCriterion("confirmer_id is null");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdIsNotNull() {
            addCriterion("confirmer_id is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdEqualTo(Long value) {
            addCriterion("confirmer_id =", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdNotEqualTo(Long value) {
            addCriterion("confirmer_id <>", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdGreaterThan(Long value) {
            addCriterion("confirmer_id >", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdGreaterThanOrEqualTo(Long value) {
            addCriterion("confirmer_id >=", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdLessThan(Long value) {
            addCriterion("confirmer_id <", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdLessThanOrEqualTo(Long value) {
            addCriterion("confirmer_id <=", value, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdIn(List<Long> values) {
            addCriterion("confirmer_id in", values, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdNotIn(List<Long> values) {
            addCriterion("confirmer_id not in", values, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdBetween(Long value1, Long value2) {
            addCriterion("confirmer_id between", value1, value2, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerIdNotBetween(Long value1, Long value2) {
            addCriterion("confirmer_id not between", value1, value2, "confirmerId");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameIsNull() {
            addCriterion("confirmer_name is null");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameIsNotNull() {
            addCriterion("confirmer_name is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameEqualTo(String value) {
            addCriterion("confirmer_name =", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameNotEqualTo(String value) {
            addCriterion("confirmer_name <>", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameGreaterThan(String value) {
            addCriterion("confirmer_name >", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameGreaterThanOrEqualTo(String value) {
            addCriterion("confirmer_name >=", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameLessThan(String value) {
            addCriterion("confirmer_name <", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameLessThanOrEqualTo(String value) {
            addCriterion("confirmer_name <=", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameLike(String value) {
            addCriterion("confirmer_name like", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameNotLike(String value) {
            addCriterion("confirmer_name not like", value, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameIn(List<String> values) {
            addCriterion("confirmer_name in", values, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameNotIn(List<String> values) {
            addCriterion("confirmer_name not in", values, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameBetween(String value1, String value2) {
            addCriterion("confirmer_name between", value1, value2, "confirmerName");
            return (Criteria) this;
        }

        public Criteria andConfirmerNameNotBetween(String value1, String value2) {
            addCriterion("confirmer_name not between", value1, value2, "confirmerName");
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

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(Date value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(Date value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(Date value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(Date value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<Date> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<Date> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
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