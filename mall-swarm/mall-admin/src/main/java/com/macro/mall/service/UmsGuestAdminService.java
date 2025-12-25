package com.macro.mall.service;

import com.macro.mall.dto.UmsGuestListVO;
import com.macro.mall.dto.UmsGuestQueryParam;

import java.util.List;

/**
 * 游客管理Service
 */
public interface UmsGuestAdminService {

    /**
     * 分页查询游客列表
     * @param queryParam 查询参数
     * @param pageSize 页大小
     * @param pageNum 页码
     * @return 游客列表
     */
    List<UmsGuestListVO> list(UmsGuestQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 更新游客状态
     * @param guestId 游客ID
     * @param status 状态
     * @return 更新数量
     */
    int updateStatus(String guestId, Integer status);

    /**
     * 删除游客
     * @param guestId 游客ID
     * @return 删除数量
     */
    int deleteGuest(String guestId);

    /**
     * 清理过期游客
     * @param expiredDays 过期天数
     * @return 清理数量
     */
    int cleanExpiredGuests(Integer expiredDays);
}
