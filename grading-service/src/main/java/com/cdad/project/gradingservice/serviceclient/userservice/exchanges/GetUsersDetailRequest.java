package com.cdad.project.gradingservice.serviceclient.userservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class GetUsersDetailRequest {
    @NotNull
    private Set<String> usersEmail;
}
