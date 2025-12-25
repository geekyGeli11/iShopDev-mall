package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分销商申请审核参数
 */
@Schema(description = "分销商申请审核参数")
public class UmsDistributorApplyReviewParam {
    
    @Schema(description = "申请ID", required = true)
    private Long applyId;
    
    @Schema(description = "审核结果：1-通过，2-拒绝", required = true)
    private Byte status;
    
    @Schema(description = "审核意见")
    private String reviewComment;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
} 