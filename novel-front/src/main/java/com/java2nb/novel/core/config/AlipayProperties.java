package com.java2nb.novel.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 11797
 */
@Data
@Component
//@ConfigurationProperties 需要配合@Configuration使用，而@Configuration属于@Conponent可以用@CompnentScan处理
//prefix表示会读取properties文件中所有以"alipay"开头的属性，并和bean中的字段进行匹配
@ConfigurationProperties(prefix="alipay")
public class AlipayProperties {

    private String appId;
    private String merchantPrivateKey;
    private String publicKey;
    private String notifyUrl;
    private String returnUrl;
    private String signType;
    private String charset;
    private String gatewayUrl;
}
