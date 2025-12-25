package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.StyleModelCardResult;
import com.macro.mall.portal.service.PortalStyleModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序端风格模型Controller
 * Created by macro on 2024/8/25.
 */
@Controller
@Tag(name = "PortalStyleModelController", description = "小程序端风格模型管理")
@RequestMapping("/styleModel")
public class PortalStyleModelController {
    @Autowired
    private PortalStyleModelService portalStyleModelService;

    @Operation(summary = "获取风格模型卡片列表")
    @RequestMapping(value = "/cards", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StyleModelCardResult>> getStyleModelCards() {
        List<StyleModelCardResult> cardList = portalStyleModelService.getStyleModelCards();
        return CommonResult.success(cardList);
    }

    @Operation(summary = "根据风格模型ID获取关联商品")
    @RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getStyleModelProducts(@PathVariable Long id,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        return CommonResult.success(portalStyleModelService.getStyleModelProducts(id, pageSize, pageNum));
    }

    @Operation(summary = "获取风格模型关联的商品列表（支持分类筛选）")
    @RequestMapping(value = "/{id}/productList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getStyleModelProductList(@PathVariable Long id,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                  @RequestParam(value = "category", required = false) String category) {
        return CommonResult.success(portalStyleModelService.getStyleModelProductList(id, page, pageSize, category));
    }

    @Operation(summary = "获取风格模型详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getStyleModelDetail(@PathVariable Long id) {
        return CommonResult.success(portalStyleModelService.getStyleModelDetail(id));
    }
}
