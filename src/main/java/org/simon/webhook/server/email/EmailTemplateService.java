package org.simon.webhook.server.email;

/**
 * @program: webhook
 * @description: TODO
 * @author: renBo
 * @create: 2025-11-27 11:13
 **/


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    /**
     * 发送模板邮件
     */
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);
        emailService.sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * 发送验证码邮件
     */
    public void sendVerificationCode(String to, String code) {
        String subject = "验证码";
        String content = String.format("""
            <html>
            <body>
                <h2>验证码</h2>
                <p>您的验证码是：<strong style="color: #007bff; font-size: 18px;">%s</strong></p>
                <p>验证码5分钟内有效，请及时使用。</p>
                <p>如果这不是您的操作，请忽略此邮件。</p>
            </body>
            </html>
            """, code);

        emailService.sendHtmlEmail(to, subject, content);
    }

    /**
     * 发送欢迎邮件
     */
    public void sendWelcomeEmail(String to, String username) {
        String subject = "欢迎注册";
        String content = String.format("""
            <html>
            <body>
                <h2>欢迎 %s！</h2>
                <p>感谢您注册我们的服务。</p>
                <p>如有任何问题，请随时联系我们。</p>
                <hr>
                <p><small>此邮件由系统自动发送，请勿回复。</small></p>
            </body>
            </html>
            """, username);

        emailService.sendHtmlEmail(to, subject, content);
    }
}