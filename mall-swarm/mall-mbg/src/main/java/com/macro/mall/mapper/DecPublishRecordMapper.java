package com.macro.mall.mapper;

import com.macro.mall.model.DecPublishRecord;
import com.macro.mall.model.DecPublishRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DecPublishRecordMapper {
    long countByExample(DecPublishRecordExample example);

    int deleteByExample(DecPublishRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DecPublishRecord row);

    int insertSelective(DecPublishRecord row);

    List<DecPublishRecord> selectByExample(DecPublishRecordExample example);

    DecPublishRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DecPublishRecord row, @Param("example") DecPublishRecordExample example);

    int updateByExample(@Param("row") DecPublishRecord row, @Param("example") DecPublishRecordExample example);

    int updateByPrimaryKeySelective(DecPublishRecord row);

    int updateByPrimaryKey(DecPublishRecord row);
}