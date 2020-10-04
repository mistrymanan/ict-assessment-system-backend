package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.entity.CurrentUser;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exchanges.AddQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.CreateAssignmentRequest;
import com.cdad.project.assignmentservice.exchanges.GetAllAssignmentsResponse;
import com.cdad.project.assignmentservice.exchanges.UpdateAssignmentRequest;
import com.cdad.project.assignmentservice.service.AssignmentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("")
public class ManageAssignmentController {
  private final AssignmentService assignmentService;

  public ManageAssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @GetMapping("/all")
  public GetAllAssignmentsResponse getAllAssignments(@AuthenticationPrincipal Jwt jwt) {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    GetAllAssignmentsResponse response = new GetAllAssignmentsResponse();
    List<AssignmentDTO> assignments = assignmentService.getAllAssignments(user);
    response.setAssignments(assignments);
    return response;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AssignmentDTO createAssignment(@RequestBody @Valid CreateAssignmentRequest request, @AuthenticationPrincipal Jwt jwt) {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    return this.assignmentService.createAssignment(request, user);
  }

  @DeleteMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAssignment(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    this.assignmentService.deleteAssignment(id, user);
  }

  @GetMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignment(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    return this.assignmentService.getAssignmentById(id, user);
  }

  @PutMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO updateAssignment(@PathVariable String id,
                                        @RequestBody @Valid UpdateAssignmentRequest updateRequest,
                                        @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    return this.assignmentService.updateAssignment(id, updateRequest, user);
  }

  @GetMapping("/slug/{slug}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignmentBySlug(@PathVariable String slug,
                                           @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    return this.assignmentService.getAssignmentBySlug(slug, user);
  }

  @PatchMapping("id/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void toggleAssignmentStatus(@PathVariable String id,
                                     @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException {
    CurrentUser user = CurrentUser.fromJwt(jwt);
    this.assignmentService.toggleAssignmentStatus(id, user);
  }


  @ExceptionHandler(AssignmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handle(Exception e) {
    return new ErrorResponse("Not Found", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handle(IllegalArgumentException e) {
    return new ErrorResponse("Invalid ID", "Please Provide Valid Hexadecimal ID");
  }
}
