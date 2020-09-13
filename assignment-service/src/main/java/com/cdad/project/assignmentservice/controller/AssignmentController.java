package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exchanges.AddQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.CreateAssignmentRequest;
import com.cdad.project.assignmentservice.exchanges.GetAllAssignmentsResponse;
import com.cdad.project.assignmentservice.service.AssignmentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("assignments")
public class AssignmentController {
  private final AssignmentService assignmentService;
  private final ModelMapper mapper;

  public AssignmentController(AssignmentService assignmentService, ModelMapper mapper) {
    this.assignmentService = assignmentService;
    this.mapper = mapper;
  }

  @GetMapping("/all")
  public GetAllAssignmentsResponse getAllAssignments() {
    GetAllAssignmentsResponse response = new GetAllAssignmentsResponse();
    List<AssignmentDTO> assignments = assignmentService.getAllAssignments();
    response.setAssignments(assignments);
    return response;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AssignmentDTO createAssignment(@RequestBody @Valid CreateAssignmentRequest request) {
    return this.assignmentService.createAssignment(request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAssignment(@PathVariable String id) {
    this.assignmentService.deleteAssignment(id);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignment(@PathVariable String id) throws AssignmentNotFoundException {
    AssignmentDTO assignment = this.assignmentService.getAssignmentById(id);
    return assignment;
  }
  @GetMapping("/slug/{slug}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignmentBySlug(@PathVariable String slug) throws AssignmentNotFoundException {
    AssignmentDTO assignment = this.assignmentService.getAssignmentBySlug(slug);
    return assignment;
  }


  @ExceptionHandler(AssignmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handle(Exception e){
    return new ErrorResponse("Not Found", e.getMessage());
  }
}
