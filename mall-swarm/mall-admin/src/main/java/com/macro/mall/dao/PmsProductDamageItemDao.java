package com.macro.mall.dao;

import com.macro.mall.model.PmsProductDamageItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品报损明细自定义Dao
 */
public interface PmsProductDamageItemDao {
    
    /**
     * 根据报损单ID查询明细列表
     */
    List<PmsProductDamageItem> selectByDamageReportId(@Param("damageReportId") Long damageReportId);
    
    /**
     * 批量插入明细
     */
    int batchInsert(@Param("list") List<PmsProductDamageItem> list);
    
    /**
     * 根据报损单ID删除明细
     */
    int deleteByDamageReportId(@Param("damageReportId") Long damageReportId);
}
