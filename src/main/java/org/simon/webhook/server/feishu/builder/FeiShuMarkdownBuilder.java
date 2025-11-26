package org.simon.webhook.server.feishu.builder;


import cn.hutool.extra.spring.SpringUtil;

import lombok.extern.slf4j.Slf4j;
import org.simon.webhook.enums.GradeIdentificationEnum;
import org.simon.webhook.enums.LogLevelEnum;
import org.simon.webhook.enums.MessageLevelEnum;
import org.simon.webhook.server.feishu.enums.FSBussinessEnum;
import org.simon.webhook.server.feishu.utils.FSMessageSendUtils;
import org.simon.webhook.utils.FormatUtils;
import org.simon.webhook.utils.RedisUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @program: atlas_oversea_micro_services
 * @description: 飞书消息建造者
 * @author: renBo
 * @create: 2025-07-10 10:56
 **/
@Slf4j
public class FeiShuMarkdownBuilder {
    /**
     *
     */
    public static RedisUtils redisUtils = SpringUtil.getBean(RedisUtils.class);

    private final StringBuilder builder;
    /**
     * 时长 单位分钟 多长时间警告一次 默认没有时长限制
     */
    private Integer duration;
    private String idempotent;
    /**
     * @用户的ID列表
     */
    private String[] atUserIds;

    private FeiShuMarkdownBuilder(String title) {
        builder = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            builder.append("主题:【").append(title).append("】\n");
        }
    }

    public static FeiShuMarkdownBuilder create(String title) {
        return new FeiShuMarkdownBuilder(title)
                .add("时区", TimeZone.getDefault().getDisplayName())
                .add("时间", FormatUtils.localDataTimeFormat(LocalDateTime.now()));
    }

    public static FeiShuMarkdownBuilder create(GradeIdentificationEnum grade, String title) {
        return create(grade.getCode() + title);
    }

    public static FeiShuMarkdownBuilder createConcat(String concat) {
        return create("飞书消息提醒")
                .add("正文", concat)
                .addLine("END")
                ;
    }

    public FeiShuMarkdownBuilder add(String key, Object value) {
        builder.append("- ").append(key).append(":  [")
                .append(value != null ? value : "").append("]  \n");
        return this;
    }

    /**
     * 警告时长设置 入参 分钟 多少分钟警告一次
     */
    public FeiShuMarkdownBuilder duration(Integer durationMin, String idempotent) {
        this.duration = durationMin * 60;
        this.idempotent = idempotent;
        return this;
    }

    public FeiShuMarkdownBuilder durationHour(Integer durationHour, String idempotent) {
        return duration(durationHour * 60, idempotent);
    }

    public FeiShuMarkdownBuilder durationDay(Integer durationDay, String idempotent) {
        return durationHour(durationDay * 24, idempotent);
    }

    public FeiShuMarkdownBuilder addLine(String content) {
        builder.append("- ").append(StringUtils.isEmpty(content) ? "END" : content).append("\n");
        return this;
    }

    /**
     * 添加需要艾特的用户列表
     */
    public FeiShuMarkdownBuilder addAtUserIds(String... atUserIds) {
        try {
            if (atUserIds != null && atUserIds.length > 0) {
                this.atUserIds = atUserIds;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public FeiShuMarkdownBuilder print(LogLevelEnum levelEnum) {
        switch (levelEnum) {
            case INFO:
                log.info("{}", builder.toString());
                break;
            case WARN:
                log.warn("{}", builder.toString());
                break;
            case ERROR:
                log.error("{}", builder.toString());
                break;
            default:
                log.debug("{}", builder.toString());
                break;
        }
        return this;
    }


    public String build() {
        return buildAtUsers() + builder.toString();
    }

    /**
     * 构建@用户标签
     */
    private String buildAtUsers() {
        if (atUserIds == null || atUserIds.length == 0) {
            return "";
        }
        StringBuilder atBuilder = new StringBuilder();
        for (String userId : atUserIds) {
            if (userId != null && !userId.trim().isEmpty()) {
                atBuilder.append("<at user_id=\"").append(userId).append("\">").append("</at> ");
            }
        }
        return atBuilder.toString();
    }

    public Boolean sendToChat(FSBussinessEnum... receiveId) {
        Arrays.stream(receiveId).forEach(x -> {
            if (!alreadySendToChat()) {
                FSMessageSendUtils.sendToChat(MessageLevelEnum.LEVEL_5, x, this);
            }
        });
        return Boolean.TRUE;
    }

    /**
     * 发送到群聊
     */
    public Boolean sendToChat(String... receiveId) {

        Arrays.stream(receiveId).forEach(x -> {
            if (!alreadySendToChat()) {
                FSMessageSendUtils.sendToChat(MessageLevelEnum.LEVEL_5, x, this);
            }
        });
        return Boolean.TRUE;
    }


    /**
     * 发送到多个群聊
     */
    public Boolean sendToChat(MessageLevelEnum levelEnum, String... receiveId) {
        Arrays.stream(receiveId).forEach(x -> {
            if (!alreadySendToChat()) {
                FSMessageSendUtils.sendToChat(levelEnum, x, this);
            }
        });
        return Boolean.TRUE;
    }


    public Boolean alreadySendToChat() {
        if (Objects.isNull(duration)) {
            return Boolean.FALSE;
        }
        if (StringUtils.isEmpty(idempotent)) {
            return Boolean.FALSE;
        }
        String key = "fs:" + idempotent;
        if (redisUtils.isExist(key)) {
            return Boolean.TRUE;
        }
        //存入缓存
        redisUtils.set(key, "", duration);
        add("下次告警时间", LocalDateTime.now().plusMinutes(duration).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Boolean.FALSE;
    }

    /**
     * 发送续爱许
     */
    public Boolean sendToUser(String receiveId) {
        return sendToUser(MessageLevelEnum.LEVEL_5, receiveId);
    }

    /**
     * 发送续爱许
     */
    public Boolean sendToUser(MessageLevelEnum levelEnum, String receiveId) {
        return FSMessageSendUtils.sendToUser(levelEnum, receiveId, this);
    }
}

