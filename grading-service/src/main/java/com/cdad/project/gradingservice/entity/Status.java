package com.cdad.project.gradingservice.entity;

public enum Status {
    UNEXPECTED_ERROR,
    WRONG_OUTPUT,
    ACCEPTED,
    SUCCEED,
    TEST_FAILED,
    COMPILE_ERROR,
    RUNTIME_ERROR,
    TIMEOUT,
    LANGUAGE_NOT_ALLOWED
}
