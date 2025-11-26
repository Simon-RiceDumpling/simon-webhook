package org.simon.webhook.busines_server;

import com.alibaba.fastjson2.JSONObject;

public interface BusinessCheckedServer {


    Boolean check(JSONObject request);
}
