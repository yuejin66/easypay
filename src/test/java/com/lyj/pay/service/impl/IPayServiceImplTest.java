package com.lyj.pay.service.impl;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lyj.pay.PayApplicationTests;
import com.lyj.pay.service.PayService;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class IPayServiceImplTest extends PayApplicationTests {

    @Autowired
    private PayService payService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void create(){

        // BigDecimal.valueOf(0.01) 和 new BigDecimal("0.01") 两者效果一样
        payService.create("123456123456", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
    }

    @Test
    public void sendMQMsg(){
        amqpTemplate.convertAndSend("payNotify", "hello");
    }
}