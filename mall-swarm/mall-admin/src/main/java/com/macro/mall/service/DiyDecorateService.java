package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.dto.DiyPageDTO;
import com.macro.mall.model.DiyComponentLibrary;

import java.util.List;

public interface DiyDecorateService {
    List<DiyComponentLibrary> listComponents();

    CommonPage<DiyPageDTO> listPages(Long storeId,Integer pageNum,Integer pageSize);

    DiyPageDTO createPage(DiyPageDTO page);

    DiyPageDTO getPage(Long id);

    DiyPageDTO saveDraft(Long id,DiyPageDTO page);

    void publish(Long id);

    String getPublishedDsl(Long storeId);

    java.util.Map<String,Object> initInfo();

    String uploadToCos(org.springframework.web.multipart.MultipartFile file);
} 