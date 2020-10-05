package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.QuestionDTO;
import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.dto.SubmissionUserDetailsDTO;
import com.cdad.project.gradingservice.entity.*;
import com.cdad.project.gradingservice.exception.*;
import com.cdad.project.gradingservice.exchange.*;
import com.cdad.project.gradingservice.service.SubmissionService;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("")
public class SubmissionController {

    private final ModelMapper modelMapper;
    private final AssignmentServiceClient assignmentServiceClient;
    private final ExecutionServiceClient executionServiceClient;
    private final SubmissionService submissionService;

    public SubmissionController(ModelMapper modelMapper, AssignmentServiceClient assignmentServiceClient, ExecutionServiceClient executionServiceClient, SubmissionService submissionService) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        this.assignmentServiceClient = assignmentServiceClient;
        this.executionServiceClient = executionServiceClient;
        this.submissionService = submissionService;
    }
    @PostMapping("run-code")
    PostRunCodeResponse postRunCode(@RequestBody PostRunCodeRequest request , @AuthenticationPrincipal Jwt jwt) throws RunCodeCompilationErrorException {
       return this.submissionService.runCode(request, jwt);
    }

    @PostMapping("/submit")
    PostSubmitResponse submitNew(@RequestBody PostSubmitRequest request, @AuthenticationPrincipal Jwt jwt) throws SubmissionCompilationErrorException, LanguageNotAllowedException, AssignmentNotActiveException, AssignmentNotStartedException {
        return this.submissionService.submit(request, jwt);
    }
    @PatchMapping("/submit")
    void startQuestion(@Valid @RequestBody StartSubmissionRequest request, @AuthenticationPrincipal Jwt jwt){
       this.submissionService.startSubmission(request,jwt);
    }


    @GetMapping("/{assignmentId}")
    public List<SubmissionDetailsDTO> getSubmissions(@PathVariable String assignmentId, @AuthenticationPrincipal Jwt jwt) throws AccessForbiddenException {
        CurrentUser currentUser=CurrentUser.fromJwt(jwt);
        Assignment assignment=this.assignmentServiceClient.getUserAssignment(assignmentId, jwt.getTokenValue())
                .block();
        return this.submissionService.getSubmissionDetails(assignmentId);
    }
    @GetMapping("/{assignmentId}/user")
    public SubmissionUserDetailsDTO getSubmissionUserDetails(@PathVariable String assignmentId,@NotNull @RequestParam String email){
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(assignmentId, email);
        SubmissionUserDetailsDTO submissionUserDetailsDTO=modelMapper.map(submissionEntity,SubmissionUserDetailsDTO.class);
        return submissionUserDetailsDTO;
    }
    @GetMapping("/{assignmentId}/{questionId}")
    public QuestionDTO getSubmissionUserDetails(@PathVariable String assignmentId,@PathVariable String questionId,@NotNull @RequestParam String email){
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(assignmentId, email);
        QuestionEntity questionEntity=submissionEntity
                .getQuestionEntities()
                .stream()
                .filter(questionEntity1 -> questionEntity1.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
        return modelMapper.map(questionEntity,QuestionDTO.class);
    }

    @ExceptionHandler(SubmissionCompilationErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handle(SubmissionCompilationErrorException error){
        ErrorResponse errorResponse=modelMapper.map(error,ErrorResponse.class);
        errorResponse.setStatus(Status.COMPILE_ERROR);
        return errorResponse;
    }

//
//    @ExceptionHandler(LanguageNotAllowedException.class)
//    @ResponseStatus(HttpStatus.OK)
//    public ErrorResponse handle(LanguageNotAllowedException error){
//        ErrorResponse errorResponse=modelMapper.map(error,ErrorResponse.class);
//        return errorResponse;
//    }



    @ExceptionHandler(RunCodeCompilationErrorException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handle(RunCodeCompilationErrorException error){
        return modelMapper.map(error,ErrorResponse.class);
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(AccessForbiddenException error){
        return modelMapper.map(error,ErrorResponse.class);
    }
}
