package com.macro.mall.dao;

import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.model.OmsOrderReturnApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单退货申请自定义Dao
 * Created by macro on 2018/10/18.
 */
public interface OmsOrderReturnApplyDao {
    /**
     * 查询申请列表
     */
    List<OmsOrderReturnApply> getList(@Param("queryParam") OmsReturnApplyQueryParam queryParam);

    /**
     * 获取申请详情
     */
    OmsOrderReturnApplyResult getDetail(@Param("id")Long id);

    /**
     * 查询申请列表（包含详细信息）
     */
    List<OmsOrderReturnApplyResult> getListWithDetails(@Param("queryParam") OmsReturnApplyQueryParam queryParam);

    /**
     * 获取未处理售后申请数量
     */
    Integer getPendingReturnApplyCount();

    /**
     * 根据订单ID获取售后申请列表
     */
    List<OmsOrderReturnApplyResult> getReturnApplyByOrderId(@Param("orderId") Long orderId);
}
