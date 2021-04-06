package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.UserQuestionResponseDTO;
import com.cdad.project.gradingservice.service.SubmissionPlagarismService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("public-plagarism")
public class PublicPlagarismController {
    private final SubmissionPlagarismService submissionPlagarismService;

    public PublicPlagarismController(SubmissionPlagarismService submissionPlagarismService) {
        this.submissionPlagarismService = submissionPlagarismService;
    }

    @GetMapping("{assignmentId}/{questionId}")
    public List<UserQuestionResponseDTO> getUserQuestionResponses(@PathVariable String assignmentId, @PathVariable String questionId,
                                                                  @AuthenticationPrincipal Jwt jwt){
        return this.submissionPlagarismService.getUsersQuestionResponseDTO(assignmentId,questionId,jwt);
    }
}
