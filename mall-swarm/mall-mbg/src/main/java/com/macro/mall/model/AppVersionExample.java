package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppVersionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AppVersionExample() {
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

        public Criteria andVersionNameIsNull() {
            addCriterion("version_name is null");
            return (Criteria) this;
        }

        public Criteria andVersionNameIsNotNull() {
            addCriterion("version_name is not null");
            return (Criteria) this;
        }

        public Criteria andVersionNameEqualTo(String value) {
            addCriterion("version_name =", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameNotEqualTo(String value) {
            addCriterion("version_name <>", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameGreaterThan(String value) {
            addCriterion("version_name >", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameGreaterThanOrEqualTo(String value) {
            addCriterion("version_name >=", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameLessThan(String value) {
            addCriterion("version_name <", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameLessThanOrEqualTo(String value) {
            addCriterion("version_name <=", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameLike(String value) {
            addCriterion("version_name like", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameNotLike(String value) {
            addCriterion("version_name not like", value, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameIn(List<String> values) {
            addCriterion("version_name in", values, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameNotIn(List<String> values) {
            addCriterion("version_name not in", values, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameBetween(String value1, String value2) {
            addCriterion("version_name between", value1, value2, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionNameNotBetween(String value1, String value2) {
            addCriterion("version_name not between", value1, value2, "versionName");
            return (Criteria) this;
        }

        public Criteria andVersionCodeIsNull() {
            addCriterion("version_code is null");
            return (Criteria) this;
        }

        public Criteria andVersionCodeIsNotNull() {
            addCriterion("version_code is not null");
            return (Criteria) this;
        }

        public Criteria andVersionCodeEqualTo(Integer value) {
            addCriterion("version_code =", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeNotEqualTo(Integer value) {
            addCriterion("version_code <>", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeGreaterThan(Integer value) {
            addCriterion("version_code >", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeGreaterThanOrEqualTo(Integer value) {
            addCriterion("version_code >=", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeLessThan(Integer value) {
            addCriterion("version_code <", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeLessThanOrEqualTo(Integer value) {
            addCriterion("version_code <=", value, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeIn(List<Integer> values) {
            addCriterion("version_code in", values, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeNotIn(List<Integer> values) {
            addCriterion("version_code not in", values, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeBetween(Integer value1, Integer value2) {
            addCriterion("version_code between", value1, value2, "versionCode");
            return (Criteria) this;
        }

        public Criteria andVersionCodeNotBetween(Integer value1, Integer value2) {
            addCriterion("version_code not between", value1, value2, "versionCode");
            return (Criteria) this;
        }

        public Criteria andApkFilePathIsNull() {
            addCriterion("apk_file_path is null");
            return (Criteria) this;
        }

        public Criteria andApkFilePathIsNotNull() {
            addCriterion("apk_file_path is not null");
            return (Criteria) this;
        }

        public Criteria andApkFilePathEqualTo(String value) {
            addCriterion("apk_file_path =", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathNotEqualTo(String value) {
            addCriterion("apk_file_path <>", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathGreaterThan(String value) {
            addCriterion("apk_file_path >", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathGreaterThanOrEqualTo(String value) {
            addCriterion("apk_file_path >=", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathLessThan(String value) {
            addCriterion("apk_file_path <", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathLessThanOrEqualTo(String value) {
            addCriterion("apk_file_path <=", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathLike(String value) {
            addCriterion("apk_file_path like", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathNotLike(String value) {
            addCriterion("apk_file_path not like", value, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathIn(List<String> values) {
            addCriterion("apk_file_path in", values, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathNotIn(List<String> values) {
            addCriterion("apk_file_path not in", values, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathBetween(String value1, String value2) {
            addCriterion("apk_file_path between", value1, value2, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFilePathNotBetween(String value1, String value2) {
            addCriterion("apk_file_path not between", value1, value2, "apkFilePath");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeIsNull() {
            addCriterion("apk_file_size is null");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeIsNotNull() {
            addCriterion("apk_file_size is not null");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeEqualTo(Long value) {
            addCriterion("apk_file_size =", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeNotEqualTo(Long value) {
            addCriterion("apk_file_size <>", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeGreaterThan(Long value) {
            addCriterion("apk_file_size >", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeGreaterThanOrEqualTo(Long value) {
            addCriterion("apk_file_size >=", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeLessThan(Long value) {
            addCriterion("apk_file_size <", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeLessThanOrEqualTo(Long value) {
            addCriterion("apk_file_size <=", value, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeIn(List<Long> values) {
            addCriterion("apk_file_size in", values, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeNotIn(List<Long> values) {
            addCriterion("apk_file_size not in", values, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeBetween(Long value1, Long value2) {
            addCriterion("apk_file_size between", value1, value2, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileSizeNotBetween(Long value1, Long value2) {
            addCriterion("apk_file_size not between", value1, value2, "apkFileSize");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5IsNull() {
            addCriterion("apk_file_md5 is null");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5IsNotNull() {
            addCriterion("apk_file_md5 is not null");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5EqualTo(String value) {
            addCriterion("apk_file_md5 =", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5NotEqualTo(String value) {
            addCriterion("apk_file_md5 <>", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5GreaterThan(String value) {
            addCriterion("apk_file_md5 >", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5GreaterThanOrEqualTo(String value) {
            addCriterion("apk_file_md5 >=", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5LessThan(String value) {
            addCriterion("apk_file_md5 <", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5LessThanOrEqualTo(String value) {
            addCriterion("apk_file_md5 <=", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5Like(String value) {
            addCriterion("apk_file_md5 like", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5NotLike(String value) {
            addCriterion("apk_file_md5 not like", value, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5In(List<String> values) {
            addCriterion("apk_file_md5 in", values, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5NotIn(List<String> values) {
            addCriterion("apk_file_md5 not in", values, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5Between(String value1, String value2) {
            addCriterion("apk_file_md5 between", value1, value2, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andApkFileMd5NotBetween(String value1, String value2) {
            addCriterion("apk_file_md5 not between", value1, value2, "apkFileMd5");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeIsNull() {
            addCriterion("update_type is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeIsNotNull() {
            addCriterion("update_type is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeEqualTo(Boolean value) {
            addCriterion("update_type =", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeNotEqualTo(Boolean value) {
            addCriterion("update_type <>", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeGreaterThan(Boolean value) {
            addCriterion("update_type >", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeGreaterThanOrEqualTo(Boolean value) {
            addCriterion("update_type >=", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeLessThan(Boolean value) {
            addCriterion("update_type <", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeLessThanOrEqualTo(Boolean value) {
            addCriterion("update_type <=", value, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeIn(List<Boolean> values) {
            addCriterion("update_type in", values, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeNotIn(List<Boolean> values) {
            addCriterion("update_type not in", values, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeBetween(Boolean value1, Boolean value2) {
            addCriterion("update_type between", value1, value2, "updateType");
            return (Criteria) this;
        }

        public Criteria andUpdateTypeNotBetween(Boolean value1, Boolean value2) {
            addCriterion("update_type not between", value1, value2, "updateType");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionIsNull() {
            addCriterion("min_support_version is null");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionIsNotNull() {
            addCriterion("min_support_version is not null");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionEqualTo(String value) {
            addCriterion("min_support_version =", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionNotEqualTo(String value) {
            addCriterion("min_support_version <>", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionGreaterThan(String value) {
            addCriterion("min_support_version >", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionGreaterThanOrEqualTo(String value) {
            addCriterion("min_support_version >=", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionLessThan(String value) {
            addCriterion("min_support_version <", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionLessThanOrEqualTo(String value) {
            addCriterion("min_support_version <=", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionLike(String value) {
            addCriterion("min_support_version like", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionNotLike(String value) {
            addCriterion("min_support_version not like", value, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionIn(List<String> values) {
            addCriterion("min_support_version in", values, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionNotIn(List<String> values) {
            addCriterion("min_support_version not in", values, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionBetween(String value1, String value2) {
            addCriterion("min_support_version between", value1, value2, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andMinSupportVersionNotBetween(String value1, String value2) {
            addCriterion("min_support_version not between", value1, value2, "minSupportVersion");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformIsNull() {
            addCriterion("target_platform is null");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformIsNotNull() {
            addCriterion("target_platform is not null");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformEqualTo(String value) {
            addCriterion("target_platform =", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformNotEqualTo(String value) {
            addCriterion("target_platform <>", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformGreaterThan(String value) {
            addCriterion("target_platform >", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformGreaterThanOrEqualTo(String value) {
            addCriterion("target_platform >=", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformLessThan(String value) {
            addCriterion("target_platform <", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformLessThanOrEqualTo(String value) {
            addCriterion("target_platform <=", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformLike(String value) {
            addCriterion("target_platform like", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformNotLike(String value) {
            addCriterion("target_platform not like", value, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformIn(List<String> values) {
            addCriterion("target_platform in", values, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformNotIn(List<String> values) {
            addCriterion("target_platform not in", values, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformBetween(String value1, String value2) {
            addCriterion("target_platform between", value1, value2, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andTargetPlatformNotBetween(String value1, String value2) {
            addCriterion("target_platform not between", value1, value2, "targetPlatform");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeIsNull() {
            addCriterion("release_time is null");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeIsNotNull() {
            addCriterion("release_time is not null");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeEqualTo(Date value) {
            addCriterion("release_time =", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeNotEqualTo(Date value) {
            addCriterion("release_time <>", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeGreaterThan(Date value) {
            addCriterion("release_time >", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("release_time >=", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeLessThan(Date value) {
            addCriterion("release_time <", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeLessThanOrEqualTo(Date value) {
            addCriterion("release_time <=", value, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeIn(List<Date> values) {
            addCriterion("release_time in", values, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeNotIn(List<Date> values) {
            addCriterion("release_time not in", values, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeBetween(Date value1, Date value2) {
            addCriterion("release_time between", value1, value2, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andReleaseTimeNotBetween(Date value1, Date value2) {
            addCriterion("release_time not between", value1, value2, "releaseTime");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNull() {
            addCriterion("is_active is null");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNotNull() {
            addCriterion("is_active is not null");
            return (Criteria) this;
        }

        public Criteria andIsActiveEqualTo(Boolean value) {
            addCriterion("is_active =", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotEqualTo(Boolean value) {
            addCriterion("is_active <>", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThan(Boolean value) {
            addCriterion("is_active >", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_active >=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThan(Boolean value) {
            addCriterion("is_active <", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThanOrEqualTo(Boolean value) {
            addCriterion("is_active <=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveIn(List<Boolean> values) {
            addCriterion("is_active in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotIn(List<Boolean> values) {
            addCriterion("is_active not in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active not between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIsNull() {
            addCriterion("download_url is null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIsNotNull() {
            addCriterion("download_url is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlEqualTo(String value) {
            addCriterion("download_url =", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotEqualTo(String value) {
            addCriterion("download_url <>", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlGreaterThan(String value) {
            addCriterion("download_url >", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlGreaterThanOrEqualTo(String value) {
            addCriterion("download_url >=", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLessThan(String value) {
            addCriterion("download_url <", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLessThanOrEqualTo(String value) {
            addCriterion("download_url <=", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLike(String value) {
            addCriterion("download_url like", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotLike(String value) {
            addCriterion("download_url not like", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIn(List<String> values) {
            addCriterion("download_url in", values, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotIn(List<String> values) {
            addCriterion("download_url not in", values, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlBetween(String value1, String value2) {
            addCriterion("download_url between", value1, value2, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotBetween(String value1, String value2) {
            addCriterion("download_url not between", value1, value2, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Date value) {
            addCriterion("created_time =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Date value) {
            addCriterion("created_time <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Date value) {
            addCriterion("created_time >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("created_time >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Date value) {
            addCriterion("created_time <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("created_time <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Date> values) {
            addCriterion("created_time in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Date> values) {
            addCriterion("created_time not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Date value1, Date value2) {
            addCriterion("created_time between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("created_time not between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNull() {
            addCriterion("updated_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNotNull() {
            addCriterion("updated_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeEqualTo(Date value) {
            addCriterion("updated_time =", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotEqualTo(Date value) {
            addCriterion("updated_time <>", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThan(Date value) {
            addCriterion("updated_time >", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_time >=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThan(Date value) {
            addCriterion("updated_time <", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("updated_time <=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIn(List<Date> values) {
            addCriterion("updated_time in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotIn(List<Date> values) {
            addCriterion("updated_time not in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeBetween(Date value1, Date value2) {
            addCriterion("updated_time between", value1, value2, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("updated_time not between", value1, value2, "updatedTime");
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