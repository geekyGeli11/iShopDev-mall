package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信商家转账到零钱结果（v3版本批量接口）
 * 对应接口：POST /v3/transfer/batches
 * 
 * @author macro
 * @date 2024/12/19
 */
@Data
public class WechatTransferResult {
    
    @Schema(title = "商家批次单号")
    private String outBatchNo;
    
    @Schema(title = "微信批次单号")
    private String batchId;
    
    @Schema(title = "批次创建时间")
    private String createTime;
    
    @Schema(title = "批次状态")
    private String batchStatus;
    
    /**
     * 判断是否成功受理
     * @return 是否成功
     */
    public boolean isSuccess() {
        return "ACCEPTED".equals(batchStatus) || 
               "PROCESSING".equals(batchStatus) || 
               "FINISHED".equals(batchStatus);
    }
    
    /**
     * 获取状态描述
     * @return 状态描述
     */
    public String getStatusDesc() {
        switch (batchStatus) {
            case "ACCEPTED":
                return "已受理";
            case "PROCESSING":
                return "转账中";
            case "FINISHED":
                return "已完成";
            case "CLOSED":
                return "已关闭";
            default:
                return "未知状态";
        }
    }
} 