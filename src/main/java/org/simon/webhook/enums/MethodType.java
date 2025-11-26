package org.simon.webhook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MethodType {
    POST("POST"),
    GET("GET"),
    DELETE("DELETE"),
    PUT("PUT"),
    ;

    final String code;


    /**
     * 根据状态查询枚举
     */
    public static MethodType getEnumByCode(String code) {
        return Arrays.stream(MethodType.values())
                .filter(x ->
                        Arrays.stream(x.getCode().split(",")).anyMatch(code::contains)
                )
                .findFirst()
                .orElse(null);
    }
}
