//package com.macro.mall.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.macro.mall.dto.PmsDiyMaterialCategoryParam;
//import com.macro.mall.dto.PmsDiyMaterialParam;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * DIY素材管理Controller集成测试
// * Created by macro on 2024/12/20.
// */
//@SpringBootTest
//@AutoConfigureTestMvc
//@ActiveProfiles("dev")
//public class PmsDiyMaterialControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testCreateCategory() throws Exception {
//        // 测试创建素材分类
//        PmsDiyMaterialCategoryParam categoryParam = new PmsDiyMaterialCategoryParam();
//        categoryParam.setName("测试图片分类");
//        categoryParam.setType((byte) 1);
//        categoryParam.setIcon("test-icon.png");
//        categoryParam.setSort(1);
//        categoryParam.setStatus((byte) 1);
//
//        mockMvc.perform(post("/diyMaterialCategory/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(categoryParam)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//
//    @Test
//    public void testListCategories() throws Exception {
//        // 测试查询分类列表
//        mockMvc.perform(get("/diyMaterialCategory/list")
//                .param("pageNum", "1")
//                .param("pageSize", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//
//    @Test
//    public void testCreateMaterial() throws Exception {
//        // 测试创建素材
//        PmsDiyMaterialParam materialParam = new PmsDiyMaterialParam();
//        materialParam.setCategoryId(1L);
//        materialParam.setName("测试图片素材");
//        materialParam.setFileUrl("http://example.com/test.png");
//        materialParam.setFileType("png");
//        materialParam.setFileSize(1024L);
//        materialParam.setTags("测试,图片");
//        materialParam.setStatus((byte) 1);
//
//        mockMvc.perform(post("/diyMaterial/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(materialParam)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//
//    @Test
//    public void testListMaterials() throws Exception {
//        // 测试查询素材列表
//        mockMvc.perform(get("/diyMaterial/list")
//                .param("pageNum", "1")
//                .param("pageSize", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//
//    @Test
//    public void testListEnabledCategories() throws Exception {
//        // 测试获取启用的分类列表
//        mockMvc.perform(get("/diyMaterialCategory/listEnabled"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//}