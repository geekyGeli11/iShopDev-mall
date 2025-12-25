package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsInviteRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsInviteRelationExample() {
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

        public Criteria andInviterIdIsNull() {
            addCriterion("inviter_id is null");
            return (Criteria) this;
        }

        public Criteria andInviterIdIsNotNull() {
            addCriterion("inviter_id is not null");
            return (Criteria) this;
        }

        public Criteria andInviterIdEqualTo(Long value) {
            addCriterion("inviter_id =", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdNotEqualTo(Long value) {
            addCriterion("inviter_id <>", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdGreaterThan(Long value) {
            addCriterion("inviter_id >", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("inviter_id >=", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdLessThan(Long value) {
            addCriterion("inviter_id <", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdLessThanOrEqualTo(Long value) {
            addCriterion("inviter_id <=", value, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdIn(List<Long> values) {
            addCriterion("inviter_id in", values, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdNotIn(List<Long> values) {
            addCriterion("inviter_id not in", values, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdBetween(Long value1, Long value2) {
            addCriterion("inviter_id between", value1, value2, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviterIdNotBetween(Long value1, Long value2) {
            addCriterion("inviter_id not between", value1, value2, "inviterId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdIsNull() {
            addCriterion("invitee_id is null");
            return (Criteria) this;
        }

        public Criteria andInviteeIdIsNotNull() {
            addCriterion("invitee_id is not null");
            return (Criteria) this;
        }

        public Criteria andInviteeIdEqualTo(Long value) {
            addCriterion("invitee_id =", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdNotEqualTo(Long value) {
            addCriterion("invitee_id <>", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdGreaterThan(Long value) {
            addCriterion("invitee_id >", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdGreaterThanOrEqualTo(Long value) {
            addCriterion("invitee_id >=", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdLessThan(Long value) {
            addCriterion("invitee_id <", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdLessThanOrEqualTo(Long value) {
            addCriterion("invitee_id <=", value, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdIn(List<Long> values) {
            addCriterion("invitee_id in", values, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdNotIn(List<Long> values) {
            addCriterion("invitee_id not in", values, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdBetween(Long value1, Long value2) {
            addCriterion("invitee_id between", value1, value2, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteeIdNotBetween(Long value1, Long value2) {
            addCriterion("invitee_id not between", value1, value2, "inviteeId");
            return (Criteria) this;
        }

        public Criteria andInviteParamIsNull() {
            addCriterion("invite_param is null");
            return (Criteria) this;
        }

        public Criteria andInviteParamIsNotNull() {
            addCriterion("invite_param is not null");
            return (Criteria) this;
        }

        public Criteria andInviteParamEqualTo(String value) {
            addCriterion("invite_param =", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamNotEqualTo(String value) {
            addCriterion("invite_param <>", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamGreaterThan(String value) {
            addCriterion("invite_param >", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamGreaterThanOrEqualTo(String value) {
            addCriterion("invite_param >=", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamLessThan(String value) {
            addCriterion("invite_param <", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamLessThanOrEqualTo(String value) {
            addCriterion("invite_param <=", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamLike(String value) {
            addCriterion("invite_param like", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamNotLike(String value) {
            addCriterion("invite_param not like", value, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamIn(List<String> values) {
            addCriterion("invite_param in", values, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamNotIn(List<String> values) {
            addCriterion("invite_param not in", values, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamBetween(String value1, String value2) {
            addCriterion("invite_param between", value1, value2, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteParamNotBetween(String value1, String value2) {
            addCriterion("invite_param not between", value1, value2, "inviteParam");
            return (Criteria) this;
        }

        public Criteria andInviteTimeIsNull() {
            addCriterion("invite_time is null");
            return (Criteria) this;
        }

        public Criteria andInviteTimeIsNotNull() {
            addCriterion("invite_time is not null");
            return (Criteria) this;
        }

        public Criteria andInviteTimeEqualTo(Date value) {
            addCriterion("invite_time =", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeNotEqualTo(Date value) {
            addCriterion("invite_time <>", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeGreaterThan(Date value) {
            addCriterion("invite_time >", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("invite_time >=", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeLessThan(Date value) {
            addCriterion("invite_time <", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeLessThanOrEqualTo(Date value) {
            addCriterion("invite_time <=", value, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeIn(List<Date> values) {
            addCriterion("invite_time in", values, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeNotIn(List<Date> values) {
            addCriterion("invite_time not in", values, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeBetween(Date value1, Date value2) {
            addCriterion("invite_time between", value1, value2, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andInviteTimeNotBetween(Date value1, Date value2) {
            addCriterion("invite_time not between", value1, value2, "inviteTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeIsNull() {
            addCriterion("register_time is null");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeIsNotNull() {
            addCriterion("register_time is not null");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeEqualTo(Date value) {
            addCriterion("register_time =", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeNotEqualTo(Date value) {
            addCriterion("register_time <>", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeGreaterThan(Date value) {
            addCriterion("register_time >", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("register_time >=", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeLessThan(Date value) {
            addCriterion("register_time <", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeLessThanOrEqualTo(Date value) {
            addCriterion("register_time <=", value, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeIn(List<Date> values) {
            addCriterion("register_time in", values, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeNotIn(List<Date> values) {
            addCriterion("register_time not in", values, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeBetween(Date value1, Date value2) {
            addCriterion("register_time between", value1, value2, "registerTime");
            return (Criteria) this;
        }

        public Criteria andRegisterTimeNotBetween(Date value1, Date value2) {
            addCriterion("register_time not between", value1, value2, "registerTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeIsNull() {
            addCriterion("first_order_time is null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeIsNotNull() {
            addCriterion("first_order_time is not null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeEqualTo(Date value) {
            addCriterion("first_order_time =", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeNotEqualTo(Date value) {
            addCriterion("first_order_time <>", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeGreaterThan(Date value) {
            addCriterion("first_order_time >", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("first_order_time >=", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeLessThan(Date value) {
            addCriterion("first_order_time <", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeLessThanOrEqualTo(Date value) {
            addCriterion("first_order_time <=", value, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeIn(List<Date> values) {
            addCriterion("first_order_time in", values, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeNotIn(List<Date> values) {
            addCriterion("first_order_time not in", values, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeBetween(Date value1, Date value2) {
            addCriterion("first_order_time between", value1, value2, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderTimeNotBetween(Date value1, Date value2) {
            addCriterion("first_order_time not between", value1, value2, "firstOrderTime");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountIsNull() {
            addCriterion("first_order_amount is null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountIsNotNull() {
            addCriterion("first_order_amount is not null");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountEqualTo(BigDecimal value) {
            addCriterion("first_order_amount =", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountNotEqualTo(BigDecimal value) {
            addCriterion("first_order_amount <>", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountGreaterThan(BigDecimal value) {
            addCriterion("first_order_amount >", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_order_amount >=", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountLessThan(BigDecimal value) {
            addCriterion("first_order_amount <", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_order_amount <=", value, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountIn(List<BigDecimal> values) {
            addCriterion("first_order_amount in", values, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountNotIn(List<BigDecimal> values) {
            addCriterion("first_order_amount not in", values, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_order_amount between", value1, value2, "firstOrderAmount");
            return (Criteria) this;
        }

        public Criteria andFirstOrderAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_order_amount not between", value1, value2, "firstOrderAmount");
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

        public Criteria andRewardStatusIsNull() {
            addCriterion("reward_status is null");
            return (Criteria) this;
        }

        public Criteria andRewardStatusIsNotNull() {
            addCriterion("reward_status is not null");
            return (Criteria) this;
        }

        public Criteria andRewardStatusEqualTo(Byte value) {
            addCriterion("reward_status =", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusNotEqualTo(Byte value) {
            addCriterion("reward_status <>", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusGreaterThan(Byte value) {
            addCriterion("reward_status >", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("reward_status >=", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusLessThan(Byte value) {
            addCriterion("reward_status <", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusLessThanOrEqualTo(Byte value) {
            addCriterion("reward_status <=", value, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusIn(List<Byte> values) {
            addCriterion("reward_status in", values, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusNotIn(List<Byte> values) {
            addCriterion("reward_status not in", values, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusBetween(Byte value1, Byte value2) {
            addCriterion("reward_status between", value1, value2, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andRewardStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("reward_status not between", value1, value2, "rewardStatus");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoIsNull() {
            addCriterion("device_info is null");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoIsNotNull() {
            addCriterion("device_info is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoEqualTo(String value) {
            addCriterion("device_info =", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoNotEqualTo(String value) {
            addCriterion("device_info <>", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoGreaterThan(String value) {
            addCriterion("device_info >", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoGreaterThanOrEqualTo(String value) {
            addCriterion("device_info >=", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoLessThan(String value) {
            addCriterion("device_info <", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoLessThanOrEqualTo(String value) {
            addCriterion("device_info <=", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoLike(String value) {
            addCriterion("device_info like", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoNotLike(String value) {
            addCriterion("device_info not like", value, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoIn(List<String> values) {
            addCriterion("device_info in", values, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoNotIn(List<String> values) {
            addCriterion("device_info not in", values, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoBetween(String value1, String value2) {
            addCriterion("device_info between", value1, value2, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andDeviceInfoNotBetween(String value1, String value2) {
            addCriterion("device_info not between", value1, value2, "deviceInfo");
            return (Criteria) this;
        }

        public Criteria andIpAddressIsNull() {
            addCriterion("ip_address is null");
            return (Criteria) this;
        }

        public Criteria andIpAddressIsNotNull() {
            addCriterion("ip_address is not null");
            return (Criteria) this;
        }

        public Criteria andIpAddressEqualTo(String value) {
            addCriterion("ip_address =", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotEqualTo(String value) {
            addCriterion("ip_address <>", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressGreaterThan(String value) {
            addCriterion("ip_address >", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressGreaterThanOrEqualTo(String value) {
            addCriterion("ip_address >=", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLessThan(String value) {
            addCriterion("ip_address <", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLessThanOrEqualTo(String value) {
            addCriterion("ip_address <=", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLike(String value) {
            addCriterion("ip_address like", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotLike(String value) {
            addCriterion("ip_address not like", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressIn(List<String> values) {
            addCriterion("ip_address in", values, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotIn(List<String> values) {
            addCriterion("ip_address not in", values, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressBetween(String value1, String value2) {
            addCriterion("ip_address between", value1, value2, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotBetween(String value1, String value2) {
            addCriterion("ip_address not between", value1, value2, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIsNull() {
            addCriterion("scene_type is null");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIsNotNull() {
            addCriterion("scene_type is not null");
            return (Criteria) this;
        }

        public Criteria andSceneTypeEqualTo(Byte value) {
            addCriterion("scene_type =", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotEqualTo(Byte value) {
            addCriterion("scene_type <>", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeGreaterThan(Byte value) {
            addCriterion("scene_type >", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("scene_type >=", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeLessThan(Byte value) {
            addCriterion("scene_type <", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeLessThanOrEqualTo(Byte value) {
            addCriterion("scene_type <=", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIn(List<Byte> values) {
            addCriterion("scene_type in", values, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotIn(List<Byte> values) {
            addCriterion("scene_type not in", values, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeBetween(Byte value1, Byte value2) {
            addCriterion("scene_type between", value1, value2, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("scene_type not between", value1, value2, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSourcePageIsNull() {
            addCriterion("source_page is null");
            return (Criteria) this;
        }

        public Criteria andSourcePageIsNotNull() {
            addCriterion("source_page is not null");
            return (Criteria) this;
        }

        public Criteria andSourcePageEqualTo(String value) {
            addCriterion("source_page =", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageNotEqualTo(String value) {
            addCriterion("source_page <>", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageGreaterThan(String value) {
            addCriterion("source_page >", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageGreaterThanOrEqualTo(String value) {
            addCriterion("source_page >=", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageLessThan(String value) {
            addCriterion("source_page <", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageLessThanOrEqualTo(String value) {
            addCriterion("source_page <=", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageLike(String value) {
            addCriterion("source_page like", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageNotLike(String value) {
            addCriterion("source_page not like", value, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageIn(List<String> values) {
            addCriterion("source_page in", values, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageNotIn(List<String> values) {
            addCriterion("source_page not in", values, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageBetween(String value1, String value2) {
            addCriterion("source_page between", value1, value2, "sourcePage");
            return (Criteria) this;
        }

        public Criteria andSourcePageNotBetween(String value1, String value2) {
            addCriterion("source_page not between", value1, value2, "sourcePage");
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

        public Criteria andRelationTypeIsNull() {
            addCriterion("relation_type is null");
            return (Criteria) this;
        }

        public Criteria andRelationTypeIsNotNull() {
            addCriterion("relation_type is not null");
            return (Criteria) this;
        }

        public Criteria andRelationTypeEqualTo(Byte value) {
            addCriterion("relation_type =", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeNotEqualTo(Byte value) {
            addCriterion("relation_type <>", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeGreaterThan(Byte value) {
            addCriterion("relation_type >", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("relation_type >=", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeLessThan(Byte value) {
            addCriterion("relation_type <", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeLessThanOrEqualTo(Byte value) {
            addCriterion("relation_type <=", value, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeIn(List<Byte> values) {
            addCriterion("relation_type in", values, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeNotIn(List<Byte> values) {
            addCriterion("relation_type not in", values, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeBetween(Byte value1, Byte value2) {
            addCriterion("relation_type between", value1, value2, "relationType");
            return (Criteria) this;
        }

        public Criteria andRelationTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("relation_type not between", value1, value2, "relationType");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelIsNull() {
            addCriterion("distributor_level is null");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelIsNotNull() {
            addCriterion("distributor_level is not null");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelEqualTo(Byte value) {
            addCriterion("distributor_level =", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelNotEqualTo(Byte value) {
            addCriterion("distributor_level <>", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelGreaterThan(Byte value) {
            addCriterion("distributor_level >", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelGreaterThanOrEqualTo(Byte value) {
            addCriterion("distributor_level >=", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelLessThan(Byte value) {
            addCriterion("distributor_level <", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelLessThanOrEqualTo(Byte value) {
            addCriterion("distributor_level <=", value, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelIn(List<Byte> values) {
            addCriterion("distributor_level in", values, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelNotIn(List<Byte> values) {
            addCriterion("distributor_level not in", values, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelBetween(Byte value1, Byte value2) {
            addCriterion("distributor_level between", value1, value2, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andDistributorLevelNotBetween(Byte value1, Byte value2) {
            addCriterion("distributor_level not between", value1, value2, "distributorLevel");
            return (Criteria) this;
        }

        public Criteria andBindSourceIsNull() {
            addCriterion("bind_source is null");
            return (Criteria) this;
        }

        public Criteria andBindSourceIsNotNull() {
            addCriterion("bind_source is not null");
            return (Criteria) this;
        }

        public Criteria andBindSourceEqualTo(Byte value) {
            addCriterion("bind_source =", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceNotEqualTo(Byte value) {
            addCriterion("bind_source <>", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceGreaterThan(Byte value) {
            addCriterion("bind_source >", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceGreaterThanOrEqualTo(Byte value) {
            addCriterion("bind_source >=", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceLessThan(Byte value) {
            addCriterion("bind_source <", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceLessThanOrEqualTo(Byte value) {
            addCriterion("bind_source <=", value, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceIn(List<Byte> values) {
            addCriterion("bind_source in", values, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceNotIn(List<Byte> values) {
            addCriterion("bind_source not in", values, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceBetween(Byte value1, Byte value2) {
            addCriterion("bind_source between", value1, value2, "bindSource");
            return (Criteria) this;
        }

        public Criteria andBindSourceNotBetween(Byte value1, Byte value2) {
            addCriterion("bind_source not between", value1, value2, "bindSource");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeIsNull() {
            addCriterion("relation_expire_time is null");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeIsNotNull() {
            addCriterion("relation_expire_time is not null");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeEqualTo(Date value) {
            addCriterion("relation_expire_time =", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeNotEqualTo(Date value) {
            addCriterion("relation_expire_time <>", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeGreaterThan(Date value) {
            addCriterion("relation_expire_time >", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("relation_expire_time >=", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeLessThan(Date value) {
            addCriterion("relation_expire_time <", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeLessThanOrEqualTo(Date value) {
            addCriterion("relation_expire_time <=", value, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeIn(List<Date> values) {
            addCriterion("relation_expire_time in", values, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeNotIn(List<Date> values) {
            addCriterion("relation_expire_time not in", values, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeBetween(Date value1, Date value2) {
            addCriterion("relation_expire_time between", value1, value2, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andRelationExpireTimeNotBetween(Date value1, Date value2) {
            addCriterion("relation_expire_time not between", value1, value2, "relationExpireTime");
            return (Criteria) this;
        }

        public Criteria andIsPermanentIsNull() {
            addCriterion("is_permanent is null");
            return (Criteria) this;
        }

        public Criteria andIsPermanentIsNotNull() {
            addCriterion("is_permanent is not null");
            return (Criteria) this;
        }

        public Criteria andIsPermanentEqualTo(Byte value) {
            addCriterion("is_permanent =", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentNotEqualTo(Byte value) {
            addCriterion("is_permanent <>", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentGreaterThan(Byte value) {
            addCriterion("is_permanent >", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_permanent >=", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentLessThan(Byte value) {
            addCriterion("is_permanent <", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentLessThanOrEqualTo(Byte value) {
            addCriterion("is_permanent <=", value, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentIn(List<Byte> values) {
            addCriterion("is_permanent in", values, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentNotIn(List<Byte> values) {
            addCriterion("is_permanent not in", values, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentBetween(Byte value1, Byte value2) {
            addCriterion("is_permanent between", value1, value2, "isPermanent");
            return (Criteria) this;
        }

        public Criteria andIsPermanentNotBetween(Byte value1, Byte value2) {
            addCriterion("is_permanent not between", value1, value2, "isPermanent");
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