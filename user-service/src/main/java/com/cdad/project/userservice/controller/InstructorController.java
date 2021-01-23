package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.exchanges.AddInstructorsRequest;
import com.cdad.project.userservice.exchanges.RemoveInstructorsRequest;
import com.cdad.project.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("instructors")
public class InstructorController {
    final private UserService userService;

    public InstructorController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    void addInstructorToClass(@RequestBody @Valid AddInstructorsRequest addInstructorsRequest){
        this.userService.addInstructorToClassroom(addInstructorsRequest);
    }
    @DeleteMapping("")
    void removeInstructorFromClass(@RequestBody @Valid RemoveInstructorsRequest removeInstructorsRequest){
        this.userService.removeInstructorsFromClassroom(removeInstructorsRequest);
    }
}
