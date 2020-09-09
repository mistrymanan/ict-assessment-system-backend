package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.Assignment;
import com.cdad.project.assignmentservice.exchanges.GetAllAssignmentsResponse;
import com.cdad.project.assignmentservice.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class AssignmentController {
  private final AssignmentService assignmentService;

  public AssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @GetMapping
  public GetAllAssignmentsResponse getAllAssignments(){
    GetAllAssignmentsResponse response = new GetAllAssignmentsResponse();
    List<Assignment> assignments = assignmentService.getAllAssignments();
    response.setAssignments(assignments);
    return response;
  }
}
