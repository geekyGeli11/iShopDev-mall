package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.mapper.UmsGuestMapper;
import com.macro.mall.model.UmsGuest;
import com.macro.mall.model.UmsGuestExample;
import com.macro.mall.selfcheck.service.SelfcheckGuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 自助结算游客服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckGuestServiceImpl implements SelfcheckGuestService {

    private static final String REDIS_KEY_GUEST = "selfcheck:session:guest:";
    private static final String REDIS_KEY_GUEST_LOGIN_MAP = "selfcheck:guest:login:";
    private static final String REDIS_KEY_LOGIN_GUEST_MAP = "selfcheck:login:guest:";
    private static final long GUEST_SESSION_TIMEOUT = 1800; // 30分钟

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UmsGuestMapper guestMapper;

    @Override
    @Transactional
    public UmsGuest createGuestSession(String deviceId, String deviceType, String loginIp) {
        String guestId = generateGuestId(deviceId);

        UmsGuest guest = new UmsGuest();
        guest.setGuestId(guestId);
        guest.setDeviceId(deviceId);
        guest.setDeviceType(deviceType);
        guest.setLastAccessIp(loginIp);
        guest.setCreateTime(new Date());
        guest.setLastActiveTime(new Date());
        guest.setStatus(1); // 1表示有效
        guest.setHasActiveOrder(false);

        try {
            // 1. 先在数据库中创建游客记录
            int result = guestMapper.insertSelective(guest);
            
            if (result > 0) {
                // 2. 在Redis中存储游客会话信息
                String guestKey = REDIS_KEY_GUEST + guestId;
                redisTemplate.opsForValue().set(guestKey, guest, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                log.info("创建游客会话成功，游客ID：{}，设备ID：{}，数据库ID：{}", guestId, deviceId, guest.getId());
            } else {
                log.error("数据库插入游客记录失败，游客ID：{}", guestId);
                throw new RuntimeException("创建游客会话失败");
            }
            
        } catch (Exception e) {
            log.error("创建游客会话失败，游客ID：{}，错误：{}", guestId, e.getMessage());
            throw new RuntimeException("创建游客会话失败：" + e.getMessage());
        }

        return guest;
    }

    @Override
    public UmsGuest getGuest(String guestId) {
        if (guestId == null) {
            return null;
        }

        // 优先从Redis获取（热数据）
        String guestKey = REDIS_KEY_GUEST + guestId;
        UmsGuest guest = (UmsGuest) redisTemplate.opsForValue().get(guestKey);
        
        if (guest != null) {
            return guest;
        }
        
        // Redis中没有，尝试从数据库恢复
        try {
            UmsGuestExample example = new UmsGuestExample();
            example.createCriteria().andGuestIdEqualTo(guestId);
            List<UmsGuest> guestList = guestMapper.selectByExample(example);
            
            if (!guestList.isEmpty()) {
                guest = guestList.get(0);
                if (guest.getStatus() == 1) {
                    // 恢复到Redis中
                    redisTemplate.opsForValue().set(guestKey, guest, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
                    log.info("从数据库恢复游客会话到Redis，游客ID：{}", guestId);
                    return guest;
                }
            }
        } catch (Exception e) {
            log.error("从数据库获取游客信息失败，游客ID：{}，错误：{}", guestId, e.getMessage());
        }
        
        return null;
    }

    @Override
    public void updateGuestActivity(String guestId) {
        if (guestId == null) {
            return;
        }

        String guestKey = REDIS_KEY_GUEST + guestId;
        UmsGuest guest = (UmsGuest) redisTemplate.opsForValue().get(guestKey);

        if (guest != null) {
            Date now = new Date();
            guest.setLastActiveTime(now);
            
            // 更新Redis
            redisTemplate.opsForValue().set(guestKey, guest, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
            
            // 异步更新数据库（降低延迟）
            try {
                UmsGuest updateGuest = new UmsGuest();
                updateGuest.setId(guest.getId());
                updateGuest.setLastActiveTime(now);
                guestMapper.updateByPrimaryKeySelective(updateGuest);
            } catch (Exception e) {
                log.error("更新游客活跃时间到数据库失败，游客ID：{}，错误：{}", guestId, e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void clearGuestSession(String guestId) {
        if (guestId == null) {
            return;
        }

        try {
            // 1. 清除Redis会话
            String guestKey = REDIS_KEY_GUEST + guestId;
            redisTemplate.delete(guestKey);
            
            // 2. 将数据库中的游客状态设为无效（软删除，保留订单关联）
            UmsGuestExample example = new UmsGuestExample();
            example.createCriteria().andGuestIdEqualTo(guestId);
            List<UmsGuest> guestList = guestMapper.selectByExample(example);
            
            if (!guestList.isEmpty()) {
                UmsGuest guest = guestList.get(0);
                guest.setStatus(0); // 设为无效状态
                guest.setLastActiveTime(new Date());
                guestMapper.updateByPrimaryKeySelective(guest);
            }
            
            // 3. 清除Token关联
            String guestToLoginKey = REDIS_KEY_GUEST_LOGIN_MAP + guestId;
            Long loginId = (Long) redisTemplate.opsForValue().get(guestToLoginKey);
            if (loginId != null) {
                String loginToGuestKey = REDIS_KEY_LOGIN_GUEST_MAP + loginId;
                redisTemplate.delete(guestToLoginKey);
                redisTemplate.delete(loginToGuestKey);
            }

            log.info("清除游客会话成功，游客ID：{}", guestId);
            
        } catch (Exception e) {
            log.error("清除游客会话失败，游客ID：{}，错误：{}", guestId, e.getMessage());
        }
    }

    @Override
    public boolean isGuestSessionValid(String guestId) {
        if (guestId == null) {
            return false;
        }

        UmsGuest guest = getGuest(guestId);
        if (guest == null) {
            return false;
        }

        // 检查会话状态
        if (guest.getStatus() != 1) {
            return false;
        }

        // 检查会话超时
        long now = System.currentTimeMillis();
        long lastActive = guest.getLastActiveTime().getTime();
        return (now - lastActive) < GUEST_SESSION_TIMEOUT * 1000;
    }

    @Override
    public String generateGuestId(String deviceId) {
        // 生成格式：guest_设备ID后8位_时间戳后6位_随机数4位
        String timestamp = String.valueOf(System.currentTimeMillis());
        String deviceSuffix = deviceId != null && deviceId.length() >= 8 ? 
                deviceId.substring(deviceId.length() - 8) : 
                String.format("%08d", Math.abs(deviceId.hashCode()) % 100000000);
        String timestampSuffix = timestamp.substring(timestamp.length() - 6);
        String randomSuffix = String.format("%04d", (int) (Math.random() * 10000));

        return "guest_" + deviceSuffix + "_" + timestampSuffix + "_" + randomSuffix;
    }

    @Override
    public void setGuestOrderStatus(String guestId, boolean hasActiveOrder) {
        if (guestId == null) {
            return;
        }

        String guestKey = REDIS_KEY_GUEST + guestId;
        UmsGuest guest = (UmsGuest) redisTemplate.opsForValue().get(guestKey);

        if (guest != null) {
            guest.setHasActiveOrder(hasActiveOrder);
            guest.setLastActiveTime(new Date());
            
            // 更新Redis
            redisTemplate.opsForValue().set(guestKey, guest, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
            
            // 同步更新数据库（订单状态比较重要）
            try {
                UmsGuest updateGuest = new UmsGuest();
                updateGuest.setId(guest.getId());
                updateGuest.setHasActiveOrder(hasActiveOrder);
                updateGuest.setLastActiveTime(new Date());
                guestMapper.updateByPrimaryKeySelective(updateGuest);
                
                log.info("更新游客订单状态，游客ID：{}，有活跃订单：{}", guestId, hasActiveOrder);
            } catch (Exception e) {
                log.error("更新游客订单状态到数据库失败，游客ID：{}，错误：{}", guestId, e.getMessage());
            }
        }
    }

    @Override
    public void linkGuestWithToken(String guestId, long guestLoginId) {
        try {
            // 双向映射：guestId -> loginId 和 loginId -> guestId
            String guestToLoginKey = REDIS_KEY_GUEST_LOGIN_MAP + guestId;
            String loginToGuestKey = REDIS_KEY_LOGIN_GUEST_MAP + guestLoginId;
            
            redisTemplate.opsForValue().set(guestToLoginKey, guestLoginId, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(loginToGuestKey, guestId, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
            
            log.debug("关联游客信息和SA-Token成功，游客ID：{}，loginId：{}", guestId, guestLoginId);
            
        } catch (Exception e) {
            log.error("关联游客信息和SA-Token失败，游客ID：{}，loginId：{}，错误：{}", guestId, guestLoginId, e.getMessage());
        }
    }

    @Override
    public String getGuestIdByLoginId(long guestLoginId) {
        try {
            String loginToGuestKey = REDIS_KEY_LOGIN_GUEST_MAP + guestLoginId;
            return (String) redisTemplate.opsForValue().get(loginToGuestKey);
        } catch (Exception e) {
            log.error("根据loginId获取游客ID失败，loginId：{}，错误：{}", guestLoginId, e.getMessage());
            return null;
        }
    }

    @Override
    public Long getLoginIdByGuestId(String guestId) {
        try {
            String guestToLoginKey = REDIS_KEY_GUEST_LOGIN_MAP + guestId;
            Object loginId = redisTemplate.opsForValue().get(guestToLoginKey);
            return loginId != null ? (Long) loginId : null;
        } catch (Exception e) {
            log.error("根据游客ID获取loginId失败，游客ID：{}，错误：{}", guestId, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean bindGuestSchool(String guestId, Long schoolId) {
        try {
            // 从数据库获取游客信息
            UmsGuestExample example = new UmsGuestExample();
            example.createCriteria().andGuestIdEqualTo(guestId);
            List<UmsGuest> guests = guestMapper.selectByExample(example);

            if (CollectionUtils.isEmpty(guests)) {
                log.warn("游客不存在，guestId：{}", guestId);
                return false;
            }

            UmsGuest guest = guests.get(0);
            UmsGuest record = new UmsGuest();
            record.setId(guest.getId());
            record.setSchoolId(schoolId);

            int result = guestMapper.updateByPrimaryKeySelective(record);

            if (result > 0) {
                // 更新缓存
                guest.setSchoolId(schoolId);
                String redisKey = REDIS_KEY_GUEST + guestId;
                redisTemplate.opsForValue().set(redisKey, guest, GUEST_SESSION_TIMEOUT, TimeUnit.SECONDS);
                log.info("游客{}绑定学校{}成功", guestId, schoolId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("游客{}绑定学校{}失败", guestId, schoolId, e);
            return false;
        }
    }

    @Override
    public Long getGuestSchoolId(String guestId) {
        UmsGuest guest = getGuest(guestId);
        return guest != null ? guest.getSchoolId() : null;
    }

    @Override
    public UmsGuest createGuestSessionWithSchoolBinding(String deviceId, String deviceType, String loginIp, Long schoolId) {
        // 先创建正常的游客会话
        UmsGuest guest = createGuestSession(deviceId, deviceType, loginIp);

        if (guest != null && schoolId != null) {
            // 检查游客是否已经绑定学校
            if (guest.getSchoolId() == null) {
                // 游客还没有绑定学校，进行绑定
                log.info("游客{}还未绑定学校，开始绑定学校ID：{}", guest.getGuestId(), schoolId);
                boolean bindSuccess = bindGuestSchool(guest.getGuestId(), schoolId);
                if (bindSuccess) {
                    // 更新guest对象中的学校ID
                    guest.setSchoolId(schoolId);
                    log.info("游客{}绑定学校{}成功", guest.getGuestId(), schoolId);
                } else {
                    log.warn("游客{}绑定学校{}失败", guest.getGuestId(), schoolId);
                }
            } else {
                log.info("游客{}已绑定学校{}，跳过绑定", guest.getGuestId(), guest.getSchoolId());
            }
        }

        return guest;
    }
}