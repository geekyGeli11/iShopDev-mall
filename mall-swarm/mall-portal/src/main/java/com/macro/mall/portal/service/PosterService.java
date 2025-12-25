package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.PosterGenerateParam;

import java.util.Map;

/**
 * 推广海报服务接口
 */
public interface PosterService {
    
    /**
     * 生成推广海报
     */
    Map<String, Object> generatePoster(Long userId, PosterGenerateParam param);
    
    /**
     * 获取海报模板列表
     */
    Map<String, Object> getPosterTemplates();
    
    /**
     * 获取我的海报列表
     */
    Map<String, Object> getMyPosters(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除海报
     */
    Boolean deletePoster(Long posterId, Long userId);
    
    /**
     * 获取海报详情
     */
    Map<String, Object> getPosterDetail(Long posterId, Long userId);
    
    /**
     * 分享海报
     */
    Map<String, Object> sharePoster(Long posterId, Long userId);
} 