package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsGiftWish;
import com.macro.mall.service.OmsGiftWishService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "OmsGiftWishController", description = "礼物心愿管理")
@RequestMapping("/giftWish")
public class OmsGiftWishController {

    @Autowired
    private OmsGiftWishService giftWishService;

    @Operation(summary = "添加礼物心愿")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody OmsGiftWish wishItem) {
        OmsGiftWish result = giftWishService.create(wishItem);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新礼物心愿")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody OmsGiftWish wishItem) {
        OmsGiftWish result = giftWishService.update(id, wishItem);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除礼物心愿")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = giftWishService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取礼物心愿详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<OmsGiftWish> getById(@PathVariable Long id) {
        OmsGiftWish result = giftWishService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取礼物心愿列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<OmsGiftWish>> listByFilters(
            @RequestParam(value = "type", required = false) Boolean type,
            @RequestParam(value = "category", required = false) Boolean category,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        List<OmsGiftWish> list = giftWishService.listByFilters(type, category, content, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
} 