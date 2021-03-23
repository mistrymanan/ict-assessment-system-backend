package com.cdad.project.userservice.controller;

import com.cdad.project.userservice.dto.ErrorResponse;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.InvalidSecretKeyException;
import com.cdad.project.userservice.exceptions.NotAuthorized;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@RestController
@RequestMapping("")
public class UserController {
    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("")
    GetUsersDetailsResponse getUsersDetails(@RequestBody GetUsersDetailRequest request, HttpServletRequest req) throws InvalidSecretKeyException {
        checkSecret(req);
        return this.userService.getUsersData(request);
    }

    @GetMapping("{emailId:.+}")
    User getUser(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return userService.getByEmailId(emailId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@RequestBody CreateUserRequest user, @AuthenticationPrincipal Jwt jwt) {
        return userService.save(user);
    }

    @PatchMapping("{emailId:.+}")
    @ResponseStatus(HttpStatus.OK)
    User updateUserInfo(@PathVariable String emailId, @AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return userService.updateUserMetadata(emailId, jwt);
    }

    @PatchMapping("{emailId}/adminAccess")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void updateUserAdminStatus(@PathVariable String emailId,@AuthenticationPrincipal Jwt jwt) throws UserNotFoundException, NotAuthorized {
        userService.toggleAdminRights(emailId,jwt);
    }
    @PatchMapping("{emailId}/createClassroomAccess")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void updateUserClassroomCreateAccess(@PathVariable String emailId,@AuthenticationPrincipal Jwt jwt) throws UserNotFoundException, NotAuthorized {
        userService.toggleClassroomCreationPermission(emailId,jwt);
    }


    public void checkSecret(HttpServletRequest req) throws InvalidSecretKeyException {
        String key = req.getHeader("X-Secret");
        if (Objects.isNull(key) || !key.equals("top-secret-communication")) {
            throw new InvalidSecretKeyException("secret not valid");
        }
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundHandler(UserNotFoundException e) {
        return new ErrorResponse("Not Found", e.getMessage());
    }

    @ExceptionHandler(InvalidSecretKeyException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void forbidden() {
    }
}