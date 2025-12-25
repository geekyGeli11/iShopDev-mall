package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.service.PmsProductSchoolRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品学校关联管理Controller
 */
@Controller
@Tag(name = "PmsProductSchoolRelationController", description = "商品学校关联管理")
@RequestMapping("/productSchoolRelation")
public class PmsProductSchoolRelationController {

    @Autowired
    private PmsProductSchoolRelationService productSchoolRelationService;

    @Operation(summary = "获取商品关联的学校列表")
    @RequestMapping(value = "/getSchoolsByProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsSchool>> getSchoolsByProductId(@PathVariable Long productId) {
        List<OmsSchool> schoolList = productSchoolRelationService.getSchoolsByProductId(productId);
        return CommonResult.success(schoolList);
    }

    @Operation(summary = "更新商品关联的学校")
    @RequestMapping(value = "/updateProductSchools/{productId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateProductSchools(@PathVariable Long productId, @RequestBody List<Long> schoolIds) {
        boolean success = productSchoolRelationService.updateProductSchools(productId, schoolIds);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "检查商品是否关联了指定学校")
    @RequestMapping(value = "/checkAssociation", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> checkAssociation(@RequestParam Long productId, @RequestParam Long schoolId) {
        boolean isAssociated = productSchoolRelationService.isProductAssociatedWithSchool(productId, schoolId);
        return CommonResult.success(isAssociated);
    }

    @Operation(summary = "根据学校获取关联的商品ID列表")
    @RequestMapping(value = "/getProductsBySchool/{schoolId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Long>> getProductIdsBySchoolId(@PathVariable Long schoolId) {
        List<Long> productIds = productSchoolRelationService.getProductIdsBySchoolId(schoolId);
        return CommonResult.success(productIds);
    }
}
