package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.ActiveAssignmentDTO;
import com.cdad.project.assignmentservice.dto.ActiveAssignmentDetailsDTO;
import com.cdad.project.assignmentservice.dto.UserQuestionDTO;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.GetActiveQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.GetAllActiveAssignmentsResponse;
import com.cdad.project.assignmentservice.service.ActiveAssignmentService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("active-assignments")
public class AssignmentsController {

  private final ActiveAssignmentService activeAssignmentService;

  public AssignmentsController(ActiveAssignmentService activeAssignmentService) {
    this.activeAssignmentService = activeAssignmentService;
  }

  @GetMapping("/all")
  public GetAllActiveAssignmentsResponse getAllActiveAssignments() {
    GetAllActiveAssignmentsResponse response = new GetAllActiveAssignmentsResponse();
    List<ActiveAssignmentDTO> activeAssignments = this.activeAssignmentService.getAll();
    response.setActiveAssignments(activeAssignments);
    return response;
  }

  @GetMapping("slug/{slug}")
  public ActiveAssignmentDetailsDTO getActiveAssignment(@PathVariable String slug) throws AssignmentNotFoundException {
    return this.activeAssignmentService.getDetails(slug);
  }
  @GetMapping("id/{id}")
  public ActiveAssignmentDetailsDTO getActiveAssignmentById(@PathVariable String id) throws AssignmentNotFoundException {
    return this.activeAssignmentService.getDetailsById(id);
  }

  @GetMapping("get-question")
  public UserQuestionDTO getQuestion(@Valid GetActiveQuestionRequest request) throws AssignmentNotFoundException, QuestionNotFoundException {
    return this.activeAssignmentService.getActiveQuestion(request);
  }
}
