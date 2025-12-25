package com.macro.mall.service;

import com.macro.mall.model.PmsDiyTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DIY模板管理Service测试
 * Created by macro on 2024/12/20.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PmsDiyTemplateServiceTest {
    
    @Autowired
    private PmsDiyTemplateService templateService;

    @Test
    public void testCreate() {
        PmsDiyTemplate template = new PmsDiyTemplate();
        template.setName("测试模板");
        template.setProductCategoryId(1L);
        template.setDescription("这是一个测试模板");
        template.setStatus((byte) 1);
        
        int result = templateService.create(template);
        assertEquals(1, result);
        assertNotNull(template.getId());
        assertNotNull(template.getCreateTime());
        assertNotNull(template.getUpdateTime());
    }

    @Test
    public void testUpdate() {
        // 先创建一个模板
        PmsDiyTemplate template = new PmsDiyTemplate();
        template.setName("原始模板");
        template.setDescription("原始描述");
        templateService.create(template);
        
        // 更新模板
        PmsDiyTemplate updateTemplate = new PmsDiyTemplate();
        updateTemplate.setName("更新后的模板");
        updateTemplate.setDescription("更新后的描述");
        
        int result = templateService.update(template.getId(), updateTemplate);
        assertEquals(1, result);
        
        // 验证更新结果
        PmsDiyTemplate updatedTemplate = templateService.getItem(template.getId());
        assertEquals("更新后的模板", updatedTemplate.getName());
        assertEquals("更新后的描述", updatedTemplate.getDescription());
    }

    @Test
    public void testDelete() {
        // 先创建一个模板
        PmsDiyTemplate template = new PmsDiyTemplate();
        template.setName("待删除模板");
        templateService.create(template);
        
        // 删除模板
        int result = templateService.delete(template.getId());
        assertEquals(1, result);
        
        // 验证删除结果
        PmsDiyTemplate deletedTemplate = templateService.getItem(template.getId());
        assertNull(deletedTemplate);
    }

    @Test
    public void testBatchDelete() {
        // 创建多个模板
        PmsDiyTemplate template1 = new PmsDiyTemplate();
        template1.setName("模板1");
        templateService.create(template1);
        
        PmsDiyTemplate template2 = new PmsDiyTemplate();
        template2.setName("模板2");
        templateService.create(template2);
        
        // 批量删除
        List<Long> ids = Arrays.asList(template1.getId(), template2.getId());
        int result = templateService.delete(ids);
        assertEquals(2, result);
    }

    @Test
    public void testList() {
        // 创建测试数据
        PmsDiyTemplate template1 = new PmsDiyTemplate();
        template1.setName("搜索模板1");
        template1.setProductCategoryId(1L);
        template1.setStatus((byte) 1);
        templateService.create(template1);
        
        PmsDiyTemplate template2 = new PmsDiyTemplate();
        template2.setName("搜索模板2");
        template2.setProductCategoryId(2L);
        template2.setStatus((byte) 0);
        templateService.create(template2);
        
        // 测试关键词搜索
        List<PmsDiyTemplate> result1 = templateService.list("搜索", null, null, 10, 1);
        assertTrue(result1.size() >= 2);
        
        // 测试分类筛选
        List<PmsDiyTemplate> result2 = templateService.list(null, 1L, null, 10, 1);
        assertTrue(result2.size() >= 1);
        
        // 测试状态筛选
        List<PmsDiyTemplate> result3 = templateService.list(null, null, (byte) 1, 10, 1);
        assertTrue(result3.size() >= 1);
    }

    @Test
    public void testListEnabled() {
        // 创建启用和禁用的模板
        PmsDiyTemplate enabledTemplate = new PmsDiyTemplate();
        enabledTemplate.setName("启用模板");
        enabledTemplate.setStatus((byte) 1);
        templateService.create(enabledTemplate);
        
        PmsDiyTemplate disabledTemplate = new PmsDiyTemplate();
        disabledTemplate.setName("禁用模板");
        disabledTemplate.setStatus((byte) 0);
        templateService.create(disabledTemplate);
        
        // 获取启用的模板列表
        List<PmsDiyTemplate> enabledList = templateService.listEnabled();
        assertTrue(enabledList.size() >= 1);
        
        // 验证所有返回的模板都是启用状态
        for (PmsDiyTemplate template : enabledList) {
            assertEquals((byte) 1, template.getStatus());
        }
    }

    @Test
    public void testUpdateStatus() {
        // 创建模板
        PmsDiyTemplate template1 = new PmsDiyTemplate();
        template1.setName("状态测试模板1");
        template1.setStatus((byte) 1);
        templateService.create(template1);
        
        PmsDiyTemplate template2 = new PmsDiyTemplate();
        template2.setName("状态测试模板2");
        template2.setStatus((byte) 1);
        templateService.create(template2);
        
        // 批量修改状态
        List<Long> ids = Arrays.asList(template1.getId(), template2.getId());
        int result = templateService.updateStatus(ids, (byte) 0);
        assertEquals(2, result);
        
        // 验证状态修改结果
        PmsDiyTemplate updatedTemplate1 = templateService.getItem(template1.getId());
        PmsDiyTemplate updatedTemplate2 = templateService.getItem(template2.getId());
        assertEquals((byte) 0, updatedTemplate1.getStatus());
        assertEquals((byte) 0, updatedTemplate2.getStatus());
    }

    @Test
    public void testCopyTemplate() {
        // 创建原始模板
        PmsDiyTemplate sourceTemplate = new PmsDiyTemplate();
        sourceTemplate.setName("原始模板");
        sourceTemplate.setProductCategoryId(1L);
        sourceTemplate.setDescription("原始描述");
        templateService.create(sourceTemplate);
        
        // 复制模板
        String newName = "复制的模板";
        int result = templateService.copyTemplate(sourceTemplate.getId(), newName);
        assertEquals(1, result);
        
        // 验证复制结果
        List<PmsDiyTemplate> templates = templateService.list(newName, null, null, 10, 1);
        assertTrue(templates.size() >= 1);
        
        PmsDiyTemplate copiedTemplate = templates.get(0);
        assertEquals(newName, copiedTemplate.getName());
        assertEquals(sourceTemplate.getProductCategoryId(), copiedTemplate.getProductCategoryId());
        assertEquals(sourceTemplate.getDescription(), copiedTemplate.getDescription());
    }

    @Test
    public void testListByProductCategory() {
        // 创建不同分类的模板
        PmsDiyTemplate template1 = new PmsDiyTemplate();
        template1.setName("分类1模板");
        template1.setProductCategoryId(1L);
        template1.setStatus((byte) 1);
        templateService.create(template1);
        
        PmsDiyTemplate template2 = new PmsDiyTemplate();
        template2.setName("分类2模板");
        template2.setProductCategoryId(2L);
        template2.setStatus((byte) 1);
        templateService.create(template2);
        
        // 测试按分类查询
        List<PmsDiyTemplate> category1Templates = templateService.listByProductCategory(1L);
        assertTrue(category1Templates.size() >= 1);
        
        for (PmsDiyTemplate template : category1Templates) {
            assertEquals(1L, template.getProductCategoryId());
            assertEquals((byte) 1, template.getStatus());
        }
    }
}
