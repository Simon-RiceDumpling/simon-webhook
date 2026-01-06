package org.simon.webhook.utils.http.executor;


import com.alibaba.fastjson2.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.simon.webhook.enums.MethodType;
import org.simon.webhook.vo.ExecuteResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class OkHttpExecutor {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     *  异步执行  Callback 响应结果自己处理
     */
    @SneakyThrows
    public static void enqueue(MethodType methodType,
                               String baseUrl,
                               Map<String, Object> headers,
                               Map<String, Object> requestParams,
                               Callback callback) {
        if (headers == null) headers = new HashMap<>();
        if (requestParams == null) requestParams = new HashMap<>();
        // 构造 URL（自动 URL encode）
        String url = buildUrl(baseUrl, requestParams);
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach((k, v) -> builder.addHeader(k, String.valueOf(v)));
        // POST 才有 body
        if (methodType == MethodType.POST) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create("【{}】", JSON);  // 默认空 JSON
            builder.post(body);
        } else {
            builder.get();
        }
        CLIENT.newCall(builder.build()).enqueue(callback);
    }


    @SneakyThrows
    public static <T> ExecuteResult<T> doExecute(MethodType methodType,
                                                 String baseUrl,
                                                 Map<String, Object> headers,
                                                 Map<String, Object> requestParams,
                                                 Class<T> tClass) {

        if (headers == null) headers = new HashMap<>();
        if (requestParams == null) requestParams = new HashMap<>();
        // 构造 URL（自动 URL encode）
        String url = buildUrl(baseUrl, requestParams);
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach((k, v) -> builder.addHeader(k, String.valueOf(v)));
        // POST 才有 body
        if (methodType == MethodType.POST) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create("【{}】", JSON);  // 默认空 JSON
            builder.post(body);
        } else {
            builder.get();
        }
        Request request = builder.build();
        Response response = CLIENT.newCall(request).execute();
        JSONObject responseHeaders = headersToJson(response.headers());
        if (!response.isSuccessful()) {
            return ExecuteResult.error(response.code(), response.message(), Objects.isNull(response.body()) ? "" : response.body().string(), responseHeaders.toJSONString());
        }
        String resultStr = response.body().string();
        T data = null;
        try {
            data = MAPPER.readValue(resultStr, tClass);
        } catch (Exception ignore) {
            // 如果解析失败，就把结果原样当作字符串返回
            if (tClass == String.class) {
                data = (T) resultStr;
            }
        }
        return ExecuteResult.success(200, data, null, responseHeaders.toJSONString());
    }

    private static JSONObject headersToJson(Headers headers) {
        JSONObject json = new JSONObject();
        try {
            headers.names().forEach(name -> {
                json.put(name, headers.get(name));
            });
        } catch (Exception e) {
            log.error("headersToJson error:", e);
        }
        return json;
    }


    /**
     * 构造带 query 的 URL，自动 encode
     */
    private static String buildUrl(String base, Map<String, Object> params) {
        if (params.isEmpty()) return base;

        String query = params.entrySet()
                .stream()
                .map(e -> encode(e.getKey()) + "=" + encode(String.valueOf(e.getValue())))
                .collect(Collectors.joining("&"));

        return base.contains("?") ? base + "&" + query : base + "?" + query;
    }

    private static String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}

