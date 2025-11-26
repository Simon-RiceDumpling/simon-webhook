package org.simon.webhook.enums;

import lombok.Getter;

import java.util.Arrays;

/** 
* @Description:  消息类型
* @Param: 
* @return: 
* @Author: renBo
* @Date: 2025/4/11
*/
@Getter
public enum MessageTypeEnum {
    TEXT("text", "文本"),
    POST("post", "富文本"),
    IMAGE("image", "图片"),
    FILE("file", "文件"),
    AUDIO("audio", "语音"),
    MEDIA("media", "视频"),
    STICKER("sticker", "表情包"),
    INTERACTIVE("interactive", "卡片"),
    SHARE_CHAT("share_chat", "分享群名片"),
    SHARE_USER("share_user", "分享个人名片"),
    SYSTEM("system", "系统消息");

    private String status;
    private String value;

    MessageTypeEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

    /**
     * 根据状态查询枚举
     */
    public static MessageTypeEnum getEnumByStatus(String status) {
        return Arrays.stream(MessageTypeEnum.values())
                .filter(x -> x.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(null);
    }
}
