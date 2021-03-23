package com.cdad.project.classroomservice.controllers;


import com.cdad.project.classroomservice.dto.AdminClassroomDetailsDTO;
import com.cdad.project.classroomservice.dto.ClassroomAndUserDetailsDTO;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.service.AdminClassroomService;
import com.cdad.project.classroomservice.service.ClassroomService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminClassroomController {
    final private ClassroomService classroomService;
    final private AdminClassroomService adminClassroomService;

    public AdminClassroomController(ClassroomService classroomService, AdminClassroomService adminClassroomService) {
        this.classroomService = classroomService;
        this.adminClassroomService = adminClassroomService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    List<AdminClassroomDetailsDTO> getAllClassrooms(@AuthenticationPrincipal Jwt jwt){
        return this.adminClassroomService.getAllClassrooms();
    }

    @GetMapping("{classroomSlug}")
    @ResponseStatus(HttpStatus.OK)
    ClassroomAndUserDetailsDTO getClassroom(@PathVariable String classroomSlug, @AuthenticationPrincipal Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        return this.adminClassroomService.getClassroom(classroomSlug, jwt);
    }
    @DeleteMapping("{classroomSlug}")
    @ResponseStatus(HttpStatus.OK)
    void deleteClassroom(@PathVariable String classroomSlug, @AuthenticationPrincipal Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        this.adminClassroomService.removeClassroom(classroomSlug, jwt);
    }

}
