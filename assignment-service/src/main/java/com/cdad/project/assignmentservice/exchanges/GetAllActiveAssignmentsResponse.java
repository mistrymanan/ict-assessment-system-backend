package com.cdad.project.assignmentservice.exchanges;

import com.cdad.project.assignmentservice.dto.ActiveAssignmentDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class GetAllActiveAssignmentsResponse {
  List<ActiveAssignmentDTO> activeAssignments;
}
