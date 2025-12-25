package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.DiyPageDTO;
import com.macro.mall.model.DiyComponentLibrary;
import com.macro.mall.service.DiyDecorateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DIY 装修 – shopxo-diy 对接接口
 */
@RestController
@Tag(name = "DiyDecorateController", description = "DIY 装修 (shopxo-diy)")
@RequestMapping("/diy")
public class DiyDecorateController {

    @Autowired
    private DiyDecorateService diyService;

    /* ---------- 组件库 ---------- */
    @Operation(summary = "组件列表")
    @GetMapping("/component/list")
    public CommonResult<List<DiyComponentLibrary>> listComponents(){
        return CommonResult.success(diyService.listComponents());
    }

    /* ---------- 页面 CRUD ---------- */
    @Operation(summary = "页面列表")
    @GetMapping("/page/list")
    public CommonResult<CommonPage<DiyPageDTO>> listPages(@RequestParam Long storeId,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize){
        return CommonResult.success(diyService.listPages(storeId,pageNum,pageSize));
    }

    @Operation(summary = "新建页面")
    @PostMapping("/page")
    public CommonResult<DiyPageDTO> createPage(@RequestBody DiyPageDTO page){
        return CommonResult.success(diyService.createPage(page));
    }

    @Operation(summary = "页面详情")
    @GetMapping("/page/{id}")
    public CommonResult<DiyPageDTO> getPage(@PathVariable Long id){
        return CommonResult.success(diyService.getPage(id));
    }

    @Operation(summary = "保存草稿")
    @PutMapping("/page/{id}")
    public CommonResult<DiyPageDTO> saveDraft(@PathVariable Long id,@RequestBody DiyPageDTO page){
        return CommonResult.success(diyService.saveDraft(id,page));
    }

    @Operation(summary = "发布页面")
    @PostMapping("/page/{id}/publish")
    public CommonResult<?> publish(@PathVariable Long id){
        diyService.publish(id);
        return CommonResult.success(null);
    }

    /* ---------- Runtime ---------- */
    @Operation(summary = "小程序端拉取首页 DSL")
    @GetMapping("/runtime/{storeId}/home")
    public CommonResult<String> runtimeHome(@PathVariable Long storeId){
        return CommonResult.success(diyService.getPublishedDsl(storeId));
    }

    /* ---------- init ---------- */
    @Operation(summary="初始化配置")
    @PostMapping("/init")
    public CommonResult<java.util.Map<String,Object>> init(){
        return CommonResult.success(diyService.initInfo());
    }

    /* ---------- 上传 ---------- */
    @Operation(summary="DIY 文件上传（代理 COS）")
    @PostMapping("/upload")
    public CommonResult<String> upload(@org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) {
        // 复用已有 OSS / COS 上传逻辑
        String url = diyService.uploadToCos(file);
        return CommonResult.success(url);
    }
} 