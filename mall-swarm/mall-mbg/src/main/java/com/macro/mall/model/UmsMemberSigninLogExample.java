package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UmsMemberSigninLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UmsMemberSigninLogExample() {
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

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
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

        public Criteria andMemberIdIsNull() {
            addCriterion("member_id is null");
            return (Criteria) this;
        }

        public Criteria andMemberIdIsNotNull() {
            addCriterion("member_id is not null");
            return (Criteria) this;
        }

        public Criteria andMemberIdEqualTo(Long value) {
            addCriterion("member_id =", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotEqualTo(Long value) {
            addCriterion("member_id <>", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThan(Long value) {
            addCriterion("member_id >", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThanOrEqualTo(Long value) {
            addCriterion("member_id >=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThan(Long value) {
            addCriterion("member_id <", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThanOrEqualTo(Long value) {
            addCriterion("member_id <=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdIn(List<Long> values) {
            addCriterion("member_id in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotIn(List<Long> values) {
            addCriterion("member_id not in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdBetween(Long value1, Long value2) {
            addCriterion("member_id between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotBetween(Long value1, Long value2) {
            addCriterion("member_id not between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameIsNull() {
            addCriterion("member_username is null");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameIsNotNull() {
            addCriterion("member_username is not null");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameEqualTo(String value) {
            addCriterion("member_username =", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameNotEqualTo(String value) {
            addCriterion("member_username <>", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameGreaterThan(String value) {
            addCriterion("member_username >", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("member_username >=", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameLessThan(String value) {
            addCriterion("member_username <", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameLessThanOrEqualTo(String value) {
            addCriterion("member_username <=", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameLike(String value) {
            addCriterion("member_username like", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameNotLike(String value) {
            addCriterion("member_username not like", value, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameIn(List<String> values) {
            addCriterion("member_username in", values, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameNotIn(List<String> values) {
            addCriterion("member_username not in", values, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameBetween(String value1, String value2) {
            addCriterion("member_username between", value1, value2, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andMemberUsernameNotBetween(String value1, String value2) {
            addCriterion("member_username not between", value1, value2, "memberUsername");
            return (Criteria) this;
        }

        public Criteria andSigninDateIsNull() {
            addCriterion("signin_date is null");
            return (Criteria) this;
        }

        public Criteria andSigninDateIsNotNull() {
            addCriterion("signin_date is not null");
            return (Criteria) this;
        }

        public Criteria andSigninDateEqualTo(Date value) {
            addCriterionForJDBCDate("signin_date =", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("signin_date <>", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateGreaterThan(Date value) {
            addCriterionForJDBCDate("signin_date >", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("signin_date >=", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateLessThan(Date value) {
            addCriterionForJDBCDate("signin_date <", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("signin_date <=", value, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateIn(List<Date> values) {
            addCriterionForJDBCDate("signin_date in", values, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("signin_date not in", values, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("signin_date between", value1, value2, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("signin_date not between", value1, value2, "signinDate");
            return (Criteria) this;
        }

        public Criteria andSigninTimeIsNull() {
            addCriterion("signin_time is null");
            return (Criteria) this;
        }

        public Criteria andSigninTimeIsNotNull() {
            addCriterion("signin_time is not null");
            return (Criteria) this;
        }

        public Criteria andSigninTimeEqualTo(Date value) {
            addCriterion("signin_time =", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeNotEqualTo(Date value) {
            addCriterion("signin_time <>", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeGreaterThan(Date value) {
            addCriterion("signin_time >", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("signin_time >=", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeLessThan(Date value) {
            addCriterion("signin_time <", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeLessThanOrEqualTo(Date value) {
            addCriterion("signin_time <=", value, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeIn(List<Date> values) {
            addCriterion("signin_time in", values, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeNotIn(List<Date> values) {
            addCriterion("signin_time not in", values, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeBetween(Date value1, Date value2) {
            addCriterion("signin_time between", value1, value2, "signinTime");
            return (Criteria) this;
        }

        public Criteria andSigninTimeNotBetween(Date value1, Date value2) {
            addCriterion("signin_time not between", value1, value2, "signinTime");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedIsNull() {
            addCriterion("points_earned is null");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedIsNotNull() {
            addCriterion("points_earned is not null");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedEqualTo(Integer value) {
            addCriterion("points_earned =", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedNotEqualTo(Integer value) {
            addCriterion("points_earned <>", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedGreaterThan(Integer value) {
            addCriterion("points_earned >", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedGreaterThanOrEqualTo(Integer value) {
            addCriterion("points_earned >=", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedLessThan(Integer value) {
            addCriterion("points_earned <", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedLessThanOrEqualTo(Integer value) {
            addCriterion("points_earned <=", value, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedIn(List<Integer> values) {
            addCriterion("points_earned in", values, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedNotIn(List<Integer> values) {
            addCriterion("points_earned not in", values, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedBetween(Integer value1, Integer value2) {
            addCriterion("points_earned between", value1, value2, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andPointsEarnedNotBetween(Integer value1, Integer value2) {
            addCriterion("points_earned not between", value1, value2, "pointsEarned");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysIsNull() {
            addCriterion("continuous_days is null");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysIsNotNull() {
            addCriterion("continuous_days is not null");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysEqualTo(Integer value) {
            addCriterion("continuous_days =", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysNotEqualTo(Integer value) {
            addCriterion("continuous_days <>", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysGreaterThan(Integer value) {
            addCriterion("continuous_days >", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysGreaterThanOrEqualTo(Integer value) {
            addCriterion("continuous_days >=", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysLessThan(Integer value) {
            addCriterion("continuous_days <", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysLessThanOrEqualTo(Integer value) {
            addCriterion("continuous_days <=", value, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysIn(List<Integer> values) {
            addCriterion("continuous_days in", values, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysNotIn(List<Integer> values) {
            addCriterion("continuous_days not in", values, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysBetween(Integer value1, Integer value2) {
            addCriterion("continuous_days between", value1, value2, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andContinuousDaysNotBetween(Integer value1, Integer value2) {
            addCriterion("continuous_days not between", value1, value2, "continuousDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysIsNull() {
            addCriterion("cycle_signin_days is null");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysIsNotNull() {
            addCriterion("cycle_signin_days is not null");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysEqualTo(Integer value) {
            addCriterion("cycle_signin_days =", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysNotEqualTo(Integer value) {
            addCriterion("cycle_signin_days <>", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysGreaterThan(Integer value) {
            addCriterion("cycle_signin_days >", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysGreaterThanOrEqualTo(Integer value) {
            addCriterion("cycle_signin_days >=", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysLessThan(Integer value) {
            addCriterion("cycle_signin_days <", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysLessThanOrEqualTo(Integer value) {
            addCriterion("cycle_signin_days <=", value, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysIn(List<Integer> values) {
            addCriterion("cycle_signin_days in", values, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysNotIn(List<Integer> values) {
            addCriterion("cycle_signin_days not in", values, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysBetween(Integer value1, Integer value2) {
            addCriterion("cycle_signin_days between", value1, value2, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andCycleSigninDaysNotBetween(Integer value1, Integer value2) {
            addCriterion("cycle_signin_days not between", value1, value2, "cycleSigninDays");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedIsNull() {
            addCriterion("is_reward_claimed is null");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedIsNotNull() {
            addCriterion("is_reward_claimed is not null");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedEqualTo(Byte value) {
            addCriterion("is_reward_claimed =", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedNotEqualTo(Byte value) {
            addCriterion("is_reward_claimed <>", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedGreaterThan(Byte value) {
            addCriterion("is_reward_claimed >", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_reward_claimed >=", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedLessThan(Byte value) {
            addCriterion("is_reward_claimed <", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedLessThanOrEqualTo(Byte value) {
            addCriterion("is_reward_claimed <=", value, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedIn(List<Byte> values) {
            addCriterion("is_reward_claimed in", values, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedNotIn(List<Byte> values) {
            addCriterion("is_reward_claimed not in", values, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedBetween(Byte value1, Byte value2) {
            addCriterion("is_reward_claimed between", value1, value2, "isRewardClaimed");
            return (Criteria) this;
        }

        public Criteria andIsRewardClaimedNotBetween(Byte value1, Byte value2) {
            addCriterion("is_reward_claimed not between", value1, value2, "isRewardClaimed");
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

        public Criteria andSigninMonthIsNull() {
            addCriterion("signin_month is null");
            return (Criteria) this;
        }

        public Criteria andSigninMonthIsNotNull() {
            addCriterion("signin_month is not null");
            return (Criteria) this;
        }

        public Criteria andSigninMonthEqualTo(String value) {
            addCriterion("signin_month =", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthNotEqualTo(String value) {
            addCriterion("signin_month <>", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthGreaterThan(String value) {
            addCriterion("signin_month >", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthGreaterThanOrEqualTo(String value) {
            addCriterion("signin_month >=", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthLessThan(String value) {
            addCriterion("signin_month <", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthLessThanOrEqualTo(String value) {
            addCriterion("signin_month <=", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthLike(String value) {
            addCriterion("signin_month like", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthNotLike(String value) {
            addCriterion("signin_month not like", value, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthIn(List<String> values) {
            addCriterion("signin_month in", values, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthNotIn(List<String> values) {
            addCriterion("signin_month not in", values, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthBetween(String value1, String value2) {
            addCriterion("signin_month between", value1, value2, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andSigninMonthNotBetween(String value1, String value2) {
            addCriterion("signin_month not between", value1, value2, "signinMonth");
            return (Criteria) this;
        }

        public Criteria andClientIpIsNull() {
            addCriterion("client_ip is null");
            return (Criteria) this;
        }

        public Criteria andClientIpIsNotNull() {
            addCriterion("client_ip is not null");
            return (Criteria) this;
        }

        public Criteria andClientIpEqualTo(String value) {
            addCriterion("client_ip =", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpNotEqualTo(String value) {
            addCriterion("client_ip <>", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpGreaterThan(String value) {
            addCriterion("client_ip >", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpGreaterThanOrEqualTo(String value) {
            addCriterion("client_ip >=", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpLessThan(String value) {
            addCriterion("client_ip <", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpLessThanOrEqualTo(String value) {
            addCriterion("client_ip <=", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpLike(String value) {
            addCriterion("client_ip like", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpNotLike(String value) {
            addCriterion("client_ip not like", value, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpIn(List<String> values) {
            addCriterion("client_ip in", values, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpNotIn(List<String> values) {
            addCriterion("client_ip not in", values, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpBetween(String value1, String value2) {
            addCriterion("client_ip between", value1, value2, "clientIp");
            return (Criteria) this;
        }

        public Criteria andClientIpNotBetween(String value1, String value2) {
            addCriterion("client_ip not between", value1, value2, "clientIp");
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