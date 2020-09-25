package com.lyj.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliAccountConfig {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String notifyUrl;
    private String returnUrl;
}
