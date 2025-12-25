package com.macro.mall.mapper;

import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.OmsStoreAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsStoreAddressMapper {
    long countByExample(OmsStoreAddressExample example);

    int deleteByExample(OmsStoreAddressExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsStoreAddress row);

    int insertSelective(OmsStoreAddress row);

    List<OmsStoreAddress> selectByExampleWithBLOBs(OmsStoreAddressExample example);

    List<OmsStoreAddress> selectByExample(OmsStoreAddressExample example);

    OmsStoreAddress selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsStoreAddress row, @Param("example") OmsStoreAddressExample example);

    int updateByExampleWithBLOBs(@Param("row") OmsStoreAddress row, @Param("example") OmsStoreAddressExample example);

    int updateByExample(@Param("row") OmsStoreAddress row, @Param("example") OmsStoreAddressExample example);

    int updateByPrimaryKeySelective(OmsStoreAddress row);

    int updateByPrimaryKeyWithBLOBs(OmsStoreAddress row);

    int updateByPrimaryKey(OmsStoreAddress row);
}