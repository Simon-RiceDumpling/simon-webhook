package org.simon.webhook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-07-09 11:09
 **/
@Getter
@AllArgsConstructor
public enum GradeIdentificationEnum {
    error("❌", "错误"),
    error2("❌❌", "错误等级2"),
    error3("❌❌❌", "错误等级3"),
    error4("❌❌❌❌", "错误等级4"),
    error5("❌❌❌❌❌", "错误等级5"),
    warn("⚠️", "警告"),
    ok("✅️", "ok"),
    nothing("", ""),
    ;
    final String code;
    final String description;


    /**
     * 根据状态查询枚举
     */
    public static GradeIdentificationEnum getEnumByCode(String code) {
        return Arrays.stream(GradeIdentificationEnum.values())
                .filter(x ->
                        Arrays.stream(x.getCode().split(",")).anyMatch(code::contains)
                )
                .findFirst()
                .orElse(null);
    }
}
