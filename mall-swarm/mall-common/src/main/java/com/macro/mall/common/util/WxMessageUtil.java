package com.macro.mall.common.util;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信订阅消息工具类
 * Created by macro on 2024/12/20.
 */
@Component
@ConditionalOnProperty(prefix = "wechat", name = "app-id")
public class WxMessageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMessageUtil.class);

    @Autowired
    private WxMaService wxMaService;

    /**
     * 发送订阅消息
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param templateData 模板数据
     * @param miniProgramPath 小程序跳转路径
     * @return 发送结果
     */
    public boolean sendSubscribeMessage(String openid, String templateId, Map<String, String> templateData, String miniProgramPath) {
        try {
            if (templateId == null || templateId.isEmpty()) {
                LOGGER.warn("模板ID为空，跳过发送订阅消息");
                return false;
            }

            WxMaSubscribeMessage subscribeMessage = new WxMaSubscribeMessage();
            subscribeMessage.setToUser(openid);
            subscribeMessage.setTemplateId(templateId);
            subscribeMessage.setPage(miniProgramPath);

            // 设置模板数据
            for (Map.Entry<String, String> entry : templateData.entrySet()) {
                subscribeMessage.addData(new WxMaSubscribeMessage.MsgData(entry.getKey(), entry.getValue()));
            }

            wxMaService.getMsgService().sendSubscribeMsg(subscribeMessage);
            return true;

        } catch (WxErrorException e) {
            LOGGER.error("发送订阅消息失败，openid：{}，错误码：{}，错误信息：{}", openid, e.getError().getErrorCode(), e.getError().getErrorMsg());
            return false;
        } catch (Exception e) {
            LOGGER.error("发送订阅消息异常，openid：{}", openid, e);
            return false;
        }
    }
} 