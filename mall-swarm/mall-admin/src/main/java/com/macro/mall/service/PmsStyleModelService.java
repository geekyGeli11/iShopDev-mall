package com.macro.mall.service;

import com.macro.mall.dto.PmsStyleModelParam;
import com.macro.mall.dto.PmsStyleModelQueryParam;
import com.macro.mall.model.PmsStyleModel;
import com.macro.mall.model.PmsProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 风格模型Service
 * Created by macro on 2024/8/25.
 */
public interface PmsStyleModelService {
    /**
     * 获取所有风格模型
     */
    List<PmsStyleModel> listAllStyleModel();

    /**
     * 分页查询风格模型
     */
    List<PmsStyleModel> list(PmsStyleModelQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 创建风格模型
     */
    int createStyleModel(PmsStyleModelParam styleModelParam);

    /**
     * 修改风格模型
     */
    @Transactional
    int updateStyleModel(Long id, PmsStyleModelParam styleModelParam);

    /**
     * 删除风格模型
     */
    int deleteStyleModel(Long id);

    /**
     * 批量删除风格模型
     */
    int deleteStyleModel(List<Long> ids);

    /**
     * 分页获取风格模型详情
     */
    PmsStyleModel getStyleModel(Long id);

    /**
     * 修改风格模型状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 批量修改风格模型状态
     */
    int updateStatus(List<Long> ids, Integer status);

    /**
     * 获取风格模型关联的商品列表
     */
    List<PmsProduct> getStyleModelProducts(Long styleModelId, Integer pageSize, Integer pageNum);

    /**
     * 添加商品到风格模型
     */
    @Transactional
    int addProductsToStyleModel(Long styleModelId, List<Long> productIds);

    /**
     * 从风格模型移除商品
     */
    @Transactional
    int removeProductsFromStyleModel(Long styleModelId, List<Long> productIds);

    /**
     * 批量操作
     */
    int batchOperate(List<Long> ids, String operateType);
}
