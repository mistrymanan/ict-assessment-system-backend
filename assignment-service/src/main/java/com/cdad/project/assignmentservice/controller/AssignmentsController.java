package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.ActiveAssignmentDTO;
import com.cdad.project.assignmentservice.dto.ActiveAssignmentDetailsDTO;
import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.dto.UserQuestionDTO;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.GetActiveQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.GetAllActiveAssignmentsResponse;
import com.cdad.project.assignmentservice.service.ActiveAssignmentService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("{classroomSlug}/active-assignments")
public class AssignmentsController {

  private final ActiveAssignmentService activeAssignmentService;

  public AssignmentsController(ActiveAssignmentService activeAssignmentService) {
    this.activeAssignmentService = activeAssignmentService;
  }

  @GetMapping("/all")
  public GetAllActiveAssignmentsResponse getAllActiveAssignments(@PathVariable String classroomSlug
            ,@AuthenticationPrincipal Jwt jwt) {
    GetAllActiveAssignmentsResponse response = new GetAllActiveAssignmentsResponse();
    List<ActiveAssignmentDTO> activeAssignments = this.activeAssignmentService.getAll(classroomSlug,jwt);
    response.setActiveAssignments(activeAssignments);
    return response;
  }

  @GetMapping("slug/{slug}")
  public ActiveAssignmentDetailsDTO getActiveAssignment(@PathVariable String classroomSlug,
                                                        @PathVariable String slug,
                                                        @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    return this.activeAssignmentService.getDetails(classroomSlug,slug, jwt);
  }

  @GetMapping("id/{id}")
  public ActiveAssignmentDetailsDTO getActiveAssignmentById(@PathVariable String classroomSlug,
                                                            @PathVariable String id,
                                                            @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    return this.activeAssignmentService.getDetailsById(id,classroomSlug, jwt);
  }

  @GetMapping("get-question")
  public UserQuestionDTO getQuestion(@PathVariable String classroomSlug,
                                     @Valid GetActiveQuestionRequest request,
                                     @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException {
    return this.activeAssignmentService.getActiveQuestion(request,classroomSlug, jwt);
  }

  @ExceptionHandler(AssignmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handle(Exception e) {
    return new ErrorResponse("Not Found", e.getMessage());
  }


}
