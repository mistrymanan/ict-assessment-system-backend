package com.cdad.project.executionservice.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@NoArgsConstructor
public class GetBuildsRequest {
    private HashSet<String> buildIds;
}
