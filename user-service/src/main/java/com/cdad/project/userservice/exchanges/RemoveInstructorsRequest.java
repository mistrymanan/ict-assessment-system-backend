package com.cdad.project.userservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class RemoveInstructorsRequest {
    @NotNull
    String classroomSlug;
    @NotNull
    List<String> users;
}
