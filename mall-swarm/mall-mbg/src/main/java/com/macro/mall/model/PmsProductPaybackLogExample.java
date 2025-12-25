package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PmsProductPaybackLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsProductPaybackLogExample() {
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

        public Criteria andBatchIdIsNull() {
            addCriterion("batch_id is null");
            return (Criteria) this;
        }

        public Criteria andBatchIdIsNotNull() {
            addCriterion("batch_id is not null");
            return (Criteria) this;
        }

        public Criteria andBatchIdEqualTo(Long value) {
            addCriterion("batch_id =", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdNotEqualTo(Long value) {
            addCriterion("batch_id <>", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdGreaterThan(Long value) {
            addCriterion("batch_id >", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("batch_id >=", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdLessThan(Long value) {
            addCriterion("batch_id <", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdLessThanOrEqualTo(Long value) {
            addCriterion("batch_id <=", value, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdIn(List<Long> values) {
            addCriterion("batch_id in", values, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdNotIn(List<Long> values) {
            addCriterion("batch_id not in", values, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdBetween(Long value1, Long value2) {
            addCriterion("batch_id between", value1, value2, "batchId");
            return (Criteria) this;
        }

        public Criteria andBatchIdNotBetween(Long value1, Long value2) {
            addCriterion("batch_id not between", value1, value2, "batchId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(Long value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(Long value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(Long value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(Long value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<Long> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<Long> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(Long value1, Long value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderSnIsNull() {
            addCriterion("order_sn is null");
            return (Criteria) this;
        }

        public Criteria andOrderSnIsNotNull() {
            addCriterion("order_sn is not null");
            return (Criteria) this;
        }

        public Criteria andOrderSnEqualTo(String value) {
            addCriterion("order_sn =", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnNotEqualTo(String value) {
            addCriterion("order_sn <>", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnGreaterThan(String value) {
            addCriterion("order_sn >", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnGreaterThanOrEqualTo(String value) {
            addCriterion("order_sn >=", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnLessThan(String value) {
            addCriterion("order_sn <", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnLessThanOrEqualTo(String value) {
            addCriterion("order_sn <=", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnLike(String value) {
            addCriterion("order_sn like", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnNotLike(String value) {
            addCriterion("order_sn not like", value, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnIn(List<String> values) {
            addCriterion("order_sn in", values, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnNotIn(List<String> values) {
            addCriterion("order_sn not in", values, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnBetween(String value1, String value2) {
            addCriterion("order_sn between", value1, value2, "orderSn");
            return (Criteria) this;
        }

        public Criteria andOrderSnNotBetween(String value1, String value2) {
            addCriterion("order_sn not between", value1, value2, "orderSn");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityIsNull() {
            addCriterion("sold_quantity is null");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityIsNotNull() {
            addCriterion("sold_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityEqualTo(Integer value) {
            addCriterion("sold_quantity =", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityNotEqualTo(Integer value) {
            addCriterion("sold_quantity <>", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityGreaterThan(Integer value) {
            addCriterion("sold_quantity >", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("sold_quantity >=", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityLessThan(Integer value) {
            addCriterion("sold_quantity <", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("sold_quantity <=", value, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityIn(List<Integer> values) {
            addCriterion("sold_quantity in", values, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityNotIn(List<Integer> values) {
            addCriterion("sold_quantity not in", values, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityBetween(Integer value1, Integer value2) {
            addCriterion("sold_quantity between", value1, value2, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("sold_quantity not between", value1, value2, "soldQuantity");
            return (Criteria) this;
        }

        public Criteria andSoldAmountIsNull() {
            addCriterion("sold_amount is null");
            return (Criteria) this;
        }

        public Criteria andSoldAmountIsNotNull() {
            addCriterion("sold_amount is not null");
            return (Criteria) this;
        }

        public Criteria andSoldAmountEqualTo(BigDecimal value) {
            addCriterion("sold_amount =", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountNotEqualTo(BigDecimal value) {
            addCriterion("sold_amount <>", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountGreaterThan(BigDecimal value) {
            addCriterion("sold_amount >", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("sold_amount >=", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountLessThan(BigDecimal value) {
            addCriterion("sold_amount <", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("sold_amount <=", value, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountIn(List<BigDecimal> values) {
            addCriterion("sold_amount in", values, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountNotIn(List<BigDecimal> values) {
            addCriterion("sold_amount not in", values, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sold_amount between", value1, value2, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andSoldAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sold_amount not between", value1, value2, "soldAmount");
            return (Criteria) this;
        }

        public Criteria andOrderDateIsNull() {
            addCriterion("order_date is null");
            return (Criteria) this;
        }

        public Criteria andOrderDateIsNotNull() {
            addCriterion("order_date is not null");
            return (Criteria) this;
        }

        public Criteria andOrderDateEqualTo(Date value) {
            addCriterionForJDBCDate("order_date =", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("order_date <>", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateGreaterThan(Date value) {
            addCriterionForJDBCDate("order_date >", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("order_date >=", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateLessThan(Date value) {
            addCriterionForJDBCDate("order_date <", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("order_date <=", value, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateIn(List<Date> values) {
            addCriterionForJDBCDate("order_date in", values, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("order_date not in", values, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("order_date between", value1, value2, "orderDate");
            return (Criteria) this;
        }

        public Criteria andOrderDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("order_date not between", value1, value2, "orderDate");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityIsNull() {
            addCriterion("cumulative_quantity is null");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityIsNotNull() {
            addCriterion("cumulative_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityEqualTo(Integer value) {
            addCriterion("cumulative_quantity =", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityNotEqualTo(Integer value) {
            addCriterion("cumulative_quantity <>", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityGreaterThan(Integer value) {
            addCriterion("cumulative_quantity >", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("cumulative_quantity >=", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityLessThan(Integer value) {
            addCriterion("cumulative_quantity <", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("cumulative_quantity <=", value, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityIn(List<Integer> values) {
            addCriterion("cumulative_quantity in", values, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityNotIn(List<Integer> values) {
            addCriterion("cumulative_quantity not in", values, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityBetween(Integer value1, Integer value2) {
            addCriterion("cumulative_quantity between", value1, value2, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("cumulative_quantity not between", value1, value2, "cumulativeQuantity");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountIsNull() {
            addCriterion("cumulative_amount is null");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountIsNotNull() {
            addCriterion("cumulative_amount is not null");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountEqualTo(BigDecimal value) {
            addCriterion("cumulative_amount =", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountNotEqualTo(BigDecimal value) {
            addCriterion("cumulative_amount <>", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountGreaterThan(BigDecimal value) {
            addCriterion("cumulative_amount >", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("cumulative_amount >=", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountLessThan(BigDecimal value) {
            addCriterion("cumulative_amount <", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("cumulative_amount <=", value, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountIn(List<BigDecimal> values) {
            addCriterion("cumulative_amount in", values, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountNotIn(List<BigDecimal> values) {
            addCriterion("cumulative_amount not in", values, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cumulative_amount between", value1, value2, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andCumulativeAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cumulative_amount not between", value1, value2, "cumulativeAmount");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeIsNull() {
            addCriterion("payback_progress_before is null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeIsNotNull() {
            addCriterion("payback_progress_before is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeEqualTo(BigDecimal value) {
            addCriterion("payback_progress_before =", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeNotEqualTo(BigDecimal value) {
            addCriterion("payback_progress_before <>", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeGreaterThan(BigDecimal value) {
            addCriterion("payback_progress_before >", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress_before >=", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeLessThan(BigDecimal value) {
            addCriterion("payback_progress_before <", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeLessThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress_before <=", value, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeIn(List<BigDecimal> values) {
            addCriterion("payback_progress_before in", values, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeNotIn(List<BigDecimal> values) {
            addCriterion("payback_progress_before not in", values, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress_before between", value1, value2, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressBeforeNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress_before not between", value1, value2, "paybackProgressBefore");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterIsNull() {
            addCriterion("payback_progress_after is null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterIsNotNull() {
            addCriterion("payback_progress_after is not null");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterEqualTo(BigDecimal value) {
            addCriterion("payback_progress_after =", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterNotEqualTo(BigDecimal value) {
            addCriterion("payback_progress_after <>", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterGreaterThan(BigDecimal value) {
            addCriterion("payback_progress_after >", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress_after >=", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterLessThan(BigDecimal value) {
            addCriterion("payback_progress_after <", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterLessThanOrEqualTo(BigDecimal value) {
            addCriterion("payback_progress_after <=", value, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterIn(List<BigDecimal> values) {
            addCriterion("payback_progress_after in", values, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterNotIn(List<BigDecimal> values) {
            addCriterion("payback_progress_after not in", values, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress_after between", value1, value2, "paybackProgressAfter");
            return (Criteria) this;
        }

        public Criteria andPaybackProgressAfterNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("payback_progress_after not between", value1, value2, "paybackProgressAfter");
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

        public Criteria andSalesChannelIsNull() {
            addCriterion("sales_channel is null");
            return (Criteria) this;
        }

        public Criteria andSalesChannelIsNotNull() {
            addCriterion("sales_channel is not null");
            return (Criteria) this;
        }

        public Criteria andSalesChannelEqualTo(String value) {
            addCriterion("sales_channel =", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelNotEqualTo(String value) {
            addCriterion("sales_channel <>", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelGreaterThan(String value) {
            addCriterion("sales_channel >", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelGreaterThanOrEqualTo(String value) {
            addCriterion("sales_channel >=", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelLessThan(String value) {
            addCriterion("sales_channel <", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelLessThanOrEqualTo(String value) {
            addCriterion("sales_channel <=", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelLike(String value) {
            addCriterion("sales_channel like", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelNotLike(String value) {
            addCriterion("sales_channel not like", value, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelIn(List<String> values) {
            addCriterion("sales_channel in", values, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelNotIn(List<String> values) {
            addCriterion("sales_channel not in", values, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelBetween(String value1, String value2) {
            addCriterion("sales_channel between", value1, value2, "salesChannel");
            return (Criteria) this;
        }

        public Criteria andSalesChannelNotBetween(String value1, String value2) {
            addCriterion("sales_channel not between", value1, value2, "salesChannel");
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