package org.simon.webhook.server.feishu.vo;




import lombok.Builder;
import lombok.Data;
import org.simon.webhook.enums.MessageLevelEnum;
import org.simon.webhook.enums.MessageTypeEnum;
import org.simon.webhook.enums.ReceiveIdTypeEnum;
import org.simon.webhook.server.feishu.builder.FeiShuMarkdownBuilder;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-04-11 12:12
 **/
@Data
@Builder
public class FSMessage {

    /**
     * 消息接收者的 ID，ID 类型与查询参数 receive_id_type 的取值一致。
     */
    private String receiveId;
    /**
     * 飞书消息主题
     */
    private FeiShuMarkdownBuilder contentBuilder;
    /**
     * 消息类型。 默认发送文本
     */
    private MessageTypeEnum messageType = MessageTypeEnum.TEXT;
    /**
     * 用户 ID 类型 默认是发送群组
     */
    private ReceiveIdTypeEnum receiveIdType = ReceiveIdTypeEnum.CHAT_ID;
    private MessageLevelEnum messageLevel;
}
