package org.simon.webhook.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.simon.webhook.busines_server.BusinessCheckedServer;
import org.simon.webhook.busines_server.impl.LSCheckedService;
import org.simon.webhook.server.ForwardMessageSendService;
import org.simon.webhook.server.pushplus.FeiShuMessageSendServer;
import org.simon.webhook.server.pushplus.PushPlusServer;
import org.simon.webhook.utils.SpringUtils;

import java.util.Arrays;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-21 15:59
 **/
@Getter
@AllArgsConstructor
public enum BusinessEnum {
    HS("HS", "炉", SpringUtils.getBean(LSCheckedService.class),
            new ForwardMessageSendService[]{SpringUtils.getBean(PushPlusServer.class),
                    SpringUtils.getBean(FeiShuMessageSendServer.class)}),
    ;
    final String code;
    final String description;
    final BusinessCheckedServer checkedServer;
    final ForwardMessageSendService[] service;


    /**
     * 根据状态查询枚举
     */
    public static BusinessEnum getEnumByCode(String code) {
        return Arrays.stream(BusinessEnum.values())
                .filter(x ->
                        Arrays.stream(x.getCode().split(",")).anyMatch(code::contains)
                )
                .findFirst()
                .orElse(null);
    }
}
