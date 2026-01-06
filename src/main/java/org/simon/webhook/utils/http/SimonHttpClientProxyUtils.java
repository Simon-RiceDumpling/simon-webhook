package org.simon.webhook.utils.http;

import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.simon.webhook.enums.MethodType;
import org.simon.webhook.utils.http.executor.SimonHttpProxyExecutor;
import org.simon.webhook.vo.ExecuteResult;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SimonHttpClientProxyUtils {

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

        public HttpCall<T> get(String url) {
            this.method = MethodType.GET;
            this.url = url;
            return this;
        }

        public HttpCall<T> method(MethodType method,String url) {
            this.method = method;
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
            return SimonHttpProxyExecutor.doExecute(method, buildUrl(url, params), headers,
                    Objects.isNull(body) ? new JSONObject() : new JSONObject(body.toJavaObject(Map.class)),
                    responseClass, 1);
        }
    }

    /**
     * 组装最终url
     */
    public static String buildUrl(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url) || params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);

        // 判断是否已经有 ?
        boolean hasQuery = url.contains("?");
        if (!hasQuery) {
            sb.append("?");
        } else if (!url.endsWith("?") && !url.endsWith("&")) {
            sb.append("&");
        }

        params.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            if (value instanceof Iterable<?> iterable) {
                for (Object v : iterable) {
                    appendParam(sb, key, v);
                }
            } else if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++) {
                    appendParam(sb, key, Array.get(value, i));
                }
            } else {
                appendParam(sb, key, value);
            }
        });

        // 去掉最后一个 &
        if (sb.charAt(sb.length() - 1) == '&') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    private static void appendParam(StringBuilder sb, String key, Object value) {
        if (value == null) {
            return;
        }
        try {
            sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8.name()))
                    .append("=")
                    .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.name()))
                    .append("&");
        } catch (Exception e) {
            throw new RuntimeException("url encode error", e);
        }
    }


}