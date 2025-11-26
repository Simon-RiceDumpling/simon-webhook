package org.simon.webhook.enums;

import lombok.Getter;

/**
 * @Description: 消息类型
 * @Param:
 * @return:
 * @Author: renBo
 * @Date: 2025/4/11
 */
@Getter
public enum MessageLevelEnum {
    LEVEL_0(0),
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5),
    LEVEL_6(6),
    LEVEL_7(7),
    LEVEL_8(8),
    LEVEL_9(9),
    LEVEL_10(10);

    private Integer level;

    MessageLevelEnum(Integer level) {
        this.level = level;
    }

}
