package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.BundleConfirmOrderResult;
import com.macro.mall.portal.domain.BundleOrderParam;
import com.macro.mall.portal.domain.PortalBundleDetail;
import com.macro.mall.portal.domain.PortalBundleListItem;
import com.macro.mall.portal.service.PortalBundleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 组合商品前台Controller
 */
@Controller
@Tag(name = "PortalBundleController", description = "组合商品前台接口")
@RequestMapping("/bundle")
public class PortalBundleController {

    @Autowired
    private PortalBundleService bundleService;

    @Operation(summary = "分页查询组合商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PortalBundleListItem>> list(
            @RequestParam(value = "schoolId", required = false) Long schoolId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<PortalBundleListItem> bundleList = bundleService.list(schoolId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(bundleList));
    }

    @Operation(summary = "获取组合商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PortalBundleDetail> getDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        PortalBundleDetail detail = bundleService.getDetail(id, storeId);
        return CommonResult.success(detail);
    }

    @Operation(summary = "生成组合商品确认订单")
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<BundleConfirmOrderResult> generateConfirmOrder(
            @Validated @RequestBody BundleOrderParam param) {
        BundleConfirmOrderResult result = bundleService.generateConfirmOrder(param);
        return CommonResult.success(result);
    }

    @Operation(summary = "创建组合商品订单")
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> createOrder(
            @Validated @RequestBody BundleOrderParam param) {
        Map<String, Object> result = bundleService.createOrder(param);
        return CommonResult.success(result);
    }
}
