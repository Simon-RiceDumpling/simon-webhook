package org.simon.webhook.busines_server.impl;

import com.alibaba.fastjson2.JSONObject;

import org.simon.webhook.busines_server.BusinessCheckedServer;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-21 16:15
 **/
@Service
public class LSCheckedService implements BusinessCheckedServer {

    @Override
    public Boolean check(JSONObject request) {
        return !request.values().stream().anyMatch(value ->
                {
                    String decode = URLDecoder.decode(value.toString());
                    if (decode.contains("ClientFrozen") || decode.contains("超时")) {
                        return true;
                    }
                    return false;
                }
        );
    }
}
