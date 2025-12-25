package com.macro.mall.portal.component;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.WxPayBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 订单支付状态检查定时任务
 * 定期查询未支付订单的支付状态
 */
// @Component
public class OrderPayStatusCheckTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPayStatusCheckTask.class);
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private WxPayBusiness wxPayBusiness;
    
    @Autowired
    private OmsPortalOrderService portalOrderService;
    
    /**
     * 每1分钟查询一次未支付订单的支付状态
     * 仅查询创建时间在15分钟内的订单，避免过度查询
     */
    @Scheduled(cron = "0 */1 * * * ?")
    private void checkOrderPayStatus() {
        LOGGER.info("开始执行订单支付状态检查任务");
        
        // 查询所有待支付的订单（状态为0），且创建时间在15分钟内
        OmsOrderExample example = new OmsOrderExample();
        Date fifteenMinutesAgo = new Date(System.currentTimeMillis() - 15 * 60 * 1000);
        example.createCriteria()
                .andStatusEqualTo(0)
                .andDeleteStatusEqualTo(0)
                .andCreateTimeGreaterThan(fifteenMinutesAgo);
        
        List<OmsOrder> orderList = orderMapper.selectByExample(example);
        LOGGER.info("待检查支付状态的订单数量: {}", orderList.size());
        
        // 检查每个订单的支付状态
        for (OmsOrder order : orderList) {
            try {
                // 通过微信支付接口查询订单支付状态
                WxPayOrderQueryResult queryResult = wxPayBusiness.queryOrder(null, order.getOrderSn());
                LOGGER.info("订单{}查询结果: {}", order.getOrderSn(), queryResult.getTradeState());
                
                // 判断支付状态
                if ("SUCCESS".equals(queryResult.getResultCode()) && "SUCCESS".equals(queryResult.getTradeState())) {
                    // 支付成功，更新订单状态
                    LOGGER.info("订单{}已支付成功，更新订单状态", order.getOrderSn());
                    portalOrderService.paySuccessByOrderSn(order.getOrderSn(), 2); // 2表示微信支付
                }
                // 其他状态不处理，等待下次检查或者回调处理
            } catch (WxPayException e) {
                LOGGER.error("查询订单{}支付状态出错: {}", order.getOrderSn(), e.getMessage());
            }
            
            // 为避免微信支付接口限流，每次查询间隔一小段时间
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        LOGGER.info("订单支付状态检查任务完成");
    }
} 