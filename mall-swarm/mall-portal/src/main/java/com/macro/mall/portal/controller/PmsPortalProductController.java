package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.PmsPortalProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台商品管理Controller
 * Created by macro on 2020/4/6.
 */
@Controller
@Tag(name = "PmsPortalProductController", description = "前台商品管理")
@RequestMapping("/product")
public class PmsPortalProductController {

    @Autowired
    private PmsPortalProductService portalProductService;

    @Operation(summary = "综合搜索、筛选、排序")
    @Parameter(name = "sort", description = "排序字段:0->综合推荐；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            in= ParameterIn.QUERY,schema = @Schema(type = "integer",defaultValue = "0",allowableValues = {"0","1","2","3","4"}))
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> search(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long brandId,
                                                       @RequestParam(required = false) Long productCategoryId,
                                                       @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                       @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                       @RequestParam(required = false, defaultValue = "0") Integer sort,
                                                       @RequestParam(required = false) Long schoolId,
                                                       @RequestParam(required = false) Boolean isDIY) {
        List<PmsProduct> productList = portalProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort, schoolId, isDIY);

        // 当有学校筛选时，需要手动构建分页信息
        if (schoolId != null) {
            long total = portalProductService.searchCount(keyword, brandId, productCategoryId, schoolId, isDIY);
            CommonPage<PmsProduct> result = new CommonPage<>();
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setTotal(total);
            result.setTotalPage((int) Math.ceil((double) total / pageSize));
            result.setList(productList);
            return CommonResult.success(result);
        } else {
            // 没有学校筛选时，使用PageHelper的分页信息
            return CommonResult.success(CommonPage.restPage(productList));
        }
    }

    @Operation(summary = "以树形结构获取所有商品分类")
    @RequestMapping(value = "/categoryTreeList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategoryNode>> categoryTreeList() {
        List<PmsProductCategoryNode> list = portalProductService.categoryTreeList();
        return CommonResult.success(list);
    }

    @Operation(summary = "获取前台商品详情")
    @Parameter(name = "storeId", description = "门店ID（暂未使用，保留兼容性）", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsPortalProductDetail> detail(@PathVariable Long id,
                                                       @RequestParam(required = false) Long storeId) {
        PmsPortalProductDetail productDetail = portalProductService.detail(id, storeId);
        if (productDetail == null) {
            return CommonResult.failed("商品不存在或已下架");
        }
        return CommonResult.success(productDetail);
    }
}
