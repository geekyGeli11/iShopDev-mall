package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsIntegrationPromotionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SmsIntegrationPromotionExample() {
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
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

        public Criteria andMultiplierIsNull() {
            addCriterion("multiplier is null");
            return (Criteria) this;
        }

        public Criteria andMultiplierIsNotNull() {
            addCriterion("multiplier is not null");
            return (Criteria) this;
        }

        public Criteria andMultiplierEqualTo(BigDecimal value) {
            addCriterion("multiplier =", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierNotEqualTo(BigDecimal value) {
            addCriterion("multiplier <>", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierGreaterThan(BigDecimal value) {
            addCriterion("multiplier >", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("multiplier >=", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierLessThan(BigDecimal value) {
            addCriterion("multiplier <", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierLessThanOrEqualTo(BigDecimal value) {
            addCriterion("multiplier <=", value, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierIn(List<BigDecimal> values) {
            addCriterion("multiplier in", values, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierNotIn(List<BigDecimal> values) {
            addCriterion("multiplier not in", values, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("multiplier between", value1, value2, "multiplier");
            return (Criteria) this;
        }

        public Criteria andMultiplierNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("multiplier not between", value1, value2, "multiplier");
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

        public Criteria andStatusEqualTo(Boolean value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Boolean value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Boolean value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Boolean value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Boolean value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Boolean value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Boolean> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Boolean> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Boolean value1, Boolean value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Boolean value1, Boolean value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNull() {
            addCriterion("priority is null");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNotNull() {
            addCriterion("priority is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityEqualTo(Integer value) {
            addCriterion("priority =", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotEqualTo(Integer value) {
            addCriterion("priority <>", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThan(Integer value) {
            addCriterion("priority >", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThanOrEqualTo(Integer value) {
            addCriterion("priority >=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThan(Integer value) {
            addCriterion("priority <", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThanOrEqualTo(Integer value) {
            addCriterion("priority <=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityIn(List<Integer> values) {
            addCriterion("priority in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotIn(List<Integer> values) {
            addCriterion("priority not in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityBetween(Integer value1, Integer value2) {
            addCriterion("priority between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotBetween(Integer value1, Integer value2) {
            addCriterion("priority not between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeIsNull() {
            addCriterion("applicable_type is null");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeIsNotNull() {
            addCriterion("applicable_type is not null");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeEqualTo(Integer value) {
            addCriterion("applicable_type =", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeNotEqualTo(Integer value) {
            addCriterion("applicable_type <>", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeGreaterThan(Integer value) {
            addCriterion("applicable_type >", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("applicable_type >=", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeLessThan(Integer value) {
            addCriterion("applicable_type <", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeLessThanOrEqualTo(Integer value) {
            addCriterion("applicable_type <=", value, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeIn(List<Integer> values) {
            addCriterion("applicable_type in", values, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeNotIn(List<Integer> values) {
            addCriterion("applicable_type not in", values, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeBetween(Integer value1, Integer value2) {
            addCriterion("applicable_type between", value1, value2, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("applicable_type not between", value1, value2, "applicableType");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsIsNull() {
            addCriterion("applicable_ids is null");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsIsNotNull() {
            addCriterion("applicable_ids is not null");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsEqualTo(String value) {
            addCriterion("applicable_ids =", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsNotEqualTo(String value) {
            addCriterion("applicable_ids <>", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsGreaterThan(String value) {
            addCriterion("applicable_ids >", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsGreaterThanOrEqualTo(String value) {
            addCriterion("applicable_ids >=", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsLessThan(String value) {
            addCriterion("applicable_ids <", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsLessThanOrEqualTo(String value) {
            addCriterion("applicable_ids <=", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsLike(String value) {
            addCriterion("applicable_ids like", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsNotLike(String value) {
            addCriterion("applicable_ids not like", value, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsIn(List<String> values) {
            addCriterion("applicable_ids in", values, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsNotIn(List<String> values) {
            addCriterion("applicable_ids not in", values, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsBetween(String value1, String value2) {
            addCriterion("applicable_ids between", value1, value2, "applicableIds");
            return (Criteria) this;
        }

        public Criteria andApplicableIdsNotBetween(String value1, String value2) {
            addCriterion("applicable_ids not between", value1, value2, "applicableIds");
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

        public Criteria andMaxIntegrationRewardIsNull() {
            addCriterion("max_integration_reward is null");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardIsNotNull() {
            addCriterion("max_integration_reward is not null");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardEqualTo(Integer value) {
            addCriterion("max_integration_reward =", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardNotEqualTo(Integer value) {
            addCriterion("max_integration_reward <>", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardGreaterThan(Integer value) {
            addCriterion("max_integration_reward >", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_integration_reward >=", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardLessThan(Integer value) {
            addCriterion("max_integration_reward <", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardLessThanOrEqualTo(Integer value) {
            addCriterion("max_integration_reward <=", value, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardIn(List<Integer> values) {
            addCriterion("max_integration_reward in", values, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardNotIn(List<Integer> values) {
            addCriterion("max_integration_reward not in", values, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardBetween(Integer value1, Integer value2) {
            addCriterion("max_integration_reward between", value1, value2, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andMaxIntegrationRewardNotBetween(Integer value1, Integer value2) {
            addCriterion("max_integration_reward not between", value1, value2, "maxIntegrationReward");
            return (Criteria) this;
        }

        public Criteria andTotalLimitIsNull() {
            addCriterion("total_limit is null");
            return (Criteria) this;
        }

        public Criteria andTotalLimitIsNotNull() {
            addCriterion("total_limit is not null");
            return (Criteria) this;
        }

        public Criteria andTotalLimitEqualTo(Integer value) {
            addCriterion("total_limit =", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitNotEqualTo(Integer value) {
            addCriterion("total_limit <>", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitGreaterThan(Integer value) {
            addCriterion("total_limit >", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_limit >=", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitLessThan(Integer value) {
            addCriterion("total_limit <", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitLessThanOrEqualTo(Integer value) {
            addCriterion("total_limit <=", value, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitIn(List<Integer> values) {
            addCriterion("total_limit in", values, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitNotIn(List<Integer> values) {
            addCriterion("total_limit not in", values, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitBetween(Integer value1, Integer value2) {
            addCriterion("total_limit between", value1, value2, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andTotalLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("total_limit not between", value1, value2, "totalLimit");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationIsNull() {
            addCriterion("used_integration is null");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationIsNotNull() {
            addCriterion("used_integration is not null");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationEqualTo(Integer value) {
            addCriterion("used_integration =", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationNotEqualTo(Integer value) {
            addCriterion("used_integration <>", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationGreaterThan(Integer value) {
            addCriterion("used_integration >", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationGreaterThanOrEqualTo(Integer value) {
            addCriterion("used_integration >=", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationLessThan(Integer value) {
            addCriterion("used_integration <", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationLessThanOrEqualTo(Integer value) {
            addCriterion("used_integration <=", value, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationIn(List<Integer> values) {
            addCriterion("used_integration in", values, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationNotIn(List<Integer> values) {
            addCriterion("used_integration not in", values, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationBetween(Integer value1, Integer value2) {
            addCriterion("used_integration between", value1, value2, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andUsedIntegrationNotBetween(Integer value1, Integer value2) {
            addCriterion("used_integration not between", value1, value2, "usedIntegration");
            return (Criteria) this;
        }

        public Criteria andMemberLimitIsNull() {
            addCriterion("member_limit is null");
            return (Criteria) this;
        }

        public Criteria andMemberLimitIsNotNull() {
            addCriterion("member_limit is not null");
            return (Criteria) this;
        }

        public Criteria andMemberLimitEqualTo(Integer value) {
            addCriterion("member_limit =", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitNotEqualTo(Integer value) {
            addCriterion("member_limit <>", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitGreaterThan(Integer value) {
            addCriterion("member_limit >", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("member_limit >=", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitLessThan(Integer value) {
            addCriterion("member_limit <", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitLessThanOrEqualTo(Integer value) {
            addCriterion("member_limit <=", value, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitIn(List<Integer> values) {
            addCriterion("member_limit in", values, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitNotIn(List<Integer> values) {
            addCriterion("member_limit not in", values, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitBetween(Integer value1, Integer value2) {
            addCriterion("member_limit between", value1, value2, "memberLimit");
            return (Criteria) this;
        }

        public Criteria andMemberLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("member_limit not between", value1, value2, "memberLimit");
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

        public Criteria andCreatorIsNull() {
            addCriterion("creator is null");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNotNull() {
            addCriterion("creator is not null");
            return (Criteria) this;
        }

        public Criteria andCreatorEqualTo(String value) {
            addCriterion("creator =", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotEqualTo(String value) {
            addCriterion("creator <>", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThan(String value) {
            addCriterion("creator >", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThanOrEqualTo(String value) {
            addCriterion("creator >=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThan(String value) {
            addCriterion("creator <", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThanOrEqualTo(String value) {
            addCriterion("creator <=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLike(String value) {
            addCriterion("creator like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotLike(String value) {
            addCriterion("creator not like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorIn(List<String> values) {
            addCriterion("creator in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotIn(List<String> values) {
            addCriterion("creator not in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorBetween(String value1, String value2) {
            addCriterion("creator between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotBetween(String value1, String value2) {
            addCriterion("creator not between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andUpdaterIsNull() {
            addCriterion("updater is null");
            return (Criteria) this;
        }

        public Criteria andUpdaterIsNotNull() {
            addCriterion("updater is not null");
            return (Criteria) this;
        }

        public Criteria andUpdaterEqualTo(String value) {
            addCriterion("updater =", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotEqualTo(String value) {
            addCriterion("updater <>", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterGreaterThan(String value) {
            addCriterion("updater >", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterGreaterThanOrEqualTo(String value) {
            addCriterion("updater >=", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLessThan(String value) {
            addCriterion("updater <", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLessThanOrEqualTo(String value) {
            addCriterion("updater <=", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLike(String value) {
            addCriterion("updater like", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotLike(String value) {
            addCriterion("updater not like", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterIn(List<String> values) {
            addCriterion("updater in", values, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotIn(List<String> values) {
            addCriterion("updater not in", values, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterBetween(String value1, String value2) {
            addCriterion("updater between", value1, value2, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotBetween(String value1, String value2) {
            addCriterion("updater not between", value1, value2, "updater");
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