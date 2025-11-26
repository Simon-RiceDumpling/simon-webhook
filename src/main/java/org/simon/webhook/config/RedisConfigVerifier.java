package org.simon.webhook.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-26 17:43
 **/
@Component
@Slf4j
public class RedisConfigVerifier {

    @Value("${spring.data.redis.host:NOT_SET}")
    private String redisHost;

    @Value("${spring.data.redis.port:0}")
    private int redisPort;

    @Value("${spring.data.redis.password:NOT_SET}")
    private String redisPassword;

    @PostConstruct
    public void verifyConfig() {
        log.info("=== Redis Configuration ===");
        log.info("Host: {}", redisHost);
        log.info("Port: {}", redisPort);
        log.info("Password: {}", redisPassword.length() > 0 ? "***" : "NOT_SET");
        log.info("========================");
    }
}
