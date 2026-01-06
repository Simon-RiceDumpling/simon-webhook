package org.simon.webhook.utils.http;

import com.alibaba.fastjson2.JSONObject;
import org.simon.webhook.enums.MethodType;
import org.simon.webhook.utils.http.enums.HttpTypeEnums;
import org.simon.webhook.vo.ExecuteResult;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: atlas-oversea-micro-services
 * @description: TODO
 * @author: renBo
 * @create: 2026-01-06 11:52
 **/
public class SimonHttpUtils {


    /**
     * 函数式HTTP调用
     */
    public static <T> HttpCall<T> call(Class<T> responseClass) {
        return new HttpCall<>(responseClass);
    }

    public static class HttpCall<T> {
        private final Class<T> responseClass;
        private HttpTypeEnums type;
        private MethodType method = MethodType.GET;
        private String url;
        private Map<String, Object> headers = new HashMap<>();
        private Map<String, Object> params = new HashMap<>();
        private JSONObject body;

        private HttpCall(Class<T> responseClass) {
            this.responseClass = responseClass;
        }

        public HttpCall<T> get(String url) {
            this.method = MethodType.GET;
            this.url = url;
            return this;
        }

        public HttpCall<T> post(String url) {
            this.method = MethodType.POST;
            this.url = url;
            return this;
        }

        public HttpCall<T> header(String key, Object value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpCall<T> header(Map<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        public HttpCall<T> param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public HttpCall<T> param(String key, JSONObject value) {
            this.params.putAll(value);
            return this;
        }


        public HttpCall<T> param(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public HttpCall<T> body(JSONObject body) {
            this.body = body;
            return this;
        }

        public HttpCall<T> type(HttpTypeEnums type) {
            this.type = type;
            return this;
        }

        public HttpCall<T> body(String key, Object value) {
            if (Objects.isNull(body)) {
                this.body = new JSONObject();
            }
            this.body.put(key, value);
            return this;
        }


        public ExecuteResult<T> execute() {
            if (Objects.isNull(type)) {
                throw new RuntimeException("type is null");
            }
            switch (type) {
                case OK_HTTP -> {
                    return SimonOkHttpUtils.call(responseClass)
                            .method(method, url)
                            .param(params)
                            .header(headers)
                            .body(body)
                            .execute();
                }
                case HTTP_CLICK -> {
                    return SimonHttpClientUtils.call(responseClass)
                            .method(method, url)
                            .param(params)
                            .header(headers)
                            .body(body)
                            .execute();
                }
                case HTTP_CLICK_PROXY -> {
                    return SimonHttpClientProxyUtils.call(responseClass)
                            .method(method, url)
                            .param(params)
                            .header(headers)
                            .body(body)
                            .execute();
                }
                case HU_TOOL_HTTP -> {
                    return SimonHuToolHttpUtils.call(responseClass)
                            .method(method, url)
                            .param(params)
                            .header(headers)
                            .body(body)
                            .execute();
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
