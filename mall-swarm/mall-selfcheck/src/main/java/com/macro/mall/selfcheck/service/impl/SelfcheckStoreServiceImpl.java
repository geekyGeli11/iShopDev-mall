package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.OmsStoreAddressExample;
import com.macro.mall.selfcheck.service.SelfcheckStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自助结算门店服务实现类
 */
@Slf4j
@Service
public class SelfcheckStoreServiceImpl implements SelfcheckStoreService {

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;

    @Override
    public List<OmsSchool> getEnabledSchools() {
        OmsSchoolExample example = new OmsSchoolExample();
        example.createCriteria().andStatusEqualTo(true);
        example.setOrderByClause("school_name ASC");
        return schoolMapper.selectByExample(example);
    }

    @Override
    public List<OmsStoreAddress> getStoresBySchool(Long schoolId) {
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();
        criteria.andSchoolIdEqualTo(schoolId);
        example.setOrderByClause("is_default DESC, create_time DESC");
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<OmsStoreAddress> getAllStores() {
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.setOrderByClause("is_default DESC, create_time DESC");
        return storeAddressMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public OmsStoreAddress getStoreById(Long id) {
        return storeAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    public Map<String, Object> getStoresGroupBySchool() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有启用的学校
        List<OmsSchool> schools = getEnabledSchools();

        // 获取所有门店
        List<OmsStoreAddress> allStores = getAllStores();

        // 按学校分组
        List<Map<String, Object>> schoolGroups = new ArrayList<>();

        // 处理有学校的门店
        Map<Long, List<OmsStoreAddress>> storesBySchool = allStores.stream()
            .filter(store -> store.getSchoolId() != null)
            .collect(Collectors.groupingBy(OmsStoreAddress::getSchoolId));

        for (OmsSchool school : schools) {
            List<OmsStoreAddress> schoolStores = storesBySchool.getOrDefault(school.getId(), new ArrayList<>());
            if (!schoolStores.isEmpty()) { // 只返回有门店的学校
                Map<String, Object> schoolGroup = new HashMap<>();
                schoolGroup.put("school", school);
                schoolGroup.put("stores", schoolStores);
                schoolGroups.add(schoolGroup);
            }
        }

        // 处理没有学校的门店
        List<OmsStoreAddress> storesWithoutSchool = allStores.stream()
            .filter(store -> store.getSchoolId() == null)
            .collect(Collectors.toList());

        if (!storesWithoutSchool.isEmpty()) {
            Map<String, Object> noSchoolGroup = new HashMap<>();
            OmsSchool noSchool = new OmsSchool();
            noSchool.setId(null);
            noSchool.setSchoolName("其他门店");
            noSchool.setSchoolLogo(null);
            noSchoolGroup.put("school", noSchool);
            noSchoolGroup.put("stores", storesWithoutSchool);
            schoolGroups.add(noSchoolGroup);
        }

        result.put("schoolGroups", schoolGroups);
        result.put("totalGroups", schoolGroups.size());
        result.put("totalStores", allStores.size());

        return result;
    }

    @Override
    public OmsSchool getSchoolById(Long id) {
        return schoolMapper.selectByPrimaryKey(id);
    }
}
