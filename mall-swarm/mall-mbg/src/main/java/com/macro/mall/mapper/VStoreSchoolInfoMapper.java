package com.macro.mall.mapper;

import com.macro.mall.model.VStoreSchoolInfo;
import com.macro.mall.model.VStoreSchoolInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VStoreSchoolInfoMapper {
    long countByExample(VStoreSchoolInfoExample example);

    int deleteByExample(VStoreSchoolInfoExample example);

    int insert(VStoreSchoolInfo row);

    int insertSelective(VStoreSchoolInfo row);

    List<VStoreSchoolInfo> selectByExample(VStoreSchoolInfoExample example);

    int updateByExampleSelective(@Param("row") VStoreSchoolInfo row, @Param("example") VStoreSchoolInfoExample example);

    int updateByExample(@Param("row") VStoreSchoolInfo row, @Param("example") VStoreSchoolInfoExample example);
}