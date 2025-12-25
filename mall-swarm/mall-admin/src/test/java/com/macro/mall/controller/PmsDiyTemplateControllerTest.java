package com.macro.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.dto.PmsDiyTemplateParam;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.service.PmsDiyTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DIY模板管理Controller测试
 * Created by macro on 2024/12/20.
 */
@WebMvcTest(PmsDiyTemplateController.class)
public class PmsDiyTemplateControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PmsDiyTemplateService templateService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private PmsDiyTemplate testTemplate;
    private PmsDiyTemplateParam testTemplateParam;

    @BeforeEach
    public void setUp() {
        testTemplate = new PmsDiyTemplate();
        testTemplate.setId(1L);
        testTemplate.setName("测试模板");
        testTemplate.setProductCategoryId(1L);
        testTemplate.setDescription("测试描述");
        testTemplate.setStatus((byte) 1);
        testTemplate.setCreateTime(new Date());
        testTemplate.setUpdateTime(new Date());
        
        testTemplateParam = new PmsDiyTemplateParam();
        testTemplateParam.setName("测试模板");
        testTemplateParam.setProductCategoryId(1L);
        testTemplateParam.setDescription("测试描述");
        testTemplateParam.setStatus((byte) 1);
    }

    @Test
    public void testCreate() throws Exception {
        when(templateService.create(any(PmsDiyTemplate.class))).thenReturn(1);
        
        mockMvc.perform(post("/diyTemplate/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTemplateParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testCreateWithInvalidParam() throws Exception {
        PmsDiyTemplateParam invalidParam = new PmsDiyTemplateParam();
        // 不设置必填字段name
        
        mockMvc.perform(post("/diyTemplate/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidParam)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        when(templateService.update(eq(1L), any(PmsDiyTemplate.class))).thenReturn(1);
        
        mockMvc.perform(post("/diyTemplate/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTemplateParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    public void testDelete() throws Exception {
        when(templateService.delete(1L)).thenReturn(1);
        
        mockMvc.perform(post("/diyTemplate/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    public void testDeleteBatch() throws Exception {
        when(templateService.delete(anyList())).thenReturn(2);
        
        mockMvc.perform(post("/diyTemplate/delete/batch")
                .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    public void testGetItem() throws Exception {
        when(templateService.getItem(1L)).thenReturn(testTemplate);
        
        mockMvc.perform(get("/diyTemplate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试模板"));
    }

    @Test
    public void testList() throws Exception {
        List<PmsDiyTemplate> templateList = Arrays.asList(testTemplate);
        when(templateService.list(anyString(), anyLong(), any(Byte.class), anyInt(), anyInt()))
                .thenReturn(templateList);
        
        mockMvc.perform(get("/diyTemplate/list")
                .param("keyword", "测试")
                .param("productCategoryId", "1")
                .param("status", "1")
                .param("pageNum", "1")
                .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].name").value("测试模板"));
    }

    @Test
    public void testListEnabled() throws Exception {
        List<PmsDiyTemplate> enabledTemplates = Arrays.asList(testTemplate);
        when(templateService.listEnabled()).thenReturn(enabledTemplates);
        
        mockMvc.perform(get("/diyTemplate/listEnabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("测试模板"));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        when(templateService.updateStatus(anyList(), any(Byte.class))).thenReturn(2);
        
        mockMvc.perform(post("/diyTemplate/update/status")
                .param("ids", "1,2")
                .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    public void testCopyTemplate() throws Exception {
        when(templateService.copyTemplate(1L, "复制的模板")).thenReturn(1);
        
        mockMvc.perform(post("/diyTemplate/copy/1")
                .param("newName", "复制的模板"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    public void testListByProductCategory() throws Exception {
        List<PmsDiyTemplate> categoryTemplates = Arrays.asList(testTemplate);
        when(templateService.listByProductCategory(1L)).thenReturn(categoryTemplates);
        
        mockMvc.perform(get("/diyTemplate/listByProductCategory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productCategoryId").value(1));
    }
}
