package com.macro.mall.mapper;

import com.macro.mall.model.PmsStockOperationApproval;
import com.macro.mall.model.PmsStockOperationApprovalExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStockOperationApprovalMapper {
    long countByExample(PmsStockOperationApprovalExample example);

    int deleteByExample(PmsStockOperationApprovalExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStockOperationApproval row);

    int insertSelective(PmsStockOperationApproval row);

    List<PmsStockOperationApproval> selectByExampleWithBLOBs(PmsStockOperationApprovalExample example);

    List<PmsStockOperationApproval> selectByExample(PmsStockOperationApprovalExample example);

    PmsStockOperationApproval selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStockOperationApproval row, @Param("example") PmsStockOperationApprovalExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsStockOperationApproval row, @Param("example") PmsStockOperationApprovalExample example);

    int updateByExample(@Param("row") PmsStockOperationApproval row, @Param("example") PmsStockOperationApprovalExample example);

    int updateByPrimaryKeySelective(PmsStockOperationApproval row);

    int updateByPrimaryKeyWithBLOBs(PmsStockOperationApproval row);

    int updateByPrimaryKey(PmsStockOperationApproval row);
}