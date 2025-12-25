package com.macro.mall.portal.service.impl;

import com.google.gson.Gson;
import com.macro.mall.portal.dao.OrderPortalLogisticsDao;
import com.macro.mall.portal.dto.LogisticsDetail;
import com.macro.mall.portal.dto.OrderLogisticsInfo;
import org.json.JSONObject;
import org.json.JSONArray;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.utils.SignUtils;
import com.macro.mall.portal.service.OrderLogisticsService;
import com.macro.mall.portal.dto.OrderPortalLogistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderLogisticsServiceImpl implements OrderLogisticsService {

    @Value("${kuaidi100.key}")
    private String key;

    @Value("${kuaidi100.customer}")
    private String customer;

    @Autowired
    private OrderPortalLogisticsDao orderPortalLogisticsDao;

    @Override
    public OrderLogisticsInfo getLogisticsInfo(Long orderId) throws Exception {
        // 获取订单的物流公司和物流单号
        OrderPortalLogistics orderDetail = orderPortalLogisticsDao.getOrderLogisticsByOrderId(orderId);
        String deliveryCompany = orderDetail.getDeliveryCompany();
        String deliverySn = orderDetail.getDeliverySn();

        // 创建查询请求对象
        QueryTrackReq queryTrackReq = new QueryTrackReq();
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        // 设置快递公司和快递单号
        queryTrackParam.setCom(deliveryCompany);
        queryTrackParam.setNum(deliverySn);
        queryTrackParam.setPhone(orderDetail.getReceiverPhone());
        // 将参数转换成JSON
        String param = new Gson().toJson(queryTrackParam);

        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(customer);
        queryTrackReq.setSign(SignUtils.querySign(param ,key,customer));
        // 执行查询
        IBaseClient baseClient = new QueryTrack();
        HttpResult result = baseClient.execute(queryTrackReq);

        // 获取响应中的结果
        String response = result.getBody();

        // 解析响应的 JSON 数据
        JSONObject jsonResponse = new JSONObject(response);
        List<LogisticsDetail> logisticsDetails = new ArrayList<>();

        // 检查响应中的 result 字段是否为 false
        if (jsonResponse.has("result") && !jsonResponse.getBoolean("result")) {
            // 如果 result 为 false，返回 message 字段中的提示信息
            String message = jsonResponse.optString("message", "查询失败，请稍后再试");
            OrderLogisticsInfo logisticsInfoDTO = new OrderLogisticsInfo();
            logisticsInfoDTO.setMessage(message); // 新增 message 字段
            return logisticsInfoDTO;
        }

        // 获取物流信息（物流信息在 "data" 字段里）
        // 检查 "data" 数组是否存在并且不为空
        if (jsonResponse.has("data") && !jsonResponse.getJSONArray("data").isEmpty()) {
            // 获取 "data" 数组
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            // 遍历 "data" 数组，将每个元素映射为 LogisticsDetail 对象
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataItem = dataArray.getJSONObject(i);

                // 创建一个新的 LogisticsDetail 对象
                LogisticsDetail logisticsDetail = new LogisticsDetail();

                // 从 JSON 数据中提取属性并设置到 LogisticsDetail 对象
                logisticsDetail.setTime(dataItem.optString("time", "暂无信息"));
                logisticsDetail.setContext(dataItem.optString("context", "暂无信息"));
                logisticsDetail.setFtime(dataItem.optString("ftime", "暂无信息"));
                logisticsDetail.setAreaCode(dataItem.optString("areaCode", "暂无信息"));
                logisticsDetail.setAreaName(dataItem.optString("areaName", "暂无信息"));
                logisticsDetail.setStatus(dataItem.optString("status", "未知状态"));
                logisticsDetail.setLocation(dataItem.optString("location", "未知位置"));
                logisticsDetail.setAreaCenter(dataItem.optString("areaCenter", "未知区域中心"));
                logisticsDetail.setAreaPinYin(dataItem.optString("areaPinYin", "未知区域拼音"));
                logisticsDetail.setStatusCode(dataItem.optString("statusCode", "暂无信息"));

                // 将 LogisticsDetail 对象添加到列表中
                logisticsDetails.add(logisticsDetail);

            }
        }

        // 封装成 OmsOrderLogisticsInfo 对象
        OrderLogisticsInfo logisticsInfoDTO = new OrderLogisticsInfo();
        logisticsInfoDTO.setDeliveryCompany(deliveryCompany);
        logisticsInfoDTO.setDeliverySn(deliverySn);
        logisticsInfoDTO.setLogisticsInfo(logisticsDetails);
        return logisticsInfoDTO;
    }

}
