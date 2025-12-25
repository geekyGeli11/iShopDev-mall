package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * DIY 装修 – 页面 DTO
 *
 * 说明：对外暴露的字段基于 diy_page 表，额外包含当前草稿版本号（version）
 * 以及草稿 DSL（dslJson）。
 * Created by AI Assistant on 2025/06/24.
 */
@Data
public class DiyPageDTO {

    /** 主键 */
    @Schema(description = "页面 ID")
    private Long id;

    /** 门店 ID */
    @Schema(description = "门店 ID")
    private Long storeId;

    /** 页面类型：1=首页 2=商品详情 3=自定义页 等 */
    @Schema(description = "页面类型")
    private Byte pageType;

    /** 页面标题 */
    @Schema(description = "页面标题")
    private String title;

    /** 状态：0=草稿 1=已发布 */
    @Schema(description = "页面状态 (0=草稿 1=已发布)")
    private Byte status;

    /** 最新草稿版本号 */
    @Schema(description = "最新草稿版本号")
    private Integer latestVersion;

    /** 已发布的版本号 */
    @Schema(description = "已发布版本号")
    private Integer publishVersion;

    /* ---------------- 扩展字段 ---------------- */

    /** 本次保存草稿的版本号 */
    @Schema(description = "草稿版本号")
    private Integer version;

    /** 草稿 DSL JSON 字符串 */
    @Schema(description = "DSL JSON")
    private String dslJson;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private Date updateTime;
} 