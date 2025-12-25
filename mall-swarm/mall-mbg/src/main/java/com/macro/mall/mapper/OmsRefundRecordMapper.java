package com.macro.mall.mapper;

import com.macro.mall.model.OmsRefundRecord;
import com.macro.mall.model.OmsRefundRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsRefundRecordMapper {
    long countByExample(OmsRefundRecordExample example);

    int deleteByExample(OmsRefundRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsRefundRecord row);

    int insertSelective(OmsRefundRecord row);

    List<OmsRefundRecord> selectByExample(OmsRefundRecordExample example);

    OmsRefundRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsRefundRecord row, @Param("example") OmsRefundRecordExample example);

    int updateByExample(@Param("row") OmsRefundRecord row, @Param("example") OmsRefundRecordExample example);

    int updateByPrimaryKeySelective(OmsRefundRecord row);

    int updateByPrimaryKey(OmsRefundRecord row);
}