package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.dto.DiyPageDTO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.DiyDecorateService;
import com.macro.mall.service.OssService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiyDecorateServiceImpl implements DiyDecorateService {

    @Autowired private DiyComponentLibraryMapper componentMapper;
    @Autowired private DiyPageMapper pageMapper;
    @Autowired private DiyPageVersionMapper versionMapper;
    @Autowired private DiyPublishRecordMapper publishMapper;
    /** 使用腾讯云 COS 实现，避免 OssService 多实现导致的注入歧义 */
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("cosServiceImpl")
    private OssService ossService;

    @Override
    public List<DiyComponentLibrary> listComponents() {
        DiyComponentLibraryExample ex = new DiyComponentLibraryExample();
        ex.setOrderByClause("id asc");
        return componentMapper.selectByExampleWithBLOBs(ex);
    }

    @Override
    public CommonPage<DiyPageDTO> listPages(Long storeId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        DiyPageExample ex = new DiyPageExample();
        ex.createCriteria().andStoreIdEqualTo(storeId);
        ex.setOrderByClause("update_time desc");
        List<DiyPage> list = pageMapper.selectByExample(ex);
        List<DiyPageDTO> dtoList = list.stream().map(this::toDTO).collect(Collectors.toList());
        return CommonPage.restPage(dtoList);
    }

    @Override
    public DiyPageDTO createPage(DiyPageDTO page) {
        DiyPage entity = new DiyPage();
        BeanUtils.copyProperties(page,entity);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        pageMapper.insertSelective(entity);
        return toDTO(entity);
    }

    @Override
    public DiyPageDTO getPage(Long id) {
        DiyPage page = pageMapper.selectByPrimaryKey(id);
        if(page==null) return null;
        DiyPageVersionExample vEx = new DiyPageVersionExample();
        vEx.setOrderByClause("version desc limit 1");
        vEx.createCriteria().andPageIdEqualTo(id);
        List<DiyPageVersion> vs = versionMapper.selectByExampleWithBLOBs(vEx);
        DiyPageDTO dto = toDTO(page);
        if(!vs.isEmpty()) dto.setDslJson(vs.get(0).getDslJson());
        return dto;
    }

    @Override
    public DiyPageDTO saveDraft(Long id, DiyPageDTO page) {
        DiyPageVersion ver = new DiyPageVersion();
        ver.setPageId(id);
        ver.setVersion(page.getVersion());
        ver.setDslJson(page.getDslJson());
        ver.setCreator("admin");
        ver.setCreateTime(new Date());
        versionMapper.insertSelective(ver);
        DiyPage upd = new DiyPage();
        upd.setId(id);
        upd.setLatestVersion(page.getVersion());
        upd.setUpdateTime(new Date());
        pageMapper.updateByPrimaryKeySelective(upd);
        return getPage(id);
    }

    @Override
    public void publish(Long id) {
        DiyPage page = pageMapper.selectByPrimaryKey(id);
        if(page==null) return;
        DiyPublishRecord rec = new DiyPublishRecord();
        rec.setPageId(id);
        rec.setVersion(page.getLatestVersion());
        rec.setOperator("admin");
        rec.setPublishTime(new Date());
        publishMapper.insertSelective(rec);
        DiyPage upd = new DiyPage();
        upd.setId(id);
        upd.setStatus((byte)1);
        upd.setPublishVersion(page.getLatestVersion());
        upd.setUpdateTime(new Date());
        pageMapper.updateByPrimaryKeySelective(upd);
    }

    @Override
    public String getPublishedDsl(Long storeId) {
        DiyPageExample ex = new DiyPageExample();
        ex.createCriteria().andStoreIdEqualTo(storeId).andPageTypeEqualTo((byte)1);
        List<DiyPage> list = pageMapper.selectByExample(ex);
        if(list.isEmpty()) return null;
        DiyPage home = list.get(0);
        DiyPageVersionExample vEx = new DiyPageVersionExample();
        vEx.createCriteria().andPageIdEqualTo(home.getId()).andVersionEqualTo(home.getPublishVersion());
        List<DiyPageVersion> vs = versionMapper.selectByExampleWithBLOBs(vEx);
        return vs.isEmpty()?null:vs.get(0).getDslJson();
    }

    @Override
    public java.util.Map<String, Object> initInfo() {
        java.util.Map<String,Object> map = new java.util.HashMap<>();
        map.put("site_name","Guanghengzhou Mall DIY");
        map.put("default_lang","zh-cn");
        java.util.Map<String,Object> uploadCfg = new java.util.HashMap<>();
        uploadCfg.put("maxSize",2048);
        uploadCfg.put("exts","jpg,png,mp4");
        map.put("upload_config",uploadCfg);
        // 附件（图片/视频）访问域名，供前端资源加载
        String attachmentHost = "http://localhost:8201/mall-admin"; // TODO 可改为配置项或者 CDN 域名

        // 老版 shopxo 前端读取 res.data.attachment_host
        map.put("attachment_host", attachmentHost);

        // 新版 online_url() 首选 res.data.config.attachment_host
        java.util.Map<String,Object> cfg = new java.util.HashMap<>();
        cfg.put("attachment_host", attachmentHost);
        // 地图/支付等配置占位（如有实际值可改）
        cfg.put("common_amap_map_ak", "");
        cfg.put("common_amap_map_safety_ak", "");
        cfg.put("common_baidu_map_ak", "");
        cfg.put("common_tencent_map_ak", "");
        cfg.put("common_tianditu_map_ak", "");
        cfg.put("currency_symbol", "¥");
        cfg.put("store_diy_url", "");
        cfg.put("site_name", "Guanghengzhou Mall DIY");
        map.put("config", cfg);

        // 其它常用数据结构，默认空数组/对象，避免前端空指针
        map.put("article_category", java.util.Collections.emptyList());
        map.put("blog_category", java.util.Collections.emptyList());
        map.put("attachment_category", java.util.Collections.emptyList());
        map.put("brand_category", java.util.Collections.emptyList());
        map.put("brand_list", java.util.Collections.emptyList());
        map.put("goods_category", java.util.Collections.emptyList());
        map.put("module_list", java.util.Collections.emptyList());
        map.put("page_link_list", java.util.Collections.emptyList());
        map.put("plugins", new java.util.HashMap<>());
        // 排序等列表
        map.put("article_order_by_type_list", java.util.Collections.emptyList());
        map.put("blog_order_by_type_list", java.util.Collections.emptyList());
        map.put("goods_order_by_type_list", java.util.Collections.emptyList());
        map.put("data_order_by_rule_list", java.util.Collections.emptyList());
        map.put("brand_order_by_type_list", java.util.Collections.emptyList());
        return map;
    }

    @Override
    public String uploadToCos(MultipartFile file) {
        return ossService.uploadFileToCos(file);
    }

    private DiyPageDTO toDTO(DiyPage page){
        DiyPageDTO dto = new DiyPageDTO();
        BeanUtils.copyProperties(page,dto);
        return dto;
    }
} 