package com.shopping.notification.controller;

import com.shopping.notification.model.Notification;
import com.shopping.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Endpoints for managing notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send a new notification")
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.sendNotification(notification));
    }

    @GetMapping("/recipient/{recipient}")
    @Operation(summary = "Get notifications by recipient")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(notificationService.getNotificationsByStatus(status));
    }

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notifications")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }
} 