package com.lyj.pay.service.impl;

import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lyj.pay.dao.PayInfoMapper;
import com.lyj.pay.enums.PayPlatformEnum;
import com.lyj.pay.pojo.PayInfo;
import com.lyj.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j //开启log打印日志
@Service
public class PayServiceImpl implements PayService {

    private static final String QUEUE_PAY_NOTIFY = "payNotify";

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {

        //把支付信息写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("9189158-最好的支付sdk");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        //支付方式为微信支付的Native方式或支付宝支付
        request.setPayTypeEnum(bestPayTypeEnum);

        PayResponse response = bestPayService.pay(request);
        log.info("发起通知，response = {}", response);
        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {

        // 1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知，response = {}", payResponse);

        // 2.金额校验（从数据库查订单金额，与异步通知的金额做校验）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        // 警告，比较严重（正常情况下是不会发生的），发出警告：钉钉、短信。
        if(payInfo == null){
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        // 如果订单支付状态不是"已支付"
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            // Double类型比较大小，精度要注意，不好控制。e.g.  1.00  1.0
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0){
                // 警告，务必加上具体的线索，方便排查。
                throw new RuntimeException("异步通知中的金额和数据库里的金额不一致，orderNo." + payResponse.getOrderId());
            }
            // 3.修改订单支付状态
            // 支付状态，更新之前把支付状态改为SUCCESS
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            // 交易的流水号,由支付平台产生
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            // 更新时间，设置为null，交由mysql管理
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        // pay发送MQ消息，mall接受MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));

        // 4.告诉微信不要再通知了
        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return "success";
        }
        throw new RuntimeException("暂不支持的支付类型");
    }


    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
