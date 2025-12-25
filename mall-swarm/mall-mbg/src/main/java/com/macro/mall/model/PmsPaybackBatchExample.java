package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsPaybackBatchExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsPaybackBatchExample() {
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

        public Criteria andProductPicIsNull() {
            addCriterion("product_pic is null");
            return (Criteria) this;
        }

        public Criteria andProductPicIsNotNull() {
            addCriterion("product_pic is not null");
            return (Criteria) this;
        }

        public Criteria andProductPicEqualTo(String value) {
            addCriterion("product_pic =", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicNotEqualTo(String value) {
            addCriterion("product_pic <>", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicGreaterThan(String value) {
            addCriterion("product_pic >", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicGreaterThanOrEqualTo(String value) {
            addCriterion("product_pic >=", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicLessThan(String value) {
            addCriterion("product_pic <", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicLessThanOrEqualTo(String value) {
            addCriterion("product_pic <=", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicLike(String value) {
            addCriterion("product_pic like", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicNotLike(String value) {
            addCriterion("product_pic not like", value, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicIn(List<String> values) {
            addCriterion("product_pic in", values, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicNotIn(List<String> values) {
            addCriterion("product_pic not in", values, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicBetween(String value1, String value2) {
            addCriterion("product_pic between", value1, value2, "productPic");
            return (Criteria) this;
        }

        public Criteria andProductPicNotBetween(String value1, String value2) {
            addCriterion("product_pic not between", value1, value2, "productPic");
            return (Criteria) this;
        }

        public Criteria andBatchNoIsNull() {
            addCriterion("batch_no is null");
            return (Criteria) this;
        }

        public Criteria andBatchNoIsNotNull() {
            addCriterion("batch_no is not null");
            return (Criteria) this;
        }

        public Criteria andBatchNoEqualTo(Integer value) {
            addCriterion("batch_no =", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoNotEqualTo(Integer value) {
            addCriterion("batch_no <>", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoGreaterThan(Integer value) {
            addCriterion("batch_no >", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("batch_no >=", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoLessThan(Integer value) {
            addCriterion("batch_no <", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoLessThanOrEqualTo(Integer value) {
            addCriterion("batch_no <=", value, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoIn(List<Integer> values) {
            addCriterion("batch_no in", values, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoNotIn(List<Integer> values) {
            addCriterion("batch_no not in", values, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoBetween(Integer value1, Integer value2) {
            addCriterion("batch_no between", value1, value2, "batchNo");
            return (Criteria) this;
        }

        public Criteria andBatchNoNotBetween(Integer value1, Integer value2) {
            addCriterion("batch_no not between", value1, value2, "batchNo");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityIsNull() {
            addCriterion("replenishment_quantity is null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityIsNotNull() {
            addCriterion("replenishment_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityEqualTo(Integer value) {
            addCriterion("replenishment_quantity =", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityNotEqualTo(Integer value) {
            addCriterion("replenishment_quantity <>", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityGreaterThan(Integer value) {
            addCriterion("replenishment_quantity >", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("replenishment_quantity >=", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityLessThan(Integer value) {
            addCriterion("replenishment_quantity <", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("replenishment_quantity <=", value, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityIn(List<Integer> values) {
            addCriterion("replenishment_quantity in", values, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityNotIn(List<Integer> values) {
            addCriterion("replenishment_quantity not in", values, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityBetween(Integer value1, Integer value2) {
            addCriterion("replenishment_quantity between", value1, value2, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("replenishment_quantity not between", value1, value2, "replenishmentQuantity");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountIsNull() {
            addCriterion("replenishment_amount is null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountIsNotNull() {
            addCriterion("replenishment_amount is not null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountEqualTo(BigDecimal value) {
            addCriterion("replenishment_amount =", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountNotEqualTo(BigDecimal value) {
            addCriterion("replenishment_amount <>", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountGreaterThan(BigDecimal value) {
            addCriterion("replenishment_amount >", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("replenishment_amount >=", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountLessThan(BigDecimal value) {
            addCriterion("replenishment_amount <", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("replenishment_amount <=", value, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountIn(List<BigDecimal> values) {
            addCriterion("replenishment_amount in", values, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountNotIn(List<BigDecimal> values) {
            addCriterion("replenishment_amount not in", values, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("replenishment_amount between", value1, value2, "replenishmentAmount");
            return (Criteria) this;
        }

        public Criteria andReplenishmentAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("replenishment_amount not between", value1, value2, "replenishmentAmount");
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

        public Criteria andReplenishmentDateIsNull() {
            addCriterion("replenishment_date is null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateIsNotNull() {
            addCriterion("replenishment_date is not null");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateEqualTo(Date value) {
            addCriterion("replenishment_date =", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateNotEqualTo(Date value) {
            addCriterion("replenishment_date <>", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateGreaterThan(Date value) {
            addCriterion("replenishment_date >", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateGreaterThanOrEqualTo(Date value) {
            addCriterion("replenishment_date >=", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateLessThan(Date value) {
            addCriterion("replenishment_date <", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateLessThanOrEqualTo(Date value) {
            addCriterion("replenishment_date <=", value, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateIn(List<Date> values) {
            addCriterion("replenishment_date in", values, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateNotIn(List<Date> values) {
            addCriterion("replenishment_date not in", values, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateBetween(Date value1, Date value2) {
            addCriterion("replenishment_date between", value1, value2, "replenishmentDate");
            return (Criteria) this;
        }

        public Criteria andReplenishmentDateNotBetween(Date value1, Date value2) {
            addCriterion("replenishment_date not between", value1, value2, "replenishmentDate");
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

        public Criteria andProfitAmountIsNull() {
            addCriterion("profit_amount is null");
            return (Criteria) this;
        }

        public Criteria andProfitAmountIsNotNull() {
            addCriterion("profit_amount is not null");
            return (Criteria) this;
        }

        public Criteria andProfitAmountEqualTo(BigDecimal value) {
            addCriterion("profit_amount =", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountNotEqualTo(BigDecimal value) {
            addCriterion("profit_amount <>", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountGreaterThan(BigDecimal value) {
            addCriterion("profit_amount >", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("profit_amount >=", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountLessThan(BigDecimal value) {
            addCriterion("profit_amount <", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("profit_amount <=", value, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountIn(List<BigDecimal> values) {
            addCriterion("profit_amount in", values, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountNotIn(List<BigDecimal> values) {
            addCriterion("profit_amount not in", values, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("profit_amount between", value1, value2, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("profit_amount not between", value1, value2, "profitAmount");
            return (Criteria) this;
        }

        public Criteria andProfitRateIsNull() {
            addCriterion("profit_rate is null");
            return (Criteria) this;
        }

        public Criteria andProfitRateIsNotNull() {
            addCriterion("profit_rate is not null");
            return (Criteria) this;
        }

        public Criteria andProfitRateEqualTo(BigDecimal value) {
            addCriterion("profit_rate =", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateNotEqualTo(BigDecimal value) {
            addCriterion("profit_rate <>", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateGreaterThan(BigDecimal value) {
            addCriterion("profit_rate >", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("profit_rate >=", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateLessThan(BigDecimal value) {
            addCriterion("profit_rate <", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateLessThanOrEqualTo(BigDecimal value) {
            addCriterion("profit_rate <=", value, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateIn(List<BigDecimal> values) {
            addCriterion("profit_rate in", values, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateNotIn(List<BigDecimal> values) {
            addCriterion("profit_rate not in", values, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("profit_rate between", value1, value2, "profitRate");
            return (Criteria) this;
        }

        public Criteria andProfitRateNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("profit_rate not between", value1, value2, "profitRate");
            return (Criteria) this;
        }

        public Criteria andBatchStatusIsNull() {
            addCriterion("batch_status is null");
            return (Criteria) this;
        }

        public Criteria andBatchStatusIsNotNull() {
            addCriterion("batch_status is not null");
            return (Criteria) this;
        }

        public Criteria andBatchStatusEqualTo(Byte value) {
            addCriterion("batch_status =", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusNotEqualTo(Byte value) {
            addCriterion("batch_status <>", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusGreaterThan(Byte value) {
            addCriterion("batch_status >", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("batch_status >=", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusLessThan(Byte value) {
            addCriterion("batch_status <", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusLessThanOrEqualTo(Byte value) {
            addCriterion("batch_status <=", value, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusIn(List<Byte> values) {
            addCriterion("batch_status in", values, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusNotIn(List<Byte> values) {
            addCriterion("batch_status not in", values, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusBetween(Byte value1, Byte value2) {
            addCriterion("batch_status between", value1, value2, "batchStatus");
            return (Criteria) this;
        }

        public Criteria andBatchStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("batch_status not between", value1, value2, "batchStatus");
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
            addCriterion("start_date =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterion("start_date <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterion("start_date >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("start_date >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterion("start_date <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterion("start_date <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterion("start_date in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterion("start_date not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterion("start_date between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterion("start_date not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateIsNull() {
            addCriterion("completed_date is null");
            return (Criteria) this;
        }

        public Criteria andCompletedDateIsNotNull() {
            addCriterion("completed_date is not null");
            return (Criteria) this;
        }

        public Criteria andCompletedDateEqualTo(Date value) {
            addCriterion("completed_date =", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateNotEqualTo(Date value) {
            addCriterion("completed_date <>", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateGreaterThan(Date value) {
            addCriterion("completed_date >", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateGreaterThanOrEqualTo(Date value) {
            addCriterion("completed_date >=", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateLessThan(Date value) {
            addCriterion("completed_date <", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateLessThanOrEqualTo(Date value) {
            addCriterion("completed_date <=", value, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateIn(List<Date> values) {
            addCriterion("completed_date in", values, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateNotIn(List<Date> values) {
            addCriterion("completed_date not in", values, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateBetween(Date value1, Date value2) {
            addCriterion("completed_date between", value1, value2, "completedDate");
            return (Criteria) this;
        }

        public Criteria andCompletedDateNotBetween(Date value1, Date value2) {
            addCriterion("completed_date not between", value1, value2, "completedDate");
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