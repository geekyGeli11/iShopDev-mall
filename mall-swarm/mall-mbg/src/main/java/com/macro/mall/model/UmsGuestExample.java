package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UmsGuestExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UmsGuestExample() {
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

        public Criteria andGuestIdIsNull() {
            addCriterion("guest_id is null");
            return (Criteria) this;
        }

        public Criteria andGuestIdIsNotNull() {
            addCriterion("guest_id is not null");
            return (Criteria) this;
        }

        public Criteria andGuestIdEqualTo(String value) {
            addCriterion("guest_id =", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdNotEqualTo(String value) {
            addCriterion("guest_id <>", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdGreaterThan(String value) {
            addCriterion("guest_id >", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdGreaterThanOrEqualTo(String value) {
            addCriterion("guest_id >=", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdLessThan(String value) {
            addCriterion("guest_id <", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdLessThanOrEqualTo(String value) {
            addCriterion("guest_id <=", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdLike(String value) {
            addCriterion("guest_id like", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdNotLike(String value) {
            addCriterion("guest_id not like", value, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdIn(List<String> values) {
            addCriterion("guest_id in", values, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdNotIn(List<String> values) {
            addCriterion("guest_id not in", values, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdBetween(String value1, String value2) {
            addCriterion("guest_id between", value1, value2, "guestId");
            return (Criteria) this;
        }

        public Criteria andGuestIdNotBetween(String value1, String value2) {
            addCriterion("guest_id not between", value1, value2, "guestId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIsNull() {
            addCriterion("device_id is null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIsNotNull() {
            addCriterion("device_id is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdEqualTo(String value) {
            addCriterion("device_id =", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotEqualTo(String value) {
            addCriterion("device_id <>", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThan(String value) {
            addCriterion("device_id >", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThanOrEqualTo(String value) {
            addCriterion("device_id >=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThan(String value) {
            addCriterion("device_id <", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThanOrEqualTo(String value) {
            addCriterion("device_id <=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLike(String value) {
            addCriterion("device_id like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotLike(String value) {
            addCriterion("device_id not like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIn(List<String> values) {
            addCriterion("device_id in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotIn(List<String> values) {
            addCriterion("device_id not in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdBetween(String value1, String value2) {
            addCriterion("device_id between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotBetween(String value1, String value2) {
            addCriterion("device_id not between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIsNull() {
            addCriterion("device_type is null");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIsNotNull() {
            addCriterion("device_type is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeEqualTo(String value) {
            addCriterion("device_type =", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotEqualTo(String value) {
            addCriterion("device_type <>", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeGreaterThan(String value) {
            addCriterion("device_type >", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeGreaterThanOrEqualTo(String value) {
            addCriterion("device_type >=", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLessThan(String value) {
            addCriterion("device_type <", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLessThanOrEqualTo(String value) {
            addCriterion("device_type <=", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLike(String value) {
            addCriterion("device_type like", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotLike(String value) {
            addCriterion("device_type not like", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIn(List<String> values) {
            addCriterion("device_type in", values, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotIn(List<String> values) {
            addCriterion("device_type not in", values, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeBetween(String value1, String value2) {
            addCriterion("device_type between", value1, value2, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotBetween(String value1, String value2) {
            addCriterion("device_type not between", value1, value2, "deviceType");
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

        public Criteria andLastActiveTimeIsNull() {
            addCriterion("last_active_time is null");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeIsNotNull() {
            addCriterion("last_active_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeEqualTo(Date value) {
            addCriterion("last_active_time =", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeNotEqualTo(Date value) {
            addCriterion("last_active_time <>", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeGreaterThan(Date value) {
            addCriterion("last_active_time >", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_active_time >=", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeLessThan(Date value) {
            addCriterion("last_active_time <", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_active_time <=", value, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeIn(List<Date> values) {
            addCriterion("last_active_time in", values, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeNotIn(List<Date> values) {
            addCriterion("last_active_time not in", values, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeBetween(Date value1, Date value2) {
            addCriterion("last_active_time between", value1, value2, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastActiveTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_active_time not between", value1, value2, "lastActiveTime");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpIsNull() {
            addCriterion("last_access_ip is null");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpIsNotNull() {
            addCriterion("last_access_ip is not null");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpEqualTo(String value) {
            addCriterion("last_access_ip =", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpNotEqualTo(String value) {
            addCriterion("last_access_ip <>", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpGreaterThan(String value) {
            addCriterion("last_access_ip >", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpGreaterThanOrEqualTo(String value) {
            addCriterion("last_access_ip >=", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpLessThan(String value) {
            addCriterion("last_access_ip <", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpLessThanOrEqualTo(String value) {
            addCriterion("last_access_ip <=", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpLike(String value) {
            addCriterion("last_access_ip like", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpNotLike(String value) {
            addCriterion("last_access_ip not like", value, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpIn(List<String> values) {
            addCriterion("last_access_ip in", values, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpNotIn(List<String> values) {
            addCriterion("last_access_ip not in", values, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpBetween(String value1, String value2) {
            addCriterion("last_access_ip between", value1, value2, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andLastAccessIpNotBetween(String value1, String value2) {
            addCriterion("last_access_ip not between", value1, value2, "lastAccessIp");
            return (Criteria) this;
        }

        public Criteria andUserAgentIsNull() {
            addCriterion("user_agent is null");
            return (Criteria) this;
        }

        public Criteria andUserAgentIsNotNull() {
            addCriterion("user_agent is not null");
            return (Criteria) this;
        }

        public Criteria andUserAgentEqualTo(String value) {
            addCriterion("user_agent =", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentNotEqualTo(String value) {
            addCriterion("user_agent <>", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentGreaterThan(String value) {
            addCriterion("user_agent >", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentGreaterThanOrEqualTo(String value) {
            addCriterion("user_agent >=", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentLessThan(String value) {
            addCriterion("user_agent <", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentLessThanOrEqualTo(String value) {
            addCriterion("user_agent <=", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentLike(String value) {
            addCriterion("user_agent like", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentNotLike(String value) {
            addCriterion("user_agent not like", value, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentIn(List<String> values) {
            addCriterion("user_agent in", values, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentNotIn(List<String> values) {
            addCriterion("user_agent not in", values, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentBetween(String value1, String value2) {
            addCriterion("user_agent between", value1, value2, "userAgent");
            return (Criteria) this;
        }

        public Criteria andUserAgentNotBetween(String value1, String value2) {
            addCriterion("user_agent not between", value1, value2, "userAgent");
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

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderIsNull() {
            addCriterion("has_active_order is null");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderIsNotNull() {
            addCriterion("has_active_order is not null");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderEqualTo(Boolean value) {
            addCriterion("has_active_order =", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderNotEqualTo(Boolean value) {
            addCriterion("has_active_order <>", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderGreaterThan(Boolean value) {
            addCriterion("has_active_order >", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderGreaterThanOrEqualTo(Boolean value) {
            addCriterion("has_active_order >=", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderLessThan(Boolean value) {
            addCriterion("has_active_order <", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderLessThanOrEqualTo(Boolean value) {
            addCriterion("has_active_order <=", value, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderIn(List<Boolean> values) {
            addCriterion("has_active_order in", values, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderNotIn(List<Boolean> values) {
            addCriterion("has_active_order not in", values, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderBetween(Boolean value1, Boolean value2) {
            addCriterion("has_active_order between", value1, value2, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andHasActiveOrderNotBetween(Boolean value1, Boolean value2) {
            addCriterion("has_active_order not between", value1, value2, "hasActiveOrder");
            return (Criteria) this;
        }

        public Criteria andSchoolIdIsNull() {
            addCriterion("school_id is null");
            return (Criteria) this;
        }

        public Criteria andSchoolIdIsNotNull() {
            addCriterion("school_id is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolIdEqualTo(Long value) {
            addCriterion("school_id =", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdNotEqualTo(Long value) {
            addCriterion("school_id <>", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdGreaterThan(Long value) {
            addCriterion("school_id >", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdGreaterThanOrEqualTo(Long value) {
            addCriterion("school_id >=", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdLessThan(Long value) {
            addCriterion("school_id <", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdLessThanOrEqualTo(Long value) {
            addCriterion("school_id <=", value, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdIn(List<Long> values) {
            addCriterion("school_id in", values, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdNotIn(List<Long> values) {
            addCriterion("school_id not in", values, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdBetween(Long value1, Long value2) {
            addCriterion("school_id between", value1, value2, "schoolId");
            return (Criteria) this;
        }

        public Criteria andSchoolIdNotBetween(Long value1, Long value2) {
            addCriterion("school_id not between", value1, value2, "schoolId");
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