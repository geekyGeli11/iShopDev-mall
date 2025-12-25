package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.SmsCouponDao;
import com.macro.mall.dao.SmsCouponProductCategoryRelationDao;
import com.macro.mall.dao.SmsCouponProductRelationDao;
import com.macro.mall.dao.SmsCouponProductExcludeRelationDao;
import com.macro.mall.dao.SmsCouponProductCategoryExcludeRelationDao;
import com.macro.mall.dto.SmsCouponListVO;
import com.macro.mall.dto.SmsCouponParam;
import com.macro.mall.dto.SmsCouponShareResult;
import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.mapper.SmsCouponSchoolRelationMapper;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.model.SmsCouponSchoolRelation;
import com.macro.mall.model.SmsCouponSchoolRelationExample;
import com.macro.mall.service.WechatMiniProgramService;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.mapper.SmsCouponProductCategoryRelationMapper;
import com.macro.mall.mapper.SmsCouponProductRelationMapper;
import com.macro.mall.mapper.SmsCouponProductExcludeRelationMapper;
import com.macro.mall.mapper.SmsCouponProductCategoryExcludeRelationMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.SmsCouponService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券管理Service实现类
 * Created by macro on 2018/8/28.
 */
@Service
public class SmsCouponServiceImpl implements SmsCouponService {
    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private SmsCouponProductRelationMapper productRelationMapper;
    @Autowired
    private SmsCouponProductCategoryRelationMapper productCategoryRelationMapper;
    @Autowired
    private SmsCouponProductRelationDao productRelationDao;
    @Autowired
    private SmsCouponProductCategoryRelationDao productCategoryRelationDao;
    @Autowired
    private SmsCouponProductExcludeRelationDao productExcludeRelationDao;
    @Autowired
    private SmsCouponProductCategoryExcludeRelationDao productCategoryExcludeRelationDao;
    @Autowired
    private SmsCouponProductExcludeRelationMapper productExcludeRelationMapper;
    @Autowired
    private SmsCouponProductCategoryExcludeRelationMapper productCategoryExcludeRelationMapper;
    @Autowired
    private SmsCouponDao couponDao;
    @Autowired
    private SmsCouponSchoolRelationMapper couponSchoolRelationMapper;
    @Autowired
    private OmsSchoolMapper schoolMapper;
    @Autowired
    private WechatMiniProgramService wechatMiniProgramService;
    @Override
    public int create(SmsCouponParam couponParam) {
        couponParam.setCount(couponParam.getPublishCount());
        couponParam.setUseCount(0);
        couponParam.setReceiveCount(0);
        //插入优惠券表
        int count = couponMapper.insert(couponParam);
        if (count > 0) {
            //插入优惠券和商品关系表
            if(couponParam.getUseType().equals(2)){
                for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                    productRelation.setCouponId(couponParam.getId());
                }
                productRelationDao.insertList(couponParam.getProductRelationList());
            }
            //插入优惠券和商品分类关系表
            if(couponParam.getUseType().equals(1)){
                for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                    couponProductCategoryRelation.setCouponId(couponParam.getId());
                }
                productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());
            }
            //插入优惠券排除商品关系表
            if(couponParam.getEnableExclude() != null && couponParam.getEnableExclude() &&
               couponParam.getProductExcludeRelationList() != null && !couponParam.getProductExcludeRelationList().isEmpty()){
                for(SmsCouponProductExcludeRelation productExcludeRelation : couponParam.getProductExcludeRelationList()){
                    productExcludeRelation.setCouponId(couponParam.getId());
                }
                productExcludeRelationDao.insertList(couponParam.getProductExcludeRelationList());
            }
            //插入优惠券排除商品分类关系表
            if(couponParam.getEnableExclude() != null && couponParam.getEnableExclude() &&
               couponParam.getProductCategoryExcludeRelationList() != null && !couponParam.getProductCategoryExcludeRelationList().isEmpty()){
                for (SmsCouponProductCategoryExcludeRelation couponProductCategoryExcludeRelation : couponParam.getProductCategoryExcludeRelationList()) {
                    couponProductCategoryExcludeRelation.setCouponId(couponParam.getId());
                }
                productCategoryExcludeRelationDao.insertList(couponParam.getProductCategoryExcludeRelationList());
            }
            // 返回新创建的优惠券ID，而不是影响行数
            return couponParam.getId().intValue();
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        //删除优惠券
        int count = couponMapper.deleteByPrimaryKey(id);
        //删除商品关联
        deleteProductRelation(id);
        //删除商品分类关联
        deleteProductCategoryRelation(id);
        //删除排除商品关联
        deleteProductExcludeRelation(id);
        //删除排除商品分类关联
        deleteProductCategoryExcludeRelation(id);
        return count;
    }

    private void deleteProductCategoryRelation(Long id) {
        SmsCouponProductCategoryRelationExample productCategoryRelationExample = new SmsCouponProductCategoryRelationExample();
        productCategoryRelationExample.createCriteria().andCouponIdEqualTo(id);
        productCategoryRelationMapper.deleteByExample(productCategoryRelationExample);
    }

    private void deleteProductRelation(Long id) {
        SmsCouponProductRelationExample productRelationExample = new SmsCouponProductRelationExample();
        productRelationExample.createCriteria().andCouponIdEqualTo(id);
        productRelationMapper.deleteByExample(productRelationExample);
    }

    private void deleteProductExcludeRelation(Long id) {
        SmsCouponProductExcludeRelationExample productExcludeRelationExample = new SmsCouponProductExcludeRelationExample();
        productExcludeRelationExample.createCriteria().andCouponIdEqualTo(id);
        productExcludeRelationMapper.deleteByExample(productExcludeRelationExample);
    }

    private void deleteProductCategoryExcludeRelation(Long id) {
        SmsCouponProductCategoryExcludeRelationExample productCategoryExcludeRelationExample = new SmsCouponProductCategoryExcludeRelationExample();
        productCategoryExcludeRelationExample.createCriteria().andCouponIdEqualTo(id);
        productCategoryExcludeRelationMapper.deleteByExample(productCategoryExcludeRelationExample);
    }

    @Override
    public int update(Long id, SmsCouponParam couponParam) {
        couponParam.setId(id);
        int count =couponMapper.updateByPrimaryKey(couponParam);
        //删除后插入优惠券和商品关系表
        if(couponParam.getUseType().equals(2)){
            for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                productRelation.setCouponId(couponParam.getId());
            }
            deleteProductRelation(id);
            productRelationDao.insertList(couponParam.getProductRelationList());
        }
        //删除后插入优惠券和商品分类关系表
        if(couponParam.getUseType().equals(1)){
            for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                couponProductCategoryRelation.setCouponId(couponParam.getId());
            }
            deleteProductCategoryRelation(id);
            productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());
        }
        //处理排除商品关系表
        deleteProductExcludeRelation(id);
        if(couponParam.getEnableExclude() != null && couponParam.getEnableExclude() &&
           couponParam.getProductExcludeRelationList() != null && !couponParam.getProductExcludeRelationList().isEmpty()){
            for(SmsCouponProductExcludeRelation productExcludeRelation : couponParam.getProductExcludeRelationList()){
                productExcludeRelation.setCouponId(couponParam.getId());
            }
            productExcludeRelationDao.insertList(couponParam.getProductExcludeRelationList());
        }
        //处理排除商品分类关系表
        deleteProductCategoryExcludeRelation(id);
        if(couponParam.getEnableExclude() != null && couponParam.getEnableExclude() &&
           couponParam.getProductCategoryExcludeRelationList() != null && !couponParam.getProductCategoryExcludeRelationList().isEmpty()){
            for (SmsCouponProductCategoryExcludeRelation couponProductCategoryExcludeRelation : couponParam.getProductCategoryExcludeRelationList()) {
                couponProductCategoryExcludeRelation.setCouponId(couponParam.getId());
            }
            productCategoryExcludeRelationDao.insertList(couponParam.getProductCategoryExcludeRelationList());
        }
        return count;
    }

    @Override
    public List<SmsCoupon> list(String name, Integer type, Long schoolId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        SmsCouponExample example=new SmsCouponExample();
        SmsCouponExample.Criteria criteria=example.createCriteria();
        if(StringUtils.isNotBlank(name)){
            criteria.andNameLike("%"+name+"%" );
        }
        if(type!=null){
            criteria.andTypeEqualTo(type);
        }

        // 如果指定了学校ID，需要通过关联表查询
        if(schoolId != null) {
            // 查询与指定学校关联的优惠券ID列表
            SmsCouponSchoolRelationExample relationExample = new SmsCouponSchoolRelationExample();
            relationExample.createCriteria().andSchoolIdEqualTo(schoolId);
            List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(relationExample);

            if(relations.isEmpty()) {
                // 如果没有关联关系，返回空列表
                return new ArrayList<>();
            }

            List<Long> couponIds = relations.stream()
                    .map(SmsCouponSchoolRelation::getCouponId)
                    .collect(Collectors.toList());
            criteria.andIdIn(couponIds);
        }

        return couponMapper.selectByExample(example);
    }

    @Override
    public List<SmsCouponListVO> listWithSchools(String name, Integer type, Long schoolId, Integer pageSize, Integer pageNum) {
        // 先获取优惠券列表
        List<SmsCoupon> coupons = list(name, type, schoolId, pageSize, pageNum);
        List<SmsCouponListVO> result = new ArrayList<>();

        for (SmsCoupon coupon : coupons) {
            SmsCouponListVO vo = new SmsCouponListVO();
            // 复制基本属性
            org.springframework.beans.BeanUtils.copyProperties(coupon, vo);

            // 查询关联的学校信息
            SmsCouponSchoolRelationExample relationExample = new SmsCouponSchoolRelationExample();
            relationExample.createCriteria().andCouponIdEqualTo(coupon.getId());
            List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(relationExample);

            if (!relations.isEmpty()) {
                List<Long> schoolIds = relations.stream()
                        .map(SmsCouponSchoolRelation::getSchoolId)
                        .collect(Collectors.toList());

                // 查询学校详细信息
                OmsSchoolExample schoolExample = new OmsSchoolExample();
                schoolExample.createCriteria().andIdIn(schoolIds);
                List<OmsSchool> schools = schoolMapper.selectByExample(schoolExample);

                // 转换为VO中的学校信息
                List<SmsCouponListVO.SchoolInfo> schoolInfos = schools.stream()
                        .map(school -> {
                            SmsCouponListVO.SchoolInfo info = new SmsCouponListVO.SchoolInfo();
                            info.setId(school.getId());
                            info.setSchoolName(school.getSchoolName());
                            info.setStatus(school.getStatus());
                            return info;
                        })
                        .collect(Collectors.toList());

                vo.setSchools(schoolInfos);

                // 设置学校名称字符串（用于显示）
                String schoolNames = schools.stream()
                        .map(OmsSchool::getSchoolName)
                        .collect(Collectors.joining(", "));
                vo.setSchoolNames(schoolNames);
            } else {
                vo.setSchools(new ArrayList<>());
                vo.setSchoolNames("全平台通用");
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public SmsCouponParam getItem(Long id) {
        return couponDao.getItem(id);
    }

    @Override
    public List<SmsCoupon> simpleList(String keyword,Integer limit){
        PageHelper.startPage(1,limit==null?20:limit);
        SmsCouponExample ex=new SmsCouponExample();
        if(StringUtils.isNotBlank(keyword)){
            ex.createCriteria().andNameLike("%"+keyword+"%" );
        }
        ex.setOrderByClause("id desc");
        return couponMapper.selectByExample(ex);
    }

    @Override
    public SmsCouponShareResult generateShareInfo(Long couponId) {
        // 验证优惠券是否存在
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        SmsCouponShareResult result = new SmsCouponShareResult();
        result.setCouponId(couponId);
        result.setCouponName(coupon.getName());

        // 生成分享标题和描述
        String shareTitle = "限时优惠券 - " + coupon.getName();
        String shareDescription;
        if (coupon.getCouponType() != null && coupon.getCouponType() == 1) {
            // 打折券
            if (coupon.getDiscountRate() != null) {
                int discount = (int) (coupon.getDiscountRate().doubleValue() * 10);
                shareDescription = String.format("%d折优惠券，快来领取！", discount);
            } else {
                shareDescription = "打折优惠券，快来领取！";
            }
        } else {
            // 满减券
            if (coupon.getAmount() != null) {
                shareDescription = String.format("立减%.0f元优惠券，快来领取！", coupon.getAmount());
            } else {
                shareDescription = "超值优惠券，快来领取！";
            }
        }

        // 如果有使用门槛，添加到描述中
        if (coupon.getMinPoint() != null && coupon.getMinPoint().compareTo(BigDecimal.ZERO) > 0) {
            shareDescription += String.format("（满%.0f元可用）", coupon.getMinPoint());
        }

        result.setShareTitle(shareTitle);
        result.setShareDescription(shareDescription);

        // 检查微信小程序配置是否可用
        if (wechatMiniProgramService.isConfigAvailable()) {
            // 生成真正的小程序短链接
            String path = "pages/coupon/couponList";
            String query = "couponId=" + couponId;
            String urlLink = wechatMiniProgramService.generateUrlLink(path, query);

            if (urlLink != null) {
                result.setShareLink(urlLink);
            } else {
                // 降级方案：生成普通链接
                result.setShareLink("小程序短链接生成失败，请检查微信小程序配置");
            }

            // 生成真正的小程序码（base64格式）
            String scene = "couponId=" + couponId;
            String page = "pages/coupon/couponList";
            String miniProgramCodeBase64 = wechatMiniProgramService.generateMiniProgramCode(scene, page, 430);

            if (miniProgramCodeBase64 != null) {
                result.setMiniProgramCodeBase64(miniProgramCodeBase64);
                result.setMiniProgramCodeUrl("小程序码已生成（base64格式）");
            } else {
                // 降级方案
                result.setMiniProgramCodeBase64(null);
                result.setMiniProgramCodeUrl("小程序码生成失败，请检查微信小程序配置");
            }
        } else {
            // 微信配置不可用时的降级方案
            result.setShareLink("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeUrl("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeBase64(null);
        }

        // 设置默认分享图片（可以根据需要自定义）
        result.setShareImageUrl("/static/images/coupon-share-default.png");

        return result;
    }

    @Override
    @Transactional
    public boolean updateCouponSchools(Long couponId, List<Long> schoolIds) {
        // 验证优惠券是否存在
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 删除原有关联关系
        SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
        example.createCriteria().andCouponIdEqualTo(couponId);
        couponSchoolRelationMapper.deleteByExample(example);

        // 添加新的关联关系
        if (schoolIds != null && !schoolIds.isEmpty()) {
            for (Long schoolId : schoolIds) {
                SmsCouponSchoolRelation relation = new SmsCouponSchoolRelation();
                relation.setCouponId(couponId);
                relation.setSchoolId(schoolId);
                couponSchoolRelationMapper.insert(relation);
            }
        }

        return true;
    }
}
