package com.lyj.pay.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayInfo {

    // 主键
    private Integer id;
    // 用户id
    private Integer userId;
    // 订单号
    private Long orderNo;
    // 支付平台:1-支付宝 2-微信
    private Integer payPlatform;
    // 支付流水号
    private String platformNumber;
    // 支付状态
    private String platformStatus;
    // 支付金额
    private BigDecimal payAmount;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

    // 创建构造方法,只需要记录下面4个数据即可。
    public PayInfo(Long orderNo, Integer payPlatform, String platformStatus, BigDecimal payAmount) {
        this.orderNo = orderNo;
        this.payPlatform = payPlatform;
        this.platformStatus = platformStatus;
        this.payAmount = payAmount;
    }
}