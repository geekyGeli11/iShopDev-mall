package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsDiyTemplateDetailVO;
import com.macro.mall.dto.PmsDiyTemplateParam;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.service.PmsDiyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DIY模板管理Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PmsDiyTemplateController", description = "DIY模板管理")
@RequestMapping("/diyTemplate")
public class PmsDiyTemplateController {
    
    @Autowired
    private PmsDiyTemplateService templateService;

    @Operation(summary = "创建DIY模板")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody PmsDiyTemplateParam templateParam) {
        PmsDiyTemplate template = new PmsDiyTemplate();
        BeanUtils.copyProperties(templateParam, template);
        int count = templateService.create(template);
        if (count > 0) {
            return CommonResult.success(template);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新DIY模板")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody PmsDiyTemplateParam templateParam) {
        PmsDiyTemplate template = new PmsDiyTemplate();
        BeanUtils.copyProperties(templateParam, template);
        int count = templateService.update(id, template);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除DIY模板")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = templateService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除DIY模板")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = templateService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取DIY模板详情")
    @GetMapping("/{id}")
    public CommonResult<PmsDiyTemplate> getItem(@PathVariable Long id) {
        PmsDiyTemplate template = templateService.getItem(id);
        return CommonResult.success(template);
    }

    @Operation(summary = "分页查询DIY模板")
    @GetMapping("/list")
    public CommonResult<CommonPage<PmsDiyTemplate>> list(
            @Parameter(description = "关键词") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "商品分类ID") @RequestParam(value = "productCategoryId", required = false) Long productCategoryId,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Byte status,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        List<PmsDiyTemplate> templateList = templateService.list(keyword, productCategoryId, status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(templateList));
    }

    @Operation(summary = "获取启用的模板列表")
    @GetMapping("/listEnabled")
    public CommonResult<List<PmsDiyTemplate>> listEnabled() {
        List<PmsDiyTemplate> templateList = templateService.listEnabled();
        return CommonResult.success(templateList);
    }

    @Operation(summary = "批量修改模板状态")
    @PostMapping("/update/status")
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids, @RequestParam("status") Byte status) {
        int count = templateService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "复制模板")
    @PostMapping("/copy/{id}")
    public CommonResult copyTemplate(@PathVariable Long id, @RequestParam("newName") String newName) {
        int count = templateService.copyTemplate(id, newName);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据商品分类获取模板列表")
    @GetMapping("/listByProductCategory/{productCategoryId}")
    public CommonResult<List<PmsDiyTemplate>> listByProductCategory(@PathVariable Long productCategoryId) {
        List<PmsDiyTemplate> templateList = templateService.listByProductCategory(productCategoryId);
        return CommonResult.success(templateList);
    }

    @Operation(summary = "获取模板完整详情")
    @GetMapping("/detail/{id}")
    public CommonResult<PmsDiyTemplateDetailVO> getTemplateDetail(@PathVariable Long id) {
        PmsDiyTemplateDetailVO templateDetail = templateService.getTemplateDetail(id);
        return CommonResult.success(templateDetail);
    }
}
