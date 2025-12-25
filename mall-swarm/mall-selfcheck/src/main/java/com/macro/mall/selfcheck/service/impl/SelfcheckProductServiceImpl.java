package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.selfcheck.dao.SelfcheckProductDao;
import com.macro.mall.selfcheck.dto.ProductScanParam;
import com.macro.mall.selfcheck.dto.ProductScanVO;
import com.macro.mall.selfcheck.service.SelfcheckProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自助收银商品服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckProductServiceImpl implements SelfcheckProductService {

    @Autowired
    private SelfcheckProductDao productDao;

    @Override
    public ProductScanVO scanProduct(ProductScanParam param) {
        try {
            log.info("开始扫码查询商品，条码：{}", param.getBarcode());

            // 验证条码不为空
            if (param.getBarcode() == null || param.getBarcode().trim().isEmpty()) {
                throw new RuntimeException("条码不能为空");
            }

            // 直接使用SKU编码精确查询
            ProductScanVO product = productDao.getProductByKeyword(param.getBarcode().trim());

            if (product == null) {
                log.warn("未找到对应的商品信息，条码：{}", param.getBarcode());
                throw new RuntimeException("商品不存在或已下架");
            }

            // 设置扫码时间
            product.setScanTime(System.currentTimeMillis());

            // 检查商品状态
            validateProductStatus(product);

            // 检查库存状态（如果需要）
            if (Boolean.TRUE.equals(param.getNeedStockCheck())) {
                checkAndUpdateStockStatus(product);
            }

            // 获取促销信息（如果需要）
            if (Boolean.TRUE.equals(param.getNeedPromotionInfo())) {
                enrichPromotionInfo(product);
            }

            // 计算销售状态
            calculateSaleableStatus(product);

            // 解析SKU规格信息
            parseSkuSpecInfo(product);

            log.info("商品扫码查询成功，商品ID：{}，商品名称：{}，扫码类型：{}",
                    product.getProductId(), product.getProductName(), product.getScanType());
            return product;

        } catch (Exception e) {
            log.error("商品扫码查询失败，条码：{}，错误：{}", param.getBarcode(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductScanVO getProductDetail(Long productId, Boolean needStockCheck, Boolean needPromotionInfo) {
        try {
            ProductScanVO product = productDao.getProductDetailById(productId);
            if (product == null) {
                log.warn("商品不存在，商品ID：{}", productId);
                return null;
            }

            // 检查库存状态
            if (Boolean.TRUE.equals(needStockCheck)) {
                checkAndUpdateStockStatus(product);
            }

            // 获取促销信息
            if (Boolean.TRUE.equals(needPromotionInfo)) {
                enrichPromotionInfo(product);
            }

            // 计算销售状态
            calculateSaleableStatus(product);

            return product;

        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID：{}，错误：{}", productId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ProductScanVO getSkuDetail(Long skuId, Boolean needStockCheck, Boolean needPromotionInfo) {
        try {
            ProductScanVO sku = productDao.getSkuDetailById(skuId);
            if (sku == null) {
                log.warn("SKU不存在，SKU ID：{}", skuId);
                return null;
            }

            // 检查库存状态
            if (Boolean.TRUE.equals(needStockCheck)) {
                checkAndUpdateStockStatus(sku);
            }

            // 获取促销信息
            if (Boolean.TRUE.equals(needPromotionInfo)) {
                enrichPromotionInfo(sku);
            }

            // 计算销售状态
            calculateSaleableStatus(sku);

            // 解析SKU规格信息
            parseSkuSpecInfo(sku);

            return sku;

        } catch (Exception e) {
            log.error("获取SKU详情失败，SKU ID：{}，错误：{}", skuId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean checkProductSaleable(Long productId, Long skuId, Integer quantity) {
        try {
            ProductScanVO product = productDao.checkProductStock(productId, skuId);
            if (product == null) {
                return false;
            }

            // 检查商品状态
            if (!"NORMAL".equals(product.getProductStatus()) || 
                product.getPublishStatus() == null || 
                product.getPublishStatus() != 1) {
                return false;
            }

            // 检查库存
            if (product.getStock() == null || product.getStock() < quantity) {
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("检查商品销售状态失败，商品ID：{}，SKU ID：{}，错误：{}", productId, skuId, e.getMessage());
            return false;
        }
    }

    @Override
    public ProductScanVO calculateProductPrice(Long productId, Long skuId, Long memberId, Integer quantity) {
        try {
            ProductScanVO product = skuId != null ? 
                    productDao.getSkuDetailById(skuId) : 
                    productDao.getProductDetailById(productId);

            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 获取促销信息
            enrichPromotionInfo(product);

            // 计算实际价格
            BigDecimal finalPrice = calculateFinalPrice(product, memberId, quantity);
            product.setCurrentPrice(finalPrice);

            log.info("商品价格计算完成，商品ID：{}，最终价格：{}", productId, finalPrice);
            return product;

        } catch (Exception e) {
            log.error("计算商品价格失败，商品ID：{}，错误：{}", productId, e.getMessage(), e);
            throw new RuntimeException("计算商品价格失败：" + e.getMessage());
        }
    }

    @Override
    public List<ProductScanVO> batchScanProducts(List<String> barcodes) {
        List<ProductScanVO> results = new ArrayList<>();

        for (String barcode : barcodes) {
            try {
                ProductScanParam param = new ProductScanParam();
                param.setBarcode(barcode);
                param.setScanType("BARCODE");
                param.setNeedStockCheck(true);
                param.setNeedPromotionInfo(true);

                ProductScanVO product = scanProduct(param);
                results.add(product);

            } catch (Exception e) {
                log.warn("批量扫码失败，条码：{}，错误：{}", barcode, e.getMessage());
                // 继续处理下一个条码，不中断整个批量操作
            }
        }

        log.info("批量扫码完成，成功：{}/{}个", results.size(), barcodes.size());
        return results;
    }

    @Override
    public String validateBarcodeFormat(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return "UNKNOWN";
        }

        // 简化验证逻辑，只要不为空就认为是有效的
        // 具体的匹配由数据库查询来处理
        return "VALID";
    }



    /**
     * 验证商品状态
     * @param product 商品信息
     */
    private void validateProductStatus(ProductScanVO product) {
        if (product.getProductStatus() == null) {
            throw new RuntimeException("商品状态异常");
        }

        switch (product.getProductStatus()) {
            case "OFF_SHELF":
                throw new RuntimeException("商品已下架");
            case "DELETED":
                throw new RuntimeException("商品已删除");
            case "NORMAL":
                // 正常状态，继续检查
                break;
            default:
                throw new RuntimeException("商品状态异常：" + product.getProductStatus());
        }

        // 检查上架状态
        if (product.getPublishStatus() == null || product.getPublishStatus() != 1) {
            throw new RuntimeException("商品未上架");
        }
    }

    /**
     * 检查并更新库存状态
     * @param product 商品信息
     */
    private void checkAndUpdateStockStatus(ProductScanVO product) {
        Integer stock = product.getStock();
        Integer lowStock = product.getLowStock();

        if (stock == null) {
            product.setStockStatus("UNKNOWN");
            return;
        }

        if (stock <= 0) {
            product.setStockStatus("OUT_OF_STOCK");
        } else if (lowStock != null && stock <= lowStock) {
            product.setStockStatus("LOW");
        } else {
            product.setStockStatus("SUFFICIENT");
        }
    }

    /**
     * 丰富促销信息
     * @param product 商品信息
     */
    private void enrichPromotionInfo(ProductScanVO product) {
        try {
            ProductScanVO promotionInfo = productDao.getProductPromotionInfo(product.getProductId());
            if (promotionInfo != null) {
                product.setPromotionType(promotionInfo.getPromotionType());
                product.setPromotionPrice(promotionInfo.getPromotionPrice());
                product.setPromotionStartTime(promotionInfo.getPromotionStartTime());
                product.setPromotionEndTime(promotionInfo.getPromotionEndTime());
            }
        } catch (Exception e) {
            log.warn("获取促销信息失败，商品ID：{}，错误：{}", product.getProductId(), e.getMessage());
        }
    }

    /**
     * 计算销售状态
     * @param product 商品信息
     */
    private void calculateSaleableStatus(ProductScanVO product) {
        boolean saleable = true;

        // 检查商品状态
        if (!"NORMAL".equals(product.getProductStatus()) || 
            product.getPublishStatus() == null || 
            product.getPublishStatus() != 1) {
            saleable = false;
        }

        // 检查库存状态
        if ("OUT_OF_STOCK".equals(product.getStockStatus())) {
            saleable = false;
        }

        product.setSaleable(saleable);
    }

    /**
     * 计算最终价格
     * @param product 商品信息
     * @param memberId 会员ID
     * @param quantity 购买数量
     * @return 最终价格
     */
    private BigDecimal calculateFinalPrice(ProductScanVO product, Long memberId, Integer quantity) {
        // 使用当前价格作为基准价格（SKU价格），确保与游客扫码结果一致
        BigDecimal basePrice = product.getCurrentPrice();
        if (basePrice == null) {
            // 如果当前价格为空，则使用原价作为备选
            basePrice = product.getOriginalPrice();
            if (basePrice == null) {
                basePrice = BigDecimal.ZERO;
            }
        }

        // 检查是否有促销价格
        if (product.getPromotionPrice() != null && isPromotionValid(product)) {
            return product.getPromotionPrice();
        }

        // TODO: 会员价格计算逻辑
        // if (memberId != null) {
        //     // 计算会员价格
        // }

        // TODO: 阶梯价格计算逻辑
        // if (quantity != null && quantity > 1) {
        //     // 计算批量价格
        // }

        return basePrice;
    }

    /**
     * 检查促销是否有效
     * @param product 商品信息
     * @return 是否有效
     */
    private boolean isPromotionValid(ProductScanVO product) {
        if (product.getPromotionType() == null || product.getPromotionType() == 0) {
            return false;
        }

        Date now = new Date();
        
        if (product.getPromotionStartTime() != null && product.getPromotionStartTime().after(now)) {
            return false;
        }

        if (product.getPromotionEndTime() != null && product.getPromotionEndTime().before(now)) {
            return false;
        }

        return true;
    }

    /**
     * 解析SKU规格信息
     * 将spData JSON数据解析为可读的规格信息字符串
     * @param product 商品信息
     */
    private void parseSkuSpecInfo(ProductScanVO product) {
        try {
            if (product.getSpData() == null || product.getSpData().trim().isEmpty()) {
                // 如果没有规格数据，使用品牌名称作为默认显示
                product.setSkuSpecInfo(product.getBrandName());
                return;
            }

            // 解析spData JSON，格式如：[{"key":"颜色","value":"红色"},{"key":"尺寸","value":"L"}]
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(product.getSpData());

            if (jsonNode.isArray() && jsonNode.size() > 0) {
                StringBuilder specInfo = new StringBuilder();

                for (int i = 0; i < jsonNode.size(); i++) {
                    com.fasterxml.jackson.databind.JsonNode spec = jsonNode.get(i);
                    if (spec.has("key") && spec.has("value")) {
                        if (specInfo.length() > 0) {
                            specInfo.append(" | ");
                        }
                        specInfo.append(spec.get("value").asText());
                    }
                }

                if (specInfo.length() > 0) {
                    product.setSkuSpecInfo(specInfo.toString());
                } else {
                    // 如果解析失败，使用品牌名称作为默认显示
                    product.setSkuSpecInfo(product.getBrandName());
                }
            } else {
                // 如果JSON格式不正确，使用品牌名称作为默认显示
                product.setSkuSpecInfo(product.getBrandName());
            }

        } catch (Exception e) {
            log.warn("解析SKU规格信息失败，商品ID：{}，spData：{}，错误：{}",
                    product.getProductId(), product.getSpData(), e.getMessage());
            // 解析失败时，使用品牌名称作为默认显示
            product.setSkuSpecInfo(product.getBrandName());
        }
    }
}