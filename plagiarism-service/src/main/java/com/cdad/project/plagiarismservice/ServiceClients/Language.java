package com.cdad.project.plagiarismservice.ServiceClients;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    JAVA("java"),
    PYTHON("python"),
    C("c"),
    CPP("cpp");
    private final String value;

    Language() {
        this.value = this.getValue();
    }

    @JsonValue
    final String value() {
        return this.getValue();
    }

}
