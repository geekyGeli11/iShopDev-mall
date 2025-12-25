package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.OmsStoreAddressExample;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.service.OmsStoreAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 门店地址管理Service实现类
 */
@Service
public class OmsStoreAddressServiceImpl implements OmsStoreAddressService {

    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Override
    public OmsStoreAddress create(OmsStoreAddress storeAddress) {
        // 验证学校ID是否有效
        if (storeAddress.getSchoolId() != null) {
            OmsSchool school = schoolMapper.selectByPrimaryKey(storeAddress.getSchoolId());
            if (school == null) {
                throw new RuntimeException("所选学校不存在，请重新选择");
            }
        }

        storeAddress.setCreateTime(new Date());
        storeAddress.setUpdateTime(new Date());
        storeAddressMapper.insert(storeAddress);
        return storeAddress;
    }

    @Override
    public OmsStoreAddress update(Long id, OmsStoreAddress storeAddress) {
        OmsStoreAddress existing = storeAddressMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }

        // 验证学校ID是否有效
        if (storeAddress.getSchoolId() != null) {
            OmsSchool school = schoolMapper.selectByPrimaryKey(storeAddress.getSchoolId());
            if (school == null) {
                throw new RuntimeException("所选学校不存在，请重新选择");
            }
        }

        storeAddress.setId(id);
        storeAddress.setUpdateTime(new Date());
        storeAddressMapper.updateByPrimaryKeySelective(storeAddress);
        return storeAddress;
    }

    @Override
    public boolean delete(Long id) {
        int count = storeAddressMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public OmsStoreAddress getById(Long id) {
        return storeAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsStoreAddress> list(String addressName, String phone, Boolean isDefault, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(addressName)) {
            criteria.andAddressNameLike("%" + addressName + "%");
        }
        if (StringUtils.hasText(phone)) {
            criteria.andPhoneLike("%" + phone + "%");
        }
        if (isDefault != null) {
            criteria.andIsDefaultEqualTo(isDefault);
        }

        // 排序条件
        example.setOrderByClause("create_time DESC");

        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<OmsStoreAddress> list(String addressName, String phone, Boolean isDefault, Long schoolId, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(addressName)) {
            criteria.andAddressNameLike("%" + addressName + "%");
        }
        if (StringUtils.hasText(phone)) {
            criteria.andPhoneLike("%" + phone + "%");
        }
        if (isDefault != null) {
            criteria.andIsDefaultEqualTo(isDefault);
        }
        if (schoolId != null) {
            // 当指定学校ID时，返回该学校的门店 + 地库（用于库存操作）
            OmsStoreAddressExample.Criteria schoolCriteria = example.createCriteria();
            schoolCriteria.andSchoolIdEqualTo(schoolId);
            
            OmsStoreAddressExample.Criteria warehouseCriteria = example.createCriteria();
            warehouseCriteria.andIsWarehouseEqualTo(true);
            
            example.or(schoolCriteria);
            example.or(warehouseCriteria);
        }

        // 排序条件
        example.setOrderByClause("create_time DESC");

        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public Map<String, Object> listGroupBySchool() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有启用的学校
        OmsSchoolExample schoolExample = new OmsSchoolExample();
        schoolExample.createCriteria().andStatusEqualTo(true);
        schoolExample.setOrderByClause("school_name ASC");
        List<OmsSchool> schools = schoolMapper.selectByExample(schoolExample);

        // 获取所有门店
        OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
        storeExample.setOrderByClause("school_id ASC, is_default DESC, create_time DESC");
        List<OmsStoreAddress> allStores = storeAddressMapper.selectByExampleWithBLOBs(storeExample);

        // 按学校分组
        List<Map<String, Object>> schoolGroups = new ArrayList<>();

        // 处理有学校的门店
        Map<Long, List<OmsStoreAddress>> storesBySchool = allStores.stream()
            .filter(store -> store.getSchoolId() != null)
            .collect(Collectors.groupingBy(OmsStoreAddress::getSchoolId));

        for (OmsSchool school : schools) {
            Map<String, Object> schoolGroup = new HashMap<>();
            schoolGroup.put("school", school);
            schoolGroup.put("stores", storesBySchool.getOrDefault(school.getId(), new ArrayList<>()));
            schoolGroups.add(schoolGroup);
        }

        // 处理没有学校的门店
        List<OmsStoreAddress> storesWithoutSchool = allStores.stream()
            .filter(store -> store.getSchoolId() == null)
            .collect(Collectors.toList());

        if (!storesWithoutSchool.isEmpty()) {
            Map<String, Object> noSchoolGroup = new HashMap<>();
            OmsSchool noSchool = new OmsSchool();
            noSchool.setId(null);
            noSchool.setSchoolName("未分配学校");
            noSchool.setSchoolLogo(null);
            noSchoolGroup.put("school", noSchool);
            noSchoolGroup.put("stores", storesWithoutSchool);
            schoolGroups.add(noSchoolGroup);
        }

        result.put("schoolGroups", schoolGroups);
        result.put("totalSchools", schools.size());
        result.put("totalStores", allStores.size());

        return result;
    }

    // ==================== 地库相关方法实现 ====================

    @Override
    public List<OmsStoreAddress> listAllStores() {
        // 返回所有门店，包括地库
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.setOrderByClause("is_warehouse DESC, create_time DESC");
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<OmsStoreAddress> listVisibleStores() {
        // 只返回非地库门店
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.createCriteria().andIsWarehouseEqualTo(false);
        example.setOrderByClause("create_time DESC");
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<OmsStoreAddress> listStoresForUser() {
        // 用户端只返回非地库门店
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.createCriteria().andIsWarehouseEqualTo(false);
        example.setOrderByClause("is_default DESC, create_time DESC");
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public Map<String, Object> listSchoolsForUser() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有启用的学校
        OmsSchoolExample schoolExample = new OmsSchoolExample();
        schoolExample.createCriteria().andStatusEqualTo(true);
        schoolExample.setOrderByClause("school_name ASC");
        List<OmsSchool> schools = schoolMapper.selectByExample(schoolExample);

        // 获取所有非地库门店
        OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
        storeExample.createCriteria().andIsWarehouseEqualTo(false);
        storeExample.setOrderByClause("school_id ASC, is_default DESC, create_time DESC");
        List<OmsStoreAddress> allStores = storeAddressMapper.selectByExampleWithBLOBs(storeExample);

        // 按学校分组
        List<Map<String, Object>> schoolGroups = new ArrayList<>();

        // 处理有学校的门店
        Map<Long, List<OmsStoreAddress>> storesBySchool = allStores.stream()
            .filter(store -> store.getSchoolId() != null)
            .collect(Collectors.groupingBy(OmsStoreAddress::getSchoolId));

        for (OmsSchool school : schools) {
            Map<String, Object> schoolGroup = new HashMap<>();
            schoolGroup.put("school", school);
            schoolGroup.put("stores", storesBySchool.getOrDefault(school.getId(), new ArrayList<>()));
            schoolGroups.add(schoolGroup);
        }

        // 处理没有学校的门店
        List<OmsStoreAddress> storesWithoutSchool = allStores.stream()
            .filter(store -> store.getSchoolId() == null)
            .collect(Collectors.toList());

        if (!storesWithoutSchool.isEmpty()) {
            Map<String, Object> noSchoolGroup = new HashMap<>();
            OmsSchool noSchool = new OmsSchool();
            noSchool.setId(null);
            noSchool.setSchoolName("未分配学校");
            noSchool.setSchoolLogo(null);
            noSchoolGroup.put("school", noSchool);
            noSchoolGroup.put("stores", storesWithoutSchool);
            schoolGroups.add(noSchoolGroup);
        }

        result.put("schoolGroups", schoolGroups);
        result.put("totalSchools", schools.size());
        result.put("totalStores", allStores.size());

        return result;
    }

    @Override
    public OmsStoreAddress getWarehouse() {
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.createCriteria().andIsWarehouseEqualTo(true);
        List<OmsStoreAddress> warehouses = storeAddressMapper.selectByExampleWithBLOBs(example);
        
        if (warehouses.isEmpty()) {
            return null;
        }
        
        return warehouses.get(0);
    }

    @Override
    public void initializeWarehouse() {
        // 检查地库是否已存在
        OmsStoreAddress existingWarehouse = getWarehouse();
        if (existingWarehouse != null) {
            return; // 地库已存在，无需初始化
        }

        // 创建地库
        OmsStoreAddress warehouse = new OmsStoreAddress();
        warehouse.setAddressName("总仓库（地库）");
        warehouse.setName("系统管理员");
        warehouse.setPhone("");
        warehouse.setProvince("系统");
        warehouse.setCity("系统");
        warehouse.setRegion("系统");
        warehouse.setDetailAddress("系统默认地库");
        warehouse.setBusinessHours("00:00-23:59");
        warehouse.setIsDefault(false);
        warehouse.setIsWarehouse(true);
        warehouse.setStoreType("WAREHOUSE");
        warehouse.setCreateTime(new Date());
        warehouse.setUpdateTime(new Date());

        storeAddressMapper.insert(warehouse);
    }

    @Override
    public boolean isWarehouse(Long storeId) {
        if (storeId == null) {
            return false;
        }
        
        OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(storeId);
        return store != null && Boolean.TRUE.equals(store.getIsWarehouse());
    }

    @Override
    public boolean isCentralWarehouse(Long storeId) {
        if (storeId == null) {
            return false;
        }
        
        OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(storeId);
        return store != null && "CENTRAL_WAREHOUSE".equals(store.getStoreType());
    }

    @Override
    public String getStoreType(Long storeId) {
        if (storeId == null) {
            return null;
        }
        
        OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(storeId);
        if (store == null) {
            return null;
        }
        return store.getStoreType() != null ? store.getStoreType() : "STORE";
    }

    @Override
    public Long getWarehouseId() {
        OmsStoreAddress warehouse = getWarehouse();
        return warehouse != null ? warehouse.getId() : null;
    }

    @Override
    public List<OmsStoreAddress> getStoresBySchoolIdWithWarehouse(Integer schoolId) {
        if (schoolId == null || schoolId <= 0) {
            return new ArrayList<>();
        }

        // 构建查询条件：该学校的门店 OR 地库
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        
        // 创建第一个条件：学校ID匹配
        OmsStoreAddressExample.Criteria schoolCriteria = example.createCriteria();
        schoolCriteria.andSchoolIdEqualTo(schoolId.longValue());
        
        // 创建第二个条件：是地库
        OmsStoreAddressExample.Criteria warehouseCriteria = example.createCriteria();
        warehouseCriteria.andIsWarehouseEqualTo(true);
        
        // 使用OR连接两个条件
        example.or(warehouseCriteria);
        
        // 排序：地库优先，然后按ID排序
        example.setOrderByClause("is_warehouse DESC, id ASC");
        
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }
}