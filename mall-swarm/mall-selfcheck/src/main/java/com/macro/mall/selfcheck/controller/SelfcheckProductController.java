package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.ProductScanParam;
import com.macro.mall.selfcheck.dto.ProductScanVO;
import com.macro.mall.selfcheck.service.SelfcheckProductService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自助收银商品管理Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/product")
@Tag(name = "SelfcheckProductController", description = "自助收银商品管理")
public class SelfcheckProductController {

    @Autowired
    private SelfcheckProductService productService;

    @Operation(summary = "扫码查询商品信息")
    @PostMapping("/scan")
    public CommonResult<ProductScanVO> scanProduct(@Valid @RequestBody ProductScanParam param) {
        try {
            // 检查会话状态（游客或会员都可以扫码）
            boolean isLogin = StpMemberUtil.isLogin();
            Long memberId = null;
            
            if (isLogin) {
                memberId = StpMemberUtil.getLoginIdAsLong();
                log.info("会员扫码查询商品，会员ID：{}，条码：{}", memberId, param.getBarcode());
            } else {
                log.info("游客扫码查询商品，条码：{}", param.getBarcode());
            }

            // 扫码查询商品
            ProductScanVO product = productService.scanProduct(param);
            if (product == null) {
                return CommonResult.failed("商品不存在或已下架");
            }

            // 添加调试日志，帮助排查SKU显示问题
            log.info("扫码查询结果 - 商品ID：{}，SKU ID：{}，商品名称：{}，SKU规格：{}，价格：{}",
                    product.getProductId(), product.getSkuId(), product.getProductName(),
                    product.getSkuSpecInfo(), product.getCurrentPrice());

            // 如果是会员，可以计算会员价格
            if (memberId != null && memberId > 0) {
                try {
                    ProductScanVO priceInfo = productService.calculateProductPrice(
                        product.getProductId(), 
                        product.getSkuId(), 
                        memberId, 
                        1
                    );
                    // 更新价格信息
                    product.setCurrentPrice(priceInfo.getCurrentPrice());
                    product.setPromotionPrice(priceInfo.getPromotionPrice());
                    product.setPromotionType(priceInfo.getPromotionType());
                } catch (Exception e) {
                    log.warn("计算会员价格失败，商品ID：{}，会员ID：{}，错误：{}", 
                            product.getProductId(), memberId, e.getMessage());
                }
            }

            return CommonResult.success(product);

        } catch (Exception e) {
            log.error("扫码查询商品失败，条码：{}，错误：{}", param.getBarcode(), e.getMessage(), e);
            return CommonResult.failed("扫码查询失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据商品ID获取商品详情")
    @GetMapping("/detail/{productId}")
    public CommonResult<ProductScanVO> getProductDetail(
            @Parameter(description = "商品ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "是否需要库存检查") 
            @RequestParam(defaultValue = "true") Boolean needStockCheck,
            @Parameter(description = "是否需要促销信息") 
            @RequestParam(defaultValue = "true") Boolean needPromotionInfo) {
        try {
            ProductScanVO product = productService.getProductDetail(productId, needStockCheck, needPromotionInfo);
            if (product == null) {
                return CommonResult.failed("商品不存在：" + productId);
            }

            // 如果是会员登录，计算会员价格
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                if (memberId > 0) {
                    try {
                        ProductScanVO priceInfo = productService.calculateProductPrice(
                            productId, product.getSkuId(), memberId, 1
                        );
                        product.setCurrentPrice(priceInfo.getCurrentPrice());
                    } catch (Exception e) {
                        log.warn("计算会员价格失败，商品ID：{}，会员ID：{}", productId, memberId);
                    }
                }
            }

            return CommonResult.success(product);

        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID：{}，错误：{}", productId, e.getMessage(), e);
            return CommonResult.failed("获取商品详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据SKU ID获取SKU详情")
    @GetMapping("/sku/{skuId}")
    public CommonResult<ProductScanVO> getSkuDetail(
            @Parameter(description = "SKU ID", required = true)
            @PathVariable Long skuId,
            @Parameter(description = "是否需要库存检查") 
            @RequestParam(defaultValue = "true") Boolean needStockCheck,
            @Parameter(description = "是否需要促销信息") 
            @RequestParam(defaultValue = "true") Boolean needPromotionInfo) {
        try {
            ProductScanVO sku = productService.getSkuDetail(skuId, needStockCheck, needPromotionInfo);
            if (sku == null) {
                return CommonResult.failed("SKU不存在：" + skuId);
            }

            // 如果是会员登录，计算会员价格
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                if (memberId > 0) {
                    try {
                        ProductScanVO priceInfo = productService.calculateProductPrice(
                            sku.getProductId(), skuId, memberId, 1
                        );
                        sku.setCurrentPrice(priceInfo.getCurrentPrice());
                    } catch (Exception e) {
                        log.warn("计算会员价格失败，SKU ID：{}，会员ID：{}", skuId, memberId);
                    }
                }
            }

            return CommonResult.success(sku);

        } catch (Exception e) {
            log.error("获取SKU详情失败，SKU ID：{}，错误：{}", skuId, e.getMessage(), e);
            return CommonResult.failed("获取SKU详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查商品是否可以销售")
    @PostMapping("/checkSaleable")
    public CommonResult<Map<String, Object>> checkProductSaleable(
            @Parameter(description = "商品ID", required = true) 
            @RequestParam Long productId,
            @Parameter(description = "SKU ID") 
            @RequestParam(required = false) Long skuId,
            @Parameter(description = "购买数量") 
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            boolean saleable = productService.checkProductSaleable(productId, skuId, quantity);

            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("skuId", skuId);
            result.put("quantity", quantity);
            result.put("saleable", saleable);
            result.put("message", saleable ? "商品可以销售" : "商品不可销售");

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("检查商品销售状态失败，商品ID：{}，SKU ID：{}，错误：{}", productId, skuId, e.getMessage(), e);
            return CommonResult.failed("检查商品销售状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "计算商品价格")
    @PostMapping("/calculatePrice")
    public CommonResult<ProductScanVO> calculateProductPrice(
            @Parameter(description = "商品ID", required = true) 
            @RequestParam Long productId,
            @Parameter(description = "SKU ID") 
            @RequestParam(required = false) Long skuId,
            @Parameter(description = "购买数量") 
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Long memberId = null;
            
            // 获取会员ID（如果已登录）
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
                if (memberId <= 0) {
                    memberId = null; // 游客模式
                }
            }

            ProductScanVO product = productService.calculateProductPrice(productId, skuId, memberId, quantity);

            log.info("商品价格计算完成，商品ID：{}，会员ID：{}，最终价格：{}", 
                    productId, memberId, product.getCurrentPrice());

            return CommonResult.success(product);

        } catch (Exception e) {
            log.error("计算商品价格失败，商品ID：{}，SKU ID：{}，错误：{}", productId, skuId, e.getMessage(), e);
            return CommonResult.failed("计算商品价格失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量扫码查询商品")
    @PostMapping("/batchScan")
    public CommonResult<Map<String, Object>> batchScanProducts(
            @Parameter(description = "条码列表", required = true)
            @RequestBody List<String> barcodes) {
        try {
            if (barcodes == null || barcodes.isEmpty()) {
                return CommonResult.validateFailed("条码列表不能为空");
            }

            if (barcodes.size() > 20) {
                return CommonResult.validateFailed("批量扫码数量不能超过20个");
            }

            List<ProductScanVO> products = productService.batchScanProducts(barcodes);

            // 如果是会员登录，为所有商品计算会员价格
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                if (memberId > 0) {
                    for (ProductScanVO product : products) {
                        try {
                            ProductScanVO priceInfo = productService.calculateProductPrice(
                                product.getProductId(), product.getSkuId(), memberId, 1
                            );
                            product.setCurrentPrice(priceInfo.getCurrentPrice());
                        } catch (Exception e) {
                            log.warn("计算会员价格失败，商品ID：{}", product.getProductId());
                        }
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", barcodes.size());
            result.put("success", products.size());
            result.put("failed", barcodes.size() - products.size());
            result.put("products", products);

            log.info("批量扫码查询完成，总数：{}，成功：{}", barcodes.size(), products.size());

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("批量扫码查询失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("批量扫码查询失败：" + e.getMessage());
        }
    }

    @Operation(summary = "验证条码格式")
    @GetMapping("/validateBarcode")
    public CommonResult<Map<String, Object>> validateBarcodeFormat(
            @Parameter(description = "条码", required = true)
            @RequestParam String barcode) {
        try {
            String barcodeType = productService.validateBarcodeFormat(barcode);

            Map<String, Object> result = new HashMap<>();
            result.put("barcode", barcode);
            result.put("type", barcodeType);
            result.put("valid", !"UNKNOWN".equals(barcodeType));
            
            String description;
            switch (barcodeType) {
                case "BARCODE":
                    description = "商品条码";
                    break;
                case "SKU_CODE":
                    description = "SKU编码";
                    break;
                case "PRODUCT_SN":
                    description = "商品货号";
                    break;
                default:
                    description = "未知格式";
                    break;
            }
            result.put("description", description);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("验证条码格式失败，条码：{}，错误：{}", barcode, e.getMessage(), e);
            return CommonResult.failed("验证条码格式失败：" + e.getMessage());
        }
    }

    @Operation(summary = "快速扫码（简化版）")
    @GetMapping("/quickScan")
    public CommonResult<ProductScanVO> quickScan(
            @Parameter(description = "条码", required = true)
            @RequestParam String barcode) {
        try {
            // 构建简化的扫码参数
            ProductScanParam param = new ProductScanParam();
            param.setBarcode(barcode);
            param.setScanType("BARCODE"); // 默认商品条码
            param.setNeedStockCheck(true);
            param.setNeedPromotionInfo(true);

            ProductScanVO product = productService.scanProduct(param);
            if (product == null) {
                return CommonResult.failed("商品不存在或已下架");
            }

            // 如果是会员，计算会员价格
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                if (memberId > 0) {
                    try {
                        ProductScanVO priceInfo = productService.calculateProductPrice(
                            product.getProductId(), product.getSkuId(), memberId, 1
                        );
                        product.setCurrentPrice(priceInfo.getCurrentPrice());
                    } catch (Exception e) {
                        log.warn("计算会员价格失败，商品ID：{}", product.getProductId());
                    }
                }
            }

            return CommonResult.success(product);

        } catch (Exception e) {
            log.error("快速扫码失败，条码：{}，错误：{}", barcode, e.getMessage(), e);
            return CommonResult.failed("扫码失败：" + e.getMessage());
        }
    }
} 