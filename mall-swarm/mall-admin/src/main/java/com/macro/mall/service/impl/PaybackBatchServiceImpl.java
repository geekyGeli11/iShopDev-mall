package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.PaybackBatchCreateRequest;
import com.macro.mall.dto.PaybackBatchQueryRequest;
import com.macro.mall.dto.PaybackBatchUpdateRequest;
import com.macro.mall.mapper.PmsPaybackBatchMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsPaybackBatch;
import com.macro.mall.model.PmsPaybackBatchExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.PaybackBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 补货批次服务实现类
 * Created by guanghengzhou on 2024/12/12.
 */
@Service
public class PaybackBatchServiceImpl implements PaybackBatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaybackBatchServiceImpl.class);

    // 批次状态常量
    public static final byte BATCH_STATUS_PENDING = 0;      // 待启动
    public static final byte BATCH_STATUS_ACTIVE = 1;       // 活跃
    public static final byte BATCH_STATUS_COMPLETED = 2;    // 已回本
    public static final byte BATCH_STATUS_EARLY_END = 3;    // 提前结束

    @Autowired
    private PmsPaybackBatchMapper batchMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public Long createBatch(PaybackBatchCreateRequest request) {
        // 1. 获取商品信息
        PmsProduct product = productMapper.selectByPrimaryKey(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 检查是否有活跃批次
        PmsPaybackBatch activeBatch = getActiveBatch(request.getProductId());
        byte initialStatus = (activeBatch == null) ? BATCH_STATUS_ACTIVE : BATCH_STATUS_PENDING;

        // 3. 获取最大批次序号和最后一个批次
        int maxBatchNo = getMaxBatchNo(request.getProductId());
        
        // 4. 校验补货日期不能早于上一个批次的补货日期
        PmsPaybackBatch lastBatch = getLastBatch(request.getProductId());
        Date replenishmentDate = request.getReplenishmentDate() != null ? request.getReplenishmentDate() : new Date();
        if (lastBatch != null && lastBatch.getReplenishmentDate() != null 
                && replenishmentDate.before(lastBatch.getReplenishmentDate())) {
            throw new RuntimeException("补货日期不能早于上一个批次的补货日期（" + lastBatch.getReplenishmentDate() + "）");
        }

        // 4. 创建批次记录
        PmsPaybackBatch batch = new PmsPaybackBatch();
        batch.setProductId(request.getProductId());
        batch.setProductName(product.getName());
        batch.setProductSn(product.getProductSn());
        batch.setProductPic(product.getPic());
        batch.setBatchNo(maxBatchNo + 1);
        batch.setReplenishmentQuantity(request.getReplenishmentQuantity());
        batch.setReplenishmentAmount(request.getReplenishmentAmount());
        batch.setTargetAmount(request.getTargetAmount());
        batch.setReplenishmentDate(replenishmentDate);
        batch.setCurrentSoldQuantity(0);
        batch.setCurrentSoldAmount(BigDecimal.ZERO);
        batch.setPaybackProgress(BigDecimal.ZERO);
        batch.setProfitAmount(BigDecimal.ZERO);
        batch.setProfitRate(BigDecimal.ZERO);
        batch.setBatchStatus(initialStatus);
        batch.setCreatedAt(new Date());
        batch.setUpdatedAt(new Date());

        // 如果是活跃状态，设置开始统计日期
        if (initialStatus == BATCH_STATUS_ACTIVE) {
            batch.setStartDate(new Date());
        }

        batchMapper.insertSelective(batch);
        LOGGER.info("创建补货批次成功，批次ID: {}, 商品ID: {}, 状态: {}", batch.getId(), request.getProductId(), initialStatus);
        return batch.getId();
    }

    @Override
    public int updateBatch(Long batchId, PaybackBatchUpdateRequest request) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return 0;
        }

        PmsPaybackBatch updateBatch = new PmsPaybackBatch();
        updateBatch.setId(batchId);
        
        if (request.getReplenishmentQuantity() != null) {
            updateBatch.setReplenishmentQuantity(request.getReplenishmentQuantity());
        }
        if (request.getReplenishmentAmount() != null) {
            updateBatch.setReplenishmentAmount(request.getReplenishmentAmount());
        }
        if (request.getTargetAmount() != null) {
            updateBatch.setTargetAmount(request.getTargetAmount());
        }
        if (request.getReplenishmentDate() != null) {
            updateBatch.setReplenishmentDate(request.getReplenishmentDate());
        }
        updateBatch.setUpdatedAt(new Date());

        return batchMapper.updateByPrimaryKeySelective(updateBatch);
    }

    @Override
    public int deleteBatch(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return 0;
        }

        Long productId = batch.getProductId();
        boolean wasActive = batch.getBatchStatus() == BATCH_STATUS_ACTIVE;

        // 删除批次
        int result = batchMapper.deleteByPrimaryKey(batchId);

        // 如果删除的是活跃批次，自动启动下一个待启动批次
        if (wasActive && result > 0) {
            activateNextPendingBatch(productId);
        }

        LOGGER.info("删除补货批次，批次ID: {}, 是否为活跃批次: {}", batchId, wasActive);
        return result;
    }

    @Override
    public int forceStartBatch(Long batchId) {
        PmsPaybackBatch targetBatch = batchMapper.selectByPrimaryKey(batchId);
        if (targetBatch == null) {
            return 0;
        }

        // 只能强制启动待启动状态的批次
        if (targetBatch.getBatchStatus() != BATCH_STATUS_PENDING) {
            throw new RuntimeException("只能强制启动待启动状态的批次");
        }

        Long productId = targetBatch.getProductId();

        // 1. 将当前活跃批次标记为提前结束
        PmsPaybackBatch activeBatch = getActiveBatch(productId);
        if (activeBatch != null) {
            PmsPaybackBatch updateActive = new PmsPaybackBatch();
            updateActive.setId(activeBatch.getId());
            updateActive.setBatchStatus(BATCH_STATUS_EARLY_END);
            updateActive.setCompletedDate(new Date());
            updateActive.setUpdatedAt(new Date());
            batchMapper.updateByPrimaryKeySelective(updateActive);
            LOGGER.info("批次提前结束，批次ID: {}", activeBatch.getId());
        }

        // 2. 将目标批次设为活跃
        PmsPaybackBatch updateTarget = new PmsPaybackBatch();
        updateTarget.setId(batchId);
        updateTarget.setBatchStatus(BATCH_STATUS_ACTIVE);
        updateTarget.setStartDate(new Date());
        updateTarget.setUpdatedAt(new Date());
        int result = batchMapper.updateByPrimaryKeySelective(updateTarget);

        LOGGER.info("强制启动批次，批次ID: {}", batchId);
        return result;
    }

    @Override
    public PmsPaybackBatch getActiveBatch(Long productId) {
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();
        example.createCriteria()
                .andProductIdEqualTo(productId)
                .andBatchStatusEqualTo(BATCH_STATUS_ACTIVE);
        List<PmsPaybackBatch> batches = batchMapper.selectByExample(example);
        return batches.isEmpty() ? null : batches.get(0);
    }

    @Override
    public PmsPaybackBatch getNextPendingBatch(Long productId) {
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();
        example.createCriteria()
                .andProductIdEqualTo(productId)
                .andBatchStatusEqualTo(BATCH_STATUS_PENDING);
        example.setOrderByClause("batch_no ASC");
        List<PmsPaybackBatch> batches = batchMapper.selectByExample(example);
        return batches.isEmpty() ? null : batches.get(0);
    }

    @Override
    public void onBatchCompleted(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null || batch.getBatchStatus() != BATCH_STATUS_ACTIVE) {
            return;
        }

        // 1. 更新批次状态为已回本
        PmsPaybackBatch updateBatch = new PmsPaybackBatch();
        updateBatch.setId(batchId);
        updateBatch.setBatchStatus(BATCH_STATUS_COMPLETED);
        updateBatch.setCompletedDate(new Date());
        updateBatch.setUpdatedAt(new Date());
        batchMapper.updateByPrimaryKeySelective(updateBatch);

        LOGGER.info("批次回本完成，批次ID: {}", batchId);

        // 2. 自动启动下一个待启动批次
        activateNextPendingBatch(batch.getProductId());
    }

    @Override
    public PmsPaybackBatch getBatchById(Long batchId) {
        return batchMapper.selectByPrimaryKey(batchId);
    }

    @Override
    public List<PmsPaybackBatch> listBatches(PaybackBatchQueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        
        PmsPaybackBatchExample example = buildQueryExample(request);
        example.setOrderByClause("created_at DESC");
        
        return batchMapper.selectByExample(example);
    }

    @Override
    public long countBatches(PaybackBatchQueryRequest request) {
        PmsPaybackBatchExample example = buildQueryExample(request);
        return batchMapper.countByExample(example);
    }

    @Override
    public int getMaxBatchNo(Long productId) {
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();
        example.createCriteria().andProductIdEqualTo(productId);
        example.setOrderByClause("batch_no DESC");
        List<PmsPaybackBatch> batches = batchMapper.selectByExample(example);
        return batches.isEmpty() ? 0 : batches.get(0).getBatchNo();
    }

    @Override
    public boolean canDeleteBatch(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        return batch != null;
    }

    @Override
    public boolean hasSalesRecords(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return false;
        }
        return batch.getCurrentSoldQuantity() != null && batch.getCurrentSoldQuantity() > 0;
    }

    @Override
    public PmsPaybackBatch getLastBatch(Long productId) {
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();
        example.createCriteria().andProductIdEqualTo(productId);
        example.setOrderByClause("batch_no DESC");
        List<PmsPaybackBatch> batches = batchMapper.selectByExample(example);
        return batches.isEmpty() ? null : batches.get(0);
    }

    /**
     * 激活下一个待启动批次
     */
    private void activateNextPendingBatch(Long productId) {
        PmsPaybackBatch nextBatch = getNextPendingBatch(productId);
        if (nextBatch != null) {
            PmsPaybackBatch updateNext = new PmsPaybackBatch();
            updateNext.setId(nextBatch.getId());
            updateNext.setBatchStatus(BATCH_STATUS_ACTIVE);
            updateNext.setStartDate(new Date());
            updateNext.setUpdatedAt(new Date());
            batchMapper.updateByPrimaryKeySelective(updateNext);
            LOGGER.info("自动启动下一个批次，批次ID: {}", nextBatch.getId());
        }
    }

    /**
     * 构建查询条件
     * 关键词搜索：商品名称 OR 货号（模糊匹配）
     * 其他条件：AND 关系
     */
    private PmsPaybackBatchExample buildQueryExample(PaybackBatchQueryRequest request) {
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();

        if (StringUtils.hasText(request.getKeyword())) {
            // 关键词搜索需要 OR 逻辑：(productName LIKE ? OR productSn LIKE ?) AND 其他条件
            // 创建两个 criteria，分别匹配商品名称和货号
            PmsPaybackBatchExample.Criteria criteria1 = example.createCriteria();
            criteria1.andProductNameLike("%" + request.getKeyword() + "%");
            addCommonCriteria(criteria1, request);

            PmsPaybackBatchExample.Criteria criteria2 = example.or();
            criteria2.andProductSnLike("%" + request.getKeyword() + "%");
            addCommonCriteria(criteria2, request);
        } else {
            // 无关键词时，只需要一个 criteria
            PmsPaybackBatchExample.Criteria criteria = example.createCriteria();
            addCommonCriteria(criteria, request);
        }

        return example;
    }

    /**
     * 添加通用查询条件
     */
    private void addCommonCriteria(PmsPaybackBatchExample.Criteria criteria, PaybackBatchQueryRequest request) {
        if (request.getBatchStatus() != null) {
            criteria.andBatchStatusEqualTo(request.getBatchStatus().byteValue());
        }

        if (request.getStartDate() != null) {
            criteria.andReplenishmentDateGreaterThanOrEqualTo(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            criteria.andReplenishmentDateLessThanOrEqualTo(request.getEndDate());
        }

        if (request.getProductId() != null) {
            criteria.andProductIdEqualTo(request.getProductId());
        }
    }
}
