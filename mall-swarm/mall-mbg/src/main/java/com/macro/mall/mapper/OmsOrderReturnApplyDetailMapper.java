package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderReturnApplyDetail;
import com.macro.mall.model.OmsOrderReturnApplyDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderReturnApplyDetailMapper {
    long countByExample(OmsOrderReturnApplyDetailExample example);

    int deleteByExample(OmsOrderReturnApplyDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderReturnApplyDetail row);

    int insertSelective(OmsOrderReturnApplyDetail row);

    List<OmsOrderReturnApplyDetail> selectByExample(OmsOrderReturnApplyDetailExample example);

    OmsOrderReturnApplyDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderReturnApplyDetail row, @Param("example") OmsOrderReturnApplyDetailExample example);

    int updateByExample(@Param("row") OmsOrderReturnApplyDetail row, @Param("example") OmsOrderReturnApplyDetailExample example);

    int updateByPrimaryKeySelective(OmsOrderReturnApplyDetail row);

    int updateByPrimaryKey(OmsOrderReturnApplyDetail row);
}