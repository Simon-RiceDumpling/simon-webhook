package org.simon.webhook.controller;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2026-03-26 16:22
 **/
import lombok.RequiredArgsConstructor;
import org.simon.webhook.kafka.conusme.DynamicKafkaConsumerService;
import org.simon.webhook.kafka.vo.KafkaTaskResp;
import org.simon.webhook.kafka.vo.KafkaTaskStartReq;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka/task")
@RequiredArgsConstructor
public class DynamicKafkaConsumerController {

    private final DynamicKafkaConsumerService dynamicKafkaConsumerService;

    @PostMapping("/start")
    public KafkaTaskResp start(@RequestBody KafkaTaskStartReq req) {
        String taskId = dynamicKafkaConsumerService.startConsumer(req.getTopic(), req.getGroupId());
        return new KafkaTaskResp(taskId, "启动成功");
    }

    @PostMapping("/stop")
    public KafkaTaskResp stop(@RequestParam String taskId) {
        dynamicKafkaConsumerService.stopConsumer(taskId);
        return new KafkaTaskResp(taskId, "停止成功");
    }

    @GetMapping("/status")
    public KafkaTaskResp status(@RequestParam String taskId) {
        boolean running = dynamicKafkaConsumerService.isRunning(taskId);
        return new KafkaTaskResp(taskId, running ? "运行中" : "未运行");
    }
}
