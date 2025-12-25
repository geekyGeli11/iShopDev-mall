package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.OmsStoreAddressExample;
import com.macro.mall.selfcheck.service.SelfcheckStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 自助结算门店管理Controller
 */
@RestController
@Tag(name = "SelfcheckStoreController", description = "自助结算门店管理")
@RequestMapping("/selfcheck/store")
public class SelfcheckStoreController {

    @Autowired
    private SelfcheckStoreService storeService;

    @Operation(summary = "获取所有启用的学校列表")
    @GetMapping("/schools")
    public CommonResult<List<OmsSchool>> getSchoolList() {
        List<OmsSchool> schools = storeService.getEnabledSchools();
        return CommonResult.success(schools);
    }

    @Operation(summary = "根据学校ID获取门店列表")
    @GetMapping("/stores")
    public CommonResult<List<OmsStoreAddress>> getStoresBySchool(
            @Parameter(description = "学校ID") @RequestParam Long schoolId) {
        List<OmsStoreAddress> stores = storeService.getStoresBySchool(schoolId);
        return CommonResult.success(stores);
    }

    @Operation(summary = "获取所有可用门店地址")
    @GetMapping("/stores/all")
    public CommonResult<List<OmsStoreAddress>> getAllStores() {
        List<OmsStoreAddress> stores = storeService.getAllStores();
        return CommonResult.success(stores);
    }

    @Operation(summary = "根据ID获取门店详情")
    @GetMapping("/stores/{id}")
    public CommonResult<OmsStoreAddress> getStoreById(@PathVariable Long id) {
        OmsStoreAddress store = storeService.getStoreById(id);
        if (store != null) {
            return CommonResult.success(store);
        }
        return CommonResult.failed("门店不存在");
    }

    @Operation(summary = "获取按学校分组的门店列表")
    @GetMapping("/stores/groupBySchool")
    public CommonResult<Map<String, Object>> getStoresGroupBySchool() {
        Map<String, Object> result = storeService.getStoresGroupBySchool();
        return CommonResult.success(result);
    }

    @Operation(summary = "根据学校ID获取学校详情")
    @GetMapping("/schools/{id}")
    public CommonResult<OmsSchool> getSchoolById(@PathVariable Long id) {
        OmsSchool school = storeService.getSchoolById(id);
        if (school != null) {
            return CommonResult.success(school);
        }
        return CommonResult.failed("学校不存在");
    }
}
