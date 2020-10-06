package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.ErrorResponse;
import com.cdad.project.gradingservice.dto.QuestionDTO;
import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.dto.SubmissionUserDetailsDTO;
import com.cdad.project.gradingservice.entity.CurrentUser;
import com.cdad.project.gradingservice.entity.QuestionEntity;
import com.cdad.project.gradingservice.entity.SubmissionEntity;
import com.cdad.project.gradingservice.exception.AccessForbiddenException;
import com.cdad.project.gradingservice.exception.InvalidSecretKeyException;
import com.cdad.project.gradingservice.exception.QuestionEntityNotFoundException;
import com.cdad.project.gradingservice.exception.SubmissionEntityNotFoundException;
import com.cdad.project.gradingservice.service.SubmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("public/submissions")
public class publicController {

    public final SubmissionService submissionService;
    private final ModelMapper modelMapper;
    public publicController(SubmissionService submissionService, ModelMapper modelMapper) {
        this.submissionService = submissionService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("{assignmentId}")
    public SubmissionUserDetailsDTO getSubmissions(HttpServletRequest request,@PathVariable String assignmentId, @AuthenticationPrincipal Jwt jwt) throws AccessForbiddenException, InvalidSecretKeyException, SubmissionEntityNotFoundException {
        checkSecret(request);
        CurrentUser currentUser=CurrentUser.fromJwt(jwt);
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(assignmentId, currentUser.getEmail());
        return modelMapper.map(submissionEntity,SubmissionUserDetailsDTO.class);
    }

    @GetMapping("{assignmentId}/{questionId}")
    public QuestionDTO getSubmissionUserDetails(HttpServletRequest request,@PathVariable String assignmentId,@PathVariable String questionId,@AuthenticationPrincipal Jwt jwt) throws InvalidSecretKeyException, SubmissionEntityNotFoundException, QuestionEntityNotFoundException {
        checkSecret(request);
        CurrentUser currentUser=CurrentUser.fromJwt(jwt);
        QuestionEntity questionEntity=this.submissionService.getQuestion(currentUser.getEmail(),assignmentId,questionId);
        return modelMapper.map(questionEntity,QuestionDTO.class);
    }

    public void checkSecret(HttpServletRequest req) throws InvalidSecretKeyException {
        String key = req.getHeader("X-Secret");
        if (Objects.isNull(key) || !key.equals("top-secret-communication")) {
            throw new InvalidSecretKeyException("secret not valid");
        }
    }
    @ExceptionHandler({SubmissionEntityNotFoundException.class,QuestionEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle(Exception error)
    {
    }
}
