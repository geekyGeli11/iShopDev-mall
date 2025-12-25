package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.BundleConfirmOrderResult;
import com.macro.mall.portal.domain.BundleOrderParam;
import com.macro.mall.portal.domain.PortalBundleDetail;
import com.macro.mall.portal.domain.PortalBundleListItem;

import java.util.List;
import java.util.Map;

/**
 * 组合商品Portal服务
 */
public interface PortalBundleService {

    /**
     * 分页查询组合商品列表
     * @param schoolId 学校ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 组合商品列表
     */
    List<PortalBundleListItem> list(Long schoolId, Integer pageNum, Integer pageSize);

    /**
     * 获取组合商品详情
     * @param id 组合商品ID
     * @param storeId 门店ID（用于获取门店库存）
     * @return 组合商品详情
     */
    PortalBundleDetail getDetail(Long id, Long storeId);

    /**
     * 生成组合商品确认订单
     * @param param 下单参数
     * @return 确认订单结果
     */
    BundleConfirmOrderResult generateConfirmOrder(BundleOrderParam param);

    /**
     * 创建组合商品订单
     * @param param 下单参数
     * @return 订单信息
     */
    Map<String, Object> createOrder(BundleOrderParam param);
}
