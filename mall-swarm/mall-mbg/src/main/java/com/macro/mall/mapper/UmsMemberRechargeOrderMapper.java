package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberRechargeOrder;
import com.macro.mall.model.UmsMemberRechargeOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberRechargeOrderMapper {
    long countByExample(UmsMemberRechargeOrderExample example);

    int deleteByExample(UmsMemberRechargeOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberRechargeOrder row);

    int insertSelective(UmsMemberRechargeOrder row);

    List<UmsMemberRechargeOrder> selectByExample(UmsMemberRechargeOrderExample example);

    UmsMemberRechargeOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberRechargeOrder row, @Param("example") UmsMemberRechargeOrderExample example);

    int updateByExample(@Param("row") UmsMemberRechargeOrder row, @Param("example") UmsMemberRechargeOrderExample example);

    int updateByPrimaryKeySelective(UmsMemberRechargeOrder row);

    int updateByPrimaryKey(UmsMemberRechargeOrder row);
}