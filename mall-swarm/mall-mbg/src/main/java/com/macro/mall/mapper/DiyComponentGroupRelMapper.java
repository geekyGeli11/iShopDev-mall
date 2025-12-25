package com.macro.mall.mapper;

import com.macro.mall.model.DiyComponentGroupRel;
import com.macro.mall.model.DiyComponentGroupRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyComponentGroupRelMapper {
    long countByExample(DiyComponentGroupRelExample example);

    int deleteByExample(DiyComponentGroupRelExample example);

    int deleteByPrimaryKey(@Param("groupId") Long groupId, @Param("componentId") Long componentId);

    int insert(DiyComponentGroupRel row);

    int insertSelective(DiyComponentGroupRel row);

    List<DiyComponentGroupRel> selectByExample(DiyComponentGroupRelExample example);

    int updateByExampleSelective(@Param("row") DiyComponentGroupRel row, @Param("example") DiyComponentGroupRelExample example);

    int updateByExample(@Param("row") DiyComponentGroupRel row, @Param("example") DiyComponentGroupRelExample example);
}