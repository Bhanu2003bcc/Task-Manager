package com.bsingh.doto.Task.Manager.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
        sendTemplatedEmail(toEmail, "Verify Your Email", "email-verification",
                context -> context.setVariable("verificationUrl", verificationUrl));
    }

    public void sendLoginAlertEmail(String toEmail) {
        sendTemplatedEmail(toEmail, "New Login Detected", "login-alert",
                context -> context.setVariable("loginTime", LocalDateTime.now()));
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;
        sendTemplatedEmail(toEmail, "Password Reset Request", "password-reset",
                context -> context.setVariable("resetUrl", resetUrl));
    }

    private void sendTemplatedEmail(String to, String subject, String templateName,
                                    java.util.function.Consumer<Context> contextConfigurator) {
        try {
            Context context = new Context();
            contextConfigurator.accept(context);

            String htmlContent = templateEngine.process(templateName, context);
            sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to process email template", e);
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }


    private String processTemplate(String templateName, Context context) {
        try {
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process template: " + templateName, e);
        }
    }
}