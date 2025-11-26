package org.simon.webhook.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-21 15:46
 **/
@Component
@Data
@Slf4j
public class ValueConfigRefresh {

    @Value("${push.plus.token:c7c54509487648678ab98beb1d47d4f6}")
    private String pushPlusToken;

}
