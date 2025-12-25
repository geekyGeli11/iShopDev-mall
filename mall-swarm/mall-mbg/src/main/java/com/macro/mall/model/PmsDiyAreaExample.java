package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsDiyAreaExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsDiyAreaExample() {
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

        public Criteria andSurfaceIdIsNull() {
            addCriterion("surface_id is null");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdIsNotNull() {
            addCriterion("surface_id is not null");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdEqualTo(Long value) {
            addCriterion("surface_id =", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdNotEqualTo(Long value) {
            addCriterion("surface_id <>", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdGreaterThan(Long value) {
            addCriterion("surface_id >", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("surface_id >=", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdLessThan(Long value) {
            addCriterion("surface_id <", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdLessThanOrEqualTo(Long value) {
            addCriterion("surface_id <=", value, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdIn(List<Long> values) {
            addCriterion("surface_id in", values, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdNotIn(List<Long> values) {
            addCriterion("surface_id not in", values, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdBetween(Long value1, Long value2) {
            addCriterion("surface_id between", value1, value2, "surfaceId");
            return (Criteria) this;
        }

        public Criteria andSurfaceIdNotBetween(Long value1, Long value2) {
            addCriterion("surface_id not between", value1, value2, "surfaceId");
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

        public Criteria andBoundsIsNull() {
            addCriterion("bounds is null");
            return (Criteria) this;
        }

        public Criteria andBoundsIsNotNull() {
            addCriterion("bounds is not null");
            return (Criteria) this;
        }

        public Criteria andBoundsEqualTo(String value) {
            addCriterion("bounds =", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsNotEqualTo(String value) {
            addCriterion("bounds <>", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsGreaterThan(String value) {
            addCriterion("bounds >", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsGreaterThanOrEqualTo(String value) {
            addCriterion("bounds >=", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsLessThan(String value) {
            addCriterion("bounds <", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsLessThanOrEqualTo(String value) {
            addCriterion("bounds <=", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsLike(String value) {
            addCriterion("bounds like", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsNotLike(String value) {
            addCriterion("bounds not like", value, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsIn(List<String> values) {
            addCriterion("bounds in", values, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsNotIn(List<String> values) {
            addCriterion("bounds not in", values, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsBetween(String value1, String value2) {
            addCriterion("bounds between", value1, value2, "bounds");
            return (Criteria) this;
        }

        public Criteria andBoundsNotBetween(String value1, String value2) {
            addCriterion("bounds not between", value1, value2, "bounds");
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

        public Criteria andMaskImageUrlIsNull() {
            addCriterion("mask_image_url is null");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlIsNotNull() {
            addCriterion("mask_image_url is not null");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlEqualTo(String value) {
            addCriterion("mask_image_url =", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlNotEqualTo(String value) {
            addCriterion("mask_image_url <>", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlGreaterThan(String value) {
            addCriterion("mask_image_url >", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlGreaterThanOrEqualTo(String value) {
            addCriterion("mask_image_url >=", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlLessThan(String value) {
            addCriterion("mask_image_url <", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlLessThanOrEqualTo(String value) {
            addCriterion("mask_image_url <=", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlLike(String value) {
            addCriterion("mask_image_url like", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlNotLike(String value) {
            addCriterion("mask_image_url not like", value, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlIn(List<String> values) {
            addCriterion("mask_image_url in", values, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlNotIn(List<String> values) {
            addCriterion("mask_image_url not in", values, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlBetween(String value1, String value2) {
            addCriterion("mask_image_url between", value1, value2, "maskImageUrl");
            return (Criteria) this;
        }

        public Criteria andMaskImageUrlNotBetween(String value1, String value2) {
            addCriterion("mask_image_url not between", value1, value2, "maskImageUrl");
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