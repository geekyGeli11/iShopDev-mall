package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SmsFreightTemplateRegionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SmsFreightTemplateRegionExample() {
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

        public Criteria andTemplateIdIsNull() {
            addCriterion("template_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNotNull() {
            addCriterion("template_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdEqualTo(Long value) {
            addCriterion("template_id =", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotEqualTo(Long value) {
            addCriterion("template_id <>", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThan(Long value) {
            addCriterion("template_id >", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_id >=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThan(Long value) {
            addCriterion("template_id <", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("template_id <=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIn(List<Long> values) {
            addCriterion("template_id in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotIn(List<Long> values) {
            addCriterion("template_id not in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdBetween(Long value1, Long value2) {
            addCriterion("template_id between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("template_id not between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andRegionTypeIsNull() {
            addCriterion("region_type is null");
            return (Criteria) this;
        }

        public Criteria andRegionTypeIsNotNull() {
            addCriterion("region_type is not null");
            return (Criteria) this;
        }

        public Criteria andRegionTypeEqualTo(Byte value) {
            addCriterion("region_type =", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotEqualTo(Byte value) {
            addCriterion("region_type <>", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeGreaterThan(Byte value) {
            addCriterion("region_type >", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("region_type >=", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeLessThan(Byte value) {
            addCriterion("region_type <", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeLessThanOrEqualTo(Byte value) {
            addCriterion("region_type <=", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeIn(List<Byte> values) {
            addCriterion("region_type in", values, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotIn(List<Byte> values) {
            addCriterion("region_type not in", values, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeBetween(Byte value1, Byte value2) {
            addCriterion("region_type between", value1, value2, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("region_type not between", value1, value2, "regionType");
            return (Criteria) this;
        }

        public Criteria andDistanceStartIsNull() {
            addCriterion("distance_start is null");
            return (Criteria) this;
        }

        public Criteria andDistanceStartIsNotNull() {
            addCriterion("distance_start is not null");
            return (Criteria) this;
        }

        public Criteria andDistanceStartEqualTo(BigDecimal value) {
            addCriterion("distance_start =", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartNotEqualTo(BigDecimal value) {
            addCriterion("distance_start <>", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartGreaterThan(BigDecimal value) {
            addCriterion("distance_start >", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("distance_start >=", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartLessThan(BigDecimal value) {
            addCriterion("distance_start <", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartLessThanOrEqualTo(BigDecimal value) {
            addCriterion("distance_start <=", value, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartIn(List<BigDecimal> values) {
            addCriterion("distance_start in", values, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartNotIn(List<BigDecimal> values) {
            addCriterion("distance_start not in", values, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("distance_start between", value1, value2, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceStartNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("distance_start not between", value1, value2, "distanceStart");
            return (Criteria) this;
        }

        public Criteria andDistanceEndIsNull() {
            addCriterion("distance_end is null");
            return (Criteria) this;
        }

        public Criteria andDistanceEndIsNotNull() {
            addCriterion("distance_end is not null");
            return (Criteria) this;
        }

        public Criteria andDistanceEndEqualTo(BigDecimal value) {
            addCriterion("distance_end =", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndNotEqualTo(BigDecimal value) {
            addCriterion("distance_end <>", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndGreaterThan(BigDecimal value) {
            addCriterion("distance_end >", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("distance_end >=", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndLessThan(BigDecimal value) {
            addCriterion("distance_end <", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndLessThanOrEqualTo(BigDecimal value) {
            addCriterion("distance_end <=", value, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndIn(List<BigDecimal> values) {
            addCriterion("distance_end in", values, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndNotIn(List<BigDecimal> values) {
            addCriterion("distance_end not in", values, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("distance_end between", value1, value2, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andDistanceEndNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("distance_end not between", value1, value2, "distanceEnd");
            return (Criteria) this;
        }

        public Criteria andFirstAmountIsNull() {
            addCriterion("first_amount is null");
            return (Criteria) this;
        }

        public Criteria andFirstAmountIsNotNull() {
            addCriterion("first_amount is not null");
            return (Criteria) this;
        }

        public Criteria andFirstAmountEqualTo(BigDecimal value) {
            addCriterion("first_amount =", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountNotEqualTo(BigDecimal value) {
            addCriterion("first_amount <>", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountGreaterThan(BigDecimal value) {
            addCriterion("first_amount >", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_amount >=", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountLessThan(BigDecimal value) {
            addCriterion("first_amount <", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_amount <=", value, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountIn(List<BigDecimal> values) {
            addCriterion("first_amount in", values, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountNotIn(List<BigDecimal> values) {
            addCriterion("first_amount not in", values, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_amount between", value1, value2, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_amount not between", value1, value2, "firstAmount");
            return (Criteria) this;
        }

        public Criteria andFirstCountIsNull() {
            addCriterion("first_count is null");
            return (Criteria) this;
        }

        public Criteria andFirstCountIsNotNull() {
            addCriterion("first_count is not null");
            return (Criteria) this;
        }

        public Criteria andFirstCountEqualTo(BigDecimal value) {
            addCriterion("first_count =", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountNotEqualTo(BigDecimal value) {
            addCriterion("first_count <>", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountGreaterThan(BigDecimal value) {
            addCriterion("first_count >", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_count >=", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountLessThan(BigDecimal value) {
            addCriterion("first_count <", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_count <=", value, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountIn(List<BigDecimal> values) {
            addCriterion("first_count in", values, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountNotIn(List<BigDecimal> values) {
            addCriterion("first_count not in", values, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_count between", value1, value2, "firstCount");
            return (Criteria) this;
        }

        public Criteria andFirstCountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_count not between", value1, value2, "firstCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountIsNull() {
            addCriterion("additional_amount is null");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountIsNotNull() {
            addCriterion("additional_amount is not null");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountEqualTo(BigDecimal value) {
            addCriterion("additional_amount =", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountNotEqualTo(BigDecimal value) {
            addCriterion("additional_amount <>", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountGreaterThan(BigDecimal value) {
            addCriterion("additional_amount >", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("additional_amount >=", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountLessThan(BigDecimal value) {
            addCriterion("additional_amount <", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("additional_amount <=", value, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountIn(List<BigDecimal> values) {
            addCriterion("additional_amount in", values, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountNotIn(List<BigDecimal> values) {
            addCriterion("additional_amount not in", values, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("additional_amount between", value1, value2, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("additional_amount not between", value1, value2, "additionalAmount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountIsNull() {
            addCriterion("additional_count is null");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountIsNotNull() {
            addCriterion("additional_count is not null");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountEqualTo(BigDecimal value) {
            addCriterion("additional_count =", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountNotEqualTo(BigDecimal value) {
            addCriterion("additional_count <>", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountGreaterThan(BigDecimal value) {
            addCriterion("additional_count >", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("additional_count >=", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountLessThan(BigDecimal value) {
            addCriterion("additional_count <", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("additional_count <=", value, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountIn(List<BigDecimal> values) {
            addCriterion("additional_count in", values, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountNotIn(List<BigDecimal> values) {
            addCriterion("additional_count not in", values, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("additional_count between", value1, value2, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andAdditionalCountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("additional_count not between", value1, value2, "additionalCount");
            return (Criteria) this;
        }

        public Criteria andSortIsNull() {
            addCriterion("sort is null");
            return (Criteria) this;
        }

        public Criteria andSortIsNotNull() {
            addCriterion("sort is not null");
            return (Criteria) this;
        }

        public Criteria andSortEqualTo(Integer value) {
            addCriterion("sort =", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotEqualTo(Integer value) {
            addCriterion("sort <>", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThan(Integer value) {
            addCriterion("sort >", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThanOrEqualTo(Integer value) {
            addCriterion("sort >=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThan(Integer value) {
            addCriterion("sort <", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThanOrEqualTo(Integer value) {
            addCriterion("sort <=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortIn(List<Integer> values) {
            addCriterion("sort in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotIn(List<Integer> values) {
            addCriterion("sort not in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortBetween(Integer value1, Integer value2) {
            addCriterion("sort between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotBetween(Integer value1, Integer value2) {
            addCriterion("sort not between", value1, value2, "sort");
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