package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class CmsLocalGoodsRelation implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "本地好物ID")
    private Long localGoodsId;

    @Schema(title = "关联商品ID")
    private Long productId;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLocalGoodsId() {
        return localGoodsId;
    }

    public void setLocalGoodsId(Long localGoodsId) {
        this.localGoodsId = localGoodsId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", localGoodsId=").append(localGoodsId);
        sb.append(", productId=").append(productId);
        sb.append(", sort=").append(sort);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}