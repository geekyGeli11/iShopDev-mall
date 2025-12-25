package com.macro.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.dto.*;
import com.macro.mall.service.WechatMiniProgramService;
import com.macro.mall.service.WechatVisitDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 微信小程序访问数据Service实现
 * Created by macro on 2025/11/28.
 */
@Service
public class WechatVisitDataServiceImpl implements WechatVisitDataService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatVisitDataServiceImpl.class);
    
    @Autowired
    private WechatMiniProgramService wechatMiniProgramService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // 小程序访问数据独立存储，避免被系统统计数据覆盖
    private static final String REDIS_KEY_PREFIX = "wechat:visit_data:";
    private static final String REDIS_USER_PORTRAIT_KEY = "wechat:user_portrait:";
    
    /**
     * 缓存过期时间：24小时（秒）
     * 小程序访问数据不需要频繁访问，使用较长的缓存时间
     */
    private static final long CACHE_EXPIRE_TIME = 24 * 60 * 60;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public WechatVisitDataVO getWechatVisitData(LocalDate startDate, LocalDate endDate) {
        // 计算日期范围
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 构建缓存key - 包含日期范围和API类型，确保不同的查询方式使用不同的缓存
        String cacheKey = buildCacheKey(startDate, endDate, daysBetween);
        
        // 尝试从缓存获取
        Object cachedData = redisService.get(cacheKey);
        if (cachedData != null) {
            LOGGER.info("从缓存获取小程序访问数据: {}", cacheKey);
            try {
                return objectMapper.convertValue(cachedData, WechatVisitDataVO.class);
            } catch (Exception e) {
                LOGGER.warn("缓存数据转换失败，将重新获取", e);
            }
        }
        
        WechatVisitDataVO result = new WechatVisitDataVO();
        
        try {
            // 获取access_token
            String accessToken = wechatMiniProgramService.getAccessToken();
            if (accessToken == null) {
                LOGGER.warn("无法获取微信access_token，返回空数据");
                return result;
            }
            
            // 获取各类数据 - 统一使用月API
            result.setVisitTrend(getMonthlyVisitTrend(accessToken));
            
            // 根据日期范围选择合适的留存API
            if (daysBetween > 7) {
                // 日期范围大于一周，使用周留存API获取多个自然周的数据
                LOGGER.info("日期范围 {} 天 > 7 天，使用周留存API", daysBetween);
                result.setVisitRetain(getWeeklyVisitRetain(accessToken, startDate, endDate));
            } else {
                // 日期范围小于等于一周，使用月留存API
                LOGGER.info("日期范围 {} 天 <= 7 天，使用月留存API", daysBetween);
                result.setVisitRetain(getMonthlyVisitRetain(accessToken));
            }
            
            result.setUserPortrait(getUserPortrait(accessToken));
            result.setPageVisit(new ArrayList<>()); // 月API不提供页面访问数据
            
            // 从月趋势数据计算访问概况
            result.setVisitSummary(calculateVisitSummary(result.getVisitTrend()));
            
            // 缓存结果
            redisService.set(cacheKey, result, CACHE_EXPIRE_TIME);
            LOGGER.info("小程序访问数据已缓存: {}", cacheKey);
            
        } catch (Exception e) {
            LOGGER.error("获取小程序访问数据失败", e);
        }
        
        return result;
    }
    
    @Override
    public void refreshWechatVisitDataCache() {
        // 清除所有小程序访问数据缓存
        Set<String> keys = redisService.keys(REDIS_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                redisService.del(key);
            }
            LOGGER.info("已清除小程序访问数据缓存");
        }
    }
    
    /**
     * 获取月访问趋势数据
     */
    private List<VisitTrendVO> getMonthlyVisitTrend(String accessToken) {
        List<VisitTrendVO> trendList = new ArrayList<>();
        
        try {
            // 使用月趋势API
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappidmonthlyvisittrend?access_token=" + accessToken;
            
            // 月趋势API要求查询整个自然月，且只能查询过去的数据（不能查询当前月）
            LocalDate today = LocalDate.now();
            LocalDate lastMonth = today.minusMonths(1);
            LocalDate monthStart = lastMonth.withDayOfMonth(1);
            LocalDate monthEnd = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            
            String beginDateStr = monthStart.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateStr = monthEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            LOGGER.info("获取月访问趋势数据 - 开始日期: {}, 结束日期: {}", beginDateStr, endDateStr);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", beginDateStr);
            requestBody.put("end_date", endDateStr);
            
            try {
                // 手动序列化JSON并设置正确的请求头
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                LOGGER.debug("微信API请求体: {}", jsonBody);
                LOGGER.debug("微信API请求URL: {}", url);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                LOGGER.debug("微信API响应: {}", response);
                
                if (response != null) {
                    // 检查是否有错误码
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        String rid = (String) response.get("rid");
                        LOGGER.warn("微信API返回错误 - 错误码: {}, 错误信息: {}, 请求ID: {}", errcode, errmsg, rid);
                        
                        // 如果是token相关错误，刷新token
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else if (response.containsKey("list")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("list");
                        
                        if (dataList != null && !dataList.isEmpty()) {
                            for (Map<String, Object> data : dataList) {
                                VisitTrendVO vo = new VisitTrendVO();
                                // 月API返回ref_date字段，格式为yyyyMM
                                String refDate = (String) data.get("ref_date");
                                vo.setDate(refDate);
                                vo.setVisitUv(((Number) data.get("visit_uv")).intValue());
                                vo.setVisitPv(((Number) data.get("visit_pv")).intValue());
                                vo.setNewUser(((Number) data.get("visit_uv_new")).intValue());
                                LOGGER.info("月访问趋势数据 - 日期: {}, UV: {}, PV: {}, 新用户: {}", 
                                    refDate, vo.getVisitUv(), vo.getVisitPv(), vo.getNewUser());
                                trendList.add(vo);
                            }
                        } else {
                            LOGGER.info("微信API返回空数据列表，可能是小程序未开通数据分析功能或没有足够的数据");
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                
                // 如果是412错误，尝试强制刷新token
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信API调用失败，可能是小程序未开通数据分析功能或配置不正确: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取访问趋势数据失败", e);
        }
        
        return trendList;
    }
    
    /**
     * 获取月访问留存数据
     */
    private List<VisitRetainVO> getMonthlyVisitRetain(String accessToken) {
        List<VisitRetainVO> retainList = new ArrayList<>();
        
        try {
            // 根据微信官方文档，使用月留存API
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappidmonthlyretaininfo?access_token=" + accessToken;
            
            // 月留存API要求查询整个自然月，且只能查询过去的数据（不能查询当前月）
            LocalDate today = LocalDate.now();
            LocalDate lastMonth = today.minusMonths(1);
            LocalDate monthStart = lastMonth.withDayOfMonth(1);
            LocalDate monthEnd = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            
            String beginDateStr = monthStart.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateStr = monthEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            LOGGER.info("获取访问留存数据 - 开始日期: {}, 结束日期: {}", beginDateStr, endDateStr);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", beginDateStr);
            requestBody.put("end_date", endDateStr);
            
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                LOGGER.debug("微信API请求体: {}", jsonBody);
                LOGGER.debug("微信API请求URL: {}", url);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                LOGGER.debug("微信API响应: {}", response);
                
                if (response != null) {
                    // 检查是否有错误码
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        String rid = (String) response.get("rid");
                        LOGGER.warn("微信API返回错误 - 错误码: {}, 错误信息: {}, 请求ID: {}", errcode, errmsg, rid);
                        
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else if (response.containsKey("visit_uv_new")) {
                        // 月留存API返回格式：visit_uv_new 和 visit_uv 数组
                        List<Map<String, Object>> visitUvNewList = (List<Map<String, Object>>) response.get("visit_uv_new");
                        List<Map<String, Object>> visitUvList = (List<Map<String, Object>>) response.get("visit_uv");
                        
                        if (visitUvNewList != null && !visitUvNewList.isEmpty()) {
                            for (int i = 0; i < visitUvNewList.size(); i++) {
                                Map<String, Object> newUserData = visitUvNewList.get(i);
                                Map<String, Object> retainData = visitUvList != null && i < visitUvList.size() ? visitUvList.get(i) : null;
                                
                                VisitRetainVO vo = new VisitRetainVO();
                                // key表示第几个月（0表示当月，1表示上月等）
                                int key = ((Number) newUserData.get("key")).intValue();
                                vo.setPeriod("第" + (key + 1) + "个月");
                                
                                int newUser = ((Number) newUserData.get("value")).intValue();
                                int retainUser = retainData != null ? ((Number) retainData.get("value")).intValue() : 0;
                                
                                vo.setNewUser(newUser);
                                vo.setRetainUser(retainUser);
                                
                                // 计算留存率
                                if (newUser > 0) {
                                    vo.setRetainRate((double) retainUser / newUser * 100);
                                } else {
                                    vo.setRetainRate(0.0);
                                }
                                
                                LOGGER.info("月访问留存数据 - 周期: {}, 新用户: {}, 留存用户: {}, 留存率: {}", 
                                    vo.getPeriod(), newUser, retainUser, vo.getRetainRate());
                                retainList.add(vo);
                            }
                        } else {
                            LOGGER.info("微信API返回空数据列表");
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信API调用失败，可能是小程序未开通数据分析功能或配置不正确: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取访问留存数据失败", e);
        }
        
        return retainList;
    }
    
    /**
     * 获取周访问留存数据 - 支持多个自然周
     * 根据结果日期前一个星期开始统计数据
     */
    private List<VisitRetainVO> getWeeklyVisitRetain(String accessToken, LocalDate startDate, LocalDate endDate) {
        List<VisitRetainVO> retainList = new ArrayList<>();
        
        try {
            // 使用周留存API
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappidweeklyretaininfo?access_token=" + accessToken;
            
            // 周留存API要求查询整个自然周，且只能查询过去的数据
            // 计算查询范围：从startDate前一周开始到endDate
            LocalDate queryStartDate = startDate.minusWeeks(1);
            LocalDate queryEndDate = endDate;
            
            String beginDateStr = queryStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateStr = queryEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            LOGGER.info("获取周访问留存数据 - 开始日期: {}, 结束日期: {}", beginDateStr, endDateStr);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", beginDateStr);
            requestBody.put("end_date", endDateStr);
            
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                LOGGER.debug("微信周留存API请求体: {}", jsonBody);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                LOGGER.debug("微信周留存API响应: {}", response);
                
                if (response != null) {
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        String rid = (String) response.get("rid");
                        LOGGER.warn("微信周留存API返回错误 - 错误码: {}, 错误信息: {}, 请求ID: {}", errcode, errmsg, rid);
                        
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else if (response.containsKey("visit_uv_new")) {
                        // 周留存API返回格式：visit_uv_new 和 visit_uv 数组
                        List<Map<String, Object>> visitUvNewList = (List<Map<String, Object>>) response.get("visit_uv_new");
                        List<Map<String, Object>> visitUvList = (List<Map<String, Object>>) response.get("visit_uv");
                        
                        if (visitUvNewList != null && !visitUvNewList.isEmpty()) {
                            for (int i = 0; i < visitUvNewList.size(); i++) {
                                Map<String, Object> newUserData = visitUvNewList.get(i);
                                Map<String, Object> retainData = visitUvList != null && i < visitUvList.size() ? visitUvList.get(i) : null;
                                
                                VisitRetainVO vo = new VisitRetainVO();
                                
                                // 获取key值，表示第几周
                                int key = ((Number) newUserData.get("key")).intValue();
                                
                                // 计算周的日期范围
                                LocalDate weekStartDate = queryStartDate.plusWeeks(key);
                                LocalDate weekEndDate = weekStartDate.plusDays(6);
                                
                                // 格式化为 "xx月xx周" 的形式
                                int month = weekStartDate.getMonthValue();
                                int weekOfMonth = getWeekOfMonth(weekStartDate);
                                vo.setPeriod(month + "月第" + weekOfMonth + "周");
                                
                                int newUser = ((Number) newUserData.get("value")).intValue();
                                int retainUser = retainData != null ? ((Number) retainData.get("value")).intValue() : 0;
                                
                                vo.setNewUser(newUser);
                                vo.setRetainUser(retainUser);
                                
                                // 计算留存率
                                if (newUser > 0) {
                                    vo.setRetainRate((double) retainUser / newUser * 100);
                                } else {
                                    vo.setRetainRate(0.0);
                                }
                                
                                LOGGER.info("周访问留存数据 - 周期: {}, 日期范围: {} 到 {}, 新用户: {}, 留存用户: {}, 留存率: {}", 
                                    vo.getPeriod(), weekStartDate, weekEndDate, newUser, retainUser, vo.getRetainRate());
                                retainList.add(vo);
                            }
                        } else {
                            LOGGER.info("微信周留存API返回空数据列表");
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信周留存API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信周留存API调用失败: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取周访问留存数据失败", e);
        }
        
        return retainList;
    }
    
    /**
     * 获取日期在该月的第几周
     */
    private int getWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int dayOfMonth = date.getDayOfMonth();
        int dayOfWeekOfFirstDay = firstDayOfMonth.getDayOfWeek().getValue();
        
        // 计算第几周：(当前日期 + 第一天的星期数 - 1) / 7 + 1
        return (dayOfMonth + dayOfWeekOfFirstDay - 2) / 7 + 1;
    }
    
    /**
     * 获取用户画像数据 - 使用独立的Redis存储
     */
    private UserPortraitVO getUserPortrait(String accessToken) {
        UserPortraitVO portrait = new UserPortraitVO();
        
        // 用户画像数据使用独立的Redis key存储
        String portraitCacheKey = REDIS_USER_PORTRAIT_KEY + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 尝试从缓存获取
        Object cachedPortrait = redisService.get(portraitCacheKey);
        if (cachedPortrait != null) {
            LOGGER.info("从缓存获取用户画像数据: {}", portraitCacheKey);
            try {
                return objectMapper.convertValue(cachedPortrait, UserPortraitVO.class);
            } catch (Exception e) {
                LOGGER.warn("缓存用户画像数据转换失败，将重新获取", e);
            }
        }
        
        try {
            // 调用用户画像API获取所有数据
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappiduserportrait?access_token=" + accessToken;
            
            // 用户画像API支持：昨天、最近7天、最近30天
            // 使用最近7天的数据
            LocalDate endDate = LocalDate.now().minusDays(1); // 昨天
            LocalDate startDate = endDate.minusDays(6); // 最近7天
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            requestBody.put("end_date", endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                if (response != null) {
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        LOGGER.warn("微信API返回错误 - 错误码: {}, 错误信息: {}", errcode, errmsg);
                        
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else {
                        // 解析性别分布
                        if (response.containsKey("visit_uv")) {
                            Map<String, Object> visitUv = (Map<String, Object>) response.get("visit_uv");
                            if (visitUv != null && visitUv.containsKey("genders")) {
                                List<Map<String, Object>> genderList = (List<Map<String, Object>>) visitUv.get("genders");
                                portrait.setGenderDistribution(parseGenderDistribution(genderList));
                            }
                            if (visitUv != null && visitUv.containsKey("ages")) {
                                List<Map<String, Object>> ageList = (List<Map<String, Object>>) visitUv.get("ages");
                                portrait.setAgeDistribution(parseAgeDistribution(ageList));
                            }
                            if (visitUv != null && visitUv.containsKey("province")) {
                                List<Map<String, Object>> provinceList = (List<Map<String, Object>>) visitUv.get("province");
                                portrait.setRegionDistribution(parseRegionDistribution(provinceList));
                            }
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信API调用失败，可能是小程序未开通数据分析功能或配置不正确: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取用户画像数据失败", e);
        }
        
        // 缓存用户画像数据到独立的Redis key
        if (portrait != null && (portrait.getGenderDistribution() != null || portrait.getAgeDistribution() != null)) {
            redisService.set(portraitCacheKey, portrait, CACHE_EXPIRE_TIME);
            LOGGER.info("用户画像数据已缓存: {}", portraitCacheKey);
        }
        
        return portrait;
    }
    
    /**
     * 解析性别分布
     */
    private List<UserPortraitVO.GenderDistributionVO> parseGenderDistribution(List<Map<String, Object>> genderList) {
        List<UserPortraitVO.GenderDistributionVO> list = new ArrayList<>();
        
        if (genderList == null || genderList.isEmpty()) {
            return list;
        }
        
        int total = genderList.stream().mapToInt(d -> ((Number) d.get("value")).intValue()).sum();
        
        for (Map<String, Object> data : genderList) {
            UserPortraitVO.GenderDistributionVO vo = new UserPortraitVO.GenderDistributionVO();
            int id = ((Number) data.get("id")).intValue();
            vo.setGender(id == 1 ? "male" : id == 2 ? "female" : "unknown");
            int count = ((Number) data.get("value")).intValue();
            vo.setCount(count);
            vo.setPercentage(total > 0 ? (double) count / total * 100 : 0.0);
            list.add(vo);
        }
        
        return list;
    }
    
    /**
     * 解析年龄分布
     */
    private List<UserPortraitVO.AgeDistributionVO> parseAgeDistribution(List<Map<String, Object>> ageList) {
        List<UserPortraitVO.AgeDistributionVO> list = new ArrayList<>();
        
        if (ageList == null || ageList.isEmpty()) {
            return list;
        }
        
        int total = ageList.stream().mapToInt(d -> ((Number) d.get("value")).intValue()).sum();
        
        for (Map<String, Object> data : ageList) {
            UserPortraitVO.AgeDistributionVO vo = new UserPortraitVO.AgeDistributionVO();
            vo.setAgeRange((String) data.get("name"));
            int count = ((Number) data.get("value")).intValue();
            vo.setCount(count);
            vo.setPercentage(total > 0 ? (double) count / total * 100 : 0.0);
            list.add(vo);
        }
        
        return list;
    }
    
    /**
     * 解析地域分布
     */
    private List<UserPortraitVO.RegionDistributionVO> parseRegionDistribution(List<Map<String, Object>> regionList) {
        List<UserPortraitVO.RegionDistributionVO> list = new ArrayList<>();
        
        if (regionList == null || regionList.isEmpty()) {
            return list;
        }
        
        int total = regionList.stream().mapToInt(d -> ((Number) d.get("value")).intValue()).sum();
        
        // 只取前10个地区
        for (int i = 0; i < Math.min(10, regionList.size()); i++) {
            Map<String, Object> data = regionList.get(i);
            UserPortraitVO.RegionDistributionVO vo = new UserPortraitVO.RegionDistributionVO();
            vo.setRegion((String) data.get("name"));
            int count = ((Number) data.get("value")).intValue();
            vo.setCount(count);
            vo.setPercentage(total > 0 ? (double) count / total * 100 : 0.0);
            list.add(vo);
        }
        
        return list;
    }
    

    
    /**
     * 获取访问页面数据
     */
    private List<PageVisitVO> getPageVisit(String accessToken) {
        List<PageVisitVO> pageList = new ArrayList<>();
        
        try {
            // 使用周趋势API获取页面访问数据
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappidweeklyvisittrend?access_token=" + accessToken;
            
            // 周趋势API要求查询整个自然周（周一到周日）
            LocalDate today = LocalDate.now();
            // 计算本周的周一
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            // 计算本周的周日
            LocalDate weekEnd = weekStart.plusDays(6);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", weekStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            requestBody.put("end_date", weekEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                if (response != null) {
                    // 检查是否有错误码
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        LOGGER.warn("微信API返回错误 - 错误码: {}, 错误信息: {}", errcode, errmsg);
                        
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else if (response.containsKey("data")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                        
                        if (dataList != null && !dataList.isEmpty()) {
                            // 只取前10个页面
                            for (int i = 0; i < Math.min(10, dataList.size()); i++) {
                                Map<String, Object> data = dataList.get(i);
                                PageVisitVO vo = new PageVisitVO();
                                vo.setPagePath((String) data.get("page_path"));
                                vo.setVisitUv(((Number) data.get("visit_uv")).intValue());
                                vo.setVisitPv(((Number) data.get("visit_pv")).intValue());
                                vo.setAvgStayTime(((Number) data.get("stay_time")).intValue());
                                vo.setBounceRate(((Number) data.get("bounce_rate")).doubleValue());
                                pageList.add(vo);
                            }
                        } else {
                            LOGGER.info("微信API返回空数据列表");
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信API调用失败，可能是小程序未开通数据分析功能或配置不正确: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取访问页面数据失败", e);
        }
        
        return pageList;
    }
    
    /**
     * 获取访问概况数据
     */
    private VisitSummaryVO getVisitSummary(String accessToken) {
        VisitSummaryVO summary = new VisitSummaryVO();
        
        try {
            // 使用日概况API获取访问概况数据
            String url = "https://api.weixin.qq.com/datacube/getweanalysisappiddailysummarytrend?access_token=" + accessToken;
            
            // 日概况API要求begin_date和end_date相同，只能查询单一日期
            // 只能查询昨天或更早的日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("begin_date", dateStr);
            requestBody.put("end_date", dateStr);
            
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentLength(bodyBytes.length);
                headers.set("Transfer-Encoding", "identity");
                
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                RestTemplate wechatRestTemplate = createWechatRestTemplate();
                ResponseEntity<Map> responseEntity = wechatRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
                Map<String, Object> response = responseEntity.getBody();
                
                if (response != null) {
                    // 检查是否有错误码
                    if (response.containsKey("errcode")) {
                        int errcode = ((Number) response.get("errcode")).intValue();
                        String errmsg = (String) response.get("errmsg");
                        LOGGER.warn("微信API返回错误 - 错误码: {}, 错误信息: {}", errcode, errmsg);
                        
                        if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                            LOGGER.info("access_token相关错误，强制刷新token");
                            wechatMiniProgramService.refreshAccessToken();
                        }
                    } else if (response.containsKey("data")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                        
                        if (dataList != null && !dataList.isEmpty()) {
                            int totalUv = 0;
                            int totalPv = 0;
                            int totalNewUser = 0;
                            
                            for (Map<String, Object> data : dataList) {
                                totalUv += ((Number) data.get("visit_uv")).intValue();
                                totalPv += ((Number) data.get("visit_pv")).intValue();
                                totalNewUser += ((Number) data.get("new_user")).intValue();
                            }
                            
                            summary.setTotalVisitUv(totalUv);
                            summary.setTotalVisitPv(totalPv);
                            summary.setNewUserCount(totalNewUser);
                            
                            // 计算平均访问深度和时长
                            if (totalUv > 0) {
                                summary.setAvgVisitDepth((double) totalPv / totalUv);
                            }
                        } else {
                            LOGGER.info("微信API返回空数据列表");
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                LOGGER.warn("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 412) {
                    LOGGER.info("收到412错误，尝试强制刷新access_token");
                    wechatMiniProgramService.refreshAccessToken();
                }
            } catch (Exception e) {
                LOGGER.warn("微信API调用失败，可能是小程序未开通数据分析功能或配置不正确: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取访问概况数据失败", e);
        }
        
        return summary;
    }
    
    /**
     * 从月趋势数据计算访问概况
     * 由于月API不提供日概况数据，我们从月趋势数据中聚合计算
     */
    private VisitSummaryVO calculateVisitSummary(List<VisitTrendVO> visitTrendList) {
        VisitSummaryVO summary = new VisitSummaryVO();
        
        if (visitTrendList == null || visitTrendList.isEmpty()) {
            LOGGER.info("访问趋势数据为空，无法计算访问概况");
            return summary;
        }
        
        int totalUv = 0;
        int totalPv = 0;
        int totalNewUser = 0;
        
        for (VisitTrendVO trend : visitTrendList) {
            totalUv += trend.getVisitUv();
            totalPv += trend.getVisitPv();
            totalNewUser += trend.getNewUser();
        }
        
        summary.setTotalVisitUv(totalUv);
        summary.setTotalVisitPv(totalPv);
        summary.setNewUserCount(totalNewUser);
        
        // 计算平均访问深度（PV/UV）
        if (totalUv > 0) {
            summary.setAvgVisitDepth((double) totalPv / totalUv);
        }
        
        LOGGER.info("访问概况统计 - 总UV: {}, 总PV: {}, 新用户: {}, 平均访问深度: {}", 
            totalUv, totalPv, totalNewUser, summary.getAvgVisitDepth());
        
        return summary;
    }
    
    /**
     * 构建缓存key
     * 包含日期范围和API类型，确保不同的查询方式使用不同的缓存
     */
    private String buildCacheKey(LocalDate startDate, LocalDate endDate, long daysBetween) {
        // 根据日期范围确定API类型
        String apiType = daysBetween > 7 ? "weekly" : "monthly";
        return REDIS_KEY_PREFIX + startDate + ":" + endDate + ":" + apiType;
    }
    
    /**
     * 创建专门用于微信API的RestTemplate实例
     * 针对微信API的特殊要求进行优化
     */
    private RestTemplate createWechatRestTemplate() {
        // 配置连接超时和读取超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000); // 15秒连接超时
        factory.setReadTimeout(30000);    // 30秒读取超时
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // 使用默认的消息转换器，确保正确处理JSON
        // 通过手动设置Content-Length避免chunked编码问题
        
        return restTemplate;
    }
}
