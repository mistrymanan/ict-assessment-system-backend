package com.cdad.project.executionservice.Executor;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Executor {
    void setupEnvironment() throws IOException;
    void createSourceFile() throws IOException;
    void createInputFile() throws IOException;
    void createOutputFile() throws IOException;
    void createErrorFile() throws IOException;
    String run() throws InterruptedException, IOException;
    void clean();
    String getOutput() throws IOException;
}
