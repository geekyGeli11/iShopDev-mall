package com.macro.mall.selfcheck.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 自助收银订单工具类
 * 
 * @author macro
 * @date 2025/01/22
 */
public class SelfcheckOrderUtil {

    /**
     * 生成订单号
     * 格式：SC + 年月日 + 时分秒 + 4位随机数
     * 
     * @return 订单号
     */
    public static String generateOrderSn() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = dateFormat.format(new Date());
        
        // 生成4位随机数
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 1000-9999
        
        return "SC" + dateStr + randomNum;
    }

    /**
     * 生成游客订单号
     * 格式：SCG + 年月日 + 时分秒 + 4位随机数
     * 
     * @return 游客订单号
     */
    public static String generateGuestOrderSn() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = dateFormat.format(new Date());
        
        // 生成4位随机数
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 1000-9999
        
        return "SCG" + dateStr + randomNum;
    }

    /**
     * 验证订单号格式
     * 
     * @param orderSn 订单号
     * @return 是否有效
     */
    public static boolean isValidOrderSn(String orderSn) {
        if (orderSn == null || orderSn.length() != 20) {
            return false;
        }
        
        // 验证前缀
        if (!orderSn.startsWith("SC") && !orderSn.startsWith("SCG")) {
            return false;
        }
        
        // 验证数字部分
        String numberPart = orderSn.substring(orderSn.startsWith("SCG") ? 3 : 2);
        try {
            Long.parseLong(numberPart);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为游客订单
     * 
     * @param orderSn 订单号
     * @return 是否为游客订单
     */
    public static boolean isGuestOrder(String orderSn) {
        return orderSn != null && orderSn.startsWith("SCG");
    }
} 