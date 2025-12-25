package com.macro.mall.service;

import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.PmsProductSchoolRelation;

import java.util.List;

/**
 * 商品学校关联Service
 */
public interface PmsProductSchoolRelationService {

    /**
     * 批量更新商品关联的学校
     * @param productId 商品ID
     * @param schoolIds 学校ID列表
     * @return 操作结果
     */
    boolean updateProductSchools(Long productId, List<Long> schoolIds);

    /**
     * 根据商品ID获取关联的学校列表
     * @param productId 商品ID
     * @return 学校列表
     */
    List<OmsSchool> getSchoolsByProductId(Long productId);

    /**
     * 根据学校ID获取关联的商品ID列表
     * @param schoolId 学校ID
     * @return 商品ID列表
     */
    List<Long> getProductIdsBySchoolId(Long schoolId);

    /**
     * 删除商品的所有学校关联
     * @param productId 商品ID
     * @return 操作结果
     */
    boolean deleteByProductId(Long productId);

    /**
     * 删除学校的所有商品关联
     * @param schoolId 学校ID
     * @return 操作结果
     */
    boolean deleteBySchoolId(Long schoolId);

    /**
     * 检查商品是否关联了指定学校
     * @param productId 商品ID
     * @param schoolId 学校ID
     * @return 是否关联
     */
    boolean isProductAssociatedWithSchool(Long productId, Long schoolId);
}
