package com.macro.mall.dto;

import com.macro.mall.model.OmsSchool;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询单个产品进行修改时返回的结果
 * Created by macro on 2018/4/26.
 */
public class PmsProductResult extends PmsProductParam {
    @Getter
    @Setter
    @Schema(title = "商品所选分类的父id")
    private Long cateParentId;

    @Getter
    @Setter
    @Schema(title = "DIY模板名称")
    private String diyTemplateName;

    @Getter
    @Setter
    @Schema(title = "关联的学校列表")
    private List<OmsSchool> schoolList;
}
