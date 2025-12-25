package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现申请参数
 */
@Schema(description = "提现申请参数")
public class WithdrawApplyParam {
    
    @Schema(description = "申请提现金额")
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于0")
    private BigDecimal applyAmount;
    
    @Schema(description = "提现方式：1-微信钱包，2-银行卡，3-存入文创余额")
    @NotNull(message = "提现方式不能为空")
    private Byte withdrawType;
    
    @Schema(description = "提现账户信息（微信openid或银行卡号）")
    private String withdrawAccount;
    
    @Schema(description = "账户名称")
    private String accountName;
    
    @Schema(description = "申请备注")
    private String remark;
    
    public BigDecimal getApplyAmount() {
        return applyAmount;
    }
    
    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }
    
    public Byte getWithdrawType() {
        return withdrawType;
    }
    
    public void setWithdrawType(Byte withdrawType) {
        this.withdrawType = withdrawType;
    }
    
    public String getWithdrawAccount() {
        return withdrawAccount;
    }
    
    public void setWithdrawAccount(String withdrawAccount) {
        this.withdrawAccount = withdrawAccount;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
} 