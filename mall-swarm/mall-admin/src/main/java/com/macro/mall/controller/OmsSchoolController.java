package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.service.OmsSchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校管理Controller
 */
@RestController
@Tag(name = "OmsSchoolController", description = "学校管理")
@RequestMapping("/school")
public class OmsSchoolController {

    @Autowired
    private OmsSchoolService schoolService;

    @Operation(summary = "添加学校")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody OmsSchool school) {
        OmsSchool result = schoolService.create(school);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed("添加学校失败");
    }

    @Operation(summary = "更新学校信息")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody OmsSchool school) {
        OmsSchool result = schoolService.update(id, school);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed("更新学校失败");
    }

    @Operation(summary = "删除学校")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = schoolService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed("删除学校失败");
    }

    @Operation(summary = "根据ID获取学校详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<OmsSchool> getById(@PathVariable Long id) {
        OmsSchool school = schoolService.getById(id);
        if (school != null) {
            return CommonResult.success(school);
        }
        return CommonResult.failed("学校不存在");
    }

    @Operation(summary = "根据条件分页获取学校列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<OmsSchool>> list(
            @Parameter(description = "学校名称") @RequestParam(value = "schoolName", required = false) String schoolName,
            @Parameter(description = "状态：false-禁用，true-启用") @RequestParam(value = "status", required = false) Boolean status,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<OmsSchool> list = schoolService.list(schoolName, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @Operation(summary = "获取所有启用状态的学校（用于下拉选择）")
    @GetMapping("/listEnabled")
    @ResponseBody
    public CommonResult<List<OmsSchool>> listEnabled() {
        List<OmsSchool> list = schoolService.listEnabled();
        return CommonResult.success(list);
    }

    @Operation(summary = "更新学校状态")
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public CommonResult updateStatus(
            @Parameter(description = "学校ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        boolean success = schoolService.updateStatus(id, status);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed("更新状态失败");
    }
}
