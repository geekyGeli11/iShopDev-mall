package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.model.OmsGiftWish;
import com.macro.mall.portal.domain.GiftOrderDetail;
import com.macro.mall.portal.service.OmsGiftRecordService;
import com.macro.mall.portal.service.OmsGiftService;
import com.macro.mall.portal.service.OmsGiftWishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 送礼管理Controller
 */
@Controller
@Tag(name = "OmsGiftController", description = "用户礼物管理")
@RequestMapping("/member/gift")
public class OmsGiftController {
    
    @Autowired
    private OmsGiftRecordService giftRecordService;
    
    @Autowired
    private OmsGiftWishService giftWishService;
    
    @Autowired
    private OmsGiftService giftService;
    
    @Operation(summary = "接收礼物")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult receiveGift(@RequestParam Long giftRecordId) {
        return giftService.receiveGift(giftRecordId);
    }
    
    @Operation(summary = "更新送礼订单收货地址")
    @RequestMapping(value = "/updateAddress", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateAddress(@RequestParam Long giftRecordId, @RequestParam Long memberReceiveAddressId) {
        return giftService.updateGiftOrderAddress(giftRecordId, memberReceiveAddressId);
    }
    
    @Operation(summary = "获取用户发送的礼物列表")
    @RequestMapping(value = "/sent", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult listSentGifts(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return giftService.listSentGifts(pageNum, pageSize);
    }
    
    @Operation(summary = "获取用户收到的礼物列表")
    @RequestMapping(value = "/received", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult listReceivedGifts(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return giftService.listReceivedGifts(pageNum, pageSize);
    }
    
    @Operation(summary = "获取礼物详情")
    @RequestMapping(value = "/detail/{giftRecordId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getGiftDetail(@PathVariable Long giftRecordId) {
        return giftService.getGiftDetail(giftRecordId);
    }
    
    @Operation(summary = "获取送礼订单详情")
    @RequestMapping(value = "/orderDetail/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<GiftOrderDetail> getGiftOrderDetail(@PathVariable Long orderId) {
        return giftService.getGiftOrderDetail(orderId);
    }
    
    @Operation(summary = "获取礼物心愿列表")
    @RequestMapping(value = "/wishes", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsGiftWish>> listWishes(
            @RequestParam(value = "type", required = false) Boolean type,
            @RequestParam(value = "category", required = false) Boolean category,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 前台只查询状态为启用的心愿列表
        Boolean status = true;
        List<OmsGiftWish> wishList = giftWishService.listWishes(type, category, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(wishList));
    }
} 