package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户详情显示VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsMemberDetailVO {
    
    @Schema(title = "用户ID")
    private Long id;
    
    @Schema(title = "用户名")
    private String username;
    
    @Schema(title = "昵称")
    private String nickname;
    
    @Schema(title = "手机号码")
    private String phone;
    
    @Schema(title = "会员码")
    private String memberCode;
    
    @Schema(title = "头像")
    private String icon;
    
    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;
    
    @Schema(title = "生日")
    private Date birthday;
    
    @Schema(title = "所在城市")
    private String city;
    
    @Schema(title = "职业")
    private String job;
    
    @Schema(title = "个性签名")
    private String personalizedSignature;
    
    @Schema(title = "用户来源：1->小程序")
    private Integer sourceType;
    
    @Schema(title = "帐号启用状态:0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "注册时间")
    private Date createTime;
    
    @Schema(title = "会员等级ID")
    private Long memberLevelId;
    
    @Schema(title = "会员等级名称")
    private String memberLevelName;
    
    @Schema(title = "积分")
    private Integer integration;
    
    @Schema(title = "成长值")
    private Integer growth;
    
    @Schema(title = "剩余抽奖次数")
    private Integer luckeyCount;
    
    @Schema(title = "历史积分数量")
    private Integer historyIntegration;
    
    @Schema(title = "微信用户openid")
    private String openid;
    
    @Schema(title = "最近登录时间")
    private Date lastLoginTime;
    
    @Schema(title = "最近登录IP")
    private String lastLoginIp;
    
    @Schema(title = "订单总数")
    private Integer orderCount;
    
    @Schema(title = "订单总金额")
    private BigDecimal orderAmount;
    
    @Schema(title = "已完成订单数")
    private Integer finishedOrderCount;
    
    @Schema(title = "已取消订单数")  
    private Integer cancelledOrderCount;
    
    @Schema(title = "退货订单数")
    private Integer returnOrderCount;
    
    @Schema(title = "收藏商品数")
    private Integer collectProductCount;
    
    @Schema(title = "持有优惠券数")
    private Integer couponCount;
    
    @Schema(title = "评价数量")
    private Integer commentCount;
    
    @Schema(title = "邀请好友数")
    private Integer inviteFriendCount;
} 