package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsMemberRechargeOrderQueryParam;
import com.macro.mall.mapper.UmsMemberRechargeOrderMapper;
import com.macro.mall.model.UmsMemberRechargeOrder;
import com.macro.mall.model.UmsMemberRechargeOrderExample;
import com.macro.mall.service.UmsMemberRechargeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 充值订单管理Service实现类
 */
@Service
public class UmsMemberRechargeOrderServiceImpl implements UmsMemberRechargeOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberRechargeOrderServiceImpl.class);

    @Autowired
    private UmsMemberRechargeOrderMapper rechargeOrderMapper;

    @Override
    public List<UmsMemberRechargeOrder> list(UmsMemberRechargeOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberRechargeOrderExample example = new UmsMemberRechargeOrderExample();
        UmsMemberRechargeOrderExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(queryParam.getOrderSn())) {
            criteria.andOrderSnLike("%" + queryParam.getOrderSn() + "%");
        }

        if (StringUtils.hasText(queryParam.getMemberUsername())) {
            criteria.andMemberUsernameLike("%" + queryParam.getMemberUsername() + "%");
        }

        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus().byteValue());
        }

        if (queryParam.getPayType() != null) {
            criteria.andPayTypeEqualTo(queryParam.getPayType().byteValue());
        }

        if (StringUtils.hasText(queryParam.getCreateTime())) {
            String createTime = queryParam.getCreateTime() + " 00:00:00";
            String endTime = queryParam.getCreateTime() + " 23:59:59";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(createTime);
                Date endDate = sdf.parse(endTime);
                criteria.andCreateTimeBetween(startDate, endDate);
            } catch (Exception e) {
                LOGGER.warn("解析创建时间失败: {}", queryParam.getCreateTime(), e);
            }
        }

        if (StringUtils.hasText(queryParam.getPayTime())) {
            String payTime = queryParam.getPayTime() + " 00:00:00";
            String endTime = queryParam.getPayTime() + " 23:59:59";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(payTime);
                Date endDate = sdf.parse(endTime);
                criteria.andPayTimeBetween(startDate, endDate);
            } catch (Exception e) {
                LOGGER.warn("解析支付时间失败: {}", queryParam.getPayTime(), e);
            }
        }

        example.setOrderByClause("create_time desc");
        return rechargeOrderMapper.selectByExample(example);
    }

    @Override
    public UmsMemberRechargeOrder getItem(Long id) {
        return rechargeOrderMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsMemberRechargeOrderExample example = new UmsMemberRechargeOrderExample();
        example.createCriteria().andIdIn(ids);
        return rechargeOrderMapper.deleteByExample(example);
    }

    @Override
    public void exportRechargeOrder(UmsMemberRechargeOrderQueryParam queryParam, HttpServletResponse response) {
        try {
            // 设置响应头
            String fileName = "recharge_order_export_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            // 查询数据（不分页，导出全部）
            UmsMemberRechargeOrderExample example = buildExportExample(queryParam);
            List<UmsMemberRechargeOrder> orderList = rechargeOrderMapper.selectByExample(example);

            // 写入CSV
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            
            // 写入BOM头，解决Excel打开中文乱码问题
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            
            // 写入表头
            writer.println("订单编号,用户账号,充值金额,支付方式,订单状态,创建时间,支付时间,第三方流水号,备注");

            // 写入数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (UmsMemberRechargeOrder order : orderList) {
                StringBuilder line = new StringBuilder();
                line.append(csvEscape(order.getOrderSn())).append(",");
                line.append(csvEscape(order.getMemberUsername())).append(",");
                line.append(order.getAmount()).append(",");
                line.append(formatPayType(order.getPayType())).append(",");
                line.append(formatStatus(order.getStatus())).append(",");
                line.append(order.getCreateTime() != null ? sdf.format(order.getCreateTime()) : "").append(",");
                line.append(order.getPayTime() != null ? sdf.format(order.getPayTime()) : "").append(",");
                line.append(csvEscape(order.getPaySn())).append(",");
                line.append(csvEscape(order.getNote()));
                writer.println(line.toString());
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            LOGGER.error("导出充值订单失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    @Override
    public List<UmsMemberRechargeOrder> getMemberRechargeHistory(Long memberId, Integer status, String startDate, String endDate, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberRechargeOrderExample example = new UmsMemberRechargeOrderExample();
        UmsMemberRechargeOrderExample.Criteria criteria = example.createCriteria();

        criteria.andMemberIdEqualTo(memberId);

        if (status != null) {
            criteria.andStatusEqualTo(status.byteValue());
        }

        if (StringUtils.hasText(startDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                criteria.andCreateTimeGreaterThanOrEqualTo(start);
            } catch (Exception e) {
                LOGGER.warn("解析开始时间失败: {}", startDate, e);
            }
        }

        if (StringUtils.hasText(endDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date end = sdf.parse(endDate + " 23:59:59");
                criteria.andCreateTimeLessThanOrEqualTo(end);
            } catch (Exception e) {
                LOGGER.warn("解析结束时间失败: {}", endDate, e);
            }
        }

        example.setOrderByClause("create_time desc");
        return rechargeOrderMapper.selectByExample(example);
    }

    /**
     * 构建导出查询条件
     */
    private UmsMemberRechargeOrderExample buildExportExample(UmsMemberRechargeOrderQueryParam queryParam) {
        UmsMemberRechargeOrderExample example = new UmsMemberRechargeOrderExample();
        UmsMemberRechargeOrderExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(queryParam.getOrderSn())) {
            criteria.andOrderSnLike("%" + queryParam.getOrderSn() + "%");
        }

        if (StringUtils.hasText(queryParam.getMemberUsername())) {
            criteria.andMemberUsernameLike("%" + queryParam.getMemberUsername() + "%");
        }

        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus().byteValue());
        }

        if (queryParam.getPayType() != null) {
            criteria.andPayTypeEqualTo(queryParam.getPayType().byteValue());
        }

        if (StringUtils.hasText(queryParam.getStartTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(queryParam.getStartTime());
                criteria.andCreateTimeGreaterThanOrEqualTo(startDate);
            } catch (Exception e) {
                LOGGER.warn("解析开始时间失败: {}", queryParam.getStartTime(), e);
            }
        }

        if (StringUtils.hasText(queryParam.getEndTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date endDate = sdf.parse(queryParam.getEndTime() + " 23:59:59");
                criteria.andCreateTimeLessThanOrEqualTo(endDate);
            } catch (Exception e) {
                LOGGER.warn("解析结束时间失败: {}", queryParam.getEndTime(), e);
            }
        }

        example.setOrderByClause("create_time desc");
        return example;
    }

    /**
     * CSV字段转义
     */
    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        // 如果包含逗号、引号或换行符，需要用引号包围，并转义引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 格式化支付方式
     */
    private String formatPayType(Byte payType) {
        if (payType == null) {
            return "未支付";
        }
        switch (payType) {
            case 1:
                return "支付宝";
            case 2:
                return "微信";
            default:
                return "未支付";
        }
    }

    /**
     * 格式化订单状态
     */
    private String formatStatus(Byte status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "支付成功";
            case 2:
                return "支付失败";
            case 3:
                return "已取消";
            default:
                return "未知状态";
        }
    }
} 