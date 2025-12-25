package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Base64数据传输测试Controller
 * Created by macro on 2024/12/30.
 */
@Controller
@Tag(name = "Base64TestController", description = "Base64数据传输测试")
@RequestMapping("/test/base64")
public class Base64TestController {
    
    @Operation(summary = "测试生成小的base64数据")
    @RequestMapping(value = "/small", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> testSmallBase64() {
        // 生成一个小的测试图片的base64数据
        String testImageBase64 = generateTestImageBase64(100, 100);
        
        Map<String, Object> result = new HashMap<>();
        result.put("imageBase64", testImageBase64);
        result.put("length", testImageBase64.length());
        result.put("description", "小尺寸测试图片");
        
        return CommonResult.success(result);
    }
    
    @Operation(summary = "测试生成大的base64数据")
    @RequestMapping(value = "/large", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> testLargeBase64() {
        // 生成一个大的测试图片的base64数据
        String testImageBase64 = generateTestImageBase64(430, 430);
        
        Map<String, Object> result = new HashMap<>();
        result.put("imageBase64", testImageBase64);
        result.put("length", testImageBase64.length());
        result.put("description", "大尺寸测试图片（模拟小程序码）");
        
        return CommonResult.success(result);
    }
    
    @Operation(summary = "测试接收base64数据")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> testReceiveBase64(@RequestBody Map<String, String> request) {
        String base64Data = request.get("base64Data");
        
        Map<String, Object> result = new HashMap<>();
        result.put("received", base64Data != null);
        result.put("length", base64Data != null ? base64Data.length() : 0);
        result.put("isValidFormat", base64Data != null && base64Data.startsWith("data:image/"));
        
        if (base64Data != null) {
            result.put("prefix", base64Data.substring(0, Math.min(50, base64Data.length())));
        }
        
        return CommonResult.success(result);
    }
    
    /**
     * 生成测试用的PNG图片base64数据
     */
    private String generateTestImageBase64(int width, int height) {
        // 创建一个简单的PNG图片数据（最小的PNG文件）
        byte[] pngHeader = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, // IHDR chunk length
            0x49, 0x48, 0x44, 0x52, // IHDR
            0x00, 0x00, 0x00, 0x01, // width (1 pixel)
            0x00, 0x00, 0x00, 0x01, // height (1 pixel)
            0x08, 0x02, 0x00, 0x00, 0x00, // bit depth, color type, compression, filter, interlace
            (byte) 0x90, (byte) 0x77, (byte) 0x53, (byte) 0xDE, // CRC
            0x00, 0x00, 0x00, 0x0C, // IDAT chunk length
            0x49, 0x44, 0x41, 0x54, // IDAT
            0x08, (byte) 0x99, 0x01, 0x01, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01, // compressed data
            (byte) 0xE2, 0x21, (byte) 0xBC, 0x33, // CRC
            0x00, 0x00, 0x00, 0x00, // IEND chunk length
            0x49, 0x45, 0x4E, 0x44, // IEND
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82 // CRC
        };
        
        // 根据请求的大小，重复数据来模拟不同大小的图片
        int targetSize = width * height / 10; // 模拟压缩后的大小
        byte[] imageData = new byte[Math.max(pngHeader.length, targetSize)];
        
        // 复制PNG头
        System.arraycopy(pngHeader, 0, imageData, 0, pngHeader.length);
        
        // 填充剩余数据
        for (int i = pngHeader.length; i < imageData.length; i++) {
            imageData[i] = (byte) (i % 256);
        }
        
        String base64 = Base64.getEncoder().encodeToString(imageData);
        return "data:image/png;base64," + base64;
    }
}
