package com.cdad.project.executionservice.Executor;

import com.cdad.project.executionservice.entities.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class JavaExecutor implements CompiledExecutor {
    private Program program;
    private ProcessBuilder processBuilder;

    private File sourceFile;
    private File inputFile;
    private File outputFile;
    private File errorFile;

    public JavaExecutor() throws IOException, InterruptedException {
        this.processBuilder=new ProcessBuilder();
    }

    public JavaExecutor(Program program) throws IOException, InterruptedException {
        this();
        this.program = program;
        this.setupEnvironment();
        this.compile();
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    public void setProcessBuilder(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    @Override
    public void setupEnvironment() throws IOException {
            this.createSourceFile();
            this.createInputFile();
            this.createErrorFile();
            this.createOutputFile();
    }

    @Override
    public void createSourceFile() throws IOException {
        this.sourceFile=new File("/jail/java/Solution.java");
        FileWriter fileWriter=new FileWriter(this.sourceFile);
        fileWriter.write(this.program.getSourceCode());
    }

    @Override
    public void createInputFile() throws IOException {
        this.inputFile=new File("/jail/java/input.txt");
        FileWriter fileWriter=new FileWriter(this.inputFile);
        fileWriter.write(this.program.getInput());
    }

    @Override
    public void createOutputFile() throws IOException {
        this.outputFile=new File("/jail/java/output.txt");
    }

    @Override
    public void createErrorFile() throws IOException {
        this.errorFile=new File("/jail/java/error.txt");
    }

    @Override
    public void compile() throws IOException, InterruptedException {
        this.processBuilder.command("/bin/bash","-c","sudo chroot . timeout "
                +this.program.getTimeout()
                +" javac Solution.java")
                .directory(new File("/jail/"));
        Process process=this.processBuilder.start();
        System.out.println("Compilation Process ID:"+process.waitFor());
    }

    @Override
    public String run() throws InterruptedException, IOException {
        this.processBuilder.command("/bin/bash","-c","sudo chroot . java Solution")
                .directory(new File("/jail/"))
                .redirectInput(this.inputFile)
                .redirectOutput(this.outputFile)
                .redirectError(this.errorFile);
        Process process=this.processBuilder.start();
        System.out.println("Compilation Process ID:"+process.waitFor());
        return this.getOutput();
    }

    @Override
    public void clean() {
        this.getErrorFile().delete();
        this.getInputFile().delete();
        this.getOutputFile().delete();
        this.getSourceFile().delete();
    }

    @Override
    public String getOutput() throws IOException {
        return Files.readString(Paths.get("/jail/java/output.txt"));
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getErrorFile() {
        return errorFile;
    }

    public void setErrorFile(File errorFile) {
        this.errorFile = errorFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
