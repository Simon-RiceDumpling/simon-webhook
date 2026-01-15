package org.simon.webhook.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.simon.webhook.enums.BusinessEnum;
import org.simon.webhook.server.ForwardMessageSendService;
import org.simon.webhook.utils.DecodeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
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
    public ResponseEntity<?> message(@RequestBody JSONObject request,
                                  @PathVariable("businessCode") String businessCode) {
        BusinessEnum business = BusinessEnum.getEnumByCode(businessCode);
        if (business == null || !business.getCheckedServer().check(request)) {
            return ResponseEntity.ok("success");
        }
        JSONObject params = new JSONObject(request); // 防御性拷贝
        // ✅ 只处理明确需要 decode 的字段
        decodeIfNeeded(params, "content");
        decodeIfNeeded(params, "remark");
        for (ForwardMessageSendService service : business.getService()) {
            try {
                log.warn("开始发送消息 server:【{}】request:【{}】",
                        service.getClass().getSimpleName(), params);
                service.send(params);
            } catch (Exception e) {
                log.error("消息发送失败 server:【{}】 err:【{}】", service, e.getMessage(), e);
            }
        }

        return ResponseEntity.ok("success");
    }

    private void decodeIfNeeded(JSONObject json, String field) {
        String value = json.getString(field);
        if (value != null && DecodeUtils.looksLikeEncoded(value)) {
            json.put(field, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
    }


}
