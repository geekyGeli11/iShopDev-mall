package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UmsAiStylizationRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UmsAiStylizationRecordExample() {
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

        public Criteria andOriginalImageIsNull() {
            addCriterion("original_image is null");
            return (Criteria) this;
        }

        public Criteria andOriginalImageIsNotNull() {
            addCriterion("original_image is not null");
            return (Criteria) this;
        }

        public Criteria andOriginalImageEqualTo(String value) {
            addCriterion("original_image =", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageNotEqualTo(String value) {
            addCriterion("original_image <>", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageGreaterThan(String value) {
            addCriterion("original_image >", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageGreaterThanOrEqualTo(String value) {
            addCriterion("original_image >=", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageLessThan(String value) {
            addCriterion("original_image <", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageLessThanOrEqualTo(String value) {
            addCriterion("original_image <=", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageLike(String value) {
            addCriterion("original_image like", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageNotLike(String value) {
            addCriterion("original_image not like", value, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageIn(List<String> values) {
            addCriterion("original_image in", values, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageNotIn(List<String> values) {
            addCriterion("original_image not in", values, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageBetween(String value1, String value2) {
            addCriterion("original_image between", value1, value2, "originalImage");
            return (Criteria) this;
        }

        public Criteria andOriginalImageNotBetween(String value1, String value2) {
            addCriterion("original_image not between", value1, value2, "originalImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageIsNull() {
            addCriterion("stylized_image is null");
            return (Criteria) this;
        }

        public Criteria andStylizedImageIsNotNull() {
            addCriterion("stylized_image is not null");
            return (Criteria) this;
        }

        public Criteria andStylizedImageEqualTo(String value) {
            addCriterion("stylized_image =", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageNotEqualTo(String value) {
            addCriterion("stylized_image <>", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageGreaterThan(String value) {
            addCriterion("stylized_image >", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageGreaterThanOrEqualTo(String value) {
            addCriterion("stylized_image >=", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageLessThan(String value) {
            addCriterion("stylized_image <", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageLessThanOrEqualTo(String value) {
            addCriterion("stylized_image <=", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageLike(String value) {
            addCriterion("stylized_image like", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageNotLike(String value) {
            addCriterion("stylized_image not like", value, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageIn(List<String> values) {
            addCriterion("stylized_image in", values, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageNotIn(List<String> values) {
            addCriterion("stylized_image not in", values, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageBetween(String value1, String value2) {
            addCriterion("stylized_image between", value1, value2, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylizedImageNotBetween(String value1, String value2) {
            addCriterion("stylized_image not between", value1, value2, "stylizedImage");
            return (Criteria) this;
        }

        public Criteria andStylePromptIsNull() {
            addCriterion("style_prompt is null");
            return (Criteria) this;
        }

        public Criteria andStylePromptIsNotNull() {
            addCriterion("style_prompt is not null");
            return (Criteria) this;
        }

        public Criteria andStylePromptEqualTo(String value) {
            addCriterion("style_prompt =", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptNotEqualTo(String value) {
            addCriterion("style_prompt <>", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptGreaterThan(String value) {
            addCriterion("style_prompt >", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptGreaterThanOrEqualTo(String value) {
            addCriterion("style_prompt >=", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptLessThan(String value) {
            addCriterion("style_prompt <", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptLessThanOrEqualTo(String value) {
            addCriterion("style_prompt <=", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptLike(String value) {
            addCriterion("style_prompt like", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptNotLike(String value) {
            addCriterion("style_prompt not like", value, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptIn(List<String> values) {
            addCriterion("style_prompt in", values, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptNotIn(List<String> values) {
            addCriterion("style_prompt not in", values, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptBetween(String value1, String value2) {
            addCriterion("style_prompt between", value1, value2, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andStylePromptNotBetween(String value1, String value2) {
            addCriterion("style_prompt not between", value1, value2, "stylePrompt");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdIsNull() {
            addCriterion("aliyun_task_id is null");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdIsNotNull() {
            addCriterion("aliyun_task_id is not null");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdEqualTo(String value) {
            addCriterion("aliyun_task_id =", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdNotEqualTo(String value) {
            addCriterion("aliyun_task_id <>", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdGreaterThan(String value) {
            addCriterion("aliyun_task_id >", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdGreaterThanOrEqualTo(String value) {
            addCriterion("aliyun_task_id >=", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdLessThan(String value) {
            addCriterion("aliyun_task_id <", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdLessThanOrEqualTo(String value) {
            addCriterion("aliyun_task_id <=", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdLike(String value) {
            addCriterion("aliyun_task_id like", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdNotLike(String value) {
            addCriterion("aliyun_task_id not like", value, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdIn(List<String> values) {
            addCriterion("aliyun_task_id in", values, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdNotIn(List<String> values) {
            addCriterion("aliyun_task_id not in", values, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdBetween(String value1, String value2) {
            addCriterion("aliyun_task_id between", value1, value2, "aliyunTaskId");
            return (Criteria) this;
        }

        public Criteria andAliyunTaskIdNotBetween(String value1, String value2) {
            addCriterion("aliyun_task_id not between", value1, value2, "aliyunTaskId");
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

        public Criteria andErrorMessageIsNull() {
            addCriterion("error_message is null");
            return (Criteria) this;
        }

        public Criteria andErrorMessageIsNotNull() {
            addCriterion("error_message is not null");
            return (Criteria) this;
        }

        public Criteria andErrorMessageEqualTo(String value) {
            addCriterion("error_message =", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageNotEqualTo(String value) {
            addCriterion("error_message <>", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageGreaterThan(String value) {
            addCriterion("error_message >", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageGreaterThanOrEqualTo(String value) {
            addCriterion("error_message >=", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageLessThan(String value) {
            addCriterion("error_message <", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageLessThanOrEqualTo(String value) {
            addCriterion("error_message <=", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageLike(String value) {
            addCriterion("error_message like", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageNotLike(String value) {
            addCriterion("error_message not like", value, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageIn(List<String> values) {
            addCriterion("error_message in", values, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageNotIn(List<String> values) {
            addCriterion("error_message not in", values, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageBetween(String value1, String value2) {
            addCriterion("error_message between", value1, value2, "errorMessage");
            return (Criteria) this;
        }

        public Criteria andErrorMessageNotBetween(String value1, String value2) {
            addCriterion("error_message not between", value1, value2, "errorMessage");
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