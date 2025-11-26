package org.simon.webhook.server.pushplus;

import com.alibaba.fastjson2.JSONObject;

import org.simon.webhook.server.ForwardMessageSendService;
import org.simon.webhook.utils.httputil.SimonHttpClient;
import org.simon.webhook.vo.ExecuteResult;
import org.springframework.stereotype.Service;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-21 15:39
 **/
@Service
public class PushPlusServer implements ForwardMessageSendService<JSONObject, JSONObject> {
    /**
     * push-plus
     */
    public ExecuteResult<JSONObject> send(JSONObject params) {
        return SimonHttpClient.call(JSONObject.class)
                .get("https://www.pushplus.plus/send")
                .param("token", getConfigRefresh().getPushPlusToken())
                .header("title", "SimonMessage")
                .param("template", "html")
                .param("content", params)
                .executeOkHttp();
    }


}
