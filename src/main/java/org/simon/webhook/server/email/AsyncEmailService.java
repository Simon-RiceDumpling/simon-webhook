package org.simon.webhook.server.email;

/**
 * @program: webhook
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-27 11:33
 **/


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {

    private final EmailService emailService;

    @Async
    public void sendEmailAsync(String to, String subject, String content) {
        try {
            log.info("开始异步发送邮件: {}", to);
            emailService.sendSimpleEmail(to, subject, content);
            log.info("异步邮件发送完成: {}", to);
        } catch (Exception e) {
            log.error("异步邮件发送失败: {}", e.getMessage());
        }
    }
}
