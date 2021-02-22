package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.CurrentUser;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.exceptions.AssignmentAlreadyExistsException;
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

  public List<AssignmentDTO> getAllAssignmentsInClassroom(String classroomSlug) {
//    List<AssignmentEntity> assignmentEntities = this.assignmentRepository.findAll();
//    assignmentEntities.forEach(System.out::println);
    return this.assignmentRepository
            .findAllByClassroomSlugEquals(classroomSlug)
            .stream()
            .map(assignment -> mapper.map(assignment, AssignmentDTO.class))
            .collect(Collectors.toList());
  }

  public AssignmentDTO createAssignment(CreateAssignmentRequest assignment, String classroomSlug, CurrentUser user) throws AssignmentAlreadyExistsException {
    String assignmentSlug = slugify(assignment.getTitle());
    if (!assignmentRepository.existsBySlugAndClassroomSlug(assignmentSlug, classroomSlug)) {
      Assignment newAssignment = mapper.map(assignment, Assignment.class);
      newAssignment.setClassroomSlug(classroomSlug);
      newAssignment.setStatus("ACTIVE");
      //newAssignment.setEmail(user.getEmail());
      newAssignment.setSlug(assignmentSlug);
      newAssignment.setTotalPoints(0);
      newAssignment = this.assignmentRepository.save(newAssignment);
      return mapper.map(newAssignment, AssignmentDTO.class);
    } else {
      throw new AssignmentAlreadyExistsException("Assignment With given Name already Exists in classroom.");
    }
  }

  public AssignmentDTO updateAssignment(String id, String classroomSlug, UpdateAssignmentRequest request, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, classroomSlug);
    this.mapper.map(request, assignment);
    assignment.setSlug(slugify(assignment.getTitle()));
    this.assignmentRepository.save(assignment);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  public AssignmentDTO updateAssignmentBySlug(String slug, String classroomSlug, UpdateAssignmentRequest request, CurrentUser user) throws AssignmentNotFoundException {
    Assignment assignment = getAssignmentBySlug(slug, classroomSlug);
    this.mapper.map(request, assignment);
    assignment.setSlug(slugify(assignment.getTitle()));
    this.assignmentRepository.save(assignment);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  private String slugify(String str) {
    return String.join("-", str.trim().toLowerCase().split("\\s+"));
  }

  public void deleteAssignment(String id, String classroomSlug) {
    this.assignmentRepository.deleteByIdAndClassroomSlug(new ObjectId(id), classroomSlug);
  }

  public void deleteAssignmentBySlug(String slug, String classroomSlug) {
    this.assignmentRepository.deleteAssignmentBySlugAndClassroomSlug(slug, classroomSlug);
  }

  public AssignmentDTO getAssignmentById(String id, String classroomSlug) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, classroomSlug);
    return mapper.map(assignment, AssignmentDTO.class);
  }

  public AssignmentDTO getAssignmentById(String id) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id);
    return mapper.map(assignment, AssignmentDTO.class);
  }

//  public AssignmentDTO getAssignmentBySlug(String slug, String classroomSlug)
//          throws AssignmentNotFoundException {
//    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlugAndClassroomSlug(slug, classroomSlug);
//    if (assignmentEntity.isPresent()) {
//      return mapper.map(assignmentEntity.get(), AssignmentDTO.class);
//    } else {
//      throw new AssignmentNotFoundException(String.format("Assignment with slug '%s' not found.", slug));
//    }
//  }

  public void addQuestionToAssignment(AddQuestionRequest request, String classroomSlug) throws AssignmentNotFoundException {
    //Assignment assignment = getAssignment(request.getAssignmentId(), classroomSlug);
    Assignment assignment = getAssignmentBySlug(request.getAssignmentSlug(), classroomSlug);
    Question newQuestion = mapper.map(request, Question.class);
    newQuestion.setId(UUID.randomUUID());
    newQuestion.setSlug(slugify(request.getTitle()));
    if (assignment.getQuestions() == null) {
      assignment.setQuestions(new ArrayList<>());
    }
    assignment.setTotalPoints(assignment.getTotalPoints() + newQuestion.getTotalPoints());
    assignment.getQuestions().add(newQuestion);
    this.assignmentRepository.save(assignment);
  }

  public QuestionDTO getQuestion(String assignmentSlug, String questionSlug, String classroomSlug) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentEntity = this.assignmentRepository.findBySlugAndClassroomSlug(assignmentSlug, classroomSlug);
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

  public void toggleAssignmentStatus(String id, String classroomSlug) throws AssignmentNotFoundException {
    Assignment assignment = getAssignment(id, classroomSlug);
    String currentStatus = assignment.getStatus();
    assignment.setStatus(currentStatus.equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
    this.assignmentRepository.save(assignment);
  }

  public void toggleAssignmentStatusBySlug(String slug, String classroomSlug) throws AssignmentNotFoundException {
    Assignment assignment = getAssignmentBySlug(slug, classroomSlug);
    String currentStatus = assignment.getStatus();
    assignment.setStatus(currentStatus.equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
    this.assignmentRepository.save(assignment);
  }

  public void deleteQuestionForAssignment(String assignmentId, String questionId, String classroomSlug) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, classroomSlug);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getId().equals(UUID.fromString(questionId)))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
    );
    assignment.getQuestions().remove(question);
    assignment.setTotalPoints(assignment.getTotalPoints() - question.getTotalPoints());
    this.assignmentRepository.save(assignment);
  }

  public void updateQuestionForAssignment(String assignmentId,String classroomSlug, QuestionDTO questionDTO) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, classroomSlug);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(q -> q.getId().equals(questionDTO.getId()))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionDTO.getId() + "' not found")
    );
    int oldTotalPoints = question.getTotalPoints();
    log.info(questionDTO.getAllowedLanguages());
    log.info("Before :: " + question.getAllowedLanguages());
    mapper.map(questionDTO, question);
    question.setAllowedLanguages(questionDTO.getAllowedLanguages());
    question.setTestCases(questionDTO.getTestCases());
    log.info("After :: " + question.getAllowedLanguages());
    question.setSlug(slugify(question.getTitle()));
    assignment.setTotalPoints(assignment.getTotalPoints() - oldTotalPoints + question.getTotalPoints());
    System.out.println(assignment);
    this.assignmentRepository.save(assignment);
  }

  private Assignment getAssignment(String assignmentId, String classroomSlug) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findByIdAndClassroomSlug(new ObjectId(assignmentId), classroomSlug);
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + assignmentId + "' not found")
    );
  }

  private Assignment getAssignmentBySlug(String slug, String classroomSlug) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findBySlugAndClassroomSlug(slug, classroomSlug);
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with Slug '" + slug + "' not found")
    );
  }

  public AssignmentDTO getAssignmentDTOBySlug(String slug, String classroomSlug) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findBySlugAndClassroomSlug(slug, classroomSlug);
    Assignment assignment = assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with Slug '" + slug + "' not found")
    );
    return mapper.map(assignment, AssignmentDTO.class);
  }

  private Assignment getAssignment(String assignmentId) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findById(assignmentId);
    return assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with id '" + assignmentId + "' not found")
    );
  }

  public QuestionDTO getQuestionUsingId(String assignmentId, String questionId, String classroomSlug) throws AssignmentNotFoundException, QuestionNotFoundException {
    Assignment assignment = getAssignment(assignmentId, classroomSlug);
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getId().equals(UUID.fromString(questionId)))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
    );
    return mapper.map(question, QuestionDTO.class);
  }

//  public QuestionDTO getQuestionUsingId(String assignmentId,String classroomSlug, String questionId) throws AssignmentNotFoundException, QuestionNotFoundException {
//    Assignment assignment = getAssignment(assignmentId,classroomSlug);
//    Optional<Question> questionOptional = assignment.getQuestions()
//            .stream()
//            .filter(question -> question.getId().equals(UUID.fromString(questionId)))
//            .findFirst();
//    Question question = questionOptional.orElseThrow(
//            () -> new QuestionNotFoundException("Question with id '" + questionId + "' not found")
//    );
//    return mapper.map(question, QuestionDTO.class);
//  }
}
