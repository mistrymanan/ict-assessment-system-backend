package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.exchanges.EnrollUsersRequest;
import com.cdad.project.userservice.exchanges.UnrollUsersRequest;
import com.cdad.project.userservice.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("enroll")
public class EnrollmentController {

    final private UserService userService;

    public EnrollmentController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    void enrollUsers(@RequestBody @Valid EnrollUsersRequest enrollUsersRequest){
    this.userService.enrollUsers(enrollUsersRequest);
    }
    @DeleteMapping("")
    void unrollUsers(@RequestBody @Valid UnrollUsersRequest unrollUsersRequest){
        this.userService.unrollUsers(unrollUsersRequest);
    }
}
