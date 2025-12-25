package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsComment;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dto.PmsCommentParam;
import com.macro.mall.portal.service.PmsCommentService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品评价管理Controller
 * Created by guanghengzhou on 2024/01/13.
 */
@RestController
@Tag(name = "PmsCommentController", description = "商品评价管理")
@RequestMapping("/comment")
public class PmsCommentController {
    
    @Autowired
    private PmsCommentService commentService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "添加商品评价")
    @PostMapping("/create")
    public CommonResult<Integer> createComment(@Valid @RequestBody PmsCommentParam commentParam) {
        try {
            int count = commentService.createComment(commentParam);
            if (count > 0) {
                return CommonResult.success(count, "评价成功");
            } else {
                return CommonResult.failed("评价失败");
            }
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("系统异常，请稍后重试");
        }
    }
    
    @Operation(summary = "根据商品ID获取评价列表")
    @GetMapping("/product/{productId}")
    public CommonResult<CommonPage<PmsComment>> getCommentsByProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        List<PmsComment> comments = commentService.getCommentsByProductId(productId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(comments));
    }
    
    @Operation(summary = "根据会员获取评价列表")
    @GetMapping("/member/{memberNickName}")
    public CommonResult<CommonPage<PmsComment>> getCommentsByMember(
            @Parameter(description = "会员昵称") @PathVariable String memberNickName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        List<PmsComment> comments = commentService.getCommentsByMember(memberNickName, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(comments));
    }
    
    @Operation(summary = "检查是否已评价（根据昵称）")
    @GetMapping("/check")
    public CommonResult<Boolean> checkCommented(
            @Parameter(description = "订单ID") @RequestParam Long orderId,
            @Parameter(description = "商品ID") @RequestParam Long productId,
            @Parameter(description = "会员昵称") @RequestParam String memberNickName) {
        boolean hasCommented = commentService.hasCommented(orderId, productId, memberNickName);
        return CommonResult.success(hasCommented);
    }
    
    @Operation(summary = "检查当前用户是否已评价")
    @GetMapping("/check/current")
    public CommonResult<Boolean> checkCurrentUserCommented(
            @Parameter(description = "订单ID") @RequestParam Long orderId,
            @Parameter(description = "商品ID") @RequestParam Long productId) {
        try {
            // 获取当前登录用户
            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                return CommonResult.failed("用户未登录");
            }
            
            boolean hasCommented = commentService.hasCommentedByMemberId(orderId, productId, currentMember.getId());
            return CommonResult.success(hasCommented);
        } catch (Exception e) {
            return CommonResult.failed("检查评价状态失败");
        }
    }
} 