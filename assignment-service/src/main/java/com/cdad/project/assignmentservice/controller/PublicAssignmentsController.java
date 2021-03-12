package com.cdad.project.assignmentservice.controller;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import com.cdad.project.assignmentservice.dto.ErrorResponse;
import com.cdad.project.assignmentservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.entity.Assignment;
import com.cdad.project.assignmentservice.entity.CurrentUser;
import com.cdad.project.assignmentservice.exceptions.AccessForbiddenException;
import com.cdad.project.assignmentservice.exceptions.AssignmentNotFoundException;
import com.cdad.project.assignmentservice.exceptions.InvalidSecretKeyException;
import com.cdad.project.assignmentservice.exceptions.QuestionNotFoundException;
import com.cdad.project.assignmentservice.exchanges.GetQuestionUsingIdRequest;
import com.cdad.project.assignmentservice.service.AssignmentService;
import com.cdad.project.assignmentservice.serviceclient.userservice.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("{classroomSlug}/public/")
public class PublicAssignmentsController {

    private final AssignmentService assignmentService;

    public PublicAssignmentsController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("assignments/{id}")
    public AssignmentDTO getAssignment(@PathVariable String classroomSlug,
                                       @PathVariable String id, HttpServletRequest req) throws AssignmentNotFoundException, InvalidSecretKeyException, UserNotFoundException, AccessForbiddenException {
        checkSecret(req);
        return this.assignmentService.getAssignmentById(id);
    }

    @GetMapping("questions/id")
    public QuestionDTO getQuestionForAssignmentById(@PathVariable String classroomSlug,
                                                    GetQuestionUsingIdRequest request, HttpServletRequest req) throws AssignmentNotFoundException, QuestionNotFoundException, InvalidSecretKeyException {
        checkSecret(req);
        return this.assignmentService.getQuestionUsingId(
                request.getAssignmentId(),
                request.getQuestionId(), classroomSlug
        );
    }

    @ExceptionHandler({InvalidSecretKeyException.class, AccessForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void forbidden() {
    }

    @ExceptionHandler({AssignmentNotFoundException.class, QuestionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(Exception e) {
        return new ErrorResponse("Not Found", e.getMessage());
    }

    public void checkSecret(HttpServletRequest req) throws InvalidSecretKeyException {
        String key = req.getHeader("X-Secret");
        if (Objects.isNull(key) || !key.equals("top-secret-communication")) {
            throw new InvalidSecretKeyException("secret not valid");
        }
    }
}
