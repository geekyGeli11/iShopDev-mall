package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.*;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.*;
import org.springframework.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsMemberPriceDao memberPriceDao;
    @Autowired
    private PmsMemberPriceMapper memberPriceMapper;
    @Autowired
    private PmsProductLadderDao productLadderDao;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionDao productFullReductionDao;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductAttributeValueDao productAttributeValueDao;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private CmsSubjectProductRelationDao subjectProductRelationDao;
    @Autowired
    private CmsSubjectProductRelationMapper subjectProductRelationMapper;
    @Autowired
    private CmsPrefrenceAreaProductRelationDao prefrenceAreaProductRelationDao;
    @Autowired
    private CmsPrefrenceAreaProductRelationMapper prefrenceAreaProductRelationMapper;
    @Autowired
    private PmsProductDao productDao;
    @Autowired
    private PmsProductVertifyRecordDao productVertifyRecordDao;
    @Autowired
    private PmsProductPaybackService paybackService;
    @Autowired
    private SmsFreightTemplateService freightTemplateService;
    @Autowired
    private PmsStoreSkuStockService storeSkuStockService;
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    @Autowired
    private PmsDiyTemplateService diyTemplateService;
    @Autowired
    private PmsProductSchoolRelationService productSchoolRelationService;
    @Autowired
    private PmsBrandService brandService;
    @Autowired
    private PmsProductCategoryService productCategoryService;
    @Autowired
    private WechatMiniProgramService wechatMiniProgramService;

    @Override
    public int create(PmsProductParam productParam) {
        int count;
        //创建商品
        PmsProduct product = productParam;
        product.setId(null);
        
        // 如果没有选择运费模板，自动关联默认模板
        if (product.getFreightTemplateId() == null) {
            try {
                SmsFreightTemplate defaultTemplate = freightTemplateService.getDefaultTemplate();
                if (defaultTemplate != null) {
                    product.setFreightTemplateId(defaultTemplate.getId());
                    LOGGER.info("商品未设置运费模板，自动关联默认模板：{}", defaultTemplate.getName());
                } else {
                    LOGGER.warn("未找到默认运费模板，商品将不关联运费模板");
                }
            } catch (Exception e) {
                LOGGER.error("获取默认运费模板失败", e);
            }
        }
        
        productMapper.insertSelective(product);
        //根据促销类型设置价格：会员价格、阶梯价格、满减价格
        Long productId = product.getId();
        //会员价格
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), productId);
        //阶梯价格
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), productId);
        //满减价格
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), productId);
        //处理sku的编码
        handleSkuStockCode(productParam.getSkuStockList(),productId);
        //添加sku库存信息
        relateAndInsertList(skuStockDao, productParam.getSkuStockList(), productId);
        //添加商品参数,添加自定义商品规格
        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), productId);
        //关联专题
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), productId);
        //关联优选
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), productId);
        //关联学校
        if (productParam.getSchoolIds() != null && !productParam.getSchoolIds().isEmpty()) {
            productSchoolRelationService.updateProductSchools(productId, productParam.getSchoolIds());
        }

        // 如果启用了回本分析，创建回本分析记录
        handlePaybackAnalysisOnCreate(productId, productParam);
        
        // 处理门店库存初始化 - 如果商品有SKU且存在门店，自动创建门店库存记录（库存为0）
        handleStoreSkuStockOnCreate(productId, productParam);
        
        count = 1;
        return count;
    }

    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId) {
        if(CollectionUtils.isEmpty(skuStockList))return;
        for(int i=0;i<skuStockList.size();i++){
            PmsSkuStock skuStock = skuStockList.get(i);
            if(!StringUtils.hasText(skuStock.getSkuCode())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                StringBuilder sb = new StringBuilder();
                //日期
                sb.append(sdf.format(new Date()));
                //四位商品id
                sb.append(String.format("%04d", productId));
                //三位索引id
                sb.append(String.format("%03d", i+1));
                skuStock.setSkuCode(sb.toString());
            }
        }
    }

    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }

    @Override
    public int update(Long id, PmsProductParam productParam) {
        int count;
        //更新商品信息
        PmsProduct product = productParam;
        product.setId(id);
        
        // 如果没有选择运费模板，自动关联默认模板
        if (product.getFreightTemplateId() == null) {
            try {
                SmsFreightTemplate defaultTemplate = freightTemplateService.getDefaultTemplate();
                if (defaultTemplate != null) {
                    product.setFreightTemplateId(defaultTemplate.getId());
                    LOGGER.info("商品更新时未设置运费模板，自动关联默认模板：{}", defaultTemplate.getName());
                } else {
                    LOGGER.warn("未找到默认运费模板，商品运费模板将保持为空");
                }
            } catch (Exception e) {
                LOGGER.error("获取默认运费模板失败", e);
            }
        }
        
        productMapper.updateByPrimaryKeySelective(product);
        //会员价格
        PmsMemberPriceExample pmsMemberPriceExample = new PmsMemberPriceExample();
        pmsMemberPriceExample.createCriteria().andProductIdEqualTo(id);
        memberPriceMapper.deleteByExample(pmsMemberPriceExample);
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), id);
        //阶梯价格
        PmsProductLadderExample ladderExample = new PmsProductLadderExample();
        ladderExample.createCriteria().andProductIdEqualTo(id);
        productLadderMapper.deleteByExample(ladderExample);
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), id);
        //满减价格
        PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
        fullReductionExample.createCriteria().andProductIdEqualTo(id);
        productFullReductionMapper.deleteByExample(fullReductionExample);
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), id);
        //修改sku库存信息
        handleUpdateSkuStockList(id, productParam);
        //修改商品参数,添加自定义商品规格
        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
        productAttributeValueExample.createCriteria().andProductIdEqualTo(id);
        productAttributeValueMapper.deleteByExample(productAttributeValueExample);
        
        // 过滤掉空值的属性参数
        List<PmsProductAttributeValue> filteredAttributeValueList = new ArrayList<>();
        if (productParam.getProductAttributeValueList() != null) {
            for (PmsProductAttributeValue attrValue : productParam.getProductAttributeValueList()) {
                if (attrValue != null && attrValue.getValue() != null && !attrValue.getValue().trim().isEmpty()) {
                    filteredAttributeValueList.add(attrValue);
                }
            }
        }
        
        relateAndInsertList(productAttributeValueDao, filteredAttributeValueList, id);
        //关联专题
        CmsSubjectProductRelationExample subjectProductRelationExample = new CmsSubjectProductRelationExample();
        subjectProductRelationExample.createCriteria().andProductIdEqualTo(id);
        subjectProductRelationMapper.deleteByExample(subjectProductRelationExample);
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), id);
        //关联优选
        CmsPrefrenceAreaProductRelationExample prefrenceAreaExample = new CmsPrefrenceAreaProductRelationExample();
        prefrenceAreaExample.createCriteria().andProductIdEqualTo(id);
        prefrenceAreaProductRelationMapper.deleteByExample(prefrenceAreaExample);
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), id);
        //更新学校关联
        productSchoolRelationService.updateProductSchools(id, productParam.getSchoolIds());

        // 处理回本分析记录的更新
        handlePaybackAnalysisOnUpdate(id, productParam);
        
        // 处理门店库存同步 - 同步SKU变更到门店库存表
        handleStoreSkuStockOnUpdate(id, productParam);
        
        count = 1;
        return count;
    }

    private void handleUpdateSkuStockList(Long id, PmsProductParam productParam) {
        //当前的sku信息
        List<PmsSkuStock> currSkuList = productParam.getSkuStockList();
        //当前没有sku直接删除
        if(CollUtil.isEmpty(currSkuList)){
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(id);
            skuStockMapper.deleteByExample(skuStockExample);
            return;
        }
        //获取初始sku信息
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(id);
        List<PmsSkuStock> oriStuList = skuStockMapper.selectByExample(skuStockExample);
        //获取新增sku信息
        List<PmsSkuStock> insertSkuList = currSkuList.stream().filter(item->item.getId()==null).collect(Collectors.toList());
        //获取需要更新的sku信息
        List<PmsSkuStock> updateSkuList = currSkuList.stream().filter(item->item.getId()!=null).collect(Collectors.toList());
        List<Long> updateSkuIds = updateSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        //获取需要删除的sku信息
        List<PmsSkuStock> removeSkuList = oriStuList.stream().filter(item-> !updateSkuIds.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(insertSkuList,id);
        handleSkuStockCode(updateSkuList,id);
        //新增sku
        if(CollUtil.isNotEmpty(insertSkuList)){
            relateAndInsertList(skuStockDao, insertSkuList, id);
        }
        //删除sku
        if(CollUtil.isNotEmpty(removeSkuList)){
            List<Long> removeSkuIds = removeSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsSkuStockExample removeExample = new PmsSkuStockExample();
            removeExample.createCriteria().andIdIn(removeSkuIds);
            skuStockMapper.deleteByExample(removeExample);
        }
        //修改sku
        if(CollUtil.isNotEmpty(updateSkuList)){
            for (PmsSkuStock pmsSkuStock : updateSkuList) {
                skuStockMapper.updateByPrimaryKeySelective(pmsSkuStock);
            }
        }

    }

    @Override
    public List<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        // 如果有学校筛选条件，优先使用学校筛选（学校的商品一定属于其下的门店）
        if (productQueryParam.getSchoolId() != null) {
            // 先获取所有符合条件的商品（不分页）
            PmsProductExample productExample = new PmsProductExample();
            PmsProductExample.Criteria criteria = productExample.createCriteria();
            criteria.andDeleteStatusEqualTo(0);
            if (productQueryParam.getPublishStatus() != null) {
                criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
            }
            if (productQueryParam.getVerifyStatus() != null) {
                criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
            }
            if (StringUtils.hasText(productQueryParam.getKeyword())) {
                criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
            }
            if (StringUtils.hasText(productQueryParam.getProductSn())) {
                criteria.andProductSnEqualTo(productQueryParam.getProductSn());
            }
            if (productQueryParam.getBrandId() != null) {
                criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
            }
            if (productQueryParam.getProductCategoryId() != null) {
                criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
            }
            if (productQueryParam.getDiyEnabled() != null) {
                criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
            }

            List<PmsProduct> allProducts = productMapper.selectByExample(productExample);

            // 过滤出关联指定学校的商品
            List<PmsProduct> filteredProducts = allProducts.stream()
                    .filter(product -> {
                        try {
                            return productSchoolRelationService.isProductAssociatedWithSchool(product.getId(), productQueryParam.getSchoolId());
                        } catch (Exception e) {
                            LOGGER.warn("检查商品学校关联失败，商品ID：{}，学校ID：{}", product.getId(), productQueryParam.getSchoolId());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            // 手动分页
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, filteredProducts.size());
            List<PmsProduct> pagedProducts = start < filteredProducts.size() ?
                    filteredProducts.subList(start, end) : new ArrayList<>();

            // 为每个商品计算SKU库存总和，替换商品本身的库存值
            calculateAndSetSkuStockSum(pagedProducts);
            
            // 创建 Page 对象来包装结果，以便 Controller 能获取正确的总数
            Page<PmsProduct> page = new Page<>();
            page.addAll(pagedProducts);
            page.setTotal(filteredProducts.size());
            page.setPageNum(pageNum);
            page.setPageSize(pageSize);
            
            return page;
        }
        
        // 如果只指定了门店ID（没有学校ID），则使用门店筛选查询
        if (productQueryParam.getStoreId() != null) {
            PageHelper.startPage(pageNum, pageSize);
            return listByStoreId(productQueryParam, productQueryParam.getStoreId());
        }
        
        // 普通查询
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            criteria.andProductSnEqualTo(productQueryParam.getProductSn());
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        if (productQueryParam.getDiyEnabled() != null) {
            criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
        }

        List<PmsProduct> products = productMapper.selectByExample(productExample);
        
        // 为每个商品计算SKU库存总和，替换商品本身的库存值
        calculateAndSetSkuStockSum(products);
        
        return products;
    }
    
    /**
     * 根据门店ID查询商品列表（只返回该门店有库存的商品）
     */
    private List<PmsProduct> listByStoreId(PmsProductQueryParam productQueryParam, Long storeId) {
        // 先获取该门店的所有库存记录
        PmsStoreSkuStockExample storeStockExample = new PmsStoreSkuStockExample();
        PmsStoreSkuStockExample.Criteria storeStockCriteria = storeStockExample.createCriteria();
        storeStockCriteria.andStoreIdEqualTo(storeId);
        // 可以选择性地只查询有库存的SKU
        // storeStockCriteria.andStockGreaterThan(0);
        
        List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(storeStockExample);
        
        if (CollectionUtils.isEmpty(storeStocks)) {
            return new ArrayList<>();
        }
        
        // 提取商品ID列表
        List<Long> productIds = storeStocks.stream()
                .map(PmsStoreSkuStock::getProductId)
                .distinct()
                .collect(Collectors.toList());
        
        // 构建商品查询条件
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        criteria.andIdIn(productIds);
        
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            criteria.andProductSnEqualTo(productQueryParam.getProductSn());
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        
        List<PmsProduct> products = productMapper.selectByExample(productExample);
        
        // 为每个商品设置门店库存信息，将门店SKU库存总和替换商品本身的库存值
        for (PmsProduct product : products) {
            List<PmsStoreSkuStock> productStoreStocks = storeStocks.stream()
                    .filter(stock -> stock.getProductId().equals(product.getId()))
                    .collect(Collectors.toList());
            
            // 计算总的门店库存
            int totalStoreStock = productStoreStocks.stream()
                    .mapToInt(stock -> stock.getStock() != null ? stock.getStock() : 0)
                    .sum();
            
            // 将门店库存总和设置为商品的库存值
            product.setStock(totalStoreStock);
            
            LOGGER.debug("门店商品库存计算 - 商品ID: {}, 商品名称: {}, 门店ID: {}, 计算库存总和: {}", 
                product.getId(), product.getName(), storeId, totalStoreStock);
        }
        
        return products;
    }

    /**
     * 计算并设置商品的SKU库存总和
     * 用于普通查询时替换商品本身的库存值
     */
    private void calculateAndSetSkuStockSum(List<PmsProduct> products) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        
        for (PmsProduct product : products) {
            try {
                // 查询该商品的所有SKU库存
                PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
                PmsSkuStockExample.Criteria criteria = skuStockExample.createCriteria();
                criteria.andProductIdEqualTo(product.getId());
                
                List<PmsSkuStock> skuStocks = skuStockMapper.selectByExample(skuStockExample);
                
                // 计算SKU库存总和
                int totalSkuStock = skuStocks.stream()
                        .mapToInt(sku -> sku.getStock() != null ? sku.getStock() : 0)
                        .sum();
                
                // 将SKU库存总和设置为商品的库存值
                product.setStock(totalSkuStock);
                
                LOGGER.debug("商品SKU库存计算 - 商品ID: {}, 商品名称: {}, SKU数量: {}, 计算库存总和: {}", 
                    product.getId(), product.getName(), skuStocks.size(), totalSkuStock);
                    
            } catch (Exception e) {
                LOGGER.error("计算商品SKU库存总和失败 - 商品ID: {}, 商品名称: {}, 错误: {}", 
                    product.getId(), product.getName(), e.getMessage(), e);
                // 发生异常时保持原库存值不变
            }
        }
    }

    @Override
    public int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail) {
        PmsProduct product = new PmsProduct();
        product.setVerifyStatus(verifyStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        List<PmsProductVertifyRecord> list = new ArrayList<>();
        int count = productMapper.updateByExampleSelective(product, example);
        //修改完审核状态后插入审核记录
        for (Long id : ids) {
            PmsProductVertifyRecord record = new PmsProductVertifyRecord();
            record.setProductId(id);
            record.setCreateTime(new Date());
            record.setDetail(detail);
            record.setStatus(verifyStatus);
            record.setVertifyMan("test");
            list.add(record);
        }
        productVertifyRecordDao.insertList(list);
        return count;
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        PmsProduct record = new PmsProduct();
        record.setPublishStatus(publishStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        PmsProduct record = new PmsProduct();
        record.setRecommandStatus(recommendStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        PmsProduct record = new PmsProduct();
        record.setNewStatus(newStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        PmsProduct record = new PmsProduct();
        record.setDeleteStatus(deleteStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<PmsProduct> list(String keyword) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if(StringUtils.hasText(keyword)){
            criteria.andNameLike("%" + keyword + "%");
            productExample.or().andDeleteStatusEqualTo(0).andProductSnLike("%" + keyword + "%");
        }
        
        List<PmsProduct> products = productMapper.selectByExample(productExample);
        
        // 为每个商品计算SKU库存总和，替换商品本身的库存值
        calculateAndSetSkuStockSum(products);
        
        return products;
    }

    /**
     * 建立和插入关系表操作
     *
     * @param dao       可以操作的dao
     * @param dataList  要插入的数据
     * @param productId 建立关系的id
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            LOGGER.info("Processing data list of type: {} with size: {}", 
                dataList.get(0).getClass().getSimpleName(), dataList.size());
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            Method insertList = dao.getClass().getMethod("insertList", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            LOGGER.error("创建产品出错 - DAO类型: {}, 数据列表大小: {}, 异常信息: {}", 
                dao.getClass().getSimpleName(), 
                dataList != null ? dataList.size() : 0, 
                e.toString(), e);
            throw new RuntimeException("处理" + dao.getClass().getSimpleName() + "数据时出错: " + e.toString());
        }
    }

    /**
     * 处理商品创建时的回本分析记录
     */
    private void handlePaybackAnalysisOnCreate(Long productId, PmsProductParam productParam) {
        try {
            // 检查是否启用了回本分析并且设置了回本目标
            if (Boolean.TRUE.equals(productParam.getEnablePaybackAnalysis()) && 
                (productParam.getPaybackTargetQuantity() != null || productParam.getPaybackTargetAmount() != null)) {
                
                LOGGER.info("为新商品创建回本分析记录：productId={}", productId);
                
                // 设置开始日期，如果没有设置则使用当前时间
                String startDate = productParam.getPaybackStartDate() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd").format(productParam.getPaybackStartDate()) : 
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
                paybackService.setPaybackTarget(
                    productId, 
                    productParam.getPaybackTargetQuantity(), 
                    productParam.getPaybackTargetAmount(), 
                    startDate
                );
                
                LOGGER.info("商品回本分析记录创建成功：productId={}", productId);
            }
        } catch (Exception e) {
            LOGGER.error("创建商品回本分析记录失败：productId={}", productId, e);
            // 不抛出异常，避免影响商品创建主流程
        }
    }

    /**
     * 处理商品更新时的回本分析记录
     */
    private void handlePaybackAnalysisOnUpdate(Long productId, PmsProductParam productParam) {
        try {
            // 获取原商品信息
            PmsProduct originalProduct = productMapper.selectByPrimaryKey(productId);
            if (originalProduct == null) {
                LOGGER.warn("找不到原商品信息：productId={}", productId);
                return;
            }
            
            boolean currentEnable = Boolean.TRUE.equals(productParam.getEnablePaybackAnalysis());
            boolean originalEnable = Boolean.TRUE.equals(originalProduct.getEnablePaybackAnalysis());
            
            if (currentEnable) {
                // 如果当前启用了回本分析
                if (productParam.getPaybackTargetQuantity() != null || productParam.getPaybackTargetAmount() != null) {
                    LOGGER.info("更新商品回本分析记录：productId={}", productId);
                    
                    // 设置开始日期，如果没有设置则使用当前时间
                    String startDate = productParam.getPaybackStartDate() != null ? 
                        new SimpleDateFormat("yyyy-MM-dd").format(productParam.getPaybackStartDate()) : 
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    
                    paybackService.setPaybackTarget(
                        productId, 
                        productParam.getPaybackTargetQuantity(), 
                        productParam.getPaybackTargetAmount(), 
                        startDate
                    );
                    
                    LOGGER.info("商品回本分析记录更新成功：productId={}", productId);
                }
            } else if (originalEnable) {
                // 如果原来启用了回本分析，现在禁用了，删除回本分析记录
                LOGGER.info("删除商品回本分析记录：productId={}", productId);
                paybackService.deletePaybackAnalysis(productId);
                LOGGER.info("商品回本分析记录删除成功：productId={}", productId);
            }
        } catch (Exception e) {
            LOGGER.error("更新商品回本分析记录失败：productId={}", productId, e);
            // 不抛出异常，避免影响商品更新主流程
        }
    }
    
    /**
     * 处理商品创建时的门店库存初始化
     */
    private void handleStoreSkuStockOnCreate(Long productId, PmsProductParam productParam) {
        try {
            // 检查是否有SKU信息
            if (CollectionUtils.isEmpty(productParam.getSkuStockList())) {
                LOGGER.info("商品无SKU信息，跳过门店库存初始化：productId={}", productId);
                return;
            }
            
            // 为新创建的商品初始化门店库存记录（库存为0，状态启用）
            LOGGER.info("为新商品初始化门店库存：productId={}", productId);
            storeSkuStockService.initStoreSkuStockForProduct(productId);
            LOGGER.info("商品门店库存初始化成功：productId={}", productId);
            
        } catch (Exception e) {
            LOGGER.error("初始化商品门店库存失败：productId={}", productId, e);
            // 不抛出异常，避免影响商品创建主流程
        }
    }
    
    /**
     * 处理商品更新时的门店库存同步
     */
    private void handleStoreSkuStockOnUpdate(Long productId, PmsProductParam productParam) {
        try {
            // 检查是否有SKU信息
            if (CollectionUtils.isEmpty(productParam.getSkuStockList())) {
                LOGGER.info("商品无SKU信息，清理门店库存：productId={}", productId);
                // 删除所有门店库存记录
                storeSkuStockService.deleteStoreSkuStockByProductId(productId);
                return;
            }
            
            LOGGER.info("同步商品门店库存：productId={}", productId);
            
            // 从数据库重新查询当前商品的所有SKU ID（确保包含新增的SKU）
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(productId);
            List<PmsSkuStock> currentSkuList = skuStockMapper.selectByExample(skuStockExample);
            
            List<Long> currentSkuIds = currentSkuList.stream()
                .map(PmsSkuStock::getId)
                .collect(Collectors.toList());
            
            // 同步门店库存：删除不存在的SKU对应的门店库存，为新SKU初始化门店库存
            storeSkuStockService.syncStoreSkuStockForProduct(productId, currentSkuIds);
            
            LOGGER.info("商品门店库存同步成功：productId={}", productId);
            
        } catch (Exception e) {
            LOGGER.error("同步商品门店库存失败：productId={}", productId, e);
            // 不抛出异常，避免影响商品更新主流程
        }
    }

    @Override
    public PmsProduct getDiyConfig(Long id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public PmsProductDiyConfigVO getDiyConfigDetail(Long id) {
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            return null;
        }

        PmsProductDiyConfigVO configVO = new PmsProductDiyConfigVO();
        BeanUtils.copyProperties(product, configVO);

        // 如果关联了DIY模板，获取模板信息
        if (product.getDiyTemplateId() != null) {
            PmsDiyTemplate template = diyTemplateService.getItem(product.getDiyTemplateId());
            configVO.setDiyTemplate(template);
        }

        return configVO;
    }

    @Override
    public int updateDiyConfig(Long id, Byte diyEnabled, Long diyTemplateId) {
        PmsProduct product = new PmsProduct();
        product.setId(id);
        product.setDiyEnabled(diyEnabled);
        product.setDiyTemplateId(diyTemplateId);
        return productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public int updateDiyStatus(List<Long> ids, Byte diyEnabled) {
        PmsProduct product = new PmsProduct();
        product.setDiyEnabled(diyEnabled);

        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(product, example);
    }

    @Override
    public List<PmsProductListVO> listWithDetails(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);

        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            // 扩展搜索：同时搜索商品货号和SKU编码
            List<Long> productIds = productDao.getProductIdsBySnOrSkuCode(productQueryParam.getProductSn());
            if (!CollectionUtils.isEmpty(productIds)) {
                criteria.andIdIn(productIds);
            } else {
                // 如果没有找到匹配的商品，返回空列表
                return new ArrayList<>();
            }
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            // 支持一级分类搜索：如果是一级分类，需要包含其所有子分类
            try {
                PmsProductCategory category = productCategoryService.getItem(productQueryParam.getProductCategoryId());
                LOGGER.info("查询分类信息：分类ID={}，分类名称={}，分类级别={}",
                    productQueryParam.getProductCategoryId(),
                    category != null ? category.getName() : "null",
                    category != null ? category.getLevel() : "null");

                if (category != null && category.getLevel() == 0) {
                    // 一级分类，获取所有子分类ID
                    List<PmsProductCategory> subCategories = productCategoryService.getList(productQueryParam.getProductCategoryId(), null, null);
                    List<Long> categoryIds = new ArrayList<>();
                    categoryIds.add(productQueryParam.getProductCategoryId());
                    if (!CollectionUtils.isEmpty(subCategories)) {
                        categoryIds.addAll(subCategories.stream().map(PmsProductCategory::getId).collect(Collectors.toList()));
                    }
                    LOGGER.info("一级分类搜索，包含分类ID列表：{}", categoryIds);
                    criteria.andProductCategoryIdIn(categoryIds);
                } else {
                    // 二级分类，直接匹配
                    LOGGER.info("二级分类搜索，分类ID：{}", productQueryParam.getProductCategoryId());
                    criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
                }
            } catch (Exception e) {
                LOGGER.warn("获取分类信息失败，分类ID：{}", productQueryParam.getProductCategoryId(), e);
                criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
            }
        }
        if (productQueryParam.getDiyEnabled() != null) {
            criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
        }

        // 如果有学校筛选条件，需要特殊处理
        if (productQueryParam.getSchoolId() != null) {
            // 先获取所有符合条件的商品（不分页）
            List<PmsProduct> allProducts = productMapper.selectByExample(productExample);

            // 过滤出关联指定学校的商品
            List<PmsProduct> filteredProducts = allProducts.stream()
                    .filter(product -> {
                        try {
                            return productSchoolRelationService.isProductAssociatedWithSchool(product.getId(), productQueryParam.getSchoolId());
                        } catch (Exception e) {
                            LOGGER.warn("检查商品学校关联失败，商品ID：{}，学校ID：{}", product.getId(), productQueryParam.getSchoolId());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            // 手动分页
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, filteredProducts.size());
            List<PmsProduct> pagedProducts = start < filteredProducts.size() ?
                    filteredProducts.subList(start, end) : new ArrayList<>();

            // 转换为VO
            return convertToVOList(pagedProducts);
        } else {
            // 没有学校筛选，直接分页查询
            PageHelper.startPage(pageNum, pageSize);
            List<PmsProduct> productList = productMapper.selectByExample(productExample);

            // 转换为VO
            return convertToVOList(productList);
        }
    }

    @Override
    public long countProductsWithDetails(PmsProductQueryParam productQueryParam) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);

        // 应用相同的查询条件
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            List<Long> productIds = productDao.getProductIdsBySnOrSkuCode(productQueryParam.getProductSn());
            if (!CollectionUtils.isEmpty(productIds)) {
                criteria.andIdIn(productIds);
            } else {
                return 0;
            }
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            try {
                PmsProductCategory category = productCategoryService.getItem(productQueryParam.getProductCategoryId());
                if (category != null && category.getLevel() == 0) {
                    List<PmsProductCategory> subCategories = productCategoryService.getList(productQueryParam.getProductCategoryId(), null, null);
                    List<Long> categoryIds = new ArrayList<>();
                    categoryIds.add(productQueryParam.getProductCategoryId());
                    if (!CollectionUtils.isEmpty(subCategories)) {
                        categoryIds.addAll(subCategories.stream().map(PmsProductCategory::getId).collect(Collectors.toList()));
                    }
                    criteria.andProductCategoryIdIn(categoryIds);
                } else {
                    criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
                }
            } catch (Exception e) {
                LOGGER.warn("获取分类信息失败，分类ID：{}", productQueryParam.getProductCategoryId());
                criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
            }
        }
        if (productQueryParam.getDiyEnabled() != null) {
            criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
        }

        // 如果有学校筛选条件，需要特殊处理
        if (productQueryParam.getSchoolId() != null) {
            List<PmsProduct> allProducts = productMapper.selectByExample(productExample);
            return allProducts.stream()
                    .filter(product -> {
                        try {
                            return productSchoolRelationService.isProductAssociatedWithSchool(product.getId(), productQueryParam.getSchoolId());
                        } catch (Exception e) {
                            LOGGER.warn("检查商品学校关联失败，商品ID：{}，学校ID：{}", product.getId(), productQueryParam.getSchoolId());
                            return false;
                        }
                    })
                    .count();
        } else {
            return productMapper.countByExample(productExample);
        }
    }

    /**
     * 将商品列表转换为VO列表，并填充额外信息
     */
    private List<PmsProductListVO> convertToVOList(List<PmsProduct> productList) {
        List<PmsProductListVO> result = new ArrayList<>();
        for (PmsProduct product : productList) {
            PmsProductListVO vo = new PmsProductListVO();
            BeanUtils.copyProperties(product, vo);

            // 填充分类名称（如果为空或需要更新）
            if (product.getProductCategoryId() != null &&
                (product.getProductCategoryName() == null || product.getProductCategoryName().isEmpty())) {
                try {
                    PmsProductCategory category = productCategoryService.getItem(product.getProductCategoryId());
                    if (category != null) {
                        vo.setProductCategoryName(category.getName());
                    }
                } catch (Exception e) {
                    LOGGER.warn("获取商品分类名称失败，商品ID：{}，分类ID：{}", product.getId(), product.getProductCategoryId());
                }
            }

            // 填充运费模板名称
            if (product.getFreightTemplateId() != null) {
                try {
                    SmsFreightTemplateVO template = freightTemplateService.getTemplateDetail(product.getFreightTemplateId());
                    if (template != null) {
                        vo.setFreightTemplateName(template.getName());
                    }
                } catch (Exception e) {
                    LOGGER.warn("获取运费模板名称失败，商品ID：{}，模板ID：{}", product.getId(), product.getFreightTemplateId());
                }
            }

            // 填充关联学校信息
            try {
                List<OmsSchool> schools = productSchoolRelationService.getSchoolsByProductId(product.getId());
                if (!CollectionUtils.isEmpty(schools)) {
                    String schoolNames = schools.stream()
                            .map(OmsSchool::getSchoolName)
                            .collect(Collectors.joining(", "));
                    vo.setSchoolNames(schoolNames);
                    vo.setSchoolCount(schools.size());
                }
            } catch (Exception e) {
                LOGGER.warn("获取商品关联学校失败，商品ID：{}", product.getId());
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public int updateProductSchools(List<Long> productIds, List<Long> schoolIds) {
        if (CollectionUtils.isEmpty(productIds) || CollectionUtils.isEmpty(schoolIds)) {
            return 0;
        }

        int count = 0;
        for (Long productId : productIds) {
            try {
                boolean success = productSchoolRelationService.updateProductSchools(productId, schoolIds);
                if (success) {
                    count++;
                }
            } catch (Exception e) {
                LOGGER.error("批量修改商品学校关联失败，商品ID：{}", productId, e);
            }
        }

        return count;
    }

    @Override
    public int updateProductCategory(List<Long> productIds, Long productCategoryId) {
        if (CollectionUtils.isEmpty(productIds) || productCategoryId == null) {
            return 0;
        }

        // 获取分类名称
        String categoryName = null;
        try {
            PmsProductCategory category = productCategoryService.getItem(productCategoryId);
            if (category != null) {
                categoryName = category.getName();
            }
        } catch (Exception e) {
            LOGGER.warn("获取分类名称失败，分类ID：{}", productCategoryId);
        }

        int count = 0;
        for (Long productId : productIds) {
            try {
                PmsProduct product = new PmsProduct();
                product.setId(productId);
                product.setProductCategoryId(productCategoryId);
                // 同时更新分类名称
                if (categoryName != null) {
                    product.setProductCategoryName(categoryName);
                }
                int result = productMapper.updateByPrimaryKeySelective(product);
                if (result > 0) {
                    count++;
                }
            } catch (Exception e) {
                LOGGER.error("批量修改商品分类失败，商品ID：{}", productId, e);
            }
        }

        return count;
    }

    @Override
    public List<PmsProductExportDTO> exportProducts(PmsProductQueryParam productQueryParam) {
        // 获取商品列表（不分页）
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);

        // 应用查询条件
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            List<Long> productIds = productDao.getProductIdsBySnOrSkuCode(productQueryParam.getProductSn());
            if (!CollectionUtils.isEmpty(productIds)) {
                criteria.andIdIn(productIds);
            } else {
                return new ArrayList<>();
            }
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        if (productQueryParam.getDiyEnabled() != null) {
            criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
        }

        List<PmsProduct> productList = productMapper.selectByExample(productExample);

        // 如果有学校筛选条件，过滤商品
        if (productQueryParam.getSchoolId() != null) {
            productList = productList.stream()
                    .filter(product -> {
                        try {
                            return productSchoolRelationService.isProductAssociatedWithSchool(product.getId(), productQueryParam.getSchoolId());
                        } catch (Exception e) {
                            LOGGER.warn("检查商品学校关联失败，商品ID：{}，学校ID：{}", product.getId(), productQueryParam.getSchoolId());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        List<PmsProductExportDTO> result = new ArrayList<>();

        for (PmsProduct product : productList) {
            // 获取商品的SKU列表
            PmsSkuStockExample skuExample = new PmsSkuStockExample();
            skuExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsSkuStock> skuList = skuStockMapper.selectByExample(skuExample);

            if (CollectionUtils.isEmpty(skuList)) {
                // 如果没有SKU，创建一条商品记录
                PmsProductExportDTO dto = createExportDTO(product, null);
                result.add(dto);
            } else {
                // 如果有SKU，为每个SKU创建一条记录
                for (PmsSkuStock sku : skuList) {
                    PmsProductExportDTO dto = createExportDTO(product, sku);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    @Override
    public long countProducts(PmsProductQueryParam productQueryParam) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);

        // 应用查询条件
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (StringUtils.hasText(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (StringUtils.hasText(productQueryParam.getProductSn())) {
            List<Long> productIds = productDao.getProductIdsBySnOrSkuCode(productQueryParam.getProductSn());
            if (!CollectionUtils.isEmpty(productIds)) {
                criteria.andIdIn(productIds);
            } else {
                return 0;
            }
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        if (productQueryParam.getDiyEnabled() != null) {
            criteria.andDiyEnabledEqualTo(productQueryParam.getDiyEnabled());
        }

        long count = productMapper.countByExample(productExample);

        // 如果有学校筛选条件，需要进一步统计
        if (productQueryParam.getSchoolId() != null) {
            List<PmsProduct> productList = productMapper.selectByExample(productExample);
            count = productList.stream()
                    .filter(product -> {
                        try {
                            return productSchoolRelationService.isProductAssociatedWithSchool(product.getId(), productQueryParam.getSchoolId());
                        } catch (Exception e) {
                            LOGGER.warn("检查商品学校关联失败，商品ID：{}，学校ID：{}", product.getId(), productQueryParam.getSchoolId());
                            return false;
                        }
                    })
                    .count();
        }

        return count;
    }

    @Override
    public List<PmsProductExportDTO> exportProductsPaged(PmsProductQueryParam productQueryParam, int pageSize, int pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return exportProducts(productQueryParam);
    }

    private PmsProductExportDTO createExportDTO(PmsProduct product, PmsSkuStock sku) {
        PmsProductExportDTO dto = new PmsProductExportDTO();

        // 商品基本信息
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductSn(product.getProductSn());
        // 获取品牌名称
        if (product.getBrandId() != null) {
            try {
                PmsBrand brand = brandService.getBrand(product.getBrandId());
                if (brand != null) {
                    dto.setBrandName(brand.getName());
                }
            } catch (Exception e) {
                LOGGER.warn("获取品牌名称失败，商品ID：{}，品牌ID：{}", product.getId(), product.getBrandId());
            }
        }
        // 获取分类名称
        if (product.getProductCategoryId() != null) {
            try {
                PmsProductCategory category = productCategoryService.getItem(product.getProductCategoryId());
                if (category != null) {
                    dto.setProductCategoryName(category.getName());
                }
            } catch (Exception e) {
                LOGGER.warn("获取分类名称失败，商品ID：{}，分类ID：{}", product.getId(), product.getProductCategoryId());
            }
        }
        dto.setPrice(product.getPrice());
        dto.setPromotionPrice(product.getPromotionPrice());
        dto.setWeight(product.getWeight());
        dto.setUnit(product.getUnit());
        dto.setStock(product.getStock());
        dto.setLowStock(product.getLowStock());
        dto.setSale(product.getSale());
        dto.setSubTitle(product.getSubTitle());
        dto.setKeywords(product.getKeywords());
        dto.setNote(product.getNote());
        dto.setServiceIds(product.getServiceIds());

        // 状态信息转换
        dto.setPublishStatus(product.getPublishStatus());
        dto.setPublishStatusName(product.getPublishStatus() == 1 ? "上架" : "下架");
        dto.setNewStatus(product.getNewStatus());
        dto.setNewStatusName(product.getNewStatus() == 1 ? "新品" : "非新品");
        dto.setRecommandStatus(product.getRecommandStatus());
        dto.setRecommandStatusName(product.getRecommandStatus() == 1 ? "推荐" : "非推荐");
        dto.setDiyEnabled(product.getDiyEnabled());
        dto.setDiyEnabledName(product.getDiyEnabled() != null && product.getDiyEnabled() == 1 ? "支持DIY" : "不支持DIY");

        // 服务信息转换
        if (StringUtils.hasText(product.getServiceIds())) {
            String[] serviceArray = product.getServiceIds().split(",");
            List<String> serviceNames = new ArrayList<>();
            for (String serviceId : serviceArray) {
                switch (serviceId.trim()) {
                    case "1":
                        serviceNames.add("无忧退货");
                        break;
                    case "2":
                        serviceNames.add("快速退款");
                        break;
                    case "3":
                        serviceNames.add("免费包邮");
                        break;
                }
            }
            dto.setServiceNames(String.join(",", serviceNames));
        }

        // 运费模板信息
        dto.setFreightTemplateId(product.getFreightTemplateId());
        if (product.getFreightTemplateId() != null) {
            try {
                SmsFreightTemplateVO template = freightTemplateService.getTemplateDetail(product.getFreightTemplateId());
                if (template != null) {
                    dto.setFreightTemplateName(template.getName());
                }
            } catch (Exception e) {
                LOGGER.warn("获取运费模板名称失败，商品ID：{}，模板ID：{}", product.getId(), product.getFreightTemplateId());
            }
        }

        // 关联学校信息
        try {
            List<OmsSchool> schools = productSchoolRelationService.getSchoolsByProductId(product.getId());
            if (!CollectionUtils.isEmpty(schools)) {
                String schoolNames = schools.stream()
                        .map(OmsSchool::getSchoolName)
                        .collect(Collectors.joining(","));
                dto.setSchoolNames(schoolNames);
            }
        } catch (Exception e) {
            LOGGER.warn("获取商品关联学校失败，商品ID：{}", product.getId());
        }

        // SKU信息
        if (sku != null) {
            dto.setSkuId(sku.getId());
            dto.setSkuCode(sku.getSkuCode());
            dto.setSkuPrice(sku.getPrice());
            dto.setSkuStock(sku.getStock());
            dto.setSkuLowStock(sku.getLowStock());
            dto.setSkuSpData(sku.getSpData());
        }

        return dto;
    }

    @Override
    public PmsProductShareResult generateShareInfo(Long productId) {
        // 验证商品是否存在
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        PmsProductShareResult result = new PmsProductShareResult();
        result.setProductId(productId);
        result.setProductName(product.getName());

        // 生成分享标题和描述
        String shareTitle = product.getName();
        String shareDescription;
        if (product.getPrice() != null) {
            shareDescription = String.format("优质商品，仅售￥%.2f，快来抢购！", product.getPrice());
        } else {
            shareDescription = "优质商品，快来抢购！";
        }

        // 如果有促销价格，添加到描述中
        if (product.getPromotionPrice() != null && product.getPromotionPrice().compareTo(product.getPrice()) < 0) {
            shareDescription = String.format("限时特价￥%.2f，原价￥%.2f，快来抢购！",
                product.getPromotionPrice(), product.getPrice());
        }

        result.setShareTitle(shareTitle);
        result.setShareDescription(shareDescription);

        // 检查微信小程序配置是否可用
        if (wechatMiniProgramService.isConfigAvailable()) {
            // 生成真正的小程序短链接
            String path = "pages/product/product";
            String query = "id=" + productId;
            String urlLink = wechatMiniProgramService.generateUrlLink(path, query);

            if (urlLink != null) {
                result.setShareLink(urlLink);
            } else {
                // 降级方案：生成普通链接
                result.setShareLink("小程序短链接生成失败，请检查微信小程序配置");
            }

            // 生成真正的小程序码（base64格式）
            // 直接使用商品ID作为scene参数，与优惠券保持一致
            String scene = productId.toString();
            String page = "pages/product/product";
            String miniProgramCodeBase64 = wechatMiniProgramService.generateMiniProgramCode(scene, page, 430);

            if (miniProgramCodeBase64 != null) {
                result.setMiniProgramCodeBase64(miniProgramCodeBase64);
                result.setMiniProgramCodeUrl("小程序码已生成（base64格式）");
            } else {
                // 降级方案
                result.setMiniProgramCodeBase64(null);
                result.setMiniProgramCodeUrl("小程序码生成失败，请检查微信小程序配置");
            }
        } else {
            // 微信配置不可用时的降级方案
            result.setShareLink("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeUrl("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeBase64(null);
        }

        // 设置默认分享图片（使用商品主图或默认图片）
        if (product.getPic() != null && !product.getPic().isEmpty()) {
            result.setShareImageUrl(product.getPic());
        } else {
            result.setShareImageUrl("/static/images/product-share-default.png");
        }

        return result;
    }

    @Override
    public List<PmsProductWithStoreStockVO> listWithStoreStock(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        // 先获取商品列表
        List<PmsProduct> productList = list(productQueryParam, pageSize, pageNum);
        
        if (CollectionUtils.isEmpty(productList)) {
            return new ArrayList<>();
        }
        
        Long storeId = productQueryParam.getStoreId();
        
        // 转换为VO列表
        List<PmsProductWithStoreStockVO> result = new ArrayList<>();
        for (PmsProduct product : productList) {
            PmsProductWithStoreStockVO vo = PmsProductWithStoreStockVO.fromProduct(product);
            
            // 如果指定了门店ID，查询该门店的SKU库存
            if (storeId != null) {
                List<PmsProductWithStoreStockVO.StoreSkuStockInfo> storeSkuStocks = new ArrayList<>();
                int totalStock = 0;
                StringBuilder displayBuilder = new StringBuilder();
                
                // 查询该商品在指定门店的所有SKU库存
                PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
                example.createCriteria()
                    .andProductIdEqualTo(product.getId())
                    .andStoreIdEqualTo(storeId);
                List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
                
                if (!CollectionUtils.isEmpty(storeStocks)) {
                    for (PmsStoreSkuStock storeStock : storeStocks) {
                        PmsProductWithStoreStockVO.StoreSkuStockInfo info = new PmsProductWithStoreStockVO.StoreSkuStockInfo();
                        info.setSkuId(storeStock.getSkuId());
                        info.setSkuCode(storeStock.getSkuCode());
                        info.setStock(storeStock.getStock() != null ? storeStock.getStock() : 0);
                        info.setLowStock(storeStock.getLowStock() != null ? storeStock.getLowStock() : 0);
                        
                        // 获取SKU规格信息
                        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(storeStock.getSkuId());
                        if (skuStock != null && skuStock.getSpData() != null) {
                            info.setSpData(skuStock.getSpData());
                            // 解析规格信息用于显示
                            String spDisplay = parseSpDataForDisplay(skuStock.getSpData());
                            if (displayBuilder.length() > 0) {
                                displayBuilder.append("\n");
                            }
                            displayBuilder.append(spDisplay)
                                .append("(").append(storeStock.getSkuCode()).append(")")
                                .append("：库存 ").append(info.getStock());
                        } else {
                            info.setSpData("");
                            if (displayBuilder.length() > 0) {
                                displayBuilder.append("\n");
                            }
                            displayBuilder.append(storeStock.getSkuCode())
                                .append("：库存 ").append(info.getStock());
                        }
                        
                        totalStock += info.getStock();
                        storeSkuStocks.add(info);
                    }
                }
                
                vo.setStoreSkuStocks(storeSkuStocks);
                vo.setStoreStockTotal(totalStock);
                vo.setStoreStockDisplay(displayBuilder.toString());
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 解析SKU规格信息用于显示
     * 将JSON格式的spData转换为可读格式，如 "重量: 680g"
     */
    private String parseSpDataForDisplay(String spData) {
        if (spData == null || spData.isEmpty()) {
            return "";
        }
        try {
            // spData格式类似: [{"key":"重量","value":"680g"}]
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<java.util.Map<String, String>> specs = mapper.readValue(spData, 
                mapper.getTypeFactory().constructCollectionType(List.class, java.util.Map.class));
            
            StringBuilder sb = new StringBuilder();
            for (java.util.Map<String, String> spec : specs) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(spec.get("key")).append(": ").append(spec.get("value"));
            }
            return sb.toString();
        } catch (Exception e) {
            LOGGER.warn("解析SKU规格信息失败: {}", spData, e);
            return spData;
        }
    }

}
