package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;

import com.cdad.project.gradingservice.serviceclient.executionservice.dto.BuildCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class GetBuildsResponse {
    private Map<String, BuildCode> builds;
}
