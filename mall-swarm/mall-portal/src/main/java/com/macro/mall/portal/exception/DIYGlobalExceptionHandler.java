package com.macro.mall.portal.exception;

import com.macro.mall.common.api.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * DIY全局异常处理器
 * Created by macro on 2024/12/20.
 */
@RestControllerAdvice(basePackages = "com.macro.mall.portal.controller")
public class DIYGlobalExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DIYGlobalExceptionHandler.class);
    
    /**
     * 处理DIY业务异常
     */
    @ExceptionHandler(DIYException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleDIYException(DIYException e, HttpServletRequest request) {
        LOGGER.error("DIY业务异常: {} - {}", e.getErrorCode(), e.getErrorMessage(), e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", e.getErrorCode());
        errorData.put("errorMessage", e.getErrorMessage());
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());
        
        if (e.getData() != null) {
            errorData.put("data", e.getData());
        }
        
        CommonResult<Object> result = CommonResult.failed(e.getErrorMessage());
        result.setData(errorData);
        return result;
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error("参数验证异常: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.INVALID_PARAMETER);
        errorData.put("validationErrors", errors);
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());
        
        CommonResult<Object> result1 = CommonResult.failed("参数验证失败");
        result1.setData(errorData);
        return result1;
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        LOGGER.error("参数绑定异常: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.INVALID_PARAMETER);
        errorData.put("bindingErrors", errors);
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());

        CommonResult<Object> result2 = CommonResult.failed("参数绑定失败");
        result2.setData(errorData);
        return result2;
    }
    
    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        LOGGER.error("文件上传大小超限: {}", e.getMessage());
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.MATERIAL_SIZE_EXCEEDED);
        errorData.put("maxSize", e.getMaxUploadSize());
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());
        
        CommonResult<Object> result3 = CommonResult.failed("文件大小超出限制");
        result3.setData(errorData);
        return result3;
    }

    /**
     * 处理参数类型转换异常（如前端传递"undefined"给Long类型参数）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        LOGGER.error("参数类型转换异常: 参数名={}, 传入值={}, 期望类型={}",
                    e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.INVALID_PARAMETER);
        errorData.put("parameterName", e.getName());
        errorData.put("inputValue", e.getValue());
        errorData.put("expectedType", e.getRequiredType().getSimpleName());
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());

        String message = String.format("参数 '%s' 的值 '%s' 无法转换为 %s 类型",
                                     e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        CommonResult<Object> result = CommonResult.failed(message);
        result.setData(errorData);
        return result;
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        LOGGER.error("非法参数异常: {}", e.getMessage());

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.INVALID_PARAMETER);
        errorData.put("errorMessage", e.getMessage());
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());

        CommonResult<Object> result4 = CommonResult.failed("参数错误: " + e.getMessage());
        result4.setData(errorData);
        return result4;
    }
    
    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        LOGGER.error("空指针异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.OPERATION_FAILED);
        errorData.put("errorMessage", "系统内部错误");
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());
        
        CommonResult<Object> result5 = CommonResult.failed("系统内部错误");
        result5.setData(errorData);
        return result5;
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("运行时异常: {}", e.getMessage(), e);

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.OPERATION_FAILED);
        errorData.put("errorMessage", e.getMessage());
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());

        CommonResult<Object> result6 = CommonResult.failed("操作失败: " + e.getMessage());
        result6.setData(errorData);
        return result6;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<Object> handleException(Exception e, HttpServletRequest request) {
        LOGGER.error("未知异常: {}", e.getMessage(), e);

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", DIYException.ErrorCode.OPERATION_FAILED);
        errorData.put("errorMessage", "系统异常");
        errorData.put("requestPath", request.getRequestURI());
        errorData.put("timestamp", System.currentTimeMillis());
        errorData.put("exceptionType", e.getClass().getSimpleName());

        CommonResult<Object> result7 = CommonResult.failed("系统异常，请稍后重试");
        result7.setData(errorData);
        return result7;
    }
}
