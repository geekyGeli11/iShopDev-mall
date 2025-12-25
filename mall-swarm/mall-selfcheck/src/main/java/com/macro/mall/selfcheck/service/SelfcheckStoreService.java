package com.macro.mall.selfcheck.service;

import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsStoreAddress;

import java.util.List;
import java.util.Map;

/**
 * 自助结算门店服务接口
 */
public interface SelfcheckStoreService {

    /**
     * 获取所有启用的学校列表
     * @return 学校列表
     */
    List<OmsSchool> getEnabledSchools();

    /**
     * 根据学校ID获取门店列表
     * @param schoolId 学校ID
     * @return 门店列表
     */
    List<OmsStoreAddress> getStoresBySchool(Long schoolId);

    /**
     * 获取所有可用门店地址
     * @return 门店列表
     */
    List<OmsStoreAddress> getAllStores();

    /**
     * 根据ID获取门店详情
     * @param id 门店ID
     * @return 门店详情
     */
    OmsStoreAddress getStoreById(Long id);

    /**
     * 获取按学校分组的门店列表
     * @return 分组结果
     */
    Map<String, Object> getStoresGroupBySchool();

    /**
     * 根据ID获取学校详情
     * @param id 学校ID
     * @return 学校详情
     */
    OmsSchool getSchoolById(Long id);
}
