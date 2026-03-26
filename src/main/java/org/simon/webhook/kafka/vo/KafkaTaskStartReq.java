package org.simon.webhook.kafka.vo;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2026-03-26 16:20
 **/
import lombok.Data;

@Data
public class KafkaTaskStartReq {
    private String topic;
    private String groupId;
}
