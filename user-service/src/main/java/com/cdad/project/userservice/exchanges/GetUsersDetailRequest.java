package com.cdad.project.userservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class GetUsersDetailRequest {
    @NotNull
    private HashSet<String> usersEmail;
}