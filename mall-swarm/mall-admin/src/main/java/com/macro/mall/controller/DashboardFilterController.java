package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.service.OmsSchoolService;
import com.macro.mall.service.OmsStoreAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看板筛选选项Controller
 * Created by macro on 2025/11/28.
 */
@RestController
@RequestMapping("/api/home/statistics")
@Tag(name = "看板筛选选项", description = "看板筛选选项相关接口")
public class DashboardFilterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardFilterController.class);
    
    @Autowired
    private OmsSchoolService schoolService;
    
    @Autowired
    private OmsStoreAddressService storeAddressService;
    
    @Operation(summary = "获取筛选选项")
    @GetMapping("/filter-options")
    public CommonResult<Map<String, Object>> getFilterOptions() {
        try {
            LOGGER.info("获取筛选选项请求");
            
            Map<String, Object> result = new HashMap<>();
            
            // 获取学校列表 - 获取所有学校
            List<OmsSchool> schools = schoolService.list(null, null, 1, 1000);
            result.put("schools", schools);
            
            // 获取门店列表 - 获取所有门店
            List<OmsStoreAddress> stores = storeAddressService.list(null, null, null, 1, 1000);
            result.put("stores", stores);
            
            return CommonResult.success(result);
        } catch (Exception e) {
            LOGGER.error("获取筛选选项失败", e);
            return CommonResult.failed("获取筛选选项失败: " + e.getMessage());
        }
    }
}
