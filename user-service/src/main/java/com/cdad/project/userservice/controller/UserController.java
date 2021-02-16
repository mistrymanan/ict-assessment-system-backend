package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.dto.ErrorResponse;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.UserNotFoundException;
import com.cdad.project.userservice.exchanges.CreateUserRequest;
import com.cdad.project.userservice.exchanges.GetUsersDetailRequest;
import com.cdad.project.userservice.exchanges.GetUsersDetailsResponse;
import com.cdad.project.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("")
public class UserController {
    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("")
    GetUsersDetailsResponse getUsersDetails(@RequestBody GetUsersDetailRequest request){
        return this.userService.getUsersData(request);
    }
    @GetMapping("{emailId:.+}")
    User getUser(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return userService.getByEmailId(emailId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@RequestBody CreateUserRequest user, @AuthenticationPrincipal Jwt jwt){
        return userService.save(user);
    }

    @PatchMapping("{emailId:.+}")
    @ResponseStatus(HttpStatus.OK)
    User updateUserInfo(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
    return userService.updateUserMetadata(emailId, jwt);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundHandler(UserNotFoundException e) {
        return new ErrorResponse("Not Found", e.getMessage());
    }
}