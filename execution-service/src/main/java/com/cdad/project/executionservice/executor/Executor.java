package com.cdad.project.executionservice.executor;

import com.cdad.project.executionservice.dto.BuildOutput;
import com.cdad.project.executionservice.dto.TestInput;
import com.cdad.project.executionservice.dto.TestOutput;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;

import java.io.IOException;
import java.util.List;

public interface Executor {
    String jailPath = "/jail";
    static String getJailPath(){return jailPath;}
    void setupEnvironment() throws IOException;

    void setupEnvironment(List<TestInput> testInputs) throws IOException;

    boolean creteWorkingDirectory() throws IOException;

    void createUtilityFiles() throws IOException;

    void createUtilityFiles(List<TestInput> testInputs) throws IOException;

    void createSourceFile() throws IOException;

    void createInputFile(TestInput testInput) throws IOException;
    void createInputFile(List<TestInput> testInputs) throws IOException;

    void createOutputFile() throws IOException;
    void createOutputFile(List<TestInput> testInputs) throws IOException;

    void createErrorFile() throws IOException;
    void createErrorFile(List<TestInput> testInputs) throws IOException;

    //void createErrorFile() throws IOException;
    //void writeOutput() throws IOException;

    TestOutput run(TestInput testInput) throws InterruptedException, IOException, CompilationErrorException;
    List<TestOutput> run(List<TestInput> testInputs) throws InterruptedException, IOException, CompilationErrorException;

    TestOutput run(String commandString,TestInput testInput) throws InterruptedException, IOException, CompilationErrorException;
    List<TestOutput> run(String commandString, List<TestInput> testInputs) throws InterruptedException, IOException, CompilationErrorException;

    void clean() throws IOException;

    String getOutput(Status status) throws IOException, InterruptedException;
    String getOutput(String testId, Status status) throws IOException, InterruptedException;

    String getBuildId();
    String getErrorMessage() throws IOException,InterruptedException;
    String getErrorMessage(String testId) throws IOException;
    long getExecutionTime();
}