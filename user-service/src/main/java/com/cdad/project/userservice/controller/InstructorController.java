package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.exceptions.InvalidSecretKeyException;
import com.cdad.project.userservice.exchanges.AddInstructorsRequest;
import com.cdad.project.userservice.exchanges.RemoveInstructorsRequest;
import com.cdad.project.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("instructors")
public class InstructorController {
    final private UserService userService;

    public InstructorController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    void addInstructorToClass(@RequestBody @Valid AddInstructorsRequest addInstructorsRequest,HttpServletRequest request) throws InvalidSecretKeyException {
        checkSecret(request);
        this.userService.addInstructorToClassroom(addInstructorsRequest);
    }
    @DeleteMapping("")
    void removeInstructorFromClass(@RequestBody @Valid RemoveInstructorsRequest removeInstructorsRequest,HttpServletRequest request) throws InvalidSecretKeyException {
        checkSecret(request);
        this.userService.removeInstructorsFromClassroom(removeInstructorsRequest);
    }

    public void checkSecret(HttpServletRequest req) throws InvalidSecretKeyException {
        String key = req.getHeader("X-Secret");
        if (Objects.isNull(key) || !key.equals("top-secret-communication")) {
            throw new InvalidSecretKeyException("secret not valid");
        }
    }
    @ExceptionHandler(InvalidSecretKeyException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void forbidden() {
    }

}
