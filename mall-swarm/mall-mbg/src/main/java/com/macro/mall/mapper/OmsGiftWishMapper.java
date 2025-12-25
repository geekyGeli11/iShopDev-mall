package com.macro.mall.mapper;

import com.macro.mall.model.OmsGiftWish;
import com.macro.mall.model.OmsGiftWishExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsGiftWishMapper {
    long countByExample(OmsGiftWishExample example);

    int deleteByExample(OmsGiftWishExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsGiftWish row);

    int insertSelective(OmsGiftWish row);

    List<OmsGiftWish> selectByExample(OmsGiftWishExample example);

    OmsGiftWish selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsGiftWish row, @Param("example") OmsGiftWishExample example);

    int updateByExample(@Param("row") OmsGiftWish row, @Param("example") OmsGiftWishExample example);

    int updateByPrimaryKeySelective(OmsGiftWish row);

    int updateByPrimaryKey(OmsGiftWish row);
}