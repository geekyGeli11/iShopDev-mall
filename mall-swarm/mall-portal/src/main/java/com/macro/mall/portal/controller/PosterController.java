package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dto.PosterGenerateParam;
import com.macro.mall.portal.service.PosterService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 推广海报生成Controller
 */
@RestController
@RequestMapping("/api/poster")
@Tag(name = "推广海报", description = "推广海报生成和管理功能")
public class PosterController {
    
    @Autowired
    private PosterService posterService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "生成推广海报")
    @PostMapping("/generate")
    public CommonResult<Map<String, Object>> generatePoster(@Valid @RequestBody PosterGenerateParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Map<String, Object> result = posterService.generatePoster(currentMember.getId(), param);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取海报模板列表")
    @GetMapping("/templates")
    public CommonResult<Map<String, Object>> getPosterTemplates() {
        try {
            Map<String, Object> templates = posterService.getPosterTemplates();
            return CommonResult.success(templates);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取我的海报列表")
    @GetMapping("/my-posters")
    public CommonResult<Map<String, Object>> getMyPosters(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Map<String, Object> result = posterService.getMyPosters(currentMember.getId(), pageNum, pageSize);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "删除海报")
    @DeleteMapping("/{posterId}")
    public CommonResult<Boolean> deletePoster(@PathVariable Long posterId) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = posterService.deletePoster(posterId, currentMember.getId());
            return CommonResult.success(result, result ? "删除成功" : "删除失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取海报详情")
    @GetMapping("/{posterId}")
    public CommonResult<Map<String, Object>> getPosterDetail(@PathVariable Long posterId) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Map<String, Object> detail = posterService.getPosterDetail(posterId, currentMember.getId());
            if (detail == null) {
                return CommonResult.failed("海报不存在");
            }
            return CommonResult.success(detail);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "分享海报")
    @PostMapping("/{posterId}/share")
    public CommonResult<Map<String, Object>> sharePoster(@PathVariable Long posterId) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Map<String, Object> shareInfo = posterService.sharePoster(posterId, currentMember.getId());
            return CommonResult.success(shareInfo);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
} 