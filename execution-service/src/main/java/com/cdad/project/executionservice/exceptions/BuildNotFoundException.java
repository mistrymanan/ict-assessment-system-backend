package com.cdad.project.executionservice.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildNotFoundException extends Exception {
    public BuildNotFoundException(String message) {
        super(message);
    }
}
