package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.controller.QuestionsController;
import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.entity.TestCase;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.AddQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.CreateAssignmentRequest;
import com.cdad.project.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.UpdateAssignmentRequest;
import com.cdad.project.assignmentservice.repository.AssignmentRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Log4j2
public class AssignmentService {
  private final ModelMapper mapper;

  private final AssignmentRepository assignmentRepository;

  public AssignmentService(AssignmentRepository assignmentRepository, ModelMapper mapper, MongoTemplate mongo) {
    this.assignmentRepository = assignmentRepository;
    this.mapper = mapper;
  }

  public List<AssignmentDTO> getAllAssignments() {
//    List<AssignmentEntity> assignmentEntities = this.assignmentRepository.findAll();
//    assignmentEntities.forEach(System.out::println);
    return this.assignmentRepository
            .findAll()
//            .find(query, Assignment.class)
            .stream()
            .map(assignment -> mapper.map(assignment, AssignmentDTO.class))
            .collect(Collectors.toList());
  }

  public AssignmentDTO createAssignment(CreateAssignmentRequest assignment) {
    Assignment newAssignment = mapper.map(assignment, Assignment.class);
    newAssignment.setStatus("ACTIVE");
    newAssignment.setSlug(slugify(assignment.getTitle()));
    newAssignment = this.assignmentRepository.save(newAssignment);
    return mapper.map(newAssignment, AssignmentDTO.class);
  }
  public AssignmentDTO updateAssignment(String id, UpdateAssignmentRequest request) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findById(id);
    if (assignmentEntity.isPresent()) {
      Assignment assignment = assignmentEntity.get();
      this.mapper.map(request, assignment);
      assignment.setSlug(slugify(assignment.getTitle()));
      this.assignmentRepository.save(assignment);
      return mapper.map(assignment,AssignmentDTO.class);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with id '%s' not found.", id));
    }
  }
  private String slugify(String str) {
    return String.join("-", str.trim().toLowerCase().split("\\s+"));
  }

  public void deleteAssignment(String id) {
    this.assignmentRepository.deleteById(id);
  }

  public AssignmentDTO getAssignmentById(String id) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findById(id);
    if (assignmentEntity.isPresent()) {
      return mapper.map(assignmentEntity.get(), AssignmentDTO.class);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with id '%s' not found.", id));
    }
  }

  public AssignmentDTO getAssignmentBySlug(String slug) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlug(slug);
    if (assignmentEntity.isPresent()) {
      return mapper.map(assignmentEntity.get(), AssignmentDTO.class);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with slug '%s' not found.", slug));
    }
  }

  public void addQuestionToAssignment(AddQuestionRequest request) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(request.getAssignmentId());
    if (assignmentOptional.isPresent()) {
      Question newQuestion = mapper.map(request, Question.class);
      newQuestion.setId(UUID.randomUUID());
      newQuestion.setSlug(slugify(request.getTitle()));
      Assignment assignment = assignmentOptional.get();
      if (assignment.getQuestions() == null) {
        assignment.setQuestions(new ArrayList<>());
      }
      assignment.getQuestions().add(newQuestion);
      this.assignmentRepository.save(assignment);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with id '%s' not found.", request.getAssignmentId()));
    }
  }

  public QuestionDTO getQuestion(GetQuestionRequest request) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(request.getAssignmentId());

    Assignment assignment = assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + request.getAssignmentId() + "' not found")
    );
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getId().equals(UUID.fromString(request.getQuestionId())))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + request.getQuestionId() + "' not found")
    );
    return mapper.map(question, QuestionDTO.class);

  }

  public void toggleAssignmentStatus(String id) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(id);
    if (assignmentOptional.isPresent()) {
      Assignment assignment = assignmentOptional.get();
      String currentStatus = assignment.getStatus();
      assignment.setStatus(currentStatus.equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
      this.assignmentRepository.save(assignment);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with id '%s' not found.", id));
    }

  }
}
