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
@RequestMapping("{classroomSlug}")
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
    PostRunCodeResponse postRunCode(@PathVariable String classroomSlug, @RequestBody PostRunCodeRequest request, @AuthenticationPrincipal Jwt jwt) throws RunCodeCompilationErrorException {
        return this.submissionService.runCode(request, classroomSlug, jwt);
    }

    @PostMapping("submit")
    PostSubmitResponse submitNew(@PathVariable String classroomSlug, @RequestBody PostSubmitRequest request, @AuthenticationPrincipal Jwt jwt) throws SubmissionCompilationErrorException, LanguageNotAllowedException, AssignmentNotActiveException, AssignmentNotStartedException, SubmissionEntityNotFoundException {
        return this.submissionService.submit(request, classroomSlug, jwt);
    }

    @PatchMapping("/start-submission/{assignmentId}")
    void startQuestion(@PathVariable String classroomSlug, @PathVariable String assignmentId, @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFound, AssignmentNotStartedException {
        this.submissionService.startSubmission(classroomSlug, assignmentId, jwt);
    }

    @GetMapping("{assignmentId}")
    public GetSubmissionDetails getSubmissions(@PathVariable String classroomSlug, @PathVariable String assignmentId, @AuthenticationPrincipal Jwt jwt) throws AccessForbiddenException {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Assignment assignment = this.assignmentServiceClient
                .getUserAssignment(assignmentId, classroomSlug, jwt.getTokenValue())
                .block();
//        checkAccess(currentUser,assignment);
        return this.submissionService.getSubmissionDetails(classroomSlug, assignmentId,jwt.getTokenValue());
    }

    @GetMapping("{assignmentId}/user")
    public SubmissionUserDetailsDTO getSubmissionUserDetails(@PathVariable String classroomSlug, @PathVariable String assignmentId, @NotNull @RequestParam String email, @AuthenticationPrincipal Jwt jwt) throws SubmissionEntityNotFoundException, AccessForbiddenException {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Assignment assignment = this.assignmentServiceClient
                .getUserAssignment(assignmentId, classroomSlug, jwt.getTokenValue())
                .block();
        //  checkAccess(currentUser,assignment);
        SubmissionEntity submissionEntity = this.submissionService.getSubmissionEntity(classroomSlug, assignmentId, email);
        SubmissionUserDetailsDTO submissionUserDetailsDTO = modelMapper.map(submissionEntity, SubmissionUserDetailsDTO.class);
        return submissionUserDetailsDTO;
    }

    @GetMapping("{assignmentId}/{questionId}")
    public QuestionDTO getSubmissionUserDetails(@PathVariable String classroomSlug, @PathVariable String assignmentId, @PathVariable String questionId, @NotNull @RequestParam String email, @AuthenticationPrincipal Jwt jwt) throws SubmissionEntityNotFoundException, QuestionEntityNotFoundException, AccessForbiddenException {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Assignment assignment = this.assignmentServiceClient.getUserAssignment(assignmentId, classroomSlug, jwt.getTokenValue())
                .block();
        // checkAccess(currentUser,assignment);
        SubmissionEntity submissionEntity = this.submissionService.getSubmissionEntity(classroomSlug, assignmentId, email);
        QuestionEntity questionEntity = submissionEntity
                .getQuestionEntities()
                .stream()
                .filter(questionEntity1 -> questionEntity1.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
        if (questionEntity == null) {
            throw new QuestionEntityNotFoundException("Not Started");
        }
        return modelMapper.map(questionEntity, QuestionDTO.class);
    }

    @ExceptionHandler(SubmissionCompilationErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handle(SubmissionCompilationErrorException error) {
        ErrorResponse errorResponse = modelMapper.map(error, ErrorResponse.class);
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
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handle(RunCodeCompilationErrorException error) {
        return modelMapper.map(error, ErrorResponse.class);
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(AccessForbiddenException error) {
        return modelMapper.map(error, ErrorResponse.class);
    }

    @ExceptionHandler(AssignmentNotStartedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public ErrorResponse handle(AssignmentNotStartedException e) {
        return modelMapper.map(e, ErrorResponse.class);
    }

    @ExceptionHandler({
            SubmissionEntityNotFoundException.class,
            AssignmentNotFound.class,
            QuestionEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle(Exception e) {
    }

    public void checkAccess(CurrentUser user, Assignment assignment) throws AccessForbiddenException {
        System.out.println(assignment.getEmail());
        System.out.println(user.getEmail());
        if (!assignment.getEmail().equals(user.getEmail())) {
            throw new AccessForbiddenException("Access Forbidden");
        }
    }
}
