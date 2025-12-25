package com.macro.mall.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 会员码生成工具类
 * 
 * @author macro
 * @since 1.0.0
 */
public class MemberCodeGenerator {
    
    private static final String CODE_PREFIX = "M";
    private static final Pattern CODE_PATTERN = Pattern.compile("^M\\d{11}$");
    private static final Random RANDOM = new Random();
    
    /**
     * 生成会员码
     * 格式：M + yyMMdd + 5位随机数
     * 例如：M24012012345
     * 
     * @return 会员码
     */
    public static String generateMemberCode() {
        // 获取当前日期的yyMMdd格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String datePart = dateFormat.format(new Date());
        
        // 生成5位随机数
        int randomNum = RANDOM.nextInt(100000);
        String randomPart = String.format("%05d", randomNum);
        
        return CODE_PREFIX + datePart + randomPart;
    }
    
    /**
     * 验证会员码格式是否正确
     * 
     * @param memberCode 会员码
     * @return true表示格式正确，false表示格式错误
     */
    public static boolean isValidMemberCode(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return false;
        }
        return CODE_PATTERN.matcher(memberCode.trim()).matches();
    }
    
    /**
     * 格式化用户输入的会员码
     * 如果用户输入11位数字，自动添加M前缀
     * 
     * @param input 用户输入
     * @return 格式化后的会员码，如果格式不正确返回null
     */
    public static String formatMemberCode(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = input.trim().toUpperCase();
        
        // 如果用户输入的是纯11位数字，自动添加M前缀
        if (trimmed.matches("^\\d{11}$")) {
            trimmed = CODE_PREFIX + trimmed;
        }
        
        // 验证格式
        if (isValidMemberCode(trimmed)) {
            return trimmed;
        }
        
        return null;
    }
    
    /**
     * 从会员码中提取日期部分
     * 
     * @param memberCode 会员码
     * @return 日期部分（yyMMdd格式），如果格式不正确返回null
     */
    public static String extractDatePart(String memberCode) {
        if (!isValidMemberCode(memberCode)) {
            return null;
        }
        // 跳过M前缀，提取6位日期部分
        return memberCode.substring(1, 7);
    }
    
    /**
     * 从会员码中提取随机数部分
     * 
     * @param memberCode 会员码
     * @return 随机数部分（5位数字），如果格式不正确返回null
     */
    public static String extractRandomPart(String memberCode) {
        if (!isValidMemberCode(memberCode)) {
            return null;
        }
        // 跳过M前缀和6位日期，提取5位随机数
        return memberCode.substring(7);
    }
} 