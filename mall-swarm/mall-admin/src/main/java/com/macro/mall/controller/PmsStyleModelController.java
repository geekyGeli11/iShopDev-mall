package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsStyleModelParam;
import com.macro.mall.dto.PmsStyleModelQueryParam;
import com.macro.mall.model.PmsStyleModel;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.AliyunImageAnalysisService;
import com.macro.mall.service.PmsStyleModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风格模型功能Controller
 * Created by macro on 2024/8/25.
 */
@Controller
@Tag(name = "PmsStyleModelController", description = "风格模型管理")
@RequestMapping("/styleModel")
public class PmsStyleModelController {
    @Autowired
    private PmsStyleModelService styleModelService;

    @Autowired
    private AliyunImageAnalysisService aliyunImageAnalysisService;

    @Operation(summary = "获取全部风格模型列表")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsStyleModel>> getList() {
        return CommonResult.success(styleModelService.listAllStyleModel());
    }

    @Operation(summary = "分页查询风格模型")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsStyleModel>> list(PmsStyleModelQueryParam queryParam,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsStyleModel> styleModelList = styleModelService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(styleModelList));
    }

    @Operation(summary = "添加风格模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody PmsStyleModelParam styleModelParam) {
        CommonResult commonResult;
        int count = styleModelService.createStyleModel(styleModelParam);
        if (count == 1) {
            commonResult = CommonResult.success(count);
        } else {
            commonResult = CommonResult.failed();
        }
        return commonResult;
    }

    @Operation(summary = "更新风格模型")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody PmsStyleModelParam styleModelParam) {
        CommonResult commonResult;
        int count = styleModelService.updateStyleModel(id, styleModelParam);
        if (count == 1) {
            commonResult = CommonResult.success(count);
        } else {
            commonResult = CommonResult.failed();
        }
        return commonResult;
    }

    @Operation(summary = "删除风格模型")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = styleModelService.deleteStyleModel(id);
        if (count == 1) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量删除风格模型")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = styleModelService.deleteStyleModel(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "获取风格模型详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsStyleModel> getItem(@PathVariable Long id) {
        PmsStyleModel styleModel = styleModelService.getStyleModel(id);
        return CommonResult.success(styleModel);
    }

    @Operation(summary = "修改风格模型状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        int count = styleModelService.updateStatus(id, status);
        if (count == 1) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "获取风格模型关联的商品列表")
    @RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> getStyleModelProducts(@PathVariable Long id,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProduct> productList = styleModelService.getStyleModelProducts(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @Operation(summary = "添加商品到风格模型")
    @RequestMapping(value = "/{id}/products/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addProductsToStyleModel(@PathVariable Long id, @RequestBody Map<String, List<Long>> request) {
        List<Long> productIds = request.get("productIds");
        int count = styleModelService.addProductsToStyleModel(id, productIds);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "从风格模型移除商品")
    @RequestMapping(value = "/{id}/products/remove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult removeProductsFromStyleModel(@PathVariable Long id, @RequestBody Map<String, List<Long>> request) {
        List<Long> productIds = request.get("productIds");
        int count = styleModelService.removeProductsFromStyleModel(id, productIds);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量操作")
    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batchOperate(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        String type = (String) request.get("type");
        int count = styleModelService.batchOperate(ids, type);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "提取图片风格介绍")
    @RequestMapping(value = "/extractStyleDescription", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> extractStyleDescription(@RequestParam String imageUrl) {
        try {
            // 检查服务是否可用
            if (!aliyunImageAnalysisService.isServiceAvailable()) {
                return CommonResult.failed("AI图片分析服务未配置或不可用");
            }

            // 调用阿里云万相API提取风格描述
            String description = aliyunImageAnalysisService.extractStyleDescription(imageUrl);
            return CommonResult.success(description);
        } catch (Exception e) {
            return CommonResult.failed("提取风格介绍失败：" + e.getMessage());
        }
    }
}
