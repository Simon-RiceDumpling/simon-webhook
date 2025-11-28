package org.simon.webhook.server.email;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.simon.webhook.server.ForwardMessageSendService;
import org.simon.webhook.server.feishu.builder.FeiShuMarkdownBuilder;
import org.simon.webhook.vo.ExecuteResult;
import org.springframework.stereotype.Service;

/**
 * @program: simon-webhook
 * @description: 发送邮件提醒
 * @author: renBo
 * @create: 2025-11-28 18:44
 **/
@Service
public class EmailSendServiceImpl implements ForwardMessageSendService<JSONObject, JSONObject> {
    @Resource
    private EmailService emailService;

    @Override
    public ExecuteResult<JSONObject> send(JSONObject params) {
        //利用飞书告警模板组装消息
        FeiShuMarkdownBuilder shuMarkdownBuilder = FeiShuMarkdownBuilder.create("simon webhook message");
        params.forEach(shuMarkdownBuilder::add);
        emailService.sendSimpleEmail("18189270679@163.com", "simon webhook message", shuMarkdownBuilder.build());
        return null;
    }
}
