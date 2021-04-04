package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@NoArgsConstructor
public class GetBuildsRequest {
    private HashSet<String> buildIds;
}
