package com.macro.mall.service;

import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyMaterialCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * DIY素材管理Service测试类
 * Created by macro on 2024/12/20.
 */
@SpringBootTest
@ActiveProfiles("dev")
public class PmsDiyMaterialServiceTest {

    @Autowired
    private PmsDiyMaterialCategoryService categoryService;

    @Autowired
    private PmsDiyMaterialService materialService;

    @Test
    public void testCreateCategory() {
        // 测试创建素材分类
        PmsDiyMaterialCategory category = new PmsDiyMaterialCategory();
        category.setName("测试图片分类");
        category.setType((byte) 1);
        category.setIcon("test-icon.png");
        category.setSort(1);
        category.setStatus((byte) 1);
        
        int result = categoryService.create(category);
        System.out.println("创建分类结果: " + result);
        System.out.println("分类ID: " + category.getId());
    }

    @Test
    public void testCreateMaterial() {
        // 测试创建素材
        PmsDiyMaterial material = new PmsDiyMaterial();
        material.setCategoryId(1L);
        material.setName("测试图片素材");
        material.setFileUrl("http://example.com/test.png");
        material.setFileType("png");
        material.setFileSize(1024L);
        material.setTags("测试,图片");
        material.setStatus((byte) 1);
        
        int result = materialService.create(material);
        System.out.println("创建素材结果: " + result);
        System.out.println("素材ID: " + material.getId());
    }

    @Test
    public void testListCategories() {
        // 测试查询分类列表
        List<PmsDiyMaterialCategory> categories = categoryService.list(null, null, 1, 10);
        System.out.println("分类数量: " + categories.size());
        for (PmsDiyMaterialCategory category : categories) {
            System.out.println("分类: " + category.getName() + ", 类型: " + category.getType());
        }
    }

    @Test
    public void testListMaterials() {
        // 测试查询素材列表
        List<PmsDiyMaterial> materials = materialService.list(null, null, null, 1, 10);
        System.out.println("素材数量: " + materials.size());
        for (PmsDiyMaterial material : materials) {
            System.out.println("素材: " + material.getName() + ", 文件类型: " + material.getFileType());
        }
    }
}