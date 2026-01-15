package org.simon.webhook.controller;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.simon.webhook.enums.BusinessEnum;

import org.simon.webhook.utils.DecodeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-20 16:55
 **/
@RestController
@Slf4j
@RequestMapping("/webhook")
public class WebhookController {


    /**
     * 监听消息
     */
    @PostMapping("/message/{businessCode}")
    public ResponseEntity<?> demo(@RequestBody JSONObject request,
                                  @PathVariable("businessCode") String businessCode) {
        Optional.ofNullable(businessCode)
                .map(BusinessEnum::getEnumByCode)
                .filter(x->x.getCheckedServer().check(request))
                .ifPresent(x->
                    Arrays.stream(x.getService()).forEach(service->{
                        try {
                            log.warn("开始发送消息 server:【{}】request:【{}】", service.getClass().getSimpleName(), request);
                            service.send(request);
                        } catch (Exception e) {
                            log.error("消息发送失败 server:【{}】 err:【{}】", service, e.getMessage(), e);
                        }
                    })
                );
        return ResponseEntity.ok("success");
    }
}
