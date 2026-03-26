package org.simon.webhook.kafka.conusme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicKafkaConsumerService {

    private final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    /**
     * 保存动态创建的消费任务
     * key = taskId
     */
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> containerMap = new ConcurrentHashMap<>();

    public String startConsumer(String topic, String groupId) {
        String taskId = "kafka-task-" + UUID.randomUUID();

        // 防止重复消费同一个 topic + group，可按需加限制
        log.info("准备启动动态消费者, taskId={}, topic={}, groupId={}", taskId, topic, groupId);

        // 1. 创建容器
        ConcurrentMessageListenerContainer<String, String> container =
                kafkaListenerContainerFactory.createContainer(topic);

        // 2. 设置 groupId
        ContainerProperties containerProperties = container.getContainerProperties();
        containerProperties.setGroupId(groupId);

        // 3. 如果你要批量消费，使用批量监听器
        container.setupMessageListener((org.springframework.kafka.listener.BatchMessageListener<String, String>) records -> {
            if (records == null || records.isEmpty()) {
                return;
            }

            log.info("动态消费者收到消息, taskId={}, topic={}, groupId={}, size={}",
                    taskId, topic, groupId, records.size());

            try {
                handleRecords(taskId, topic, groupId, records);
            } catch (Exception e) {
                log.error("动态消费者处理失败, taskId={}", taskId, e);
                throw e;
            }
        });

        // 4. 给容器一个 beanName，方便排查
        container.setBeanName(taskId);

        // 5. 启动
        container.start();

        // 6. 保存
        containerMap.put(taskId, container);

        log.info("动态消费者启动成功, taskId={}, topic={}, groupId={}", taskId, topic, groupId);
        return taskId;
    }

    public void stopConsumer(String taskId) {
        ConcurrentMessageListenerContainer<String, String> container = containerMap.remove(taskId);
        if (container == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }

        try {
            container.stop();
            log.info("动态消费者停止成功, taskId={}", taskId);
        } catch (Exception e) {
            log.error("动态消费者停止失败, taskId={}", taskId, e);
            throw e;
        }
    }

    public boolean isRunning(String taskId) {
        ConcurrentMessageListenerContainer<String, String> container = containerMap.get(taskId);
        return container != null && container.isRunning();
    }

    private void handleRecords(String taskId,
                               String topic,
                               String groupId,
                               List<ConsumerRecord<String, String>> records) {
        for (ConsumerRecord<String, String> record : records) {
            try {
                log.info("taskId={}, topic={}, partition={}, offset={}, value={}",
                        taskId,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.value());
                // TODO 这里写你的业务处理逻辑
                doBusiness(record);
            } catch (Exception e) {
                log.error("单条消息处理失败, taskId={}, offset={}", taskId, record.offset(), e);
            }
        }
    }

    private void doBusiness(ConsumerRecord<String, String> record) {
        // TODO 你的业务处理
        // 例如：
        // String value = record.value();
        // 转JSON
        // 调服务
        // 落库
    }
}