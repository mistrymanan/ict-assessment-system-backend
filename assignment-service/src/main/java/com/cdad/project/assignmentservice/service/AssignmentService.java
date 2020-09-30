package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.controller.QuestionsController;
import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.entity.TestCase;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.*;
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
    Assignment assignment = getAssignment(id);
    this.mapper.map(request, assignment);
    assignment.setSlug(slugify(assignment.getTitle()));
    this.assignmentRepository.save(assignment);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  private String slugify(String str) {
    return String.join("-", str.trim().toLowerCase().split("\\s+"));
  }

  public void deleteAssignment(String id) {
    this.assignmentRepository.deleteById(id);
  }

  public AssignmentDTO getAssignmentById(String id) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id);
    return mapper.map(assignment, AssignmentDTO.class);
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
      Assignment assignment =  getAssignment(request.getAssignmentId());
      Question newQuestion = mapper.map(request, Question.class);
      newQuestion.setId(UUID.randomUUID());
      newQuestion.setSlug(slugify(request.getTitle()));
      if (assignment.getQuestions() == null) {
        assignment.setQuestions(new ArrayList<>());
      }
      assignment.getQuestions().add(newQuestion);
      this.assignmentRepository.save(assignment);
  }

  public QuestionDTO getQuestion(String assignmentSlug, String questionSlug) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlug(assignmentSlug);
    if (assignmentEntity.isPresent()) {
      Assignment assignment = assignmentEntity.get();
      Optional<Question> questionOptional = assignment.getQuestions()
              .stream()
              .filter(question -> question.getSlug().equals(questionSlug))
              .findFirst();
      Question question = questionOptional.orElseThrow(
              () -> new QuestionNotFoundException("Question with slug '" + questionSlug + "' not found")
      );
      return mapper.map(question, QuestionDTO.class);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with slug '%s' not found.", assignmentSlug));
    }
  }

  public void toggleAssignmentStatus(String id) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id);
    String currentStatus = assignment.getStatus();
    assignment.setStatus(currentStatus.equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
    this.assignmentRepository.save(assignment);
  }

  public void deleteQuestionForAssignment(String assignmentId, String questionId) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getId().equals(UUID.fromString(questionId)))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
    );
    assignment.getQuestions().remove(question);
    this.assignmentRepository.save(assignment);
  }
  public void updateQuestionForAssignment(String assignmentId, QuestionDTO questionDTO) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(q -> q.getId().equals(questionDTO.getId()))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionDTO.getId() + "' not found")
    );
    log.info(questionDTO.getAllowedLanguages());
    log.info("Before :: " + question.getAllowedLanguages());
    mapper.map(questionDTO, question);
    question.setAllowedLanguages(questionDTO.getAllowedLanguages());
    question.setTestCases(questionDTO.getTestCases());
    log.info("After :: " + question.getAllowedLanguages());
    question.setSlug(slugify(question.getTitle()));
    this.assignmentRepository.save(assignment);



  }

  private Assignment getAssignment(String assignmentId) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(assignmentId);
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + assignmentId + "' not found")
    );
  }

  public QuestionDTO getQuestionUsingId(String assignmentId, String questionId) throws AssignmentNotFoundException, QuestionNotFoundException {
      Assignment assignment = getAssignment(assignmentId);
      Optional<Question> questionOptional = assignment.getQuestions()
              .stream()
              .filter(question -> question.getId().equals(UUID.fromString(questionId)))
              .findFirst();
      Question question = questionOptional.orElseThrow(
              () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
      );
      return mapper.map(question, QuestionDTO.class);
  }
}
