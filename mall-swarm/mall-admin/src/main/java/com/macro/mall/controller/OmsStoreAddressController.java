package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.service.OmsStoreAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "OmsStoreAddressController", description = "门店地址管理")
@RequestMapping("/storeAddress")
public class OmsStoreAddressController {

    @Autowired
    private OmsStoreAddressService storeAddressService;

    @Operation(summary = "添加门店地址")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody OmsStoreAddress storeAddress) {
        OmsStoreAddress result = storeAddressService.create(storeAddress);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新门店地址")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody OmsStoreAddress storeAddress) {
        OmsStoreAddress result = storeAddressService.update(id, storeAddress);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除门店地址")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = storeAddressService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取门店地址详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<OmsStoreAddress> getById(@PathVariable Long id) {
        OmsStoreAddress result = storeAddressService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取门店地址列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<OmsStoreAddress>> list(
            @Parameter(description = "门店名称") @RequestParam(value = "addressName", required = false) String addressName,
            @Parameter(description = "联系电话") @RequestParam(value = "phone", required = false) String phone,
            @Parameter(description = "是否默认") @RequestParam(value = "isDefault", required = false) Boolean isDefault,
            @Parameter(description = "学校ID") @RequestParam(value = "schoolId", required = false) Long schoolId,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<OmsStoreAddress> list = storeAddressService.list(addressName, phone, isDefault, schoolId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @Operation(summary = "获取按学校分组的门店列表")
    @GetMapping("/listGroupBySchool")
    @ResponseBody
    public CommonResult<Map<String, Object>> listGroupBySchool() {
        Map<String, Object> result = storeAddressService.listGroupBySchool();
        return CommonResult.success(result);
    }

    // ==================== 地库相关 API ====================

    @Operation(summary = "获取所有门店（包含地库）- 用于后台库存操作")
    @GetMapping("/listAll")
    @ResponseBody
    public CommonResult<List<OmsStoreAddress>> listAllStores() {
        List<OmsStoreAddress> list = storeAddressService.listAllStores();
        return CommonResult.success(list);
    }

    @Operation(summary = "获取可见门店列表（不包含地库）- 用于门店地址管理")
    @GetMapping("/listVisible")
    @ResponseBody
    public CommonResult<List<OmsStoreAddress>> listVisibleStores() {
        List<OmsStoreAddress> list = storeAddressService.listVisibleStores();
        return CommonResult.success(list);
    }

    @Operation(summary = "获取地库信息")
    @GetMapping("/warehouse")
    @ResponseBody
    public CommonResult<OmsStoreAddress> getWarehouse() {
        OmsStoreAddress warehouse = storeAddressService.getWarehouse();
        if (warehouse != null) {
            return CommonResult.success(warehouse);
        }
        return CommonResult.failed("地库不存在");
    }

    @Operation(summary = "初始化地库")
    @PostMapping("/initWarehouse")
    @ResponseBody
    public CommonResult initializeWarehouse() {
        try {
            storeAddressService.initializeWarehouse();
            return CommonResult.success(null, "地库初始化成功");
        } catch (Exception e) {
            return CommonResult.failed("地库初始化失败：" + e.getMessage());
        }
    }
}