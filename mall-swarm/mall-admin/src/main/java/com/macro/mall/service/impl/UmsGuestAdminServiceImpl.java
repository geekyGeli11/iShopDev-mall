package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsGuestAdminDao;
import com.macro.mall.dto.UmsGuestListVO;
import com.macro.mall.dto.UmsGuestQueryParam;
import com.macro.mall.mapper.UmsGuestMapper;
import com.macro.mall.model.UmsGuest;
import com.macro.mall.model.UmsGuestExample;
import com.macro.mall.service.UmsGuestAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 游客管理Service实现类
 */
@Service
public class UmsGuestAdminServiceImpl implements UmsGuestAdminService {

    @Autowired
    private UmsGuestAdminDao guestAdminDao;

    @Autowired
    private UmsGuestMapper guestMapper;

    @Override
    public List<UmsGuestListVO> list(UmsGuestQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return guestAdminDao.getGuestList(queryParam);
    }

    @Override
    public int updateStatus(String guestId, Integer status) {
        UmsGuestExample example = new UmsGuestExample();
        example.createCriteria().andGuestIdEqualTo(guestId);
        
        List<UmsGuest> guests = guestMapper.selectByExample(example);
        if (guests.isEmpty()) {
            return 0;
        }
        
        UmsGuest guest = new UmsGuest();
        guest.setId(guests.get(0).getId());
        guest.setStatus(status);
        guest.setLastActiveTime(new Date());
        
        return guestMapper.updateByPrimaryKeySelective(guest);
    }

    @Override
    public int deleteGuest(String guestId) {
        UmsGuestExample example = new UmsGuestExample();
        example.createCriteria().andGuestIdEqualTo(guestId);
        return guestMapper.deleteByExample(example);
    }

    @Override
    public int cleanExpiredGuests(Integer expiredDays) {
        return guestAdminDao.cleanExpiredGuests(expiredDays);
    }
}
