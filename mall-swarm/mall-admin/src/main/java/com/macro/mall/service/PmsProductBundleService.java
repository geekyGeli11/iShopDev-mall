package com.macro.mall.service;

import com.macro.mall.dto.PmsProductBundleDetail;
import com.macro.mall.dto.PmsProductBundleParam;
import com.macro.mall.model.PmsProductBundle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 组合商品Service
 */
public interface PmsProductBundleService {

    /**
     * 分页查询组合商品
     * @param keyword 关键词（组合名称）
     * @param publishStatus 上架状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 组合商品列表
     */
    List<PmsProductBundle> list(String keyword, Integer publishStatus, Integer pageNum, Integer pageSize);

    /**
     * 创建组合商品
     * @param param 组合商品参数
     * @return 创建结果
     */
    @Transactional
    int create(PmsProductBundleParam param);

    /**
     * 更新组合商品
     * @param id 组合商品ID
     * @param param 组合商品参数
     * @return 更新结果
     */
    @Transactional
    int update(Long id, PmsProductBundleParam param);

    /**
     * 获取组合商品详情
     * @param id 组合商品ID
     * @return 组合商品详情
     */
    PmsProductBundleDetail getDetail(Long id);

    /**
     * 删除组合商品
     * @param id 组合商品ID
     * @return 删除结果
     */
    @Transactional
    int delete(Long id);

    /**
     * 批量删除组合商品
     * @param ids 组合商品ID列表
     * @return 删除结果
     */
    @Transactional
    int delete(List<Long> ids);

    /**
     * 更新上架状态
     * @param ids 组合商品ID列表
     * @param publishStatus 上架状态
     * @return 更新结果
     */
    int updatePublishStatus(List<Long> ids, Integer publishStatus);
}
