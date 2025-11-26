package org.simon.webhook.server.pushplus;

import com.alibaba.fastjson2.JSONObject;
import org.simon.webhook.enums.BusinessEnum;
import org.simon.webhook.server.ForwardMessageSendService;
import org.simon.webhook.server.feishu.builder.FeiShuMarkdownBuilder;
import org.simon.webhook.server.feishu.enums.FSBussinessEnum;
import org.simon.webhook.vo.ExecuteResult;
import org.springframework.stereotype.Service;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-26 19:08
 **/
@Service
public class FeiShuMessageSendServer implements ForwardMessageSendService<JSONObject, JSONObject> {

    @Override
    public ExecuteResult<JSONObject> send(JSONObject params) {
        FeiShuMarkdownBuilder shuMarkdownBuilder = FeiShuMarkdownBuilder.create("飞书提醒");
        params.forEach(shuMarkdownBuilder::add);
        shuMarkdownBuilder.sendToChat(FSBussinessEnum.SIMON_FS.getStatus());
        return null;
    }
}
