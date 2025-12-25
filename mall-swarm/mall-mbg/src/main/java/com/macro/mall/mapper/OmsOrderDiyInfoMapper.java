package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.OmsOrderDiyInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderDiyInfoMapper {
    long countByExample(OmsOrderDiyInfoExample example);

    int deleteByExample(OmsOrderDiyInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderDiyInfo row);

    int insertSelective(OmsOrderDiyInfo row);

    List<OmsOrderDiyInfo> selectByExampleWithBLOBs(OmsOrderDiyInfoExample example);

    List<OmsOrderDiyInfo> selectByExample(OmsOrderDiyInfoExample example);

    OmsOrderDiyInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderDiyInfo row, @Param("example") OmsOrderDiyInfoExample example);

    int updateByExampleWithBLOBs(@Param("row") OmsOrderDiyInfo row, @Param("example") OmsOrderDiyInfoExample example);

    int updateByExample(@Param("row") OmsOrderDiyInfo row, @Param("example") OmsOrderDiyInfoExample example);

    int updateByPrimaryKeySelective(OmsOrderDiyInfo row);

    int updateByPrimaryKeyWithBLOBs(OmsOrderDiyInfo row);

    int updateByPrimaryKey(OmsOrderDiyInfo row);
}