package com.macro.mall.dto;

import com.macro.mall.model.DecPage;

/**
 * 页面 DTO：在 DecPage 基础上补充 content 字段，用于向前端返回/接收 DSL。
 * 保持 MyBatis-Generator 生成的 DecPage 不被修改。
 */
public class DecPageDTO extends DecPage {
    /** 页面 DSL 内容（JSON） */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 