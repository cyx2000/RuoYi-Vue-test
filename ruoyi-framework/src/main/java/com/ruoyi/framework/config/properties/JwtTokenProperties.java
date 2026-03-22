package com.ruoyi.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt属性集
 *
 * @author winter123
 */
@ConfigurationProperties(prefix = "token")
@Component
public class JwtTokenProperties {

    // 令牌自定义标识
    private String header;

    // 令牌秘钥
    private String secret;

    // 令牌有效期（默认30分钟）
    private int expireTime;

    // 加密算法，可选：HmacSHA256，HmacSHA384，HmacSHA512
    private String algorithm = "HmacSHA256";

    // 解密使用算法，可选：HS256，HS384，HS512
    private String macAlgorithm = "HS256";


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

}
