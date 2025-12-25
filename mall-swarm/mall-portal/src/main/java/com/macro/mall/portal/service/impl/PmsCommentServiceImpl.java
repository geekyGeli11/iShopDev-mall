package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsCommentMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsComment;
import com.macro.mall.model.PmsCommentExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dto.PmsCommentParam;
import com.macro.mall.portal.service.PmsCommentService;
import com.macro.mall.portal.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 商品评价管理Service实现类
 * Created by guanghengzhou on 2024/01/13.
 */
@Service
public class PmsCommentServiceImpl implements PmsCommentService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsCommentServiceImpl.class);
    
    @Autowired
    private PmsCommentMapper commentMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Autowired
    private HttpServletRequest request;
    
    @Override
    @Transactional
    public int createComment(PmsCommentParam commentParam) {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 检查是否已经评价过 - 使用新的逻辑
        if (hasCommented(commentParam.getOrderId(), commentParam.getProductId(), currentMember.getNickname())) {
            throw new RuntimeException("该订单商品已评价过");
        }
        
        // 获取商品信息
        PmsProduct product = productMapper.selectByPrimaryKey(commentParam.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 创建评价记录
        PmsComment comment = new PmsComment();
        comment.setProductId(commentParam.getProductId());
        comment.setOrderId(commentParam.getOrderId()); // 设置订单ID
        comment.setMemberId(currentMember.getId()); // 设置用户ID
        comment.setProductName(product.getName());
        comment.setStar(commentParam.getStar());
        comment.setContent(commentParam.getContent());
        comment.setCreateTime(new Date());
        comment.setShowStatus(1); // 默认显示
        comment.setCollectCouont(0);
        comment.setReadCount(0);
        comment.setReplayCount(0);
        
        // 设置会员信息
        if (commentParam.getIsAnonymous() != null && commentParam.getIsAnonymous() == 1) {
            // 匿名评价
            comment.setMemberNickName("匿名用户");
            comment.setMemberIcon("");
        } else {
            // 实名评价
            comment.setMemberNickName(StringUtils.hasText(currentMember.getNickname()) ? 
                currentMember.getNickname() : "用户" + currentMember.getId());
            comment.setMemberIcon(currentMember.getIcon());
        }
        
        // 设置其他信息
        comment.setPics(commentParam.getPics());
        comment.setProductAttribute(commentParam.getProductAttribute());
        comment.setMemberIp(getClientIpAddress());
        
        int result = commentMapper.insertSelective(comment);
        
        if (result > 0) {
            LOGGER.info("用户{}成功评价商品，订单ID：{}，商品ID：{}，评分：{}星", 
                       currentMember.getId(), commentParam.getOrderId(), 
                       commentParam.getProductId(), commentParam.getStar());
        }
        
        return result;
    }
    
    @Override
    public List<PmsComment> getCommentsByProductId(Long productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsCommentExample example = new PmsCommentExample();
        example.createCriteria()
               .andProductIdEqualTo(productId)
               .andShowStatusEqualTo(1);
        example.setOrderByClause("create_time desc");
        return commentMapper.selectByExampleWithBLOBs(example);
    }
    
    @Override
    public List<PmsComment> getCommentsByMember(String memberNickName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsCommentExample example = new PmsCommentExample();
        example.createCriteria()
               .andMemberNickNameEqualTo(memberNickName)
               .andShowStatusEqualTo(1);
        example.setOrderByClause("create_time desc");
        return commentMapper.selectByExampleWithBLOBs(example);
    }
    
    @Override
    public boolean hasCommented(Long orderId, Long productId, String memberNickName) {
        // 使用订单ID、商品ID和用户昵称来精确查询
        PmsCommentExample example = new PmsCommentExample();
        PmsCommentExample.Criteria criteria = example.createCriteria();
        
        // 必须同时满足订单ID和商品ID
        criteria.andOrderIdEqualTo(orderId)
               .andProductIdEqualTo(productId);
        
        // 如果提供了用户昵称，也加入查询条件
        if (StringUtils.hasText(memberNickName)) {
            criteria.andMemberNickNameEqualTo(memberNickName);
        }
        
        long count = commentMapper.countByExample(example);
        return count > 0;
    }
    
    /**
     * 根据用户ID检查是否已评价
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param memberId 用户ID
     * @return 是否已评价
     */
    public boolean hasCommentedByMemberId(Long orderId, Long productId, Long memberId) {
        PmsCommentExample example = new PmsCommentExample();
        example.createCriteria()
               .andOrderIdEqualTo(orderId)
               .andProductIdEqualTo(productId)
               .andMemberIdEqualTo(memberId);
        long count = commentMapper.countByExample(example);
        return count > 0;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 