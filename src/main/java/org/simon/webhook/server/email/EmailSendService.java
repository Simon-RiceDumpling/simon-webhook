package org.simon.webhook.server.email;

import com.alibaba.fastjson2.JSONObject;
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
public class EmailSendService implements ForwardMessageSendService<JSONObject, JSONObject> {

    private

    @Override
    public ExecuteResult<JSONObject> send(JSONObject params) {
        //利用飞书告警模板组装消息
        FeiShuMarkdownBuilder shuMarkdownBuilder = FeiShuMarkdownBuilder.create("simon webhook message");
        params.forEach(shuMarkdownBuilder::add);
        String message = shuMarkdownBuilder.build();

        return null;
    }
}
