package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsMemberDetailVO;
import com.macro.mall.dto.UmsMemberListVO;
import com.macro.mall.dto.UmsMemberQueryParam;
import com.macro.mall.dto.UmsMemberUpdateParam;
import com.macro.mall.dto.SmsCouponHistoryDetailVO;
import com.macro.mall.service.UmsMemberAdminService;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.PmsInviteRelation;
import com.macro.mall.model.PmsInviteReward;
import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.model.UmsMemberTag;
import com.macro.mall.model.UmsMemberSigninLog;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.mapper.OmsSchoolMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
 */
@RestController
@Tag(name = "UmsMemberController", description = "用户管理")
@RequestMapping("/member")
public class UmsMemberController {

    @Autowired
    private UmsMemberAdminService memberAdminService;

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsMemberListVO>> list(
        UmsMemberQueryParam queryParam,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<UmsMemberListVO> memberList = memberAdminService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(memberList));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/detail/{id}")
    public CommonResult<UmsMemberDetailVO> getDetail(@PathVariable Long id) {
        UmsMemberDetailVO memberDetail = memberAdminService.getDetail(id);
        return CommonResult.success(memberDetail);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update/{id}")
    public CommonResult<Integer> updateMember(
        @PathVariable Long id, 
        @RequestBody UmsMemberUpdateParam updateParam
    ) {
        int count = memberAdminService.updateMember(id, updateParam);
        if (count > 0) {
            return CommonResult.success(count, "用户信息更新成功");
        }
        return CommonResult.failed("用户信息更新失败");
    }

    @Operation(summary = "更新用户状态")
    @PostMapping("/updateStatus")
    public CommonResult<Integer> updateStatus(
        @RequestParam Long id,
        @RequestParam Integer status
    ) {
        int count = memberAdminService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count, "用户状态更新成功");
        }
        return CommonResult.failed("用户状态更新失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Integer> deleteMember(@PathVariable Long id) {
        int count = memberAdminService.deleteMember(id);
        if (count > 0) {
            return CommonResult.success(count, "用户删除成功");
        }
        return CommonResult.failed("用户删除失败");
    }

    // =============== 积分管理API ===============
    
    @Operation(summary = "发放积分给用户")
    @PostMapping("/integration/give")
    public CommonResult<Integer> giveIntegration(
        @RequestParam Long memberId,
        @RequestParam Integer integration,
        @RequestParam(required = false, defaultValue = "管理员发放积分") String reason
    ) {
        int count = memberAdminService.giveIntegration(memberId, integration, reason);
        if (count > 0) {
            return CommonResult.success(count, "积分发放成功");
        }
        return CommonResult.failed("积分发放失败");
    }
    
    @Operation(summary = "扣减用户积分")
    @PostMapping("/integration/deduct")
    public CommonResult<Integer> deductIntegration(
        @RequestParam Long memberId,
        @RequestParam Integer integration,
        @RequestParam(required = false, defaultValue = "管理员扣减积分") String reason
    ) {
        int count = memberAdminService.deductIntegration(memberId, integration, reason);
        if (count > 0) {
            return CommonResult.success(count, "积分扣减成功");
        }
        return CommonResult.failed("积分扣减失败，可能是积分不足");
    }
    
    @Operation(summary = "获取用户积分变更历史")
    @GetMapping("/integration/history/{id}")
    public CommonResult<CommonPage<UmsIntegrationChangeHistory>> getIntegrationHistory(
        @PathVariable Long id,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<UmsIntegrationChangeHistory> historyList = memberAdminService.getIntegrationHistory(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
    
    @Operation(summary = "批量调整用户积分")
    @PostMapping("/integration/batch")
    public CommonResult<Integer> batchAdjustIntegration(
        @RequestParam List<Long> memberIds,
        @RequestParam Integer integration,
        @RequestParam Integer operationType,
        @RequestParam(required = false, defaultValue = "管理员批量调整积分") String reason
    ) {
        int count = memberAdminService.batchAdjustIntegration(memberIds, integration, operationType, reason);
        return CommonResult.success(count, "批量积分调整成功");
    }
    
    @Operation(summary = "获取用户积分汇总信息")
    @GetMapping("/integration/summary/{id}")
    public CommonResult<Map<String, Object>> getIntegrationSummary(@PathVariable Long id) {
        Map<String, Object> summary = memberAdminService.getIntegrationSummary(id);
        return CommonResult.success(summary);
    }
    
    @Operation(summary = "获取用户优惠券使用历史")
    @GetMapping("/coupon/history/{id}")
    public CommonResult<CommonPage<SmsCouponHistoryDetailVO>> getCouponHistory(
        @PathVariable Long id,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<SmsCouponHistoryDetailVO> couponHistoryList = memberAdminService.getCouponHistoryDetail(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(couponHistoryList));
    }
    
    @Operation(summary = "发放优惠券给用户")
    @PostMapping("/coupon/send")
    public CommonResult<Integer> sendCoupon(
        @RequestParam Long memberId,
        @RequestParam Long couponId,
        @RequestParam(required = false, defaultValue = "管理员发放优惠券") String reason
    ) {
        try {
            // 先检查用户是否存在
            UmsMemberDetailVO member = memberAdminService.getDetail(memberId);
            if (member == null) {
                return CommonResult.failed("用户不存在");
            }
            
            // 获取优惠券信息进行验证
            List<SmsCoupon> availableCoupons = memberAdminService.getAvailableCoupons(memberId, 1000, 1);
            SmsCoupon targetCoupon = null;
            for (SmsCoupon coupon : availableCoupons) {
                if (coupon.getId().equals(couponId)) {
                    targetCoupon = coupon;
                    break;
                }
            }
            
            if (targetCoupon == null) {
                return CommonResult.failed("该优惠券不可发放（可能已过期、库存不足或用户已达领取上限）");
            }
            
            int count = memberAdminService.sendCouponToMember(memberId, couponId, reason);
            if (count > 0) {
                return CommonResult.success(count, "优惠券发放成功");
            }
            return CommonResult.failed("优惠券发放失败，用户可能已达到该优惠券的领取上限");
        } catch (Exception e) {
            return CommonResult.failed("优惠券发放失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取可发放的优惠券列表")
    @GetMapping("/coupon/available")
    public CommonResult<CommonPage<SmsCoupon>> getAvailableCoupons(
        @RequestParam(value = "memberId", required = false) Long memberId,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<SmsCoupon> couponList = memberAdminService.getAvailableCoupons(memberId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(couponList));
    }
    
    @Operation(summary = "批量发放优惠券")
    @PostMapping("/coupon/batch")
    public CommonResult<Integer> batchSendCoupon(
        @RequestParam List<Long> memberIds,
        @RequestParam Long couponId,
        @RequestParam(required = false, defaultValue = "管理员批量发放优惠券") String reason
    ) {
        int count = memberAdminService.batchSendCoupon(memberIds, couponId, reason);
        return CommonResult.success(count, "批量优惠券发放成功");
    }
    
    @Operation(summary = "获取用户订单列表")
    @GetMapping("/orders/{id}")
    public CommonResult<CommonPage<OmsOrder>> getMemberOrders(
        @PathVariable Long id,
        @RequestParam(value = "orderType", required = false) Integer orderType, // 0:正常订单, 1:秒杀订单, 2:送礼订单
        @RequestParam(value = "status", required = false) Integer status, // 0:待付款, 1:待发货, 2:已发货, 3:已完成, 4:已关闭
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<OmsOrder> orderList = memberAdminService.getMemberOrders(id, orderType, status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }
    
    @Operation(summary = "获取用户订单统计")
    @GetMapping("/orders/statistics/{id}")
    public CommonResult<Map<String, Object>> getMemberOrderStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = memberAdminService.getMemberOrderStatistics(id);
        return CommonResult.success(statistics);
    }
    
    // =============== 邀请数据管理API ===============
    
    @Operation(summary = "获取用户邀请汇总信息")
    @GetMapping("/invite/summary/{id}")
    public CommonResult<Map<String, Object>> getInviteSummary(@PathVariable Long id) {
        Map<String, Object> summary = memberAdminService.getInviteSummary(id);
        return CommonResult.success(summary);
    }

    @Operation(summary = "获取用户邀请关系列表")
    @GetMapping("/invite/relations/{id}")
    public CommonResult<CommonPage<com.macro.mall.dto.PmsInviteRelationDetailVO>> getInviteRelations(
        @PathVariable Long id,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<com.macro.mall.dto.PmsInviteRelationDetailVO> relationList = memberAdminService.getInviteRelations(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(relationList));
    }

    @Operation(summary = "获取用户邀请奖励记录")
    @GetMapping("/invite/rewards/{id}")
    public CommonResult<CommonPage<com.macro.mall.dto.PmsInviteRewardDetailVO>> getInviteRewards(
        @PathVariable Long id,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<com.macro.mall.dto.PmsInviteRewardDetailVO> rewardList = memberAdminService.getInviteRewards(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(rewardList));
    }

    @Operation(summary = "获取用户提现申请记录")
    @GetMapping("/invite/withdraws/{id}")
    public CommonResult<CommonPage<PmsInviteWithdrawApply>> getWithdrawApplies(
        @PathVariable Long id,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<PmsInviteWithdrawApply> withdrawList = memberAdminService.getWithdrawApplies(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(withdrawList));
    }

    // =============== 用户画像管理API ===============
    
    @Operation(summary = "获取用户画像信息")
    @GetMapping("/profile/{id}")
    public CommonResult<Map<String, Object>> getMemberProfile(@PathVariable Long id) {
        Map<String, Object> profile = memberAdminService.getMemberProfile(id);
        return CommonResult.success(profile);
    }
    
    @Operation(summary = "重新生成用户画像")
    @PostMapping("/profile/regenerate/{id}")
    public CommonResult<Map<String, Object>> generateMemberProfile(@PathVariable Long id) {
        Map<String, Object> profile = memberAdminService.generateMemberProfile(id);
        return CommonResult.success(profile, "用户画像重新生成成功");
    }
    
    @Operation(summary = "获取用户标签")
    @GetMapping("/tags/{id}")
    public CommonResult<List<UmsMemberTag>> getMemberTags(@PathVariable Long id) {
        List<UmsMemberTag> tags = memberAdminService.getMemberTags(id);
        return CommonResult.success(tags);
    }
    
    @Operation(summary = "更新用户标签")
    @PostMapping("/tags/update/{id}")
    public CommonResult<Integer> updateMemberTags(
        @PathVariable Long id,
        @RequestParam List<Long> tagIds
    ) {
        int count = memberAdminService.updateMemberTags(id, tagIds);
        return CommonResult.success(count, "用户标签更新成功");
    }
    
    // =============== 标签管理API ===============
    
    @Operation(summary = "获取所有标签列表")
    @GetMapping("/tags/list")
    public CommonResult<List<UmsMemberTag>> getAllTags() {
        List<UmsMemberTag> tagList = memberAdminService.getAllTags();
        return CommonResult.success(tagList);
    }
    
    @Operation(summary = "创建新标签")
    @PostMapping("/tags/create")
    public CommonResult<Integer> createTag(
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false, defaultValue = "false") Boolean tagType,
        @RequestParam(required = false) String color
    ) {
        int count = memberAdminService.createTag(name, description, tagType, color);
        if (count > 0) {
            return CommonResult.success(count, "标签创建成功");
        }
        return CommonResult.failed("标签创建失败");
    }
    
    @Operation(summary = "更新标签")
    @PutMapping("/tags/update/{tagId}")
    public CommonResult<Integer> updateTag(
        @PathVariable Long tagId,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String color
    ) {
        int count = memberAdminService.updateTag(tagId, name, description, color);
        if (count > 0) {
            return CommonResult.success(count, "标签更新成功");
        }
        return CommonResult.failed("标签更新失败");
    }
    
    @Operation(summary = "更新标签状态")
    @PutMapping("/tags/status/{tagId}")
    public CommonResult<Integer> updateTagStatus(
        @PathVariable Long tagId,
        @RequestParam Boolean status
    ) {
        int count = memberAdminService.updateTagStatus(tagId, status);
        if (count > 0) {
            return CommonResult.success(count, "标签状态更新成功");
        }
        return CommonResult.failed("标签状态更新失败");
    }
    
    @Operation(summary = "删除标签")
    @DeleteMapping("/tags/delete/{tagId}")
    public CommonResult<Integer> deleteTag(@PathVariable Long tagId) {
        int count = memberAdminService.deleteTag(tagId);
        if (count > 0) {
            return CommonResult.success(count, "标签删除成功");
        }
        return CommonResult.failed("标签删除失败");
    }
    
    @Operation(summary = "给用户绑定标签")
    @PostMapping("/tags/bind/{memberId}")
    public CommonResult<Integer> bindMemberTag(
        @PathVariable Long memberId,
        @RequestParam Long tagId
    ) {
        int count = memberAdminService.bindMemberTag(memberId, tagId);
        if (count > 0) {
            return CommonResult.success(count, "标签绑定成功");
        }
        return CommonResult.failed("标签绑定失败");
    }
    
    @Operation(summary = "解除用户标签绑定")
    @PostMapping("/tags/unbind/{memberId}")
    public CommonResult<Integer> unbindMemberTag(
        @PathVariable Long memberId,
        @RequestParam Long tagId
    ) {
        int count = memberAdminService.unbindMemberTag(memberId, tagId);
        if (count > 0) {
            return CommonResult.success(count, "标签解绑成功");
        }
        return CommonResult.failed("标签解绑失败");
    }
    
    @Operation(summary = "批量给用户打标签")
    @PostMapping("/tags/batch/bind")
    public CommonResult<Integer> batchBindTags(
        @RequestParam List<Long> memberIds,
        @RequestParam List<Long> tagIds
    ) {
        int count = memberAdminService.batchBindTags(memberIds, tagIds);
        if (count > 0) {
            return CommonResult.success(count, "批量标签绑定成功");
        }
        return CommonResult.failed("批量标签绑定失败");
    }
    
    @Operation(summary = "导出用户数据")
    @GetMapping("/export")
    public CommonResult<List<UmsMemberListVO>> exportMemberData(UmsMemberQueryParam queryParam) {
        List<UmsMemberListVO> memberList = memberAdminService.exportMemberData(queryParam);
        return CommonResult.success(memberList, "用户数据导出成功");
    }
    
    // =============== 签到记录管理API ===============
    
    @Operation(summary = "获取用户签到记录列表")
    @GetMapping("/signin/history")
    public CommonResult<CommonPage<UmsMemberSigninLog>> getSignInHistory(
        @RequestParam Long memberId,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<UmsMemberSigninLog> signInList = memberAdminService.getSignInHistory(
            memberId, startDate, endDate, status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(signInList));
    }
    
    @Operation(summary = "获取用户签到统计信息")
    @GetMapping("/signin/summary/{memberId}")
    public CommonResult<Map<String, Object>> getSignInSummary(@PathVariable Long memberId) {
        Map<String, Object> summary = memberAdminService.getSignInSummary(memberId);
        return CommonResult.success(summary);
    }
    
    @Operation(summary = "管理员为用户补签")
    @PostMapping("/signin/supplement")
    public CommonResult<Integer> supplementSignIn(
        @RequestParam Long memberId,
        @RequestParam String signinDate,
        @RequestParam(required = false, defaultValue = "管理员补签") String reason
    ) {
        int count = memberAdminService.supplementSignIn(memberId, signinDate, reason);
        if (count > 0) {
            return CommonResult.success(count, "补签成功");
        }
        return CommonResult.failed("补签失败，该日期可能已经签到过");
    }

    @Operation(summary = "获取学校列表")
    @GetMapping("/schools")
    public CommonResult<List<OmsSchool>> getSchoolList() {
        OmsSchoolExample example = new OmsSchoolExample();
        example.createCriteria().andStatusEqualTo(true);
        example.setOrderByClause("school_name ASC");
        List<OmsSchool> schoolList = schoolMapper.selectByExample(example);
        return CommonResult.success(schoolList);
    }
}