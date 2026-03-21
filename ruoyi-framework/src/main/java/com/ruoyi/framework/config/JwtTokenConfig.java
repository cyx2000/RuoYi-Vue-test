package com.ruoyi.framework.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.ruoyi.framework.config.properties.JwtTokenProperties;

@Configuration
public class JwtTokenConfig {

    @Bean
    public SecretKey jwtSecretKey(JwtTokenProperties jwtProperties) {
        return new SecretKeySpec(jwtProperties.getSecret().getBytes(), jwtProperties.getAlgorithm());
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey key, JwtTokenProperties jwtProperties) {
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.from(jwtProperties.getMacAlgorithm()))
                .build();
    }

	@Bean
	public JwtEncoder jwtEncoder(SecretKey key) {
		JWKSource<SecurityContext> jwks = new ImmutableSecret<>(key);
		return new NimbusJwtEncoder(jwks);
	}
}
