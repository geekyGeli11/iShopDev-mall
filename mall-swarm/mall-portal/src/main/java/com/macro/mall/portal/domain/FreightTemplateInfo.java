package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运费模板信息DTO
 * Created by auto on 2025/01/01.
 */
@Data
public class FreightTemplateInfo implements Serializable {
    
    @Schema(title = "运费模板ID")
    private Long id;

    @Schema(title = "模板名称")
    private String name;

    @Schema(title = "计费类型：1-按件数,2-按重量,3-按体积,4-固定运费")
    private Byte chargeType;

    @Schema(title = "配送类型：1-快递配送,2-同城配送,3-到店自提")
    private Byte deliveryType;

    @Schema(title = "包邮类型：0-不包邮,1-满金额包邮,2-满件数包邮,3-满重量包邮；4-无条件包邮")
    private Byte freeType;

    @Schema(title = "包邮金额条件")
    private BigDecimal freeAmount;

    @Schema(title = "包邮件数条件")
    private Integer freeCount;

    @Schema(title = "包邮重量条件(kg)")
    private BigDecimal freeWeight;

    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

    @Schema(title = "是否默认模板：0-否，1-是")
    private Boolean isDefault;

    @Schema(title = "运费描述文本，根据模板配置生成")
    private String freightDescription;

    private static final long serialVersionUID = 1L;

    /**
     * 根据运费模板生成描述文本
     */
    public void generateFreightDescription() {
        if (this.status == null || this.status == 0) {
            this.freightDescription = "暂不支持配送";
            return;
        }

        StringBuilder desc = new StringBuilder();
        
        // 配送类型描述
        switch (this.deliveryType) {
            case 1:
                desc.append("快递：");
                break;
            case 2:
                desc.append("同城配送：");
                break;
            case 3:
                desc.append("到店自提：");
                break;
            default:
                desc.append("配送：");
                break;
        }

        // 包邮条件描述
        switch (this.freeType) {
            case 0:
                // 不包邮，显示计费方式
                switch (this.chargeType) {
                    case 1:
                        desc.append("按件计费");
                        break;
                    case 2:
                        desc.append("按重量计费");
                        break;
                    case 3:
                        desc.append("按体积计费");
                        break;
                    case 4:
                        desc.append("固定运费");
                        break;
                    default:
                        desc.append("运费到付");
                        break;
                }
                break;
            case 1:
                if (this.freeAmount != null && this.freeAmount.compareTo(BigDecimal.ZERO) > 0) {
                    desc.append("满").append(this.freeAmount).append("元包邮");
                } else {
                    desc.append("免运费");
                }
                break;
            case 2:
                if (this.freeCount != null && this.freeCount > 0) {
                    desc.append("满").append(this.freeCount).append("件包邮");
                } else {
                    desc.append("免运费");
                }
                break;
            case 3:
                if (this.freeWeight != null && this.freeWeight.compareTo(BigDecimal.ZERO) > 0) {
                    desc.append("满").append(this.freeWeight).append("kg包邮");
                } else {
                    desc.append("免运费");
                }
                break;
            case 4:
                desc.append("免运费");
                break;
            default:
                desc.append("运费详询客服");
                break;
        }

        // 特殊情况：顺丰可选（如果是快递配送）
        if (this.deliveryType == 1) {
            desc.append("，可加顺丰");
        }

        this.freightDescription = desc.toString();
    }
} 