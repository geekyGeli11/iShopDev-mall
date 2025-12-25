package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.CustomerServiceInfoDTO;
import com.macro.mall.portal.service.CustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 客服信息Controller
 * Created by macro on 2025/11/28.
 */
@Controller
@Tag(name = "CustomerServiceController", description = "客服信息")
@RequestMapping("/customerService")
public class CustomerServiceController {
    
    @Autowired
    private CustomerServiceService customerServiceService;
    
    @Operation(summary = "获取客服信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CustomerServiceInfoDTO> getCustomerServiceInfo() {
        CustomerServiceInfoDTO info = customerServiceService.getCustomerServiceInfo();
        return CommonResult.success(info);
    }
}
