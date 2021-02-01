package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.dto.ActiveAssignmentDTO;
import com.cdad.project.assignmentservice.dto.ActiveAssignmentDetailsDTO;
import com.cdad.project.assignmentservice.dto.UserQuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.GetActiveQuestionRequest;
import com.cdad.project.assignmentservice.repository.AssignmentRepository;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.GradingServiceClient;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.dto.QuestionUserDetailsDTO;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.QuestionStatus;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.SubmissionStatus;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.exeptions.SubmissionDetailsNotFoundException;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.exeptions.SubmissionQuestionNotFoundException;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActiveAssignmentService {

  private final AssignmentRepository assignmentRepository;
  private final ModelMapper modelMapper;
  private final GradingServiceClient gradingServiceClient;


  public ActiveAssignmentService(AssignmentRepository assignmentRepository, ModelMapper modelMapper, GradingServiceClient gradingServiceClient) {
    this.assignmentRepository = assignmentRepository;
    this.modelMapper = modelMapper;
    this.gradingServiceClient = gradingServiceClient;
  }

  public List<ActiveAssignmentDTO> getAll(String classroomSlug,Jwt jwt) {
    List<Assignment> assignments = this.assignmentRepository.findAllByClassroomSlugEqualsAndStatusEquals(classroomSlug,"ACTIVE");
    return assignments.parallelStream()
            .map(assignment -> mapAssignmentToActiveAssignment(assignment, jwt))
            .collect(Collectors.toList());
  }

  public ActiveAssignmentDetailsDTO getDetails(String classroomSlug,String slug, Jwt jwt) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.
            findBySlugAndStatusAndClassroomSlug(slug, "ACTIVE",classroomSlug);
    if (assignmentOptional.isPresent()) {
      Assignment assignment = assignmentOptional.get();
      return mapAssignmentToActiveAssignmentDetails(assignment, jwt);
    } else {
      throw new AssignmentNotFoundException("Assignment with slug '" + slug + "' not found");
    }
  }

  public ActiveAssignmentDTO mapAssignmentToActiveAssignment(Assignment assignment, Jwt jwt) {
    ActiveAssignmentDTO activeAssignment = modelMapper.map(assignment, ActiveAssignmentDTO.class);
    try {
      this.gradingServiceClient
              .getSubmissionDetails(assignment.getId().toString(), jwt)
              .doOnSuccess(submissionDetailsDTO -> {
                activeAssignment.setCurrentStatus(submissionDetailsDTO.getSubmissionStatus());
              })
              .doOnError(SubmissionDetailsNotFoundException.class, e -> {
                activeAssignment.setCurrentStatus(SubmissionStatus.NOT_STARTED);
              })
              .block();
    } catch (SubmissionDetailsNotFoundException ignored) {
    }
    return activeAssignment;
  }

  public ActiveAssignmentDetailsDTO mapAssignmentToActiveAssignmentDetails(Assignment assignment, Jwt jwt) {
    ActiveAssignmentDetailsDTO activeAssignment = modelMapper.map(assignment, ActiveAssignmentDetailsDTO.class);
    try {
      this.gradingServiceClient
              .getSubmissionDetails(assignment.getId().toString(), jwt)
              .doOnSuccess(submissionDetailsDTO -> {
                System.out.println(submissionDetailsDTO.getCurrentScore());
                activeAssignment.setCurrentScore(Optional.ofNullable(submissionDetailsDTO.getCurrentScore()).orElse(0d));
                activeAssignment.setSubmissionStatus(submissionDetailsDTO.getSubmissionStatus());
                fillStatusAndScore(activeAssignment, submissionDetailsDTO);
              })
              .doOnError(SubmissionDetailsNotFoundException.class, e -> {
                activeAssignment.setCurrentScore(0d);
                activeAssignment.setSubmissionStatus(SubmissionStatus.NOT_STARTED);
                activeAssignment.getQuestions().forEach(question -> {
                  question.setCurrentScore(0d);
                  question.setCurrentStatus(QuestionStatus.NOT_STARTED);
                });
              })
              .block();
    } catch (SubmissionDetailsNotFoundException ignored) {
    }

    return activeAssignment;
  }

  public ActiveAssignmentDetailsDTO getDetailsById(String id,String classroomSlug, Jwt jwt) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository
            .findByIdAndStatusAndClassroomSlug(new ObjectId(id), "ACTIVE",classroomSlug);
    if (assignmentOptional.isPresent()) {
      Assignment assignment = assignmentOptional.get();
      return mapAssignmentToActiveAssignmentDetails(assignment, jwt);
    } else {
      throw new AssignmentNotFoundException("Assignment with id '" + id + "' not found");
    }
  }

  public UserQuestionDTO getActiveQuestion(GetActiveQuestionRequest request,String classroomSlug, Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findBySlugAndStatusAndClassroomSlug(request.getAssignmentSlug(), "ACTIVE",classroomSlug);

    Assignment assignment = assignmentOptional.orElseThrow(
            () -> new AssignmentNotFoundException("Assignment with slug '" + request.getAssignmentSlug() + "' not found")
    );
    Optional<Question> questionOptional = assignment.getQuestions()
            .stream()
            .filter(question -> question.getSlug().equals(request.getQuestionSlug()))
            .findFirst();
    Question question = questionOptional.orElseThrow(
            () -> new QuestionNotFoundException("Question with id '" + request.getQuestionSlug() + "' not found")
    );
    return modelMapper.map(question, UserQuestionDTO.class);
  }

  public void fillStatusAndScore(ActiveAssignmentDetailsDTO activeAssignmentDetails, SubmissionDetailsDTO submissionDetails) {
    if (submissionDetails.getQuestionEntities() == null) {
      submissionDetails.setQuestionEntities(new ArrayList<>());
    }
    Map<String, QuestionUserDetailsDTO> questionDetailMap = submissionDetails.getQuestionEntities()
            .stream()
            .collect(Collectors.toMap(QuestionUserDetailsDTO::getQuestionId, question -> question));
    activeAssignmentDetails.getQuestions()
            .forEach(userQuestionDTO -> {
              String id = userQuestionDTO.getId().toString();
              QuestionUserDetailsDTO questionUserDetailsDTO = questionDetailMap.getOrDefault(id, null);
              if (Objects.isNull(questionUserDetailsDTO)) {
                userQuestionDTO.setCurrentStatus(QuestionStatus.NOT_STARTED);
                userQuestionDTO.setCurrentScore(0d);
              } else {
                userQuestionDTO.setCurrentStatus(questionUserDetailsDTO.getQuestionStatus());
                userQuestionDTO.setCurrentScore(questionUserDetailsDTO.getScore());
              }
            });
//    activeAssignmentDetails.getQuestions()
//            .forEach(userQuestionDTO -> {
//              try {
//                gradingServiceClient
//                        .getQuestionOfSubmission(
//                                activeAssignmentDetails.getId(),
//                                userQuestionDTO.getId().toString(),
//                                jwt
//                        )
//                        .doOnSuccess(question -> {
//                          userQuestionDTO.setCurrentScore(question.getScore());
//                          userQuestionDTO.setCurrentStatus(question.getQuestionStatus());
//                        })
//                        .doOnError(SubmissionQuestionNotFoundException.class, e -> {
//                          userQuestionDTO.setCurrentScore(0d);
//                          userQuestionDTO.setCurrentStatus(QuestionStatus.NOT_STARTED);
//                        })
//                        .block();
//              } catch (SubmissionQuestionNotFoundException ignored) {
//
//              }
//            });
  }
}
