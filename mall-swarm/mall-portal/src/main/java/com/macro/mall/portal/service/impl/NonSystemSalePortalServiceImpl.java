package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PmsNonSystemSaleMapper;
import com.macro.mall.mapper.PmsNonSystemSaleItemMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsNonSystemSale;
import com.macro.mall.model.PmsNonSystemSaleItem;
import com.macro.mall.model.PmsNonSystemSaleItemExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.NonSystemSalePortalService;
import com.macro.mall.portal.service.UmsMemberService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 非系统销售单小程序端服务实现
 * Created by macro on 2025-12-12.
 */
@Service
public class NonSystemSalePortalServiceImpl implements NonSystemSalePortalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NonSystemSalePortalServiceImpl.class);
    
    @Autowired
    private PmsNonSystemSaleMapper nonSystemSaleMapper;
    
    @Autowired
    private PmsNonSystemSaleItemMapper nonSystemSaleItemMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Override
    public Map<String, Object> getSaleDetailWithAuth(Long saleId) {
        // 1. 获取销售单信息
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("销售单不存在");
        }
        
        // 2. 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            throw new SecurityException("请先登录");
        }
        
        // 3. 验证权限：检查用户手机号是否与客户手机号一致
        String customerPhone = sale.getCustomerPhone();
        String memberPhone = currentMember.getPhone();
        
        if (customerPhone == null || customerPhone.trim().isEmpty()) {
            throw new SecurityException("该销售单未设置客户手机号，无法查看");
        }
        
        if (memberPhone == null || memberPhone.trim().isEmpty()) {
            throw new SecurityException("您的账号未绑定手机号，请先绑定手机号");
        }
        
        if (!customerPhone.equals(memberPhone)) {
            LOGGER.warn("用户无权查看销售单，用户手机号: {}, 客户手机号: {}", memberPhone, customerPhone);
            throw new SecurityException("您无权查看该销售单");
        }
        
        // 4. 获取销售明细
        PmsNonSystemSaleItemExample itemExample = new PmsNonSystemSaleItemExample();
        itemExample.createCriteria().andSaleIdEqualTo(saleId);
        List<PmsNonSystemSaleItem> items = nonSystemSaleItemMapper.selectByExample(itemExample);
        
        // 5. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", sale.getId());
        result.put("saleNo", sale.getSaleNo());
        result.put("saleTypeName", sale.getSaleTypeName());
        result.put("storeName", sale.getStoreName());
        result.put("saleDate", sale.getSaleDate());
        result.put("totalAmount", sale.getTotalAmount());
        result.put("totalQuantity", sale.getTotalQuantity());
        result.put("remark", sale.getRemark());
        result.put("customerName", sale.getCustomerName());
        result.put("status", sale.getStatus());
        result.put("statusName", getStatusName(sale.getStatus()));
        result.put("createTime", sale.getCreateTime());
        
        // 批量查询商品图片和SKU规格信息
        Set<Long> productIds = items.stream()
            .map(PmsNonSystemSaleItem::getProductId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        Set<Long> skuIds = items.stream()
            .map(PmsNonSystemSaleItem::getSkuId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        
        LOGGER.info("查询商品图片，productIds: {}", productIds);
        LOGGER.info("查询SKU规格，skuIds: {}", skuIds);
        
        // 查询商品图片
        Map<Long, String> productPicMap = new HashMap<>();
        for (Long productId : productIds) {
            PmsProduct product = productMapper.selectByPrimaryKey(productId);
            if (product != null && product.getPic() != null) {
                productPicMap.put(productId, product.getPic());
            }
        }
        
        // 查询SKU规格信息
        Map<Long, String> skuSpecsMap = new HashMap<>();
        for (Long skuId : skuIds) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            if (skuStock != null && skuStock.getSpData() != null) {
                String specs = formatSpecs(skuStock.getSpData());
                if (specs != null) {
                    skuSpecsMap.put(skuId, specs);
                }
            }
        }
        
        LOGGER.info("商品图片Map: {}", productPicMap);
        LOGGER.info("SKU规格Map: {}", skuSpecsMap);
        
        // 转换销售明细
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (PmsNonSystemSaleItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productName", item.getProductName());
            itemMap.put("skuCode", item.getSkuCode());
            itemMap.put("productPic", productPicMap.get(item.getProductId()));
            itemMap.put("specs", skuSpecsMap.get(item.getSkuId()));
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("salePrice", item.getSalePrice());
            itemMap.put("lineAmount", item.getLineAmount());
            itemList.add(itemMap);
        }
        result.put("items", itemList);
        
        LOGGER.info("用户查看销售单成功，saleId: {}, memberPhone: {}", saleId, memberPhone);
        return result;
    }
    
    @Override
    public Map<String, Object> checkSaleAuth(Long saleId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 获取销售单信息
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            result.put("hasAuth", false);
            result.put("needLogin", false);
            result.put("message", "销售单不存在");
            return result;
        }
        
        // 2. 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            result.put("hasAuth", false);
            result.put("needLogin", true);
            result.put("message", "请先登录");
            return result;
        }
        
        // 3. 验证权限
        String customerPhone = sale.getCustomerPhone();
        String memberPhone = currentMember.getPhone();
        
        if (customerPhone == null || customerPhone.trim().isEmpty()) {
            result.put("hasAuth", false);
            result.put("needLogin", false);
            result.put("message", "该销售单未设置客户手机号");
            return result;
        }
        
        if (memberPhone == null || memberPhone.trim().isEmpty()) {
            result.put("hasAuth", false);
            result.put("needLogin", false);
            result.put("needBindPhone", true);
            result.put("message", "请先绑定手机号");
            return result;
        }
        
        if (!customerPhone.equals(memberPhone)) {
            result.put("hasAuth", false);
            result.put("needLogin", false);
            result.put("message", "您无权查看该销售单");
            return result;
        }
        
        result.put("hasAuth", true);
        result.put("needLogin", false);
        result.put("message", "验证通过");
        return result;
    }
    
    /**
     * 获取状态名称
     */
    private String getStatusName(Byte status) {
        if (status == null) return "";
        switch (status) {
            case 1:
                return "已提交";
            case 2:
                return "已审核";
            case 3:
                return "已驳回";
            default:
                return "未知";
        }
    }
    
    /**
     * 格式化规格信息
     * 将JSON格式的spData转换为可读的规格字符串
     */
    @SuppressWarnings("unchecked")
    private String formatSpecs(String spData) {
        if (spData == null || spData.isEmpty()) {
            return null;
        }
        try {
            // spData格式: [{"key":"重量","value":"500克"},{"key":"颜色","value":"红色"}]
            List<Map> specList = JSON.parseArray(spData, Map.class);
            if (specList == null || specList.isEmpty()) {
                return null;
            }
            return specList.stream()
                .map(spec -> spec.get("key") + "：" + spec.get("value"))
                .collect(Collectors.joining("，"));
        } catch (Exception e) {
            LOGGER.warn("解析规格信息失败: {}", spData, e);
            return spData;
        }
    }
}
