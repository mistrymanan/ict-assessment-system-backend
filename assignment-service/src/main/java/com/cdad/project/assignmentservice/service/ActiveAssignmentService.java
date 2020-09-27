package com.cdad.project.assignmentservice.service;

import com.cdad.project.assignmentservice.dto.ActiveAssignmentDTO;
import com.cdad.project.assignmentservice.dto.ActiveAssignmentDetailsDTO;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.dto.UserQuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.Question;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.GetActiveQuestionRequest;
import com.cdad.project.assignmentservice.repository.AssignmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActiveAssignmentService {

  private final AssignmentRepository assignmentRepository;
  private final ModelMapper modelMapper;

  public ActiveAssignmentService(AssignmentRepository assignmentRepository, ModelMapper modelMapper) {
    this.assignmentRepository = assignmentRepository;
    this.modelMapper = modelMapper;
  }

  public List<ActiveAssignmentDTO> getAll() {
    List<Assignment> assignments = this.assignmentRepository.findAllByStatusEquals("ACTIVE");
    return assignments.stream()
            .map(assignment -> modelMapper.map(assignment, ActiveAssignmentDTO.class))
            .collect(Collectors.toList());
  }

  public ActiveAssignmentDetailsDTO getDetails(String slug) throws AssignmentNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findBySlug(slug);
    if(assignmentOptional.isPresent()) {
      Assignment assignment = assignmentOptional.get();
      return modelMapper.map(assignment, ActiveAssignmentDetailsDTO.class);
    } else {
      throw new AssignmentNotFoundException("Assignment with slug '"+slug+"' not found");
    }
  }

  public UserQuestionDTO getActiveQuestion(GetActiveQuestionRequest request) throws AssignmentNotFoundException, QuestionNotFoundException {
    Optional<Assignment> assignmentOptional = this.assignmentRepository.findBySlug(request.getAssignmentSlug());

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
}
