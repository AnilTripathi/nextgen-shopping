package com.shopping.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${notification.email.retry-attempts}")
    private int retryAttempts;

    @Value("${notification.email.retry-delay}")
    private long retryDelay;

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        int attempts = 0;
        MessagingException lastException = null;

        while (attempts < retryAttempts) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);
                
                mailSender.send(message);
                log.info("Email sent successfully to: {}", to);
                return;
            } catch (MessagingException e) {
                lastException = e;
                attempts++;
                log.warn("Failed to send email to: {} (Attempt: {})", to, attempts, e);
                
                if (attempts < retryAttempts) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new MessagingException("Email sending interrupted", ie);
                    }
                }
            }
        }

        log.error("Failed to send email after {} attempts", retryAttempts);
        throw lastException;
    }
} 