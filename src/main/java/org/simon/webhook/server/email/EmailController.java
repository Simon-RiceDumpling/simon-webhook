package org.simon.webhook.server.email;

/**
 * @program: webhook
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-27 11:32
 **/


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    /**
     * 发送简单邮件
     */
    @PostMapping("/simple")
    public String sendSimpleEmail(@RequestParam String to,
                                  @RequestParam String subject,
                                  @RequestParam String content) {
        emailService.sendSimpleEmail(to, subject, content);
        return "邮件发送成功";
    }

    /**
     * 发送HTML邮件
     */
    @PostMapping("/html")
    public String sendHtmlEmail(@RequestParam String to,
                                @RequestParam String subject,
                                @RequestParam String htmlContent) {
        emailService.sendHtmlEmail(to, subject, htmlContent);
        return "HTML邮件发送成功";
    }

    /**
     * 发送验证码
     */
    @PostMapping("/verification")
    public String sendVerificationCode(@RequestParam String to,
                                       @RequestParam String code) {
        emailTemplateService.sendVerificationCode(to, code);
        return "验证码发送成功";
    }

    /**
     * 发送欢迎邮件
     */
    @PostMapping("/welcome")
    public String sendWelcomeEmail(@RequestParam String to,
                                   @RequestParam String username) {
        emailTemplateService.sendWelcomeEmail(to, username);
        return "欢迎邮件发送成功";
    }
}