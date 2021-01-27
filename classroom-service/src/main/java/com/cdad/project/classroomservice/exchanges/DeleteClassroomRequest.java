package com.cdad.project.classroomservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteClassroomRequest {
    private String classroomSlug;
}
