package com.cdad.project.assignmentservice.exchanges;

import com.cdad.project.assignmentservice.dto.Assignment;
import lombok.Data;

import java.util.List;

@Data
public class GetAllAssignmentsResponse {
  List<Assignment> assignments;
}
