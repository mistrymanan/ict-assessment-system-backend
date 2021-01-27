package com.cdad.project.classroomservice.controllers;

import com.cdad.project.classroomservice.dto.ErrorResponse;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomAlreadyExists;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.exchanges.AddInstructorsRequest;
import com.cdad.project.classroomservice.exchanges.RemoveInstructorsRequest;
import com.cdad.project.classroomservice.service.ClassroomService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{classroomSlug}/instructors")
public class InstructorController {

    final private ClassroomService classroomService;
    final private ModelMapper modelMapper;
    public InstructorController(ClassroomService classroomService, ModelMapper modelMapper) {
        this.classroomService = classroomService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    void addInstructorsToClassroom(@PathVariable String classroomSlug, @RequestBody AddInstructorsRequest request, @AuthenticationPrincipal Jwt jwt) throws ClassroomAccessForbidden, ClassroomNotFound {
        classroomService.addInstructorsToClassroom(request, classroomSlug, jwt);
    }

    @DeleteMapping("")
    void removeInstructorsFromClassroom(@PathVariable String classroomSlug, @RequestBody RemoveInstructorsRequest request,@AuthenticationPrincipal Jwt jwt) throws ClassroomAccessForbidden, ClassroomNotFound {
        classroomService.removeInstructorsFromClassroom(request, classroomSlug, jwt);
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
