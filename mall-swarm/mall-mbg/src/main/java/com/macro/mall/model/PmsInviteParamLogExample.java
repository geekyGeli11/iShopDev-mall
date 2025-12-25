package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsInviteParamLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsInviteParamLogExample() {
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

        public Criteria andParamTimestampIsNull() {
            addCriterion("param_timestamp is null");
            return (Criteria) this;
        }

        public Criteria andParamTimestampIsNotNull() {
            addCriterion("param_timestamp is not null");
            return (Criteria) this;
        }

        public Criteria andParamTimestampEqualTo(Long value) {
            addCriterion("param_timestamp =", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampNotEqualTo(Long value) {
            addCriterion("param_timestamp <>", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampGreaterThan(Long value) {
            addCriterion("param_timestamp >", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampGreaterThanOrEqualTo(Long value) {
            addCriterion("param_timestamp >=", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampLessThan(Long value) {
            addCriterion("param_timestamp <", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampLessThanOrEqualTo(Long value) {
            addCriterion("param_timestamp <=", value, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampIn(List<Long> values) {
            addCriterion("param_timestamp in", values, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampNotIn(List<Long> values) {
            addCriterion("param_timestamp not in", values, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampBetween(Long value1, Long value2) {
            addCriterion("param_timestamp between", value1, value2, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andParamTimestampNotBetween(Long value1, Long value2) {
            addCriterion("param_timestamp not between", value1, value2, "paramTimestamp");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIsNull() {
            addCriterion("expire_time is null");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIsNotNull() {
            addCriterion("expire_time is not null");
            return (Criteria) this;
        }

        public Criteria andExpireTimeEqualTo(Date value) {
            addCriterion("expire_time =", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotEqualTo(Date value) {
            addCriterion("expire_time <>", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeGreaterThan(Date value) {
            addCriterion("expire_time >", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("expire_time >=", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeLessThan(Date value) {
            addCriterion("expire_time <", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeLessThanOrEqualTo(Date value) {
            addCriterion("expire_time <=", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIn(List<Date> values) {
            addCriterion("expire_time in", values, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotIn(List<Date> values) {
            addCriterion("expire_time not in", values, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeBetween(Date value1, Date value2) {
            addCriterion("expire_time between", value1, value2, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotBetween(Date value1, Date value2) {
            addCriterion("expire_time not between", value1, value2, "expireTime");
            return (Criteria) this;
        }

        public Criteria andUseCountIsNull() {
            addCriterion("use_count is null");
            return (Criteria) this;
        }

        public Criteria andUseCountIsNotNull() {
            addCriterion("use_count is not null");
            return (Criteria) this;
        }

        public Criteria andUseCountEqualTo(Integer value) {
            addCriterion("use_count =", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountNotEqualTo(Integer value) {
            addCriterion("use_count <>", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountGreaterThan(Integer value) {
            addCriterion("use_count >", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("use_count >=", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountLessThan(Integer value) {
            addCriterion("use_count <", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountLessThanOrEqualTo(Integer value) {
            addCriterion("use_count <=", value, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountIn(List<Integer> values) {
            addCriterion("use_count in", values, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountNotIn(List<Integer> values) {
            addCriterion("use_count not in", values, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountBetween(Integer value1, Integer value2) {
            addCriterion("use_count between", value1, value2, "useCount");
            return (Criteria) this;
        }

        public Criteria andUseCountNotBetween(Integer value1, Integer value2) {
            addCriterion("use_count not between", value1, value2, "useCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountIsNull() {
            addCriterion("max_use_count is null");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountIsNotNull() {
            addCriterion("max_use_count is not null");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountEqualTo(Integer value) {
            addCriterion("max_use_count =", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountNotEqualTo(Integer value) {
            addCriterion("max_use_count <>", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountGreaterThan(Integer value) {
            addCriterion("max_use_count >", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_use_count >=", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountLessThan(Integer value) {
            addCriterion("max_use_count <", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountLessThanOrEqualTo(Integer value) {
            addCriterion("max_use_count <=", value, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountIn(List<Integer> values) {
            addCriterion("max_use_count in", values, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountNotIn(List<Integer> values) {
            addCriterion("max_use_count not in", values, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountBetween(Integer value1, Integer value2) {
            addCriterion("max_use_count between", value1, value2, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andMaxUseCountNotBetween(Integer value1, Integer value2) {
            addCriterion("max_use_count not between", value1, value2, "maxUseCount");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlIsNull() {
            addCriterion("qr_code_url is null");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlIsNotNull() {
            addCriterion("qr_code_url is not null");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlEqualTo(String value) {
            addCriterion("qr_code_url =", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlNotEqualTo(String value) {
            addCriterion("qr_code_url <>", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlGreaterThan(String value) {
            addCriterion("qr_code_url >", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlGreaterThanOrEqualTo(String value) {
            addCriterion("qr_code_url >=", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlLessThan(String value) {
            addCriterion("qr_code_url <", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlLessThanOrEqualTo(String value) {
            addCriterion("qr_code_url <=", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlLike(String value) {
            addCriterion("qr_code_url like", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlNotLike(String value) {
            addCriterion("qr_code_url not like", value, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlIn(List<String> values) {
            addCriterion("qr_code_url in", values, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlNotIn(List<String> values) {
            addCriterion("qr_code_url not in", values, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlBetween(String value1, String value2) {
            addCriterion("qr_code_url between", value1, value2, "qrCodeUrl");
            return (Criteria) this;
        }

        public Criteria andQrCodeUrlNotBetween(String value1, String value2) {
            addCriterion("qr_code_url not between", value1, value2, "qrCodeUrl");
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

        public Criteria andLastUsedTimeIsNull() {
            addCriterion("last_used_time is null");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeIsNotNull() {
            addCriterion("last_used_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeEqualTo(Date value) {
            addCriterion("last_used_time =", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeNotEqualTo(Date value) {
            addCriterion("last_used_time <>", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeGreaterThan(Date value) {
            addCriterion("last_used_time >", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_used_time >=", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeLessThan(Date value) {
            addCriterion("last_used_time <", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_used_time <=", value, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeIn(List<Date> values) {
            addCriterion("last_used_time in", values, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeNotIn(List<Date> values) {
            addCriterion("last_used_time not in", values, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeBetween(Date value1, Date value2) {
            addCriterion("last_used_time between", value1, value2, "lastUsedTime");
            return (Criteria) this;
        }

        public Criteria andLastUsedTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_used_time not between", value1, value2, "lastUsedTime");
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