package com.cdad.project.classroomservice.controllers;

import com.cdad.project.classroomservice.dto.ErrorResponse;
import com.cdad.project.classroomservice.entity.Classroom;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomAlreadyExists;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.exchanges.CreateClassroomRequest;
import com.cdad.project.classroomservice.exchanges.GetClassroomsResponse;
import com.cdad.project.classroomservice.service.ClassroomService;
import com.cdad.project.classroomservice.serviceclient.userservice.exceptions.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("")
public class ClassroomController {
    final private ClassroomService classroomService;
    final private ModelMapper modelMapper;

    public ClassroomController(ClassroomService classroomService, ModelMapper modelMapper) {
        this.classroomService = classroomService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("{classroomSlug}")
    @ResponseStatus(HttpStatus.OK)
    Classroom getClassroom(@PathVariable String classroomSlug,@AuthenticationPrincipal Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        return classroomService.getClassroom(classroomSlug, jwt);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    GetClassroomsResponse getClassrooms(@AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return classroomService.getUsersClassrooms(jwt);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Classroom createClassroom(@RequestBody @Valid CreateClassroomRequest createClassroomRequest, @AuthenticationPrincipal Jwt jwt) throws ClassroomAlreadyExists {
        return classroomService.addClassroom(createClassroomRequest, jwt);
    }

    @DeleteMapping("{classroomSlug}")
    @ResponseStatus(HttpStatus.OK)
    void deleteClassroom(@PathVariable String classroomSlug, @AuthenticationPrincipal Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        classroomService.removeClassroom(classroomSlug, jwt);
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