package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.model.UmsMemberBalanceHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberBalanceHistoryMapper {
    long countByExample(UmsMemberBalanceHistoryExample example);

    int deleteByExample(UmsMemberBalanceHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberBalanceHistory row);

    int insertSelective(UmsMemberBalanceHistory row);

    List<UmsMemberBalanceHistory> selectByExample(UmsMemberBalanceHistoryExample example);

    UmsMemberBalanceHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberBalanceHistory row, @Param("example") UmsMemberBalanceHistoryExample example);

    int updateByExample(@Param("row") UmsMemberBalanceHistory row, @Param("example") UmsMemberBalanceHistoryExample example);

    int updateByPrimaryKeySelective(UmsMemberBalanceHistory row);

    int updateByPrimaryKey(UmsMemberBalanceHistory row);
}