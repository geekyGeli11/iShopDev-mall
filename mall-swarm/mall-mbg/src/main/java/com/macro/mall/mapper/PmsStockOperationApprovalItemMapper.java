package com.macro.mall.mapper;

import com.macro.mall.model.PmsStockOperationApprovalItem;
import com.macro.mall.model.PmsStockOperationApprovalItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStockOperationApprovalItemMapper {
    long countByExample(PmsStockOperationApprovalItemExample example);

    int deleteByExample(PmsStockOperationApprovalItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStockOperationApprovalItem row);

    int insertSelective(PmsStockOperationApprovalItem row);

    List<PmsStockOperationApprovalItem> selectByExample(PmsStockOperationApprovalItemExample example);

    PmsStockOperationApprovalItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStockOperationApprovalItem row, @Param("example") PmsStockOperationApprovalItemExample example);

    int updateByExample(@Param("row") PmsStockOperationApprovalItem row, @Param("example") PmsStockOperationApprovalItemExample example);

    int updateByPrimaryKeySelective(PmsStockOperationApprovalItem row);

    int updateByPrimaryKey(PmsStockOperationApprovalItem row);
}