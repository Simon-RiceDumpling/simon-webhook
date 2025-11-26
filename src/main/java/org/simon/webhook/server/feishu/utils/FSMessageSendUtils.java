package org.simon.webhook.server.feishu.utils;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.JsonParser;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import lombok.extern.slf4j.Slf4j;
import org.simon.webhook.enums.MessageLevelEnum;
import org.simon.webhook.enums.MessageTypeEnum;
import org.simon.webhook.enums.ReceiveIdTypeEnum;
import org.simon.webhook.server.feishu.builder.FeiShuMarkdownBuilder;
import org.simon.webhook.server.feishu.enums.FSBussinessEnum;
import org.simon.webhook.server.feishu.vo.FSMessage;
import org.simon.webhook.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @program: atlas_oversea_micro_services
 * @description: 飞书消息发送类
 * @author: renBo
 * @create: 2025-04-11 12:23
 **/
@Component
@Slf4j
public class FSMessageSendUtils {

    public static final RateLimiter fsMessageRate = RateLimiter.create(5);

    @Value("${feishu.appId:cli_a7723c0b9271900b}")
    private String appId;
    @Value("${feishu.appSecret:Y6LrKd1xqe9F1kGGWdzQtehZrDQBs0s7}")
    private String appSecret;
    @Value("${feishu.messageLevel:1}")
    private Integer messageLevel;

    /**
     * 发送消费给用户
     *
     * @param receiveId 用户id https://open.feishu.cn/document/server-docs/im-v1/message/create
     * @param content   消息体
     */
    public static Boolean sendToUser(String receiveId, String content) {

        return sendToUser(receiveId, FeiShuMarkdownBuilder.createConcat(content));
    }

    /**
     * 发送消费给用户
     *
     * @param receiveId      用户id https://open.feishu.cn/document/server-docs/im-v1/message/create
     * @param contentBuilder 消息体
     */
    public static Boolean sendToUser(String receiveId, FeiShuMarkdownBuilder contentBuilder) {
        return sendToUser(MessageLevelEnum.LEVEL_1, receiveId, contentBuilder);
    }

    /**
     * 发送消息给用户
     */
    public static Boolean sendToUser(MessageLevelEnum levelEnum, String receiveId, FeiShuMarkdownBuilder contentBuilder) {
        return SpringUtils.getBean(FSMessageSendUtils.class).doSend(FSMessage.builder()
                .receiveId(receiveId)
                .messageLevel(levelEnum)
                .messageType(MessageTypeEnum.TEXT)
                .receiveIdType(ReceiveIdTypeEnum.USER_ID)
                .contentBuilder(contentBuilder)
                .build());
    }

    /**
     * 发送消费给群聊
     *
     * @param bussinessEnum 群聊枚举
     * @param content       消息体
     */
    public static Boolean sendToChat(FSBussinessEnum bussinessEnum, String content) {
        return sendToChat(bussinessEnum.getStatus(), content);
    }

    /**
     * 发送消费给群聊
     *
     * @param receiveId 群聊id https://open.feishu.cn/document/server-docs/im-v1/message/create
     * @param content   消息体
     */
    public static Boolean sendToChat(String receiveId, String content) {
        return sendToChat(MessageLevelEnum.LEVEL_1, receiveId, FeiShuMarkdownBuilder.create(content));
    }


    /**
     * 发送消费给群聊
     *
     * @param bussinessEnum  群聊id https://open.feishu.cn/document/server-docs/im-v1/message/create
     * @param contentBuilder 消息体
     */
    public static Boolean sendToChat(FeiShuMarkdownBuilder contentBuilder,FSBussinessEnum... bs) {
     Arrays.stream(bs).forEach(x->{
         sendToChat(x, contentBuilder);
     });
        return Boolean.TRUE;
    }

    public static Boolean sendToChat(FSBussinessEnum bussinessEnum, FeiShuMarkdownBuilder contentBuilder) {
        return sendToChat(MessageLevelEnum.LEVEL_1, bussinessEnum, contentBuilder);
    }

    public static Boolean sendToChat(MessageLevelEnum levelEnum, FSBussinessEnum bussinessEnum, FeiShuMarkdownBuilder contentBuilder) {
        return sendToChat(levelEnum, bussinessEnum.getStatus(), contentBuilder);
    }

    /**
     * 发送消费给群聊
     *
     * @param receiveId      群聊id https://open.feishu.cn/document/server-docs/im-v1/message/create
     * @param contentBuilder 消息体
     */
    public static Boolean sendToChat(MessageLevelEnum levelEnum, String receiveId, FeiShuMarkdownBuilder contentBuilder) {
        return SpringUtils.getBean(FSMessageSendUtils.class).doSend(FSMessage.builder()
                .receiveId(receiveId)
                .messageLevel(levelEnum)
                .receiveIdType(ReceiveIdTypeEnum.CHAT_ID)
                .messageType(MessageTypeEnum.TEXT)
                .contentBuilder(contentBuilder)
                .build());

    }


    /**
     * 发送消息-最终执行
     */
    private synchronized Boolean doSend(FSMessage message) {
        try {
            fsMessageRate.acquire();
            log.warn("message send start message:【{}】", message);
            if (!Objects.isNull(message.getMessageLevel()) && message.getMessageLevel().getLevel() < messageLevel) {
                //消息级别小于阈值的不发送告警
                return Boolean.TRUE;
            }
            // 创建请求对象
            CreateMessageReq req = CreateMessageReq.newBuilder()
                    .receiveIdType(message.getReceiveIdType().getStatus())
                    .createMessageReqBody(CreateMessageReqBody.newBuilder()
                            .receiveId(message.getReceiveId())
                            .msgType(Objects.isNull(message.getMessageType()) ? MessageTypeEnum.TEXT.getStatus() : message.getMessageType().getStatus())
                            .content(createTextMessage(message.getContentBuilder().build()))
                            .build())
                    .build();
            // 发起请求
            CreateMessageResp resp = Client.newBuilder(appId, appSecret).build().im().v1().message().create(req);
            // 处理服务端错误
            if (!resp.success()) {
                log.error(" message send fail code:【{}】msg:【{}】reqId:【{}】resp:【{}】",
                        resp.getCode(),
                        resp.getMsg(),
                        resp.getRequestId(),
                        Jsons.createGSON(true, false)
                                .toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)))
                );
                return Boolean.FALSE;
            }
            // 业务数据处理
            log.info("message send success");
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("message send error :【{}】", e.getMessage());
            return Boolean.FALSE;
        }
    }

    /**
     * 创建消息体
     */
    public String createTextMessage(String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", content);
        return jsonObject.toJSONString();
    }
}
