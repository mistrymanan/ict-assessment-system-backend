package com.cdad.project.classroomservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class EnrollUsersRequest {
    @NotNull
    HashSet<String> users;
}
