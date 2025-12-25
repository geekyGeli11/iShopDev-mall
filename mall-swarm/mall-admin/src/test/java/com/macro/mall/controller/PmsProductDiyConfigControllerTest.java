package com.macro.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.dto.PmsProductDiyConfigParam;
import com.macro.mall.dto.PmsProductDiyConfigVO;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.service.PmsProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商品DIY配置Controller测试
 * Created by macro on 2024/12/20.
 */
@WebMvcTest(PmsProductController.class)
public class PmsProductDiyConfigControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PmsProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private PmsProductDiyConfigVO testConfigVO;
    private PmsProductDiyConfigParam testConfigParam;

    @BeforeEach
    public void setUp() {
        // 创建测试模板
        PmsDiyTemplate template = new PmsDiyTemplate();
        template.setId(1L);
        template.setName("测试DIY模板");
        template.setDescription("测试模板描述");
        template.setStatus((byte) 1);
        
        // 创建测试配置VO
        testConfigVO = new PmsProductDiyConfigVO();
        testConfigVO.setId(1L);
        testConfigVO.setName("测试商品");
        testConfigVO.setDiyEnabled((byte) 1);
        testConfigVO.setDiyTemplateId(1L);
        testConfigVO.setDiyTemplate(template);
        
        // 创建测试配置参数
        testConfigParam = new PmsProductDiyConfigParam();
        testConfigParam.setDiyEnabled((byte) 1);
        testConfigParam.setDiyTemplateId(1L);
    }

    @Test
    public void testGetDiyConfig() throws Exception {
        when(productService.getDiyConfigDetail(1L)).thenReturn(testConfigVO);
        
        mockMvc.perform(get("/product/diyConfig/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试商品"))
                .andExpect(jsonPath("$.data.diyEnabled").value(1))
                .andExpect(jsonPath("$.data.diyTemplateId").value(1))
                .andExpect(jsonPath("$.data.diyTemplate.name").value("测试DIY模板"));
    }

    @Test
    public void testGetDiyConfigNotFound() throws Exception {
        when(productService.getDiyConfigDetail(999L)).thenReturn(null);
        
        mockMvc.perform(get("/product/diyConfig/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testUpdateDiyConfig() throws Exception {
        when(productService.updateDiyConfig(eq(1L), eq((byte) 1), eq(1L))).thenReturn(1);
        
        mockMvc.perform(post("/product/diyConfig/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testConfigParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    public void testUpdateDiyConfigWithInvalidParam() throws Exception {
        PmsProductDiyConfigParam invalidParam = new PmsProductDiyConfigParam();
        // 不设置必填字段diyEnabled
        
        mockMvc.perform(post("/product/diyConfig/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidParam)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateDiyConfigDisable() throws Exception {
        PmsProductDiyConfigParam disableParam = new PmsProductDiyConfigParam();
        disableParam.setDiyEnabled((byte) 0);
        disableParam.setDiyTemplateId(null);
        
        when(productService.updateDiyConfig(eq(1L), eq((byte) 0), isNull())).thenReturn(1);
        
        mockMvc.perform(post("/product/diyConfig/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(disableParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    public void testUpdateDiyConfigFailed() throws Exception {
        when(productService.updateDiyConfig(eq(1L), eq((byte) 1), eq(1L))).thenReturn(0);
        
        mockMvc.perform(post("/product/diyConfig/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testConfigParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("操作失败"));
    }

    @Test
    public void testUpdateDiyStatus() throws Exception {
        when(productService.updateDiyStatus(anyList(), eq((byte) 1))).thenReturn(2);
        
        mockMvc.perform(post("/product/diyConfig/updateStatus")
                .param("ids", "1,2")
                .param("diyEnabled", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    public void testUpdateDiyStatusDisable() throws Exception {
        when(productService.updateDiyStatus(anyList(), eq((byte) 0))).thenReturn(2);
        
        mockMvc.perform(post("/product/diyConfig/updateStatus")
                .param("ids", "1,2")
                .param("diyEnabled", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    public void testUpdateDiyStatusFailed() throws Exception {
        when(productService.updateDiyStatus(anyList(), eq((byte) 1))).thenReturn(0);
        
        mockMvc.perform(post("/product/diyConfig/updateStatus")
                .param("ids", "1,2")
                .param("diyEnabled", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("操作失败"));
    }

    @Test
    public void testUpdateDiyStatusEmptyIds() throws Exception {
        when(productService.updateDiyStatus(anyList(), eq((byte) 1))).thenReturn(0);
        
        mockMvc.perform(post("/product/diyConfig/updateStatus")
                .param("ids", "")
                .param("diyEnabled", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(0));
    }

    @Test
    public void testUpdateDiyStatusSingleId() throws Exception {
        when(productService.updateDiyStatus(anyList(), eq((byte) 1))).thenReturn(1);
        
        mockMvc.perform(post("/product/diyConfig/updateStatus")
                .param("ids", "1")
                .param("diyEnabled", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }
}
