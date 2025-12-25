package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class VSelfcheckStats implements Serializable {
    private String userType;

    private Long totalCount;

    private Long activeCount;

    private Long withOrderCount;

    private Long recentActiveCount;

    private static final long serialVersionUID = 1L;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getWithOrderCount() {
        return withOrderCount;
    }

    public void setWithOrderCount(Long withOrderCount) {
        this.withOrderCount = withOrderCount;
    }

    public Long getRecentActiveCount() {
        return recentActiveCount;
    }

    public void setRecentActiveCount(Long recentActiveCount) {
        this.recentActiveCount = recentActiveCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userType=").append(userType);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", activeCount=").append(activeCount);
        sb.append(", withOrderCount=").append(withOrderCount);
        sb.append(", recentActiveCount=").append(recentActiveCount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}