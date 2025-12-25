//package com.macro.mall.portal;
//
//import com.macro.mall.common.api.CommonPage;
//import com.macro.mall.common.api.CommonResult;
//import com.macro.mall.model.PmsProduct;
//import com.macro.mall.portal.controller.PmsPortalProductController;
//import com.macro.mall.portal.service.PmsPortalProductService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 商品搜索分页功能测试
// * 验证修复后的分页逻辑是否正确
// */
//@SpringBootTest
//@ActiveProfiles("test")
//public class PmsPortalProductServiceTests {
//
//    @Autowired
//    private PmsPortalProductService portalProductService;
//
//    @Autowired
//    private PmsPortalProductController portalProductController;
//
//    @Test
//    public void testSearchWithoutSchoolId() {
//        // 测试没有学校筛选的情况
//        List<PmsProduct> products = portalProductService.search(null, null, null, 1, 10, 0, null);
//        assertNotNull(products);
//
//        // 通过Controller测试分页信息
//        CommonResult<CommonPage<PmsProduct>> result = portalProductController.search(null, null, null, 1, 10, 0, null);
//        assertNotNull(result);
//        assertTrue(result.getCode() == 200);
//
//        CommonPage<PmsProduct> page = result.getData();
//        assertNotNull(page);
//        assertEquals(1, page.getPageNum());
//        assertEquals(10, page.getPageSize());
//        assertNotNull(page.getTotal());
//        assertNotNull(page.getTotalPage());
//
//        System.out.println("无学校筛选 - 总数: " + page.getTotal() + ", 总页数: " + page.getTotalPage());
//    }
//
//    @Test
//    public void testSearchWithSchoolId() {
//        // 测试有学校筛选的情况
//        Long schoolId = 1L; // 假设学校ID为1
//
//        // 先获取总数
//        long totalCount = portalProductService.searchCount(null, null, null, schoolId);
//        System.out.println("学校ID " + schoolId + " 关联的商品总数: " + totalCount);
//
//        // 测试第一页
//        List<PmsProduct> products = portalProductService.search(null, null, null, 1, 10, 0, schoolId);
//        assertNotNull(products);
//
//        // 通过Controller测试分页信息
//        CommonResult<CommonPage<PmsProduct>> result = portalProductController.search(null, null, null, 1, 10, 0, schoolId);
//        assertNotNull(result);
//        assertTrue(result.getCode() == 200);
//
//        CommonPage<PmsProduct> page = result.getData();
//        assertNotNull(page);
//        assertEquals(1, page.getPageNum());
//        assertEquals(10, page.getPageSize());
//        assertEquals(totalCount, page.getTotal());
//
//        // 验证总页数计算是否正确
//        int expectedTotalPage = (int) Math.ceil((double) totalCount / 10);
//        assertEquals(expectedTotalPage, page.getTotalPage());
//
//        System.out.println("有学校筛选 - 总数: " + page.getTotal() + ", 总页数: " + page.getTotalPage());
//        System.out.println("当前页商品数量: " + page.getList().size());
//
//        // 如果总数大于10，测试第二页
//        if (totalCount > 10) {
//            CommonResult<CommonPage<PmsProduct>> result2 = portalProductController.search(null, null, null, 2, 10, 0, schoolId);
//            CommonPage<PmsProduct> page2 = result2.getData();
//            assertEquals(2, page2.getPageNum());
//            assertEquals(totalCount, page2.getTotal()); // 总数应该保持一致
//            assertEquals(expectedTotalPage, page2.getTotalPage()); // 总页数应该保持一致
//
//            System.out.println("第二页商品数量: " + page2.getList().size());
//        }
//    }
//
//    @Test
//    public void testSearchCountConsistency() {
//        // 测试搜索总数的一致性
//        Long schoolId = 1L;
//
//        // 获取总数
//        long totalCount = portalProductService.searchCount(null, null, null, schoolId);
//
//        // 获取所有商品（使用一个很大的pageSize）
//        List<PmsProduct> allProducts = portalProductService.search(null, null, null, 1, 1000, 0, schoolId);
//
//        // 验证总数是否一致
//        assertEquals(totalCount, allProducts.size());
//
//        System.out.println("总数一致性验证 - searchCount: " + totalCount + ", 实际查询数量: " + allProducts.size());
//    }
//
//    @Test
//    public void testSearchWithKeywordAndSchoolId() {
//        // 测试关键词搜索 + 学校筛选
//        String keyword = "商品"; // 假设有包含"商品"的商品名称
//        Long schoolId = 1L;
//
//        long totalCount = portalProductService.searchCount(keyword, null, null, schoolId);
//
//        CommonResult<CommonPage<PmsProduct>> result = portalProductController.search(keyword, null, null, 1, 5, 0, schoolId);
//        CommonPage<PmsProduct> page = result.getData();
//
//        assertEquals(totalCount, page.getTotal());
//
//        System.out.println("关键词搜索 + 学校筛选 - 关键词: " + keyword + ", 总数: " + page.getTotal());
//    }
//}
