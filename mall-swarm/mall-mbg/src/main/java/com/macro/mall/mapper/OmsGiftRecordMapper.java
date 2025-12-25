package com.macro.mall.mapper;

import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.model.OmsGiftRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsGiftRecordMapper {
    long countByExample(OmsGiftRecordExample example);

    int deleteByExample(OmsGiftRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsGiftRecord row);

    int insertSelective(OmsGiftRecord row);

    List<OmsGiftRecord> selectByExample(OmsGiftRecordExample example);

    OmsGiftRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsGiftRecord row, @Param("example") OmsGiftRecordExample example);

    int updateByExample(@Param("row") OmsGiftRecord row, @Param("example") OmsGiftRecordExample example);

    int updateByPrimaryKeySelective(OmsGiftRecord row);

    int updateByPrimaryKey(OmsGiftRecord row);
}