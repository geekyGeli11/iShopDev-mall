package com.macro.mall.service;

import com.macro.mall.model.OmsStoreAddress;
import java.util.List;
import java.util.Map;

/**
 * 门店地址管理Service
 */
public interface OmsStoreAddressService {
    /**
     * 创建门店地址
     */
    OmsStoreAddress create(OmsStoreAddress storeAddress);

    /**
     * 更新门店地址
     */
    OmsStoreAddress update(Long id, OmsStoreAddress storeAddress);

    /**
     * 删除门店地址
     */
    boolean delete(Long id);

    /**
     * 根据ID获取门店地址详情
     */
    OmsStoreAddress getById(Long id);

    /**
     * 根据条件分页获取门店地址列表
     */
    List<OmsStoreAddress> list(String addressName, String phone, Boolean isDefault, int pageNum, int pageSize);

    /**
     * 根据条件分页获取门店地址列表（支持学校筛选）
     */
    List<OmsStoreAddress> list(String addressName, String phone, Boolean isDefault, Long schoolId, int pageNum, int pageSize);

    /**
     * 获取按学校分组的门店列表
     */
    Map<String, Object> listGroupBySchool();

    // ==================== 地库相关方法 ====================

    /**
     * 获取所有门店（包含地库）- 用于后台库存操作
     */
    List<OmsStoreAddress> listAllStores();

    /**
     * 获取可见门店列表（不包含地库）- 用于门店地址管理
     */
    List<OmsStoreAddress> listVisibleStores();

    /**
     * 获取用户端门店列表（不包含地库）
     */
    List<OmsStoreAddress> listStoresForUser();

    /**
     * 获取用户端学校列表（学校下的门店不包含地库）
     */
    Map<String, Object> listSchoolsForUser();

    /**
     * 获取地库信息
     */
    OmsStoreAddress getWarehouse();

    /**
     * 初始化地库（系统启动时调用）
     */
    void initializeWarehouse();

    /**
     * 检查是否为地库
     */
    boolean isWarehouse(Long storeId);

    /**
     * 检查是否为中央仓库
     */
    boolean isCentralWarehouse(Long storeId);

    /**
     * 获取门店类型
     * @return STORE-门店，WAREHOUSE-地库，CENTRAL_WAREHOUSE-中央仓库
     */
    String getStoreType(Long storeId);

    /**
     * 获取地库ID
     */
    Long getWarehouseId();

    /**
     * 根据学校ID获取门店列表（包括地库）- 用于非系统销售
     */
    List<OmsStoreAddress> getStoresBySchoolIdWithWarehouse(Integer schoolId);
}