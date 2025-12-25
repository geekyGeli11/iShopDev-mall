package com.macro.mall.portal.service.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.DiyDesignParam;
import com.macro.mall.portal.domain.DiyPreviewResult;
import com.macro.mall.portal.domain.ProductDiyConfig;
import com.macro.mall.portal.domain.FacePreviewResult;
import com.macro.mall.portal.domain.TemplateFaceConfig;
import com.macro.mall.portal.domain.CustomizableArea;
import com.macro.mall.portal.service.PortalDiyService;
import com.macro.mall.portal.service.AliyunWanxService;
import com.macro.mall.portal.util.MaskBlendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小程序端DIY功能Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PortalDiyServiceImpl implements PortalDiyService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalDiyServiceImpl.class);

    // 图片缓存
    private final Map<String, BufferedImage> imageCache = new ConcurrentHashMap<>();

    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsDiyTemplateMapper templateMapper;
    
    @Autowired
    private PmsDiyMaterialMapper materialMapper;

    @Autowired
    private PmsDiyMaterialCategoryMapper materialCategoryMapper;

    @Autowired
    private PmsDiyTemplateSurfaceMapper templateSurfaceMapper;

    @Autowired
    private PmsDiyAreaMapper diyAreaMapper;

    @Autowired
    private UmsDiyDesignMapper designMapper;
    
    @Autowired
    private UmsAiStylizationRecordMapper aiRecordMapper;

    @Autowired
    private AliyunWanxService aliyunWanxService;

    // OSS相关配置
    @Autowired
    private OSSClient ossClient;

    @Value("${aliyun.oss.endpoint}")
    private String ossEndpoint;

    @Value("${aliyun.oss.bucketName}")
    private String ossBucketName;

    @Value("${aliyun.oss.dir.prefix}")
    private String ossDirectoryPrefix;

    @Override
    public PmsDiyTemplate getDiyTemplateByProductId(Long productId) {
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getDiyEnabled() == null || product.getDiyEnabled() != 1) {
            return null;
        }
        
        if (product.getDiyTemplateId() != null) {
            return templateMapper.selectByPrimaryKey(product.getDiyTemplateId());
        }
        
        return null;
    }

    @Override
    public List<PmsDiyMaterialCategory> getDiyMaterialCategories() {
        PmsDiyMaterialCategoryExample example = new PmsDiyMaterialCategoryExample();
        PmsDiyMaterialCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 只获取启用的分类
        example.setOrderByClause("sort asc, create_time desc");
        return materialCategoryMapper.selectByExample(example);
    }

    @Override
    public List<PmsDiyMaterial> getDiyMaterials(Long categoryId, Integer type) {
        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        PmsDiyMaterialExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 只获取启用的素材
        
        if (categoryId != null) {
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if (type != null) {
            // 根据type值映射到对应的文件类型进行筛选
            String fileTypePattern = getFileTypePattern(type);
            if (fileTypePattern != null) {
                criteria.andFileTypeLike("%" + fileTypePattern + "%");
            }
        }
        
        // 注意：如果数据库已添加sort字段，可以使用：example.setOrderByClause("sort asc, create_time desc");
        example.setOrderByClause("create_time desc");
        return materialMapper.selectByExample(example);
    }

    @Override
    public Long saveDiyDesign(DiyDesignParam designParam, Long memberId) {
        UmsDiyDesign design = new UmsDiyDesign();
        BeanUtils.copyProperties(designParam, design);
        design.setUserId(memberId); // 设置用户ID
        design.setCreateTime(new Date());
        design.setUpdateTime(new Date());

        // 处理预览图数组
        if (designParam.getPreviewImages() != null) {
            design.setPreviewImages(designParam.getPreviewImages());
        }

        // 设置状态：草稿为1，完成为2
        if (designParam.getIsDraft() != null) {
            design.setStatus(designParam.getIsDraft() ? (byte) 1 : (byte) 2);
        } else {
            design.setStatus((byte) 1); // 默认为草稿
        }

        // 设计名称通过设计数据中的元数据来管理，这里不需要单独的字段

        LOGGER.info("保存新DIY设计，用户ID: {}, 预览图数量: {}", memberId,
                   designParam.getPreviewImages() != null ? "已设置" : "未设置");

        int result = designMapper.insertSelective(design);
        return result > 0 ? design.getId() : null;
    }

    @Override
    public int updateDiyDesign(Long designId, DiyDesignParam designParam) {
        UmsDiyDesign design = new UmsDiyDesign();
        BeanUtils.copyProperties(designParam, design);
        design.setId(designId);
        design.setUpdateTime(new Date());

        // 处理预览图数组
        if (designParam.getPreviewImages() != null) {
            design.setPreviewImages(designParam.getPreviewImages());
        }

        // 设置状态：草稿为1，完成为2
        if (designParam.getIsDraft() != null) {
            design.setStatus(designParam.getIsDraft() ? (byte) 1 : (byte) 2);
        }

        LOGGER.info("更新DIY设计，ID: {}, 预览图数量: {}", designId,
                   designParam.getPreviewImages() != null ? "已设置" : "未设置");

        return designMapper.updateByPrimaryKeySelective(design);
    }

    @Override
    public UmsDiyDesign getDiyDesign(Long designId, Long memberId) {
        // 查询设计数据并验证用户权限
        UmsDiyDesign design = designMapper.selectByPrimaryKey(designId);
        if (design != null && !design.getUserId().equals(memberId)) {
            // 如果设计存在但不属于当前用户，返回null
            return null;
        }
        return design;
    }

    @Override
    public List<UmsDiyDesign> getUserDiyDesigns(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsDiyDesignExample example = new UmsDiyDesignExample();
        example.createCriteria().andUserIdEqualTo(memberId);
        example.setOrderByClause("update_time desc");
        return designMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public int deleteDiyDesign(Long designId, Long memberId) {
        UmsDiyDesignExample example = new UmsDiyDesignExample();
        example.createCriteria()
                .andIdEqualTo(designId)
                .andUserIdEqualTo(memberId);
        return designMapper.deleteByExample(example);
    }

    @Override
    public DiyPreviewResult generatePreview(DiyDesignParam designParam) {
        System.out.println("🚀🚀🚀🚀🚀 DIY预览图生成开始 - 商品ID: " + designParam.getProductId() + ", 模板ID: " + designParam.getTemplateId());
        LOGGER.error("🚀🚀🚀🚀🚀 DIY预览图生成开始 - 商品ID: {}, 模板ID: {}",
            designParam.getProductId(), designParam.getTemplateId());

        // 添加文件调试 - 确保我们能看到代码被执行
        try {
            String debugPath = System.getProperty("user.dir") + "/debug_diy_execution.txt";
            java.nio.file.Files.write(
                java.nio.file.Paths.get(debugPath),
                ("DIY预览图生成被调用 - " + new java.util.Date() +
                 " - 商品ID: " + designParam.getProductId() +
                 ", 模板ID: " + designParam.getTemplateId() +
                 " - 工作目录: " + System.getProperty("user.dir") + "\n").getBytes(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            );
            System.out.println("✅ 调试文件写入成功: " + debugPath);
        } catch (Exception e) {
            System.out.println("❌ 写入调试文件失败: " + e.getMessage());
            e.printStackTrace();
        }

        DiyPreviewResult result = new DiyPreviewResult();
        result.setTimestamp(System.currentTimeMillis());

        try {
            // 1. 参数验证
            System.out.println("📋📋📋 开始参数验证...");
            LOGGER.error("📋📋📋 开始参数验证...");
            validateDesignParam(designParam);

            // 2. 解析设计数据
            System.out.println("📊📊📊 开始解析设计数据...");
            LOGGER.error("📊📊📊 开始解析设计数据...");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode designData = objectMapper.readTree(designParam.getDesignData());
            System.out.println("📊📊📊 设计数据解析完成，数据: " + designData.toString());
            LOGGER.error("📊📊📊 设计数据解析完成，数据: {}", designData.toString());

            // 3. 获取模板信息和定制区域配置
            System.out.println("🔍🔍🔍 获取模板信息，模板ID: " + designParam.getTemplateId());
            LOGGER.error("🔍🔍🔍 获取模板信息，模板ID: {}", designParam.getTemplateId());
            PmsDiyTemplate template = templateMapper.selectByPrimaryKey(designParam.getTemplateId());
            if (template == null) {
                System.out.println("❌❌❌ 模板不存在: " + designParam.getTemplateId());
                LOGGER.error("❌❌❌ 模板不存在: {}", designParam.getTemplateId());
                throw new IllegalArgumentException("DIY模板不存在");
            }
            System.out.println("✅✅✅ 模板获取成功: " + template.getName());
            LOGGER.error("✅✅✅ 模板获取成功: {}", template.getName());

            // 4. 批量生成各面预览图
            System.out.println("🎨🎨🎨 开始生成多面预览图...");
            LOGGER.error("🎨🎨🎨 开始生成多面预览图...");
            List<FacePreviewResult> faceResults = generateMultiFacePreview(designData, template);

            // 5. 构建返回结果
            result.setPreviewImages(faceResults);
            result.setStatus(1); // 生成成功

            LOGGER.info("生成DIY预览图成功，共{}个面", faceResults.size());

        } catch (Exception e) {
            LOGGER.error("生成DIY预览图失败", e);
            result.setStatus(2); // 生成失败
            result.setErrorMessage("预览图生成失败：" + e.getMessage());

            // 降级方案：返回默认预览图
            result.setPreviewImages(generateFallbackPreview(designParam));
        }

        return result;
    }

    @Override
    public List<Object> getAIStyles() {
        // 返回AI风格列表
        List<Object> styles = new ArrayList<>();

        // 创建风格数据
        Map<String, Object> style1 = new HashMap<>();
        style1.put("id", 1);
        style1.put("name", "油画风格");
        style1.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-oil.jpg");
        style1.put("description", "经典油画艺术风格");
        styles.add(style1);

        Map<String, Object> style2 = new HashMap<>();
        style2.put("id", 2);
        style2.put("name", "富贵人间");
        style2.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-rich.jpg");
        style2.put("description", "富贵典雅风格");
        styles.add(style2);

        Map<String, Object> style3 = new HashMap<>();
        style3.put("id", 3);
        style3.put("name", "赛博朋克");
        style3.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-cyber.jpg");
        style3.put("description", "未来科技风格");
        styles.add(style3);

        Map<String, Object> style4 = new HashMap<>();
        style4.put("id", 4);
        style4.put("name", "多彩插画");
        style4.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-colorful.jpg");
        style4.put("description", "多彩插画风格");
        styles.add(style4);

        Map<String, Object> style5 = new HashMap<>();
        style5.put("id", 5);
        style5.put("name", "暖光街道");
        style5.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-warm.jpg");
        style5.put("description", "温暖街道风格");
        styles.add(style5);

        Map<String, Object> style6 = new HashMap<>();
        style6.put("id", 6);
        style6.put("name", "新国风国潮建筑插画风");
        style6.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-chinese.jpg");
        style6.put("description", "新国风建筑插画风格");
        styles.add(style6);

        Map<String, Object> style7 = new HashMap<>();
        style7.put("id", 7);
        style7.put("name", "清凉夏日");
        style7.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-summer.jpg");
        style7.put("description", "清凉夏日风格");
        styles.add(style7);

        Map<String, Object> style8 = new HashMap<>();
        style8.put("id", 8);
        style8.put("name", "国风水墨画");
        style8.put("image", "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-ink.jpg");
        style8.put("description", "传统水墨画风格");
        styles.add(style8);

        LOGGER.info("获取AI风格列表成功，共{}个风格", styles.size());
        return styles;
    }

    @Override
    public String aiStylization(Long memberId, String imageUrl, String style, String prompt, String functionType) {
        try {
            LOGGER.info("开始AI风格化处理：原图={}, 风格={}, 提示词={}, 功能类型={}", imageUrl, style, prompt, functionType);

            // 前端已经组合好了完整的提示词，直接使用prompt参数
            // style参数仅用于记录和日志，万相API只需要prompt
            String finalPrompt = prompt != null && !prompt.trim().isEmpty() ? prompt.trim() : style;

            // 如果没有传递 functionType，使用默认值
            String finalFunctionType = functionType != null && !functionType.trim().isEmpty()
                ? functionType.trim()
                : "description_edit";

            // 调用阿里云万相API进行真正的AI风格化，传递 functionType
            String stylizedImageUrl = aliyunWanxService.stylizeImage(imageUrl, finalPrompt, finalFunctionType);

            // 保存AI风格化记录
            saveAiStylizationRecord(memberId, imageUrl, stylizedImageUrl, style, prompt);

            LOGGER.info("AI风格化处理成功：{} -> {}，最终提示词：{}，功能类型：{}", imageUrl, stylizedImageUrl, finalPrompt, finalFunctionType);
            return stylizedImageUrl;

        } catch (Exception e) {
            LOGGER.error("AI风格化处理失败", e);
            throw new RuntimeException("AI风格化处理失败：" + e.getMessage());
        }
    }

    @Override
    public List<UmsAiStylizationRecord> getAiStylizationRecords(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsAiStylizationRecordExample example = new UmsAiStylizationRecordExample();
        example.createCriteria().andUserIdEqualTo(memberId);
        example.setOrderByClause("create_time desc");
        return aiRecordMapper.selectByExample(example);
    }

    @Override
    public boolean checkProductDiyEnabled(Long productId) {
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        return product != null && product.getDiyEnabled() != null && product.getDiyEnabled() == 1;
    }

    @Override
    public ProductDiyConfig getProductDiyConfig(Long productId) {
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return null;
        }
        
        ProductDiyConfig config = new ProductDiyConfig();
        config.setProductId(product.getId());
        config.setProductName(product.getName());
        config.setDiyEnabled(product.getDiyEnabled() != null && product.getDiyEnabled() == 1);
        config.setDiyTemplateId(product.getDiyTemplateId());
        config.setProductImage(product.getPic());
        config.setPrice(product.getPrice());
        
        // 获取DIY模板信息
        if (product.getDiyTemplateId() != null) {
            PmsDiyTemplate template = templateMapper.selectByPrimaryKey(product.getDiyTemplateId());
            config.setDiyTemplate(template);
        }
        
        return config;
    }
    
    /**
     * 参数验证
     */
    private void validateDesignParam(DiyDesignParam designParam) {
        if (designParam == null) {
            throw new IllegalArgumentException("设计参数不能为空");
        }
        if (designParam.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (designParam.getTemplateId() == null) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        if (StringUtils.isEmpty(designParam.getDesignData())) {
            throw new IllegalArgumentException("设计数据不能为空");
        }
    }

    /**
     * 生成多面预览图
     */
    private List<FacePreviewResult> generateMultiFacePreview(JsonNode designData, PmsDiyTemplate template) {
        System.out.println("🎭🎭🎭 进入generateMultiFacePreview方法");
        LOGGER.error("🎭🎭🎭 进入generateMultiFacePreview方法");
        List<FacePreviewResult> results = new ArrayList<>();

        try {
            // 获取模板配置的面信息
            System.out.println("📋📋📋 开始解析模板面配置...");
            LOGGER.error("📋📋📋 开始解析模板面配置...");
            List<TemplateFaceConfig> faceConfigs = parseTemplateFaceConfigs(template);
            System.out.println("📋📋📋 模板面配置解析完成，面数量: " + faceConfigs.size());
            LOGGER.error("📋📋📋 模板面配置解析完成，面数量: {}", faceConfigs.size());

            // 获取设计数据中的面信息
            JsonNode facesNode = designData.get("faces");
            if (facesNode == null || !facesNode.isArray()) {
                throw new IllegalArgumentException("设计数据中缺少faces信息");
            }

            // 为每个面生成预览图 - 根据面ID匹配配置
            for (int i = 0; i < facesNode.size(); i++) {
                JsonNode faceData = facesNode.get(i);

                // 获取面ID，支持多种字段名
                Long faceId = null;
                if (faceData.has("faceId")) {
                    faceId = faceData.get("faceId").asLong();
                } else if (faceData.has("id")) {
                    faceId = faceData.get("id").asLong();
                }

                // 根据面ID查找对应的配置
                TemplateFaceConfig faceConfig = findFaceConfigById(faceConfigs, faceId);
                if (faceConfig == null) {
                    // 如果找不到对应配置，使用索引匹配（兼容旧逻辑）
                    faceConfig = i < faceConfigs.size() ? faceConfigs.get(i) : faceConfigs.get(0);
                    LOGGER.warn("⚠️ 面ID {} 找不到对应配置，使用索引 {} 的配置", faceId, i);
                } else {
                    LOGGER.info("✅ 面ID {} 匹配到配置: {}", faceId, faceConfig.getFaceName());
                }

                FacePreviewResult faceResult = generateSingleFacePreview(faceData, faceConfig, i);
                results.add(faceResult);
            }

        } catch (Exception e) {
            LOGGER.error("生成多面预览图失败", e);
            throw new RuntimeException("多面预览图生成失败", e);
        }

        return results;
    }

    /**
     * 根据面ID查找对应的面配置
     */
    private TemplateFaceConfig findFaceConfigById(List<TemplateFaceConfig> faceConfigs, Long faceId) {
        if (faceId == null || faceConfigs == null) {
            return null;
        }

        for (TemplateFaceConfig config : faceConfigs) {
            if (config.getFaceId() != null && config.getFaceId().equals(faceId)) {
                return config;
            }
        }

        return null;
    }

    /**
     * 生成单个面的预览图（增强调试版本）
     */
    private FacePreviewResult generateSingleFacePreview(JsonNode faceData, TemplateFaceConfig faceConfig, int faceIndex) {
        FacePreviewResult result = new FacePreviewResult();
        result.setFaceIndex(faceIndex);
        result.setFaceName(faceConfig.getFaceName());

        LOGGER.info("🎨 开始生成面 {} ({}) 的预览图", faceIndex, faceConfig.getFaceName());

        try {
            // 1. 加载底图
            LOGGER.info("📷 加载底图: {}", faceConfig.getBaseImageUrl());
            BufferedImage baseImage = loadTemplateBaseImage(faceConfig.getBaseImageUrl());
            if (baseImage == null) {
                throw new RuntimeException("无法加载底图: " + faceConfig.getBaseImageUrl());
            }
            LOGGER.info("✅ 底图加载成功: {}x{}", baseImage.getWidth(), baseImage.getHeight());

            // 2. 获取定制区域配置
            CustomizableArea customArea = faceConfig.getCustomizableArea();
            LOGGER.info("📐 定制区域配置: x={}, y={}, w={}, h={}",
                customArea.getX(), customArea.getY(), customArea.getWidth(), customArea.getHeight());

            // 3. 检查设计数据
            if (faceData == null || !faceData.has("elements")) {
                LOGGER.warn("⚠️ 面 {} 没有设计元素，将生成空白预览图", faceIndex);
            } else {
                JsonNode elements = faceData.get("elements");
                LOGGER.info("🎯 面 {} 包含 {} 个设计元素", faceIndex, elements.size());

                // 打印每个元素的详细信息
                for (int i = 0; i < elements.size(); i++) {
                    JsonNode element = elements.get(i);
                    String type = element.has("type") ? element.get("type").asText() : "unknown";
                    LOGGER.info("  元素 {}: type={}, data={}", i, type, element.toString());
                }
            }

            // 4. 生成用户设计的合成图
            LOGGER.info("🖼️ 生成用户设计图...");
            BufferedImage userDesignImage = generateUserDesignImage(faceData, customArea);
            boolean hasContent = hasVisibleContent(userDesignImage);
            LOGGER.info("✅ 用户设计图生成完成: {}x{}, 有可见内容: {}",
                userDesignImage.getWidth(), userDesignImage.getHeight(), hasContent);

            // 5. 将用户设计图合成到底图的定制区域
            LOGGER.info("🔄 开始图像合成...");
            BufferedImage finalImage = compositeImageToCustomArea(baseImage, userDesignImage, customArea);
            LOGGER.info("✅ 图像合成完成: {}x{}", finalImage.getWidth(), finalImage.getHeight());

            // 6. 上传并获取URL（使用PNG格式支持透明背景）
            String fileName = "face_" + faceIndex + "_" + System.currentTimeMillis() + ".png";
            LOGGER.info("☁️ 上传图片到存储: {}", fileName);
            String imageUrl = uploadImageToStorage(finalImage, fileName);
            LOGGER.info("✅ 图片上传成功: {}", imageUrl);

            result.setPreviewImageUrl(imageUrl);
            result.setStatus("success");

        } catch (Exception e) {
            LOGGER.error("❌ 生成面{}预览图失败", faceIndex, e);
            result.setStatus("failed");
            result.setErrorMessage(e.getMessage());
            result.setPreviewImageUrl(generateDefaultFacePreviewUrl(faceIndex));
        }

        LOGGER.info("🏁 面 {} 预览图生成完成，状态: {}", faceIndex, result.getStatus());
        return result;
    }

    /**
     * 生成真实的预览图
     */
    private String generateRealPreviewImage(DiyDesignParam designParam, PmsDiyTemplate template, String designDataJson) {
        try {
            // 解析设计数据
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode designData = objectMapper.readTree(designDataJson);

            // 获取模板基础图片
            String templateImageUrl = getDefaultTemplateImageUrl(template);
            BufferedImage baseImage = loadImageFromUrl(templateImageUrl);

            if (baseImage == null) {
                // 如果无法加载模板图片，创建默认画布
                baseImage = createDefaultCanvas(800, 600);
            }

            // 在基础图片上渲染设计元素
            BufferedImage resultImage = renderDesignElements(baseImage, designData);

            // 上传图片到文件存储并返回URL（使用PNG格式支持透明背景）
            return uploadImageToStorage(resultImage, "preview_" + System.currentTimeMillis() + ".png");

        } catch (Exception e) {
            LOGGER.error("生成预览图失败", e);
            // 返回默认预览图
            return generateDefaultPreviewUrl(designParam);
        }
    }

    /**
     * 生成高清预览图
     */
    private String generateHDPreviewImage(DiyDesignParam designParam, PmsDiyTemplate template, String designDataJson) {
        try {
            // 解析设计数据
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode designData = objectMapper.readTree(designDataJson);

            // 获取模板基础图片（高清版本）
            String templateImageUrl = getDefaultTemplateImageUrl(template);
            BufferedImage baseImage = loadImageFromUrl(templateImageUrl);

            if (baseImage == null) {
                // 创建高清默认画布
                baseImage = createDefaultCanvas(1600, 1200);
            } else {
                // 放大到高清尺寸
                baseImage = scaleImage(baseImage, 2.0);
            }

            // 在基础图片上渲染设计元素（高清版本）
            BufferedImage resultImage = renderDesignElements(baseImage, designData);

            // 上传高清图片到文件存储并返回URL（使用PNG格式支持透明背景）
            return uploadImageToStorage(resultImage, "hd_preview_" + System.currentTimeMillis() + ".png");

        } catch (Exception e) {
            LOGGER.error("生成高清预览图失败", e);
            // 返回默认预览图
            return generateDefaultPreviewUrl(designParam);
        }
    }
    

    
    /**
     * 保存AI风格化记录
     */
    private void saveAiStylizationRecord(Long memberId, String originalImageUrl, String stylizedImageUrl, String style, String prompt) {
        UmsAiStylizationRecord record = new UmsAiStylizationRecord();
        record.setUserId(memberId); // 设置用户ID
        record.setOriginalImage(originalImageUrl);
        record.setStylizedImage(stylizedImageUrl);
        // 将风格和用户提示词组合保存到stylePrompt字段
        String combinedPrompt = style;
        if (prompt != null && !prompt.trim().isEmpty()) {
            combinedPrompt = style + " | " + prompt.trim();
        }
        record.setStylePrompt(combinedPrompt);
        record.setStatus((byte) 1); // 设置状态为成功
        record.setCreateTime(new Date());

        aiRecordMapper.insertSelective(record);
    }

    @Override
    public List<Object> getProductCustomizableAreas(Long productId) {
        try {
            // 检查商品是否存在且支持DIY
            PmsProduct product = productMapper.selectByPrimaryKey(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            if (product.getDiyEnabled() == null || product.getDiyEnabled() != 1) {
                throw new RuntimeException("该商品不支持DIY定制");
            }

            // 获取商品的DIY模板信息
            PmsDiyTemplate template = getDiyTemplateByProductId(productId);
            if (template == null) {
                throw new RuntimeException("该商品未配置DIY模板");
            }

            // 从数据库查询模板的所有定制面
            List<Object> areas = new ArrayList<>();
            List<PmsDiyTemplateSurface> surfaces = getTemplateSurfaces(template.getId());

            if (surfaces == null || surfaces.isEmpty()) {
                // 如果数据库中没有配置定制面，返回默认的模拟数据作为兜底
                return getDefaultCustomizableAreas(product);
            }

            // 将数据库中的定制面数据转换为API返回格式
            for (PmsDiyTemplateSurface surface : surfaces) {
                areas.add(createCustomizableAreaFromSurface(surface));
            }

            return areas;

        } catch (Exception e) {
            LOGGER.error("获取商品可定制面信息失败", e);
            throw new RuntimeException("获取可定制面信息失败：" + e.getMessage());
        }
    }

    /**
     * 创建可定制面对象
     */
    private Object createCustomizableArea(Long id, String name, String previewImage, boolean customizable) {
        Map<String, Object> area = new HashMap<>();
        area.put("id", id);
        area.put("name", name);
        area.put("previewImage", previewImage);
        area.put("previewImageWithMarks", generatePreviewImageWithMarks(previewImage, id));
        area.put("customizable", customizable);

        // 添加可定制区域的坐标信息
        if (customizable) {
            area.put("customizableRegion", getCustomizableRegion(id, name));
        }

        return area;
    }

    /**
     * 生成带有可定制区域标识的预览图URL
     */
    private String generatePreviewImageWithMarks(String originalImage, Long areaId) {
        // 这里可以调用图片处理服务，在原图上叠加可定制区域的标识
        // 暂时返回原图，实际项目中可以集成图片处理服务
        return originalImage + "?marks=true&areaId=" + areaId;
    }

    /**
     * 从URL加载图片
     */
    private BufferedImage loadImageFromUrl(String imageUrl) {
        InputStream inputStream = null;
        try {
            if (StringUtils.isEmpty(imageUrl)) {
                LOGGER.warn("图片URL为空");
                return null;
            }

            LOGGER.debug("开始加载图片: {}", imageUrl);
            URL url = new URL(imageUrl);

            // 设置连接超时和读取超时
            java.net.URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(30000);    // 30秒读取超时

            // 设置User-Agent，避免某些服务器拒绝请求
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            inputStream = connection.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);

            if (image != null) {
                LOGGER.debug("图片加载成功: {} ({}x{})", imageUrl, image.getWidth(), image.getHeight());
            } else {
                LOGGER.error("图片加载失败，ImageIO.read返回null: {}", imageUrl);
            }

            return image;

        } catch (java.net.MalformedURLException e) {
            LOGGER.error("无效的图片URL: {}", imageUrl, e);
        } catch (java.net.SocketTimeoutException e) {
            LOGGER.error("图片加载超时: {}", imageUrl, e);
        } catch (java.io.IOException e) {
            LOGGER.error("图片加载IO异常: {}", imageUrl, e);
        } catch (Exception e) {
            LOGGER.error("图片加载未知异常: {}", imageUrl, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.warn("关闭输入流失败", e);
                }
            }
        }

        return null;
    }

    /**
     * 创建默认画布
     */
    private BufferedImage createDefaultCanvas(int width, int height) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 创建透明背景（不填充任何颜色）
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        g2d.dispose();
        return canvas;
    }

    /**
     * 缩放图片
     */
    private BufferedImage scaleImage(BufferedImage originalImage, double scale) {
        int newWidth = (int) (originalImage.getWidth() * scale);
        int newHeight = (int) (originalImage.getHeight() * scale);

        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    /**
     * 缩放图片以适应指定尺寸（保持宽高比）
     */
    private BufferedImage scaleImageToFit(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if (originalImage == null) {
            return null;
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，保持宽高比
        double scaleX = (double) targetWidth / originalWidth;
        double scaleY = (double) targetHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY); // 使用较小的比例以确保图片完全适应

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 居中绘制
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;
        g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);

        g2d.dispose();
        return scaledImage;
    }

    /**
     * 缩放图片以完全填满指定尺寸（可能裁剪图片内容）
     * 优先保证目标区域100%被覆盖，图片内容可以适当裁剪
     */
    private BufferedImage scaleImageToFill(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if (originalImage == null) {
            return null;
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，确保完全填满目标区域
        double scaleX = (double) targetWidth / originalWidth;
        double scaleY = (double) targetHeight / originalHeight;
        double scale = Math.max(scaleX, scaleY); // 使用较大的比例以确保完全填满

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 居中绘制，可能会裁剪超出部分
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;
        g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);

        g2d.dispose();

        LOGGER.info("🎯 图片填充缩放完成: 原始={}x{}, 目标={}x{}, 缩放比例={:.2f}, 实际绘制={}x{}",
            originalWidth, originalHeight, targetWidth, targetHeight, scale, newWidth, newHeight);

        return scaledImage;
    }

    /**
     * 为SVG形状优化的图片缩放方法
     * 确保图片完全覆盖SVG形状的边界框
     */
    private BufferedImage scaleImageForSVGShape(BufferedImage originalImage, CustomizableArea customArea) {
        if (originalImage == null) {
            return null;
        }

        // 如果有SVG路径数据，计算实际需要的覆盖区域
        if (StringUtils.hasText(customArea.getPathData())) {
            Shape svgShape = parseSVGPathToShape(customArea.getPathData());
            if (svgShape != null) {
                Rectangle2D bounds = svgShape.getBounds2D();

                // 为了确保完全覆盖，稍微扩大边界（增加5%的边距）
                int expandedWidth = (int) (bounds.getWidth() * 1.05);
                int expandedHeight = (int) (bounds.getHeight() * 1.05);

                LOGGER.info("🎯 SVG形状优化缩放: SVG边界={}x{}, 扩展后={}x{}",
                    (int)bounds.getWidth(), (int)bounds.getHeight(), expandedWidth, expandedHeight);

                return scaleImageToFill(originalImage, expandedWidth, expandedHeight);
            }
        }

        // 回退到普通填充缩放
        return scaleImageToFill(originalImage, customArea.getWidth(), customArea.getHeight());
    }

    /**
     * 解析模板面配置 - 从数据库获取真实数据
     */
    private List<TemplateFaceConfig> parseTemplateFaceConfigs(PmsDiyTemplate template) {
        List<TemplateFaceConfig> configs = new ArrayList<>();

        try {
            // 从数据库查询模板的所有面
            List<PmsDiyTemplateSurface> surfaces = getTemplateSurfaces(template.getId());

            if (surfaces != null && !surfaces.isEmpty()) {
                for (PmsDiyTemplateSurface surface : surfaces) {
                    TemplateFaceConfig config = createFaceConfigFromSurface(surface);
                    configs.add(config);
                }
                LOGGER.info("从数据库加载了{}个面配置", configs.size());
            } else {
                LOGGER.warn("模板ID {}没有找到面配置，使用默认配置", template.getId());
                configs.add(createDefaultFaceConfig(template));
            }

        } catch (Exception e) {
            LOGGER.error("从数据库解析模板面配置失败，模板ID: {}", template.getId(), e);
            configs.add(createDefaultFaceConfig(template));
        }

        return configs;
    }

    /**
     * 查询模板的所有面
     */
    private List<PmsDiyTemplateSurface> getTemplateSurfaces(Long templateId) {
        try {
            PmsDiyTemplateSurfaceExample example = new PmsDiyTemplateSurfaceExample();
            example.createCriteria().andTemplateIdEqualTo(templateId);
            example.setOrderByClause("sort ASC, id ASC");

            List<PmsDiyTemplateSurface> surfaces = templateSurfaceMapper.selectByExample(example);
            LOGGER.info("查询到模板ID {}的面数量: {}", templateId, surfaces.size());

            return surfaces;
        } catch (Exception e) {
            LOGGER.error("查询模板面失败，模板ID: {}", templateId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 从数据库面数据创建面配置
     */
    private TemplateFaceConfig createFaceConfigFromSurface(PmsDiyTemplateSurface surface) {
        TemplateFaceConfig config = new TemplateFaceConfig();

        config.setFaceId(surface.getId()); // 设置面ID，用于匹配前端传来的面数据
        config.setFaceName(surface.getName());
        config.setBaseImageUrl(surface.getExampleImage());

        // 获取该面的定制区域
        CustomizableArea customArea = getCustomizableAreaBySurfaceId(surface.getId());
        config.setCustomizableArea(customArea);

        LOGGER.debug("创建面配置: ID={}, 名称={}, 底图={}", surface.getId(), surface.getName(), surface.getExampleImage());

        return config;
    }

    /**
     * 根据面ID获取定制区域
     */
    private CustomizableArea getCustomizableAreaBySurfaceId(Long surfaceId) {
        try {
            PmsDiyAreaExample example = new PmsDiyAreaExample();
            example.createCriteria().andSurfaceIdEqualTo(surfaceId);

            List<PmsDiyArea> areas = diyAreaMapper.selectByExample(example);

            if (areas != null && !areas.isEmpty()) {
                // 取第一个区域（通常一个面只有一个主要定制区域）
                PmsDiyArea area = areas.get(0);
                return parseCustomizableAreaFromBounds(area);
            } else {
                LOGGER.warn("面ID {}没有找到定制区域，使用默认区域", surfaceId);
                return createDefaultCustomizableArea();
            }

        } catch (Exception e) {
            LOGGER.error("查询定制区域失败，面ID: {}", surfaceId, e);
            return createDefaultCustomizableArea();
        }
    }

    /**
     * 从bounds字符串解析定制区域（增强版本）
     */
    private CustomizableArea parseCustomizableAreaFromBounds(PmsDiyArea area) {
        CustomizableArea customArea = new CustomizableArea();

        try {
            String bounds = area.getBounds();
            if (StringUtils.hasText(bounds)) {
                // 检查bounds格式并解析
                if (bounds.contains("L") && bounds.contains(",")) {
                    // SVG路径格式: "5 319.05625 L 199.5 319.05625 L 199.81,339.239"
                    customArea = parseSVGPathBounds(bounds, area.getName());
                } else if (bounds.split(",").length >= 4) {
                    // 简单格式: "x,y,width,height"
                    customArea = parseSimpleBounds(bounds, area.getName());
                } else {
                    LOGGER.warn("无法识别的bounds格式: {}, 使用默认区域", bounds);
                    return createDefaultCustomizableArea();
                }

                // 保存原始的pathData用于精确裁剪
                if (StringUtils.hasText(area.getPathData())) {
                    customArea.setPathData(area.getPathData());
                    customArea.setName(area.getName());
                    LOGGER.info("🎯 保存SVG路径数据: 区域={}, pathData={}", area.getName(), area.getPathData());
                }

                // 保存蒙版URL(画笔模式)
                if (StringUtils.hasText(area.getMaskImageUrl())) {
                    customArea.setMaskImageUrl(area.getMaskImageUrl());
                    LOGGER.info("🎭 保存蒙版URL: 区域={}, maskImageUrl={}", area.getName(), area.getMaskImageUrl());
                }

                // 验证解析结果的合理性
                if (customArea.getWidth() <= 0 || customArea.getHeight() <= 0) {
                    LOGGER.warn("解析出的区域尺寸无效: w={}, h={}, 使用默认区域",
                        customArea.getWidth(), customArea.getHeight());
                    return createDefaultCustomizableArea();
                }

                LOGGER.info("成功解析定制区域: {} -> x={}, y={}, w={}, h={}",
                    area.getName(), customArea.getX(), customArea.getY(),
                    customArea.getWidth(), customArea.getHeight());

            } else {
                LOGGER.warn("bounds为空，使用默认区域");
                return createDefaultCustomizableArea();
            }

        } catch (Exception e) {
            LOGGER.error("解析bounds失败: {}", area.getBounds(), e);
            return createDefaultCustomizableArea();
        }

        return customArea;
    }

    /**
     * 解析SVG路径格式的bounds
     * 格式示例: "5 319.05625 L 199.5 319.05625 L 199.81,339.239"
     */
    private CustomizableArea parseSVGPathBounds(String bounds, String areaName) {
        CustomizableArea customArea = new CustomizableArea();

        try {
            // 提取所有数字坐标点
            String cleanPath = bounds.replaceAll("[LMZ]", " ").trim();
            String[] parts = cleanPath.split("[\\s,]+");
            List<Double> xCoords = new ArrayList<>();
            List<Double> yCoords = new ArrayList<>();

            // 按x,y对提取坐标
            for (int i = 0; i < parts.length - 1; i += 2) {
                if (StringUtils.hasText(parts[i]) && StringUtils.hasText(parts[i + 1])) {
                    try {
                        double x = Double.parseDouble(parts[i]);
                        double y = Double.parseDouble(parts[i + 1]);
                        xCoords.add(x);
                        yCoords.add(y);
                    } catch (NumberFormatException e) {
                        LOGGER.debug("跳过无效坐标: {} {}", parts[i], parts[i + 1]);
                    }
                }
            }

            if (xCoords.size() >= 2 && yCoords.size() >= 2) {
                // 计算边界框
                double minX = xCoords.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double maxX = xCoords.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                double minY = yCoords.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double maxY = yCoords.stream().mapToDouble(Double::doubleValue).max().orElse(0);

                customArea.setX((int) Math.round(minX));
                customArea.setY((int) Math.round(minY));
                customArea.setWidth((int) Math.round(maxX - minX));
                customArea.setHeight((int) Math.round(maxY - minY));

                LOGGER.info("SVG路径解析成功: {} -> 坐标点数={}, X范围=[{},{}], Y范围=[{},{}], 边界框=({},{},{},{})",
                    areaName, xCoords.size(), minX, maxX, minY, maxY,
                    customArea.getX(), customArea.getY(), customArea.getWidth(), customArea.getHeight());
            } else {
                throw new IllegalArgumentException("SVG路径坐标点不足: " + xCoords.size() + " 个坐标对");
            }

        } catch (Exception e) {
            LOGGER.error("解析SVG路径失败: {}", bounds, e);
            throw e;
        }

        return customArea;
    }

    /**
     * 将SVG路径数据解析为Java Shape对象
     * 支持SVG路径命令：M(moveTo), L(lineTo), A(arcTo), Z(closePath)
     * 增强版本，支持矩形和圆形路径
     */
    private Shape parseSVGPathToShape(String pathData) {
        if (!StringUtils.hasText(pathData)) {
            return null;
        }

        try {
            // 首先检查是否为圆形路径
            Shape circleShape = parseCirclePath(pathData);
            if (circleShape != null) {
                LOGGER.info("✅ 识别为圆形路径: {}", pathData);
                return circleShape;
            }

            // 解析一般路径
            GeneralPath path = new GeneralPath();

            // 清理路径数据，移除多余的空格
            String cleanPath = pathData.trim().replaceAll("\\s+", " ");

            // 分割路径命令，支持更多命令类型
            String[] commands = cleanPath.split("(?=[MLAZmlaz])");

            for (String command : commands) {
                command = command.trim();
                if (command.isEmpty()) continue;

                char cmdType = command.charAt(0);
                String coords = command.substring(1).trim();

                switch (cmdType) {
                    case 'M': // MoveTo (绝对坐标)
                    case 'm': // MoveTo (相对坐标)
                        String[] moveCoords = coords.split("[\\s,]+");
                        if (moveCoords.length >= 2) {
                            float x = Float.parseFloat(moveCoords[0]);
                            float y = Float.parseFloat(moveCoords[1]);
                            path.moveTo(x, y);
                            LOGGER.debug("SVG MoveTo: ({}, {})", x, y);
                        }
                        break;

                    case 'L': // LineTo (绝对坐标)
                    case 'l': // LineTo (相对坐标)
                        String[] lineCoords = coords.split("[\\s,]+");
                        if (lineCoords.length >= 2) {
                            float x = Float.parseFloat(lineCoords[0]);
                            float y = Float.parseFloat(lineCoords[1]);
                            path.lineTo(x, y);
                            LOGGER.debug("SVG LineTo: ({}, {})", x, y);
                        }
                        break;

                    case 'A': // ArcTo (绝对坐标)
                    case 'a': // ArcTo (相对坐标)
                        // 解析弧线命令：A rx ry x-axis-rotation large-arc-flag sweep-flag x y
                        parseArcCommand(path, coords, cmdType == 'A');
                        break;

                    case 'Z': // ClosePath
                    case 'z':
                        path.closePath();
                        LOGGER.debug("SVG ClosePath");
                        break;

                    default:
                        LOGGER.warn("不支持的SVG路径命令: {}", cmdType);
                        break;
                }
            }

            LOGGER.info("✅ SVG路径解析成功: {} -> Shape边界={}", pathData, path.getBounds2D());
            return path;

        } catch (Exception e) {
            LOGGER.error("SVG路径解析失败: {}", pathData, e);
            return null;
        }
    }

    /**
     * 解析圆形路径
     * 支持标准圆形SVG路径格式
     */
    private Shape parseCirclePath(String pathData) {
        try {
            // 检查是否包含弧线命令A，这通常表示圆形
            if (pathData.contains("A ") || pathData.contains("a ")) {
                // 尝试解析圆形路径模式
                // 标准圆形路径格式: M cx cy m -r 0 A r r 0 1 1 r 0 A r r 0 1 1 -r 0 Z
                String[] parts = pathData.split("[\\s,]+");

                if (parts.length >= 4) {
                    // 查找M命令后的坐标作为圆心
                    float centerX = 0, centerY = 0, radius = 0;

                    for (int i = 0; i < parts.length - 1; i++) {
                        if ("M".equals(parts[i]) && i + 2 < parts.length) {
                            centerX = Float.parseFloat(parts[i + 1]);
                            centerY = Float.parseFloat(parts[i + 2]);
                        } else if ("A".equals(parts[i]) && i + 1 < parts.length) {
                            radius = Float.parseFloat(parts[i + 1]);
                            break;
                        }
                    }

                    if (radius > 0) {
                        // 创建圆形Shape
                        Ellipse2D.Float circle = new Ellipse2D.Float(
                            centerX - radius, centerY - radius,
                            radius * 2, radius * 2
                        );
                        LOGGER.info("✅ 解析圆形成功: 圆心=({}, {}), 半径={}", centerX, centerY, radius);
                        return circle;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("圆形路径解析失败，将作为一般路径处理: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析弧线命令
     */
    private void parseArcCommand(GeneralPath path, String coords, boolean absolute) {
        try {
            String[] arcParams = coords.split("[\\s,]+");
            if (arcParams.length >= 7) {
                float rx = Float.parseFloat(arcParams[0]);
                float ry = Float.parseFloat(arcParams[1]);
                float xAxisRotation = Float.parseFloat(arcParams[2]);
                boolean largeArcFlag = "1".equals(arcParams[3]);
                boolean sweepFlag = "1".equals(arcParams[4]);
                float x = Float.parseFloat(arcParams[5]);
                float y = Float.parseFloat(arcParams[6]);

                // 简化处理：对于圆形，直接连线到目标点
                // 完整的弧线实现较复杂，这里提供基础支持
                path.lineTo(x, y);
                LOGGER.debug("SVG ArcTo: 简化为LineTo({}, {})", x, y);
            }
        } catch (Exception e) {
            LOGGER.warn("弧线命令解析失败: {}", coords, e);
        }
    }

    /**
     * 解析简单格式的bounds
     * 格式: "x,y,width,height" (左上角坐标和尺寸)
     */
    private CustomizableArea parseSimpleBounds(String bounds, String areaName) {
        CustomizableArea customArea = new CustomizableArea();

        String[] parts = bounds.split(",");
        customArea.setX((int) Double.parseDouble(parts[0]));
        customArea.setY((int) Double.parseDouble(parts[1]));
        customArea.setWidth((int) Double.parseDouble(parts[2]));
        customArea.setHeight((int) Double.parseDouble(parts[3]));

        LOGGER.info("🔧 边界框解析: {} -> 原始bounds={} -> x={}, y={}, w={}, h={}",
            areaName, bounds, customArea.getX(), customArea.getY(),
            customArea.getWidth(), customArea.getHeight());

        return customArea;
    }

    /**
     * 创建裁剪形状
     * 优先使用SVG路径，如果没有则使用矩形
     */
    private Shape createClipShape(CustomizableArea customArea, int x, int y, int width, int height) {
        // 优先尝试使用SVG路径
        if (StringUtils.hasText(customArea.getPathData())) {
            Shape svgShape = parseSVGPathToShape(customArea.getPathData());
            if (svgShape != null) {
                LOGGER.info("🎯 创建SVG裁剪形状成功: 区域={}, 边界={}",
                    customArea.getName(), svgShape.getBounds2D());
                return svgShape;
            } else {
                LOGGER.warn("⚠️ SVG路径解析失败，回退到矩形裁剪: 区域={}", customArea.getName());
            }
        }

        // 回退到矩形裁剪
        Rectangle clipRect = new Rectangle(x, y, width, height);
        LOGGER.debug("📐 创建矩形裁剪形状: x={}, y={}, w={}, h={}", x, y, width, height);
        return clipRect;
    }

    /**
     * 创建精确的裁剪形状（用于蒙版）
     */
    private Shape createPreciseClipShape(CustomizableArea customArea) {
        if (StringUtils.hasText(customArea.getPathData())) {
            Shape shape = parseSVGPathToShape(customArea.getPathData());
            if (shape != null) {
                LOGGER.info("🎯 创建精确裁剪形状: 区域={}, 类型={}",
                    customArea.getName(), getShapeType(shape));
                return shape;
            }
        }

        // 回退到矩形
        return new Rectangle(customArea.getX(), customArea.getY(),
                           customArea.getWidth(), customArea.getHeight());
    }

    /**
     * 应用形状蒙版
     */
    private BufferedImage applyShapeMask(BufferedImage sourceImage, Shape maskShape,
                                       int offsetX, int offsetY, int targetWidth, int targetHeight) {
        BufferedImage maskedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = maskedImage.createGraphics();

        try {
            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // 创建相对于目标区域的裁剪形状
            Shape relativeShape = createRelativeShape(maskShape, offsetX, offsetY);
            g2d.setClip(relativeShape);

            // 绘制源图像
            g2d.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);

            LOGGER.debug("✅ 形状蒙版应用完成: 蒙版类型={}, 目标尺寸={}x{}",
                getShapeType(maskShape), targetWidth, targetHeight);

        } finally {
            g2d.dispose();
        }

        return maskedImage;
    }

    /**
     * 创建相对形状（将绝对坐标转换为相对坐标）
     */
    private Shape createRelativeShape(Shape absoluteShape, int offsetX, int offsetY) {
        AffineTransform transform = AffineTransform.getTranslateInstance(-offsetX, -offsetY);
        return transform.createTransformedShape(absoluteShape);
    }

    /**
     * 获取形状类型描述
     */
    private String getShapeType(Shape shape) {
        if (shape instanceof Ellipse2D) {
            return "圆形";
        } else if (shape instanceof Rectangle) {
            return "矩形";
        } else if (shape instanceof GeneralPath) {
            return "自定义路径";
        } else {
            return "未知形状";
        }
    }

    /**
     * 缩放图片为正方形（用于圆形区域）
     */
    private BufferedImage scaleImageToFillSquare(BufferedImage originalImage, int size) {
        if (originalImage == null || size <= 0) {
            return originalImage;
        }

        BufferedImage scaledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        try {
            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 计算缩放参数以填满正方形
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            double scale = Math.max((double) size / originalWidth, (double) size / originalHeight);
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // 居中绘制
            int x = (size - scaledWidth) / 2;
            int y = (size - scaledHeight) / 2;

            g2d.drawImage(originalImage, x, y, scaledWidth, scaledHeight, null);

            LOGGER.debug("✅ 正方形缩放完成: 原始={}x{}, 目标={}x{}, 缩放比={}",
                originalWidth, originalHeight, size, size, scale);

        } finally {
            g2d.dispose();
        }

        return scaledImage;
    }

    /**
     * 坐标转换工具类
     * 将相对坐标(0-1)转换为定制区域内的绝对坐标
     */
    private static class CoordinateTransformer {
        private final CustomizableArea customArea;

        public CoordinateTransformer(CustomizableArea customArea) {
            this.customArea = customArea;
        }

        /**
         * 转换X坐标
         */
        public int transformX(double relativeX) {
            relativeX = Math.max(0, Math.min(1, relativeX));
            return (int) (relativeX * customArea.getWidth());
        }

        /**
         * 转换Y坐标
         */
        public int transformY(double relativeY) {
            relativeY = Math.max(0, Math.min(1, relativeY));
            return (int) (relativeY * customArea.getHeight());
        }

        /**
         * 转换宽度
         */
        public int transformWidth(double relativeWidth) {
            relativeWidth = Math.max(0.01, Math.min(1, relativeWidth));
            return (int) (relativeWidth * customArea.getWidth());
        }

        /**
         * 转换高度
         */
        public int transformHeight(double relativeHeight) {
            relativeHeight = Math.max(0.01, Math.min(1, relativeHeight));
            return (int) (relativeHeight * customArea.getHeight());
        }

        /**
         * 转换文字Y坐标（考虑基线）
         */
        public int transformTextY(double relativeY, FontMetrics fm) {
            relativeY = Math.max(0, Math.min(1, relativeY));
            // 文字的Y坐标是基线位置，需要加上ascent
            int baseY = (int) (relativeY * customArea.getHeight());
            return baseY + fm.getAscent();
        }

        /**
         * 边界检查并调整坐标
         */
        public Rectangle adjustBounds(int x, int y, int width, int height) {
            // 确保不超出定制区域边界
            x = Math.max(0, Math.min(x, customArea.getWidth()));
            y = Math.max(0, Math.min(y, customArea.getHeight()));

            // 调整尺寸以适应剩余空间
            width = Math.min(width, customArea.getWidth() - x);
            height = Math.min(height, customArea.getHeight() - y);

            // 确保尺寸为正数
            width = Math.max(1, width);
            height = Math.max(1, height);

            return new Rectangle(x, y, width, height);
        }

        /**
         * 获取定制区域信息
         */
        public CustomizableArea getCustomArea() {
            return customArea;
        }
    }

    /**
     * 解析单个面配置
     */
    private TemplateFaceConfig parseSingleFaceConfig(JsonNode faceNode) {
        TemplateFaceConfig config = new TemplateFaceConfig();

        config.setFaceName(faceNode.has("name") ? faceNode.get("name").asText() : "正面");
        config.setBaseImageUrl(faceNode.has("baseImage") ? faceNode.get("baseImage").asText() : "");

        // 解析定制区域
        if (faceNode.has("customizableArea")) {
            JsonNode areaNode = faceNode.get("customizableArea");
            CustomizableArea area = new CustomizableArea();
            area.setX(areaNode.has("x") ? areaNode.get("x").asInt() : 0);
            area.setY(areaNode.has("y") ? areaNode.get("y").asInt() : 0);
            area.setWidth(areaNode.has("width") ? areaNode.get("width").asInt() : 300);
            area.setHeight(areaNode.has("height") ? areaNode.get("height").asInt() : 300);
            config.setCustomizableArea(area);
        } else {
            config.setCustomizableArea(createDefaultCustomizableArea());
        }

        return config;
    }

    /**
     * 创建默认面配置
     */
    private TemplateFaceConfig createDefaultFaceConfig(PmsDiyTemplate template) {
        TemplateFaceConfig config = new TemplateFaceConfig();
        config.setFaceName("正面");
        // 使用默认的模板图片URL，或者从其他地方获取
        config.setBaseImageUrl(getDefaultTemplateImageUrl(template));
        config.setCustomizableArea(createDefaultCustomizableArea());
        return config;
    }

    /**
     * 获取默认模板图片URL（已废弃，应使用数据库中的真实URL）
     */
    @Deprecated
    private String getDefaultTemplateImageUrl(PmsDiyTemplate template) {
        LOGGER.warn("使用了废弃的getDefaultTemplateImageUrl方法，应该从数据库获取真实图片URL");

        // 尝试从模板的第一个面获取图片URL
        try {
            List<PmsDiyTemplateSurface> surfaces = getTemplateSurfaces(template.getId());
            if (surfaces != null && !surfaces.isEmpty()) {
                String imageUrl = surfaces.get(0).getExampleImage();
                if (StringUtils.hasText(imageUrl)) {
                    LOGGER.info("从数据库获取到模板图片URL: {}", imageUrl);
                    return imageUrl;
                }
            }
        } catch (Exception e) {
            LOGGER.error("从数据库获取模板图片URL失败", e);
        }

        // 最后的降级方案：返回null，让调用方处理
        LOGGER.error("无法获取模板图片URL，模板ID: {}", template.getId());
        return null;
    }

    /**
     * 创建默认定制区域
     */
    private CustomizableArea createDefaultCustomizableArea() {
        CustomizableArea area = new CustomizableArea();
        area.setX(100);
        area.setY(100);
        area.setWidth(300);
        area.setHeight(300);
        return area;
    }

    /**
     * 加载模板底图
     */
    private BufferedImage loadTemplateBaseImage(String imageUrl) {
        try {
            if (StringUtils.isEmpty(imageUrl)) {
                LOGGER.warn("图片URL为空");
                return null;
            }

            // 验证URL格式
            if (!isValidImageUrl(imageUrl)) {
                LOGGER.error("无效的图片URL: {}", imageUrl);
                return null;
            }

            // 添加缓存逻辑
            String cacheKey = "template_image_" + imageUrl.hashCode();
            BufferedImage cachedImage = imageCache.get(cacheKey);
            if (cachedImage != null) {
                LOGGER.debug("从缓存加载图片: {}", imageUrl);
                return deepCopyImage(cachedImage);
            }

            // 从URL加载图片
            LOGGER.info("开始加载图片: {}", imageUrl);
            BufferedImage image = loadImageFromUrl(imageUrl);
            if (image != null) {
                imageCache.put(cacheKey, image);
                LOGGER.info("图片加载成功，尺寸: {}x{}", image.getWidth(), image.getHeight());
                return deepCopyImage(image);
            } else {
                LOGGER.error("图片加载失败: {}", imageUrl);
            }

        } catch (Exception e) {
            LOGGER.error("加载模板底图异常: {}", imageUrl, e);
        }

        return null;
    }

    /**
     * 验证图片URL是否有效
     */
    private boolean isValidImageUrl(String imageUrl) {
        if (StringUtils.isEmpty(imageUrl)) {
            return false;
        }

        // 检查URL格式
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return false;
        }

        // 检查是否是图片文件
        String lowerUrl = imageUrl.toLowerCase();
        return lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") ||
               lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") ||
               lowerUrl.endsWith(".bmp") || lowerUrl.endsWith(".webp");
    }

    /**
     * 深拷贝图片
     */
    private BufferedImage deepCopyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    /**
     * 渲染设计元素到图片上
     */
    private BufferedImage renderDesignElements(BufferedImage baseImage, JsonNode designData) {
        BufferedImage resultImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resultImage.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 绘制基础图片
        g2d.drawImage(baseImage, 0, 0, null);

        try {
            // 解析并渲染设计元素
            if (designData.has("elements")) {
                JsonNode elements = designData.get("elements");
                for (JsonNode element : elements) {
                    renderElement(g2d, element, baseImage.getWidth(), baseImage.getHeight());
                }
            }

            // 如果有faces数据，渲染每个面的元素
            if (designData.has("faces")) {
                JsonNode faces = designData.get("faces");
                for (JsonNode face : faces) {
                    if (face.has("elements")) {
                        JsonNode faceElements = face.get("elements");
                        for (JsonNode element : faceElements) {
                            renderElement(g2d, element, baseImage.getWidth(), baseImage.getHeight());
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("渲染设计元素失败", e);
        }

        g2d.dispose();
        return resultImage;
    }

    /**
     * 渲染单个设计元素
     */
    private void renderElement(Graphics2D g2d, JsonNode element, int canvasWidth, int canvasHeight) {
        try {
            String type = element.has("type") ? element.get("type").asText() : "";

            switch (type) {
                case "text":
                    renderTextElement(g2d, element, canvasWidth, canvasHeight);
                    break;
                case "image":
                    renderImageElement(g2d, element, canvasWidth, canvasHeight);
                    break;
                case "shape":
                    renderShapeElement(g2d, element, canvasWidth, canvasHeight);
                    break;
                default:
                    LOGGER.debug("未知的元素类型: {}", type);
            }
        } catch (Exception e) {
            LOGGER.error("渲染元素失败", e);
        }
    }

    /**
     * 渲染文本元素
     */
    private void renderTextElement(Graphics2D g2d, JsonNode element, int canvasWidth, int canvasHeight) {
        try {
            String text = element.has("content") ? element.get("content").asText() : "";
            if (StringUtils.isEmpty(text)) return;

            // 获取位置信息
            int x = element.has("x") ? element.get("x").asInt() : 0;
            int y = element.has("y") ? element.get("y").asInt() : 0;

            // 获取字体信息
            String fontFamily = element.has("fontFamily") ? element.get("fontFamily").asText() : "Arial";
            int fontSize = element.has("fontSize") ? element.get("fontSize").asInt() : 16;
            boolean bold = element.has("bold") && element.get("bold").asBoolean();
            boolean italic = element.has("italic") && element.get("italic").asBoolean();

            // 获取颜色信息
            String colorStr = element.has("color") ? element.get("color").asText() : "#000000";
            Color color = parseColor(colorStr);

            // 设置字体
            int style = Font.PLAIN;
            if (bold) style |= Font.BOLD;
            if (italic) style |= Font.ITALIC;
            Font font = new Font(fontFamily, style, fontSize);

            g2d.setFont(font);
            g2d.setColor(color);
            g2d.drawString(text, x, y);

        } catch (Exception e) {
            LOGGER.error("渲染文本元素失败", e);
        }
    }

    /**
     * 渲染图片元素
     */
    private void renderImageElement(Graphics2D g2d, JsonNode element, int canvasWidth, int canvasHeight) {
        try {
            String imageUrl = element.has("src") ? element.get("src").asText() : "";
            if (StringUtils.isEmpty(imageUrl)) return;

            // 获取位置和尺寸信息
            int x = element.has("x") ? element.get("x").asInt() : 0;
            int y = element.has("y") ? element.get("y").asInt() : 0;
            int width = element.has("width") ? element.get("width").asInt() : 100;
            int height = element.has("height") ? element.get("height").asInt() : 100;

            // 加载并绘制图片
            BufferedImage image = loadImageFromUrl(imageUrl);
            if (image != null) {
                g2d.drawImage(image, x, y, width, height, null);
            }

        } catch (Exception e) {
            LOGGER.error("渲染图片元素失败", e);
        }
    }

    /**
     * 渲染形状元素
     */
    private void renderShapeElement(Graphics2D g2d, JsonNode element, int canvasWidth, int canvasHeight) {
        try {
            String shapeType = element.has("shapeType") ? element.get("shapeType").asText() : "rectangle";

            // 获取位置和尺寸信息
            int x = element.has("x") ? element.get("x").asInt() : 0;
            int y = element.has("y") ? element.get("y").asInt() : 0;
            int width = element.has("width") ? element.get("width").asInt() : 100;
            int height = element.has("height") ? element.get("height").asInt() : 100;

            // 获取颜色信息
            String fillColorStr = element.has("fillColor") ? element.get("fillColor").asText() : "#FFFFFF";
            String strokeColorStr = element.has("strokeColor") ? element.get("strokeColor").asText() : "#000000";
            Color fillColor = parseColor(fillColorStr);
            Color strokeColor = parseColor(strokeColorStr);

            // 绘制形状
            switch (shapeType) {
                case "rectangle":
                    g2d.setColor(fillColor);
                    g2d.fillRect(x, y, width, height);
                    g2d.setColor(strokeColor);
                    g2d.drawRect(x, y, width, height);
                    break;
                case "circle":
                    g2d.setColor(fillColor);
                    g2d.fillOval(x, y, width, height);
                    g2d.setColor(strokeColor);
                    g2d.drawOval(x, y, width, height);
                    break;
            }

        } catch (Exception e) {
            LOGGER.error("渲染形状元素失败", e);
        }
    }

    /**
     * 解析颜色字符串
     */
    private Color parseColor(String colorStr) {
        try {
            if (StringUtils.isEmpty(colorStr)) {
                return Color.BLACK;
            }

            // 移除#号
            if (colorStr.startsWith("#")) {
                colorStr = colorStr.substring(1);
            }

            // 解析RGB值
            if (colorStr.length() == 6) {
                int r = Integer.parseInt(colorStr.substring(0, 2), 16);
                int g = Integer.parseInt(colorStr.substring(2, 4), 16);
                int b = Integer.parseInt(colorStr.substring(4, 6), 16);
                return new Color(r, g, b);
            }

        } catch (Exception e) {
            LOGGER.warn("解析颜色失败: {}", colorStr, e);
        }

        return Color.BLACK;
    }

    /**
     * 验证文字是否成功绘制（通过检查像素变化）
     */
    private boolean verifyTextRendered(BufferedImage canvas, int x, int y, int width, int height, Color textColor) {
        try {
            // 检查文字区域内是否有非背景色的像素
            int startX = Math.max(0, x - 5);
            int startY = Math.max(0, y - height - 5);
            int endX = Math.min(canvas.getWidth(), x + width + 5);
            int endY = Math.min(canvas.getHeight(), y + 5);

            int textColorRGB = textColor.getRGB() & 0xFFFFFF; // 忽略alpha通道

            for (int py = startY; py < endY; py++) {
                for (int px = startX; px < endX; px++) {
                    int pixelColor = canvas.getRGB(px, py) & 0xFFFFFF;
                    if (pixelColor == textColorRGB) {
                        return true; // 找到了文字颜色的像素
                    }
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.warn("验证文字渲染时出错", e);
            return false;
        }
    }

    /**
     * 生成用户设计图（增强调试版本）
     * 优先使用前端传递的剪裁后图片，如果没有则重新生成
     */
    private BufferedImage generateUserDesignImage(JsonNode faceData, CustomizableArea customArea) {
        LOGGER.info("🎨 开始生成用户设计图，画布尺寸: {}x{}", customArea.getWidth(), customArea.getHeight());

        // 1. 优先检查是否有前端传递的剪裁后图片
        if (faceData != null && faceData.has("canvasImagePath") && faceData.has("isClipped")) {
            String canvasImagePath = faceData.get("canvasImagePath").asText();
            boolean isClipped = faceData.get("isClipped").asBoolean();

            if (canvasImagePath != null && !canvasImagePath.trim().isEmpty() && isClipped) {
                LOGGER.info("🎯 发现前端剪裁后的图片，直接使用: {}", canvasImagePath);
                try {
                    BufferedImage clippedImage = loadImageFromUrl(canvasImagePath);
                    if (clippedImage != null) {
                        // 确保图片尺寸与定制区域一致
                        if (clippedImage.getWidth() == customArea.getWidth() &&
                            clippedImage.getHeight() == customArea.getHeight()) {
                            LOGGER.info("✅ 前端剪裁图片尺寸匹配，直接使用: {}x{}",
                                clippedImage.getWidth(), clippedImage.getHeight());
                            return clippedImage;
                        } else {
                            // 如果尺寸不匹配，缩放到定制区域尺寸
                            BufferedImage scaledImage = scaleImageToFill(clippedImage,
                                customArea.getWidth(), customArea.getHeight());
                            LOGGER.info("✅ 前端剪裁图片已缩放到定制区域尺寸: {}x{} -> {}x{}",
                                clippedImage.getWidth(), clippedImage.getHeight(),
                                scaledImage.getWidth(), scaledImage.getHeight());
                            return scaledImage;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("⚠️ 加载前端剪裁图片失败，回退到重新生成: {}", e.getMessage());
                }
            }
        }

        // 2. 如果没有前端剪裁图片，按原有逻辑重新生成
        LOGGER.info("🔄 没有前端剪裁图片，开始重新生成用户设计图");

        // 创建用户设计画布，尺寸与定制区域一致
        BufferedImage designCanvas = new BufferedImage(
            customArea.getWidth(),
            customArea.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = designCanvas.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        try {
            // 创建完全透明的背景
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, customArea.getWidth(), customArea.getHeight());
            g2d.setComposite(AlphaComposite.SrcOver);

            LOGGER.info("✅ 创建透明画布完成: {}x{}", customArea.getWidth(), customArea.getHeight());

            // 1. 首先处理AI生成的背景图片
            if (faceData != null && faceData.has("aiGeneratedImageUrl")) {
                String aiImageUrl = faceData.get("aiGeneratedImageUrl").asText();
                if (aiImageUrl != null && !aiImageUrl.trim().isEmpty()) {
                    LOGGER.info("🤖 发现AI生成图片，开始渲染: {}", aiImageUrl);
                    try {
                        BufferedImage aiImage = loadImageFromUrl(aiImageUrl);
                        if (aiImage != null) {
                            // 使用优化的缩放方法，确保完全填满DIY区域
                            BufferedImage scaledAiImage;
                            if (StringUtils.hasText(customArea.getPathData())) {
                                // 对于SVG形状，使用专门的优化缩放
                                scaledAiImage = scaleImageForSVGShape(aiImage, customArea);
                                LOGGER.info("🎯 使用SVG形状优化缩放AI图片");
                            } else {
                                // 对于矩形区域，使用填充缩放
                                scaledAiImage = scaleImageToFill(aiImage, customArea.getWidth(), customArea.getHeight());
                                LOGGER.info("📐 使用填充缩放AI图片");
                            }

                            if (scaledAiImage != null) {
                                g2d.drawImage(scaledAiImage, 0, 0, null);
                                LOGGER.info("✅ AI生成图片渲染成功: 原始={}x{}, 缩放后={}x{}",
                                    aiImage.getWidth(), aiImage.getHeight(),
                                    scaledAiImage.getWidth(), scaledAiImage.getHeight());
                            }
                        } else {
                            LOGGER.warn("⚠️ AI生成图片加载失败: {}", aiImageUrl);
                        }
                    } catch (Exception e) {
                        LOGGER.error("❌ AI生成图片处理失败: {}", aiImageUrl, e);
                    }
                } else {
                    LOGGER.debug("AI生成图片URL为空，跳过处理");
                }
            }

            // 2. 然后渲染用户设计元素（叠加在AI图片之上）
            if (faceData != null && faceData.has("elements")) {
                JsonNode elements = faceData.get("elements");
                LOGGER.info("🎯 开始渲染 {} 个设计元素", elements.size());

                int elementCount = 0;
                int successCount = 0;
                for (JsonNode element : elements) {
                    try {
                        String elementType = element.has("type") ? element.get("type").asText() : "unknown";
                        String elementId = element.has("id") ? element.get("id").asText() : "element_" + elementCount;

                        LOGGER.info("  🔧 渲染元素 {}: type={}, id={}", elementCount, elementType, elementId);
                        renderElementToDesignCanvas(g2d, element, customArea);
                        successCount++;
                        LOGGER.info("  ✅ 元素 {} 渲染成功", elementCount);

                    } catch (Exception e) {
                        LOGGER.error("  ❌ 元素 {} 渲染失败", elementCount, e);
                    }
                    elementCount++;
                }
                LOGGER.info("✅ 设计元素渲染完成: 总数={}, 成功={}, 失败={}",
                    elementCount, successCount, elementCount - successCount);
            } else {
                LOGGER.warn("⚠️ 没有找到设计元素或faceData为空");
            }

            // 检查画布是否有内容
            boolean hasContent = hasVisibleContent(designCanvas);
            LOGGER.info("🔍 用户设计图内容检查: 有可见内容={}", hasContent);

        } catch (Exception e) {
            LOGGER.error("❌ 生成用户设计图失败", e);
        } finally {
            g2d.dispose();
        }

        return designCanvas;
    }

    /**
     * 将用户设计图合成到底图的定制区域（增强版本）
     * 支持精确的形状裁剪、智能缩放、滤镜效果和蒙版模式
     */
    private BufferedImage compositeImageToCustomArea(BufferedImage baseImage, BufferedImage userDesignImage, CustomizableArea customArea) {
        try {
            // 检查是否使用蒙版模式
            if (customArea.getMaskImageUrl() != null && !customArea.getMaskImageUrl().isEmpty()) {
                LOGGER.info("🎭 使用蒙版模式合成");
                return compositeWithMask(baseImage, userDesignImage, customArea);
            } else {
                LOGGER.info("🔲 使用传统模式合成(矩形/圆形)");
                return compositeWithShape(baseImage, userDesignImage, customArea);
            }
        } catch (Exception e) {
            LOGGER.error("图像合成失败，返回原始底图", e);
            return baseImage;
        }
    }

    /**
     * 使用蒙版进行图像合成
     */
    private BufferedImage compositeWithMask(BufferedImage baseImage, BufferedImage userDesignImage, CustomizableArea customArea) {
        try {
            LOGGER.info("🎨 开始蒙版模式合成 - 蒙版URL: {}", customArea.getMaskImageUrl());

            // 1. 加载蒙版图片
            BufferedImage maskImage = ImageIO.read(new URL(customArea.getMaskImageUrl()));
            LOGGER.info("✅ 蒙版图片加载成功: {}x{}", maskImage.getWidth(), maskImage.getHeight());

            // 2. 验证蒙版图片
            if (!MaskBlendUtil.isValidMask(maskImage)) {
                LOGGER.warn("⚠️ 蒙版图片格式不正确，降级到传统模式");
                return compositeWithShape(baseImage, userDesignImage, customArea);
            }

            // 3. 智能缩放用户设计图
            // 蒙版模式下,需要将用户设计图缩放到底图尺寸,但保持在定制区域内居中
            LOGGER.info("🔄 开始智能缩放用户设计图...");
            BufferedImage scaledDesignImage = scaleDesignImageForMask(
                userDesignImage,
                baseImage.getWidth(),
                baseImage.getHeight(),
                customArea
            );
            LOGGER.info("✅ 用户设计图智能缩放完成: 原始={}x{}, 缩放后={}x{}",
                userDesignImage.getWidth(), userDesignImage.getHeight(),
                scaledDesignImage.getWidth(), scaledDesignImage.getHeight());

            // 4. 使用蒙版混合
            BufferedImage result = MaskBlendUtil.blendWithMask(
                baseImage,
                scaledDesignImage,
                maskImage
            );

            LOGGER.info("✅ 蒙版模式合成完成");
            return result;

        } catch (Exception e) {
            LOGGER.error("蒙版模式合成失败，降级到传统模式", e);
            return compositeWithShape(baseImage, userDesignImage, customArea);
        }
    }

    /**
     * 为蒙版模式缩放用户设计图
     * 将用户设计图缩放到底图尺寸,但保持在定制区域内居中填充
     *
     * 关键: 创建白色画布,因为正片叠底时白色区域会保持底图不变
     */
    private BufferedImage scaleDesignImageForMask(
        BufferedImage userDesignImage,
        int baseWidth,
        int baseHeight,
        CustomizableArea customArea
    ) {
        // 创建与底图相同尺寸的白色画布(不是透明画布!)
        // 因为正片叠底时: (base * 255) / 255 = base (保持底图)
        //              (base * 0) / 255 = 0 (变成黑色)
        BufferedImage result = new BufferedImage(
            baseWidth,
            baseHeight,
            BufferedImage.TYPE_INT_RGB  // RGB 模式,默认黑色
        );

        Graphics2D g2d = result.createGraphics();

        // 填充白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, baseWidth, baseHeight);

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 获取定制区域的位置和尺寸
        int areaX = customArea.getX();
        int areaY = customArea.getY();
        int areaWidth = customArea.getWidth();
        int areaHeight = customArea.getHeight();

        // 计算缩放比例,使用户设计图完全填充定制区域
        double scaleX = (double) areaWidth / userDesignImage.getWidth();
        double scaleY = (double) areaHeight / userDesignImage.getHeight();
        double scale = Math.max(scaleX, scaleY); // 使用较大的缩放比例以完全覆盖

        int scaledWidth = (int) Math.round(userDesignImage.getWidth() * scale);
        int scaledHeight = (int) Math.round(userDesignImage.getHeight() * scale);

        // 计算居中位置
        int drawX = areaX + (areaWidth - scaledWidth) / 2;
        int drawY = areaY + (areaHeight - scaledHeight) / 2;

        LOGGER.debug("蒙版模式缩放参数: 区域={}x{} at ({},{}), 缩放后={}x{} at ({},{}), 缩放比例={}",
            areaWidth, areaHeight, areaX, areaY,
            scaledWidth, scaledHeight, drawX, drawY, scale);

        // 绘制缩放后的用户设计图到指定位置
        g2d.drawImage(userDesignImage, drawX, drawY, scaledWidth, scaledHeight, null);
        g2d.dispose();

        LOGGER.info("✅ 设计图已绘制到白色画布: 画布尺寸={}x{}, 设计图位置=({},{}), 设计图尺寸={}x{}",
            baseWidth, baseHeight, drawX, drawY, scaledWidth, scaledHeight);

        return result;
    }

    /**
     * 使用形状进行图像合成(传统模式)
     */
    private BufferedImage compositeWithShape(BufferedImage baseImage, BufferedImage userDesignImage, CustomizableArea customArea) {
        try {
            // 1. 验证定制区域的有效性
            int targetX = customArea.getX();
            int targetY = customArea.getY();
            int targetWidth = customArea.getWidth();
            int targetHeight = customArea.getHeight();

            LOGGER.debug("定制区域参数: x={}, y={}, w={}, h={}", targetX, targetY, targetWidth, targetHeight);

            // 2. 边界检查
            if (targetX < 0 || targetY < 0 || targetWidth <= 0 || targetHeight <= 0) {
                LOGGER.warn("定制区域参数无效，跳过用户设计图合成");
                return baseImage;
            }

            // 3. 确保合成区域在底图范围内
            int clippedX = Math.max(0, Math.min(targetX, baseImage.getWidth()));
            int clippedY = Math.max(0, Math.min(targetY, baseImage.getHeight()));
            int clippedWidth = Math.min(targetWidth, baseImage.getWidth() - clippedX);
            int clippedHeight = Math.min(targetHeight, baseImage.getHeight() - clippedY);

            if (clippedWidth <= 0 || clippedHeight <= 0) {
                LOGGER.warn("定制区域完全超出底图范围，跳过用户设计图合成");
                return baseImage;
            }

            // 4. 检查用户设计图是否有实际内容
            if (userDesignImage == null || !hasVisibleContent(userDesignImage)) {
                LOGGER.debug("用户设计图为空或无可见内容，跳过合成");
                return baseImage;
            }

            // 5. 智能缩放用户设计图以完全填充定制区域
            BufferedImage scaledDesignImage = scaleImageForCustomArea(userDesignImage, customArea);
            LOGGER.info("🔄 用户设计图智能缩放完成: 原始={}x{}, 缩放后={}x{}",
                userDesignImage.getWidth(), userDesignImage.getHeight(),
                scaledDesignImage.getWidth(), scaledDesignImage.getHeight());

            // 6. 创建精确的裁剪形状
            Shape clipShape = createPreciseClipShape(customArea);

            // 7. 准备要合成的设计图（应用形状蒙版）
            BufferedImage imageToComposite;
            if (clipShape != null) {
                // 使用形状蒙版进行精确裁剪
                imageToComposite = applyShapeMask(scaledDesignImage, clipShape, clippedX, clippedY, clippedWidth, clippedHeight);
                LOGGER.info("✅ 精确形状裁剪完成: 区域={}, 形状类型={}",
                    customArea.getName(), getShapeType(clipShape));
            } else {
                // 使用矩形裁剪
                imageToComposite = cropToRectangle(scaledDesignImage, clippedWidth, clippedHeight);
                LOGGER.info("✅ 矩形裁剪完成: 区域={}", customArea.getName());
            }

            // 8. 直接合成到底图
            LOGGER.info("🔄 开始合成图像...");
            BufferedImage resultImage = new BufferedImage(
                baseImage.getWidth(),
                baseImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2d = resultImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // 绘制底图
            g2d.drawImage(baseImage, 0, 0, null);

            // 绘制设计图
            g2d.drawImage(imageToComposite, clippedX, clippedY, null);
            g2d.dispose();

            LOGGER.info("✅ 图像合成完成");

            return resultImage;

        } catch (Exception e) {
            LOGGER.error("图像合成失败，返回原始底图", e);
            return baseImage;
        }
    }

    /**
     * 裁剪图像到指定矩形尺寸
     */
    private BufferedImage cropToRectangle(BufferedImage image, int width, int height) {
        BufferedImage cropped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cropped.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

        return cropped;
    }

    /**
     * 为定制区域智能缩放图片
     * 根据区域形状选择最佳缩放策略
     */
    private BufferedImage scaleImageForCustomArea(BufferedImage originalImage, CustomizableArea customArea) {
        if (originalImage == null) {
            return null;
        }

        // 根据SVG路径判断区域类型并选择缩放策略
        if (StringUtils.hasText(customArea.getPathData())) {
            Shape shape = parseSVGPathToShape(customArea.getPathData());
            if (shape != null) {
                Rectangle2D bounds = shape.getBounds2D();

                // 对于圆形，使用正方形缩放以确保完全覆盖
                if (shape instanceof Ellipse2D) {
                    int size = Math.max((int)bounds.getWidth(), (int)bounds.getHeight());
                    return scaleImageToFillSquare(originalImage, size);
                }

                // 对于其他形状，使用边界框缩放并稍微放大以确保覆盖
                int expandedWidth = (int)(bounds.getWidth() * 1.1);
                int expandedHeight = (int)(bounds.getHeight() * 1.1);
                return scaleImageToFill(originalImage, expandedWidth, expandedHeight);
            }
        }

        // 默认使用定制区域尺寸
        return scaleImageToFill(originalImage, customArea.getWidth(), customArea.getHeight());
    }

    /**
     * 检查图像是否有可见内容（非完全透明）
     */
    private boolean hasVisibleContent(BufferedImage image) {
        if (image == null) {
            return false;
        }

        // 检查图像是否有非透明像素
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                if (alpha > 0) {
                    return true; // 找到非透明像素
                }
            }
        }
        return false;
    }

    /**
     * 缩放信息类
     */
    private static class ScaleInfo {
        int scaledWidth;
        int scaledHeight;
        double scale;

        ScaleInfo(int scaledWidth, int scaledHeight, double scale) {
            this.scaledWidth = scaledWidth;
            this.scaledHeight = scaledHeight;
            this.scale = scale;
        }
    }

    /**
     * 计算保持宽高比的缩放参数
     */
    private ScaleInfo calculateAspectRatioScale(BufferedImage sourceImage, int targetWidth, int targetHeight) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        // 计算宽高比
        double sourceAspectRatio = (double) sourceWidth / sourceHeight;
        double targetAspectRatio = (double) targetWidth / targetHeight;

        int scaledWidth, scaledHeight;
        double scale;

        if (sourceAspectRatio > targetAspectRatio) {
            // 源图更宽，以宽度为准缩放
            scale = (double) targetWidth / sourceWidth;
            scaledWidth = targetWidth;
            scaledHeight = (int) Math.round(sourceHeight * scale);
        } else {
            // 源图更高，以高度为准缩放
            scale = (double) targetHeight / sourceHeight;
            scaledWidth = (int) Math.round(sourceWidth * scale);
            scaledHeight = targetHeight;
        }

        LOGGER.debug("缩放计算: 源图({},{}) 目标({},{}) 源比例={} 目标比例={} -> 缩放比例={} 结果({},{})",
            sourceWidth, sourceHeight, targetWidth, targetHeight,
            sourceAspectRatio, targetAspectRatio, scale, scaledWidth, scaledHeight);

        return new ScaleInfo(scaledWidth, scaledHeight, scale);
    }

    /**
     * 渲染元素到设计画布
     */
    private void renderElementToDesignCanvas(Graphics2D g2d, JsonNode element, CustomizableArea customArea) {
        try {
            String type = element.has("type") ? element.get("type").asText() : "";

            switch (type) {
                case "text":
                    renderTextElementToCanvas(g2d, element, customArea);
                    break;
                case "image":
                    renderImageElementToCanvas(g2d, element, customArea);
                    break;
                case "shape":
                    renderShapeElementToCanvas(g2d, element, customArea);
                    break;
                default:
                    LOGGER.debug("未知的元素类型: {}", type);
            }
        } catch (Exception e) {
            LOGGER.error("渲染元素到设计画布失败", e);
        }
    }

    /**
     * 渲染文本元素到画布（精确版本）
     */
    private void renderTextElementToCanvas(Graphics2D g2d, JsonNode element, CustomizableArea customArea) {
        try {
            String text = element.has("content") ? element.get("content").asText() : "";
            if (StringUtils.isEmpty(text)) {
                LOGGER.warn("📝 文字元素内容为空，跳过渲染");
                return;
            }

            // 创建坐标转换器
            CoordinateTransformer transformer = new CoordinateTransformer(customArea);

            // 获取相对位置（0-1范围，相对于定制区域）
            double relativeX = element.has("x") ? element.get("x").asDouble() : 0;
            double relativeY = element.has("y") ? element.get("y").asDouble() : 0;

            // 获取字体信息
            String fontFamily = element.has("fontFamily") ? element.get("fontFamily").asText() : "Arial";
            int baseFontSize = element.has("fontSize") ? element.get("fontSize").asInt() : 16;
            boolean bold = element.has("bold") && element.get("bold").asBoolean();
            boolean italic = element.has("italic") && element.get("italic").asBoolean();

            // 验证字体可用性
            String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            boolean fontAvailable = false;
            for (String availableFont : availableFonts) {
                if (availableFont.equalsIgnoreCase(fontFamily)) {
                    fontAvailable = true;
                    break;
                }
            }

            if (!fontAvailable) {
                LOGGER.warn("⚠️ 字体 '{}' 不可用，使用默认字体", fontFamily);
                fontFamily = "SansSerif"; // 使用Java保证可用的逻辑字体
            }

            // 根据画布尺寸调整字体大小（保持相对比例）
            int scaledFontSize = Math.max(8, (int) (baseFontSize * Math.min(customArea.getWidth(), customArea.getHeight()) / 300.0));

            // 获取颜色信息
            String colorStr = element.has("color") ? element.get("color").asText() : "#000000";
            Color color = parseColor(colorStr);

            LOGGER.info("📝 开始渲染文字: '{}' 定制区域=({},{},{}x{}) 相对位置=({},{}) 原始字体大小={} 缩放后字体大小={} 颜色={}",
                text, customArea.getX(), customArea.getY(), customArea.getWidth(), customArea.getHeight(),
                relativeX, relativeY, baseFontSize, scaledFontSize, colorStr);

            // 设置字体
            int style = Font.PLAIN;
            if (bold) style |= Font.BOLD;
            if (italic) style |= Font.ITALIC;
            Font font = new Font(fontFamily, style, scaledFontSize);

            // 设置高质量文字渲染
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            g2d.setFont(font);
            g2d.setColor(color);

            LOGGER.info("🎨 字体设置完成: 字体={} 样式={} 大小={} 颜色={}",
                fontFamily, style, scaledFontSize, String.format("#%06X", color.getRGB() & 0xFFFFFF));

            // 获取文本度量信息
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            // 使用坐标转换器进行精确转换
            int canvasX = transformer.transformX(relativeX);
            int canvasY = transformer.transformTextY(relativeY, fm);

            LOGGER.info("📐 文字坐标转换: 相对坐标=({},{}) -> 画布坐标=({},{}) 文本尺寸=({},{}) 字体度量=ascent:{} descent:{} height:{}",
                relativeX, relativeY, canvasX, canvasY, textWidth, textHeight,
                fm.getAscent(), fm.getDescent(), fm.getHeight());

            // 使用坐标转换器进行边界检查和调整
            Rectangle adjustedBounds = transformer.adjustBounds(canvasX, canvasY - fm.getAscent(), textWidth, textHeight);

            // 重新计算文字绘制位置（基线位置）
            int finalX = adjustedBounds.x;
            int finalY = adjustedBounds.y + fm.getAscent();

            // 确保文字不会被裁剪
            if (finalX + textWidth > customArea.getWidth()) {
                finalX = Math.max(0, customArea.getWidth() - textWidth);
            }
            if (finalY > customArea.getHeight()) {
                finalY = customArea.getHeight() - fm.getDescent();
            }
            if (finalY - fm.getAscent() < 0) {
                finalY = fm.getAscent();
            }

            if (finalX != canvasX || finalY != canvasY) {
                LOGGER.info("⚠️ 文字坐标边界调整: ({},{}) -> ({},{})",
                    canvasX, canvasY, finalX, finalY);
            }

            LOGGER.info("✍️ 绘制文字: '{}' 最终坐标=({},{}) 字体={} 大小={} 颜色={}",
                text, finalX, finalY, fontFamily, scaledFontSize, colorStr);

            // 验证绘制参数
            if (finalX < 0 || finalY < 0 || finalX >= customArea.getWidth() || finalY >= customArea.getHeight()) {
                LOGGER.warn("⚠️ 文字绘制坐标可能有问题: ({},{}) 定制区域: {}x{}",
                    finalX, finalY, customArea.getWidth(), customArea.getHeight());
            }

            // 验证字体和颜色
            Font currentFont = g2d.getFont();
            Color currentColor = g2d.getColor();
            LOGGER.info("🔍 当前Graphics2D状态: 字体={} 颜色={} 透明度={}",
                currentFont, currentColor, currentColor.getAlpha());

            // 绘制文本 - 使用多种方法确保文字被正确渲染
            try {
                // 方法1: 标准文字绘制
                g2d.drawString(text, finalX, finalY);

                // 方法2: 如果文字太小或颜色太淡，尝试加粗绘制
                if (scaledFontSize < 12 || color.equals(Color.WHITE) || color.getAlpha() < 128) {
                    // 绘制文字轮廓以增强可见性
                    Color originalColor = g2d.getColor();
                    Stroke originalStroke = g2d.getStroke();

                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(1.0f));

                    // 绘制文字轮廓
                    FontRenderContext frc = g2d.getFontRenderContext();
                    java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, currentFont, frc);
                    Shape textShape = textLayout.getOutline(null);

                    // 移动到正确位置
                    AffineTransform transform = AffineTransform.getTranslateInstance(finalX, finalY);
                    Shape transformedShape = transform.createTransformedShape(textShape);

                    g2d.draw(transformedShape);

                    // 恢复原始设置
                    g2d.setColor(originalColor);
                    g2d.setStroke(originalStroke);

                    // 再次绘制填充文字
                    g2d.drawString(text, finalX, finalY);

                    LOGGER.info("🔍 使用增强渲染模式绘制文字: '{}'", text);
                }

                // 验证文字是否真的被绘制了（通过绘制一个小矩形标记）
                Color originalColor = g2d.getColor();
                g2d.setColor(Color.RED);
                g2d.drawRect(finalX - 2, finalY - fm.getAscent() - 2, textWidth + 4, textHeight + 4);
                g2d.setColor(originalColor);
                LOGGER.info("🔴 绘制文字边界框用于调试");

            } catch (Exception renderException) {
                LOGGER.error("文字绘制过程中出错", renderException);
                // 降级处理：绘制一个简单的矩形表示文字位置
                g2d.setColor(Color.BLUE);
                g2d.fillRect(finalX, finalY - fm.getAscent(), textWidth, textHeight);
                LOGGER.warn("🔵 使用矩形替代文字渲染");
            }

            LOGGER.info("✅ 文字渲染完成: '{}' 实际绘制坐标=({},{})", text, finalX, finalY);

        } catch (Exception e) {
            LOGGER.error("渲染文本元素到画布失败", e);
        }
    }

    /**
     * 上传图片到阿里云OSS存储
     */
    private String uploadImageToStorage(BufferedImage image, String fileName) {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;

        try {
            // 将BufferedImage转换为字节数组
            baos = new ByteArrayOutputStream();

            // 根据文件扩展名确定格式
            String format = fileName.toLowerCase().endsWith(".png") ? "png" : "jpg";

            if ("png".equals(format)) {
                // PNG格式，支持透明背景
                ImageIO.write(image, "png", baos);
            } else {
                // JPEG格式，设置质量参数
                javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.9f); // 90%质量

                javax.imageio.stream.ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                writer.dispose();
                ios.close();
            }

            byte[] imageBytes = baos.toByteArray();

            LOGGER.info("准备上传图片到OSS: 文件名={}, 大小={}KB", fileName, imageBytes.length / 1024);

            // 创建输入流
            bais = new ByteArrayInputStream(imageBytes);

            // 构建OSS文件路径
            String ossKey = ossDirectoryPrefix + fileName;

            // 上传到阿里云OSS
            ossClient.putObject(ossBucketName, ossKey, bais);

            // 构建访问URL
            String fileUrl = "https://" + ossBucketName + "." + ossEndpoint + "/" + ossKey;

            LOGGER.info("图片上传OSS成功: {}", fileUrl);

            return fileUrl;

        } catch (Exception e) {
            LOGGER.error("上传图片到OSS失败: fileName={}", fileName, e);
            return generateDefaultPreviewUrl(null);
        } finally {
            // 关闭流资源
            try {
                if (bais != null) bais.close();
                if (baos != null) baos.close();
            } catch (IOException e) {
                LOGGER.warn("关闭流资源失败", e);
            }
        }
    }

    /**
     * 渲染图片元素到画布（增强调试版本）
     */
    private void renderImageElementToCanvas(Graphics2D g2d, JsonNode element, CustomizableArea customArea) {
        try {
            String imageUrl = element.has("src") ? element.get("src").asText() : "";
            if (StringUtils.isEmpty(imageUrl)) {
                LOGGER.warn("    ⚠️ 图片元素缺少src属性，跳过渲染");
                return;
            }

            LOGGER.info("    🖼️ 渲染图片元素: {}", imageUrl);

            // 创建坐标转换器
            CoordinateTransformer transformer = new CoordinateTransformer(customArea);

            // 获取相对位置和尺寸（0-1范围）
            double relativeX = element.has("x") ? element.get("x").asDouble() : 0;
            double relativeY = element.has("y") ? element.get("y").asDouble() : 0;
            double relativeWidth = element.has("width") ? element.get("width").asDouble() : 0.3;
            double relativeHeight = element.has("height") ? element.get("height").asDouble() : 0.3;

            LOGGER.info("    📐 相对坐标: x={}, y={}, w={}, h={}", relativeX, relativeY, relativeWidth, relativeHeight);

            // 是否保持宽高比
            boolean keepAspectRatio = element.has("keepAspectRatio") ? element.get("keepAspectRatio").asBoolean() : true;

            // 使用坐标转换器进行精确转换
            int canvasX = transformer.transformX(relativeX);
            int canvasY = transformer.transformY(relativeY);
            int targetWidth = transformer.transformWidth(relativeWidth);
            int targetHeight = transformer.transformHeight(relativeHeight);

            LOGGER.info("    📍 画布坐标: x={}, y={}, w={}, h={}", canvasX, canvasY, targetWidth, targetHeight);

            // 使用坐标转换器进行边界检查和调整
            Rectangle adjustedBounds = transformer.adjustBounds(canvasX, canvasY, targetWidth, targetHeight);

            int finalX = adjustedBounds.x;
            int finalY = adjustedBounds.y;
            int finalWidth = adjustedBounds.width;
            int finalHeight = adjustedBounds.height;

            if (finalWidth <= 0 || finalHeight <= 0) {
                LOGGER.warn("    ⚠️ 图片尺寸无效，跳过渲染");
                return;
            }

            if (finalX != canvasX || finalY != canvasY || finalWidth != targetWidth || finalHeight != targetHeight) {
                LOGGER.info("    ⚠️ 图片坐标边界调整: ({},{},{}x{}) -> ({},{},{}x{})",
                    canvasX, canvasY, targetWidth, targetHeight, finalX, finalY, finalWidth, finalHeight);
            }

            // 加载图片
            LOGGER.info("    📥 加载图片: {}", imageUrl);
            BufferedImage image = loadImageFromUrl(imageUrl);
            if (image == null) {
                LOGGER.warn("    ❌ 无法加载图片: {}", imageUrl);
                return;
            }
            LOGGER.info("    ✅ 图片加载成功: {}x{}", image.getWidth(), image.getHeight());

            // 使用调整后的坐标和尺寸
            int renderWidth = finalWidth;
            int renderHeight = finalHeight;
            int drawX = finalX;
            int drawY = finalY;

            // 根据keepAspectRatio决定缩放策略
            if (keepAspectRatio) {
                // 保持宽高比，可能有空白区域（适应模式）
                ScaleInfo scaleInfo = calculateAspectRatioScale(image, finalWidth, finalHeight);
                renderWidth = scaleInfo.scaledWidth;
                renderHeight = scaleInfo.scaledHeight;

                // 居中显示
                drawX = finalX + (finalWidth - renderWidth) / 2;
                drawY = finalY + (finalHeight - renderHeight) / 2;

                LOGGER.info("    🔄 保持宽高比调整: 目标尺寸={}x{}, 最终尺寸={}x{}, 模式=适应",
                    finalWidth, finalHeight, renderWidth, renderHeight);
            } else {
                // 不保持宽高比，完全填满目标区域（填充模式）
                // 这种模式下图片会被拉伸以完全填满指定区域
                renderWidth = finalWidth;
                renderHeight = finalHeight;
                drawX = finalX;
                drawY = finalY;

                LOGGER.info("    🎯 填充模式: 完全填满目标区域={}x{}, 模式=填充",
                    renderWidth, renderHeight);
            }

            LOGGER.info("    🎯 最终渲染参数: 位置=({},{}), 尺寸={}x{}, 保持比例={}",
                drawX, drawY, renderWidth, renderHeight, keepAspectRatio);

            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // 绘制图片
            g2d.drawImage(image, drawX, drawY, renderWidth, renderHeight, null);

            LOGGER.info("    ✅ 图片元素渲染完成");

        } catch (Exception e) {
            LOGGER.error("    ❌ 渲染图片元素到画布失败", e);
        }
    }

    /**
     * 渲染形状元素到画布
     */
    private void renderShapeElementToCanvas(Graphics2D g2d, JsonNode element, CustomizableArea customArea) {
        try {
            String shapeType = element.has("shapeType") ? element.get("shapeType").asText() : "rectangle";

            // 获取相对位置和尺寸
            double relativeX = element.has("x") ? element.get("x").asDouble() : 0;
            double relativeY = element.has("y") ? element.get("y").asDouble() : 0;
            double relativeWidth = element.has("width") ? element.get("width").asDouble() : 0.2;
            double relativeHeight = element.has("height") ? element.get("height").asDouble() : 0.2;

            // 转换为画布坐标
            int canvasX = (int) (relativeX * customArea.getWidth());
            int canvasY = (int) (relativeY * customArea.getHeight());
            int canvasWidth = (int) (relativeWidth * customArea.getWidth());
            int canvasHeight = (int) (relativeHeight * customArea.getHeight());

            // 获取颜色信息
            String fillColorStr = element.has("fillColor") ? element.get("fillColor").asText() : "#FFFFFF";
            String strokeColorStr = element.has("strokeColor") ? element.get("strokeColor").asText() : "#000000";
            Color fillColor = parseColor(fillColorStr);
            Color strokeColor = parseColor(strokeColorStr);

            // 绘制形状
            switch (shapeType) {
                case "rectangle":
                    g2d.setColor(fillColor);
                    g2d.fillRect(canvasX, canvasY, canvasWidth, canvasHeight);
                    g2d.setColor(strokeColor);
                    g2d.drawRect(canvasX, canvasY, canvasWidth, canvasHeight);
                    break;
                case "circle":
                    g2d.setColor(fillColor);
                    g2d.fillOval(canvasX, canvasY, canvasWidth, canvasHeight);
                    g2d.setColor(strokeColor);
                    g2d.drawOval(canvasX, canvasY, canvasWidth, canvasHeight);
                    break;
            }

        } catch (Exception e) {
            LOGGER.error("渲染形状元素到画布失败", e);
        }
    }

    /**
     * 生成降级预览图
     */
    private List<FacePreviewResult> generateFallbackPreview(DiyDesignParam designParam) {
        List<FacePreviewResult> fallbackResults = new ArrayList<>();

        // 创建默认的单面预览结果
        FacePreviewResult defaultResult = new FacePreviewResult();
        defaultResult.setFaceIndex(0);
        defaultResult.setFaceName("正面");
        defaultResult.setPreviewImageUrl(generateDefaultPreviewUrl(designParam));
        defaultResult.setStatus("fallback");

        fallbackResults.add(defaultResult);
        return fallbackResults;
    }

    /**
     * 生成默认面预览URL
     */
    private String generateDefaultFacePreviewUrl(int faceIndex) {
        return "${DEFAULT_OSS_URL}/static/diy/default-face-" + faceIndex + ".jpg";
    }

    /**
     * 生成默认预览URL
     */
    private String generateDefaultPreviewUrl(DiyDesignParam designParam) {
        // 返回默认的预览图URL
        return "${DEFAULT_OSS_URL}/static/diy/default-preview.jpg";
    }

    /**
     * 获取可定制区域的坐标信息
     */
    private Map<String, Object> getCustomizableRegion(Long areaId, String areaName) {
        Map<String, Object> region = new HashMap<>();

        // 根据不同的定制面返回不同的坐标信息
        switch (areaName) {
            case "正面":
                region.put("x", 50);
                region.put("y", 80);
                region.put("width", 180);
                region.put("height", 120);
                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                break;
            case "背面":
                region.put("x", 50);
                region.put("y", 80);
                region.put("width", 180);
                region.put("height", 120);
                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                break;
            case "左袖口":
                region.put("x", 20);
                region.put("y", 180);
                region.put("width", 60);
                region.put("height", 40);
                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                break;
            case "右袖口":
                region.put("x", 200);
                region.put("y", 180);
                region.put("width", 60);
                region.put("height", 40);
                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                break;
            default:
                region.put("x", 50);
                region.put("y", 50);
                region.put("width", 180);
                region.put("height", 180);
                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                break;
        }

        return region;
    }



    /**
     * 从数据库获取定制面的真实可定制区域坐标
     */
    private List<Map<String, Object>> getCustomizableRegionsFromDatabase(Long surfaceId) {
        List<Map<String, Object>> regions = new ArrayList<>();

        try {
            PmsDiyAreaExample example = new PmsDiyAreaExample();
            example.createCriteria().andSurfaceIdEqualTo(surfaceId);
            List<PmsDiyArea> areas = diyAreaMapper.selectByExampleWithBLOBs(example);

            for (PmsDiyArea area : areas) {
                Map<String, Object> region = new HashMap<>();
                region.put("id", area.getId());
                region.put("name", area.getName());
                region.put("pathData", area.getPathData());
                region.put("bounds", area.getBounds());
                region.put("maskImageUrl", area.getMaskImageUrl()); // 添加蒙版URL

                // 解析bounds字符串 (格式: "x,y,width,height")
                if (area.getBounds() != null) {
                    String[] boundsArray = area.getBounds().split(",");
                    if (boundsArray.length >= 4) {
                        try {
                            region.put("x", Integer.parseInt(boundsArray[0].trim()));
                            region.put("y", Integer.parseInt(boundsArray[1].trim()));
                            region.put("width", Integer.parseInt(boundsArray[2].trim()));
                            region.put("height", Integer.parseInt(boundsArray[3].trim()));
                        } catch (NumberFormatException e) {
                            LOGGER.warn("解析bounds失败: {}", area.getBounds(), e);
                        }
                    }
                }

                region.put("borderColor", "#A9FF00");
                region.put("borderStyle", "dashed");
                regions.add(region);
            }

            // 如果数据库中没有区域数据，返回默认区域
            if (regions.isEmpty()) {
                regions.add(getDefaultRegion());
            }

        } catch (Exception e) {
            LOGGER.error("查询定制面区域失败，surfaceId: {}", surfaceId, e);
            // 返回默认区域作为兜底
            regions.add(getDefaultRegion());
        }

        return regions;
    }

    /**
     * 获取默认的可定制区域
     */
    private Map<String, Object> getDefaultRegion() {
        Map<String, Object> region = new HashMap<>();
        region.put("id", 0L);
        region.put("name", "默认区域");
        region.put("x", 50);
        region.put("y", 50);
        region.put("width", 180);
        region.put("height", 120);
        region.put("borderColor", "#A9FF00");
        region.put("borderStyle", "dashed");
        return region;
    }

    /**
     * 从数据库定制面数据创建API返回对象
     */
    private Object createCustomizableAreaFromSurface(PmsDiyTemplateSurface surface) {
        Map<String, Object> area = new HashMap<>();
        area.put("id", surface.getId());
        area.put("name", surface.getName());
        area.put("previewImage", surface.getExampleImage());
        area.put("previewImageWithMarks", generatePreviewImageWithMarks(surface.getExampleImage(), surface.getId()));
        area.put("customizable", true);

        // 从数据库获取真实的可定制区域坐标信息
        List<Map<String, Object>> customizableRegions = getCustomizableRegionsFromDatabase(surface.getId());
        area.put("customizableRegions", customizableRegions);

        return area;
    }

    /**
     * 获取默认的可定制面数据（兜底方案）
     */
    private List<Object> getDefaultCustomizableAreas(PmsProduct product) {
        List<Object> areas = new ArrayList<>();
        String productType = getProductType(product);

        switch (productType) {
            case "clothing":
                areas.add(createCustomizableArea(1L, "正面", "${DEFAULT_OSS_URL}/static/new_index/banner1.jpg", true));
                areas.add(createCustomizableArea(2L, "背面", "${DEFAULT_OSS_URL}/static/new_index/banner2.jpg", true));
                areas.add(createCustomizableArea(3L, "左袖口", "${DEFAULT_OSS_URL}/static/new_index/banner1.jpg", true));
                areas.add(createCustomizableArea(4L, "右袖口", "${DEFAULT_OSS_URL}/static/new_index/banner2.jpg", true));
                break;
            case "stationery":
                areas.add(createCustomizableArea(1L, "正面", "${DEFAULT_OSS_URL}/static/new_index/banner1.jpg", true));
                areas.add(createCustomizableArea(2L, "背面", "${DEFAULT_OSS_URL}/static/new_index/banner2.jpg", true));
                break;
            default:
                areas.add(createCustomizableArea(1L, "正面", "${DEFAULT_OSS_URL}/static/new_index/banner1.jpg", true));
                break;
        }

        return areas;
    }

    /**
     * 根据商品信息判断商品类型
     */
    private String getProductType(PmsProduct product) {
        // 这里可以根据商品分类或其他字段来判断商品类型
        // 暂时返回默认类型
        if (product.getProductCategoryId() != null) {
            Long categoryId = product.getProductCategoryId();
            if (categoryId == 1L) {
                return "clothing";
            } else if (categoryId == 2L) {
                return "stationery";
            } else if (categoryId == 3L) {
                return "lifestyle";
            } else if (categoryId == 4L) {
                return "digital";
            }
        }
        return "default";
    }

    /**
     * 根据类型值映射到文件类型模式
     */
    private String getFileTypePattern(Integer type) {
        if (type == null) return null;

        switch (type) {
            case 0: // 图片类型
                return "jpg,jpeg,png,gif,webp";
            case 1: // 文字/字体类型
                return "ttf,otf,woff";
            case 2: // 形状/矢量类型
                return "svg";
            default:
                return null;
        }
    }
}
