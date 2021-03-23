package com.cdad.project.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminInstructorAndStudentCounts {
    private Integer numberOfInstructors;
    private Integer numberOfStudents;
}
