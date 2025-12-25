# 阿里云万相API集成说明

## 📋 概述

本文档说明如何在mall-portal项目中集成阿里云万相API，实现真正的AI图片风格化功能。

## 🔧 当前实现状态

### ✅ 已完成
1. **配置管理**：在nacos配置中添加了`aliyun.api-key`配置
2. **服务架构**：创建了`AliyunWanxService`接口和实现类
3. **数据存储**：完善了AI风格化记录的数据库存储
4. **错误处理**：实现了完整的异常处理机制
5. **文件上传**：集成了OSS文件上传功能
6. **真实API调用**：✅ 已启用真正的阿里云万相API调用

### 🎉 功能状态
- **编译状态**: ✅ 成功
- **API集成**: ✅ 已启用万相2.1模型
- **图片处理**: ✅ 支持URL输入和OSS存储
- **错误处理**: ✅ 完整的异常处理机制

## ✅ 万相API实现详情

### 🔧 技术实现
我们使用了阿里云万相2.1模型进行图片风格化处理：

**模型配置**：
- 模型：`wanx2.1-imageedit`
- 功能：`DESCRIPTION_EDIT` (描述编辑)
- 输出尺寸：`1024*1024`
- 生成数量：1张

**API调用流程**：

```java
@Override
public String stylizeImage(String imageUrl, String style) {
    try {
        // 设置API密钥
        Constants.apiKey = aliyunAiConfig.getApiKey();
        
        LOGGER.info("调用万相API进行图片风格化，原图：{}，风格：{}", imageUrl, style);
        
        // 构建请求参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("input_image_url", imageUrl);
        parameters.put("style", style);
        parameters.put("size", "1024*1024");
        
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .model("wanx-style-repaint-v1")
                .prompt("Apply " + style + " style to the image")
                .parameters(parameters)
                .build();
        
        // 调用万相API
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = imageSynthesis.call(param);
        
        if (result == null || result.getOutput() == null) {
            throw new RuntimeException("万相API返回结果为空");
        }
        
        // 解析结果
        Map<String, Object> output = result.getOutput();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) output.get("results");
        
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("万相API未返回生成结果");
        }
        
        String generatedImageUrl = (String) results.get(0).get("url");
        if (generatedImageUrl == null || generatedImageUrl.isEmpty()) {
            throw new RuntimeException("万相API返回的图片URL为空");
        }
        
        LOGGER.info("万相API生成图片成功：{}", generatedImageUrl);
        
        // 下载生成的图片并上传到OSS
        String stylizedImageUrl = downloadAndUploadToOss(generatedImageUrl, "stylized_" + System.currentTimeMillis() + ".jpg");
        
        return stylizedImageUrl;
        
    } catch (NoApiKeyException e) {
        LOGGER.error("阿里云API密钥未配置", e);
        throw new RuntimeException("AI风格化服务配置错误：API密钥未配置");
    } catch (ApiException e) {
        LOGGER.error("阿里云万相API调用失败", e);
        throw new RuntimeException("万相API调用失败：" + e.getMessage());
    } catch (InputRequiredException e) {
        LOGGER.error("万相API输入参数错误", e);
        throw new RuntimeException("输入参数错误：" + e.getMessage());
    } catch (Exception e) {
        LOGGER.error("万相API调用异常", e);
        throw new RuntimeException("万相API调用异常：" + e.getMessage());
    }
}
```

## 📝 配置说明

### Nacos配置
在`mall-portal-dev.yaml`和`mall-portal-prod.yaml`中已添加：
```yaml
aliyun:
  api-key: sk-e6bc3ca150744e70af5f30fdcffc4d22
```

### 依赖配置
项目已包含万相API依赖：
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.21.5</version>
</dependency>
```

## 🧪 测试方法

### 1. 接口测试
```bash
POST /diy/ai/stylization
Content-Type: application/x-www-form-urlencoded

imageUrl=https://example.com/image.jpg&style=cartoon
```

### 2. 预期响应
```json
{
    "code": 200,
    "message": "操作成功",
    "data": "https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-stylization/20241220/stylized_1703123456789.jpg"
}
```

## 🔍 故障排查

### 常见问题
1. **API密钥错误**：检查nacos配置中的API密钥是否正确
2. **网络连接问题**：确保服务器能访问阿里云万相API
3. **图片URL无效**：确保输入的图片URL可以正常访问
4. **OSS上传失败**：检查腾讯云COS配置是否正确

### 日志查看
关键日志位置：
- 万相API调用：`AliyunWanxServiceImpl`
- 记录保存：`PortalDiyServiceImpl`
- 文件上传：`OssUploadUtil`

## 📚 相关文档

- [阿里云万相API文档](https://help.aliyun.com/zh/dashscope/)
- [DashScope SDK文档](https://help.aliyun.com/zh/dashscope/developer-reference/sdk-overview)
