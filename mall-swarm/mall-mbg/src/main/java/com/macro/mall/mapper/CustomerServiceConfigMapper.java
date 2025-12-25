package com.macro.mall.mapper;

import com.macro.mall.model.CustomerServiceConfig;
import com.macro.mall.model.CustomerServiceConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CustomerServiceConfigMapper {
    long countByExample(CustomerServiceConfigExample example);

    int deleteByExample(CustomerServiceConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CustomerServiceConfig row);

    int insertSelective(CustomerServiceConfig row);

    List<CustomerServiceConfig> selectByExampleWithBLOBs(CustomerServiceConfigExample example);

    List<CustomerServiceConfig> selectByExample(CustomerServiceConfigExample example);

    CustomerServiceConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CustomerServiceConfig row, @Param("example") CustomerServiceConfigExample example);

    int updateByExampleWithBLOBs(@Param("row") CustomerServiceConfig row, @Param("example") CustomerServiceConfigExample example);

    int updateByExample(@Param("row") CustomerServiceConfig row, @Param("example") CustomerServiceConfigExample example);

    int updateByPrimaryKeySelective(CustomerServiceConfig row);

    int updateByPrimaryKeyWithBLOBs(CustomerServiceConfig row);

    int updateByPrimaryKey(CustomerServiceConfig row);
}