package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 会员优惠券列表查询参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Schema(title = "会员优惠券列表查询参数")
public class MemberCouponListParam {

    @Schema(title = "使用状态：0->未使用；1->已使用；2->已过期；null->全部", description = "优惠券使用状态筛选")
    @Min(value = 0, message = "使用状态值不能小于0")
    @Max(value = 2, message = "使用状态值不能大于2")
    private Integer useStatus;

    @Schema(title = "页码", description = "当前页码，从1开始", example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum;

    @Schema(title = "每页数量", description = "每页显示数量，最大20", example = "10")
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 20, message = "每页数量不能大于20")
    private Integer pageSize;

    @Schema(title = "排序字段", description = "排序字段：createTime-创建时间，endTime-到期时间，amount-优惠金额", example = "createTime")
    private String orderBy;

    @Schema(title = "排序方向", description = "排序方向：asc-升序，desc-降序", example = "desc")
    private String orderDirection;

    public MemberCouponListParam() {
        // 默认参数
        this.pageNum = 1;
        this.pageSize = 10;
        this.orderBy = "createTime";
        this.orderDirection = "desc";
    }

    public Integer getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public String toString() {
        return "MemberCouponListParam{" +
                "useStatus=" + useStatus +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirection='" + orderDirection + '\'' +
                '}';
    }
} 