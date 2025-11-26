package org.simon.webhook.server;


import org.simon.webhook.config.ValueConfigRefresh;
import org.simon.webhook.utils.SpringUtils;
import org.simon.webhook.vo.ExecuteResult;

/**
 * @program: simon
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-21 15:52
 **/
public interface ForwardMessageSendService<R,T> {
    // 方案1：使用懒加载获取Bean
    default ValueConfigRefresh getConfigRefresh() {
        return SpringUtils.getBean(ValueConfigRefresh.class);
    }

    ExecuteResult<R> send(T params);

}
