package org.simon.webhook.server.feishu.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Description: 消息发送的消费选择类型
 * @Param:
 * @return:
 * @Author: renBo
 * @Date: 2025/4/11
 */
@Getter
public enum FSBussinessEnum {
    SIMON_FS("oc_2246dfc38f192098502a24a633fe7c22", "simon")
    ;

    private String status;
    private String value;

    FSBussinessEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

    /**
     * 根据状态查询枚举
     */
    public static FSBussinessEnum getEnumByStatus(String status) {
        return Arrays.stream(FSBussinessEnum.values())
                .filter(x -> x.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(null);
    }
}
