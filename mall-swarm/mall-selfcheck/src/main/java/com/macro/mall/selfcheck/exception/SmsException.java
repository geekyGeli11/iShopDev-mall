package com.macro.mall.selfcheck.exception;

/**
 * 短信服务异常
 * 
 * @author macro
 * @since 1.0.0
 */
public class SmsException extends RuntimeException {
    
    private final String errorCode;
    private final SmsErrorType errorType;
    
    public SmsException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
        this.errorType = SmsErrorType.UNKNOWN;
    }
    
    public SmsException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = determineErrorType(errorCode);
    }
    
    public SmsException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorType = determineErrorType(errorCode);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public SmsErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * 根据错误代码确定错误类型
     */
    private SmsErrorType determineErrorType(String errorCode) {
        if (errorCode == null) {
            return SmsErrorType.UNKNOWN;
        }
        
        if (errorCode.contains("流控") || errorCode.contains("限流") || 
            errorCode.contains("Permits") || errorCode.contains("限制")) {
            return SmsErrorType.RATE_LIMIT;
        }
        
        if (errorCode.contains("余额") || errorCode.contains("欠费") || 
            errorCode.contains("账户")) {
            return SmsErrorType.ACCOUNT_ISSUE;
        }
        
        if (errorCode.contains("签名") || errorCode.contains("模板")) {
            return SmsErrorType.TEMPLATE_ISSUE;
        }
        
        if (errorCode.contains("手机号") || errorCode.contains("号码")) {
            return SmsErrorType.PHONE_ISSUE;
        }
        
        return SmsErrorType.SERVICE_ERROR;
    }
    
    /**
     * 短信错误类型枚举
     */
    public enum SmsErrorType {
        /** 流控限制 */
        RATE_LIMIT,
        /** 账户问题（余额不足等） */
        ACCOUNT_ISSUE,
        /** 模板或签名问题 */
        TEMPLATE_ISSUE,
        /** 手机号问题 */
        PHONE_ISSUE,
        /** 服务错误 */
        SERVICE_ERROR,
        /** 未知错误 */
        UNKNOWN
    }
} 