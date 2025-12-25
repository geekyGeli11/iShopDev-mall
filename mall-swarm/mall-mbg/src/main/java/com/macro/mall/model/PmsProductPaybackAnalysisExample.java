package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PmsProductPaybackAnalysisExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsProductPaybackAnalysisExample() {
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

        public Criteria andProductIdIsNull() {
            addCriterion("product_id is null");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNotNull() {
            addCriterion("product_id is not null");
            return (Criteria) this;
        }

        public Criteria andProductIdEqualTo(Long value) {
            addCriterion("product_id =", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotEqualTo(Long value) {
            addCriterion("product_id <>", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThan(Long value) {
            addCriterion("product_id >", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThanOrEqualTo(Long value) {
            addCriterion("product_id >=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThan(Long value) {
            addCriterion("product_id <", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThanOrEqualTo(Long value) {
            addCriterion("product_id <=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdIn(List<Long> values) {
            addCriterion("product_id in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotIn(List<Long> values) {
            addCriterion("product_id not in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdBetween(Long value1, Long value2) {
            addCriterion("product_id between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotBetween(Long value1, Long value2) {
            addCriterion("product_id not between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductNameIsNull() {
            addCriterion("product_name is null");
            return (Criteria) this;
        }

        public Criteria andProductNameIsNotNull() {
            addCriterion("product_name is not null");
            return (Criteria) this;
        }

        public Criteria andProductNameEqualTo(String value) {
            addCriterion("product_name =", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotEqualTo(String value) {
            addCriterion("product_name <>", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameGreaterThan(String value) {
            addCriterion("product_name >", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameGreaterThanOrEqualTo(String value) {
            addCriterion("product_name >=", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLessThan(String value) {
            addCriterion("product_name <", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLessThanOrEqualTo(String value) {
            addCriterion("product_name <=", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameLike(String value) {
            addCriterion("product_name like", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotLike(String value) {
            addCriterion("product_name not like", value, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameIn(List<String> values) {
            addCriterion("product_name in", values, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotIn(List<String> values) {
            addCriterion("product_name not in", values, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameBetween(String value1, String value2) {
            addCriterion("product_name between", value1, value2, "productName");
            return (Criteria) this;
        }

        public Criteria andProductNameNotBetween(String value1, String value2) {
            addCriterion("product_name not between", value1, value2, "productName");
            return (Criteria) this;
        }

        public Criteria andProductSnIsNull() {
            addCriterion("product_sn is null");
            return (Criteria) this;
        }

        public Criteria andProductSnIsNotNull() {
            addCriterion("product_sn is not null");
            return (Criteria) this;
        }

        public Criteria andProductSnEqualTo(String value) {
            addCriterion("product_sn =", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotEqualTo(String value) {
            addCriterion("product_sn <>", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnGreaterThan(String value) {
            addCriterion("product_sn >", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnGreaterThanOrEqualTo(String value) {
            addCriterion("product_sn >=", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLessThan(String value) {
            addCriterion("product_sn <", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLessThanOrEqualTo(String value) {
            addCriterion("product_sn <=", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnLike(String value) {
            addCriterion("product_sn like", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotLike(String value) {
            addCriterion("product_sn not like", value, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnIn(List<String> values) {
            addCriterion("product_sn in", values, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotIn(List<String> values) {
            addCriterion("product_sn not in", values, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnBetween(String value1, String value2) {
            addCriterion("product_sn between", value1, value2, "productSn");
            return (Criteria) this;
        }

        public Criteria andProductSnNotBetween(String value1, String value2) {
            addCriterion("product_sn not between", value1, value2, "productSn");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityIsNull() {
            addCriterion("target_quantity is null");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityIsNotNull() {
            addCriterion("target_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityEqualTo(Integer value) {
            addCriterion("target_quantity =", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityNotEqualTo(Integer value) {
            addCriterion("target_quantity <>", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityGreaterThan(Integer value) {
            addCriterion("target_quantity >", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("target_quantity >=", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityLessThan(Integer value) {
            addCriterion("target_quantity <", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("target_quantity <=", value, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityIn(List<Integer> values) {
            addCriterion("target_quantity in", values, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityNotIn(List<Integer> values) {
            addCriterion("target_quantity not in", values, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityBetween(Integer value1, Integer value2) {
            addCriterion("target_quantity between", value1, value2, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("target_quantity not between", value1, value2, "targetQuantity");
            return (Criteria) this;
        }

        public Criteria andTargetAmountIsNull() {
            addCriterion("target_amount is null");
            return (Criteria) this;
        }

        public Criteria andTargetAmountIsNotNull() {
            addCriterion("target_amount is not null");
            return (Criteria) this;
        }

        public Criteria andTargetAmountEqualTo(BigDecimal value) {
            addCriterion("target_amount =", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountNotEqualTo(BigDecimal value) {
            addCriterion("target_amount <>", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountGreaterThan(BigDecimal value) {
            addCriterion("target_amount >", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("target_amount >=", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountLessThan(BigDecimal value) {
            addCriterion("target_amount <", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("target_amount <=", value, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountIn(List<BigDecimal> values) {
            addCriterion("target_amount in", values, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountNotIn(List<BigDecimal> values) {
            addCriterion("target_amount not in", values, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("target_amount between", value1, value2, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andTargetAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("target_amount not between", value1, value2, "targetAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityIsNull() {
            addCriterion("current_sold_quantity is null");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityIsNotNull() {
            addCriterion("current_sold_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityEqualTo(Integer value) {
            addCriterion("current_sold_quantity =", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityNotEqualTo(Integer value) {
            addCriterion("current_sold_quantity <>", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityGreaterThan(Integer value) {
            addCriterion("current_sold_quantity >", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_sold_quantity >=", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityLessThan(Integer value) {
            addCriterion("current_sold_quantity <", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("current_sold_quantity <=", value, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityIn(List<Integer> values) {
            addCriterion("current_sold_quantity in", values, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityNotIn(List<Integer> values) {
            addCriterion("current_sold_quantity not in", values, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityBetween(Integer value1, Integer value2) {
            addCriterion("current_sold_quantity between", value1, value2, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("current_sold_quantity not between", value1, value2, "currentSoldQuantity");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountIsNull() {
            addCriterion("current_sold_amount is null");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountIsNotNull() {
            addCriterion("current_sold_amount is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountEqualTo(BigDecimal value) {
            addCriterion("current_sold_amount =", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountNotEqualTo(BigDecimal value) {
            addCriterion("current_sold_amount <>", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountGreaterThan(BigDecimal value) {
            addCriterion("current_sold_amount >", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("current_sold_amount >=", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountLessThan(BigDecimal value) {
            addCriterion("current_sold_amount <", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("current_sold_amount <=", value, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountIn(List<BigDecimal> values) {
            addCriterion("current_sold_amount in", values, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountNotIn(List<BigDecimal> values) {
            addCriterion("current_sold_amount not in", values, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("current_sold_amount between", value1, value2, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andCurrentSoldAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("current_sold_amount not between", value1, value2, "currentSoldAmount");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressIsNull() {
            addCriterion("payback_progress is null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressIsNotNull() {
            addCriterion("payback_progress is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressEqualTo(BigDecimal value) {
            addCriterion("payback_progress =", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressNotEqualTo(BigDecimal value) {
            addCriterion("payback_progress <>", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressGreaterThan(BigDecimal value) {
            addCriterion("payback_progress >", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress >=", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressLessThan(BigDecimal value) {
            addCriterion("payback_progress <", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressLessThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress <=", value, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressIn(List<BigDecimal> values) {
            addCriterion("payback_progress in", values, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressNotIn(List<BigDecimal> values) {
            addCriterion("payback_progress not in", values, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress between", value1, value2, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress not between", value1, value2, "paybackProgress");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusIsNull() {
            addCriterion("payback_status is null");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusIsNotNull() {
            addCriterion("payback_status is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusEqualTo(Byte value) {
            addCriterion("payback_status =", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusNotEqualTo(Byte value) {
            addCriterion("payback_status <>", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusGreaterThan(Byte value) {
            addCriterion("payback_status >", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("payback_status >=", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusLessThan(Byte value) {
            addCriterion("payback_status <", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusLessThanOrEqualTo(Byte value) {
            addCriterion("payback_status <=", value, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusIn(List<Byte> values) {
            addCriterion("payback_status in", values, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusNotIn(List<Byte> values) {
            addCriterion("payback_status not in", values, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusBetween(Byte value1, Byte value2) {
            addCriterion("payback_status between", value1, value2, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andPaybackStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("payback_status not between", value1, value2, "paybackStatus");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNull() {
            addCriterion("start_date is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("start_date is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterionForJDBCDate("start_date =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("start_date <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterionForJDBCDate("start_date >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_date >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterionForJDBCDate("start_date <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_date <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterionForJDBCDate("start_date in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("start_date not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_date between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_date not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateIsNull() {
            addCriterion("payback_completed_date is null");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateIsNotNull() {
            addCriterion("payback_completed_date is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateEqualTo(Date value) {
            addCriterionForJDBCDate("payback_completed_date =", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("payback_completed_date <>", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateGreaterThan(Date value) {
            addCriterionForJDBCDate("payback_completed_date >", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("payback_completed_date >=", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateLessThan(Date value) {
            addCriterionForJDBCDate("payback_completed_date <", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("payback_completed_date <=", value, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateIn(List<Date> values) {
            addCriterionForJDBCDate("payback_completed_date in", values, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("payback_completed_date not in", values, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("payback_completed_date between", value1, value2, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackCompletedDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("payback_completed_date not between", value1, value2, "paybackCompletedDate");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysIsNull() {
            addCriterion("payback_days is null");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysIsNotNull() {
            addCriterion("payback_days is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysEqualTo(Integer value) {
            addCriterion("payback_days =", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysNotEqualTo(Integer value) {
            addCriterion("payback_days <>", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysGreaterThan(Integer value) {
            addCriterion("payback_days >", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysGreaterThanOrEqualTo(Integer value) {
            addCriterion("payback_days >=", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysLessThan(Integer value) {
            addCriterion("payback_days <", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysLessThanOrEqualTo(Integer value) {
            addCriterion("payback_days <=", value, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysIn(List<Integer> values) {
            addCriterion("payback_days in", values, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysNotIn(List<Integer> values) {
            addCriterion("payback_days not in", values, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysBetween(Integer value1, Integer value2) {
            addCriterion("payback_days between", value1, value2, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andPaybackDaysNotBetween(Integer value1, Integer value2) {
            addCriterion("payback_days not between", value1, value2, "paybackDays");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityIsNull() {
            addCriterion("daily_avg_quantity is null");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityIsNotNull() {
            addCriterion("daily_avg_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityEqualTo(BigDecimal value) {
            addCriterion("daily_avg_quantity =", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityNotEqualTo(BigDecimal value) {
            addCriterion("daily_avg_quantity <>", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityGreaterThan(BigDecimal value) {
            addCriterion("daily_avg_quantity >", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("daily_avg_quantity >=", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityLessThan(BigDecimal value) {
            addCriterion("daily_avg_quantity <", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityLessThanOrEqualTo(BigDecimal value) {
            addCriterion("daily_avg_quantity <=", value, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityIn(List<BigDecimal> values) {
            addCriterion("daily_avg_quantity in", values, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityNotIn(List<BigDecimal> values) {
            addCriterion("daily_avg_quantity not in", values, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("daily_avg_quantity between", value1, value2, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andDailyAvgQuantityNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("daily_avg_quantity not between", value1, value2, "dailyAvgQuantity");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateIsNull() {
            addCriterion("predicted_completion_date is null");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateIsNotNull() {
            addCriterion("predicted_completion_date is not null");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateEqualTo(Date value) {
            addCriterionForJDBCDate("predicted_completion_date =", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("predicted_completion_date <>", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateGreaterThan(Date value) {
            addCriterionForJDBCDate("predicted_completion_date >", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predicted_completion_date >=", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateLessThan(Date value) {
            addCriterionForJDBCDate("predicted_completion_date <", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predicted_completion_date <=", value, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateIn(List<Date> values) {
            addCriterionForJDBCDate("predicted_completion_date in", values, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("predicted_completion_date not in", values, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predicted_completion_date between", value1, value2, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andPredictedCompletionDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predicted_completion_date not between", value1, value2, "predictedCompletionDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateIsNull() {
            addCriterion("last_order_date is null");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateIsNotNull() {
            addCriterion("last_order_date is not null");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateEqualTo(Date value) {
            addCriterionForJDBCDate("last_order_date =", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("last_order_date <>", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateGreaterThan(Date value) {
            addCriterionForJDBCDate("last_order_date >", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("last_order_date >=", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateLessThan(Date value) {
            addCriterionForJDBCDate("last_order_date <", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("last_order_date <=", value, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateIn(List<Date> values) {
            addCriterionForJDBCDate("last_order_date in", values, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("last_order_date not in", values, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("last_order_date between", value1, value2, "lastOrderDate");
            return (Criteria) this;
        }

        public Criteria andLastOrderDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("last_order_date not between", value1, value2, "lastOrderDate");
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