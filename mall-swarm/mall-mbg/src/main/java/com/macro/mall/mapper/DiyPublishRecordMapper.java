package com.macro.mall.mapper;

import com.macro.mall.model.DiyPublishRecord;
import com.macro.mall.model.DiyPublishRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyPublishRecordMapper {
    long countByExample(DiyPublishRecordExample example);

    int deleteByExample(DiyPublishRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyPublishRecord row);

    int insertSelective(DiyPublishRecord row);

    List<DiyPublishRecord> selectByExample(DiyPublishRecordExample example);

    DiyPublishRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyPublishRecord row, @Param("example") DiyPublishRecordExample example);

    int updateByExample(@Param("row") DiyPublishRecord row, @Param("example") DiyPublishRecordExample example);

    int updateByPrimaryKeySelective(DiyPublishRecord row);

    int updateByPrimaryKey(DiyPublishRecord row);
}