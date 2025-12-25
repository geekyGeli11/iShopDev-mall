package com.macro.mall.mapper;

import com.macro.mall.model.UmsAiStylizationRecord;
import com.macro.mall.model.UmsAiStylizationRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsAiStylizationRecordMapper {
    long countByExample(UmsAiStylizationRecordExample example);

    int deleteByExample(UmsAiStylizationRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsAiStylizationRecord row);

    int insertSelective(UmsAiStylizationRecord row);

    List<UmsAiStylizationRecord> selectByExample(UmsAiStylizationRecordExample example);

    UmsAiStylizationRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsAiStylizationRecord row, @Param("example") UmsAiStylizationRecordExample example);

    int updateByExample(@Param("row") UmsAiStylizationRecord row, @Param("example") UmsAiStylizationRecordExample example);

    int updateByPrimaryKeySelective(UmsAiStylizationRecord row);

    int updateByPrimaryKey(UmsAiStylizationRecord row);
}