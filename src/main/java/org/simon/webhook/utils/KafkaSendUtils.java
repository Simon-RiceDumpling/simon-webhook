package org.simon.webhook.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;



/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-04-16 10:15
 **/

@Slf4j
public class KafkaSendUtils {

    public static final String dataProcessKey = "bsKey";

    private static final KafkaTemplate<String, String> kafkaTemplate = SpringUtil.getBean(KafkaTemplate.class);

    /**
     * 发送数据
     */
    public static void send(String topic, String message) {
        log.warn("开始发送kafka业务消息topic:【{}】,message:【{}】", topic, message);
        kafkaTemplate.send(topic, message);
    }

    //String topic, Integer partition, K key, @Nullable V data
    public static void send(String topic, String message, Integer partition, String key) {
        kafkaTemplate.send(topic, partition, key, message);
    }


    /**
     * 发送数据
     */
    public static void send(String topic, String payload, HashMap<String, String> headers) {
        MessageBuilder<String> stringMessageBuilder = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic);
        headers.forEach(stringMessageBuilder::setHeader);
        kafkaTemplate.send(stringMessageBuilder.build());
    }

    public static void main(String[] args) {
        String topic = "BUSINESS_CONSUME_TOPIC";

    }


}
