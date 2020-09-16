package com.cdad.project.executionservice.executor;

import com.cdad.project.executionservice.entity.Status;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;

public interface Executor {
    String jailPath = "/jail";
    static String getJailPath(){return jailPath;}
    void setupEnvironment() throws IOException;
    boolean creteWorkingDirectory() throws IOException;

    void createUtilityFiles() throws IOException;
    void createSourceFile() throws IOException;
    void createInputFile() throws IOException;
    void createOutputFile() throws IOException;
    void createErrorFile() throws IOException;

    Status run() throws InterruptedException, IOException;
    void clean() throws IOException;

    String getOutput() throws IOException, InterruptedException;
    String getBuildId();
    String getErrorMessage() throws IOException,InterruptedException;
    long getExecutionTime();
}