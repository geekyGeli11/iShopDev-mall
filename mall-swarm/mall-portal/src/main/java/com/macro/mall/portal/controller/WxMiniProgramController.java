package com.macro.mall.portal.controller;

import com.macro.mall.portal.service.WxMiniProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 微信小程序相关接口
 * Created on 2025/04/30.
 */
@Controller
@Tag(name = "WxMiniProgramController", description = "微信小程序相关接口")
@RequestMapping("/wx/miniprogram")
public class WxMiniProgramController {

    @Autowired
    private WxMiniProgramService wxMiniProgramService;

    @Operation(summary = "生成小程序码")
    @GetMapping(value = "/qrcode")
    public ResponseEntity<byte[]> generateQrCode(
            @Parameter(description = "小程序页面路径，例如 pages/index/index") @RequestParam String page,
            @Parameter(description = "页面参数，例如 id=1") @RequestParam(required = false) String params) {
        try {
            // 处理page和params
            if (page.startsWith("/")) {
                // 去除可能的开头斜杠
                page = page.substring(1);
            }
            
            // 场景值，用于小程序获取页面参数
            String scene = params != null ? params : "";
            
            // 生成小程序码
            InputStream inputStream = wxMiniProgramService.generateMiniProgramCode(page, scene);
            
            // 转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            // 返回图片数据
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
} 