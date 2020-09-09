package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.dto.Assignment;
import com.cdad.project.assignmentservice.entity.AssignmentEntity;
import com.cdad.project.assignmentservice.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AssignmentService {

  private final AssignmentRepository assignmentRepository;

  public AssignmentService(AssignmentRepository assignmentRepository) {
    this.assignmentRepository = assignmentRepository;
  }

  public List<Assignment> getAllAssignments(){
//    List<AssignmentEntity> assignmentEntities = this.assignmentRepository.findAll();
//    assignmentEntities.forEach(System.out::println);
    return this.assignmentRepository
            .findAll()
            .stream()
            .map(assignment -> new Assignment(assignment.getTitle()))
            .collect(Collectors.toList());
  }
}
