package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.OmsOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 订单管理Service
 * Created by macro on 2018/10/11.
 */
public interface OmsOrderService {
    /**
     * 订单查询
     */
    List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 订单查询（包含商品信息）
     */
    List<OmsOrderWithItems> listWithItems(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量发货
     */
    @Transactional
    int delivery(List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 批量关闭订单
     */
    @Transactional
    int close(List<Long> ids, String note);

    /**
     * 批量删除订单
     */
    int delete(List<Long> ids);

    /**
     * 获取指定订单详情
     */
    OmsOrderDetail detail(Long id);

    /**
     * 修改订单收货人信息
     */
    @Transactional
    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    /**
     * 修改订单费用信息
     */
    @Transactional
    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
    @Transactional
    int updateNote(Long id, String note, Integer status);

    /**
     * 核销订单
     */
    @Transactional
    Map<String, Object> pickupOrder(String pickupCode, String operator);

    /**
     * 无分页订单查询，用于导出
     */
    List<OmsOrder> listAll(OmsOrderQueryParam queryParam);

    /**
     * 根据条件统计订单数量
     */
    Long count(OmsOrderQueryParam queryParam);

    /**
     * 从Excel文件批量发货
     */
    @Transactional
    Map<String, Object> batchDeliveryFromExcel(org.springframework.web.multipart.MultipartFile file) throws Exception;

    /**
     * 下载发货模板
     */
    void downloadDeliveryTemplate(jakarta.servlet.http.HttpServletResponse response);

    /**
     * 获取指定SKU在各门店的库存信息
     */
    List<OmsStoreStockInfo> getStoreStockList(Long skuId);

    /**
     * 改选发货门店
     * @param param 改选参数
     * @return 是否成功
     */
    @Transactional
    boolean changeDeliveryStore(OmsChangeDeliveryStoreParam param);
}
