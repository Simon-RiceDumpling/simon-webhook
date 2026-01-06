package org.simon.webhook.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * String url, Map<String, Object> formParams, Map<String, Object> headers, Class<T> tClass
 * @create: 2025-07-24 10:36
 **/

/**
 * @program: atlas_oversea_micro_services
 * @description: HTTP请求执行结果封装
 * @author: renBo
 * @create: 2025-07-24 10:36
 **/
@Data
public class ExecuteResult<T> {

    /**
     * HTTP状态码
     */
    private Integer httpCode;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 原始响应体
     */
    private String rawResponse;
    private String headerResponse;
    public static <T> ExecuteResult<T> success(Integer httpCode, T data, String rawResponse) {
        return success(httpCode, data, rawResponse, null);
    }

    public static <T> ExecuteResult<T> success(Integer httpCode, T data, String rawResponse, String headerResponse) {
        ExecuteResult<T> result = new ExecuteResult<>();
        result.setHttpCode(httpCode);
        result.setSuccess(true);
        result.setData(data);
        result.setRawResponse(rawResponse);
        result.setHeaderResponse(headerResponse);
        return result;
    }

    public static <T> ExecuteResult<T> error(Integer httpCode, String message, String rawResponse) {
        return error(httpCode, message, rawResponse, null);
    }

    public static <T> ExecuteResult<T> error(Integer httpCode, String message, String rawResponse, String headerResponse) {
        ExecuteResult<T> result = new ExecuteResult<>();
        result.setHttpCode(httpCode);
        result.setSuccess(false);
        result.setMessage(message);
        result.setRawResponse(rawResponse);
        result.setHeaderResponse(headerResponse);
        return result;
    }


    public Boolean isSuccess() {
        return httpCode == 200 && success;
    }

    public Boolean isSuccessAndHasData() {
        return isSuccess() && Objects.nonNull(data);
    }
}
