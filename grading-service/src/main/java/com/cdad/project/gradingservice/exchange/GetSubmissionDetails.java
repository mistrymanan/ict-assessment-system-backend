package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.serviceclient.classroomservice.dto.UserDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class GetSubmissionDetails {
    private List<SubmissionDetailsDTO> completed;
    private Set<String> due;
}
