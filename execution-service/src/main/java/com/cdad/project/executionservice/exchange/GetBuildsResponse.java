package com.cdad.project.executionservice.exchange;

import com.cdad.project.executionservice.dto.BuildCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class GetBuildsResponse {
    private Map<String, BuildCode> builds;
}
