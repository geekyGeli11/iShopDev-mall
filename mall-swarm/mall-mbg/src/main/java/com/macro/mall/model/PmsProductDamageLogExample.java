package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsProductDamageLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsProductDamageLogExample() {
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

        public Criteria andDamageReportIdIsNull() {
            addCriterion("damage_report_id is null");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdIsNotNull() {
            addCriterion("damage_report_id is not null");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdEqualTo(Long value) {
            addCriterion("damage_report_id =", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdNotEqualTo(Long value) {
            addCriterion("damage_report_id <>", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdGreaterThan(Long value) {
            addCriterion("damage_report_id >", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdGreaterThanOrEqualTo(Long value) {
            addCriterion("damage_report_id >=", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdLessThan(Long value) {
            addCriterion("damage_report_id <", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdLessThanOrEqualTo(Long value) {
            addCriterion("damage_report_id <=", value, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdIn(List<Long> values) {
            addCriterion("damage_report_id in", values, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdNotIn(List<Long> values) {
            addCriterion("damage_report_id not in", values, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdBetween(Long value1, Long value2) {
            addCriterion("damage_report_id between", value1, value2, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andDamageReportIdNotBetween(Long value1, Long value2) {
            addCriterion("damage_report_id not between", value1, value2, "damageReportId");
            return (Criteria) this;
        }

        public Criteria andActionTypeIsNull() {
            addCriterion("action_type is null");
            return (Criteria) this;
        }

        public Criteria andActionTypeIsNotNull() {
            addCriterion("action_type is not null");
            return (Criteria) this;
        }

        public Criteria andActionTypeEqualTo(Byte value) {
            addCriterion("action_type =", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotEqualTo(Byte value) {
            addCriterion("action_type <>", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeGreaterThan(Byte value) {
            addCriterion("action_type >", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("action_type >=", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeLessThan(Byte value) {
            addCriterion("action_type <", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeLessThanOrEqualTo(Byte value) {
            addCriterion("action_type <=", value, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeIn(List<Byte> values) {
            addCriterion("action_type in", values, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotIn(List<Byte> values) {
            addCriterion("action_type not in", values, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeBetween(Byte value1, Byte value2) {
            addCriterion("action_type between", value1, value2, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("action_type not between", value1, value2, "actionType");
            return (Criteria) this;
        }

        public Criteria andActionContentIsNull() {
            addCriterion("action_content is null");
            return (Criteria) this;
        }

        public Criteria andActionContentIsNotNull() {
            addCriterion("action_content is not null");
            return (Criteria) this;
        }

        public Criteria andActionContentEqualTo(String value) {
            addCriterion("action_content =", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentNotEqualTo(String value) {
            addCriterion("action_content <>", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentGreaterThan(String value) {
            addCriterion("action_content >", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentGreaterThanOrEqualTo(String value) {
            addCriterion("action_content >=", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentLessThan(String value) {
            addCriterion("action_content <", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentLessThanOrEqualTo(String value) {
            addCriterion("action_content <=", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentLike(String value) {
            addCriterion("action_content like", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentNotLike(String value) {
            addCriterion("action_content not like", value, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentIn(List<String> values) {
            addCriterion("action_content in", values, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentNotIn(List<String> values) {
            addCriterion("action_content not in", values, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentBetween(String value1, String value2) {
            addCriterion("action_content between", value1, value2, "actionContent");
            return (Criteria) this;
        }

        public Criteria andActionContentNotBetween(String value1, String value2) {
            addCriterion("action_content not between", value1, value2, "actionContent");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusIsNull() {
            addCriterion("before_status is null");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusIsNotNull() {
            addCriterion("before_status is not null");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusEqualTo(Byte value) {
            addCriterion("before_status =", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusNotEqualTo(Byte value) {
            addCriterion("before_status <>", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusGreaterThan(Byte value) {
            addCriterion("before_status >", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("before_status >=", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusLessThan(Byte value) {
            addCriterion("before_status <", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusLessThanOrEqualTo(Byte value) {
            addCriterion("before_status <=", value, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusIn(List<Byte> values) {
            addCriterion("before_status in", values, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusNotIn(List<Byte> values) {
            addCriterion("before_status not in", values, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusBetween(Byte value1, Byte value2) {
            addCriterion("before_status between", value1, value2, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andBeforeStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("before_status not between", value1, value2, "beforeStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusIsNull() {
            addCriterion("after_status is null");
            return (Criteria) this;
        }

        public Criteria andAfterStatusIsNotNull() {
            addCriterion("after_status is not null");
            return (Criteria) this;
        }

        public Criteria andAfterStatusEqualTo(Byte value) {
            addCriterion("after_status =", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusNotEqualTo(Byte value) {
            addCriterion("after_status <>", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusGreaterThan(Byte value) {
            addCriterion("after_status >", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("after_status >=", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusLessThan(Byte value) {
            addCriterion("after_status <", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusLessThanOrEqualTo(Byte value) {
            addCriterion("after_status <=", value, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusIn(List<Byte> values) {
            addCriterion("after_status in", values, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusNotIn(List<Byte> values) {
            addCriterion("after_status not in", values, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusBetween(Byte value1, Byte value2) {
            addCriterion("after_status between", value1, value2, "afterStatus");
            return (Criteria) this;
        }

        public Criteria andAfterStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("after_status not between", value1, value2, "afterStatus");
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