package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetBuildsRequest {
    private List<String> buildIds;
}
