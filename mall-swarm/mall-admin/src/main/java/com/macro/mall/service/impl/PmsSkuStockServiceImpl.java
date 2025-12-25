package com.macro.mall.service.impl;

import com.macro.mall.dao.PmsSkuStockDao;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.PmsStoreSkuStockExample;
import com.macro.mall.dto.PmsSkuStockWithStoreDTO;
import com.macro.mall.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品sku库存管理Service实现类
 * Created by macro on 2018/4/27.
 */
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andSkuCodeLike("%" + keyword + "%");
        }
        return skuStockMapper.selectByExample(example);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        return skuStockDao.replaceList(skuStockList);
    }

    @Override
    public List<PmsSkuStockWithStoreDTO> getListWithStoreStock(Long pid, String keyword) {
        // 获取SKU库存列表
        List<PmsSkuStock> skuStockList = getList(pid, keyword);
        
        // 转换为DTO并添加门店库存信息
        return skuStockList.stream().map(skuStock -> {
            PmsSkuStockWithStoreDTO dto = new PmsSkuStockWithStoreDTO();
            // 复制SKU库存信息
            dto.setId(skuStock.getId());
            dto.setProductId(skuStock.getProductId());
            dto.setSkuCode(skuStock.getSkuCode());
            dto.setPrice(skuStock.getPrice());
            dto.setStock(skuStock.getStock());
            dto.setLowStock(skuStock.getLowStock());
            dto.setPic(skuStock.getPic());
            dto.setSale(skuStock.getSale());
            dto.setPromotionPrice(skuStock.getPromotionPrice());
            dto.setLockStock(skuStock.getLockStock());
            dto.setSpData(skuStock.getSpData());
            
            // 获取该SKU的所有门店库存
            PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
            example.createCriteria().andSkuIdEqualTo(skuStock.getId());
            List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
            dto.setStoreStocks(storeStocks);
            
            return dto;
        }).collect(Collectors.toList());
    }
}
