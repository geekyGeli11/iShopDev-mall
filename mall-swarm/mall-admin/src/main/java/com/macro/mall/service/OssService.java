package com.macro.mall.service;

import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.dto.OssPolicyResult;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * oss上传管理Service
 * Created by macro on 2018/5/17.
 */
public interface OssService {
    /**
     * oss上传策略生成
     */
    OssPolicyResult policy();
    /**
     * oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);

    String uploadFileToCos(MultipartFile file);
    
    /**
     * 获取腾讯云COS临时密钥
     * @return 临时密钥信息
     */
    Map<String, Object> getStsToken();
    
    /**
     * DIY素材文件直接上传到OSS
     * @param file 上传的文件
     * @return 上传结果
     */
    OssCallbackResult uploadDiyMaterial(MultipartFile file);
}
