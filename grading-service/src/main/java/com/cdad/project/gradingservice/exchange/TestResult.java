package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Reason;
import com.cdad.project.gradingservice.entity.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
    private String id;
    private ResultStatus status;
    private Reason reason;
}
