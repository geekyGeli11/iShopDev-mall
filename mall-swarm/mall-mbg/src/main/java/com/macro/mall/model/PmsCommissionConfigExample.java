package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsCommissionConfigExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsCommissionConfigExample() {
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

        public Criteria andConfigNameIsNull() {
            addCriterion("config_name is null");
            return (Criteria) this;
        }

        public Criteria andConfigNameIsNotNull() {
            addCriterion("config_name is not null");
            return (Criteria) this;
        }

        public Criteria andConfigNameEqualTo(String value) {
            addCriterion("config_name =", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotEqualTo(String value) {
            addCriterion("config_name <>", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameGreaterThan(String value) {
            addCriterion("config_name >", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameGreaterThanOrEqualTo(String value) {
            addCriterion("config_name >=", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLessThan(String value) {
            addCriterion("config_name <", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLessThanOrEqualTo(String value) {
            addCriterion("config_name <=", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLike(String value) {
            addCriterion("config_name like", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotLike(String value) {
            addCriterion("config_name not like", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameIn(List<String> values) {
            addCriterion("config_name in", values, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotIn(List<String> values) {
            addCriterion("config_name not in", values, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameBetween(String value1, String value2) {
            addCriterion("config_name between", value1, value2, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotBetween(String value1, String value2) {
            addCriterion("config_name not between", value1, value2, "configName");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdIsNull() {
            addCriterion("product_category_id is null");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdIsNotNull() {
            addCriterion("product_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdEqualTo(Long value) {
            addCriterion("product_category_id =", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdNotEqualTo(Long value) {
            addCriterion("product_category_id <>", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdGreaterThan(Long value) {
            addCriterion("product_category_id >", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("product_category_id >=", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdLessThan(Long value) {
            addCriterion("product_category_id <", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("product_category_id <=", value, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdIn(List<Long> values) {
            addCriterion("product_category_id in", values, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdNotIn(List<Long> values) {
            addCriterion("product_category_id not in", values, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdBetween(Long value1, Long value2) {
            addCriterion("product_category_id between", value1, value2, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andProductCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("product_category_id not between", value1, value2, "productCategoryId");
            return (Criteria) this;
        }

        public Criteria andLevel1RateIsNull() {
            addCriterion("level1_rate is null");
            return (Criteria) this;
        }

        public Criteria andLevel1RateIsNotNull() {
            addCriterion("level1_rate is not null");
            return (Criteria) this;
        }

        public Criteria andLevel1RateEqualTo(BigDecimal value) {
            addCriterion("level1_rate =", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateNotEqualTo(BigDecimal value) {
            addCriterion("level1_rate <>", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateGreaterThan(BigDecimal value) {
            addCriterion("level1_rate >", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("level1_rate >=", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateLessThan(BigDecimal value) {
            addCriterion("level1_rate <", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateLessThanOrEqualTo(BigDecimal value) {
            addCriterion("level1_rate <=", value, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateIn(List<BigDecimal> values) {
            addCriterion("level1_rate in", values, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateNotIn(List<BigDecimal> values) {
            addCriterion("level1_rate not in", values, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level1_rate between", value1, value2, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1RateNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level1_rate not between", value1, value2, "level1Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateIsNull() {
            addCriterion("level2_rate is null");
            return (Criteria) this;
        }

        public Criteria andLevel2RateIsNotNull() {
            addCriterion("level2_rate is not null");
            return (Criteria) this;
        }

        public Criteria andLevel2RateEqualTo(BigDecimal value) {
            addCriterion("level2_rate =", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateNotEqualTo(BigDecimal value) {
            addCriterion("level2_rate <>", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateGreaterThan(BigDecimal value) {
            addCriterion("level2_rate >", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("level2_rate >=", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateLessThan(BigDecimal value) {
            addCriterion("level2_rate <", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateLessThanOrEqualTo(BigDecimal value) {
            addCriterion("level2_rate <=", value, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateIn(List<BigDecimal> values) {
            addCriterion("level2_rate in", values, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateNotIn(List<BigDecimal> values) {
            addCriterion("level2_rate not in", values, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level2_rate between", value1, value2, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel2RateNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level2_rate not between", value1, value2, "level2Rate");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountIsNull() {
            addCriterion("level1_amount is null");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountIsNotNull() {
            addCriterion("level1_amount is not null");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountEqualTo(BigDecimal value) {
            addCriterion("level1_amount =", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountNotEqualTo(BigDecimal value) {
            addCriterion("level1_amount <>", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountGreaterThan(BigDecimal value) {
            addCriterion("level1_amount >", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("level1_amount >=", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountLessThan(BigDecimal value) {
            addCriterion("level1_amount <", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("level1_amount <=", value, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountIn(List<BigDecimal> values) {
            addCriterion("level1_amount in", values, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountNotIn(List<BigDecimal> values) {
            addCriterion("level1_amount not in", values, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level1_amount between", value1, value2, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel1AmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level1_amount not between", value1, value2, "level1Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountIsNull() {
            addCriterion("level2_amount is null");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountIsNotNull() {
            addCriterion("level2_amount is not null");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountEqualTo(BigDecimal value) {
            addCriterion("level2_amount =", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountNotEqualTo(BigDecimal value) {
            addCriterion("level2_amount <>", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountGreaterThan(BigDecimal value) {
            addCriterion("level2_amount >", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("level2_amount >=", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountLessThan(BigDecimal value) {
            addCriterion("level2_amount <", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("level2_amount <=", value, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountIn(List<BigDecimal> values) {
            addCriterion("level2_amount in", values, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountNotIn(List<BigDecimal> values) {
            addCriterion("level2_amount not in", values, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level2_amount between", value1, value2, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andLevel2AmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("level2_amount not between", value1, value2, "level2Amount");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeIsNull() {
            addCriterion("commission_type is null");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeIsNotNull() {
            addCriterion("commission_type is not null");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeEqualTo(Byte value) {
            addCriterion("commission_type =", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeNotEqualTo(Byte value) {
            addCriterion("commission_type <>", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeGreaterThan(Byte value) {
            addCriterion("commission_type >", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("commission_type >=", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeLessThan(Byte value) {
            addCriterion("commission_type <", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeLessThanOrEqualTo(Byte value) {
            addCriterion("commission_type <=", value, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeIn(List<Byte> values) {
            addCriterion("commission_type in", values, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeNotIn(List<Byte> values) {
            addCriterion("commission_type not in", values, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeBetween(Byte value1, Byte value2) {
            addCriterion("commission_type between", value1, value2, "commissionType");
            return (Criteria) this;
        }

        public Criteria andCommissionTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("commission_type not between", value1, value2, "commissionType");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountIsNull() {
            addCriterion("min_order_amount is null");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountIsNotNull() {
            addCriterion("min_order_amount is not null");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountEqualTo(BigDecimal value) {
            addCriterion("min_order_amount =", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountNotEqualTo(BigDecimal value) {
            addCriterion("min_order_amount <>", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountGreaterThan(BigDecimal value) {
            addCriterion("min_order_amount >", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("min_order_amount >=", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountLessThan(BigDecimal value) {
            addCriterion("min_order_amount <", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("min_order_amount <=", value, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountIn(List<BigDecimal> values) {
            addCriterion("min_order_amount in", values, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountNotIn(List<BigDecimal> values) {
            addCriterion("min_order_amount not in", values, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_order_amount between", value1, value2, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMinOrderAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_order_amount not between", value1, value2, "minOrderAmount");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionIsNull() {
            addCriterion("max_commission is null");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionIsNotNull() {
            addCriterion("max_commission is not null");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionEqualTo(BigDecimal value) {
            addCriterion("max_commission =", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionNotEqualTo(BigDecimal value) {
            addCriterion("max_commission <>", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionGreaterThan(BigDecimal value) {
            addCriterion("max_commission >", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("max_commission >=", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionLessThan(BigDecimal value) {
            addCriterion("max_commission <", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionLessThanOrEqualTo(BigDecimal value) {
            addCriterion("max_commission <=", value, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionIn(List<BigDecimal> values) {
            addCriterion("max_commission in", values, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionNotIn(List<BigDecimal> values) {
            addCriterion("max_commission not in", values, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_commission between", value1, value2, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andMaxCommissionNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_commission not between", value1, value2, "maxCommission");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNull() {
            addCriterion("is_active is null");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNotNull() {
            addCriterion("is_active is not null");
            return (Criteria) this;
        }

        public Criteria andIsActiveEqualTo(Byte value) {
            addCriterion("is_active =", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotEqualTo(Byte value) {
            addCriterion("is_active <>", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThan(Byte value) {
            addCriterion("is_active >", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_active >=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThan(Byte value) {
            addCriterion("is_active <", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThanOrEqualTo(Byte value) {
            addCriterion("is_active <=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveIn(List<Byte> values) {
            addCriterion("is_active in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotIn(List<Byte> values) {
            addCriterion("is_active not in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveBetween(Byte value1, Byte value2) {
            addCriterion("is_active between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotBetween(Byte value1, Byte value2) {
            addCriterion("is_active not between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNull() {
            addCriterion("start_time is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("start_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Date value) {
            addCriterion("start_time =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Date value) {
            addCriterion("start_time <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Date value) {
            addCriterion("start_time >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("start_time >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Date value) {
            addCriterion("start_time <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("start_time <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Date> values) {
            addCriterion("start_time in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Date> values) {
            addCriterion("start_time not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Date value1, Date value2) {
            addCriterion("start_time between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("start_time not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Date value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Date value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Date value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Date value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Date> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Date> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Date value1, Date value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
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