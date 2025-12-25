package com.macro.mall.portal.service;

import com.macro.mall.model.PmsComment;
import com.macro.mall.portal.dto.PmsCommentParam;

import java.util.List;

/**
 * 商品评价管理Service
 * Created by guanghengzhou on 2024/01/13.
 */
public interface PmsCommentService {
    
    /**
     * 添加商品评价
     * @param commentParam 评价参数
     * @return 成功数量
     */
    int createComment(PmsCommentParam commentParam);
    
    /**
     * 根据商品ID获取评价列表
     * @param productId 商品ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 评价列表
     */
    List<PmsComment> getCommentsByProductId(Long productId, Integer pageNum, Integer pageSize);
    
    /**
     * 根据会员获取评价列表
     * @param memberNickName 会员昵称
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 评价列表
     */
    List<PmsComment> getCommentsByMember(String memberNickName, Integer pageNum, Integer pageSize);
    
    /**
     * 检查用户是否已对订单商品进行评价
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param memberNickName 会员昵称
     * @return 是否已评价
     */
    boolean hasCommented(Long orderId, Long productId, String memberNickName);
    
    /**
     * 根据用户ID检查是否已评价
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param memberId 用户ID
     * @return 是否已评价
     */
    boolean hasCommentedByMemberId(Long orderId, Long productId, Long memberId);
} 