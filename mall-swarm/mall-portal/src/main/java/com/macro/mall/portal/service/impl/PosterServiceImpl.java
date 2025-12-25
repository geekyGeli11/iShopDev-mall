package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dto.PosterGenerateParam;
import com.macro.mall.portal.service.DistributionService;
import com.macro.mall.portal.service.PosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 推广海报服务实现类
 */
@Service
public class PosterServiceImpl implements PosterService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PosterServiceImpl.class);
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsInviteParamLogMapper inviteParamLogMapper;
    
    @Autowired
    private DistributionService distributionService;
    
    @Value("${poster.base-url:https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/posters/}")
    private String posterBaseUrl;
    
    @Value("${poster.qrcode.base-url:https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/qrcodes/}")
    private String qrCodeBaseUrl;

    @Override
    @Transactional
    public Map<String, Object> generatePoster(Long userId, PosterGenerateParam param) {
        try {
            // 验证用户
            UmsMember member = memberMapper.selectByPrimaryKey(userId);
            if (member == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 获取分销码
            String distributionCode = distributionService.getMyDistributionCode(userId);
            if (distributionCode == null) {
                distributionCode = distributionService.generateDistributionCode(userId);
            }
            
            // 根据海报类型生成不同内容
            Map<String, Object> posterData = buildPosterData(userId, param, distributionCode);
            
            // 生成海报URL（这里简化处理，实际应该调用图片生成服务）
            String posterUrl = generatePosterUrl(posterData);
            
            // 生成二维码URL
            String qrCodeUrl = null;
            if (param.getIncludeQrCode()) {
                qrCodeUrl = generateQrCodeUrl(distributionCode);
            }
            
            // 保存海报记录
            Long posterId = savePosterRecord(userId, param, posterUrl, qrCodeUrl, distributionCode);
            
            Map<String, Object> result = new HashMap<>();
            result.put("posterId", posterId);
            result.put("posterUrl", posterUrl);
            result.put("qrCodeUrl", qrCodeUrl);
            result.put("distributionCode", distributionCode);
            result.put("createTime", new Date());
            result.put("shareText", generateShareText(param));
            
            return result;
        } catch (Exception e) {
            LOGGER.error("生成海报失败, userId: {}", userId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPosterTemplates() {
        Map<String, Object> result = new HashMap<>();
        
        // 预定义海报模板（实际项目中应该从数据库读取）
        List<Map<String, Object>> templates = new ArrayList<>();
        
        // 商品海报模板
        Map<String, Object> productTemplate = new HashMap<>();
        productTemplate.put("id", 1L);
        productTemplate.put("name", "经典商品海报");
        productTemplate.put("type", 1);
        productTemplate.put("typeName", "商品海报");
        productTemplate.put("description", "适合商品推广的经典模板");
        productTemplate.put("previewUrl", posterBaseUrl + "templates/product_template_1.jpg");
        productTemplate.put("isDefault", true);
        templates.add(productTemplate);
        
        // 店铺海报模板
        Map<String, Object> storeTemplate = new HashMap<>();
        storeTemplate.put("id", 2L);
        storeTemplate.put("name", "简约店铺海报");
        storeTemplate.put("type", 2);
        storeTemplate.put("typeName", "店铺海报");
        storeTemplate.put("description", "简约风格的店铺推广模板");
        storeTemplate.put("previewUrl", posterBaseUrl + "templates/store_template_1.jpg");
        storeTemplate.put("isDefault", true);
        templates.add(storeTemplate);
        
        // 活动海报模板
        Map<String, Object> activityTemplate = new HashMap<>();
        activityTemplate.put("id", 3L);
        activityTemplate.put("name", "促销活动海报");
        activityTemplate.put("type", 3);
        activityTemplate.put("typeName", "活动海报");
        activityTemplate.put("description", "适合促销活动的醒目模板");
        activityTemplate.put("previewUrl", posterBaseUrl + "templates/activity_template_1.jpg");
        activityTemplate.put("isDefault", true);
        templates.add(activityTemplate);
        
        result.put("templates", templates);
        result.put("total", templates.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getMyPosters(Long userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟分页查询海报记录（实际应该从数据库查询）
            List<Map<String, Object>> posters = new ArrayList<>();
            
            // 这里简化处理，实际应该查询 pms_poster_record 表
            // 示例数据
            for (int i = 1; i <= Math.min(pageSize, 5); i++) {
                Map<String, Object> poster = new HashMap<>();
                poster.put("id", (long) i);
                poster.put("title", "推广海报 " + i);
                poster.put("posterType", 1);
                poster.put("posterTypeName", "商品海报");
                poster.put("posterUrl", posterBaseUrl + "user_" + userId + "_poster_" + i + ".jpg");
                poster.put("qrCodeUrl", qrCodeBaseUrl + "user_" + userId + "_qr_" + i + ".jpg");
                poster.put("createTime", new Date());
                poster.put("shareCount", (int) (Math.random() * 100));
                poster.put("viewCount", (int) (Math.random() * 500));
                posters.add(poster);
            }
            
            result.put("list", posters);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("total", 5L);
            result.put("pages", 1);
            
            return result;
        } catch (Exception e) {
            LOGGER.error("获取我的海报列表失败, userId: {}", userId, e);
            throw new RuntimeException("获取海报列表失败");
        }
    }

    @Override
    @Transactional
    public Boolean deletePoster(Long posterId, Long userId) {
        try {
            // 实际应该删除数据库记录
            LOGGER.info("删除海报, posterId: {}, userId: {}", posterId, userId);
            return true;
        } catch (Exception e) {
            LOGGER.error("删除海报失败, posterId: {}, userId: {}", posterId, userId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getPosterDetail(Long posterId, Long userId) {
        try {
            // 实际应该从数据库查询
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", posterId);
            detail.put("title", "推广海报详情");
            detail.put("posterType", 1);
            detail.put("posterTypeName", "商品海报");
            detail.put("posterUrl", posterBaseUrl + "user_" + userId + "_poster_" + posterId + ".jpg");
            detail.put("qrCodeUrl", qrCodeBaseUrl + "user_" + userId + "_qr_" + posterId + ".jpg");
            detail.put("createTime", new Date());
            detail.put("shareCount", 15);
            detail.put("viewCount", 128);
            detail.put("distributionCode", distributionService.getMyDistributionCode(userId));
            
            return detail;
        } catch (Exception e) {
            LOGGER.error("获取海报详情失败, posterId: {}, userId: {}", posterId, userId, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> sharePoster(Long posterId, Long userId) {
        try {
            Map<String, Object> shareInfo = new HashMap<>();
            
            // 生成分享链接
            String shareUrl = "https://yourdomain.com/poster/share?id=" + posterId + "&user=" + userId;
            
            shareInfo.put("shareUrl", shareUrl);
            shareInfo.put("shareTitle", "快来看看我的推广海报");
            shareInfo.put("shareDesc", "优质商品，超值推荐，快来抢购吧！");
            shareInfo.put("shareImage", posterBaseUrl + "user_" + userId + "_poster_" + posterId + ".jpg");
            
            // 更新分享统计（实际应该更新数据库）
            LOGGER.info("海报分享统计更新, posterId: {}, userId: {}", posterId, userId);
            
            return shareInfo;
        } catch (Exception e) {
            LOGGER.error("分享海报失败, posterId: {}, userId: {}", posterId, userId, e);
            throw new RuntimeException("分享失败");
        }
    }
    
    /**
     * 构建海报数据
     */
    private Map<String, Object> buildPosterData(Long userId, PosterGenerateParam param, String distributionCode) {
        Map<String, Object> posterData = new HashMap<>();
        
        // 基础信息
        UmsMember member = memberMapper.selectByPrimaryKey(userId);
        posterData.put("userNickname", member.getNickname());
        posterData.put("userAvatar", member.getIcon());
        posterData.put("distributionCode", distributionCode);
        
        // 根据海报类型添加特定数据
        switch (param.getPosterType()) {
            case 1: // 商品海报
                if (param.getProductId() != null) {
                    PmsProduct product = productMapper.selectByPrimaryKey(param.getProductId());
                    if (product != null) {
                        posterData.put("productName", product.getName());
                        posterData.put("productPrice", product.getPrice());
                        posterData.put("productImage", product.getPic());
                        posterData.put("productDescription", product.getSubTitle());
                    }
                }
                break;
            case 2: // 店铺海报
                posterData.put("storeName", "优选好物商城");
                posterData.put("storeSlogan", "精选优质商品，为您提供最好的购物体验");
                break;
            case 3: // 活动海报
                posterData.put("activityTitle", "限时大促销");
                posterData.put("activityDesc", "全场商品5折起，更多优惠等你来");
                break;
        }
        
        // 自定义文案
        if (param.getCustomText() != null) {
            posterData.put("customText", param.getCustomText());
        }
        
        posterData.put("generateTime", new Date());
        
        return posterData;
    }
    
    /**
     * 生成海报URL
     */
    private String generatePosterUrl(Map<String, Object> posterData) {
        // 这里简化处理，实际应该调用图片生成服务
        String filename = "poster_" + System.currentTimeMillis() + ".jpg";
        return posterBaseUrl + filename;
    }
    
    /**
     * 生成二维码URL
     */
    private String generateQrCodeUrl(String distributionCode) {
        // 这里简化处理，实际应该调用二维码生成服务
        String filename = "qr_" + distributionCode + ".jpg";
        return qrCodeBaseUrl + filename;
    }
    
    /**
     * 保存海报记录
     */
    private Long savePosterRecord(Long userId, PosterGenerateParam param, String posterUrl, String qrCodeUrl, String distributionCode) {
        // 实际应该保存到 pms_poster_record 表
        // 这里返回模拟ID
        Long posterId = System.currentTimeMillis() % 100000;
        LOGGER.info("保存海报记录, posterId: {}, userId: {}, posterUrl: {}", posterId, userId, posterUrl);
        return posterId;
    }
    
    /**
     * 生成分享文案
     */
    private String generateShareText(PosterGenerateParam param) {
        switch (param.getPosterType()) {
            case 1:
                return "发现好物，赶紧来看看这个超值商品！";
            case 2:
                return "我的专属店铺，精选好物等你来挑选！";
            case 3:
                return "限时活动火热进行中，快来参与吧！";
            default:
                return "快来看看我为你推荐的好物！";
        }
    }
} 