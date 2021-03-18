package com.cdad.project.notificationservice.controllers;

import com.cdad.project.notificationservice.exchanges.PostEmailNotification;
import com.cdad.project.notificationservice.services.EmailNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class NotificationController {
    private final EmailNotificationService emailNotificationService;

    public NotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("")
    void sendNotification(@RequestBody PostEmailNotification requestBody){
    emailNotificationService.sendEmail(requestBody.getEmails()
            ,requestBody.getSubject()
            ,requestBody.getMessage());
    }
}
