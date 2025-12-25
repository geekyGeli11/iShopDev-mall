package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UmsSigninRewardConfigExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UmsSigninRewardConfigExample() {
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

        public Criteria andBasePointsIsNull() {
            addCriterion("base_points is null");
            return (Criteria) this;
        }

        public Criteria andBasePointsIsNotNull() {
            addCriterion("base_points is not null");
            return (Criteria) this;
        }

        public Criteria andBasePointsEqualTo(Integer value) {
            addCriterion("base_points =", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsNotEqualTo(Integer value) {
            addCriterion("base_points <>", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsGreaterThan(Integer value) {
            addCriterion("base_points >", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsGreaterThanOrEqualTo(Integer value) {
            addCriterion("base_points >=", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsLessThan(Integer value) {
            addCriterion("base_points <", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsLessThanOrEqualTo(Integer value) {
            addCriterion("base_points <=", value, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsIn(List<Integer> values) {
            addCriterion("base_points in", values, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsNotIn(List<Integer> values) {
            addCriterion("base_points not in", values, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsBetween(Integer value1, Integer value2) {
            addCriterion("base_points between", value1, value2, "basePoints");
            return (Criteria) this;
        }

        public Criteria andBasePointsNotBetween(Integer value1, Integer value2) {
            addCriterion("base_points not between", value1, value2, "basePoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsIsNull() {
            addCriterion("increment_points is null");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsIsNotNull() {
            addCriterion("increment_points is not null");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsEqualTo(Integer value) {
            addCriterion("increment_points =", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsNotEqualTo(Integer value) {
            addCriterion("increment_points <>", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsGreaterThan(Integer value) {
            addCriterion("increment_points >", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsGreaterThanOrEqualTo(Integer value) {
            addCriterion("increment_points >=", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsLessThan(Integer value) {
            addCriterion("increment_points <", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsLessThanOrEqualTo(Integer value) {
            addCriterion("increment_points <=", value, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsIn(List<Integer> values) {
            addCriterion("increment_points in", values, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsNotIn(List<Integer> values) {
            addCriterion("increment_points not in", values, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsBetween(Integer value1, Integer value2) {
            addCriterion("increment_points between", value1, value2, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andIncrementPointsNotBetween(Integer value1, Integer value2) {
            addCriterion("increment_points not between", value1, value2, "incrementPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsIsNull() {
            addCriterion("max_daily_points is null");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsIsNotNull() {
            addCriterion("max_daily_points is not null");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsEqualTo(Integer value) {
            addCriterion("max_daily_points =", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsNotEqualTo(Integer value) {
            addCriterion("max_daily_points <>", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsGreaterThan(Integer value) {
            addCriterion("max_daily_points >", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_daily_points >=", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsLessThan(Integer value) {
            addCriterion("max_daily_points <", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsLessThanOrEqualTo(Integer value) {
            addCriterion("max_daily_points <=", value, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsIn(List<Integer> values) {
            addCriterion("max_daily_points in", values, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsNotIn(List<Integer> values) {
            addCriterion("max_daily_points not in", values, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsBetween(Integer value1, Integer value2) {
            addCriterion("max_daily_points between", value1, value2, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andMaxDailyPointsNotBetween(Integer value1, Integer value2) {
            addCriterion("max_daily_points not between", value1, value2, "maxDailyPoints");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardIsNull() {
            addCriterion("continuous_days_for_reward is null");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardIsNotNull() {
            addCriterion("continuous_days_for_reward is not null");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardEqualTo(Integer value) {
            addCriterion("continuous_days_for_reward =", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardNotEqualTo(Integer value) {
            addCriterion("continuous_days_for_reward <>", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardGreaterThan(Integer value) {
            addCriterion("continuous_days_for_reward >", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardGreaterThanOrEqualTo(Integer value) {
            addCriterion("continuous_days_for_reward >=", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardLessThan(Integer value) {
            addCriterion("continuous_days_for_reward <", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardLessThanOrEqualTo(Integer value) {
            addCriterion("continuous_days_for_reward <=", value, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardIn(List<Integer> values) {
            addCriterion("continuous_days_for_reward in", values, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardNotIn(List<Integer> values) {
            addCriterion("continuous_days_for_reward not in", values, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardBetween(Integer value1, Integer value2) {
            addCriterion("continuous_days_for_reward between", value1, value2, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysForRewardNotBetween(Integer value1, Integer value2) {
            addCriterion("continuous_days_for_reward not between", value1, value2, "continuousDaysForReward");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdIsNull() {
            addCriterion("reward_coupon_id is null");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdIsNotNull() {
            addCriterion("reward_coupon_id is not null");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdEqualTo(Long value) {
            addCriterion("reward_coupon_id =", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdNotEqualTo(Long value) {
            addCriterion("reward_coupon_id <>", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdGreaterThan(Long value) {
            addCriterion("reward_coupon_id >", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdGreaterThanOrEqualTo(Long value) {
            addCriterion("reward_coupon_id >=", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdLessThan(Long value) {
            addCriterion("reward_coupon_id <", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdLessThanOrEqualTo(Long value) {
            addCriterion("reward_coupon_id <=", value, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdIn(List<Long> values) {
            addCriterion("reward_coupon_id in", values, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdNotIn(List<Long> values) {
            addCriterion("reward_coupon_id not in", values, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdBetween(Long value1, Long value2) {
            addCriterion("reward_coupon_id between", value1, value2, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andRewardCouponIdNotBetween(Long value1, Long value2) {
            addCriterion("reward_coupon_id not between", value1, value2, "rewardCouponId");
            return (Criteria) this;
        }

        public Criteria andCycleDaysIsNull() {
            addCriterion("cycle_days is null");
            return (Criteria) this;
        }

        public Criteria andCycleDaysIsNotNull() {
            addCriterion("cycle_days is not null");
            return (Criteria) this;
        }

        public Criteria andCycleDaysEqualTo(Integer value) {
            addCriterion("cycle_days =", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysNotEqualTo(Integer value) {
            addCriterion("cycle_days <>", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysGreaterThan(Integer value) {
            addCriterion("cycle_days >", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysGreaterThanOrEqualTo(Integer value) {
            addCriterion("cycle_days >=", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysLessThan(Integer value) {
            addCriterion("cycle_days <", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysLessThanOrEqualTo(Integer value) {
            addCriterion("cycle_days <=", value, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysIn(List<Integer> values) {
            addCriterion("cycle_days in", values, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysNotIn(List<Integer> values) {
            addCriterion("cycle_days not in", values, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysBetween(Integer value1, Integer value2) {
            addCriterion("cycle_days between", value1, value2, "cycleDays");
            return (Criteria) this;
        }

        public Criteria andCycleDaysNotBetween(Integer value1, Integer value2) {
            addCriterion("cycle_days not between", value1, value2, "cycleDays");
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