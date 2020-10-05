package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.CurrentUser;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.AddQuestionRequest;
import com.cdad.project.assignmentservice.exchanges.CreateAssignmentRequest;
import com.cdad.project.assignmentservice.exchanges.UpdateAssignmentRequest;
import com.cdad.project.assignmentservice.repository.AssignmentRepository;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
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

  public AssignmentService(AssignmentRepository assignmentRepository, ModelMapper mapper) {
    this.assignmentRepository = assignmentRepository;
    this.mapper = mapper;
  }

  public List<AssignmentDTO> getAllAssignments(CurrentUser user) {
//    List<AssignmentEntity> assignmentEntities = this.assignmentRepository.findAll();
//    assignmentEntities.forEach(System.out::println);
    return this.assignmentRepository
            .findAllByEmail(user.getEmail())
            .stream()
            .map(assignment -> mapper.map(assignment, AssignmentDTO.class))
            .collect(Collectors.toList());
  }

  public AssignmentDTO createAssignment(CreateAssignmentRequest assignment, CurrentUser user) {
    Assignment newAssignment = mapper.map(assignment, Assignment.class);
    newAssignment.setStatus("ACTIVE");
    newAssignment.setEmail(user.getEmail());
    newAssignment.setSlug(slugify(assignment.getTitle()));
    newAssignment = this.assignmentRepository.save(newAssignment);
    return mapper.map(newAssignment, AssignmentDTO.class);
  }

  public AssignmentDTO updateAssignment(String id, UpdateAssignmentRequest request, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, user);
    this.mapper.map(request, assignment);
    assignment.setSlug(slugify(assignment.getTitle()));
    this.assignmentRepository.save(assignment);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  private String slugify(String str) {
    return String.join("-", str.trim().toLowerCase().split("\\s+"));
  }

  public void deleteAssignment(String id, CurrentUser user) {
    this.assignmentRepository.deleteByIdAndEmail(new ObjectId(id), user.getEmail());
  }

  public AssignmentDTO getAssignmentById(String id, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, user);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  public AssignmentDTO getAssignmentById(String id) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  public AssignmentDTO getAssignmentBySlug(String slug, CurrentUser user) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlugAndEmail(slug, user.getEmail());
    if (assignmentEntity.isPresent()) {
      return mapper.map(assignmentEntity.get(), AssignmentDTO.class);
    } else {
      throw new AssignmentNotFoundException(String.format("Assignment with slug '%s' not found.", slug));
    }
  }

  public void addQuestionToAssignment(AddQuestionRequest request, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(request.getAssignmentId(), user);
    Question newQuestion = mapper.map(request, Question.class);
    newQuestion.setId(UUID.randomUUID());
    newQuestion.setSlug(slugify(request.getTitle()));
    if (assignment.getQuestions() == null) {
      assignment.setQuestions(new ArrayList<>());
    }
    assignment.getQuestions().add(newQuestion);
    this.assignmentRepository.save(assignment);
  }

  public QuestionDTO getQuestion(String assignmentSlug, String questionSlug, CurrentUser user) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlugAndEmail(assignmentSlug, user.getEmail());
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

  public void toggleAssignmentStatus(String id, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, user);
    String currentStatus = assignment.getStatus();
    assignment.setStatus(currentStatus.equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
    this.assignmentRepository.save(assignment);
  }

  public void deleteQuestionForAssignment(String assignmentId, String questionId, CurrentUser user) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, user);
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

  public void updateQuestionForAssignment(String assignmentId, QuestionDTO questionDTO, CurrentUser user) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, user);
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

  private Assignment getAssignment(String assignmentId, CurrentUser user) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findByIdAndEmail(new ObjectId(assignmentId), user.getEmail());
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + assignmentId + "' not found")
    );
  }

  private Assignment getAssignment(String assignmentId) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(assignmentId);
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + assignmentId + "' not found")
    );
  }

  public QuestionDTO getQuestionUsingId(String assignmentId, String questionId, CurrentUser user) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, user);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getId().equals(UUID.fromString(questionId)))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
    );
    return mapper.map(question, QuestionDTO.class);
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
