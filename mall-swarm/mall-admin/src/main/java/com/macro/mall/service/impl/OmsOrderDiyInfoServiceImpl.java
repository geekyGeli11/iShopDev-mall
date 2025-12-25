package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsOrderDiyInfoMapper;
import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.OmsOrderDiyInfoExample;
import com.macro.mall.service.OmsOrderDiyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 订单DIY信息管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class OmsOrderDiyInfoServiceImpl implements OmsOrderDiyInfoService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsOrderDiyInfoServiceImpl.class);
    
    @Autowired
    private OmsOrderDiyInfoMapper diyInfoMapper;

    @Override
    public int create(OmsOrderDiyInfo diyInfo) {
        diyInfo.setCreateTime(new Date());
        if (diyInfo.getProductionStatus() == null) {
            diyInfo.setProductionStatus((byte) 0); // 默认待生产
        }
        return diyInfoMapper.insertSelective(diyInfo);
    }

    @Override
    public int update(Long id, OmsOrderDiyInfo diyInfo) {
        diyInfo.setId(id);
        return diyInfoMapper.updateByPrimaryKeySelective(diyInfo);
    }

    @Override
    public int delete(Long id) {
        return diyInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        example.createCriteria().andIdIn(ids);
        return diyInfoMapper.deleteByExample(example);
    }

    @Override
    public OmsOrderDiyInfo getItem(Long id) {
        return diyInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsOrderDiyInfo> listByOrderId(Long orderId) {
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        example.setOrderByClause("create_time desc");
        return diyInfoMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public OmsOrderDiyInfo getByOrderItemId(Long orderItemId) {
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        example.createCriteria().andOrderItemIdEqualTo(orderItemId);
        List<OmsOrderDiyInfo> list = diyInfoMapper.selectByExampleWithBLOBs(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<OmsOrderDiyInfo> list(String keyword, Byte productionStatus, String startTime, String endTime, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        OmsOrderDiyInfoExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(keyword)) {
            criteria.andOrderSnLike("%" + keyword + "%");
        }
        if (productionStatus != null) {
            criteria.andProductionStatusEqualTo(productionStatus);
        }
        if (StringUtils.hasText(startTime)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime + " 00:00:00");
                criteria.andCreateTimeGreaterThanOrEqualTo(startDate);
            } catch (Exception e) {
                LOGGER.warn("解析开始时间失败: {}", startTime, e);
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date endDate = sdf.parse(endTime + " 23:59:59");
                criteria.andCreateTimeLessThanOrEqualTo(endDate);
            } catch (Exception e) {
                LOGGER.warn("解析结束时间失败: {}", endTime, e);
            }
        }

        example.setOrderByClause("create_time desc");
        return diyInfoMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public int updateProductionStatus(Long id, Byte productionStatus) {
        OmsOrderDiyInfo diyInfo = new OmsOrderDiyInfo();
        diyInfo.setId(id);
        diyInfo.setProductionStatus(productionStatus);
        return diyInfoMapper.updateByPrimaryKeySelective(diyInfo);
    }

    @Override
    public int updateProductionStatus(List<Long> ids, Byte productionStatus) {
        OmsOrderDiyInfo diyInfo = new OmsOrderDiyInfo();
        diyInfo.setProductionStatus(productionStatus);
        
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        example.createCriteria().andIdIn(ids);
        return diyInfoMapper.updateByExampleSelective(diyInfo, example);
    }

    @Override
    public String downloadDesignFile(Long id) {
        OmsOrderDiyInfo diyInfo = diyInfoMapper.selectByPrimaryKey(id);
        if (diyInfo == null) {
            LOGGER.warn("订单DIY信息不存在：id={}", id);
            return null;
        }
        
        // 这里应该实现文件下载逻辑，返回下载URL或文件路径
        // 暂时返回设计数据中的预览图URL
        return diyInfo.getPreviewImage();
    }

    @Override
    public String generateProductionFile(Long id) {
        OmsOrderDiyInfo diyInfo = diyInfoMapper.selectByPrimaryKey(id);
        if (diyInfo == null) {
            LOGGER.warn("订单DIY信息不存在：id={}", id);
            return null;
        }
        
        try {
            // 这里应该实现生产文件生成逻辑
            // 1. 解析设计数据
            // 2. 生成高分辨率的生产文件
            // 3. 保存到文件存储系统
            // 4. 返回文件URL
            
            // 暂时返回预览图URL作为示例
            String productionFileUrl = diyInfo.getPreviewImage();
            
            // 更新生产状态为已生成文件
            updateProductionStatus(id, (byte) 1);
            
            LOGGER.info("生成生产文件成功：id={}, url={}", id, productionFileUrl);
            return productionFileUrl;
            
        } catch (Exception e) {
            LOGGER.error("生成生产文件失败：id={}", id, e);
            return null;
        }
    }

    @Override
    public Long countByProductionStatus(Byte productionStatus) {
        OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
        if (productionStatus != null) {
            example.createCriteria().andProductionStatusEqualTo(productionStatus);
        }
        return diyInfoMapper.countByExample(example);
    }
}
