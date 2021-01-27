package com.cdad.project.classroomservice.controllers;

import com.cdad.project.classroomservice.dto.ErrorResponse;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomAlreadyExists;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.exchanges.EnrollUsersRequest;
import com.cdad.project.classroomservice.exchanges.RemoveUsersRequest;
import com.cdad.project.classroomservice.service.ClassroomService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("{classroomSlug}/enroll")
public class EnrollUserController {
    final private ClassroomService classroomService;
    final private ModelMapper modelMapper;

    public EnrollUserController(ClassroomService classroomService, ModelMapper modelMapper) {
        this.classroomService = classroomService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    void enrollUsers(@PathVariable String classroomSlug,@RequestBody @Valid EnrollUsersRequest request, @AuthenticationPrincipal Jwt jwt) throws ClassroomAccessForbidden, ClassroomNotFound {
        classroomService.enrollUsersToClassroom(request, classroomSlug, jwt);
    }
    @DeleteMapping("")
    void removeUsers(@PathVariable String classroomSlug, @RequestBody @Valid RemoveUsersRequest request,@AuthenticationPrincipal Jwt jwt) throws ClassroomAccessForbidden, ClassroomNotFound {
        classroomService.unrollUsersFromClassroom(request,classroomSlug,jwt);
    }
    @ExceptionHandler(ClassroomNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleClassroomNotFound(ClassroomNotFound e){
        ErrorResponse errorResponse=modelMapper.map(e,ErrorResponse.class);
        errorResponse.setError("Not Found");
        return errorResponse;
    }

    @ExceptionHandler(ClassroomAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleClassroomAlreadyExists(ClassroomAlreadyExists e){
        ErrorResponse errorResponse=modelMapper.map(e,ErrorResponse.class);
        errorResponse.setError("Already Exists");
        return errorResponse;
    }
    @ExceptionHandler(ClassroomAccessForbidden.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse handleClassroomAccessForbidden(ClassroomAccessForbidden e){
        ErrorResponse errorResponse=modelMapper.map(e,ErrorResponse.class);
        errorResponse.setError("Classroom Access Forbidden");
        return errorResponse;
    }
}
