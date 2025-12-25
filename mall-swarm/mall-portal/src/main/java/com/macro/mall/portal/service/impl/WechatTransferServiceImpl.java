package com.macro.mall.portal.service.impl;

import com.macro.mall.common.service.WechatTransferService;
import com.macro.mall.dto.WechatTransferParam;
import com.macro.mall.dto.WechatTransferResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信商家转账到零钱服务实现（v3版本）
 * 对应接口：POST /v3/transfer/batches
 * 
 * @author macro
 * @date 2024/12/19
 */
@Service
public class WechatTransferServiceImpl implements WechatTransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatTransferServiceImpl.class);

    @Autowired
    private WxPayService wxPayService;

    // 使用统一的微信支付配置，不再单独读取配置
    // 所有配置都通过 WxPayService 获取
    
    /**
     * 发起商家转账到零钱
     *
     * @param transferParam 转账参数
     * @return 转账结果
     */
    @Override
    public WechatTransferResult transfer(WechatTransferParam transferParam) {
        LOGGER.info("开始执行微信转账，批次单号：{}", transferParam.getOutBatchNo());

        WechatTransferResult result = new WechatTransferResult();

        try {
            // 验证转账参数
            if (transferParam.getTransferDetailList() == null || transferParam.getTransferDetailList().isEmpty()) {
                throw new IllegalArgumentException("转账明细不能为空");
            }

            LOGGER.info("微信v3转账请求参数：批次单号={}，总金额={}分，总笔数={}",
                       transferParam.getOutBatchNo(), transferParam.getTotalAmount(), transferParam.getTotalNum());

            // 构建v3版本商家转账到零钱请求
            String requestBody = buildTransferRequestBody(transferParam);

            LOGGER.info("微信v3转账请求体：{}", requestBody);

            // 调用微信支付v3商家转账到零钱接口
            String responseBody = callWechatTransferV3Api(requestBody);

            LOGGER.info("微信v3转账响应：{}", responseBody);

            // 解析响应结果
            result = parseTransferResponse(responseBody, transferParam.getOutBatchNo());

        } catch (WxPayException e) {
            LOGGER.error("微信v3转账调用异常，批次单号：{}，错误信息：{}", transferParam.getOutBatchNo(), e.getMessage(), e);
            result.setOutBatchNo(transferParam.getOutBatchNo());
            result.setBatchStatus("CLOSED");
            result.setStatusDesc("转账异常：" + e.getErrCodeDes());
        } catch (Exception e) {
            LOGGER.error("微信v3转账执行失败，批次单号：{}", transferParam.getOutBatchNo(), e);
            result.setOutBatchNo(transferParam.getOutBatchNo());
            result.setBatchStatus("CLOSED");
            result.setStatusDesc("转账失败: " + e.getMessage());
        }

        return result;
    }
    

    
    /**
     * 构建转账参数
     * 
     * @param withdrawSn 提现编号
     * @param openid 用户openid
     * @param amount 转账金额（元）
     * @param userName 用户姓名（可选）
     * @param userNickname 用户昵称
     * @return 转账参数
     */
    @Override
    public WechatTransferParam buildTransferParam(String withdrawSn, String openid, 
                                                BigDecimal amount, String userName, String userNickname) {
        WechatTransferParam param = new WechatTransferParam();
        
        // 设置批次参数
        param.setAppid(wxPayService.getConfig().getAppId());
        param.setOutBatchNo(withdrawSn); // 使用提现编号作为批次单号
        param.setBatchName("用户提现-" + withdrawSn);
        param.setBatchRemark("用户" + (userNickname != null ? userNickname : "匿名") + "提现申请");
        
        // 金额转换为分
        int amountFen = amount.multiply(new BigDecimal("100")).intValue();
        param.setTotalAmount(amountFen);
        param.setTotalNum(1); // 单笔转账
        
        // 设置转账明细
        WechatTransferParam.TransferDetail detail = new WechatTransferParam.TransferDetail();
        detail.setOutDetailNo(withdrawSn + "-1"); // 明细单号
        detail.setTransferAmount(amountFen);
        detail.setTransferRemark("提现到账");
        detail.setOpenid(openid);
        
        // 金额>=20元时设置用户姓名（微信要求>=2000分时必填）
        if (amountFen >= 2000 && userName != null && !userName.trim().isEmpty()) {
            detail.setUserName(userName);
        }
        
        List<WechatTransferParam.TransferDetail> detailList = new ArrayList<>();
        detailList.add(detail);
        param.setTransferDetailList(detailList);
        
        // 设置通知地址（暂时不设置，转账接口不需要回调）
        // param.setNotifyUrl(wxPayService.getConfig().getNotifyUrl() + "/wechat/transfer/notify");
        
        return param;
    }

    /**
     * 构建v3版本转账请求体
     */
    private String buildTransferRequestBody(WechatTransferParam transferParam) {
        Gson gson = new GsonBuilder().create();

        // 构建请求体Map
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("appid", transferParam.getAppid());
        requestMap.put("out_batch_no", transferParam.getOutBatchNo());
        requestMap.put("batch_name", transferParam.getBatchName());
        requestMap.put("batch_remark", transferParam.getBatchRemark());
        requestMap.put("total_amount", transferParam.getTotalAmount());
        requestMap.put("total_num", transferParam.getTotalNum());

        // 转账明细列表
        List<Map<String, Object>> transferDetailList = new ArrayList<>();
        for (WechatTransferParam.TransferDetail detail : transferParam.getTransferDetailList()) {
            Map<String, Object> detailMap = new HashMap<>();
            detailMap.put("out_detail_no", detail.getOutDetailNo());
            detailMap.put("transfer_amount", detail.getTransferAmount());
            detailMap.put("transfer_remark", detail.getTransferRemark());
            detailMap.put("openid", detail.getOpenid());

            // 如果有用户姓名，添加用户姓名字段
            if (detail.getUserName() != null && !detail.getUserName().trim().isEmpty()) {
                detailMap.put("user_name", detail.getUserName());
            }

            transferDetailList.add(detailMap);
        }
        requestMap.put("transfer_detail_list", transferDetailList);

        // 转账场景ID（1000-现金红包；2000-促销奖励；3000-佣金奖励）
        requestMap.put("transfer_scene_id", transferParam.getTransferSceneId() != null ?
                      transferParam.getTransferSceneId() : "1000");

        return gson.toJson(requestMap);
    }

    /**
     * 解析v3版本转账响应
     */
    private WechatTransferResult parseTransferResponse(String responseBody, String outBatchNo) {
        WechatTransferResult result = new WechatTransferResult();
        result.setOutBatchNo(outBatchNo);

        try {
            Gson gson = new GsonBuilder().create();
            Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);

            result.setBatchId((String) responseMap.get("batch_id"));
            result.setCreateTime((String) responseMap.get("create_time"));
            result.setBatchStatus((String) responseMap.get("batch_status"));

            // 根据批次状态设置描述
            String batchStatus = result.getBatchStatus();
            switch (batchStatus) {
                case "ACCEPTED":
                    result.setStatusDesc("批次受理成功");
                    break;
                case "PROCESSING":
                    result.setStatusDesc("批次处理中");
                    break;
                case "FINISHED":
                    result.setStatusDesc("批次处理完成");
                    break;
                case "CLOSED":
                    result.setStatusDesc("批次关闭");
                    break;
                default:
                    result.setStatusDesc("未知状态：" + batchStatus);
                    break;
            }

            LOGGER.info("微信v3转账成功，批次单号：{}，微信批次单号：{}，状态：{}",
                       outBatchNo, result.getBatchId(), result.getBatchStatus());

        } catch (Exception e) {
            LOGGER.error("解析微信v3转账响应失败，响应体：{}", responseBody, e);
            result.setBatchStatus("CLOSED");
            result.setStatusDesc("响应解析失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 调用微信支付v3商家转账到零钱API
     */
    private String callWechatTransferV3Api(String requestBody) throws WxPayException {
        try {
            LOGGER.info("调用微信支付v3转账接口，请求体：{}", requestBody);

            // 使用wxPayService的v3接口调用方法
            // 注意：weixin-java-pay 4.4.0版本中，v3接口调用方式
            String url = "/v3/transfer/batches";

            // 调用微信支付v3接口
            String response = wxPayService.postV3(url, requestBody);

            LOGGER.info("微信支付v3转账接口响应：{}", response);
            return response;

        } catch (WxPayException e) {
            LOGGER.error("调用微信支付v3转账接口失败：{}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("调用微信支付v3转账接口异常：{}", e.getMessage(), e);
            throw new WxPayException("调用微信支付v3转账接口异常", e);
        }
    }
}
