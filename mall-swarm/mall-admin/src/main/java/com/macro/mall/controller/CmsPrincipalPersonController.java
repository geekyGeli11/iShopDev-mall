package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsPrincipalPerson;
import com.macro.mall.service.CmsPrincipalPersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "CmsPrincipalPersonController", description = "百大主理人管理")
@RequestMapping("/principalPerson")
public class CmsPrincipalPersonController {

    @Autowired
    private CmsPrincipalPersonService principalPersonService;

    @Operation(summary = "添加主理人")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsPrincipalPerson principalPerson) {
        CmsPrincipalPerson result = principalPersonService.create(principalPerson);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新主理人")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsPrincipalPerson principalPerson) {
        CmsPrincipalPerson result = principalPersonService.update(id, principalPerson);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除主理人")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = principalPersonService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取主理人详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsPrincipalPerson> getById(@PathVariable Long id) {
        CmsPrincipalPerson result = principalPersonService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取主理人列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsPrincipalPerson>> list(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "isRecommend", required = false) Boolean isRecommend,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsPrincipalPerson> list = principalPersonService.list(name, position, status, isRecommend, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
}
