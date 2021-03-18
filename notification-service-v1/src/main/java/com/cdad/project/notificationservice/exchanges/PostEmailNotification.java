package com.cdad.project.notificationservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostEmailNotification {
    private List<String> emails;
    private String subject;
    private String message;
}
