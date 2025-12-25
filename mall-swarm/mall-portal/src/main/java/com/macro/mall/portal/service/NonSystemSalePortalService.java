package com.macro.mall.portal.service;

import java.util.Map;

/**
 * 非系统销售单小程序端服务接口
 * Created by macro on 2025-12-12.
 */
public interface NonSystemSalePortalService {
    
    /**
     * 获取销售单详情（带权限验证）
     * 验证当前登录用户的手机号是否与销售单的客户手机号一致
     * @param saleId 销售单ID
     * @return 销售单详情
     * @throws SecurityException 如果用户无权限查看
     */
    Map<String, Object> getSaleDetailWithAuth(Long saleId);
    
    /**
     * 检查是否有权限查看销售单
     * @param saleId 销售单ID
     * @return 权限检查结果
     */
    Map<String, Object> checkSaleAuth(Long saleId);
}
