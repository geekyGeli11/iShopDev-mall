package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.OmsStoreAddressExample;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台门店地址管理
 */
@RestController
@Tag(name = "StoreAddressController", description = "前台门店地址管理")
@RequestMapping("/storeAddress")
public class StoreAddressController {

    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Operation(summary = "获取所有可用门店地址（不包含地库和中央仓库）")
    @GetMapping("/list")
    public CommonResult<List<OmsStoreAddress>> list() {
        // 查询所有普通门店地址（排除地库和中央仓库），按照是否默认排序
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        example.createCriteria()
                .andIsWarehouseEqualTo(false)
                .andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        example.setOrderByClause("is_default DESC, create_time DESC");
        List<OmsStoreAddress> list = storeAddressMapper.selectByExampleWithBLOBs(example);
        return CommonResult.success(list);
    }

    @Operation(summary = "根据学校ID获取门店列表（不包含地库和中央仓库）")
    @GetMapping("/listBySchool")
    public CommonResult<List<OmsStoreAddress>> listBySchool(
            @Parameter(description = "学校ID") @RequestParam Long schoolId) {

        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();
        criteria.andSchoolIdEqualTo(schoolId);
        criteria.andIsWarehouseEqualTo(false); // 过滤地库
        criteria.andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        example.setOrderByClause("is_default DESC, create_time DESC");

        List<OmsStoreAddress> list = storeAddressMapper.selectByExampleWithBLOBs(example);
        return CommonResult.success(list);
    }

    @Operation(summary = "根据ID获取门店地址详情（不包含地库和中央仓库）")
    @GetMapping("/{id}")
    public CommonResult<OmsStoreAddress> getById(@PathVariable Long id) {
        OmsStoreAddress storeAddress = storeAddressMapper.selectByPrimaryKey(id);
        if (storeAddress != null) {
            // 检查是否为地库或中央仓库，这些对用户端不可见
            if (Boolean.TRUE.equals(storeAddress.getIsWarehouse()) 
                    || "CENTRAL_WAREHOUSE".equals(storeAddress.getStoreType())) {
                return CommonResult.failed("门店不存在");
            }
            return CommonResult.success(storeAddress);
        }
        return CommonResult.failed("门店不存在");
    }

    @Operation(summary = "根据距离查询附近门店（不包含地库和中央仓库）")
    @GetMapping("/nearby")
    public CommonResult<List<OmsStoreAddress>> getNearbyStores(
            @Parameter(description = "用户当前经度") @RequestParam BigDecimal longitude,
            @Parameter(description = "用户当前纬度") @RequestParam BigDecimal latitude,
            @Parameter(description = "搜索半径（公里）") @RequestParam(defaultValue = "10") Double radius) {
        
        // 获取所有有坐标的普通门店（排除地库和中央仓库）
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();
        criteria.andLongitudeIsNotNull();
        criteria.andLatitudeIsNotNull();
        criteria.andIsWarehouseEqualTo(false); // 过滤地库
        criteria.andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        
        List<OmsStoreAddress> allStores = storeAddressMapper.selectByExampleWithBLOBs(example);
        
        // 计算距离并筛选
        List<OmsStoreAddress> nearbyStores = allStores.stream()
            .filter(store -> {
                double distance = calculateDistance(
                    latitude.doubleValue(), longitude.doubleValue(),
                    store.getLatitude().doubleValue(), store.getLongitude().doubleValue()
                );
                return distance <= radius;
            })
            .sorted((a, b) -> {
                double distanceA = calculateDistance(
                    latitude.doubleValue(), longitude.doubleValue(),
                    a.getLatitude().doubleValue(), a.getLongitude().doubleValue()
                );
                double distanceB = calculateDistance(
                    latitude.doubleValue(), longitude.doubleValue(),
                    b.getLatitude().doubleValue(), b.getLongitude().doubleValue()
                );
                return Double.compare(distanceA, distanceB);
            })
            .collect(Collectors.toList());
        
        return CommonResult.success(nearbyStores);
    }

    @Operation(summary = "获取默认门店地址（不包含地库和中央仓库）")
    @GetMapping("/default")
    public CommonResult<OmsStoreAddress> getDefaultStore() {
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();
        criteria.andIsDefaultEqualTo(true);
        criteria.andIsWarehouseEqualTo(false); // 过滤地库
        criteria.andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        
        List<OmsStoreAddress> list = storeAddressMapper.selectByExampleWithBLOBs(example);
        if (!list.isEmpty()) {
            return CommonResult.success(list.get(0));
        }
        return CommonResult.failed("未找到默认门店");
    }

    @Operation(summary = "根据城市查询门店（不包含地库和中央仓库）")
    @GetMapping("/by-city")
    public CommonResult<List<OmsStoreAddress>> getStoresByCity(
            @Parameter(description = "城市名称") @RequestParam String city) {
        
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();
        criteria.andCityLike("%" + city + "%");
        criteria.andIsWarehouseEqualTo(false); // 过滤地库
        criteria.andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        example.setOrderByClause("is_default DESC, create_time DESC");
        
        List<OmsStoreAddress> list = storeAddressMapper.selectByExampleWithBLOBs(example);
        return CommonResult.success(list);
    }

    /**
     * 计算两个坐标点之间的距离（单位：公里）
     * 使用Haversine公式
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    @Operation(summary = "搜索门店（不包含地库和中央仓库）")
    @GetMapping("/search")
    public CommonResult<List<OmsStoreAddress>> searchStores(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "学校ID") @RequestParam(required = false) String schoolIdStr) {

        OmsStoreAddressExample example = new OmsStoreAddressExample();
        OmsStoreAddressExample.Criteria criteria = example.createCriteria();

        // 过滤地库和中央仓库
        criteria.andIsWarehouseEqualTo(false);
        criteria.andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库

        // 按门店名称模糊搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            criteria.andAddressNameLike("%" + keyword.trim() + "%");
        }

        // 处理学校ID参数，支持字符串"null"的情况
        Long schoolId = null;
        if (schoolIdStr != null && !schoolIdStr.trim().isEmpty() && !"null".equals(schoolIdStr)) {
            try {
                schoolId = Long.parseLong(schoolIdStr);
            } catch (NumberFormatException e) {
                // 如果转换失败，忽略学校ID筛选
                schoolId = null;
            }
        }

        // 如果指定了学校ID，则按学校筛选
        if (schoolId != null) {
            criteria.andSchoolIdEqualTo(schoolId);
        }

        example.setOrderByClause("is_default DESC, create_time DESC");
        List<OmsStoreAddress> list = storeAddressMapper.selectByExampleWithBLOBs(example);
        return CommonResult.success(list);
    }

    @Operation(summary = "获取按学校分组的门店列表（不包含地库和中央仓库）")
    @GetMapping("/listGroupBySchool")
    public CommonResult<Map<String, Object>> listGroupBySchool() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有启用的学校
        OmsSchoolExample schoolExample = new OmsSchoolExample();
        schoolExample.createCriteria().andStatusEqualTo(true);
        schoolExample.setOrderByClause("school_name ASC");
        List<OmsSchool> schools = schoolMapper.selectByExample(schoolExample);

        // 获取所有普通门店（排除地库和中央仓库）
        OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
        storeExample.createCriteria()
                .andIsWarehouseEqualTo(false) // 过滤地库
                .andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        storeExample.setOrderByClause("school_id ASC, is_default DESC, create_time DESC");
        List<OmsStoreAddress> allStores = storeAddressMapper.selectByExampleWithBLOBs(storeExample);

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

        return CommonResult.success(result);
    }
}