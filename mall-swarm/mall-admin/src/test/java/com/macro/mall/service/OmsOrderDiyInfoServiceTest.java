//package com.macro.mall.service;
//
//import com.macro.mall.model.OmsOrderDiyInfo;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 订单DIY信息管理Service测试
// * Created by macro on 2024/12/20.
// */
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class OmsOrderDiyInfoServiceTest {
//
//    @Autowired
//    private OmsOrderDiyInfoService diyInfoService;
//
//    @Test
//    public void testCreate() {
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//
//        int result = diyInfoService.create(diyInfo);
//        assertEquals(1, result);
//        assertNotNull(diyInfo.getId());
//        assertNotNull(diyInfo.getCreateTime());
//        assertEquals((byte) 0, diyInfo.getProductionStatus()); // 默认待生产
//    }
//
//    @Test
//    public void testUpdate() {
//        // 先创建一个DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        diyInfoService.create(diyInfo);
//
//        // 更新DIY信息
//        OmsOrderDiyInfo updateDiyInfo = new OmsOrderDiyInfo();
//        updateDiyInfo.setProductionStatus((byte) 1);
//        updateDiyInfo.setPreviewImage("http://updated.com/preview.jpg");
//
//        int result = diyInfoService.update(diyInfo.getId(), updateDiyInfo);
//        assertEquals(1, result);
//
//        // 验证更新结果
//        OmsOrderDiyInfo updatedDiyInfo = diyInfoService.getItem(diyInfo.getId());
//        assertEquals((byte) 1, updatedDiyInfo.getProductionStatus());
//        assertEquals("http://updated.com/preview.jpg", updatedDiyInfo.getPreviewImage());
//    }
//
//    @Test
//    public void testDelete() {
//        // 先创建一个DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        diyInfoService.create(diyInfo);
//
//        // 删除DIY信息
//        int result = diyInfoService.delete(diyInfo.getId());
//        assertEquals(1, result);
//
//        // 验证删除结果
//        OmsOrderDiyInfo deletedDiyInfo = diyInfoService.getItem(diyInfo.getId());
//        assertNull(deletedDiyInfo);
//    }
//
//    @Test
//    public void testBatchDelete() {
//        // 创建多个DIY信息
//        OmsOrderDiyInfo diyInfo1 = createTestDiyInfo();
//        diyInfo1.setOrderSn("ORDER001");
//        diyInfoService.create(diyInfo1);
//
//        OmsOrderDiyInfo diyInfo2 = createTestDiyInfo();
//        diyInfo2.setOrderSn("ORDER002");
//        diyInfoService.create(diyInfo2);
//
//        // 批量删除
//        List<Long> ids = Arrays.asList(diyInfo1.getId(), diyInfo2.getId());
//        int result = diyInfoService.delete(ids);
//        assertEquals(2, result);
//    }
//
//    @Test
//    public void testListByOrderId() {
//        // 创建同一订单的多个DIY信息
//        Long orderId = 1001L;
//
//        OmsOrderDiyInfo diyInfo1 = createTestDiyInfo();
//        diyInfo1.setOrderId(orderId);
//        diyInfo1.setOrderItemId(1L);
//        diyInfoService.create(diyInfo1);
//
//        OmsOrderDiyInfo diyInfo2 = createTestDiyInfo();
//        diyInfo2.setOrderId(orderId);
//        diyInfo2.setOrderItemId(2L);
//        diyInfoService.create(diyInfo2);
//
//        // 查询订单的DIY信息列表
//        List<OmsOrderDiyInfo> diyInfoList = diyInfoService.listByOrderId(orderId);
//        assertTrue(diyInfoList.size() >= 2);
//
//        // 验证都属于同一订单
//        for (OmsOrderDiyInfo diyInfo : diyInfoList) {
//            assertEquals(orderId, diyInfo.getOrderId());
//        }
//    }
//
//    @Test
//    public void testGetByOrderItemId() {
//        // 创建DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        Long orderItemId = 1001L;
//        diyInfo.setOrderItemId(orderItemId);
//        diyInfoService.create(diyInfo);
//
//        // 根据订单项ID查询
//        OmsOrderDiyInfo foundDiyInfo = diyInfoService.getByOrderItemId(orderItemId);
//        assertNotNull(foundDiyInfo);
//        assertEquals(orderItemId, foundDiyInfo.getOrderItemId());
//        assertEquals(diyInfo.getOrderSn(), foundDiyInfo.getOrderSn());
//    }
//
//    @Test
//    public void testList() {
//        // 创建测试数据
//        OmsOrderDiyInfo diyInfo1 = createTestDiyInfo();
//        diyInfo1.setOrderSn("SEARCH001");
//        diyInfo1.setProductionStatus((byte) 0);
//        diyInfoService.create(diyInfo1);
//
//        OmsOrderDiyInfo diyInfo2 = createTestDiyInfo();
//        diyInfo2.setOrderSn("SEARCH002");
//        diyInfo2.setProductionStatus((byte) 1);
//        diyInfoService.create(diyInfo2);
//
//        // 测试关键词搜索
//        List<OmsOrderDiyInfo> result1 = diyInfoService.list("SEARCH", null, 10, 1);
//        assertTrue(result1.size() >= 2);
//
//        // 测试生产状态筛选
//        List<OmsOrderDiyInfo> result2 = diyInfoService.list(null, (byte) 0, 10, 1);
//        assertTrue(result2.size() >= 1);
//
//        // 验证筛选结果
//        for (OmsOrderDiyInfo diyInfo : result2) {
//            assertEquals((byte) 0, diyInfo.getProductionStatus());
//        }
//    }
//
//    @Test
//    public void testUpdateProductionStatus() {
//        // 创建DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        diyInfoService.create(diyInfo);
//
//        // 更新生产状态
//        int result = diyInfoService.updateProductionStatus(diyInfo.getId(), (byte) 2);
//        assertEquals(1, result);
//
//        // 验证更新结果
//        OmsOrderDiyInfo updatedDiyInfo = diyInfoService.getItem(diyInfo.getId());
//        assertEquals((byte) 2, updatedDiyInfo.getProductionStatus());
//    }
//
//    @Test
//    public void testBatchUpdateProductionStatus() {
//        // 创建多个DIY信息
//        OmsOrderDiyInfo diyInfo1 = createTestDiyInfo();
//        diyInfoService.create(diyInfo1);
//
//        OmsOrderDiyInfo diyInfo2 = createTestDiyInfo();
//        diyInfoService.create(diyInfo2);
//
//        // 批量更新生产状态
//        List<Long> ids = Arrays.asList(diyInfo1.getId(), diyInfo2.getId());
//        int result = diyInfoService.updateProductionStatus(ids, (byte) 1);
//        assertEquals(2, result);
//
//        // 验证更新结果
//        OmsOrderDiyInfo updatedDiyInfo1 = diyInfoService.getItem(diyInfo1.getId());
//        OmsOrderDiyInfo updatedDiyInfo2 = diyInfoService.getItem(diyInfo2.getId());
//        assertEquals((byte) 1, updatedDiyInfo1.getProductionStatus());
//        assertEquals((byte) 1, updatedDiyInfo2.getProductionStatus());
//    }
//
//    @Test
//    public void testDownloadDesignFile() {
//        // 创建DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        diyInfoService.create(diyInfo);
//
//        // 下载设计文件
//        String fileUrl = diyInfoService.downloadDesignFile(diyInfo.getId());
//        assertNotNull(fileUrl);
//        assertEquals(diyInfo.getPreviewImage(), fileUrl);
//    }
//
//    @Test
//    public void testGenerateProductionFile() {
//        // 创建DIY信息
//        OmsOrderDiyInfo diyInfo = createTestDiyInfo();
//        diyInfoService.create(diyInfo);
//
//        // 生成生产文件
//        String fileUrl = diyInfoService.generateProductionFile(diyInfo.getId());
//        assertNotNull(fileUrl);
//
//        // 验证生产状态已更新
//        OmsOrderDiyInfo updatedDiyInfo = diyInfoService.getItem(diyInfo.getId());
//        assertEquals((byte) 1, updatedDiyInfo.getProductionStatus());
//    }
//
//    @Test
//    public void testCountByProductionStatus() {
//        // 创建不同生产状态的DIY信息
//        OmsOrderDiyInfo diyInfo1 = createTestDiyInfo();
//        diyInfo1.setProductionStatus((byte) 0);
//        diyInfoService.create(diyInfo1);
//
//        OmsOrderDiyInfo diyInfo2 = createTestDiyInfo();
//        diyInfo2.setProductionStatus((byte) 1);
//        diyInfoService.create(diyInfo2);
//
//        OmsOrderDiyInfo diyInfo3 = createTestDiyInfo();
//        diyInfo3.setProductionStatus((byte) 0);
//        diyInfoService.create(diyInfo3);
//
//        // 统计待生产数量
//        Long count0 = diyInfoService.countByProductionStatus((byte) 0);
//        assertTrue(count0 >= 2);
//
//        // 统计生产中数量
//        Long count1 = diyInfoService.countByProductionStatus((byte) 1);
//        assertTrue(count1 >= 1);
//
//        // 统计总数量
//        Long totalCount = diyInfoService.countByProductionStatus(null);
//        assertTrue(totalCount >= 3);
//    }
//
//    /**
//     * 创建测试DIY信息
//     */
//    private OmsOrderDiyInfo createTestDiyInfo() {
//        OmsOrderDiyInfo diyInfo = new OmsOrderDiyInfo();
//        diyInfo.setOrderId(1000L);
//        diyInfo.setOrderItemId(1L);
//        diyInfo.setOrderSn("TEST" + System.currentTimeMillis());
//        diyInfo.setProductId(1L);
//        diyInfo.setProductName("测试商品");
//        diyInfo.setTemplateId(1L);
//        diyInfo.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"测试文字\"}]}");
//        diyInfo.setPreviewImage("http://test.com/preview.jpg");
//        diyInfo.setProductionStatus((byte) 0);
//        return diyInfo;
//    }
//}
