package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 微信商家转账到零钱参数（v3版本批量接口）
 * 对应接口：POST /v3/transfer/batches
 * 
 * @author macro
 * @date 2024/12/19
 */
@Data
public class WechatTransferParam {
    
    @Schema(title = "商户appid")
    private String appid;
    
    @Schema(title = "商家批次单号")
    private String outBatchNo;
    
    @Schema(title = "批次名称")
    private String batchName;
    
    @Schema(title = "批次备注")
    private String batchRemark;
    
    @Schema(title = "转账总金额（分）")
    private Integer totalAmount;
    
    @Schema(title = "转账总笔数")
    private Integer totalNum;
    
    @Schema(title = "转账明细列表")
    private List<TransferDetail> transferDetailList;
    
    @Schema(title = "转账场景ID")
    private String transferSceneId;
    
    @Schema(title = "通知地址")
    private String notifyUrl;
    
    /**
     * 转账明细
     */
    @Data
    public static class TransferDetail {
        
        @Schema(title = "商家明细单号")
        private String outDetailNo;
        
        @Schema(title = "转账金额（分）")
        private Integer transferAmount;
        
        @Schema(title = "转账备注")
        private String transferRemark;
        
        @Schema(title = "收款用户openid")
        private String openid;
        
        @Schema(title = "收款用户姓名（可选，金额>=2000元时必填）")
        private String userName;
    }
} 