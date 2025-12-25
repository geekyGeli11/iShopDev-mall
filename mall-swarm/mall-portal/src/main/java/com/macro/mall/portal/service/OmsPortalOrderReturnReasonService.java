package com.macro.mall.portal.service;

import com.macro.mall.model.OmsOrderReturnReason;

import java.util.List;

/**
 * Portal端退货原因管理Service
 * Created by macro on 2018/10/17.
 */
public interface OmsPortalOrderReturnReasonService {
    /**
     * 获取启用的退货原因列表
     */
    List<OmsOrderReturnReason> getEnabledReasons();
} 