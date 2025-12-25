package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.portal.domain.TraceabilityDetail;
import com.macro.mall.portal.service.TraceabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "CmsTraceabilityController", description = "用户端溯源管理")
@RequestMapping("/traceability")
public class CmsTraceabilityController {

    @Autowired
    private TraceabilityService traceabilityListService;

    @Operation(summary = "获取不同分类的溯源记录前三个")
    @GetMapping("/top3")
    public CommonResult<List<CmsTraceabilityList>> getTop3ByCategory(@RequestParam String category) {
        List<CmsTraceabilityList> traceabilityLists = traceabilityListService.getTop3ByCategory(category);
        if (traceabilityLists != null && !traceabilityLists.isEmpty()) {
            return CommonResult.success(traceabilityLists, "获取成功");
        }
        return CommonResult.failed("未找到相关记录");
    }

    @Operation(summary = "分页获取某个分类的溯源记录")
    @GetMapping("/list")
    public CommonResult<CommonPage<CmsTraceabilityList>> listByCategory(
            @RequestParam String category,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<CmsTraceabilityList> traceabilityLists = traceabilityListService.listByCategory(category, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(traceabilityLists), "查询成功");
    }

    @Operation(summary = "获取溯源记录详情及其关联商品列表")
    @GetMapping("/detail/{id}")
    public CommonResult<TraceabilityDetail> getTraceabilityDetail(
            @PathVariable Integer id,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        TraceabilityDetail traceability = traceabilityListService.getDetailWithProducts(id, pageNum, pageSize);

        return CommonResult.success(traceability, "查询成功");
    }

}
