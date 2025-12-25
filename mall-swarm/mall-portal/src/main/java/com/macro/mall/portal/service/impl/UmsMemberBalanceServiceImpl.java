package com.macro.mall.portal.service.impl;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.macro.mall.mapper.UmsMemberBalanceHistoryMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsMemberRechargeOrderMapper;
import com.macro.mall.mapper.UmsBalanceConfigMapper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.UmsMemberBalanceDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.UmsMemberBalanceService;
import com.macro.mall.portal.service.WxPayBusiness;
import com.macro.mall.portal.service.UmsIntegrationService;
import com.macro.mall.portal.util.StpMemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户余额管理Service实现类
 */
@Service
public class UmsMemberBalanceServiceImpl implements UmsMemberBalanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberBalanceServiceImpl.class);

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberBalanceHistoryMapper balanceHistoryMapper;

    @Autowired
    private UmsMemberRechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;

    @Autowired
    private UmsBalanceConfigMapper balanceConfigMapper;

    @Autowired
    private UmsMemberBalanceDao memberBalanceDao;

    @Autowired
    private WxPayBusiness wxPayBusiness;

    @Autowired
    private UmsIntegrationService integrationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MemberBalanceInfo getBalanceInfo(Long memberId) {
        try {
            // 获取用户基本信息
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                throw new RuntimeException("用户不存在");
            }

            // 构建余额信息
            MemberBalanceInfo balanceInfo = new MemberBalanceInfo();
            balanceInfo.setMemberId(memberId);
            
            // 优先使用nickname，如果为空则使用username
            String displayName = (member.getNickname() != null && !member.getNickname().trim().isEmpty()) 
                ? member.getNickname() 
                : member.getUsername();
            balanceInfo.setUsername(displayName);
            balanceInfo.setBalance(member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO);

            // 获取统计信息
            Map<String, BigDecimal> statistics = memberBalanceDao.getBalanceStatistics(memberId);
            if (statistics != null) {
                balanceInfo.setTotalRechargeAmount(statistics.get("totalRechargeAmount"));
                balanceInfo.setTotalConsumeAmount(statistics.get("totalConsumeAmount"));
                balanceInfo.setTodayRechargeAmount(statistics.get("todayRechargeAmount"));
                balanceInfo.setTodayConsumeAmount(statistics.get("todayConsumeAmount"));
            }

            // 检查配置
            balanceInfo.setCanRecharge(getConfigBoolean("balance.enable_recharge", true));
            balanceInfo.setCanConsume(getConfigBoolean("balance.enable_consume", true));

            return balanceInfo;
        } catch (Exception e) {
            LOGGER.error("获取用户余额信息失败, memberId: {}", memberId, e);
            throw new RuntimeException("获取余额信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RechargeOrderResult createRechargeOrder(Long memberId, RechargeOrderParam param) {
        try {
            // 获取用户信息
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                throw new RuntimeException("用户不存在");
            }

            // 验证充值金额
            validateRechargeAmount(param.getAmount());

            // 生成订单号
            String orderSn = generateOrderSn();

            // 创建充值订单
            UmsMemberRechargeOrder rechargeOrder = new UmsMemberRechargeOrder();
            rechargeOrder.setOrderSn(orderSn);
            rechargeOrder.setMemberId(memberId);
            
            // 使用nickname字段，如果为空则使用username作为后备
            String memberName = (member.getNickname() != null && !member.getNickname().trim().isEmpty()) 
                ? member.getNickname() 
                : member.getUsername();
            rechargeOrder.setMemberUsername(memberName);
            
            rechargeOrder.setAmount(param.getAmount());
            rechargeOrder.setPayType(param.getPayType().byteValue());
            rechargeOrder.setStatus((byte) 0); // 待支付
            rechargeOrder.setNote(param.getNote());
            rechargeOrder.setCreateTime(new Date());
            rechargeOrder.setUpdateTime(new Date());

            int result = rechargeOrderMapper.insertSelective(rechargeOrder);
            if (result <= 0) {
                throw new RuntimeException("创建充值订单失败");
            }

            // 构建返回结果
            RechargeOrderResult orderResult = new RechargeOrderResult();
            BeanUtils.copyProperties(rechargeOrder, orderResult);
            orderResult.setOrderId(rechargeOrder.getId());

            // 根据支付方式生成支付参数（目前只支持微信支付）
            if (param.getPayType() == 2) { // 微信支付
                try {
                    WxPayMpOrderResult payParams = generateWxPayParams(rechargeOrder, member);
                    orderResult.setPayParams(payParams);
                } catch (Exception e) {
                    LOGGER.error("生成微信支付参数失败, orderSn: {}", orderSn, e);
                    throw new RuntimeException("生成支付参数失败: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("目前只支持微信支付");
            }

            return orderResult;
        } catch (Exception e) {
            LOGGER.error("创建充值订单失败, memberId: {}, param: {}", memberId, param, e);
            throw new RuntimeException("创建充值订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRechargePaySuccess(String orderSn, Integer payType, String paySn) {
        try {
            // 查询充值订单
            UmsMemberRechargeOrderExample example = new UmsMemberRechargeOrderExample();
            example.createCriteria().andOrderSnEqualTo(orderSn);
            List<UmsMemberRechargeOrder> orders = rechargeOrderMapper.selectByExample(example);

            if (orders.isEmpty()) {
                throw new RuntimeException("充值订单不存在");
            }

            UmsMemberRechargeOrder order = orders.get(0);
            if (order.getStatus() != 0) {
                LOGGER.warn("充值订单状态不正确, orderSn: {}, status: {}", orderSn, order.getStatus());
                return order.getStatus() == 1; // 如果已经是支付成功状态，返回true
            }

            // 更新订单状态
            order.setStatus((byte) 1); // 支付成功
            order.setPayTime(new Date());
            order.setPaySn(paySn);
            order.setUpdateTime(new Date());
            rechargeOrderMapper.updateByPrimaryKeySelective(order);

            // 处理充值金额和赠送奖励
            boolean result = processRechargeRewards(order.getMemberId(), order.getAmount(), orderSn);
            
            if (!result) {
                throw new RuntimeException("处理充值奖励失败");
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("处理充值支付成功失败, orderSn: {}", orderSn, e);
            throw new RuntimeException("处理充值支付成功失败: " + e.getMessage());
        }
    }

    @Override
    public RechargeConfigResult getRechargeConfig() {
        try {
            RechargeConfigResult config = new RechargeConfigResult();
            
            // 获取充值金额配置
            config.setMinAmount(new BigDecimal(getConfigValue("recharge.min_amount", "1.00")));
            config.setMaxAmount(new BigDecimal(getConfigValue("recharge.max_amount", "50000.00")));
            
            // 解析快速充值选项（JSON格式）
            String quickAmountsJson = getConfigValue("recharge.quick_amounts", 
                "[{\"amount\":100,\"bonusBalance\":0.00,\"bonusIntegration\":0,\"description\":\"充值100元无赠送\"}]");
            
            List<RechargeConfigResult.QuickAmountOption> quickAmountOptions = parseQuickAmounts(quickAmountsJson);
            config.setQuickAmounts(quickAmountOptions);
            
            // 获取赠送相关配置
            config.setEnableBonus(getConfigBoolean("recharge.enable_bonus", true));
            config.setBonusBalanceRate(new BigDecimal(getConfigValue("recharge.bonus_balance_rate", "0.00")));
            config.setBonusIntegrationRate(new BigDecimal(getConfigValue("recharge.bonus_integration_rate", "0.00")));
            config.setBonusMinAmount(new BigDecimal(getConfigValue("recharge.bonus_min_amount", "50.00")));
            
            return config;
        } catch (Exception e) {
            LOGGER.error("获取充值配置失败", e);
            throw new RuntimeException("获取充值配置失败: " + e.getMessage());
        }
    }

    /**
     * 解析快速充值选项JSON配置
     */
    private List<RechargeConfigResult.QuickAmountOption> parseQuickAmounts(String quickAmountsJson) {
        try {
            // 先尝试解析JSON格式
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            List<Map<String, Object>> jsonList = objectMapper.readValue(quickAmountsJson, typeRef);
            
            return jsonList.stream().map(map -> {
                RechargeConfigResult.QuickAmountOption option = new RechargeConfigResult.QuickAmountOption();
                option.setAmount(new BigDecimal(map.get("amount").toString()));
                option.setBonusBalance(new BigDecimal(map.get("bonusBalance").toString()));
                option.setBonusIntegration(Integer.valueOf(map.get("bonusIntegration").toString()));
                option.setDescription(map.get("description").toString());
                return option;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            LOGGER.warn("解析快速充值选项JSON失败，尝试兼容旧格式: {}", quickAmountsJson, e);
            
            // 兼容旧的逗号分隔格式
            String[] amounts = quickAmountsJson.split(",");
            return Arrays.stream(amounts).map(amountStr -> {
                RechargeConfigResult.QuickAmountOption option = new RechargeConfigResult.QuickAmountOption();
                BigDecimal amount = new BigDecimal(amountStr.trim());
                option.setAmount(amount);
                option.setBonusBalance(BigDecimal.ZERO);
                option.setBonusIntegration(0);
                option.setDescription("充值" + amount + "元无赠送");
                return option;
            }).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean consumeBalance(Long memberId, BigDecimal amount, String businessType, String businessId, String remark) {
        try {
            // 检查余额是否足够
            if (!checkBalanceEnough(memberId, amount)) {
                throw new RuntimeException("余额不足");
            }

            // 扣减余额
            return deductBalance(memberId, amount, businessType, businessId, remark);
        } catch (Exception e) {
            LOGGER.error("余额消费失败, memberId: {}, amount: {}", memberId, amount, e);
            throw new RuntimeException("余额消费失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundBalance(Long memberId, BigDecimal amount, String businessType, String businessId, String remark) {
        try {
            // 增加余额
            return addBalance(memberId, amount, businessType, businessId, remark);
        } catch (Exception e) {
            LOGGER.error("余额退款失败, memberId: {}, amount: {}", memberId, amount, e);
            throw new RuntimeException("余额退款失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordBalanceChange(Long memberId, Integer changeType, BigDecimal amount, 
                                     String businessType, String businessId, String remark) {
        try {
            // 获取用户当前余额
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                throw new RuntimeException("用户不存在");
            }

            BigDecimal balanceBefore = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            BigDecimal balanceAfter;
            
            // 计算变动后余额
            if (changeType == 1 || changeType == 3 || changeType == 4) {
                // 充值、退款、转入 - 增加余额
                balanceAfter = balanceBefore.add(amount);
            } else {
                // 消费、转出 - 减少余额
                balanceAfter = balanceBefore.subtract(amount);
                if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("余额不足");
                }
            }

            // 更新用户余额
            member.setBalance(balanceAfter);
            memberMapper.updateByPrimaryKeySelective(member);

            // 记录余额变动历史
            UmsMemberBalanceHistory history = new UmsMemberBalanceHistory();
            history.setMemberId(memberId);
            history.setChangeType(changeType.byteValue());
            history.setAmount(changeType == 2 || changeType == 5 ? amount.negate() : amount);
            history.setBalanceBefore(balanceBefore);
            history.setBalanceAfter(balanceAfter);
            history.setBusinessType(businessType);
            history.setBusinessId(businessId);
            history.setRemark(remark);
            history.setOperator("system");
            history.setCreateTime(new Date());
            history.setUpdateTime(new Date());

            balanceHistoryMapper.insertSelective(history);

            return true;
        } catch (Exception e) {
            LOGGER.error("记录余额变动失败, memberId: {}, changeType: {}, amount: {}", 
                memberId, changeType, amount, e);
            throw new RuntimeException("记录余额变动失败: " + e.getMessage());
        }
    }

    @Override
    public boolean checkBalanceEnough(Long memberId, BigDecimal amount) {
        try {
            LOGGER.info("检查余额是否足够：memberId={}, 需要金额={}", memberId, amount);

            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                LOGGER.warn("用户不存在：memberId={}", memberId);
                return false;
            }

            BigDecimal balance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            boolean isEnough = balance.compareTo(amount) >= 0;

            LOGGER.info("余额检查结果：memberId={}, 当前余额={}, 需要金额={}, 是否足够={}",
                memberId, balance, amount, isEnough);

            return isEnough;
        } catch (Exception e) {
            LOGGER.error("检查余额是否足够失败, memberId: {}, amount: {}", memberId, amount, e);
            return false;
        }
    }

    /**
     * 增加余额
     */
    private boolean addBalance(Long memberId, BigDecimal amount, String businessType, 
                              String businessId, String remark) {
        return recordBalanceChange(memberId, 1, amount, businessType, businessId, remark);
    }

    /**
     * 扣减余额
     */
    private boolean deductBalance(Long memberId, BigDecimal amount, String businessType, 
                                 String businessId, String remark) {
        return recordBalanceChange(memberId, 2, amount, businessType, businessId, remark);
    }

    /**
     * 验证充值金额
     */
    private void validateRechargeAmount(BigDecimal amount) {
        BigDecimal minAmount = new BigDecimal(getConfigValue("recharge.min_amount", "1.00"));
        BigDecimal maxAmount = new BigDecimal(getConfigValue("recharge.max_amount", "50000.00"));

        if (amount.compareTo(minAmount) < 0) {
            throw new RuntimeException("充值金额不能小于" + minAmount);
        }

        if (amount.compareTo(maxAmount) > 0) {
            throw new RuntimeException("充值金额不能大于" + maxAmount);
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderSn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        String random = String.valueOf((int) (Math.random() * 9000) + 1000);
        return "RC" + timestamp + random;
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        try {
            UmsBalanceConfigExample example = new UmsBalanceConfigExample();
            example.createCriteria().andConfigKeyEqualTo(key).andStatusEqualTo((byte) 1);
            List<UmsBalanceConfig> configs = balanceConfigMapper.selectByExample(example);
            
            if (!configs.isEmpty()) {
                return configs.get(0).getConfigValue();
            }
        } catch (Exception e) {
            LOGGER.warn("获取配置失败, key: {}", key, e);
        }
        return defaultValue;
    }

    /**
     * 获取布尔配置值
     */
    private boolean getConfigBoolean(String key, boolean defaultValue) {
        String value = getConfigValue(key, String.valueOf(defaultValue));
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    /**
     * 处理充值奖励（包括原始充值金额、赠送余额和赠送积分）
     */
    private boolean processRechargeRewards(Long memberId, BigDecimal rechargeAmount, String orderSn) {
        try {
            // 1. 增加原始充值金额
            boolean rechargeResult = addBalance(memberId, rechargeAmount, "recharge", orderSn, 
                "充值" + rechargeAmount + "元");
            if (!rechargeResult) {
                throw new RuntimeException("增加充值余额失败");
            }

            // 2. 获取充值配置，查找是否有赠送奖励
            RechargeConfigResult.QuickAmountOption bonusOption = findBonusOption(rechargeAmount);
            
            if (bonusOption != null) {
                // 3. 处理赠送余额
                if (bonusOption.getBonusBalance() != null && bonusOption.getBonusBalance().compareTo(BigDecimal.ZERO) > 0) {
                    boolean bonusBalanceResult = addBalance(memberId, bonusOption.getBonusBalance(), 
                        "recharge_bonus", orderSn, 
                        "充值赠送余额" + bonusOption.getBonusBalance() + "元");
                    if (!bonusBalanceResult) {
                        throw new RuntimeException("增加赠送余额失败");
                    }
                }

                // 4. 处理赠送积分
                if (bonusOption.getBonusIntegration() != null && bonusOption.getBonusIntegration() > 0) {
                    boolean bonusIntegrationResult = addIntegration(memberId, bonusOption.getBonusIntegration(), 
                        orderSn, "充值赠送积分" + bonusOption.getBonusIntegration() + "分");
                    if (!bonusIntegrationResult) {
                        throw new RuntimeException("增加赠送积分失败");
                    }
                }
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("处理充值奖励失败, memberId: {}, rechargeAmount: {}, orderSn: {}", 
                memberId, rechargeAmount, orderSn, e);
            throw new RuntimeException("处理充值奖励失败: " + e.getMessage());
        }
    }

    /**
     * 根据充值金额查找对应的赠送配置
     */
    private RechargeConfigResult.QuickAmountOption findBonusOption(BigDecimal rechargeAmount) {
        try {
            RechargeConfigResult config = getRechargeConfig();
            if (config.getQuickAmounts() != null) {
                for (RechargeConfigResult.QuickAmountOption option : config.getQuickAmounts()) {
                    if (option.getAmount().compareTo(rechargeAmount) == 0) {
                        return option;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.warn("查找赠送配置失败, rechargeAmount: {}", rechargeAmount, e);
            return null;
        }
    }

    /**
     * 增加用户积分
     */
    private boolean addIntegration(Long memberId, Integer integration, String businessId, String remark) {
        try {
            // 使用统一积分服务更新积分并检查等级升级
            boolean result = integrationService.updateIntegrationAndCheckLevel(
                memberId,
                integration,
                remark,
                4 // 4->其他（充值赠送）
            );

            if (result) {
                LOGGER.info("充值赠送积分成功, memberId: {}, integration: {}, remark: {}",
                    memberId, integration, remark);
            } else {
                LOGGER.error("充值赠送积分失败, memberId: {}, integration: {}", memberId, integration);
            }

            return result;
        } catch (Exception e) {
            LOGGER.error("增加用户积分失败, memberId: {}, integration: {}", memberId, integration, e);
            return false;
        }
    }

    /**
     * 生成微信支付参数
     */
    private WxPayMpOrderResult generateWxPayParams(UmsMemberRechargeOrder rechargeOrder, UmsMember member) throws Exception {
        // 检查用户openid
        if (member.getOpenid() == null || member.getOpenid().trim().isEmpty()) {
            throw new RuntimeException("用户未绑定微信openid，无法使用微信支付");
        }
        
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setBody("余额充值");
        request.setOutTradeNo(rechargeOrder.getOrderSn());
        request.setTotalFee(rechargeOrder.getAmount().multiply(new BigDecimal(100)).intValue()); // 转换为分
        request.setSpbillCreateIp("127.0.0.1"); // 这里应该获取真实IP
        request.setNotifyUrl("http://localhost:8201/mall-portal/wxpay/notify"); // 支付回调地址
        request.setTradeType("JSAPI"); // 小程序支付
        request.setOpenid(member.getOpenid()); // 设置用户openid
        
        LOGGER.info("创建微信支付订单，订单号：{}，金额：{}元，用户openid：{}", 
            rechargeOrder.getOrderSn(), rechargeOrder.getAmount(), member.getOpenid());
        
        return wxPayBusiness.createOrder(request);
    }

    @Override
    public List<UmsMemberBalanceHistory> getBalanceHistory(Long memberId, Integer changeType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        
        UmsMemberBalanceHistoryExample example = new UmsMemberBalanceHistoryExample();
        UmsMemberBalanceHistoryExample.Criteria criteria = example.createCriteria();
        
        criteria.andMemberIdEqualTo(memberId);
        
        if (changeType != null) {
            criteria.andChangeTypeEqualTo(changeType.byteValue());
        }
        
        // 按创建时间倒序排列
        example.setOrderByClause("create_time DESC");
        
        return balanceHistoryMapper.selectByExample(example);
    }
} 