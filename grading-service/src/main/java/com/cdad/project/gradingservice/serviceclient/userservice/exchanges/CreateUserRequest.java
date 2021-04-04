package com.cdad.project.classroomservice.serviceclient.userservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateUserRequest {
    @NotNull
    private String emailId;
}
