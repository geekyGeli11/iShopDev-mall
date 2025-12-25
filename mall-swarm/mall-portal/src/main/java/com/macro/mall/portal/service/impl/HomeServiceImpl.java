package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.HomeDao;
import com.macro.mall.portal.domain.FlashPromotionProduct;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.domain.HomeFlashPromotion;
import com.macro.mall.portal.domain.ProductListDTO;
import com.macro.mall.portal.domain.SubjectDetailResult;
import com.macro.mall.portal.domain.MemberProductCollection;
import com.macro.mall.portal.repository.MemberProductCollectionRepository;
import com.macro.mall.portal.service.HomeService;
import com.macro.mall.portal.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 首页内容管理Service实现类
 * Created by macro on 2019/1/28.
 */
@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private SmsHomeAdvertiseMapper advertiseMapper;
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private SmsFlashPromotionMapper flashPromotionMapper;
    @Autowired
    private SmsFlashPromotionSessionMapper promotionSessionMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private CmsSubjectMapper subjectMapper;
    @Autowired
    private SmsHomeRecommendSubjectMapper smsHomeRecommendSubjectMapper;
    @Autowired
    private MemberProductCollectionRepository memberProductCollectionRepository;

    @Override
    public HomeContentResult content() {
        return content(null);
    }

    @Override
    public HomeContentResult content(Long schoolId) {
        HomeContentResult result = new HomeContentResult();
        //获取首页广告（支持学校筛选）
        result.setAdvertiseList(getHomeAdvertiseList(schoolId));
        //获取秒杀信息（支持学校筛选）
        result.setHomeFlashPromotion(getHomeFlashPromotion(schoolId));
        //获取新品推荐（支持学校筛选）
        if (schoolId != null) {
            result.setNewProductList(homeDao.getNewProductList(0,10, schoolId));
        } else {
            result.setNewProductList(homeDao.getNewProductList(0,10));
        }
        //获取热销推荐（支持学校筛选）
        if (schoolId != null) {
            result.setHotProductList(homeDao.getHotProductListWithPrice(0,10, schoolId));
        } else {
            result.setHotProductList(homeDao.getHotProductListWithPrice(0,10));
        }
        return result;
    }

    @Override
    public List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum) {
        // TODO: 2019/1/29 暂时默认推荐所有商品
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andPublishStatusEqualTo(1);
        List<PmsProduct> products = productMapper.selectByExample(example);
        // 注意：PmsProduct实体类中的sale字段已经包含了销量信息，前端可以直接使用
        return products;
    }

    @Override
    public List<PmsProductCategory> getProductCateList(Long parentId) {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria()
                .andShowStatusEqualTo(1)
                .andParentIdEqualTo(parentId);
        example.setOrderByClause("sort desc");
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        CmsSubjectExample example = new CmsSubjectExample();
        CmsSubjectExample.Criteria criteria = example.createCriteria();
        criteria.andShowStatusEqualTo(1);
        if(cateId!=null){
            criteria.andCategoryIdEqualTo(cateId);
        }
        return subjectMapper.selectByExample(example);
    }

    @Override
    public List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize) {
        return hotProductList(pageNum, pageSize, null);
    }

    @Override
    public List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize, Long schoolId) {
        int offset = pageSize * (pageNum - 1);
        if (schoolId != null) {
            return homeDao.getHotProductListBySchool(offset, pageSize, schoolId);
        } else {
            return homeDao.getHotProductList(offset, pageSize);
        }
    }

    @Override
    public List<java.util.Map<String, Object>> hotProductListWithPrice(Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return homeDao.getHotProductListWithPrice(offset, pageSize);
    }

    @Override
    public List<com.macro.mall.portal.domain.NewProductDTO> newProductList(Integer pageNum, Integer pageSize) {
        return newProductList(pageNum, pageSize, null);
    }

    @Override
    public List<com.macro.mall.portal.domain.NewProductDTO> newProductList(Integer pageNum, Integer pageSize, Long schoolId) {
        int offset = pageSize * (pageNum - 1);
        if (schoolId != null) {
            return homeDao.getNewProductList(offset, pageSize, schoolId);
        } else {
            return homeDao.getNewProductList(offset, pageSize);
        }
    }

    @Override
    public List<ProductListDTO> productListBySales(Integer pageNum, Integer pageSize) {
        return productListBySales(pageNum, pageSize, null);
    }

    @Override
    public List<ProductListDTO> productListBySales(Integer pageNum, Integer pageSize, Long schoolId) {
        int offset = pageSize * (pageNum - 1);
        List<ProductListDTO> productList;
        if (schoolId != null) {
            productList = homeDao.getProductListBySales(offset, pageSize, schoolId);
        } else {
            productList = homeDao.getProductListBySales(offset, pageSize);
        }
        
        // 如果商品列表不为空，批量查询收藏数量
        if (!CollectionUtils.isEmpty(productList)) {
            // 提取商品ID列表
            List<Long> productIds = productList.stream()
                    .map(ProductListDTO::getId)
                    .collect(Collectors.toList());
            
            // 批量查询收藏记录
            List<MemberProductCollection> collections = memberProductCollectionRepository.findByProductIdIn(productIds);
            
            // 统计每个商品的收藏数量
            Map<Long, Long> collectCountMap = collections.stream()
                    .collect(Collectors.groupingBy(
                        MemberProductCollection::getProductId, 
                        Collectors.counting()
                    ));
            
            // 设置收藏数量到商品列表中
            productList.forEach(product -> {
                Long collectCount = collectCountMap.getOrDefault(product.getId(), 0L);
                product.setCollectCount(collectCount.intValue());
            });
        }
        
        return productList;
    }

    private HomeFlashPromotion getHomeFlashPromotion() {
        return getHomeFlashPromotion(null);
    }

    private HomeFlashPromotion getHomeFlashPromotion(Long schoolId) {
        HomeFlashPromotion homeFlashPromotion = new HomeFlashPromotion();
        Date now = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(now);
        SmsFlashPromotion flashPromotion = getFlashPromotion(now, schoolId);
        if (flashPromotion != null) {
            SmsFlashPromotionSession flashPromotionSession = getFlashPromotionSession(now);
            if (flashPromotionSession != null) {
                // 拼接当前场次时间
                String startDateTimeStr = today + " " + new SimpleDateFormat("HH:mm:ss").format(flashPromotionSession.getStartTime());
                String endDateTimeStr = today + " " + new SimpleDateFormat("HH:mm:ss").format(flashPromotionSession.getEndTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    homeFlashPromotion.setStartTime(sdf.parse(startDateTimeStr));
                    homeFlashPromotion.setEndTime(sdf.parse(endDateTimeStr));
                } catch (ParseException e) {
                    homeFlashPromotion.setStartTime(null);
                    homeFlashPromotion.setEndTime(null);
                }
                // 获取下一个秒杀场次
                SmsFlashPromotionSession nextSession = getNextFlashPromotionSession(homeFlashPromotion.getStartTime());
                if(nextSession != null){
                    // 直接复用 today
                    String nextStartDateTimeStr = today + " " + new SimpleDateFormat("HH:mm:ss").format(nextSession.getStartTime());
                    String nextEndDateTimeStr = today + " " + new SimpleDateFormat("HH:mm:ss").format(nextSession.getEndTime());
                    try {
                        homeFlashPromotion.setNextStartTime(sdf.parse(nextStartDateTimeStr));
                        homeFlashPromotion.setNextEndTime(sdf.parse(nextEndDateTimeStr));
                    } catch (ParseException e) {
                        homeFlashPromotion.setNextStartTime(null);
                        homeFlashPromotion.setNextEndTime(null);
                    }
                }
                //获取秒杀商品
                List<FlashPromotionProduct> flashProductList = homeDao.getFlashProductList(flashPromotion.getId(), flashPromotionSession.getId());
                homeFlashPromotion.setProductList(flashProductList);
            }
        }
        return homeFlashPromotion;
    }

    //获取下一个场次信息
    private SmsFlashPromotionSession getNextFlashPromotionSession(Date date) {
        SmsFlashPromotionSessionExample sessionExample = new SmsFlashPromotionSessionExample();
        sessionExample.createCriteria()
                .andStartTimeGreaterThan(date);
        sessionExample.setOrderByClause("start_time asc");
        List<SmsFlashPromotionSession> promotionSessionList = promotionSessionMapper.selectByExample(sessionExample);
        if (!CollectionUtils.isEmpty(promotionSessionList)) {
            return promotionSessionList.get(0);
        }
        return null;
    }

    private List<SmsHomeAdvertise> getHomeAdvertiseList() {
        return getHomeAdvertiseList(null);
    }

    private List<SmsHomeAdvertise> getHomeAdvertiseList(Long schoolId) {
        SmsHomeAdvertiseExample example = new SmsHomeAdvertiseExample();
        SmsHomeAdvertiseExample.Criteria criteria = example.createCriteria()
                .andTypeEqualTo(1)
                .andStatusEqualTo(1);

        // 添加学校筛选条件
        if (schoolId != null) {
            criteria.andSchoolIdEqualTo(schoolId);
        } else {
            // schoolId为空时，查询全平台通用的广告（school_id为NULL）
            criteria.andSchoolIdIsNull();
        }

        example.setOrderByClause("sort desc");
        return advertiseMapper.selectByExample(example);
    }

    //根据时间获取秒杀活动
    private SmsFlashPromotion getFlashPromotion(Date date) {
        return getFlashPromotion(date, null);
    }

    //根据时间获取秒杀活动（支持学校筛选）
    private SmsFlashPromotion getFlashPromotion(Date date, Long schoolId) {
        Date currDate = DateUtil.getDate(date);
        SmsFlashPromotionExample example = new SmsFlashPromotionExample();
        SmsFlashPromotionExample.Criteria criteria = example.createCriteria()
                .andStatusEqualTo(1)
                .andStartDateLessThanOrEqualTo(currDate)
                .andEndDateGreaterThanOrEqualTo(currDate);

        // 添加学校筛选条件
        if (schoolId != null) {
            criteria.andSchoolIdEqualTo(schoolId);
        } else {
            // schoolId为空时，查询全平台通用的秒杀活动（school_id为NULL）
            criteria.andSchoolIdIsNull();
        }

        List<SmsFlashPromotion> flashPromotionList = flashPromotionMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(flashPromotionList)) {
            return flashPromotionList.get(0);
        }
        return null;
    }

    //根据时间获取秒杀场次
    private SmsFlashPromotionSession getFlashPromotionSession(Date date) {
        Date currTime = DateUtil.getTime(date);
        SmsFlashPromotionSessionExample sessionExample = new SmsFlashPromotionSessionExample();
        sessionExample.createCriteria()
                .andStartTimeLessThanOrEqualTo(currTime)
                .andEndTimeGreaterThanOrEqualTo(currTime);
        List<SmsFlashPromotionSession> promotionSessionList = promotionSessionMapper.selectByExample(sessionExample);
        if (!CollectionUtils.isEmpty(promotionSessionList)) {
            return promotionSessionList.get(0);
        }
        return null;
    }
    // 根据专题ID获取专题详情及关联商品列表
    @Override
    public SubjectDetailResult getSubjectDetail(Long subjectId, Long recommendSubjectId) {
        // 获取推荐专题详情
        SmsHomeRecommendSubject subject = smsHomeRecommendSubjectMapper.selectByPrimaryKey(recommendSubjectId);
        if (subject == null) {
            return null;
        }

        // 获取关联商品列表
        List<Long> productIds = homeDao.getProductIdsBySubjectId(subjectId);
        List<PmsProduct> productList = new ArrayList<>();
        if (!productIds.isEmpty()) {
            productList = homeDao.selectByIds(productIds);
        }

        // 封装结果
        SubjectDetailResult result = new SubjectDetailResult();
        result.setSubject(subject);
        result.setProductList(productList);
        return result;
    }

}
