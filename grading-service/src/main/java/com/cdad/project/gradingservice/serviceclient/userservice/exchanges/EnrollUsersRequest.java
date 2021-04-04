package com.cdad.project.gradingservice.serviceclient.userservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollUsersRequest {
    @NotNull
    String classroomSlug;
    @NotNull
    HashSet<String> users;
}
