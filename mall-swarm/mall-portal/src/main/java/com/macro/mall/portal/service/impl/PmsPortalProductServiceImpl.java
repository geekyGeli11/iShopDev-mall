package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.FreightTemplateInfo;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.PmsPortalProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台订单管理Service实现类
 * Created by macro on 2020/4/6.
 */
@Service
public class PmsPortalProductServiceImpl implements PmsPortalProductService {
    private static final Logger log = LoggerFactory.getLogger(PmsPortalProductServiceImpl.class);

    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PortalProductDao portalProductDao;
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    @Autowired
    private SmsFreightTemplateMapper freightTemplateMapper;

    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
        return search(keyword, brandId, productCategoryId, pageNum, pageSize, sort, null, null);
    }

    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort, Long schoolId) {
        return search(keyword, brandId, productCategoryId, pageNum, pageSize, sort, schoolId, null);
    }

    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort, Long schoolId, Boolean isDIY) {
        PmsProductExample example = new PmsProductExample();
        PmsProductExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        criteria.andPublishStatusEqualTo(1);
        if (StrUtil.isNotEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (brandId != null) {
            criteria.andBrandIdEqualTo(brandId);
        }
        if (productCategoryId != null) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        // 如果isDIY为true，只查询可DIY定制的商品
        if (isDIY != null && isDIY) {
            criteria.andDiyEnabledEqualTo((byte) 1);
        }

        //0->综合推荐；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低
        if (sort == 0) {
            // 综合推荐：优先显示推荐商品，然后按ID降序（相当于按时间倒序）
            example.setOrderByClause("recommand_status desc, id desc");
        } else if (sort == 1) {
            example.setOrderByClause("sort desc");
        } else if (sort == 2) {
            example.setOrderByClause("sale desc");
        } else if (sort == 3) {
            example.setOrderByClause("price asc");
        } else if (sort == 4) {
            example.setOrderByClause("price desc");
        }

        List<PmsProduct> productList;
        // 如果有学校筛选条件，需要特殊处理分页
        if (schoolId != null) {
            // 查询与指定学校关联的商品ID
            List<Long> schoolProductIds = portalProductDao.getProductIdsBySchoolId(schoolId);
            if (CollectionUtils.isEmpty(schoolProductIds)) {
                // 如果该学校没有关联任何商品，返回空列表
                return new ArrayList<>();
            }

            // 先获取所有符合条件的商品（不分页）
            criteria.andIdIn(schoolProductIds);
            List<PmsProduct> allProducts = productMapper.selectByExample(example);

            // 手动分页
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, allProducts.size());
            productList = start < allProducts.size() ?
                    new ArrayList<>(allProducts.subList(start, end)) : new ArrayList<>();
        } else {
            // 没有学校筛选，直接分页查询
            PageHelper.startPage(pageNum, pageSize);
            productList = productMapper.selectByExample(example);
        }

        // 填充商品的SKU库存信息（用于判断是否售罄）
        fillProductSkuStock(productList);

        return productList;
    }

    /**
     * 批量填充商品的SKU库存信息
     * 计算每个商品所有SKU的库存总和，更新到商品的stock字段
     */
    private void fillProductSkuStock(List<PmsProduct> productList) {
        if (CollectionUtils.isEmpty(productList)) {
            return;
        }

        // 获取所有商品ID
        List<Long> productIds = productList.stream()
                .map(PmsProduct::getId)
                .collect(Collectors.toList());

        // 批量查询所有商品的SKU库存
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdIn(productIds);
        List<PmsSkuStock> allSkuStocks = skuStockMapper.selectByExample(skuExample);

        // 按商品ID分组，计算每个商品的SKU库存总和
        Map<Long, Integer> productStockMap = allSkuStocks.stream()
                .collect(Collectors.groupingBy(
                        PmsSkuStock::getProductId,
                        Collectors.summingInt(sku -> sku.getStock() != null ? sku.getStock() : 0)
                ));

        // 更新每个商品的stock字段
        for (PmsProduct product : productList) {
            Integer totalStock = productStockMap.get(product.getId());
            // 如果没有SKU或SKU库存总和为0，则设置stock为0（表示售罄）
            product.setStock(totalStock != null ? totalStock : 0);
        }

        log.debug("填充商品SKU库存完成，商品数量：{}，SKU数量：{}", productList.size(), allSkuStocks.size());
    }

    @Override
    public long searchCount(String keyword, Long brandId, Long productCategoryId, Long schoolId) {
        return searchCount(keyword, brandId, productCategoryId, schoolId, null);
    }

    @Override
    public long searchCount(String keyword, Long brandId, Long productCategoryId, Long schoolId, Boolean isDIY) {
        PmsProductExample example = new PmsProductExample();
        PmsProductExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        criteria.andPublishStatusEqualTo(1);
        if (StrUtil.isNotEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (brandId != null) {
            criteria.andBrandIdEqualTo(brandId);
        }
        if (productCategoryId != null) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        // 如果isDIY为true，只查询可DIY定制的商品
        if (isDIY != null && isDIY) {
            criteria.andDiyEnabledEqualTo((byte) 1);
        }

        // 如果有学校筛选条件，需要特殊处理
        if (schoolId != null) {
            // 查询与指定学校关联的商品ID
            List<Long> schoolProductIds = portalProductDao.getProductIdsBySchoolId(schoolId);
            if (CollectionUtils.isEmpty(schoolProductIds)) {
                return 0;
            }
            criteria.andIdIn(schoolProductIds);
        }

        return productMapper.countByExample(example);
    }

    @Override
    public List<PmsProductCategoryNode> categoryTreeList() {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        List<PmsProductCategory> allList = productCategoryMapper.selectByExample(example);
        List<PmsProductCategoryNode> result = allList.stream()
                .filter(item -> item.getParentId().equals(0L))
                .map(item -> covert(item, allList)).collect(Collectors.toList());
        return result;
    }

    @Override
    public PmsPortalProductDetail detail(Long id) {
        return detail(id, null);
    }

    @Override
    public PmsPortalProductDetail detail(Long id, Long storeId) {
        PmsPortalProductDetail result = new PmsPortalProductDetail();
        //获取商品信息
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        // 检查商品是否存在且已上架
        if (product == null || product.getDeleteStatus() == 1 || product.getPublishStatus() == 0) {
            return null;
        }
        result.setProduct(product);
        //获取品牌信息
        PmsBrand brand = brandMapper.selectByPrimaryKey(product.getBrandId());
        result.setBrand(brand);
        //获取商品属性信息
        PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
        attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(product.getProductAttributeCategoryId());
        List<PmsProductAttribute> productAttributeList = productAttributeMapper.selectByExample(attributeExample);
        result.setProductAttributeList(productAttributeList);
        //获取商品属性值信息
        if(CollUtil.isNotEmpty(productAttributeList)){
            List<Long> attributeIds = productAttributeList.stream().map(PmsProductAttribute::getId).collect(Collectors.toList());
            PmsProductAttributeValueExample attributeValueExample = new PmsProductAttributeValueExample();
            attributeValueExample.createCriteria().andProductIdEqualTo(product.getId())
                    .andProductAttributeIdIn(attributeIds);
            List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectByExample(attributeValueExample);
            result.setProductAttributeValueList(productAttributeValueList);
        }
        
        //获取商品SKU库存信息（直接查询总库存表）
        List<PmsSkuStock> skuStockList = getTotalSkuStockList(product.getId());
        result.setSkuStockList(skuStockList);
        
        //商品阶梯价格设置
        if(product.getPromotionType()==3){
            PmsProductLadderExample ladderExample = new PmsProductLadderExample();
            ladderExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductLadder> productLadderList = productLadderMapper.selectByExample(ladderExample);
            result.setProductLadderList(productLadderList);
        }
        //商品满减价格设置
        if(product.getPromotionType()==4){
            PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
            fullReductionExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper.selectByExample(fullReductionExample);
            result.setProductFullReductionList(productFullReductionList);
        }
        //商品可用优惠券
        result.setCouponList(portalProductDao.getAvailableCouponList(product.getId(),product.getProductCategoryId()));
        
        //获取运费模板信息
        FreightTemplateInfo freightTemplateInfo = getFreightTemplateInfo(product.getFreightTemplateId());
        result.setFreightTemplateInfo(freightTemplateInfo);
        
        return result;
    }

    /**
     * 获取商品SKU的库存信息（直接查询总库存表）
     */
    private List<PmsSkuStock> getTotalSkuStockList(Long productId) {
        try {
            // 直接获取该商品的所有SKU库存信息
            PmsSkuStockExample skuExample = new PmsSkuStockExample();
            skuExample.createCriteria().andProductIdEqualTo(productId);
            List<PmsSkuStock> allSkuStocks = skuStockMapper.selectByExample(skuExample);

            log.debug("获取商品SKU库存信息，商品ID：{}，SKU数量：{}", productId, allSkuStocks.size());

            return allSkuStocks;

        } catch (Exception e) {
            log.error("获取商品SKU库存失败，商品ID：{}", productId, e);
            // 返回空列表
            return new ArrayList<>();
        }
    }

    /**
     * 获取门店SKU库存并转换为PmsSkuStock格式
     */
    private List<PmsSkuStock> getStoreSkuStockList(Long productId, Long storeId) {
        // 先获取商品的所有SKU信息（仅用于获取SKU基本信息如价格、编码等）
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        List<PmsSkuStock> originalSkuList = skuStockMapper.selectByExample(skuExample);
        
        // 查询门店库存
        PmsStoreSkuStockExample storeSkuExample = new PmsStoreSkuStockExample();
        storeSkuExample.createCriteria()
                .andProductIdEqualTo(productId)
                .andStoreIdEqualTo(storeId)
                .andStatusEqualTo(1); // 只查询启用状态的库存
        List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockMapper.selectByExample(storeSkuExample);
        
        // 将门店库存数据映射到SKU库存列表中
        for (PmsSkuStock skuStock : originalSkuList) {
            // 查找对应的门店库存
            PmsStoreSkuStock storeSkuStock = storeSkuStockList.stream()
                    .filter(store -> store.getSkuId().equals(skuStock.getId()))
                    .findFirst()
                    .orElse(null);
            
            if (storeSkuStock != null) {
                // 使用门店库存数据替换所有库存相关字段
                skuStock.setStock(storeSkuStock.getStock());
                skuStock.setLowStock(storeSkuStock.getLowStock());
                skuStock.setSale(storeSkuStock.getSaleCount());
                skuStock.setLockStock(storeSkuStock.getLockedStock()); // 设置门店锁定库存
            } else {
                // 如果门店没有该SKU的库存，将库存设为0表示门店无库存
                skuStock.setStock(0);
                skuStock.setLockStock(0);
                // 保持价格等其他信息不变，只修改库存相关字段
            }
        }
        
        return originalSkuList;
    }

    /**
     * 获取运费模板信息
     */
    private FreightTemplateInfo getFreightTemplateInfo(Long freightTemplateId) {
        if (freightTemplateId == null) {
            // 如果商品没有配置运费模板，返回默认的免运费信息
            FreightTemplateInfo defaultInfo = new FreightTemplateInfo();
            defaultInfo.setId(0L);
            defaultInfo.setName("默认运费");
            defaultInfo.setDeliveryType((byte) 1); // 快递配送
            defaultInfo.setFreeType((byte) 4); // 无条件包邮
            defaultInfo.setStatus((byte) 1); // 启用
            defaultInfo.setIsDefault(true);
            defaultInfo.generateFreightDescription();
            return defaultInfo;
        }
        
        try {
            SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(freightTemplateId);
            if (template == null) {
                // 运费模板不存在，返回默认信息
                FreightTemplateInfo defaultInfo = new FreightTemplateInfo();
                defaultInfo.setId(0L);
                defaultInfo.setName("运费模板不存在");
                defaultInfo.setDeliveryType((byte) 1);
                defaultInfo.setFreeType((byte) 0); // 不包邮
                defaultInfo.setChargeType((byte) 1); // 按件计费
                defaultInfo.setStatus((byte) 1);
                defaultInfo.setIsDefault(false);
                defaultInfo.generateFreightDescription();
                return defaultInfo;
            }
            
            // 将运费模板转换为DTO
            FreightTemplateInfo freightTemplateInfo = new FreightTemplateInfo();
            BeanUtils.copyProperties(template, freightTemplateInfo);
            
            // 生成运费描述文本
            freightTemplateInfo.generateFreightDescription();
            
            return freightTemplateInfo;
        } catch (Exception e) {
            // 查询异常，返回默认信息
            FreightTemplateInfo errorInfo = new FreightTemplateInfo();
            errorInfo.setId(0L);
            errorInfo.setName("运费查询异常");
            errorInfo.setDeliveryType((byte) 1);
            errorInfo.setFreeType((byte) 0);
            errorInfo.setChargeType((byte) 1);
            errorInfo.setStatus((byte) 1);
            errorInfo.setIsDefault(false);
            errorInfo.setFreightDescription("运费详询客服");
            return errorInfo;
        }
    }

    /**
     * 初始对象转化为节点对象
     */
    private PmsProductCategoryNode covert(PmsProductCategory item, List<PmsProductCategory> allList) {
        PmsProductCategoryNode node = new PmsProductCategoryNode();
        BeanUtils.copyProperties(item, node);
        List<PmsProductCategoryNode> children = allList.stream()
                .filter(subItem -> subItem.getParentId().equals(item.getId()))
                .map(subItem -> covert(subItem, allList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
