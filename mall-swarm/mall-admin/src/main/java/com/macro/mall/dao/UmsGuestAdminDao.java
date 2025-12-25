package com.macro.mall.dao;

import com.macro.mall.dto.UmsGuestListVO;
import com.macro.mall.dto.UmsGuestQueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游客管理Dao
 */
public interface UmsGuestAdminDao {

    /**
     * 获取游客列表
     * @param queryParam 查询参数
     * @return 游客列表
     */
    List<UmsGuestListVO> getGuestList(@Param("queryParam") UmsGuestQueryParam queryParam);

    /**
     * 清理过期游客
     * @param expiredDays 过期天数
     * @return 清理数量
     */
    int cleanExpiredGuests(@Param("expiredDays") Integer expiredDays);
}
