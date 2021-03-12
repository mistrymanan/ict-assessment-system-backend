package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.exceptions.InvalidSecretKeyException;
import com.cdad.project.userservice.exchanges.EnrollUsersRequest;
import com.cdad.project.userservice.exchanges.UnrollUsersRequest;
import com.cdad.project.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("enroll")
public class EnrollmentController {

    final private UserService userService;

    public EnrollmentController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    void enrollUsers(@RequestBody @Valid EnrollUsersRequest enrollUsersRequest, HttpServletRequest request) throws InvalidSecretKeyException {
        checkSecret(request);
        this.userService.enrollUsers(enrollUsersRequest);
    }

    @DeleteMapping("")
    void unrollUsers(@RequestBody @Valid UnrollUsersRequest unrollUsersRequest, HttpServletRequest request) throws InvalidSecretKeyException {
        checkSecret(request);
        this.userService.unrollUsers(unrollUsersRequest);
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
