package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsProductDamageReportExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsProductDamageReportExample() {
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

        public Criteria andReportSnIsNull() {
            addCriterion("report_sn is null");
            return (Criteria) this;
        }

        public Criteria andReportSnIsNotNull() {
            addCriterion("report_sn is not null");
            return (Criteria) this;
        }

        public Criteria andReportSnEqualTo(String value) {
            addCriterion("report_sn =", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnNotEqualTo(String value) {
            addCriterion("report_sn <>", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnGreaterThan(String value) {
            addCriterion("report_sn >", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnGreaterThanOrEqualTo(String value) {
            addCriterion("report_sn >=", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnLessThan(String value) {
            addCriterion("report_sn <", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnLessThanOrEqualTo(String value) {
            addCriterion("report_sn <=", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnLike(String value) {
            addCriterion("report_sn like", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnNotLike(String value) {
            addCriterion("report_sn not like", value, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnIn(List<String> values) {
            addCriterion("report_sn in", values, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnNotIn(List<String> values) {
            addCriterion("report_sn not in", values, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnBetween(String value1, String value2) {
            addCriterion("report_sn between", value1, value2, "reportSn");
            return (Criteria) this;
        }

        public Criteria andReportSnNotBetween(String value1, String value2) {
            addCriterion("report_sn not between", value1, value2, "reportSn");
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

        public Criteria andStoreNameIsNull() {
            addCriterion("store_name is null");
            return (Criteria) this;
        }

        public Criteria andStoreNameIsNotNull() {
            addCriterion("store_name is not null");
            return (Criteria) this;
        }

        public Criteria andStoreNameEqualTo(String value) {
            addCriterion("store_name =", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotEqualTo(String value) {
            addCriterion("store_name <>", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameGreaterThan(String value) {
            addCriterion("store_name >", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameGreaterThanOrEqualTo(String value) {
            addCriterion("store_name >=", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLessThan(String value) {
            addCriterion("store_name <", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLessThanOrEqualTo(String value) {
            addCriterion("store_name <=", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLike(String value) {
            addCriterion("store_name like", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotLike(String value) {
            addCriterion("store_name not like", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameIn(List<String> values) {
            addCriterion("store_name in", values, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotIn(List<String> values) {
            addCriterion("store_name not in", values, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameBetween(String value1, String value2) {
            addCriterion("store_name between", value1, value2, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotBetween(String value1, String value2) {
            addCriterion("store_name not between", value1, value2, "storeName");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityIsNull() {
            addCriterion("total_quantity is null");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityIsNotNull() {
            addCriterion("total_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityEqualTo(Integer value) {
            addCriterion("total_quantity =", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityNotEqualTo(Integer value) {
            addCriterion("total_quantity <>", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityGreaterThan(Integer value) {
            addCriterion("total_quantity >", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_quantity >=", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityLessThan(Integer value) {
            addCriterion("total_quantity <", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("total_quantity <=", value, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityIn(List<Integer> values) {
            addCriterion("total_quantity in", values, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityNotIn(List<Integer> values) {
            addCriterion("total_quantity not in", values, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityBetween(Integer value1, Integer value2) {
            addCriterion("total_quantity between", value1, value2, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("total_quantity not between", value1, value2, "totalQuantity");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountIsNull() {
            addCriterion("total_damage_amount is null");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountIsNotNull() {
            addCriterion("total_damage_amount is not null");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountEqualTo(BigDecimal value) {
            addCriterion("total_damage_amount =", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountNotEqualTo(BigDecimal value) {
            addCriterion("total_damage_amount <>", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountGreaterThan(BigDecimal value) {
            addCriterion("total_damage_amount >", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_damage_amount >=", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountLessThan(BigDecimal value) {
            addCriterion("total_damage_amount <", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_damage_amount <=", value, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountIn(List<BigDecimal> values) {
            addCriterion("total_damage_amount in", values, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountNotIn(List<BigDecimal> values) {
            addCriterion("total_damage_amount not in", values, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_damage_amount between", value1, value2, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalDamageAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_damage_amount not between", value1, value2, "totalDamageAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountIsNull() {
            addCriterion("total_sales_amount is null");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountIsNotNull() {
            addCriterion("total_sales_amount is not null");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountEqualTo(BigDecimal value) {
            addCriterion("total_sales_amount =", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountNotEqualTo(BigDecimal value) {
            addCriterion("total_sales_amount <>", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountGreaterThan(BigDecimal value) {
            addCriterion("total_sales_amount >", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_sales_amount >=", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountLessThan(BigDecimal value) {
            addCriterion("total_sales_amount <", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_sales_amount <=", value, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountIn(List<BigDecimal> values) {
            addCriterion("total_sales_amount in", values, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountNotIn(List<BigDecimal> values) {
            addCriterion("total_sales_amount not in", values, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_sales_amount between", value1, value2, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andTotalSalesAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_sales_amount not between", value1, value2, "totalSalesAmount");
            return (Criteria) this;
        }

        public Criteria andDamageTypeIsNull() {
            addCriterion("damage_type is null");
            return (Criteria) this;
        }

        public Criteria andDamageTypeIsNotNull() {
            addCriterion("damage_type is not null");
            return (Criteria) this;
        }

        public Criteria andDamageTypeEqualTo(Byte value) {
            addCriterion("damage_type =", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeNotEqualTo(Byte value) {
            addCriterion("damage_type <>", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeGreaterThan(Byte value) {
            addCriterion("damage_type >", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("damage_type >=", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeLessThan(Byte value) {
            addCriterion("damage_type <", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeLessThanOrEqualTo(Byte value) {
            addCriterion("damage_type <=", value, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeIn(List<Byte> values) {
            addCriterion("damage_type in", values, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeNotIn(List<Byte> values) {
            addCriterion("damage_type not in", values, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeBetween(Byte value1, Byte value2) {
            addCriterion("damage_type between", value1, value2, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("damage_type not between", value1, value2, "damageType");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdIsNull() {
            addCriterion("damage_reason_id is null");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdIsNotNull() {
            addCriterion("damage_reason_id is not null");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdEqualTo(Long value) {
            addCriterion("damage_reason_id =", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdNotEqualTo(Long value) {
            addCriterion("damage_reason_id <>", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdGreaterThan(Long value) {
            addCriterion("damage_reason_id >", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdGreaterThanOrEqualTo(Long value) {
            addCriterion("damage_reason_id >=", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdLessThan(Long value) {
            addCriterion("damage_reason_id <", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdLessThanOrEqualTo(Long value) {
            addCriterion("damage_reason_id <=", value, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdIn(List<Long> values) {
            addCriterion("damage_reason_id in", values, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdNotIn(List<Long> values) {
            addCriterion("damage_reason_id not in", values, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdBetween(Long value1, Long value2) {
            addCriterion("damage_reason_id between", value1, value2, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIdNotBetween(Long value1, Long value2) {
            addCriterion("damage_reason_id not between", value1, value2, "damageReasonId");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIsNull() {
            addCriterion("damage_reason is null");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIsNotNull() {
            addCriterion("damage_reason is not null");
            return (Criteria) this;
        }

        public Criteria andDamageReasonEqualTo(String value) {
            addCriterion("damage_reason =", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonNotEqualTo(String value) {
            addCriterion("damage_reason <>", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonGreaterThan(String value) {
            addCriterion("damage_reason >", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonGreaterThanOrEqualTo(String value) {
            addCriterion("damage_reason >=", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonLessThan(String value) {
            addCriterion("damage_reason <", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonLessThanOrEqualTo(String value) {
            addCriterion("damage_reason <=", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonLike(String value) {
            addCriterion("damage_reason like", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonNotLike(String value) {
            addCriterion("damage_reason not like", value, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonIn(List<String> values) {
            addCriterion("damage_reason in", values, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonNotIn(List<String> values) {
            addCriterion("damage_reason not in", values, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonBetween(String value1, String value2) {
            addCriterion("damage_reason between", value1, value2, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamageReasonNotBetween(String value1, String value2) {
            addCriterion("damage_reason not between", value1, value2, "damageReason");
            return (Criteria) this;
        }

        public Criteria andDamagePicsIsNull() {
            addCriterion("damage_pics is null");
            return (Criteria) this;
        }

        public Criteria andDamagePicsIsNotNull() {
            addCriterion("damage_pics is not null");
            return (Criteria) this;
        }

        public Criteria andDamagePicsEqualTo(String value) {
            addCriterion("damage_pics =", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsNotEqualTo(String value) {
            addCriterion("damage_pics <>", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsGreaterThan(String value) {
            addCriterion("damage_pics >", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsGreaterThanOrEqualTo(String value) {
            addCriterion("damage_pics >=", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsLessThan(String value) {
            addCriterion("damage_pics <", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsLessThanOrEqualTo(String value) {
            addCriterion("damage_pics <=", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsLike(String value) {
            addCriterion("damage_pics like", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsNotLike(String value) {
            addCriterion("damage_pics not like", value, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsIn(List<String> values) {
            addCriterion("damage_pics in", values, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsNotIn(List<String> values) {
            addCriterion("damage_pics not in", values, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsBetween(String value1, String value2) {
            addCriterion("damage_pics between", value1, value2, "damagePics");
            return (Criteria) this;
        }

        public Criteria andDamagePicsNotBetween(String value1, String value2) {
            addCriterion("damage_pics not between", value1, value2, "damagePics");
            return (Criteria) this;
        }

        public Criteria andHandleMethodIsNull() {
            addCriterion("handle_method is null");
            return (Criteria) this;
        }

        public Criteria andHandleMethodIsNotNull() {
            addCriterion("handle_method is not null");
            return (Criteria) this;
        }

        public Criteria andHandleMethodEqualTo(Byte value) {
            addCriterion("handle_method =", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodNotEqualTo(Byte value) {
            addCriterion("handle_method <>", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodGreaterThan(Byte value) {
            addCriterion("handle_method >", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodGreaterThanOrEqualTo(Byte value) {
            addCriterion("handle_method >=", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodLessThan(Byte value) {
            addCriterion("handle_method <", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodLessThanOrEqualTo(Byte value) {
            addCriterion("handle_method <=", value, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodIn(List<Byte> values) {
            addCriterion("handle_method in", values, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodNotIn(List<Byte> values) {
            addCriterion("handle_method not in", values, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodBetween(Byte value1, Byte value2) {
            addCriterion("handle_method between", value1, value2, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandleMethodNotBetween(Byte value1, Byte value2) {
            addCriterion("handle_method not between", value1, value2, "handleMethod");
            return (Criteria) this;
        }

        public Criteria andHandlePicsIsNull() {
            addCriterion("handle_pics is null");
            return (Criteria) this;
        }

        public Criteria andHandlePicsIsNotNull() {
            addCriterion("handle_pics is not null");
            return (Criteria) this;
        }

        public Criteria andHandlePicsEqualTo(String value) {
            addCriterion("handle_pics =", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsNotEqualTo(String value) {
            addCriterion("handle_pics <>", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsGreaterThan(String value) {
            addCriterion("handle_pics >", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsGreaterThanOrEqualTo(String value) {
            addCriterion("handle_pics >=", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsLessThan(String value) {
            addCriterion("handle_pics <", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsLessThanOrEqualTo(String value) {
            addCriterion("handle_pics <=", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsLike(String value) {
            addCriterion("handle_pics like", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsNotLike(String value) {
            addCriterion("handle_pics not like", value, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsIn(List<String> values) {
            addCriterion("handle_pics in", values, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsNotIn(List<String> values) {
            addCriterion("handle_pics not in", values, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsBetween(String value1, String value2) {
            addCriterion("handle_pics between", value1, value2, "handlePics");
            return (Criteria) this;
        }

        public Criteria andHandlePicsNotBetween(String value1, String value2) {
            addCriterion("handle_pics not between", value1, value2, "handlePics");
            return (Criteria) this;
        }

        public Criteria andSupplierNameIsNull() {
            addCriterion("supplier_name is null");
            return (Criteria) this;
        }

        public Criteria andSupplierNameIsNotNull() {
            addCriterion("supplier_name is not null");
            return (Criteria) this;
        }

        public Criteria andSupplierNameEqualTo(String value) {
            addCriterion("supplier_name =", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameNotEqualTo(String value) {
            addCriterion("supplier_name <>", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameGreaterThan(String value) {
            addCriterion("supplier_name >", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameGreaterThanOrEqualTo(String value) {
            addCriterion("supplier_name >=", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameLessThan(String value) {
            addCriterion("supplier_name <", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameLessThanOrEqualTo(String value) {
            addCriterion("supplier_name <=", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameLike(String value) {
            addCriterion("supplier_name like", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameNotLike(String value) {
            addCriterion("supplier_name not like", value, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameIn(List<String> values) {
            addCriterion("supplier_name in", values, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameNotIn(List<String> values) {
            addCriterion("supplier_name not in", values, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameBetween(String value1, String value2) {
            addCriterion("supplier_name between", value1, value2, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierNameNotBetween(String value1, String value2) {
            addCriterion("supplier_name not between", value1, value2, "supplierName");
            return (Criteria) this;
        }

        public Criteria andSupplierContactIsNull() {
            addCriterion("supplier_contact is null");
            return (Criteria) this;
        }

        public Criteria andSupplierContactIsNotNull() {
            addCriterion("supplier_contact is not null");
            return (Criteria) this;
        }

        public Criteria andSupplierContactEqualTo(String value) {
            addCriterion("supplier_contact =", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactNotEqualTo(String value) {
            addCriterion("supplier_contact <>", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactGreaterThan(String value) {
            addCriterion("supplier_contact >", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactGreaterThanOrEqualTo(String value) {
            addCriterion("supplier_contact >=", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactLessThan(String value) {
            addCriterion("supplier_contact <", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactLessThanOrEqualTo(String value) {
            addCriterion("supplier_contact <=", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactLike(String value) {
            addCriterion("supplier_contact like", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactNotLike(String value) {
            addCriterion("supplier_contact not like", value, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactIn(List<String> values) {
            addCriterion("supplier_contact in", values, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactNotIn(List<String> values) {
            addCriterion("supplier_contact not in", values, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactBetween(String value1, String value2) {
            addCriterion("supplier_contact between", value1, value2, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andSupplierContactNotBetween(String value1, String value2) {
            addCriterion("supplier_contact not between", value1, value2, "supplierContact");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnIsNull() {
            addCriterion("reshipment_sn is null");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnIsNotNull() {
            addCriterion("reshipment_sn is not null");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnEqualTo(String value) {
            addCriterion("reshipment_sn =", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnNotEqualTo(String value) {
            addCriterion("reshipment_sn <>", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnGreaterThan(String value) {
            addCriterion("reshipment_sn >", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnGreaterThanOrEqualTo(String value) {
            addCriterion("reshipment_sn >=", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnLessThan(String value) {
            addCriterion("reshipment_sn <", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnLessThanOrEqualTo(String value) {
            addCriterion("reshipment_sn <=", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnLike(String value) {
            addCriterion("reshipment_sn like", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnNotLike(String value) {
            addCriterion("reshipment_sn not like", value, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnIn(List<String> values) {
            addCriterion("reshipment_sn in", values, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnNotIn(List<String> values) {
            addCriterion("reshipment_sn not in", values, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnBetween(String value1, String value2) {
            addCriterion("reshipment_sn between", value1, value2, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentSnNotBetween(String value1, String value2) {
            addCriterion("reshipment_sn not between", value1, value2, "reshipmentSn");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeIsNull() {
            addCriterion("reshipment_time is null");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeIsNotNull() {
            addCriterion("reshipment_time is not null");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeEqualTo(Date value) {
            addCriterion("reshipment_time =", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeNotEqualTo(Date value) {
            addCriterion("reshipment_time <>", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeGreaterThan(Date value) {
            addCriterion("reshipment_time >", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("reshipment_time >=", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeLessThan(Date value) {
            addCriterion("reshipment_time <", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeLessThanOrEqualTo(Date value) {
            addCriterion("reshipment_time <=", value, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeIn(List<Date> values) {
            addCriterion("reshipment_time in", values, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeNotIn(List<Date> values) {
            addCriterion("reshipment_time not in", values, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeBetween(Date value1, Date value2) {
            addCriterion("reshipment_time between", value1, value2, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andReshipmentTimeNotBetween(Date value1, Date value2) {
            addCriterion("reshipment_time not between", value1, value2, "reshipmentTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusIsNull() {
            addCriterion("acceptance_status is null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusIsNotNull() {
            addCriterion("acceptance_status is not null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusEqualTo(Byte value) {
            addCriterion("acceptance_status =", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusNotEqualTo(Byte value) {
            addCriterion("acceptance_status <>", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusGreaterThan(Byte value) {
            addCriterion("acceptance_status >", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("acceptance_status >=", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusLessThan(Byte value) {
            addCriterion("acceptance_status <", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusLessThanOrEqualTo(Byte value) {
            addCriterion("acceptance_status <=", value, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusIn(List<Byte> values) {
            addCriterion("acceptance_status in", values, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusNotIn(List<Byte> values) {
            addCriterion("acceptance_status not in", values, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusBetween(Byte value1, Byte value2) {
            addCriterion("acceptance_status between", value1, value2, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("acceptance_status not between", value1, value2, "acceptanceStatus");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeIsNull() {
            addCriterion("acceptance_time is null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeIsNotNull() {
            addCriterion("acceptance_time is not null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeEqualTo(Date value) {
            addCriterion("acceptance_time =", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeNotEqualTo(Date value) {
            addCriterion("acceptance_time <>", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeGreaterThan(Date value) {
            addCriterion("acceptance_time >", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("acceptance_time >=", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeLessThan(Date value) {
            addCriterion("acceptance_time <", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeLessThanOrEqualTo(Date value) {
            addCriterion("acceptance_time <=", value, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeIn(List<Date> values) {
            addCriterion("acceptance_time in", values, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeNotIn(List<Date> values) {
            addCriterion("acceptance_time not in", values, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeBetween(Date value1, Date value2) {
            addCriterion("acceptance_time between", value1, value2, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceTimeNotBetween(Date value1, Date value2) {
            addCriterion("acceptance_time not between", value1, value2, "acceptanceTime");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkIsNull() {
            addCriterion("acceptance_remark is null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkIsNotNull() {
            addCriterion("acceptance_remark is not null");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkEqualTo(String value) {
            addCriterion("acceptance_remark =", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkNotEqualTo(String value) {
            addCriterion("acceptance_remark <>", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkGreaterThan(String value) {
            addCriterion("acceptance_remark >", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("acceptance_remark >=", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkLessThan(String value) {
            addCriterion("acceptance_remark <", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkLessThanOrEqualTo(String value) {
            addCriterion("acceptance_remark <=", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkLike(String value) {
            addCriterion("acceptance_remark like", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkNotLike(String value) {
            addCriterion("acceptance_remark not like", value, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkIn(List<String> values) {
            addCriterion("acceptance_remark in", values, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkNotIn(List<String> values) {
            addCriterion("acceptance_remark not in", values, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkBetween(String value1, String value2) {
            addCriterion("acceptance_remark between", value1, value2, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andAcceptanceRemarkNotBetween(String value1, String value2) {
            addCriterion("acceptance_remark not between", value1, value2, "acceptanceRemark");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeIsNull() {
            addCriterion("borrow_purpose is null");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeIsNotNull() {
            addCriterion("borrow_purpose is not null");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeEqualTo(String value) {
            addCriterion("borrow_purpose =", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeNotEqualTo(String value) {
            addCriterion("borrow_purpose <>", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeGreaterThan(String value) {
            addCriterion("borrow_purpose >", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeGreaterThanOrEqualTo(String value) {
            addCriterion("borrow_purpose >=", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeLessThan(String value) {
            addCriterion("borrow_purpose <", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeLessThanOrEqualTo(String value) {
            addCriterion("borrow_purpose <=", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeLike(String value) {
            addCriterion("borrow_purpose like", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeNotLike(String value) {
            addCriterion("borrow_purpose not like", value, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeIn(List<String> values) {
            addCriterion("borrow_purpose in", values, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeNotIn(List<String> values) {
            addCriterion("borrow_purpose not in", values, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeBetween(String value1, String value2) {
            addCriterion("borrow_purpose between", value1, value2, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPurposeNotBetween(String value1, String value2) {
            addCriterion("borrow_purpose not between", value1, value2, "borrowPurpose");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonIsNull() {
            addCriterion("borrow_person is null");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonIsNotNull() {
            addCriterion("borrow_person is not null");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonEqualTo(String value) {
            addCriterion("borrow_person =", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonNotEqualTo(String value) {
            addCriterion("borrow_person <>", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonGreaterThan(String value) {
            addCriterion("borrow_person >", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonGreaterThanOrEqualTo(String value) {
            addCriterion("borrow_person >=", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonLessThan(String value) {
            addCriterion("borrow_person <", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonLessThanOrEqualTo(String value) {
            addCriterion("borrow_person <=", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonLike(String value) {
            addCriterion("borrow_person like", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonNotLike(String value) {
            addCriterion("borrow_person not like", value, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonIn(List<String> values) {
            addCriterion("borrow_person in", values, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonNotIn(List<String> values) {
            addCriterion("borrow_person not in", values, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonBetween(String value1, String value2) {
            addCriterion("borrow_person between", value1, value2, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowPersonNotBetween(String value1, String value2) {
            addCriterion("borrow_person not between", value1, value2, "borrowPerson");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeIsNull() {
            addCriterion("borrow_time is null");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeIsNotNull() {
            addCriterion("borrow_time is not null");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeEqualTo(Date value) {
            addCriterion("borrow_time =", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeNotEqualTo(Date value) {
            addCriterion("borrow_time <>", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeGreaterThan(Date value) {
            addCriterion("borrow_time >", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("borrow_time >=", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeLessThan(Date value) {
            addCriterion("borrow_time <", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeLessThanOrEqualTo(Date value) {
            addCriterion("borrow_time <=", value, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeIn(List<Date> values) {
            addCriterion("borrow_time in", values, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeNotIn(List<Date> values) {
            addCriterion("borrow_time not in", values, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeBetween(Date value1, Date value2) {
            addCriterion("borrow_time between", value1, value2, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andBorrowTimeNotBetween(Date value1, Date value2) {
            addCriterion("borrow_time not between", value1, value2, "borrowTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeIsNull() {
            addCriterion("return_time is null");
            return (Criteria) this;
        }

        public Criteria andReturnTimeIsNotNull() {
            addCriterion("return_time is not null");
            return (Criteria) this;
        }

        public Criteria andReturnTimeEqualTo(Date value) {
            addCriterion("return_time =", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeNotEqualTo(Date value) {
            addCriterion("return_time <>", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeGreaterThan(Date value) {
            addCriterion("return_time >", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("return_time >=", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeLessThan(Date value) {
            addCriterion("return_time <", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeLessThanOrEqualTo(Date value) {
            addCriterion("return_time <=", value, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeIn(List<Date> values) {
            addCriterion("return_time in", values, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeNotIn(List<Date> values) {
            addCriterion("return_time not in", values, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeBetween(Date value1, Date value2) {
            addCriterion("return_time between", value1, value2, "returnTime");
            return (Criteria) this;
        }

        public Criteria andReturnTimeNotBetween(Date value1, Date value2) {
            addCriterion("return_time not between", value1, value2, "returnTime");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdIsNull() {
            addCriterion("submit_admin_id is null");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdIsNotNull() {
            addCriterion("submit_admin_id is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdEqualTo(Long value) {
            addCriterion("submit_admin_id =", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdNotEqualTo(Long value) {
            addCriterion("submit_admin_id <>", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdGreaterThan(Long value) {
            addCriterion("submit_admin_id >", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdGreaterThanOrEqualTo(Long value) {
            addCriterion("submit_admin_id >=", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdLessThan(Long value) {
            addCriterion("submit_admin_id <", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdLessThanOrEqualTo(Long value) {
            addCriterion("submit_admin_id <=", value, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdIn(List<Long> values) {
            addCriterion("submit_admin_id in", values, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdNotIn(List<Long> values) {
            addCriterion("submit_admin_id not in", values, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdBetween(Long value1, Long value2) {
            addCriterion("submit_admin_id between", value1, value2, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminIdNotBetween(Long value1, Long value2) {
            addCriterion("submit_admin_id not between", value1, value2, "submitAdminId");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameIsNull() {
            addCriterion("submit_admin_name is null");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameIsNotNull() {
            addCriterion("submit_admin_name is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameEqualTo(String value) {
            addCriterion("submit_admin_name =", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameNotEqualTo(String value) {
            addCriterion("submit_admin_name <>", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameGreaterThan(String value) {
            addCriterion("submit_admin_name >", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameGreaterThanOrEqualTo(String value) {
            addCriterion("submit_admin_name >=", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameLessThan(String value) {
            addCriterion("submit_admin_name <", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameLessThanOrEqualTo(String value) {
            addCriterion("submit_admin_name <=", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameLike(String value) {
            addCriterion("submit_admin_name like", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameNotLike(String value) {
            addCriterion("submit_admin_name not like", value, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameIn(List<String> values) {
            addCriterion("submit_admin_name in", values, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameNotIn(List<String> values) {
            addCriterion("submit_admin_name not in", values, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameBetween(String value1, String value2) {
            addCriterion("submit_admin_name between", value1, value2, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andSubmitAdminNameNotBetween(String value1, String value2) {
            addCriterion("submit_admin_name not between", value1, value2, "submitAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdIsNull() {
            addCriterion("handle_admin_id is null");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdIsNotNull() {
            addCriterion("handle_admin_id is not null");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdEqualTo(Long value) {
            addCriterion("handle_admin_id =", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdNotEqualTo(Long value) {
            addCriterion("handle_admin_id <>", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdGreaterThan(Long value) {
            addCriterion("handle_admin_id >", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdGreaterThanOrEqualTo(Long value) {
            addCriterion("handle_admin_id >=", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdLessThan(Long value) {
            addCriterion("handle_admin_id <", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdLessThanOrEqualTo(Long value) {
            addCriterion("handle_admin_id <=", value, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdIn(List<Long> values) {
            addCriterion("handle_admin_id in", values, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdNotIn(List<Long> values) {
            addCriterion("handle_admin_id not in", values, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdBetween(Long value1, Long value2) {
            addCriterion("handle_admin_id between", value1, value2, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminIdNotBetween(Long value1, Long value2) {
            addCriterion("handle_admin_id not between", value1, value2, "handleAdminId");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameIsNull() {
            addCriterion("handle_admin_name is null");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameIsNotNull() {
            addCriterion("handle_admin_name is not null");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameEqualTo(String value) {
            addCriterion("handle_admin_name =", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameNotEqualTo(String value) {
            addCriterion("handle_admin_name <>", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameGreaterThan(String value) {
            addCriterion("handle_admin_name >", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameGreaterThanOrEqualTo(String value) {
            addCriterion("handle_admin_name >=", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameLessThan(String value) {
            addCriterion("handle_admin_name <", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameLessThanOrEqualTo(String value) {
            addCriterion("handle_admin_name <=", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameLike(String value) {
            addCriterion("handle_admin_name like", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameNotLike(String value) {
            addCriterion("handle_admin_name not like", value, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameIn(List<String> values) {
            addCriterion("handle_admin_name in", values, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameNotIn(List<String> values) {
            addCriterion("handle_admin_name not in", values, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameBetween(String value1, String value2) {
            addCriterion("handle_admin_name between", value1, value2, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleAdminNameNotBetween(String value1, String value2) {
            addCriterion("handle_admin_name not between", value1, value2, "handleAdminName");
            return (Criteria) this;
        }

        public Criteria andHandleTimeIsNull() {
            addCriterion("handle_time is null");
            return (Criteria) this;
        }

        public Criteria andHandleTimeIsNotNull() {
            addCriterion("handle_time is not null");
            return (Criteria) this;
        }

        public Criteria andHandleTimeEqualTo(Date value) {
            addCriterion("handle_time =", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeNotEqualTo(Date value) {
            addCriterion("handle_time <>", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeGreaterThan(Date value) {
            addCriterion("handle_time >", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("handle_time >=", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeLessThan(Date value) {
            addCriterion("handle_time <", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeLessThanOrEqualTo(Date value) {
            addCriterion("handle_time <=", value, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeIn(List<Date> values) {
            addCriterion("handle_time in", values, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeNotIn(List<Date> values) {
            addCriterion("handle_time not in", values, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeBetween(Date value1, Date value2) {
            addCriterion("handle_time between", value1, value2, "handleTime");
            return (Criteria) this;
        }

        public Criteria andHandleTimeNotBetween(Date value1, Date value2) {
            addCriterion("handle_time not between", value1, value2, "handleTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeIsNull() {
            addCriterion("complete_time is null");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeIsNotNull() {
            addCriterion("complete_time is not null");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeEqualTo(Date value) {
            addCriterion("complete_time =", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotEqualTo(Date value) {
            addCriterion("complete_time <>", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeGreaterThan(Date value) {
            addCriterion("complete_time >", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("complete_time >=", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeLessThan(Date value) {
            addCriterion("complete_time <", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeLessThanOrEqualTo(Date value) {
            addCriterion("complete_time <=", value, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeIn(List<Date> values) {
            addCriterion("complete_time in", values, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotIn(List<Date> values) {
            addCriterion("complete_time not in", values, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeBetween(Date value1, Date value2) {
            addCriterion("complete_time between", value1, value2, "completeTime");
            return (Criteria) this;
        }

        public Criteria andCompleteTimeNotBetween(Date value1, Date value2) {
            addCriterion("complete_time not between", value1, value2, "completeTime");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusIsNull() {
            addCriterion("delete_status is null");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusIsNotNull() {
            addCriterion("delete_status is not null");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusEqualTo(Byte value) {
            addCriterion("delete_status =", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusNotEqualTo(Byte value) {
            addCriterion("delete_status <>", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusGreaterThan(Byte value) {
            addCriterion("delete_status >", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("delete_status >=", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusLessThan(Byte value) {
            addCriterion("delete_status <", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusLessThanOrEqualTo(Byte value) {
            addCriterion("delete_status <=", value, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusIn(List<Byte> values) {
            addCriterion("delete_status in", values, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusNotIn(List<Byte> values) {
            addCriterion("delete_status not in", values, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusBetween(Byte value1, Byte value2) {
            addCriterion("delete_status between", value1, value2, "deleteStatus");
            return (Criteria) this;
        }

        public Criteria andDeleteStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("delete_status not between", value1, value2, "deleteStatus");
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