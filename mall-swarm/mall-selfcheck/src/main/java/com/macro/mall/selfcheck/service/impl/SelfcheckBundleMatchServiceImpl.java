package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.mapper.PmsProductBundleItemMapper;
import com.macro.mall.mapper.PmsProductBundleMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import com.macro.mall.selfcheck.dto.BundleMatchResult;
import com.macro.mall.selfcheck.dto.OrderItemParam;
import com.macro.mall.selfcheck.service.SelfcheckBundleMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自助收银组合优惠匹配服务实现类
 *
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckBundleMatchServiceImpl implements SelfcheckBundleMatchService {

    @Autowired
    private PmsProductBundleMapper bundleMapper;

    @Autowired
    private PmsProductBundleItemMapper bundleItemMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public BundleMatchResult matchBundlePromotion(List<OrderItemParam> orderItems, Long schoolId) {
        log.info("开始检测组合优惠，订单商品数量：{}，学校ID：{}", orderItems.size(), schoolId);

        BundleMatchResult result = new BundleMatchResult();
        result.setMatched(false);

        if (CollectionUtils.isEmpty(orderItems)) {
            return result;
        }

        // 1. 获取所有已上架的组合商品
        List<PmsProductBundle> bundles = getAvailableBundles(schoolId);
        if (CollectionUtils.isEmpty(bundles)) {
            log.info("没有可用的组合商品");
            return result;
        }

        // 2. 构建订单商品映射（productId -> 总数量）
        Map<Long, Integer> orderProductQuantityMap = buildOrderProductQuantityMap(orderItems);

        // 3. 遍历组合商品，找到最优匹配（优惠金额最大的）
        BundleMatchResult bestMatch = null;
        BigDecimal maxSavedAmount = BigDecimal.ZERO;

        for (PmsProductBundle bundle : bundles) {
            BundleMatchResult matchResult = tryMatchBundle(bundle, orderProductQuantityMap, orderItems);
            if (matchResult.getMatched() && matchResult.getTotalSavedAmount().compareTo(maxSavedAmount) > 0) {
                bestMatch = matchResult;
                maxSavedAmount = matchResult.getTotalSavedAmount();
            }
        }

        if (bestMatch != null) {
            log.info("匹配到组合优惠：{}，优惠金额：{}", bestMatch.getBundleName(), bestMatch.getTotalSavedAmount());
            return bestMatch;
        }

        return result;
    }

    @Override
    public List<BundleMatchResult> matchAllBundlePromotions(List<OrderItemParam> orderItems, Long schoolId) {
        log.info("开始检测所有组合优惠，订单商品数量：{}，学校ID：{}", orderItems.size(), schoolId);

        List<BundleMatchResult> results = new ArrayList<>();

        if (CollectionUtils.isEmpty(orderItems)) {
            return results;
        }

        // 1. 获取所有已上架的组合商品
        List<PmsProductBundle> bundles = getAvailableBundles(schoolId);
        if (CollectionUtils.isEmpty(bundles)) {
            return results;
        }

        // 2. 复制一份订单商品数量用于消耗
        Map<Long, Integer> remainingQuantityMap = buildOrderProductQuantityMap(orderItems);
        List<OrderItemParam> remainingItems = new ArrayList<>(orderItems);

        // 3. 按优惠金额排序组合商品（优先匹配优惠大的）
        bundles.sort((a, b) -> {
            BigDecimal savedA = a.getOriginalPrice().subtract(a.getBundlePrice());
            BigDecimal savedB = b.getOriginalPrice().subtract(b.getBundlePrice());
            return savedB.compareTo(savedA);
        });

        // 4. 贪心匹配：依次尝试匹配每个组合
        for (PmsProductBundle bundle : bundles) {
            BundleMatchResult matchResult = tryMatchBundleWithRemaining(bundle, remainingQuantityMap, remainingItems);
            if (matchResult.getMatched()) {
                results.add(matchResult);
                // 更新剩余商品
                remainingItems = matchResult.getUnmatchedItems();
                remainingQuantityMap = buildOrderProductQuantityMap(remainingItems);
            }
        }

        log.info("共匹配到 {} 个组合优惠", results.size());
        return results;
    }

    /**
     * 获取可用的组合商品
     */
    private List<PmsProductBundle> getAvailableBundles(Long schoolId) {
        PmsProductBundleExample example = new PmsProductBundleExample();

        if (schoolId != null) {
            // 查询指定学校的组合商品 或 schoolId为null的通用组合商品
            PmsProductBundleExample.Criteria criteria1 = example.createCriteria();
            criteria1.andPublishStatusEqualTo((byte) 1);
            criteria1.andSchoolIdEqualTo(schoolId);

            PmsProductBundleExample.Criteria criteria2 = example.createCriteria();
            criteria2.andPublishStatusEqualTo((byte) 1);
            criteria2.andSchoolIdIsNull();

            example.or(criteria2);
        } else {
            // 未指定学校时，查询所有已上架的组合商品
            PmsProductBundleExample.Criteria criteria = example.createCriteria();
            criteria.andPublishStatusEqualTo((byte) 1);
        }

        example.setOrderByClause("sort desc, create_time desc");
        return bundleMapper.selectByExample(example);
    }

    /**
     * 构建订单商品数量映射
     */
    private Map<Long, Integer> buildOrderProductQuantityMap(List<OrderItemParam> orderItems) {
        Map<Long, Integer> map = new HashMap<>();
        for (OrderItemParam item : orderItems) {
            map.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return map;
    }

    /**
     * 尝试匹配单个组合商品
     */
    private BundleMatchResult tryMatchBundle(PmsProductBundle bundle,
                                              Map<Long, Integer> orderProductQuantityMap,
                                              List<OrderItemParam> orderItems) {
        BundleMatchResult result = new BundleMatchResult();
        result.setMatched(false);

        // 获取组合内的商品
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(bundle.getId());
        List<PmsProductBundleItem> bundleItems = bundleItemMapper.selectByExample(itemExample);

        if (CollectionUtils.isEmpty(bundleItems)) {
            return result;
        }

        // 检查订单是否包含组合所需的所有商品，并计算可匹配的组合数量
        int maxMatchCount = Integer.MAX_VALUE;
        for (PmsProductBundleItem bundleItem : bundleItems) {
            Integer orderQuantity = orderProductQuantityMap.get(bundleItem.getProductId());
            if (orderQuantity == null || orderQuantity < bundleItem.getQuantity()) {
                // 订单中没有该商品或数量不足
                return result;
            }
            // 计算该商品可以匹配多少个组合
            int matchCount = orderQuantity / bundleItem.getQuantity();
            maxMatchCount = Math.min(maxMatchCount, matchCount);
        }

        if (maxMatchCount <= 0) {
            return result;
        }

        // 匹配成功，计算优惠
        result.setMatched(true);
        result.setBundleId(bundle.getId());
        result.setBundleName(bundle.getName());
        result.setOriginalPrice(bundle.getOriginalPrice());
        result.setBundlePrice(bundle.getBundlePrice());
        result.setSavedAmount(bundle.getOriginalPrice().subtract(bundle.getBundlePrice()));
        result.setMatchedQuantity(maxMatchCount);
        result.setTotalSavedAmount(result.getSavedAmount().multiply(new BigDecimal(maxMatchCount)));

        // 计算优惠分摊
        Map<Long, BigDecimal> discountDistribution = calculateDiscountDistribution(bundleItems, result.getSavedAmount());
        result.setDiscountDistribution(discountDistribution);

        // 构建匹配详情
        List<BundleMatchResult.MatchedItem> matchedItems = buildMatchedItems(bundleItems, discountDistribution, maxMatchCount);
        result.setMatchedItems(matchedItems);

        // 计算剩余商品
        List<OrderItemParam> unmatchedItems = calculateUnmatchedItems(orderItems, bundleItems, maxMatchCount);
        result.setUnmatchedItems(unmatchedItems);

        // 计算SKU级别的优惠分摊
        Map<Long, BigDecimal> skuDiscountDistribution = calculateSkuDiscountDistribution(orderItems, bundleItems, discountDistribution, maxMatchCount);
        result.setSkuDiscountDistribution(skuDiscountDistribution);

        return result;
    }

    /**
     * 尝试匹配组合商品（使用剩余商品）
     */
    private BundleMatchResult tryMatchBundleWithRemaining(PmsProductBundle bundle,
                                                           Map<Long, Integer> remainingQuantityMap,
                                                           List<OrderItemParam> remainingItems) {
        return tryMatchBundle(bundle, remainingQuantityMap, remainingItems);
    }

    /**
     * 计算优惠分摊（按商品原价比例分摊）
     */
    private Map<Long, BigDecimal> calculateDiscountDistribution(List<PmsProductBundleItem> bundleItems,
                                                                  BigDecimal totalSaved) {
        Map<Long, BigDecimal> result = new HashMap<>();

        // 计算原价总和
        BigDecimal totalOriginal = BigDecimal.ZERO;
        Map<Long, BigDecimal> itemPriceMap = new HashMap<>();

        for (PmsProductBundleItem item : bundleItems) {
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product != null && product.getPrice() != null) {
                BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
                itemPriceMap.put(item.getProductId(), itemTotal);
                totalOriginal = totalOriginal.add(itemTotal);
            }
        }

        // 按比例分摊优惠
        BigDecimal distributedTotal = BigDecimal.ZERO;
        int index = 0;
        for (PmsProductBundleItem item : bundleItems) {
            index++;
            BigDecimal itemPrice = itemPriceMap.getOrDefault(item.getProductId(), BigDecimal.ZERO);

            BigDecimal discount;
            if (index == bundleItems.size()) {
                // 最后一个商品，用剩余的优惠金额，避免精度问题
                discount = totalSaved.subtract(distributedTotal);
            } else {
                // 按比例计算
                if (totalOriginal.compareTo(BigDecimal.ZERO) > 0) {
                    discount = totalSaved.multiply(itemPrice)
                            .divide(totalOriginal, 2, RoundingMode.HALF_UP);
                } else {
                    discount = BigDecimal.ZERO;
                }
            }

            result.put(item.getProductId(), discount);
            distributedTotal = distributedTotal.add(discount);
        }

        return result;
    }

    /**
     * 构建匹配详情
     */
    private List<BundleMatchResult.MatchedItem> buildMatchedItems(List<PmsProductBundleItem> bundleItems,
                                                                    Map<Long, BigDecimal> discountDistribution,
                                                                    int matchedQuantity) {
        List<BundleMatchResult.MatchedItem> matchedItems = new ArrayList<>();

        for (PmsProductBundleItem bundleItem : bundleItems) {
            PmsProduct product = productMapper.selectByPrimaryKey(bundleItem.getProductId());

            BundleMatchResult.MatchedItem item = new BundleMatchResult.MatchedItem();
            item.setProductId(bundleItem.getProductId());
            item.setProductName(bundleItem.getProductName());
            item.setQuantity(bundleItem.getQuantity() * matchedQuantity);

            if (product != null) {
                item.setOriginalPrice(product.getPrice());
                BigDecimal discount = discountDistribution.getOrDefault(bundleItem.getProductId(), BigDecimal.ZERO);
                // 计算单个商品的优惠后单价
                BigDecimal totalDiscount = discount.multiply(new BigDecimal(matchedQuantity));
                BigDecimal totalOriginal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setDiscountedPrice(totalOriginal.subtract(totalDiscount)
                        .divide(new BigDecimal(item.getQuantity()), 2, RoundingMode.HALF_UP));
                item.setDiscountAmount(totalDiscount);
            }

            matchedItems.add(item);
        }

        return matchedItems;
    }

    /**
     * 计算剩余未匹配的商品
     */
    private List<OrderItemParam> calculateUnmatchedItems(List<OrderItemParam> orderItems,
                                                          List<PmsProductBundleItem> bundleItems,
                                                          int matchedQuantity) {
        // 构建组合商品消耗映射
        Map<Long, Integer> consumedMap = new HashMap<>();
        for (PmsProductBundleItem bundleItem : bundleItems) {
            consumedMap.put(bundleItem.getProductId(), bundleItem.getQuantity() * matchedQuantity);
        }

        // 计算剩余商品
        List<OrderItemParam> unmatchedItems = new ArrayList<>();
        for (OrderItemParam orderItem : orderItems) {
            Integer consumed = consumedMap.getOrDefault(orderItem.getProductId(), 0);
            int remaining = orderItem.getQuantity() - consumed;

            if (remaining > 0) {
                OrderItemParam newItem = new OrderItemParam();
                newItem.setProductId(orderItem.getProductId());
                newItem.setSkuId(orderItem.getSkuId());
                newItem.setQuantity(remaining);
                newItem.setUnitPrice(orderItem.getUnitPrice());
                newItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(remaining)));
                unmatchedItems.add(newItem);
            }

            // 更新消耗映射（处理同一商品多个SKU的情况）
            if (consumed > 0) {
                int newConsumed = Math.max(0, consumed - orderItem.getQuantity());
                consumedMap.put(orderItem.getProductId(), newConsumed);
            }
        }

        return unmatchedItems;
    }

    /**
     * 计算SKU级别的优惠分摊
     */
    private Map<Long, BigDecimal> calculateSkuDiscountDistribution(List<OrderItemParam> orderItems,
                                                                     List<PmsProductBundleItem> bundleItems,
                                                                     Map<Long, BigDecimal> productDiscountMap,
                                                                     int matchedQuantity) {
        Map<Long, BigDecimal> skuDiscountMap = new HashMap<>();

        // 构建组合商品消耗映射
        Map<Long, Integer> consumedMap = new HashMap<>();
        for (PmsProductBundleItem bundleItem : bundleItems) {
            consumedMap.put(bundleItem.getProductId(), bundleItem.getQuantity() * matchedQuantity);
        }

        // 按商品分组订单项
        Map<Long, List<OrderItemParam>> productOrderItemsMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemParam::getProductId));

        // 为每个商品的SKU分摊优惠
        for (Map.Entry<Long, BigDecimal> entry : productDiscountMap.entrySet()) {
            Long productId = entry.getKey();
            BigDecimal productDiscount = entry.getValue().multiply(new BigDecimal(matchedQuantity));
            List<OrderItemParam> productItems = productOrderItemsMap.get(productId);

            if (productItems == null || productItems.isEmpty()) {
                continue;
            }

            // 计算该商品的总数量
            int totalQuantity = productItems.stream().mapToInt(OrderItemParam::getQuantity).sum();
            int consumedQuantity = consumedMap.getOrDefault(productId, 0);

            // 按SKU数量比例分摊优惠
            BigDecimal distributedDiscount = BigDecimal.ZERO;
            for (int i = 0; i < productItems.size(); i++) {
                OrderItemParam item = productItems.get(i);
                int skuConsumed = Math.min(item.getQuantity(), consumedQuantity);

                if (skuConsumed > 0) {
                    BigDecimal skuDiscount;
                    if (i == productItems.size() - 1) {
                        // 最后一个SKU，用剩余优惠
                        skuDiscount = productDiscount.subtract(distributedDiscount);
                    } else {
                        // 按比例计算
                        skuDiscount = productDiscount.multiply(new BigDecimal(skuConsumed))
                                .divide(new BigDecimal(consumedQuantity), 2, RoundingMode.HALF_UP);
                    }

                    if (item.getSkuId() != null) {
                        skuDiscountMap.put(item.getSkuId(), skuDiscount);
                    }
                    distributedDiscount = distributedDiscount.add(skuDiscount);
                    consumedQuantity -= skuConsumed;
                }
            }
        }

        return skuDiscountMap;
    }
}
