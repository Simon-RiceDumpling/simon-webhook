package org.simon.webhook.server.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单文本邮件
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("简单邮件发送成功: {}", to);
        } catch (Exception e) {
            log.error("简单邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送HTML邮件
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true表示HTML格式

            mailSender.send(message);
            log.info("HTML邮件发送成功: {}", to);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送带附件的邮件
     */
    public void sendEmailWithAttachment(String to, String subject, String content, File attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            if (attachment != null && attachment.exists()) {
                helper.addAttachment(attachment.getName(), attachment);
            }

            mailSender.send(message);
            log.info("带附件邮件发送成功: {}", to);
        } catch (MessagingException e) {
            log.error("带附件邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 批量发送邮件
     */
    public void sendBatchEmail(String[] toList, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(toList);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("批量邮件发送成功，收件人数量: {}", toList.length);
        } catch (Exception e) {
            log.error("批量邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}