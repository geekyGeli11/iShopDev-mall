package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.dto.BundleItemParam;
import com.macro.mall.dto.PmsProductBundleDetail;
import com.macro.mall.dto.PmsProductBundleParam;
import com.macro.mall.mapper.PmsProductBundleItemMapper;
import com.macro.mall.mapper.PmsProductBundleMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductBundleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组合商品Service实现类
 */
@Service
public class PmsProductBundleServiceImpl implements PmsProductBundleService {

    @Autowired
    private PmsProductBundleMapper bundleMapper;
    @Autowired
    private PmsProductBundleItemMapper bundleItemMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsProductBundle> list(String keyword, Integer publishStatus, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductBundleExample example = new PmsProductBundleExample();
        example.setOrderByClause("sort desc, create_time desc");
        PmsProductBundleExample.Criteria criteria = example.createCriteria();
        if (StringUtils.hasText(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (publishStatus != null) {
            criteria.andPublishStatusEqualTo(publishStatus.byteValue());
        }
        return bundleMapper.selectByExample(example);
    }

    @Override
    public int create(PmsProductBundleParam param) {
        // 验证组合商品项
        validateBundleItems(param.getItems());
        
        // 计算原价总和
        BigDecimal originalPrice = calculateOriginalPrice(param.getItems());
        
        // 计算组合价格
        BigDecimal bundlePrice = calculateBundlePrice(param, originalPrice);
        
        // 验证组合价格必须低于原价
        if (bundlePrice.compareTo(originalPrice) >= 0) {
            Asserts.fail("组合价格必须低于原价总和");
        }
        
        // 创建组合商品
        PmsProductBundle bundle = new PmsProductBundle();
        BeanUtils.copyProperties(param, bundle);
        bundle.setOriginalPrice(originalPrice);
        bundle.setBundlePrice(bundlePrice);
        bundle.setPriceType(param.getPriceType().byteValue());
        bundle.setPublishStatus((byte) 0); // 默认下架
        bundle.setSaleCount(0);
        bundleMapper.insertSelective(bundle);
        
        // 创建组合商品项
        createBundleItems(bundle.getId(), param.getItems());
        
        return 1;
    }

    @Override
    public int update(Long id, PmsProductBundleParam param) {
        // 验证组合商品是否存在
        PmsProductBundle existingBundle = bundleMapper.selectByPrimaryKey(id);
        if (existingBundle == null) {
            Asserts.fail("组合商品不存在");
        }
        
        // 验证组合商品项
        validateBundleItems(param.getItems());
        
        // 计算原价总和
        BigDecimal originalPrice = calculateOriginalPrice(param.getItems());
        
        // 计算组合价格
        BigDecimal bundlePrice = calculateBundlePrice(param, originalPrice);
        
        // 验证组合价格必须低于原价
        if (bundlePrice.compareTo(originalPrice) >= 0) {
            Asserts.fail("组合价格必须低于原价总和");
        }
        
        // 更新组合商品
        PmsProductBundle bundle = new PmsProductBundle();
        BeanUtils.copyProperties(param, bundle);
        bundle.setId(id);
        bundle.setOriginalPrice(originalPrice);
        bundle.setBundlePrice(bundlePrice);
        bundle.setPriceType(param.getPriceType().byteValue());
        bundleMapper.updateByPrimaryKeySelective(bundle);
        
        // 删除原有组合商品项
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(id);
        bundleItemMapper.deleteByExample(itemExample);
        
        // 创建新的组合商品项
        createBundleItems(id, param.getItems());
        
        return 1;
    }

    @Override
    public PmsProductBundleDetail getDetail(Long id) {
        PmsProductBundle bundle = bundleMapper.selectByPrimaryKey(id);
        if (bundle == null) {
            return null;
        }
        
        PmsProductBundleDetail detail = new PmsProductBundleDetail();
        BeanUtils.copyProperties(bundle, detail);
        
        // 计算节省金额
        if (bundle.getOriginalPrice() != null && bundle.getBundlePrice() != null) {
            detail.setSavedAmount(bundle.getOriginalPrice().subtract(bundle.getBundlePrice()));
        }
        
        // 获取组合商品项
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(id);
        itemExample.setOrderByClause("sort asc");
        List<PmsProductBundleItem> items = bundleItemMapper.selectByExample(itemExample);
        
        // 获取商品价格信息
        List<Long> productIds = items.stream()
                .map(PmsProductBundleItem::getProductId)
                .collect(Collectors.toList());
        Map<Long, PmsProduct> productMap = getProductMap(productIds);
        
        // 构建详情项
        List<PmsProductBundleDetail.BundleItemDetail> itemDetails = new ArrayList<>();
        for (PmsProductBundleItem item : items) {
            PmsProductBundleDetail.BundleItemDetail itemDetail = new PmsProductBundleDetail.BundleItemDetail();
            BeanUtils.copyProperties(item, itemDetail);
            
            PmsProduct product = productMap.get(item.getProductId());
            if (product != null) {
                itemDetail.setProductPrice(product.getPrice());
                itemDetail.setProductPublishStatus(product.getPublishStatus().intValue());
            }
            itemDetails.add(itemDetail);
        }
        detail.setItems(itemDetails);
        
        return detail;
    }

    @Override
    public int delete(Long id) {
        // 删除组合商品项（外键级联删除）
        return bundleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        PmsProductBundleExample example = new PmsProductBundleExample();
        example.createCriteria().andIdIn(ids);
        return bundleMapper.deleteByExample(example);
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        PmsProductBundle bundle = new PmsProductBundle();
        bundle.setPublishStatus(publishStatus.byteValue());
        PmsProductBundleExample example = new PmsProductBundleExample();
        example.createCriteria().andIdIn(ids);
        return bundleMapper.updateByExampleSelective(bundle, example);
    }

    /**
     * 验证组合商品项
     */
    private void validateBundleItems(List<BundleItemParam> items) {
        if (CollectionUtils.isEmpty(items) || items.size() < 2) {
            Asserts.fail("组合商品至少需要包含2个商品");
        }
        
        // 验证商品是否存在且已上架
        for (BundleItemParam item : items) {
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product == null) {
                Asserts.fail("商品不存在：" + item.getProductId());
            }
            if (product.getPublishStatus() != 1) {
                Asserts.fail("组合内包含已下架商品：" + product.getName());
            }
        }
    }

    /**
     * 计算原价总和
     */
    private BigDecimal calculateOriginalPrice(List<BundleItemParam> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BundleItemParam item : items) {
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product != null && product.getPrice() != null) {
                totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return totalPrice;
    }

    /**
     * 计算组合价格
     */
    private BigDecimal calculateBundlePrice(PmsProductBundleParam param, BigDecimal originalPrice) {
        if (param.getPriceType() == 1) {
            // 固定价格
            if (param.getBundlePrice() == null) {
                Asserts.fail("固定价格模式下，组合价格不能为空");
            }
            return param.getBundlePrice();
        } else if (param.getPriceType() == 2) {
            // 折扣比例
            if (param.getDiscountRate() == null) {
                Asserts.fail("折扣模式下，折扣比例不能为空");
            }
            return originalPrice.multiply(param.getDiscountRate())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        Asserts.fail("无效的定价方式");
        return BigDecimal.ZERO;
    }

    /**
     * 创建组合商品项
     */
    private void createBundleItems(Long bundleId, List<BundleItemParam> items) {
        int sort = 0;
        for (BundleItemParam itemParam : items) {
            PmsProduct product = productMapper.selectByPrimaryKey(itemParam.getProductId());
            
            PmsProductBundleItem item = new PmsProductBundleItem();
            item.setBundleId(bundleId);
            item.setProductId(itemParam.getProductId());
            item.setProductName(product.getName());
            item.setProductPic(product.getPic());
            item.setQuantity(itemParam.getQuantity());
            item.setSort(itemParam.getSort() != null ? itemParam.getSort() : sort++);
            bundleItemMapper.insertSelective(item);
        }
    }

    /**
     * 获取商品Map
     */
    private Map<Long, PmsProduct> getProductMap(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Map.of();
        }
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(productIds);
        List<PmsProduct> products = productMapper.selectByExample(example);
        return products.stream().collect(Collectors.toMap(PmsProduct::getId, p -> p));
    }
}
