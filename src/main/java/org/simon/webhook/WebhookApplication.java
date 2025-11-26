package org.simon.webhook;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"org.simon.*"})
@Slf4j
@MapperScan(value = {"org.simon.*"})

public class WebhookApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

}
