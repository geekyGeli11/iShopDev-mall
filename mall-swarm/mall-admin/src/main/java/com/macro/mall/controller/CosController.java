package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.dto.OssPolicyResult;
import com.macro.mall.service.impl.CosServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * COS相关操作接口
 * Created by user on 2025/1/2.
 */
@Controller
@Tag(name = "CosController", description = "COS管理")
@RequestMapping("/tencent/cos")
public class CosController {

    @Autowired
    private CosServiceImpl cosService;

    @Operation(summary = "cos上传签名生成")
    @RequestMapping(value = "/policy", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OssPolicyResult> policy() {
        OssPolicyResult result = cosService.policy();
        return CommonResult.success(result);
    }

    @Operation(summary = "cos上传成功回调")
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<OssCallbackResult> callback(HttpServletRequest request) {
        OssCallbackResult callbackResult = cosService.callback(request);
        return CommonResult.success(callbackResult);
    }

    @Operation(summary = "上传图片并由后端上传到COS")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public CommonResult<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonResult.failed("文件为空，无法上传");
        }
        try {
            // 使用服务类将文件上传到 COS
            String fileUrl = cosService.uploadFileToCos(file);
            return CommonResult.success(fileUrl, "文件上传成功");
        } catch (Exception e) {
            return CommonResult.failed("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取腾讯云COS临时密钥")
    @RequestMapping(value = "/sts", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getStsToken() {
        try {
            Map<String, Object> result = cosService.getStsToken();
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("获取临时密钥失败：" + e.getMessage());
        }
    }
}