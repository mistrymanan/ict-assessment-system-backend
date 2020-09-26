package com.cdad.project.executionservice.executor;

import com.cdad.project.executionservice.dto.ProgramInput;
import com.cdad.project.executionservice.dto.TestInput;
import com.cdad.project.executionservice.dto.TestOutput;
import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;
import lombok.Data;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
abstract public class BaseExecutor implements Executor {
    private String jailPath = "/jail";
    private String buildId;
    private ProgramInput program;
    private String baseExecutionPath;
    private String uniquePath;
    private String containerPath;
    private String sourceFileExtention;
    final private Integer timeout = 5;
    private long executionTime;
    private Status status;
    private String runCommandString;

    private BaseExecutor() {
        this.buildId = UUID.randomUUID().toString();
        this.uniquePath = this.getBuildId();

    }

    public BaseExecutor(ProgramInput program) throws IOException {
        this();
        this.program = program;
        if (program.getLanguage().equals(Language.PYTHON)) {
            this.baseExecutionPath = "/python/" + this.getUniquePath() + "/";
            this.sourceFileExtention = ".py";
        } else if (program.getLanguage().equals(Language.JAVA)) {
            this.baseExecutionPath = "/java/" + this.getUniquePath() + "/";
            this.sourceFileExtention = ".java";
        } else if (program.getLanguage().equals(Language.C)) {
            this.baseExecutionPath = "/c/" + this.getUniquePath() + "/";
            this.sourceFileExtention = ".c";
        } else if (program.getLanguage().equals(Language.CPP)) {
            this.baseExecutionPath = "/c++/" + this.getUniquePath() + "/";
            this.sourceFileExtention = ".cpp";
        }
        this.containerPath = this.getJailPath() + this.baseExecutionPath;
        this.setupEnvironment();
    }


    @Override
    public void setupEnvironment() throws IOException {
        if (this.creteWorkingDirectory()) {
            this.createUtilityFiles();
        }
    }

    @Override
    public void setupEnvironment(List<TestInput> testInputs) throws IOException{
        if (this.creteWorkingDirectory()) {
            this.createUtilityFiles(testInputs);
        }
    }

    @Override
    public boolean creteWorkingDirectory() {
        File file = new File(this.getContainerPath());
        return file.mkdir() || file.isDirectory();
    }

    @Override
    public void createUtilityFiles() throws IOException {
        this.createSourceFile();
        this.createErrorFile();
        this.createOutputFile();
    }

    @Override
    public void createUtilityFiles(List<TestInput> testInputs) throws IOException {
        this.createInputFile(testInputs);
        this.createErrorFile(testInputs);
        this.createOutputFile(testInputs);
    }

    @Override
    public void createSourceFile() throws IOException {
        Files.writeString(Paths.get(getContainerPath() + "Solution" + this.getSourceFileExtention()), this.getProgram().getSourceCode());
    }

    @Override
    public void createInputFile(String input) throws IOException{
        if (input != null) {
            Files.writeString(Paths.get(getContainerPath() + "Input.txt"),input);
        } else {
            new File(this.getContainerPath() + "Input.txt").createNewFile();
        }
    }

    @Override
    public void createInputFile(List<TestInput> testInputList) throws IOException {
        if (testInputList != null && !testInputList.isEmpty()) {
            testInputList.forEach(testCase -> {
                try {
                    if (testCase.getInput() != null) {

                        Files.writeString(Paths.get(getContainerPath()
                                        + "Input-"
                                        + testCase.getId()
                                        + ".txt")
                                , testCase.getInput());
                    } else {
                        new File(getContainerPath() +  testCase.getId() + ".txt").createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void createOutputFile() throws IOException {
        new File(this.getContainerPath() + "Output.txt").createNewFile();
    }

    @Override
    public void createOutputFile(List<TestInput> testInputs) throws IOException {
        if (testInputs != null && !testInputs.isEmpty()) {
            testInputs.forEach(testCase -> {
                try {
                    new File(getContainerPath() + "Output-"
                            + testCase.getId()
                            + ".txt").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void createErrorFile() throws IOException{
        new File(this.getContainerPath() + "Error.txt").createNewFile();
    }

    @Override
    public void createErrorFile(List<TestInput> testInputs) throws IOException {
        if (testInputs != null && !testInputs.isEmpty()) {
            testInputs.forEach(testCase -> {
                try {
                    new File(getContainerPath() + "Error-" + testCase.getId() + "" + ".txt").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void clean() throws IOException {
        File file = new File(this.getContainerPath());
        FileUtils.deleteDirectory(file);
    }



    public TestOutput run(String commandString,String input) throws InterruptedException, IOException, CompilationErrorException {
        if (this.getStatus() != null && this.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new CompilationErrorException(getErrorMessage());
        }
        this.createInputFile(input);
            TestOutput testOutput=new TestOutput();
            long startTime, endTime;
            ProcessBuilder processBuilder = new ProcessBuilder().command("/bin/bash", "-c", commandString)
                    .directory(new File(getJailPath()))
                    .redirectInput(new File(this.getContainerPath() + "Input.txt"))
                    .redirectOutput(new File(this.getContainerPath() + "Output.txt"))
                    .redirectError(new File(this.getContainerPath() + "Error.txt"));
            startTime = System.currentTimeMillis();
            Process process = processBuilder.start();
            Integer statusCode = process.waitFor();
            endTime = System.currentTimeMillis();
            Status status1=getStatusBasedOnStatusCode(statusCode);
            testOutput.setExecutionTime(endTime - startTime);
            testOutput.setStatus(status1);
            testOutput.setOutput(getOutput(status1));
            return testOutput;

    }


//    public TestOutput run(String commandString, TestInput testInput) throws InterruptedException, IOException, CompilationErrorException {
//        if (this.getStatus() != null && this.getStatus().equals(Status.COMPILE_ERROR)) {
//            throw new CompilationErrorException(getErrorMessage());
//        }
//        this.createInputFile(testInput);
//            TestOutput testOutput=new TestOutput();
//            long startTime, endTime;
//            ProcessBuilder processBuilder = new ProcessBuilder().command("/bin/bash", "-c", commandString)
//                    .directory(new File(getJailPath()))
//                    .redirectInput(new File(this.getContainerPath() + "Input.txt"))
//                    .redirectOutput(new File(this.getContainerPath() + "Output.txt"))
//                    .redirectError(new File(this.getContainerPath() + "Error.txt"));
//            startTime = System.currentTimeMillis();
//            Process process = processBuilder.start();
//            Integer statusCode = process.waitFor();
//            endTime = System.currentTimeMillis();
//            Status status1=getStatusBasedOnStatusCode(statusCode);
//            testOutput.setId(testInput.getId());
//            testOutput.setExecutionTime(endTime - startTime);
//            testOutput.setStatus(status1);
//            testOutput.setOutput(getOutput(status1));
//            return testOutput;
//
//    }


    @Override
    public List<TestOutput> run(String commandString, List<TestInput> testInputs) throws InterruptedException, IOException, CompilationErrorException {
        if (this.getStatus() != null && this.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new CompilationErrorException(getErrorMessage());
        }
        this.setupEnvironment(testInputs);
        List<TestOutput> testOutputs=new LinkedList<>();
        testInputs.forEach(testCase -> {

            TestOutput testOutput=new TestOutput();
            testOutput.setId(testCase.getId());

            long startTime, endTime;
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("/bin/bash", "-c", commandString)
                    .directory(new File(Executor.getJailPath()))
                    .redirectInput(new File(getContainerPath() + "Input-" + testCase.getId() + ".txt"))
                    .redirectOutput(new File(getContainerPath() + "Output-" + testCase.getId() + ".txt"))
                    .redirectError(new File(getContainerPath() + "Error-" +testCase.getId()+ ".txt"));
            startTime = System.currentTimeMillis();
            Process process = null;
            Integer statusCode = null;
            try {
                process = processBuilder.start();
                statusCode = process.waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
            Status status=this.getStatusBasedOnStatusCode(statusCode);

            testOutput.setExecutionTime(endTime - startTime);
            testOutput.setStatus(status);

            try {
                testOutput.setOutput(this.getOutput(testCase.getId(),status));
            } catch (IOException e) {
                e.printStackTrace();
            }
        testOutputs.add(testOutput);
        });

        boolean testSucceed = testOutputs.stream().allMatch(testCase -> testCase.getStatus().equals(Status.SUCCEED));
        this.status= testSucceed ? Status.SUCCEED : Status.TEST_FAILED;
        return testOutputs;
    }

    @Override
    public String getOutput(Status status) throws IOException, InterruptedException {
        if (status.equals(Status.RUNTIME_ERROR)) {
            return this.getErrorMessage();
        } else if (status.equals(Status.TIMEOUT)) {
            return "TIMEOUT";
        }
        return Files.readString(Paths.get(this.getContainerPath() + "Output.txt"));
    }

    @Override
    public String getOutput(String testCaseId,Status status) throws IOException {
        if (status.equals(Status.RUNTIME_ERROR)) {
            return this.getErrorMessage(testCaseId);
        } else if (status.equals(Status.TIMEOUT)) {
            return "TIMEOUT";
        }
        return Files.readString(Paths.get(this.getContainerPath() + "Output-"+testCaseId+".txt"));
    }

    @Override
    public String getErrorMessage() throws IOException {
        return Files.readString(Paths.get(this.getContainerPath() + "Error.txt"));
    }

    @Override
    public String getErrorMessage(String testId) throws IOException {
        return Files.readString(Paths.get(this.getContainerPath()+"Error-"+testId+".txt"));
    }


//    @Override
//    public void writeOutput() {
//        if (getProgram().getTestCasesList() != null) {
//            getProgram().getTestCasesList().forEach(testCase -> {
//                try {
//                    String output = Files.readString((Paths.get(this.getContainerPath()
//                            + "Output-"
//                            + testCase.getTestCase()
//                            + ".txt"
//                    )));
//                    testCase.setOutput(output);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }

    public Status getStatusBasedOnStatusCode(Integer statusCode) {
        if (statusCode.equals(0)) {
            return Status.SUCCEED;
        } else if (statusCode.equals(143) || statusCode.equals(124)) {
            return Status.TIMEOUT;
        } else {
            return Status.RUNTIME_ERROR;
        }
    }

    public void setStatusBasedOnStatusCode(Integer statusCode) {
        if (statusCode.equals(0)) {
            status = Status.SUCCEED;
        } else if (statusCode.equals(143) || statusCode.equals(124)) {
            status = Status.TIMEOUT;
        } else {
            status = Status.RUNTIME_ERROR;
        }
    }

}
