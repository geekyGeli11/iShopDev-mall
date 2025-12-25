package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 积分服务测试类
 * Created by macro on 2025/09/28.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // 测试后回滚数据
public class UmsIntegrationServiceTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsIntegrationServiceTest.class);
    
    @Autowired
    private UmsIntegrationService integrationService;
    
    @Autowired
    private UmsMemberService memberService;
    
    /**
     * 测试积分更新和等级检查功能
     */
    @Test
    public void testUpdateIntegrationAndCheckLevel() {
        LOGGER.info("开始测试积分更新和等级检查功能");
        
        // 这里需要一个真实的用户ID进行测试
        // 在实际测试中，应该创建测试数据或使用已知的测试用户
        Long testMemberId = 1L;
        
        try {
            // 测试增加积分
            boolean result = integrationService.updateIntegrationAndCheckLevel(
                testMemberId, 100, "测试增加积分", 4);
            
            LOGGER.info("积分增加测试结果: {}", result);
            
            // 测试减少积分
            boolean deductResult = integrationService.updateIntegrationAndCheckLevel(
                testMemberId, -50, "测试减少积分", 4);
            
            LOGGER.info("积分减少测试结果: {}", deductResult);
            
        } catch (Exception e) {
            LOGGER.error("积分更新测试失败", e);
        }
    }
    
    /**
     * 测试等级计算功能
     */
    @Test
    public void testCalculateMemberLevelByIntegration() {
        LOGGER.info("开始测试等级计算功能");
        
        // 测试不同积分对应的等级
        int[] testPoints = {0, 100, 500, 1000, 1500, 3000, 6000, 10000};
        
        for (int points : testPoints) {
            Long levelId = integrationService.calculateMemberLevelByIntegration(points);
            LOGGER.info("积分: {}, 对应等级ID: {}", points, levelId);
        }
    }
    
    /**
     * 测试新用户默认优惠券发放
     */
    @Test
    public void testGrantDefaultLevelCoupon() {
        LOGGER.info("开始测试新用户默认优惠券发放");
        
        Long testMemberId = 1L;
        
        try {
            boolean result = integrationService.grantDefaultLevelCoupon(testMemberId);
            LOGGER.info("默认优惠券发放测试结果: {}", result);
            
        } catch (Exception e) {
            LOGGER.error("默认优惠券发放测试失败", e);
        }
    }
    
    /**
     * 测试等级升级优惠券发放
     */
    @Test
    public void testGrantLevelUpgradeCoupon() {
        LOGGER.info("开始测试等级升级优惠券发放");
        
        Long testMemberId = 1L;
        Long testLevelId = 2L; // 假设等级ID为2
        
        try {
            boolean result = integrationService.grantLevelUpgradeCoupon(testMemberId, testLevelId);
            LOGGER.info("等级升级优惠券发放测试结果: {}", result);
            
        } catch (Exception e) {
            LOGGER.error("等级升级优惠券发放测试失败", e);
        }
    }
    
    /**
     * 测试剩余积分计算功能
     */
    @Test
    public void testCalculateRemainingPointsToNextLevel() {
        LOGGER.info("开始测试剩余积分计算功能");

        Long testMemberId = 1L;

        try {
            Integer remainingPoints = integrationService.calculateRemainingPointsToNextLevel(testMemberId);
            LOGGER.info("用户距离下一等级剩余积分: memberId={}, remainingPoints={}", testMemberId, remainingPoints);

            // 测试不存在的用户
            Integer invalidResult = integrationService.calculateRemainingPointsToNextLevel(99999L);
            assertEquals(Integer.valueOf(-1), invalidResult);
            LOGGER.info("不存在用户测试通过");

        } catch (Exception e) {
            LOGGER.error("剩余积分计算测试失败", e);
        }
    }

    /**
     * 测试完整的用户注册流程
     */
    @Test
    public void testCompleteUserRegistrationFlow() {
        LOGGER.info("开始测试完整的用户注册流程");

        try {
            // 模拟新用户注册
            String testOpenId = "test_openid_" + System.currentTimeMillis();
            String testPhone = "13800000000";
            String testNickname = "测试用户";

            // 注册用户（这会触发默认优惠券发放）
            UmsMember newMember = memberService.registerByOpenIdAndPhone(
                testOpenId, testPhone, testNickname, null, null, null);

            assertNotNull(newMember);
            assertNotNull(newMember.getId());
            LOGGER.info("新用户注册成功: memberId={}, nickname={}", newMember.getId(), newMember.getNickname());

            // 测试积分增加和等级升级
            boolean integrationResult = integrationService.updateIntegrationAndCheckLevel(
                newMember.getId(), 1000, "测试积分增加", 4);

            assertTrue(integrationResult);
            LOGGER.info("新用户积分增加测试成功");

            // 测试剩余积分计算
            Integer remainingPoints = integrationService.calculateRemainingPointsToNextLevel(newMember.getId());
            LOGGER.info("新用户距离下一等级剩余积分: {}", remainingPoints);

        } catch (Exception e) {
            LOGGER.error("完整用户注册流程测试失败", e);
        }
    }
}
