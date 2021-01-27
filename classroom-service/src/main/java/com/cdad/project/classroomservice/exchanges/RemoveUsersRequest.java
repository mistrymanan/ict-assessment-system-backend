package com.cdad.project.classroomservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@NoArgsConstructor
public class RemoveUsersRequest {
    private HashSet<String> users;
}
