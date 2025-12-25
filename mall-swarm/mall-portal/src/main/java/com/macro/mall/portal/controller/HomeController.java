package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsSubject;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.SmsHomeNewProduct;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.domain.NewProductDTO;
import com.macro.mall.portal.domain.ProductListDTO;
import com.macro.mall.portal.domain.SubjectDetailResult;
import com.macro.mall.portal.service.HomeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页内容管理Controller
 * Created by macro on 2019/1/28.
 */
@Controller
@Tag(name = "HomeController", description = "首页内容管理")
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @Operation(summary = "首页内容页信息展示")
    @RequestMapping(value = "/content", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeContentResult> content(@RequestParam(required = false) Long schoolId) {
        HomeContentResult contentResult = homeService.content(schoolId);
        return CommonResult.success(contentResult);
    }

    @Operation(summary = "分页获取推荐商品")
    @RequestMapping(value = "/recommendProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> recommendProductList(@RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize,
                                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProduct> productList = homeService.recommendProductList(pageSize, pageNum);
        return CommonResult.success(productList);
    }

    @Operation(summary = "获取首页商品分类")
    @RequestMapping(value = "/productCateList/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategory>> getProductCateList(@PathVariable Long parentId) {
        List<PmsProductCategory> productCategoryList = homeService.getProductCateList(parentId);
        return CommonResult.success(productCategoryList);
    }

    @Operation(summary = "根据分类获取专题")
    @RequestMapping(value = "/subjectList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsSubject>> getSubjectList(@RequestParam(required = false) Long cateId,
                                                         @RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<CmsSubject> subjectList = homeService.getSubjectList(cateId, pageSize, pageNum);
        return CommonResult.success(subjectList);
    }

    @Operation(summary = "分页获取人气推荐商品")
    @RequestMapping(value = "/hotProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> hotProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                                         @RequestParam(required = false) Long schoolId) {
        List<PmsProduct> productList = homeService.hotProductList(pageNum, pageSize, schoolId);
        return CommonResult.success(productList);
    }

    @Operation(summary = "分页获取人气推荐商品（包含价格）")
    @RequestMapping(value = "/hotProductListWithPrice", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<java.util.Map<String, Object>>> hotProductListWithPrice(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                                      @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        List<java.util.Map<String, Object>> productList = homeService.hotProductListWithPrice(pageNum, pageSize);
        return CommonResult.success(productList);
    }

    @Operation(summary = "分页获取新品推荐商品")
    @RequestMapping(value = "/newProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<NewProductDTO>> newProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                                         @RequestParam(required = false) Long schoolId) {
        List<NewProductDTO> productList = homeService.newProductList(pageNum, pageSize, schoolId);
        return CommonResult.success(productList);
    }

    @Operation(summary = "按销量分页获取商品列表")
    @RequestMapping(value = "/productListBySales", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<ProductListDTO>> productListBySales(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                 @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                                 @RequestParam(required = false) Long schoolId) {
        List<ProductListDTO> productList = homeService.productListBySales(pageNum, pageSize, schoolId);
        return CommonResult.success(productList);
    }

    @Operation(summary = "根据专题ID获取专题详情及关联商品列表")
    @RequestMapping(value = "/subjectDetail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SubjectDetailResult> getSubjectDetail(
            @RequestParam(value = "subjectId") Long subjectId,
            @RequestParam(value = "recommendSubjectId", required = false) Long recommendSubjectId) {
        // 调用服务层方法获取专题详情和关联商品列表
        SubjectDetailResult subjectDetail = homeService.getSubjectDetail(subjectId, recommendSubjectId);
        return CommonResult.success(subjectDetail);
    }

}
