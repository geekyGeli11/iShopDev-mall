package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PmsInviteStatisticsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsInviteStatisticsExample() {
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

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andStatDateIsNull() {
            addCriterion("stat_date is null");
            return (Criteria) this;
        }

        public Criteria andStatDateIsNotNull() {
            addCriterion("stat_date is not null");
            return (Criteria) this;
        }

        public Criteria andStatDateEqualTo(Date value) {
            addCriterionForJDBCDate("stat_date =", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("stat_date <>", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateGreaterThan(Date value) {
            addCriterionForJDBCDate("stat_date >", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("stat_date >=", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateLessThan(Date value) {
            addCriterionForJDBCDate("stat_date <", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("stat_date <=", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateIn(List<Date> values) {
            addCriterionForJDBCDate("stat_date in", values, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("stat_date not in", values, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("stat_date between", value1, value2, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("stat_date not between", value1, value2, "statDate");
            return (Criteria) this;
        }

        public Criteria andInviteCountIsNull() {
            addCriterion("invite_count is null");
            return (Criteria) this;
        }

        public Criteria andInviteCountIsNotNull() {
            addCriterion("invite_count is not null");
            return (Criteria) this;
        }

        public Criteria andInviteCountEqualTo(Integer value) {
            addCriterion("invite_count =", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountNotEqualTo(Integer value) {
            addCriterion("invite_count <>", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountGreaterThan(Integer value) {
            addCriterion("invite_count >", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("invite_count >=", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountLessThan(Integer value) {
            addCriterion("invite_count <", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountLessThanOrEqualTo(Integer value) {
            addCriterion("invite_count <=", value, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountIn(List<Integer> values) {
            addCriterion("invite_count in", values, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountNotIn(List<Integer> values) {
            addCriterion("invite_count not in", values, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountBetween(Integer value1, Integer value2) {
            addCriterion("invite_count between", value1, value2, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andInviteCountNotBetween(Integer value1, Integer value2) {
            addCriterion("invite_count not between", value1, value2, "inviteCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountIsNull() {
            addCriterion("register_count is null");
            return (Criteria) this;
        }

        public Criteria andRegisterCountIsNotNull() {
            addCriterion("register_count is not null");
            return (Criteria) this;
        }

        public Criteria andRegisterCountEqualTo(Integer value) {
            addCriterion("register_count =", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountNotEqualTo(Integer value) {
            addCriterion("register_count <>", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountGreaterThan(Integer value) {
            addCriterion("register_count >", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("register_count >=", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountLessThan(Integer value) {
            addCriterion("register_count <", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountLessThanOrEqualTo(Integer value) {
            addCriterion("register_count <=", value, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountIn(List<Integer> values) {
            addCriterion("register_count in", values, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountNotIn(List<Integer> values) {
            addCriterion("register_count not in", values, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountBetween(Integer value1, Integer value2) {
            addCriterion("register_count between", value1, value2, "registerCount");
            return (Criteria) this;
        }

        public Criteria andRegisterCountNotBetween(Integer value1, Integer value2) {
            addCriterion("register_count not between", value1, value2, "registerCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountIsNull() {
            addCriterion("first_order_count is null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountIsNotNull() {
            addCriterion("first_order_count is not null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountEqualTo(Integer value) {
            addCriterion("first_order_count =", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountNotEqualTo(Integer value) {
            addCriterion("first_order_count <>", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountGreaterThan(Integer value) {
            addCriterion("first_order_count >", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("first_order_count >=", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountLessThan(Integer value) {
            addCriterion("first_order_count <", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountLessThanOrEqualTo(Integer value) {
            addCriterion("first_order_count <=", value, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountIn(List<Integer> values) {
            addCriterion("first_order_count in", values, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountNotIn(List<Integer> values) {
            addCriterion("first_order_count not in", values, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountBetween(Integer value1, Integer value2) {
            addCriterion("first_order_count between", value1, value2, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderCountNotBetween(Integer value1, Integer value2) {
            addCriterion("first_order_count not between", value1, value2, "firstOrderCount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsIsNull() {
            addCriterion("total_reward_points is null");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsIsNotNull() {
            addCriterion("total_reward_points is not null");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsEqualTo(Integer value) {
            addCriterion("total_reward_points =", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsNotEqualTo(Integer value) {
            addCriterion("total_reward_points <>", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsGreaterThan(Integer value) {
            addCriterion("total_reward_points >", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_reward_points >=", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsLessThan(Integer value) {
            addCriterion("total_reward_points <", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsLessThanOrEqualTo(Integer value) {
            addCriterion("total_reward_points <=", value, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsIn(List<Integer> values) {
            addCriterion("total_reward_points in", values, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsNotIn(List<Integer> values) {
            addCriterion("total_reward_points not in", values, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsBetween(Integer value1, Integer value2) {
            addCriterion("total_reward_points between", value1, value2, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardPointsNotBetween(Integer value1, Integer value2) {
            addCriterion("total_reward_points not between", value1, value2, "totalRewardPoints");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountIsNull() {
            addCriterion("total_reward_amount is null");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountIsNotNull() {
            addCriterion("total_reward_amount is not null");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountEqualTo(BigDecimal value) {
            addCriterion("total_reward_amount =", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountNotEqualTo(BigDecimal value) {
            addCriterion("total_reward_amount <>", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountGreaterThan(BigDecimal value) {
            addCriterion("total_reward_amount >", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_reward_amount >=", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountLessThan(BigDecimal value) {
            addCriterion("total_reward_amount <", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_reward_amount <=", value, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountIn(List<BigDecimal> values) {
            addCriterion("total_reward_amount in", values, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountNotIn(List<BigDecimal> values) {
            addCriterion("total_reward_amount not in", values, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_reward_amount between", value1, value2, "totalRewardAmount");
            return (Criteria) this;
        }

        public Criteria andTotalRewardAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_reward_amount not between", value1, value2, "totalRewardAmount");
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

        public Criteria andDistributorStatusIsNull() {
            addCriterion("distributor_status is null");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusIsNotNull() {
            addCriterion("distributor_status is not null");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusEqualTo(Byte value) {
            addCriterion("distributor_status =", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusNotEqualTo(Byte value) {
            addCriterion("distributor_status <>", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusGreaterThan(Byte value) {
            addCriterion("distributor_status >", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("distributor_status >=", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusLessThan(Byte value) {
            addCriterion("distributor_status <", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusLessThanOrEqualTo(Byte value) {
            addCriterion("distributor_status <=", value, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusIn(List<Byte> values) {
            addCriterion("distributor_status in", values, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusNotIn(List<Byte> values) {
            addCriterion("distributor_status not in", values, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusBetween(Byte value1, Byte value2) {
            addCriterion("distributor_status between", value1, value2, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andDistributorStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("distributor_status not between", value1, value2, "distributorStatus");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionIsNull() {
            addCriterion("total_commission is null");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionIsNotNull() {
            addCriterion("total_commission is not null");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionEqualTo(BigDecimal value) {
            addCriterion("total_commission =", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionNotEqualTo(BigDecimal value) {
            addCriterion("total_commission <>", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionGreaterThan(BigDecimal value) {
            addCriterion("total_commission >", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_commission >=", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionLessThan(BigDecimal value) {
            addCriterion("total_commission <", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_commission <=", value, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionIn(List<BigDecimal> values) {
            addCriterion("total_commission in", values, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionNotIn(List<BigDecimal> values) {
            addCriterion("total_commission not in", values, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_commission between", value1, value2, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andTotalCommissionNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_commission not between", value1, value2, "totalCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionIsNull() {
            addCriterion("withdraw_commission is null");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionIsNotNull() {
            addCriterion("withdraw_commission is not null");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionEqualTo(BigDecimal value) {
            addCriterion("withdraw_commission =", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionNotEqualTo(BigDecimal value) {
            addCriterion("withdraw_commission <>", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionGreaterThan(BigDecimal value) {
            addCriterion("withdraw_commission >", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("withdraw_commission >=", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionLessThan(BigDecimal value) {
            addCriterion("withdraw_commission <", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionLessThanOrEqualTo(BigDecimal value) {
            addCriterion("withdraw_commission <=", value, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionIn(List<BigDecimal> values) {
            addCriterion("withdraw_commission in", values, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionNotIn(List<BigDecimal> values) {
            addCriterion("withdraw_commission not in", values, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("withdraw_commission between", value1, value2, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andWithdrawCommissionNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("withdraw_commission not between", value1, value2, "withdrawCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionIsNull() {
            addCriterion("pending_commission is null");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionIsNotNull() {
            addCriterion("pending_commission is not null");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionEqualTo(BigDecimal value) {
            addCriterion("pending_commission =", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionNotEqualTo(BigDecimal value) {
            addCriterion("pending_commission <>", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionGreaterThan(BigDecimal value) {
            addCriterion("pending_commission >", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("pending_commission >=", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionLessThan(BigDecimal value) {
            addCriterion("pending_commission <", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionLessThanOrEqualTo(BigDecimal value) {
            addCriterion("pending_commission <=", value, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionIn(List<BigDecimal> values) {
            addCriterion("pending_commission in", values, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionNotIn(List<BigDecimal> values) {
            addCriterion("pending_commission not in", values, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pending_commission between", value1, value2, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andPendingCommissionNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pending_commission not between", value1, value2, "pendingCommission");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountIsNull() {
            addCriterion("level1_customer_count is null");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountIsNotNull() {
            addCriterion("level1_customer_count is not null");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountEqualTo(Integer value) {
            addCriterion("level1_customer_count =", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountNotEqualTo(Integer value) {
            addCriterion("level1_customer_count <>", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountGreaterThan(Integer value) {
            addCriterion("level1_customer_count >", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("level1_customer_count >=", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountLessThan(Integer value) {
            addCriterion("level1_customer_count <", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountLessThanOrEqualTo(Integer value) {
            addCriterion("level1_customer_count <=", value, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountIn(List<Integer> values) {
            addCriterion("level1_customer_count in", values, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountNotIn(List<Integer> values) {
            addCriterion("level1_customer_count not in", values, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountBetween(Integer value1, Integer value2) {
            addCriterion("level1_customer_count between", value1, value2, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel1CustomerCountNotBetween(Integer value1, Integer value2) {
            addCriterion("level1_customer_count not between", value1, value2, "level1CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountIsNull() {
            addCriterion("level2_customer_count is null");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountIsNotNull() {
            addCriterion("level2_customer_count is not null");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountEqualTo(Integer value) {
            addCriterion("level2_customer_count =", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountNotEqualTo(Integer value) {
            addCriterion("level2_customer_count <>", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountGreaterThan(Integer value) {
            addCriterion("level2_customer_count >", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("level2_customer_count >=", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountLessThan(Integer value) {
            addCriterion("level2_customer_count <", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountLessThanOrEqualTo(Integer value) {
            addCriterion("level2_customer_count <=", value, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountIn(List<Integer> values) {
            addCriterion("level2_customer_count in", values, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountNotIn(List<Integer> values) {
            addCriterion("level2_customer_count not in", values, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountBetween(Integer value1, Integer value2) {
            addCriterion("level2_customer_count between", value1, value2, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andLevel2CustomerCountNotBetween(Integer value1, Integer value2) {
            addCriterion("level2_customer_count not between", value1, value2, "level2CustomerCount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountIsNull() {
            addCriterion("total_order_amount is null");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountIsNotNull() {
            addCriterion("total_order_amount is not null");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountEqualTo(BigDecimal value) {
            addCriterion("total_order_amount =", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountNotEqualTo(BigDecimal value) {
            addCriterion("total_order_amount <>", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountGreaterThan(BigDecimal value) {
            addCriterion("total_order_amount >", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_order_amount >=", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountLessThan(BigDecimal value) {
            addCriterion("total_order_amount <", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_order_amount <=", value, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountIn(List<BigDecimal> values) {
            addCriterion("total_order_amount in", values, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountNotIn(List<BigDecimal> values) {
            addCriterion("total_order_amount not in", values, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_order_amount between", value1, value2, "totalOrderAmount");
            return (Criteria) this;
        }

        public Criteria andTotalOrderAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_order_amount not between", value1, value2, "totalOrderAmount");
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