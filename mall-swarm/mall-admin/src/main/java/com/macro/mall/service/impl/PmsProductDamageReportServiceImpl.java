package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsProductDamageItemDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductDamageReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 产品报损管理Service实现类
 */
@Service
public class PmsProductDamageReportServiceImpl implements PmsProductDamageReportService {
    
    @Autowired
    private PmsProductDamageReportMapper damageReportMapper;
    
    @Autowired
    private PmsProductDamageItemDao damageItemDao;
    
    @Autowired
    private PmsProductDamageLogMapper damageLogMapper;
    
    @Autowired
    private PmsProductDamageReasonMapper damageReasonMapper;
    
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    
    @Override
    @Transactional
    public int create(PmsProductDamageReportParam param, Long adminId, String adminName) {
        // 校验明细
        if (CollectionUtils.isEmpty(param.getItems())) {
            throw new RuntimeException("报损商品明细不能为空");
        }
        
        // 创建主表记录
        PmsProductDamageReport report = new PmsProductDamageReport();
        BeanUtils.copyProperties(param, report);
        
        // 生成报损单号
        report.setReportSn(generateReportSn());
        
        // 设置门店名称
        if (param.getStoreId() != null) {
            OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(param.getStoreId());
            if (store != null) {
                report.setStoreName(store.getAddressName());
            }
        }
        
        // 设置报损原因名称
        if (param.getDamageReasonId() != null) {
            PmsProductDamageReason reason = damageReasonMapper.selectByPrimaryKey(param.getDamageReasonId());
            if (reason != null) {
                report.setDamageReason(reason.getName());
            }
        }
        
        // 计算汇总数据
        int totalQuantity = 0;
        BigDecimal totalDamageAmount = BigDecimal.ZERO;
        BigDecimal totalSalesAmount = BigDecimal.ZERO;
        
        for (PmsProductDamageItemParam item : param.getItems()) {
            int qty = item.getDamageQuantity() != null ? item.getDamageQuantity() : 1;
            totalQuantity += qty;
            
            BigDecimal costPrice = item.getCostPrice() != null ? item.getCostPrice() : BigDecimal.ZERO;
            BigDecimal salePrice = item.getSalePrice() != null ? item.getSalePrice() : BigDecimal.ZERO;
            
            totalDamageAmount = totalDamageAmount.add(costPrice.multiply(new BigDecimal(qty)));
            totalSalesAmount = totalSalesAmount.add(salePrice.multiply(new BigDecimal(qty)));
        }
        
        report.setTotalQuantity(totalQuantity);
        report.setTotalDamageAmount(totalDamageAmount);
        report.setTotalSalesAmount(totalSalesAmount);
        
        // 设置提交人信息
        report.setSubmitAdminId(adminId);
        report.setSubmitAdminName(adminName);
        report.setStatus((byte) 0); // 待处理
        report.setDeleteStatus((byte) 0);
        report.setDamageType(param.getDamageType() != null ? param.getDamageType().byteValue() : null);
        report.setCreateTime(new Date());
        report.setUpdateTime(new Date());
        
        int count = damageReportMapper.insertSelective(report);
        
        if (count > 0) {
            // 插入明细记录
            Date now = new Date();
            List<PmsProductDamageItem> items = new ArrayList<>();
            for (PmsProductDamageItemParam itemParam : param.getItems()) {
                PmsProductDamageItem item = new PmsProductDamageItem();
                BeanUtils.copyProperties(itemParam, item);
                item.setDamageReportId(report.getId());
                
                // 计算明细金额
                int qty = itemParam.getDamageQuantity() != null ? itemParam.getDamageQuantity() : 1;
                BigDecimal costPrice = itemParam.getCostPrice() != null ? itemParam.getCostPrice() : BigDecimal.ZERO;
                BigDecimal salePrice = itemParam.getSalePrice() != null ? itemParam.getSalePrice() : BigDecimal.ZERO;
                
                item.setDamageAmount(costPrice.multiply(new BigDecimal(qty)));
                item.setSalesAmount(salePrice.multiply(new BigDecimal(qty)));
                item.setCreateTime(now);
                item.setUpdateTime(now);
                
                items.add(item);
            }
            
            if (!items.isEmpty()) {
                damageItemDao.batchInsert(items);
            }
            
            // 记录操作日志
            addLog(report.getId(), (byte) 1, "提交报损申请，共" + totalQuantity + "件商品", null, (byte) 0, adminId, adminName);
        }
        
        return count;
    }

    @Override
    public List<PmsProductDamageReport> list(PmsProductDamageReportQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductDamageReportExample example = new PmsProductDamageReportExample();
        PmsProductDamageReportExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo((byte) 0);
        
        if (StringUtils.hasText(queryParam.getReportSn())) {
            criteria.andReportSnLike("%" + queryParam.getReportSn() + "%");
        }
        if (queryParam.getStoreId() != null) {
            criteria.andStoreIdEqualTo(queryParam.getStoreId());
        }
        if (queryParam.getDamageType() != null) {
            criteria.andDamageTypeEqualTo(queryParam.getDamageType().byteValue());
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus().byteValue());
        }
        if (queryParam.getSubmitAdminId() != null) {
            criteria.andSubmitAdminIdEqualTo(queryParam.getSubmitAdminId());
        }
        if (queryParam.getHandleAdminId() != null) {
            criteria.andHandleAdminIdEqualTo(queryParam.getHandleAdminId());
        }
        if (queryParam.getStartTime() != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(queryParam.getStartTime());
        }
        if (queryParam.getEndTime() != null) {
            criteria.andCreateTimeLessThanOrEqualTo(queryParam.getEndTime());
        }
        
        example.setOrderByClause("create_time desc");
        return damageReportMapper.selectByExampleWithBLOBs(example);
    }
    
    @Override
    public PmsProductDamageReportResult getDetail(Long id) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null) {
            return null;
        }
        
        PmsProductDamageReportResult result = new PmsProductDamageReportResult();
        BeanUtils.copyProperties(report, result);
        
        // 获取报损明细
        List<PmsProductDamageItem> items = damageItemDao.selectByDamageReportId(id);
        result.setItems(items);
        
        // 获取处理日志
        PmsProductDamageLogExample logExample = new PmsProductDamageLogExample();
        logExample.createCriteria().andDamageReportIdEqualTo(id);
        logExample.setOrderByClause("create_time asc");
        result.setLogList(damageLogMapper.selectByExample(logExample));
        
        return result;
    }
    
    @Override
    @Transactional
    public int startHandle(Long id, Long adminId, String adminName) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null || report.getStatus() != 0) {
            return 0;
        }
        
        PmsProductDamageReport updateReport = new PmsProductDamageReport();
        updateReport.setId(id);
        updateReport.setStatus((byte) 1); // 处理中
        updateReport.setHandleAdminId(adminId);
        updateReport.setHandleAdminName(adminName);
        updateReport.setHandleTime(new Date());
        updateReport.setUpdateTime(new Date());
        
        int count = damageReportMapper.updateByPrimaryKeySelective(updateReport);
        
        if (count > 0) {
            addLog(id, (byte) 2, "开始处理报损", (byte) 0, (byte) 1, adminId, adminName);
        }
        
        return count;
    }
    
    @Override
    @Transactional
    public int updateHandle(Long id, PmsProductDamageHandleParam param, Long adminId, String adminName) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null) {
            return 0;
        }
        
        PmsProductDamageReport updateReport = new PmsProductDamageReport();
        updateReport.setId(id);
        updateReport.setHandleMethod(param.getHandleMethod() != null ? param.getHandleMethod().byteValue() : null);
        updateReport.setHandleDescription(param.getHandleDescription());
        updateReport.setHandlePics(param.getHandlePics());
        updateReport.setSupplierFeedback(param.getSupplierFeedback());
        updateReport.setReshipmentSn(param.getReshipmentSn());
        updateReport.setReshipmentTime(param.getReshipmentTime());
        updateReport.setUpdateTime(new Date());
        
        // 如果有重新发货单号，状态改为待验收
        if (StringUtils.hasText(param.getReshipmentSn())) {
            updateReport.setStatus((byte) 2); // 待验收
            addLog(id, (byte) 4, "厂家已发货，单号：" + param.getReshipmentSn(), report.getStatus(), (byte) 2, adminId, adminName);
        } else {
            addLog(id, (byte) 3, "更新处理信息", null, null, adminId, adminName);
        }
        
        if (StringUtils.hasText(param.getRemark())) {
            updateReport.setRemark(param.getRemark());
        }
        
        return damageReportMapper.updateByPrimaryKeySelective(updateReport);
    }

    @Override
    @Transactional
    public int acceptance(Long id, Integer acceptanceStatus, String acceptanceRemark, Long adminId, String adminName) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null || report.getStatus() != 2) {
            return 0;
        }
        
        PmsProductDamageReport updateReport = new PmsProductDamageReport();
        updateReport.setId(id);
        updateReport.setAcceptanceStatus(acceptanceStatus.byteValue());
        updateReport.setAcceptanceTime(new Date());
        updateReport.setAcceptanceRemark(acceptanceRemark);
        updateReport.setUpdateTime(new Date());
        
        // 验收通过则完成，不通过则继续处理
        if (acceptanceStatus == 1) {
            updateReport.setStatus((byte) 3); // 已完成
            updateReport.setCompleteTime(new Date());
            addLog(id, (byte) 5, "验收通过：" + (acceptanceRemark != null ? acceptanceRemark : ""), (byte) 2, (byte) 3, adminId, adminName);
        } else {
            updateReport.setStatus((byte) 1); // 处理中
            addLog(id, (byte) 5, "验收不通过：" + (acceptanceRemark != null ? acceptanceRemark : ""), (byte) 2, (byte) 1, adminId, adminName);
        }
        
        return damageReportMapper.updateByPrimaryKeySelective(updateReport);
    }
    
    @Override
    @Transactional
    public int complete(Long id, Long adminId, String adminName) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null || report.getStatus() == 3 || report.getStatus() == 4) {
            return 0;
        }
        
        PmsProductDamageReport updateReport = new PmsProductDamageReport();
        updateReport.setId(id);
        updateReport.setStatus((byte) 3); // 已完成
        updateReport.setCompleteTime(new Date());
        updateReport.setUpdateTime(new Date());
        
        int count = damageReportMapper.updateByPrimaryKeySelective(updateReport);
        
        if (count > 0) {
            addLog(id, (byte) 6, "完成处理", report.getStatus(), (byte) 3, adminId, adminName);
        }
        
        return count;
    }
    
    @Override
    @Transactional
    public int close(Long id, String remark, Long adminId, String adminName) {
        PmsProductDamageReport report = damageReportMapper.selectByPrimaryKey(id);
        if (report == null || report.getStatus() == 3 || report.getStatus() == 4) {
            return 0;
        }
        
        PmsProductDamageReport updateReport = new PmsProductDamageReport();
        updateReport.setId(id);
        updateReport.setStatus((byte) 4); // 已关闭
        updateReport.setRemark(remark);
        updateReport.setUpdateTime(new Date());
        
        int count = damageReportMapper.updateByPrimaryKeySelective(updateReport);
        
        if (count > 0) {
            addLog(id, (byte) 7, "关闭报损：" + (remark != null ? remark : ""), report.getStatus(), (byte) 4, adminId, adminName);
        }
        
        return count;
    }
    
    @Override
    public int delete(List<Long> ids) {
        PmsProductDamageReport report = new PmsProductDamageReport();
        report.setDeleteStatus((byte) 1);
        report.setUpdateTime(new Date());
        
        PmsProductDamageReportExample example = new PmsProductDamageReportExample();
        example.createCriteria().andIdIn(ids);
        return damageReportMapper.updateByExampleSelective(report, example);
    }
    
    @Override
    public Integer getPendingCount() {
        PmsProductDamageReportExample example = new PmsProductDamageReportExample();
        example.createCriteria()
                .andDeleteStatusEqualTo((byte) 0)
                .andStatusEqualTo((byte) 0);
        return (int) damageReportMapper.countByExample(example);
    }
    
    @Override
    public List<PmsProductDamageStatistics> statisticsByStore(String startDate, String endDate) {
        List<PmsProductDamageStatistics> result = new ArrayList<>();
        
        PmsProductDamageReportExample example = new PmsProductDamageReportExample();
        PmsProductDamageReportExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo((byte) 0);
        
        List<PmsProductDamageReport> reports = damageReportMapper.selectByExample(example);
        
        // 按门店分组统计
        Map<String, PmsProductDamageStatistics> storeMap = new HashMap<>();
        for (PmsProductDamageReport report : reports) {
            String storeName = report.getStoreName() != null ? report.getStoreName() : "未知门店";
            PmsProductDamageStatistics stats = storeMap.computeIfAbsent(storeName, k -> {
                PmsProductDamageStatistics s = new PmsProductDamageStatistics();
                s.setDimension(k);
                s.setDamageCount(0);
                s.setDamageAmount(BigDecimal.ZERO);
                s.setSalesAmount(BigDecimal.ZERO);
                s.setPendingCount(0);
                s.setProcessingCount(0);
                s.setCompletedCount(0);
                return s;
            });
            
            Integer qty = report.getTotalQuantity() != null ? report.getTotalQuantity() : 0;
            stats.setDamageCount(stats.getDamageCount() + qty);
            if (report.getTotalDamageAmount() != null) {
                stats.setDamageAmount(stats.getDamageAmount().add(report.getTotalDamageAmount()));
            }
            if (report.getTotalSalesAmount() != null) {
                stats.setSalesAmount(stats.getSalesAmount().add(report.getTotalSalesAmount()));
            }
            
            if (report.getStatus() == 0) {
                stats.setPendingCount(stats.getPendingCount() + 1);
            } else if (report.getStatus() == 1 || report.getStatus() == 2) {
                stats.setProcessingCount(stats.getProcessingCount() + 1);
            } else if (report.getStatus() == 3) {
                stats.setCompletedCount(stats.getCompletedCount() + 1);
            }
        }
        
        result.addAll(storeMap.values());
        return result;
    }
    
    @Override
    public List<PmsProductDamageStatistics> statisticsByType(String startDate, String endDate) {
        List<PmsProductDamageStatistics> result = new ArrayList<>();
        String[] typeNames = {"", "到货瑕疵", "外借损坏", "保存不当", "人为原因", "其他"};
        
        for (int i = 1; i <= 5; i++) {
            PmsProductDamageReportExample example = new PmsProductDamageReportExample();
            example.createCriteria()
                    .andDeleteStatusEqualTo((byte) 0)
                    .andDamageTypeEqualTo((byte) i);
            
            List<PmsProductDamageReport> reports = damageReportMapper.selectByExample(example);
            
            PmsProductDamageStatistics stats = new PmsProductDamageStatistics();
            stats.setDimension(typeNames[i]);
            stats.setDamageCount(0);
            stats.setDamageAmount(BigDecimal.ZERO);
            stats.setSalesAmount(BigDecimal.ZERO);
            
            for (PmsProductDamageReport report : reports) {
                Integer qty = report.getTotalQuantity() != null ? report.getTotalQuantity() : 0;
                stats.setDamageCount(stats.getDamageCount() + qty);
                if (report.getTotalDamageAmount() != null) {
                    stats.setDamageAmount(stats.getDamageAmount().add(report.getTotalDamageAmount()));
                }
                if (report.getTotalSalesAmount() != null) {
                    stats.setSalesAmount(stats.getSalesAmount().add(report.getTotalSalesAmount()));
                }
            }
            
            result.add(stats);
        }
        
        return result;
    }
    
    @Override
    public List<PmsProductDamageStatistics> statisticsByTime(String startDate, String endDate, String timeUnit) {
        return new ArrayList<>();
    }
    
    /**
     * 生成报损单号
     */
    private String generateReportSn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "DMG" + sdf.format(new Date()) + String.format("%04d", new Random().nextInt(10000));
    }
    
    /**
     * 添加操作日志
     */
    private void addLog(Long reportId, Byte actionType, String actionContent, Byte beforeStatus, Byte afterStatus, Long operatorId, String operatorName) {
        PmsProductDamageLog log = new PmsProductDamageLog();
        log.setDamageReportId(reportId);
        log.setActionType(actionType);
        log.setActionContent(actionContent);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setCreateTime(new Date());
        damageLogMapper.insertSelective(log);
    }
}
