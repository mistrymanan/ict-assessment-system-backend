package com.cdad.project.executionservice.Executor;

public interface Executor {
    void setupEnvironment();
    void createSourceFile();
    void createInputFile();
    String run();
    void clean();
    String getOutput();
}
