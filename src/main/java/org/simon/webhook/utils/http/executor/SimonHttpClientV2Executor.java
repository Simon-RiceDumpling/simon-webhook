package org.simon.webhook.utils.http.executor;



import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.simon.webhook.constants.SimonConstant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * @ClassName HttpUtils
 * @Description
 * @Author renBo
 * @Date 2025/4/15 12:12
 */
@Slf4j
public class SimonHttpClientV2Executor {


    /**
     * Post请求调用
     */
    @SneakyThrows
    public static Object doPostExecute(String url, Map<String, Object> params, Class tClass) {
        PostMethod postMethod = new PostMethod();
        JSONObject json = new JSONObject();
        json.putAll(params);
        postMethod.setRequestBody(json.toJSONString());
        return execute(postMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doPostExecute(String url, JSONObject json, Class tClass) {
        PostMethod postMethod = new PostMethod();
        postMethod.setRequestBody(json.toJSONString());
        return execute(postMethod, url, tClass);
    }


    @SneakyThrows
    public static Object doPostExecute(String url, JSONObject params, Map<String, Object> headers, Class tClass) {
        PostMethod postMethod = new PostMethod();
        headers.entrySet().forEach(set -> postMethod.setRequestHeader(set.getKey(), set.getValue().toString()));
        postMethod.setRequestBody(params.toJSONString());
        return execute(postMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doGetExecuteHeaderParams(String baseUrl, Map<String, String> headers, Map<String, Object> params, Class tClass) {
        GetMethod getMethod = new GetMethod();
        headers.entrySet().forEach(set -> getMethod.addRequestHeader(set.getKey(), set.getValue()));
        StringBuilder[] url = {new StringBuilder(baseUrl)};
        params.entrySet().forEach(obj -> {
            if (url[0].toString().contains("?")) {
                url[0] = url[0].append("&" + obj.getKey() + "=" + obj.getValue());
            } else {
                url[0] = url[0].append("?" + obj.getKey() + "=" + obj.getValue());
            }
        });
        return execute(getMethod, url.toString(), tClass);
    }

    /**
     * Get请求调用
     */
    @SneakyThrows
    public static Object doGetExecuteHeaderParams(String url, Map<String, Object> headers, Class tClass) {
        GetMethod getMethod = new GetMethod();
        headers.entrySet().forEach(set -> getMethod.addRequestHeader(set.getKey(), String.valueOf(set.getValue())));
        return execute(getMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doGetExecuteRequestParams(String baseUrl, Class tClass) {
        GetMethod getMethod = new GetMethod();
        final StringBuilder[] url = {new StringBuilder(baseUrl)};
        return execute(getMethod, url[0].toString(), tClass);
    }

    @SneakyThrows
    public static JSONObject doGetExecuteRequestParams(String baseUrl, Map<String, Object> params, Integer retryCount) {
        JSONObject result = (JSONObject) doGetExecuteRequestParams(baseUrl, params, JSONObject.class);
        if (result.getInteger(SimonConstant.httpCode) != 200 && retryCount > 0) {
            return doGetExecuteRequestParams(baseUrl, params, retryCount - 1);
        }
        return result;

    }

    @SneakyThrows
    public static Object doGetExecuteRequestParams(String baseUrl, Map<String, Object> params, Class tClass) {
        GetMethod getMethod = new GetMethod();
        final StringBuilder[] url = {new StringBuilder(baseUrl)};
        params.entrySet().forEach(obj -> {
            if (url[0].toString().contains("?")) {
                url[0] = url[0].append("&" + obj.getKey() + "=" + obj.getValue());
            } else {
                url[0] = url[0].append("?" + obj.getKey() + "=" + obj.getValue());
            }
        });
        return execute(getMethod, url[0].toString(), tClass);
    }

    /**
     * 执行调用
     */
    @SneakyThrows
    private static Object execute(HttpMethodBase httpMethod, String url, Class tClass) {
        httpMethod.setRequestHeader("Content-Type", "application/json");
        HttpClient httpClient = new HttpClient();
        httpMethod.setPath(url);
        int httpCode = httpClient.executeMethod(httpMethod);
        //响应转码 统一转为utf-8
        JSONObject jsonObject = parseJson(httpMethod);
        jsonObject.put(SimonConstant.httpCode, httpCode);
        log.debug("url->:【{}】响应结果:【{}】 ", url, jsonObject);
        return JSONObject.parseObject(jsonObject.toString(), tClass);
    }


    /**
     * 解析数据
     */
    public static JSONObject parseJson(HttpMethodBase httpMethod) {
        try {
            String responseBodyAsString = httpMethod.getResponseBodyAsString();
            if (responseBodyAsString.equals(new String(responseBodyAsString.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                responseBodyAsString = new String(responseBodyAsString.getBytes("ISO-8859-1"), "utf-8");
            }
            if (StringUtils.isNotBlank(responseBodyAsString) && isJson(responseBodyAsString)) {
                return JSONObject.from(JSON.parseObject(responseBodyAsString));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("responseMsg", responseBodyAsString);
            return jsonObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static Boolean isJson(String responseBodyAsString) {
        try {
            JSONObject.from(JSON.parseObject(responseBodyAsString));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 下载 Appsflyer 报告并传递 InputStream（不落地）
     *
     * @param url           Appsflyer 报告下载地址
     * @param apiToken      授权 API Token
     * @param inputConsumer InputStream 消费函数（你可以在这里上传到 OBS）
     * @throws IOException 异常
     */
    public static void downloadReport(String url, String apiToken,
                                      Consumer<InputStream> inputConsumer,
                                      BiConsumer<Integer, String> errorConsume) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Bearer " + apiToken);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (status == 200) {
                    if (entity != null) {
                        try (InputStream inputStream = entity.getContent()) {
                            inputConsumer.accept(inputStream); // 执行用户自定义处理，如上传到OBS
                        }
                    }
                } else {
                    // 读取错误响应内容
                    String errorMsg = "";
                    if (entity != null) {
                        try (InputStream errorStream = entity.getContent()) {
                            errorMsg = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (Exception e) {
                            errorMsg = "读取错误信息失败: " + e.getMessage();
                        }
                    }
                    // 传递给错误处理器
                    if (errorConsume != null) {
                        errorConsume.accept(status, errorMsg);
                    }
                }
            }
        }
    }

}