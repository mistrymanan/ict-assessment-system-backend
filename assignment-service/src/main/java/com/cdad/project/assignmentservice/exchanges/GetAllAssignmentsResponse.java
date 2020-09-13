package com.cdad.project.assignmentservice.exchanges;

import com.cdad.project.assignmentservice.dto.AssignmentDTO;
import lombok.Data;

import java.util.List;

@Data
public class GetAllAssignmentsResponse {
  List<AssignmentDTO> assignments;
}
