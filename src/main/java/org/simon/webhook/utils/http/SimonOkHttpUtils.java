package org.simon.webhook.utils.http;

import com.alibaba.fastjson2.JSONObject;

import org.simon.webhook.enums.MethodType;
import org.simon.webhook.utils.http.executor.OkHttpExecutor;
import org.simon.webhook.vo.ExecuteResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SimonOkHttpUtils {

    /**
     * 函数式HTTP调用
     */
    public static <T> HttpCall<T> call(Class<T> responseClass) {
        return new HttpCall<>(responseClass);
    }

    public static class HttpCall<T> {
        private final Class<T> responseClass;
        private MethodType method = MethodType.GET;
        private String url;
        private Map<String, Object> headers = new HashMap<>();
        private Map<String, Object> params = new HashMap<>();
        private JSONObject body;

        private HttpCall(Class<T> responseClass) {
            this.responseClass = responseClass;
        }

        public HttpCall<T> method(MethodType method,String url) {
            this.method = method;
            this.url = url;
            return this;
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

        public HttpCall<T> body(String key, Object value) {
            if (Objects.isNull(body)) {
                this.body = new JSONObject();
            }
            this.body.put(key, value);
            return this;
        }

        public ExecuteResult<T> execute() {
            if (body != null && !body.isEmpty()) {
                Map<String, Object> bodyParams = body.toJavaObject(Map.class);
                return OkHttpExecutor.doExecute(method, url, headers, bodyParams, responseClass);
            } else {
                return OkHttpExecutor.doExecute(method, url, headers, params, responseClass);
            }
        }
    }


}