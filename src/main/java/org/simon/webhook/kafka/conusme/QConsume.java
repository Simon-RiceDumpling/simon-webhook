package org.simon.webhook.kafka.conusme;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2026-03-26 16:14
 **/
@Slf4j
@Service
public class QConsume {
    @KafkaListener(
            topics = "your_topic",
            groupId = "your_group"
    )
    public void consume(List<ConsumerRecord<String, String>> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        log.info("批量消费，records.size={}", records.size());
        for (ConsumerRecord<String, String> record : records) {
            try {
                log.info("topic={}, partition={}, offset={}, key={}, value={}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value());
                handleMessage(record.value());
            } catch (Exception e) {
                log.error("处理消息失败，topic={}, partition={}, offset={}, value={}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.value(),
                        e);
            }
        }
    }

    private void handleMessage(String message) {
        // TODO 业务逻辑
    }
}
