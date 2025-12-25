package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsProductDiyConfigParam;
import com.macro.mall.dto.PmsProductDiyConfigVO;
import com.macro.mall.dto.PmsProductExportDTO;
import com.macro.mall.dto.PmsProductListVO;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.dto.PmsProductShareResult;
import com.macro.mall.dto.PmsProductWithStoreStockVO;
import com.macro.mall.util.CsvExportUtil;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.PmsProductService;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品管理Controller
 * Created by macro on 2018/4/26.
 */
@Controller
@Tag(name = "PmsProductController", description = "商品管理")
@RequestMapping("/product")
public class PmsProductController {
    @Autowired
    private PmsProductService productService;

    @Operation(summary = "创建商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsProductParam productParam) {
        int count = productService.create(productParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "根据商品id获取商品编辑信息")
    @RequestMapping(value = "/updateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductResult> getUpdateInfo(@PathVariable Long id) {
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }

    @Operation(summary = "更新商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PmsProductParam productParam) {
        int count = productService.update(id, productParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "查询商品")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<? extends PmsProduct>> getList(PmsProductQueryParam productQueryParam,
                                                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        // 如果传递了门店ID，返回包含门店库存的商品列表
        if (productQueryParam.getStoreId() != null) {
            List<PmsProductWithStoreStockVO> productList = productService.listWithStoreStock(productQueryParam, pageSize, pageNum);
            return CommonResult.success(CommonPage.restPage(productList));
        }
        List<PmsProduct> productList = productService.list(productQueryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @Operation(summary = "根据商品名称或货号模糊查询")
    @RequestMapping(value = "/simpleList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> getList(String keyword) {
        List<PmsProduct> productList = productService.list(keyword);
        return CommonResult.success(productList);
    }

    @Operation(summary = "批量修改审核状态")
    @RequestMapping(value = "/update/verifyStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateVerifyStatus(@RequestParam("ids") List<Long> ids,
                                           @RequestParam("verifyStatus") Integer verifyStatus,
                                           @RequestParam("detail") String detail) {
        int count = productService.updateVerifyStatus(ids, verifyStatus, detail);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量上下架")
    @RequestMapping(value = "/update/publishStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                            @RequestParam("publishStatus") Integer publishStatus) {
        int count = productService.updatePublishStatus(ids, publishStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量推荐商品")
    @RequestMapping(value = "/update/recommendStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                              @RequestParam("recommendStatus") Integer recommendStatus) {
        int count = productService.updateRecommendStatus(ids, recommendStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量设为新品")
    @RequestMapping(value = "/update/newStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNewStatus(@RequestParam("ids") List<Long> ids,
                                        @RequestParam("newStatus") Integer newStatus) {
        int count = productService.updateNewStatus(ids, newStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量修改删除状态")
    @RequestMapping(value = "/update/deleteStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDeleteStatus(@RequestParam("ids") List<Long> ids,
                                           @RequestParam("deleteStatus") Integer deleteStatus) {
        int count = productService.updateDeleteStatus(ids, deleteStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "获取商品DIY配置")
    @GetMapping("/diyConfig/{id}")
    @ResponseBody
    public CommonResult<PmsProductDiyConfigVO> getDiyConfig(@PathVariable Long id) {
        PmsProductDiyConfigVO configVO = productService.getDiyConfigDetail(id);
        return CommonResult.success(configVO);
    }

    @Operation(summary = "更新商品DIY配置")
    @PostMapping("/diyConfig/update/{id}")
    @ResponseBody
    public CommonResult updateDiyConfig(@PathVariable Long id, @Validated @RequestBody PmsProductDiyConfigParam configParam) {
        int count = productService.updateDiyConfig(id, configParam.getDiyEnabled(), configParam.getDiyTemplateId());
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量修改商品DIY状态")
    @PostMapping("/diyConfig/updateStatus")
    @ResponseBody
    public CommonResult updateDiyStatus(@RequestParam("ids") List<Long> ids, @RequestParam("diyEnabled") Byte diyEnabled) {
        int count = productService.updateDiyStatus(ids, diyEnabled);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "查询商品列表（包含运费模板和学校信息）")
    @RequestMapping(value = "/listWithDetails", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductListVO>> getListWithDetails(PmsProductQueryParam productQueryParam,
                                                                         @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        // 获取商品列表和总数
        List<PmsProductListVO> productList = productService.listWithDetails(productQueryParam, pageSize, pageNum);
        long total = productService.countProductsWithDetails(productQueryParam);

        // 手动构建分页信息
        CommonPage<PmsProductListVO> result = new CommonPage<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setTotalPage((int) Math.ceil((double) total / pageSize));
        result.setList(productList);

        return CommonResult.success(result);
    }

    @Operation(summary = "批量修改商品学校关联")
    @RequestMapping(value = "/update/schools", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateProductSchools(@RequestParam("productIds") List<Long> productIds,
                                           @RequestParam("schoolIds") List<Long> schoolIds) {
        int count = productService.updateProductSchools(productIds, schoolIds);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量修改商品分类")
    @RequestMapping(value = "/update/category", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateProductCategory(@RequestParam("ids") List<Long> ids,
                                            @RequestParam("productCategoryId") Long productCategoryId) {
        int count = productService.updateProductCategory(ids, productCategoryId);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "导出商品数据")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<?> exportProducts(PmsProductQueryParam productQueryParam, HttpServletResponse response) throws IOException {
        // 判断数据规模
        long total = productService.countProducts(productQueryParam);
        final int PAGE_SIZE = 3000;
        long threshold = 100_000; // 10万行以内同步导出

        // 表头映射
        LinkedHashMap<String, String> headerMap = buildProductExportHeader();

        if (total <= threshold) {
            // 同步导出：分页拉取并直接流式写出 CSV
            java.util.concurrent.atomic.AtomicInteger pageNo = new java.util.concurrent.atomic.AtomicInteger(1);
            List<PmsProductExportDTO> firstBatch = productService.exportProductsPaged(productQueryParam, PAGE_SIZE, pageNo.get());
            List<Map<String, Object>> firstData = convertProductBatchForExport(firstBatch);

            CsvExportUtil.exportCsv("商品数据", headerMap, firstData, () -> {
                int next = pageNo.incrementAndGet();
                List<PmsProductExportDTO> batch = productService.exportProductsPaged(productQueryParam, PAGE_SIZE, next);
                return batch.isEmpty() ? null : convertProductBatchForExport(batch);
            }, response);
            return null;
        } else {
            // 数据量过大，提示异步导出
            return CommonResult.success(null, "数据量过大，已转入后台异步导出，请稍后在导出列表查看");
        }
    }

    private LinkedHashMap<String, String> buildProductExportHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productId", "商品ID");
        headerMap.put("productName", "商品名称");
        headerMap.put("productSn", "商品货号");
        headerMap.put("brandName", "品牌名称");
        headerMap.put("productCategoryName", "商品分类");
        headerMap.put("price", "商品价格");
        headerMap.put("promotionPrice", "促销价格");
        headerMap.put("weight", "商品重量(克)");
        headerMap.put("unit", "商品单位");
        headerMap.put("stock", "商品库存");
        headerMap.put("lowStock", "库存预警值");
        headerMap.put("sale", "销量");
        headerMap.put("publishStatusName", "上架状态");
        headerMap.put("newStatusName", "新品状态");
        headerMap.put("recommandStatusName", "推荐状态");
        headerMap.put("diyEnabledName", "DIY状态");
        headerMap.put("freightTemplateName", "运费模板名称");
        headerMap.put("schoolNames", "关联学校");
        headerMap.put("skuId", "SKU ID");
        headerMap.put("skuCode", "SKU编码");
        headerMap.put("skuPrice", "SKU价格");
        headerMap.put("skuStock", "SKU库存");
        headerMap.put("skuLowStock", "SKU库存预警值");
        headerMap.put("skuSpData", "SKU规格信息");
        headerMap.put("subTitle", "商品副标题");
        headerMap.put("keywords", "商品关键词");
        headerMap.put("note", "商品备注");
        headerMap.put("serviceNames", "商品服务");
        return headerMap;
    }

    private List<Map<String, Object>> convertProductBatchForExport(List<PmsProductExportDTO> batch) {
        return batch.stream().map(dto -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", dto.getProductId());
            map.put("productName", dto.getProductName());
            map.put("productSn", dto.getProductSn());
            map.put("brandName", dto.getBrandName());
            map.put("productCategoryName", dto.getProductCategoryName());
            map.put("price", dto.getPrice());
            map.put("promotionPrice", dto.getPromotionPrice());
            map.put("weight", dto.getWeight());
            map.put("unit", dto.getUnit());
            map.put("stock", dto.getStock());
            map.put("lowStock", dto.getLowStock());
            map.put("sale", dto.getSale());
            map.put("publishStatusName", dto.getPublishStatusName());
            map.put("newStatusName", dto.getNewStatusName());
            map.put("recommandStatusName", dto.getRecommandStatusName());
            map.put("diyEnabledName", dto.getDiyEnabledName());
            map.put("freightTemplateName", dto.getFreightTemplateName());
            map.put("schoolNames", dto.getSchoolNames());
            map.put("skuId", dto.getSkuId());
            map.put("skuCode", dto.getSkuCode());
            map.put("skuPrice", dto.getSkuPrice());
            map.put("skuStock", dto.getSkuStock());
            map.put("skuLowStock", dto.getSkuLowStock());
            map.put("skuSpData", dto.getSkuSpData());
            map.put("subTitle", dto.getSubTitle());
            map.put("keywords", dto.getKeywords());
            map.put("note", dto.getNote());
            map.put("serviceNames", dto.getServiceNames());
            return map;
        }).collect(Collectors.toList());
    }

    @Operation(summary = "生成商品分享信息")
    @RequestMapping(value = "/generateShareInfo/{productId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<PmsProductShareResult> generateShareInfo(@PathVariable Long productId) {
        try {
            PmsProductShareResult shareResult = productService.generateShareInfo(productId);
            return CommonResult.success(shareResult, "分享信息生成成功");
        } catch (Exception e) {
            return CommonResult.failed("生成分享信息失败：" + e.getMessage());
        }
    }
}
