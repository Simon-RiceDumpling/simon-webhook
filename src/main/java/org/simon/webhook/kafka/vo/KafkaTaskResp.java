package org.simon.webhook.kafka.vo;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2026-03-26 16:20
 **/
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaTaskResp {
    private String taskId;
    private String message;
}