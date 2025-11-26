package org.simon.webhook.enums;

import lombok.Getter;

import java.util.Arrays;

/** 
* @Description:  消息发送的消费选择类型
* @Param: 
* @return: 
* @Author: renBo
* @Date: 2025/4/11
*/
@Getter
public enum ReceiveIdTypeEnum {
    OPEN_ID("open_id", "标识一个用户在某个应用中的身份。同一个用户在不同应用中的 Open ID 不同"),
    UNION_ID("union_id", "标识一个用户在某个应用开发商下的身份"),
    USER_ID("user_id", "标识一个用户在某个租户内的身份"),
    EMAIL("email", "以用户的真实邮箱来标识用户。"),
    CHAT_ID("chat_id", "以群 ID 来标识群聊。");

    private String status;
    private String value;

    ReceiveIdTypeEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

    /**
     * 根据状态查询枚举
     */
    public static ReceiveIdTypeEnum getEnumByStatus(String status) {
        return Arrays.stream(ReceiveIdTypeEnum.values())
                .filter(x -> x.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(null);
    }
}
