package com.shopping.notification.service;

import com.shopping.notification.model.Notification;
import com.shopping.notification.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public Notification sendNotification(Notification notification) {
        notification.setStatus("PENDING");
        notification = notificationRepository.save(notification);

        try {
            if ("EMAIL".equals(notification.getType())) {
                emailService.sendEmail(
                    notification.getRecipient(),
                    notification.getSubject(),
                    notification.getContent()
                );
                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
            } else {
                throw new UnsupportedOperationException("Notification type not supported: " + notification.getType());
            }
        } catch (MessagingException e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification: {}", e.getMessage());
        }

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }

    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus("FAILED");
        for (Notification notification : failedNotifications) {
            try {
                if ("EMAIL".equals(notification.getType())) {
                    emailService.sendEmail(
                        notification.getRecipient(),
                        notification.getSubject(),
                        notification.getContent()
                    );
                    notification.setStatus("SENT");
                    notification.setSentAt(LocalDateTime.now());
                    notification.setErrorMessage(null);
                    notificationRepository.save(notification);
                }
            } catch (MessagingException e) {
                log.error("Failed to retry notification {}: {}", notification.getId(), e.getMessage());
            }
        }
    }
} 