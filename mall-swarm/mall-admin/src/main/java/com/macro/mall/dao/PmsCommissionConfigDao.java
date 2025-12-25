package com.macro.mall.dao;

import com.macro.mall.dto.PmsCommissionConfigQueryParam;
import com.macro.mall.dto.PmsCommissionConfigVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 佣金配置数据访问接口
 */
public interface PmsCommissionConfigDao {
    
    /**
     * 分页查询佣金配置列表
     * @param queryParam 查询参数
     * @return 佣金配置列表
     */
    List<PmsCommissionConfigVO> selectByPage(@Param("param") PmsCommissionConfigQueryParam queryParam);
    
    /**
     * 获取佣金配置详情（包含关联信息）
     * @param id 配置ID
     * @return 佣金配置详情
     */
    PmsCommissionConfigVO selectDetailById(@Param("id") Long id);
    
    /**
     * 获取佣金配置统计数据
     * @return 统计数据Map
     */
    Map<String, Object> selectStatistics();
    
    /**
     * 根据商品分类ID获取生效的佣金配置
     * @param productCategoryId 商品分类ID
     * @return 佣金配置信息
     */
    PmsCommissionConfigVO selectActiveByCategoryId(@Param("productCategoryId") Long productCategoryId);
    
    /**
     * 获取全局默认佣金配置
     * @return 佣金配置信息
     */
    PmsCommissionConfigVO selectGlobalDefault();
    
    /**
     * 检查配置名称是否重复
     * @param configName 配置名称
     * @param excludeId 排除的配置ID（用于更新时检查）
     * @return 重复数量
     */
    int checkConfigNameExists(@Param("configName") String configName, @Param("excludeId") Long excludeId);
    
    /**
     * 更新配置状态
     * @param id 配置ID
     * @param isActive 启用状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("isActive") Byte isActive);
    
    /**
     * 批量更新配置状态
     * @param ids 配置ID列表
     * @param isActive 启用状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isActive") Byte isActive);
    
    /**
     * 获取配置应用的产品数量
     * @param configId 配置ID
     * @return 产品数量
     */
    Integer countAppliedProducts(@Param("configId") Long configId);
    
    /**
     * 获取配置累计发放的佣金
     * @param configId 配置ID
     * @return 累计佣金
     */
    BigDecimal getTotalCommission(@Param("configId") Long configId);
} 