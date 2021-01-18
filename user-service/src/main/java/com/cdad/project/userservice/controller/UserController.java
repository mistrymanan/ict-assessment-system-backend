package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.dto.ErrorResponse;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.UserNotFoundException;
import com.cdad.project.userservice.exchanges.CreateUserRequest;
import com.cdad.project.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("")
public class UserController {
    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    User createUser(@Valid CreateUserRequest user, @AuthenticationPrincipal Jwt jwt){
        return userService.save(user);
    }
    @GetMapping("{emailId}")
    User getUser(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return userService.getByEmailId(emailId);
    }
    @PatchMapping("{emailId}")
    User updateUserInfo(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
    return userService.updateUserMetadata(emailId, jwt);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(Exception e) {
        return new ErrorResponse("Not Found", e.getMessage());
    }
}
