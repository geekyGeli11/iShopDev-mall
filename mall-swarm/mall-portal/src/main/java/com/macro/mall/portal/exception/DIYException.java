package com.macro.mall.portal.exception;

/**
 * DIY业务异常类
 * Created by macro on 2024/12/20.
 */
public class DIYException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;
    private Object data;
    
    public DIYException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    
    public DIYException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public DIYException(String errorCode, String errorMessage, Object data) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }
    
    public DIYException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * DIY异常错误码常量
     */
    public static class ErrorCode {
        // 通用错误
        public static final String INVALID_PARAMETER = "DIY_001";
        public static final String RESOURCE_NOT_FOUND = "DIY_002";
        public static final String PERMISSION_DENIED = "DIY_003";
        public static final String OPERATION_FAILED = "DIY_004";
        
        // 素材相关错误
        public static final String MATERIAL_NOT_FOUND = "DIY_101";
        public static final String MATERIAL_UPLOAD_FAILED = "DIY_102";
        public static final String MATERIAL_FORMAT_INVALID = "DIY_103";
        public static final String MATERIAL_SIZE_EXCEEDED = "DIY_104";
        
        // 模板相关错误
        public static final String TEMPLATE_NOT_FOUND = "DIY_201";
        public static final String TEMPLATE_INVALID = "DIY_202";
        public static final String TEMPLATE_NOT_SUPPORTED = "DIY_203";
        
        // 设计相关错误
        public static final String DESIGN_SAVE_FAILED = "DIY_301";
        public static final String DESIGN_LOAD_FAILED = "DIY_302";
        public static final String DESIGN_INVALID = "DIY_303";
        public static final String DESIGN_TOO_COMPLEX = "DIY_304";
        
        // AI相关错误
        public static final String AI_SERVICE_UNAVAILABLE = "DIY_401";
        public static final String AI_PROCESSING_FAILED = "DIY_402";
        public static final String AI_QUOTA_EXCEEDED = "DIY_403";
        public static final String AI_CONTENT_INVALID = "DIY_404";
        
        // 预览相关错误
        public static final String PREVIEW_GENERATION_FAILED = "DIY_501";
        public static final String PREVIEW_TIMEOUT = "DIY_502";
        public static final String PREVIEW_QUALITY_LOW = "DIY_503";
        
        // 订单相关错误
        public static final String ORDER_CREATE_FAILED = "DIY_601";
        public static final String ORDER_PRICE_INVALID = "DIY_602";
        public static final String ORDER_PRODUCT_INVALID = "DIY_603";
        public static final String ORDER_ADDRESS_INVALID = "DIY_604";
        
        // 文件相关错误
        public static final String FILE_UPLOAD_FAILED = "DIY_701";
        public static final String FILE_DELETE_FAILED = "DIY_702";
        public static final String FILE_NOT_FOUND = "DIY_703";
        public static final String FILE_FORMAT_INVALID = "DIY_704";
        
        // 缓存相关错误
        public static final String CACHE_OPERATION_FAILED = "DIY_801";
        public static final String CACHE_KEY_INVALID = "DIY_802";
    }
    
    /**
     * 创建常用异常的静态方法
     */
    public static DIYException invalidParameter(String message) {
        return new DIYException(ErrorCode.INVALID_PARAMETER, message);
    }
    
    public static DIYException resourceNotFound(String message) {
        return new DIYException(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
    
    public static DIYException permissionDenied(String message) {
        return new DIYException(ErrorCode.PERMISSION_DENIED, message);
    }
    
    public static DIYException operationFailed(String message) {
        return new DIYException(ErrorCode.OPERATION_FAILED, message);
    }
    
    public static DIYException materialNotFound(String message) {
        return new DIYException(ErrorCode.MATERIAL_NOT_FOUND, message);
    }
    
    public static DIYException materialUploadFailed(String message) {
        return new DIYException(ErrorCode.MATERIAL_UPLOAD_FAILED, message);
    }
    
    public static DIYException templateNotFound(String message) {
        return new DIYException(ErrorCode.TEMPLATE_NOT_FOUND, message);
    }
    
    public static DIYException designSaveFailed(String message) {
        return new DIYException(ErrorCode.DESIGN_SAVE_FAILED, message);
    }
    
    public static DIYException aiServiceUnavailable(String message) {
        return new DIYException(ErrorCode.AI_SERVICE_UNAVAILABLE, message);
    }
    
    public static DIYException previewGenerationFailed(String message) {
        return new DIYException(ErrorCode.PREVIEW_GENERATION_FAILED, message);
    }
    
    public static DIYException orderCreateFailed(String message) {
        return new DIYException(ErrorCode.ORDER_CREATE_FAILED, message);
    }
    
    public static DIYException fileUploadFailed(String message) {
        return new DIYException(ErrorCode.FILE_UPLOAD_FAILED, message);
    }
}
