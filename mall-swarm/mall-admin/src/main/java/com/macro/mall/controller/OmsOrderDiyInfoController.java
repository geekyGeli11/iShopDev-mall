package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OmsOrderDiyInfoParam;
import com.macro.mall.dto.OmsOrderDiyInfoVO;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.service.OmsOrderDiyInfoService;
import com.macro.mall.service.PmsDiyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单DIY信息管理Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "OmsOrderDiyInfoController", description = "订单DIY信息管理")
@RequestMapping("/orderDiyInfo")
public class OmsOrderDiyInfoController {
    
    @Autowired
    private OmsOrderDiyInfoService diyInfoService;

    @Autowired
    private PmsDiyTemplateService templateService;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Operation(summary = "创建订单DIY信息")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody OmsOrderDiyInfoParam diyInfoParam) {
        OmsOrderDiyInfo diyInfo = new OmsOrderDiyInfo();
        BeanUtils.copyProperties(diyInfoParam, diyInfo);
        int count = diyInfoService.create(diyInfo);
        if (count > 0) {
            return CommonResult.success(diyInfo);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新订单DIY信息")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody OmsOrderDiyInfoParam diyInfoParam) {
        OmsOrderDiyInfo diyInfo = new OmsOrderDiyInfo();
        BeanUtils.copyProperties(diyInfoParam, diyInfo);
        int count = diyInfoService.update(id, diyInfo);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除订单DIY信息")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = diyInfoService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除订单DIY信息")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = diyInfoService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取订单DIY信息详情")
    @GetMapping("/{id}")
    public CommonResult<OmsOrderDiyInfoVO> getItem(@PathVariable Long id) {
        OmsOrderDiyInfo diyInfo = diyInfoService.getItem(id);
        if (diyInfo == null) {
            return CommonResult.failed("订单DIY信息不存在");
        }
        
        OmsOrderDiyInfoVO diyInfoVO = convertToVO(diyInfo);
        return CommonResult.success(diyInfoVO);
    }

    @Operation(summary = "根据订单ID获取DIY信息列表")
    @GetMapping("/listByOrder/{orderId}")
    public CommonResult<List<OmsOrderDiyInfoVO>> listByOrderId(@PathVariable Long orderId) {
        List<OmsOrderDiyInfo> diyInfoList = diyInfoService.listByOrderId(orderId);
        List<OmsOrderDiyInfoVO> diyInfoVOList = convertToVOList(diyInfoList);
        return CommonResult.success(diyInfoVOList);
    }

    @Operation(summary = "根据订单项ID获取DIY信息")
    @GetMapping("/getByOrderItem/{orderItemId}")
    public CommonResult<OmsOrderDiyInfoVO> getByOrderItemId(@PathVariable Long orderItemId) {
        OmsOrderDiyInfo diyInfo = diyInfoService.getByOrderItemId(orderItemId);
        if (diyInfo == null) {
            return CommonResult.failed("订单项DIY信息不存在");
        }
        
        OmsOrderDiyInfoVO diyInfoVO = convertToVO(diyInfo);
        return CommonResult.success(diyInfoVO);
    }

    @Operation(summary = "分页查询订单DIY信息")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsOrderDiyInfoVO>> list(
            @Parameter(description = "关键词") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "生产状态") @RequestParam(value = "productionStatus", required = false) Byte productionStatus,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        List<OmsOrderDiyInfo> diyInfoList = diyInfoService.list(keyword, productionStatus, startTime, endTime, pageSize, pageNum);
        List<OmsOrderDiyInfoVO> diyInfoVOList = convertToVOList(diyInfoList);
        return CommonResult.success(CommonPage.restPage(diyInfoVOList));
    }

    @Operation(summary = "更新生产状态")
    @PostMapping("/updateProductionStatus/{id}")
    public CommonResult updateProductionStatus(@PathVariable Long id, @RequestParam("productionStatus") Byte productionStatus) {
        int count = diyInfoService.updateProductionStatus(id, productionStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量更新生产状态")
    @PostMapping("/updateProductionStatus/batch")
    public CommonResult updateProductionStatusBatch(@RequestParam("ids") List<Long> ids, @RequestParam("productionStatus") Byte productionStatus) {
        int count = diyInfoService.updateProductionStatus(ids, productionStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "下载DIY设计文件")
    @GetMapping("/downloadDesignFile/{id}")
    public CommonResult<String> downloadDesignFile(@PathVariable Long id) {
        String fileUrl = diyInfoService.downloadDesignFile(id);
        if (fileUrl != null) {
            return CommonResult.success(fileUrl);
        }
        return CommonResult.failed("文件不存在或下载失败");
    }

    @Operation(summary = "生成生产文件")
    @PostMapping("/generateProductionFile/{id}")
    public CommonResult<String> generateProductionFile(@PathVariable Long id) {
        String fileUrl = diyInfoService.generateProductionFile(id);
        if (fileUrl != null) {
            return CommonResult.success(fileUrl);
        }
        return CommonResult.failed("生成生产文件失败");
    }

    @Operation(summary = "根据生产状态统计数量")
    @GetMapping("/countByProductionStatus")
    public CommonResult<Long> countByProductionStatus(@RequestParam(value = "productionStatus", required = false) Byte productionStatus) {
        Long count = diyInfoService.countByProductionStatus(productionStatus);
        return CommonResult.success(count);
    }

    /**
     * 转换为VO对象
     */
    private OmsOrderDiyInfoVO convertToVO(OmsOrderDiyInfo diyInfo) {
        OmsOrderDiyInfoVO diyInfoVO = new OmsOrderDiyInfoVO();
        BeanUtils.copyProperties(diyInfo, diyInfoVO);

        // 设置生产状态描述
        diyInfoVO.setProductionStatusDesc(OmsOrderDiyInfoVO.getProductionStatusDesc(diyInfo.getProductionStatus()));

        // 获取关联的模板信息
        if (diyInfo.getTemplateId() != null) {
            PmsDiyTemplate template = templateService.getItem(diyInfo.getTemplateId());
            diyInfoVO.setTemplate(template);
        }

        // 获取商品名称
        if (diyInfo.getOrderItemId() != null) {
            OmsOrderItem orderItem = orderItemMapper.selectByPrimaryKey(diyInfo.getOrderItemId());
            if (orderItem != null) {
                diyInfoVO.setProductName(orderItem.getProductName());
            }
        }

        // 获取订单信息(用户名、收货地址、金额等)
        if (diyInfo.getOrderId() != null) {
            OmsOrder order = orderMapper.selectByPrimaryKey(diyInfo.getOrderId());
            if (order != null) {
                // 用户信息
                diyInfoVO.setMemberUsername(order.getMemberUsername());

                // 收货地址信息
                diyInfoVO.setReceiverName(order.getReceiverName());
                diyInfoVO.setReceiverPhone(order.getReceiverPhone());
                diyInfoVO.setReceiverPostCode(order.getReceiverPostCode());
                diyInfoVO.setReceiverProvince(order.getReceiverProvince());
                diyInfoVO.setReceiverCity(order.getReceiverCity());
                diyInfoVO.setReceiverRegion(order.getReceiverRegion());
                diyInfoVO.setReceiverDetailAddress(order.getReceiverDetailAddress());

                // 金额信息
                diyInfoVO.setTotalAmount(order.getTotalAmount());
                diyInfoVO.setPayAmount(order.getPayAmount());
                diyInfoVO.setFreightAmount(order.getFreightAmount());
                diyInfoVO.setPromotionAmount(order.getPromotionAmount());
                diyInfoVO.setIntegrationAmount(order.getIntegrationAmount());
                diyInfoVO.setCouponAmount(order.getCouponAmount());
                diyInfoVO.setDiscountAmount(order.getDiscountAmount());
            }
        }

        return diyInfoVO;
    }

    /**
     * 转换为VO列表
     */
    private List<OmsOrderDiyInfoVO> convertToVOList(List<OmsOrderDiyInfo> diyInfoList) {
        List<OmsOrderDiyInfoVO> diyInfoVOList = new ArrayList<>();
        for (OmsOrderDiyInfo diyInfo : diyInfoList) {
            diyInfoVOList.add(convertToVO(diyInfo));
        }
        return diyInfoVOList;
    }
}
