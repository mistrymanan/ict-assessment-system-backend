package com.cdad.project.plagiarismservice.controller;

import com.cdad.project.plagiarismservice.service.PlagiarismService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{classroomSlug}")
public class PlagiarismController {
    final private PlagiarismService plagiarismService;

    public PlagiarismController(PlagiarismService plagiarismService) {
        this.plagiarismService = plagiarismService;
    }
    @GetMapping("{assignmentId}/{questionId}")
    public void checkPlagiarism(@PathVariable String classroomSlug, @PathVariable String assignmentId, @PathVariable String questionId, @AuthenticationPrincipal Jwt jwt){
        plagiarismService.plagiarismCheck(classroomSlug,assignmentId,questionId,jwt);
    }
}
