package com.macro.mall.mapper;

import com.macro.mall.model.DiyAbBucket;
import com.macro.mall.model.DiyAbBucketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyAbBucketMapper {
    long countByExample(DiyAbBucketExample example);

    int deleteByExample(DiyAbBucketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyAbBucket row);

    int insertSelective(DiyAbBucket row);

    List<DiyAbBucket> selectByExample(DiyAbBucketExample example);

    DiyAbBucket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyAbBucket row, @Param("example") DiyAbBucketExample example);

    int updateByExample(@Param("row") DiyAbBucket row, @Param("example") DiyAbBucketExample example);

    int updateByPrimaryKeySelective(DiyAbBucket row);

    int updateByPrimaryKey(DiyAbBucket row);
}