package org.simon.webhook.utils.http.executor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.simon.webhook.config.HttpProxyConfigIpIdea;
import org.simon.webhook.enums.MethodType;
import org.simon.webhook.utils.JsonUtils;
import org.simon.webhook.utils.SpringUtils;
import org.simon.webhook.constants.SimonConstant;
import org.simon.webhook.vo.ExecuteResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AtlasHttpProxyUtils
 * @Description HTTP代理工具类 - 优雅版本
 * @Author renBo
 * @Date 2025/4/15 12:12
 */
@Slf4j
public class SimonHttpProxyExecutor {

    // ==================== 便捷方法 ====================

    public static ExecuteResult<JSONObject> doPostExecute(String url) {
        return doExecute(MethodType.POST, url);
    }

    public static ExecuteResult<JSONObject> doGetExecute(String url) {
        return doExecute(MethodType.GET, url);
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url) {
        return doExecute(methodType, url, new JSONObject());
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url, JSONObject requestBody) {
        return doExecute(methodType, url, new HashMap<>(), requestBody);
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url, Map<String, Object> headers, JSONObject requestBody) {
        return doExecute(methodType, url, headers, requestBody, JSONObject.class, 3);
    }

    // ==================== 核心执行方法 ====================

    /**
     * 执行带代理的HTTP请求
     */
    public static <T> ExecuteResult<T> doExecute(MethodType methodType, String url, Map<String, Object> headers,
                                                 JSONObject requestBody, Class<T> responseType, int retry) {
        log.info("执行代理HTTP请求 method:【{}】 url:【{}】 headers:【{}】 responseType:【{}】",
                methodType, url, headers, responseType.getSimpleName());
        try (CloseableHttpClient httpClient = createHttpClientWithProxy()) {
            CloseableHttpResponse response = executeRequest(httpClient, methodType, url, headers, requestBody);
            try (response) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("代理请求成功，状态码:【{}】响应:【{}】", statusCode, statusCode);
                // 检查IP封禁
                if (isIpBlocked(responseBody)) {
                    return retry > 0 ? doExecute(methodType, url, headers, requestBody, responseType, retry - 1) :
                            ExecuteResult.error(403, "代理IP被封禁: " + responseBody, null);
                }
                // 解析响应
                T data = parseResponse(responseBody, responseType);
                return ExecuteResult.success(statusCode, data, responseBody, headersToJson(response.getAllHeaders()).toJSONString());
            }
        } catch (Exception e) {
            if (retry > 0) {
                //重试
                return doExecute(methodType, url, headers, requestBody, responseType, retry - 1);
            }
            log.error("代理HTTP请求失败: method:【{}】 url:【{}】 error:【{}】", methodType, url, e.getMessage(), e);
            return ExecuteResult.error(500, e.getMessage(), null);
        }
    }

    private static JSONObject headersToJson(Header[] headers) {
        JSONObject json = new JSONObject();
        try {
            if (headers == null) {
                return json;
            }
            for (Header header : headers) {
                // 如果同名 header 多次出现，可按需改成 JSONArray
                json.put(header.getName(), header.getValue());
            }
        } catch (Exception e) {
            log.error("headersToJson error:", e);
        }
        return json;
    }

    // ==================== 兼容旧版本方法 ====================

    /**
     * @deprecated 使用 doExecute(MethodType.POST, url, headers, requestBody) 替代
     */
    @Deprecated
    public static JSONObject doPostExecuteWithProxy(String url, Object requestBody, Map<String, Object> headers) {
        ExecuteResult<JSONObject> result = doExecute(MethodType.POST, url, headers,
                requestBody instanceof JSONObject ? (JSONObject) requestBody :
                        new JSONObject(JSON.parseObject(JSON.toJSONString(requestBody))));
        JSONObject response = result.getData() != null ? result.getData() : new JSONObject();
        response.put(SimonConstant.httpCode, result.getHttpCode());
        if (!result.isSuccess()) {
            response.put(SimonConstant.errorMsg, result.getMessage());
        }
        return response;
    }

    /**
     * @deprecated 使用 doExecute(MethodType.GET, url, headers, null, responseType) 替代
     */
    @Deprecated
    public static Object doGetExecuteWithProxy(String url, Map<String, Object> headers,
                                               Map<String, Object> proxyConfig, Class<?> responseType) {
        ExecuteResult<?> result = doExecute(MethodType.GET, url, headers, new JSONObject(), responseType, 3);
        return result.getData();
    }

    // ==================== 私有辅助方法 ====================

    private static CloseableHttpResponse executeRequest(CloseableHttpClient httpClient, MethodType methodType,
                                                        String url, Map<String, Object> headers, JSONObject requestBody) throws Exception {
        switch (methodType) {
            case GET -> {
                HttpGet httpGet = new HttpGet(url);
                setHeaders(httpGet, headers);
                return httpClient.execute(httpGet);
            }
            case POST -> {
                HttpPost httpPost = new HttpPost(url);
                setHeaders(httpPost, headers);
                if (requestBody != null && !requestBody.isEmpty()) {
                    StringEntity entity = new StringEntity(requestBody.toJSONString(), StandardCharsets.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                }
                return httpClient.execute(httpPost);
            }
            default -> throw new IllegalArgumentException("不支持的请求类型: " + methodType);
        }
    }

    private static void setHeaders(org.apache.http.HttpRequest request, Map<String, Object> headers) {
        if (headers != null) {
            headers.forEach((key, value) -> request.setHeader(key, value.toString()));
        }
        // 设置默认Content-Type
        if (headers == null || !headers.containsKey("Content-Type")) {
            request.setHeader("Content-Type", "application/json");
        }
    }

    private static boolean isIpBlocked(String responseBody) {
        return responseBody != null &&
                (responseBody.contains("ip forbidden") || responseBody.contains("ip banned"));
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseResponse(String responseBody, Class<T> responseType) {
        if (responseType == String.class) {
            return (T) responseBody;
        }
        if (responseType == JSONObject.class) {
            return (T) safeParseJson(responseBody);
        }
        try {
            return JSON.parseObject(responseBody, responseType);
        } catch (Exception e) {
            log.warn("解析响应失败，返回null: {}", e.getMessage());
            return null;
        }
    }

    private static JSONObject safeParseJson(String responseBody) {
        try {
            if (!JsonUtils.isJson(responseBody)) {
                log.warn("非json格式数据");
                return new JSONObject();
            }
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return new JSONObject();
            }
            JSONObject result = JSON.parseObject(responseBody, JSONObject.class);
            return result != null ? result : new JSONObject();
        } catch (Exception e) {
            log.warn("解析JSON失败，返回空对象");
            return new JSONObject();
        }
    }

    private static CloseableHttpClient createHttpClientWithProxy() {
        HttpProxyConfigIpIdea proxyConfig = SpringUtils.getBean(HttpProxyConfigIpIdea.class);
        HttpHost proxy = new HttpHost(proxyConfig.getProxyHost(), proxyConfig.getProxyPort());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(proxy),
                new UsernamePasswordCredentials(proxyConfig.getProxyUsername(), proxyConfig.getProxyPassword())
        );
        return HttpClients.custom()
                .setProxy(proxy)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}