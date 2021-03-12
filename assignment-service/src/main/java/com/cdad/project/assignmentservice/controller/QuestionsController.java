package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.CurrentUser;
import com.cdad.project.assignmentservice.exceptions.AccessForbiddenException;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.*;
import com.cdad.project.assignmentservice.service.AssignmentService;
import com.cdad.project.assignmentservice.serviceclient.userservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("{classroomSlug}/questions")
public class QuestionsController {

    private final AssignmentService assignmentService;

    public QuestionsController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/add-question")
    @ResponseStatus(HttpStatus.CREATED)
    public void addQuestionToAssignment(@PathVariable String classroomSlug,
                                        @RequestBody AddQuestionRequest request,
                                        @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, UserNotFoundException, AccessForbiddenException {
        CurrentUser user = CurrentUser.fromJwt(jwt);
        this.assignmentService.addQuestionToAssignment(request, classroomSlug, jwt);
    }

    @GetMapping
    public QuestionDTO getQuestionForAssignment(@PathVariable String classroomSlug,
                                                GetQuestionUsingSlugRequest request,
                                                @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException {
        return this.assignmentService.getQuestion(
                request.getAssignmentSlug(),
                request.getQuestionSlug(),
                classroomSlug
        );
    }

    @GetMapping("/id")
    public QuestionDTO getQuestionForAssignmentById(@PathVariable String classroomSlug,
                                                    GetQuestionUsingIdRequest request,
                                                    @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException {
        return this.assignmentService.getQuestionUsingId(
                request.getAssignmentId(),
                request.getQuestionId(),
                classroomSlug
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteQuestionForAssignment(@PathVariable String classroomSlug,
                                            DeleteQuestionRequest request,
                                            @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException, UserNotFoundException, AccessForbiddenException {
        this.assignmentService.deleteQuestionForAssignment(
                request.getAssignmentId(),
                request.getQuestionId(),
                classroomSlug,
                jwt
        );
    }

    @PutMapping("/update-question")
    @ResponseStatus(HttpStatus.OK)
    public void updateQuestionForAssignment(@PathVariable String classroomSlug,
                                            @RequestBody UpdateAssignmentQuestionRequest updateRequest,
                                            @AuthenticationPrincipal Jwt jwt) throws AssignmentNotFoundException, QuestionNotFoundException, UserNotFoundException, AccessForbiddenException {
        this.assignmentService.updateQuestionForAssignment(
                updateRequest.getAssignmentId(),
                classroomSlug,
                updateRequest.getQuestion(),
                jwt
        );
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(AccessForbiddenException e) {
        return new ErrorResponse("Forbidden", e.getMessage());
    }

    @ExceptionHandler({AssignmentNotFoundException.class, QuestionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(Exception e) {
        return new ErrorResponse("Not Found", e.getMessage());
    }


}
