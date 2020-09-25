package com.lyj.pay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BestPayConfig {

    @Autowired
    private WxAccountConfig wxAccountConfig;

    @Autowired
    private AliAccountConfig aliAccountConfig;

    @Bean //项目执行后自动启动这段代码，Bean交给Spring容器托管
    public BestPayService bestPayService(WxPayConfig wxPayConfig){

        //支付宝支付配置
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(aliAccountConfig.getAppId());
        aliPayConfig.setPrivateKey(aliAccountConfig.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(aliAccountConfig.getPublicKey());
        //设置支付宝异步回调商户地址
        aliPayConfig.setNotifyUrl(aliAccountConfig.getNotifyUrl());
        //支付后支付宝跳转的地址
        aliPayConfig.setReturnUrl(aliAccountConfig.getReturnUrl());

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig(){
        //微信支付配置
        WxPayConfig wxPayConfig = new WxPayConfig();
        //微信native支付的应用id
        wxPayConfig.setAppId(wxAccountConfig.getAppId());
        //商户号
        wxPayConfig.setMchId(wxAccountConfig.getMchId());
        //商户私钥，注意：下面的支付私钥千万不能泄露出去！
        wxPayConfig.setMchKey(wxAccountConfig.getMchKey());
        //设置微信异步回调商户地址
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());
        //
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());
        return wxPayConfig;
    }
}
