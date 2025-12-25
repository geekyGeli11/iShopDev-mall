package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyMaterialCategory;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.model.UmsAiStylizationRecord;
import com.macro.mall.model.UmsDiyDesign;
import com.macro.mall.portal.domain.DiyDesignParam;
import com.macro.mall.portal.domain.DiyPreviewResult;
import com.macro.mall.portal.domain.ProductDiyConfig;
import com.macro.mall.portal.service.PortalDiyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.macro.mall.portal.util.StpMemberUtil;

/**
 * 小程序端DIY功能Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PortalDiyController", description = "小程序端DIY功能")
@RequestMapping("/diy")
public class PortalDiyController {
    
    @Autowired
    private PortalDiyService diyService;

    @Operation(summary = "获取商品DIY配置信息")
    @GetMapping("/product/{productId}/config")
    public CommonResult<ProductDiyConfig> getProductDiyConfig(@PathVariable Long productId) {
        ProductDiyConfig config = diyService.getProductDiyConfig(productId);
        if (config == null) {
            return CommonResult.failed("商品不存在");
        }
        if (!config.getDiyEnabled()) {
            return CommonResult.failed("该商品不支持DIY定制");
        }
        return CommonResult.success(config);
    }

    @Operation(summary = "根据商品ID获取DIY模板")
    @GetMapping("/product/{productId}/template")
    public CommonResult<PmsDiyTemplate> getDiyTemplate(@PathVariable Long productId) {
        PmsDiyTemplate template = diyService.getDiyTemplateByProductId(productId);
        if (template == null) {
            return CommonResult.failed("该商品不支持DIY或未配置模板");
        }
        return CommonResult.success(template);
    }

    @Operation(summary = "获取商品可定制面信息")
    @GetMapping("/product/{productId}/customizableAreas")
    public CommonResult getProductCustomizableAreas(@PathVariable Long productId) {
        try {
            List<Object> areas = diyService.getProductCustomizableAreas(productId);
            return CommonResult.success(areas);
        } catch (Exception e) {
            return CommonResult.failed("获取可定制面信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取DIY素材分类列表")
    @GetMapping("/materials/categories")
    public CommonResult<List<PmsDiyMaterialCategory>> getDiyMaterialCategories() {
        List<PmsDiyMaterialCategory> categories = diyService.getDiyMaterialCategories();
        return CommonResult.success(categories);
    }

    @Operation(summary = "获取DIY素材列表")
    @GetMapping("/materials")
    public CommonResult<List<PmsDiyMaterial>> getDiyMaterials(
            @Parameter(description = "素材分类ID") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @Parameter(description = "素材类型：1-图片，2-文字") @RequestParam(value = "type", required = false) Integer type) {
        List<PmsDiyMaterial> materials = diyService.getDiyMaterials(categoryId, type);
        return CommonResult.success(materials);
    }

    @Operation(summary = "保存DIY设计")
    @PostMapping("/design/save")
    public CommonResult<Long> saveDiyDesign(@Validated @RequestBody DiyDesignParam designParam) {
        // 获取当前登录用户ID
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }

        // 判断是新建还是更新
        if (designParam.getDesignId() != null) {
            // 更新现有设计
            int updateCount = diyService.updateDiyDesign(designParam.getDesignId(), designParam);
            if (updateCount > 0) {
                return CommonResult.success(designParam.getDesignId());
            }
            return CommonResult.failed("更新失败");
        } else {
            // 新建设计
            Long designId = diyService.saveDiyDesign(designParam, memberId);
            if (designId != null) {
                return CommonResult.success(designId);
            }
            return CommonResult.failed("保存失败");
        }
    }

    @Operation(summary = "更新DIY设计")
    @PostMapping("/design/{designId}/update")
    public CommonResult updateDiyDesign(@PathVariable Long designId, @Validated @RequestBody DiyDesignParam designParam) {
        int count = diyService.updateDiyDesign(designId, designParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("更新失败");
    }

    @Operation(summary = "获取DIY设计详情")
    @GetMapping("/design/{designId}")
    public CommonResult<UmsDiyDesign> getDiyDesign(@PathVariable Long designId) {
        // 获取当前登录用户ID
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }

        UmsDiyDesign design = diyService.getDiyDesign(designId, memberId);
        if (design == null) {
            return CommonResult.failed("设计不存在或无权访问");
        }
        return CommonResult.success(design);
    }

    @Operation(summary = "获取用户的DIY设计列表")
    @GetMapping("/design/list")
    public CommonResult<CommonPage<UmsDiyDesign>> getUserDiyDesigns(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 这里需要从当前登录用户获取memberId
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        List<UmsDiyDesign> designs = diyService.getUserDiyDesigns(memberId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(designs));
    }

    @Operation(summary = "删除DIY设计")
    @PostMapping("/design/{designId}/delete")
    public CommonResult deleteDiyDesign(@PathVariable Long designId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        int count = diyService.deleteDiyDesign(designId, memberId);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("删除失败");
    }

    @Operation(summary = "生成DIY预览图")
    @PostMapping("/preview/generate")
    public CommonResult<DiyPreviewResult> generatePreview(@Validated @RequestBody DiyDesignParam designParam) {
        DiyPreviewResult result = diyService.generatePreview(designParam);
        return CommonResult.success(result);
    }

    @Operation(summary = "AI风格化处理")
    @PostMapping("/ai/stylization")
    public CommonResult<String> aiStylization(@RequestBody Map<String, Object> params) {
        try {
            // 获取当前用户ID
            Long memberId = getCurrentMemberId();
            if (memberId == null) {
                return CommonResult.unauthorized("请先登录");
            }

            String imageUrl = (String) params.get("imageUrl");
            String style = (String) params.get("style");
            String prompt = (String) params.get("prompt");
            String functionType = (String) params.get("functionType");

            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return CommonResult.failed("图片URL不能为空");
            }
            if (style == null || style.trim().isEmpty()) {
                return CommonResult.failed("风格参数不能为空");
            }

            String stylizedImageUrl = diyService.aiStylization(memberId, imageUrl, style, prompt, functionType);
            return CommonResult.success(stylizedImageUrl);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取AI风格化记录")
    @GetMapping("/ai/records")
    public CommonResult<CommonPage<UmsAiStylizationRecord>> getAiStylizationRecords(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        List<UmsAiStylizationRecord> records = diyService.getAiStylizationRecords(memberId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(records));
    }

    @Operation(summary = "检查商品是否支持DIY")
    @GetMapping("/product/{productId}/check")
    public CommonResult<Boolean> checkProductDiyEnabled(@PathVariable Long productId) {
        boolean enabled = diyService.checkProductDiyEnabled(productId);
        return CommonResult.success(enabled);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentMemberId() {
        try {
            return StpMemberUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 如果获取失败，返回null
            return null;
        }
    }
}
