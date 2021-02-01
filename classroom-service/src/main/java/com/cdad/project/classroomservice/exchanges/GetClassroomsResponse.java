package com.cdad.project.classroomservice.exchanges;

import com.cdad.project.classroomservice.dto.ClassroomDetailsDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetClassroomsResponse {
    private List<ClassroomDetailsDTO> instructClassrooms;
    private List<ClassroomDetailsDTO> enrolledClassrooms;
}