package com.cdad.project.classroomservice.serviceclient.notificationservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEmailNotification {
    private HashSet<String> emails;
    private String subject;
    private String message;
}
