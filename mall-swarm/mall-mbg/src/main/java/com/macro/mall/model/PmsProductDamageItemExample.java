package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsProductDamageItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsProductDamageItemExample() {
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

        public Criteria andSkuIdIsNull() {
            addCriterion("sku_id is null");
            return (Criteria) this;
        }

        public Criteria andSkuIdIsNotNull() {
            addCriterion("sku_id is not null");
            return (Criteria) this;
        }

        public Criteria andSkuIdEqualTo(Long value) {
            addCriterion("sku_id =", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotEqualTo(Long value) {
            addCriterion("sku_id <>", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdGreaterThan(Long value) {
            addCriterion("sku_id >", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdGreaterThanOrEqualTo(Long value) {
            addCriterion("sku_id >=", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdLessThan(Long value) {
            addCriterion("sku_id <", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdLessThanOrEqualTo(Long value) {
            addCriterion("sku_id <=", value, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdIn(List<Long> values) {
            addCriterion("sku_id in", values, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotIn(List<Long> values) {
            addCriterion("sku_id not in", values, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdBetween(Long value1, Long value2) {
            addCriterion("sku_id between", value1, value2, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuIdNotBetween(Long value1, Long value2) {
            addCriterion("sku_id not between", value1, value2, "skuId");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNull() {
            addCriterion("sku_code is null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNotNull() {
            addCriterion("sku_code is not null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeEqualTo(String value) {
            addCriterion("sku_code =", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotEqualTo(String value) {
            addCriterion("sku_code <>", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThan(String value) {
            addCriterion("sku_code >", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThanOrEqualTo(String value) {
            addCriterion("sku_code >=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThan(String value) {
            addCriterion("sku_code <", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThanOrEqualTo(String value) {
            addCriterion("sku_code <=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLike(String value) {
            addCriterion("sku_code like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotLike(String value) {
            addCriterion("sku_code not like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIn(List<String> values) {
            addCriterion("sku_code in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotIn(List<String> values) {
            addCriterion("sku_code not in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeBetween(String value1, String value2) {
            addCriterion("sku_code between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotBetween(String value1, String value2) {
            addCriterion("sku_code not between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuSpecIsNull() {
            addCriterion("sku_spec is null");
            return (Criteria) this;
        }

        public Criteria andSkuSpecIsNotNull() {
            addCriterion("sku_spec is not null");
            return (Criteria) this;
        }

        public Criteria andSkuSpecEqualTo(String value) {
            addCriterion("sku_spec =", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecNotEqualTo(String value) {
            addCriterion("sku_spec <>", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecGreaterThan(String value) {
            addCriterion("sku_spec >", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecGreaterThanOrEqualTo(String value) {
            addCriterion("sku_spec >=", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecLessThan(String value) {
            addCriterion("sku_spec <", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecLessThanOrEqualTo(String value) {
            addCriterion("sku_spec <=", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecLike(String value) {
            addCriterion("sku_spec like", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecNotLike(String value) {
            addCriterion("sku_spec not like", value, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecIn(List<String> values) {
            addCriterion("sku_spec in", values, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecNotIn(List<String> values) {
            addCriterion("sku_spec not in", values, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecBetween(String value1, String value2) {
            addCriterion("sku_spec between", value1, value2, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andSkuSpecNotBetween(String value1, String value2) {
            addCriterion("sku_spec not between", value1, value2, "skuSpec");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityIsNull() {
            addCriterion("damage_quantity is null");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityIsNotNull() {
            addCriterion("damage_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityEqualTo(Integer value) {
            addCriterion("damage_quantity =", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityNotEqualTo(Integer value) {
            addCriterion("damage_quantity <>", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityGreaterThan(Integer value) {
            addCriterion("damage_quantity >", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("damage_quantity >=", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityLessThan(Integer value) {
            addCriterion("damage_quantity <", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("damage_quantity <=", value, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityIn(List<Integer> values) {
            addCriterion("damage_quantity in", values, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityNotIn(List<Integer> values) {
            addCriterion("damage_quantity not in", values, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityBetween(Integer value1, Integer value2) {
            addCriterion("damage_quantity between", value1, value2, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andDamageQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("damage_quantity not between", value1, value2, "damageQuantity");
            return (Criteria) this;
        }

        public Criteria andCostPriceIsNull() {
            addCriterion("cost_price is null");
            return (Criteria) this;
        }

        public Criteria andCostPriceIsNotNull() {
            addCriterion("cost_price is not null");
            return (Criteria) this;
        }

        public Criteria andCostPriceEqualTo(BigDecimal value) {
            addCriterion("cost_price =", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotEqualTo(BigDecimal value) {
            addCriterion("cost_price <>", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceGreaterThan(BigDecimal value) {
            addCriterion("cost_price >", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("cost_price >=", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceLessThan(BigDecimal value) {
            addCriterion("cost_price <", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("cost_price <=", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceIn(List<BigDecimal> values) {
            addCriterion("cost_price in", values, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotIn(List<BigDecimal> values) {
            addCriterion("cost_price not in", values, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cost_price between", value1, value2, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cost_price not between", value1, value2, "costPrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceIsNull() {
            addCriterion("sale_price is null");
            return (Criteria) this;
        }

        public Criteria andSalePriceIsNotNull() {
            addCriterion("sale_price is not null");
            return (Criteria) this;
        }

        public Criteria andSalePriceEqualTo(BigDecimal value) {
            addCriterion("sale_price =", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceNotEqualTo(BigDecimal value) {
            addCriterion("sale_price <>", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceGreaterThan(BigDecimal value) {
            addCriterion("sale_price >", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("sale_price >=", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceLessThan(BigDecimal value) {
            addCriterion("sale_price <", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("sale_price <=", value, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceIn(List<BigDecimal> values) {
            addCriterion("sale_price in", values, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceNotIn(List<BigDecimal> values) {
            addCriterion("sale_price not in", values, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sale_price between", value1, value2, "salePrice");
            return (Criteria) this;
        }

        public Criteria andSalePriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sale_price not between", value1, value2, "salePrice");
            return (Criteria) this;
        }

        public Criteria andDamageAmountIsNull() {
            addCriterion("damage_amount is null");
            return (Criteria) this;
        }

        public Criteria andDamageAmountIsNotNull() {
            addCriterion("damage_amount is not null");
            return (Criteria) this;
        }

        public Criteria andDamageAmountEqualTo(BigDecimal value) {
            addCriterion("damage_amount =", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountNotEqualTo(BigDecimal value) {
            addCriterion("damage_amount <>", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountGreaterThan(BigDecimal value) {
            addCriterion("damage_amount >", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("damage_amount >=", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountLessThan(BigDecimal value) {
            addCriterion("damage_amount <", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("damage_amount <=", value, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountIn(List<BigDecimal> values) {
            addCriterion("damage_amount in", values, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountNotIn(List<BigDecimal> values) {
            addCriterion("damage_amount not in", values, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("damage_amount between", value1, value2, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andDamageAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("damage_amount not between", value1, value2, "damageAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountIsNull() {
            addCriterion("sales_amount is null");
            return (Criteria) this;
        }

        public Criteria andSalesAmountIsNotNull() {
            addCriterion("sales_amount is not null");
            return (Criteria) this;
        }

        public Criteria andSalesAmountEqualTo(BigDecimal value) {
            addCriterion("sales_amount =", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountNotEqualTo(BigDecimal value) {
            addCriterion("sales_amount <>", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountGreaterThan(BigDecimal value) {
            addCriterion("sales_amount >", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("sales_amount >=", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountLessThan(BigDecimal value) {
            addCriterion("sales_amount <", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("sales_amount <=", value, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountIn(List<BigDecimal> values) {
            addCriterion("sales_amount in", values, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountNotIn(List<BigDecimal> values) {
            addCriterion("sales_amount not in", values, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sales_amount between", value1, value2, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andSalesAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("sales_amount not between", value1, value2, "salesAmount");
            return (Criteria) this;
        }

        public Criteria andItemRemarkIsNull() {
            addCriterion("item_remark is null");
            return (Criteria) this;
        }

        public Criteria andItemRemarkIsNotNull() {
            addCriterion("item_remark is not null");
            return (Criteria) this;
        }

        public Criteria andItemRemarkEqualTo(String value) {
            addCriterion("item_remark =", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkNotEqualTo(String value) {
            addCriterion("item_remark <>", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkGreaterThan(String value) {
            addCriterion("item_remark >", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("item_remark >=", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkLessThan(String value) {
            addCriterion("item_remark <", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkLessThanOrEqualTo(String value) {
            addCriterion("item_remark <=", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkLike(String value) {
            addCriterion("item_remark like", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkNotLike(String value) {
            addCriterion("item_remark not like", value, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkIn(List<String> values) {
            addCriterion("item_remark in", values, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkNotIn(List<String> values) {
            addCriterion("item_remark not in", values, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkBetween(String value1, String value2) {
            addCriterion("item_remark between", value1, value2, "itemRemark");
            return (Criteria) this;
        }

        public Criteria andItemRemarkNotBetween(String value1, String value2) {
            addCriterion("item_remark not between", value1, value2, "itemRemark");
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

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
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