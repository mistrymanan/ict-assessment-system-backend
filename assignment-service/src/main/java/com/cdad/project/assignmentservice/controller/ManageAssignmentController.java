package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exchanges.AddQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.CreateAssignmentRequest;
import com.cdad.project.assignmentservice.exchanges.GetAllAssignmentsResponse;
import com.cdad.project.assignmentservice.exchanges.UpdateAssignmentRequest;
import com.cdad.project.assignmentservice.service.AssignmentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("")
public class ManageAssignmentController {
  private final AssignmentService assignmentService;

  public ManageAssignmentController(AssignmentService assignmentService){
    this.assignmentService = assignmentService;
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

  @DeleteMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAssignment(@PathVariable String id) {
    this.assignmentService.deleteAssignment(id);
  }

  @GetMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignment(@PathVariable String id) throws AssignmentNotFoundException {
    return this.assignmentService.getAssignmentById(id);
  }
  @PutMapping("id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO updateAssignment(@PathVariable String id, @RequestBody @Valid UpdateAssignmentRequest updateRequest) throws AssignmentNotFoundException {
    return this.assignmentService.updateAssignment(id, updateRequest);
  }
  @GetMapping("/slug/{slug}")
  @ResponseStatus(HttpStatus.OK)
  public AssignmentDTO getAssignmentBySlug(@PathVariable String slug) throws AssignmentNotFoundException {
    return this.assignmentService.getAssignmentBySlug(slug);
  }

  @PatchMapping("id/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void toggleAssignmentStatus(@PathVariable String id) throws AssignmentNotFoundException {
    this.assignmentService.toggleAssignmentStatus(id);
  }



  @ExceptionHandler(AssignmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handle(Exception e){
    return new ErrorResponse("Not Found", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handle(IllegalArgumentException e) { return new ErrorResponse("Invalid ID", "Please Provide Valid Hexadecimal ID");}
}
