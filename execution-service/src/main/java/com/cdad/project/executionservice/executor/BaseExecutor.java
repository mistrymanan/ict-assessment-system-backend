package com.cdad.project.executionservice.executor;

import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.entity.Status;
import lombok.Data;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Data
abstract public class BaseExecutor implements Executor {
    private String jailPath = "/jail";
    private String buildId;
    private Program program;
    private String baseExecutionPath;
    private String uniquePath;
    private String containerPath;
    private String sourceFileExtention;
    final private Integer timeout = 5;
    private long executionTime;
    public Status status;

    private BaseExecutor() {
        this.buildId = UUID.randomUUID().toString();
        this.uniquePath = this.getBuildId();

    }

    public BaseExecutor(Program program) throws IOException {
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
    public boolean creteWorkingDirectory() throws IOException {
        File file = new File(this.getContainerPath());
        return file.mkdir();
    }

    @Override
    public void createUtilityFiles() throws IOException {
        this.createSourceFile();
        this.createInputFile();
        this.createErrorFile();
        this.createOutputFile();
    }

    @Override
    public void createSourceFile() throws IOException {
        Files.writeString(Paths.get(getContainerPath() + "Solution" + this.getSourceFileExtention()), this.getProgram().getSourceCode());
    }

    @Override
    public void createInputFile() throws IOException {
        if (getProgram().getTestCasesList()!=null && !getProgram().getTestCasesList().isEmpty()) {
            getProgram().getTestCasesList().forEach(testCase -> {
                try {
                    if (testCase.getInput() != null) {

                        Files.writeString(Paths.get(getContainerPath()
                                        + "Input-"
                                        + testCase.getTestCase()
                                        + ".txt")
                                , testCase.getInput());
                    } else {
                        new File(getContainerPath() + testCase.getTestCase() + ".txt").createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(getProgram().getInput()!=null){
            Files.writeString(Paths.get(getContainerPath() + "Input.txt"), this.getProgram().getInput());
        }
        else{
            new File(this.getContainerPath() + "Input.txt").createNewFile();
        }
    }

    @Override
    public void createOutputFile() throws IOException {
        if (getProgram().getTestCasesList()!=null && !getProgram().getTestCasesList().isEmpty()) {
            getProgram().getTestCasesList().forEach(testCase -> {
                try {
                    new File(getContainerPath() + "Output-"
                            + testCase.getTestCase()
                            + ".txt").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        new File(this.getContainerPath() + "Output.txt").createNewFile();
    }

    @Override
    public void createErrorFile() throws IOException {
        if (getProgram().getTestCasesList()!=null && !getProgram().getTestCasesList().isEmpty()) {
            getProgram().getTestCasesList().forEach(testCase -> {
                try {
                    new File(getContainerPath() + "Error-" + testCase.getTestCase() + "" + ".txt").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        new File(this.getContainerPath() + "Error.txt").createNewFile();
    }

    @Override
    public void clean() throws IOException {
        File file = new File(this.getContainerPath());
        FileUtils.deleteDirectory(file);
    }

    @Override
    public String getOutput() throws IOException, InterruptedException {
        System.out.println("from abstract clas " + status);
        if (this.status.equals(Status.RUNTIMEERROR) || this.status.equals(Status.COMPILATIONERROR)) {
            return this.getErrorMessage();
        } else if (this.status.equals(Status.TIMEOUT)) {
            return "TIMEOUT";
        }
        return Files.readString(Paths.get(this.getContainerPath() + "Output.txt"));
    }
    @Override
    public String getErrorMessage() throws IOException, InterruptedException {
        return Files.readString(Paths.get(this.getContainerPath() + "Error.txt"));
    }

    @Override
    public void writeOutput() throws IOException {
        if(getProgram().getTestCasesList()!=null){
            getProgram().getTestCasesList().forEach(testCase -> {
                try {
                    String output=Files.readString((Paths.get(this.getContainerPath()
                            +"Output-"
                            +testCase.getTestCase()
                            +".txt"
                    )));
                    testCase.setOutput(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public Status getStatusBasedOnStatusCode(Integer statusCode){
        if (statusCode.equals(0)) { return Status.SUCCEED;}
        else if(statusCode.equals(143) || statusCode.equals(124)){
            return Status.TIMEOUT;
        }
        else{
            return Status.RUNTIMEERROR;
        }
    }

    public void setStatusBasedOnStatusCode(Integer statusCode) {
        if (statusCode.equals(0)) { status=Status.SUCCEED;}
        else if(statusCode.equals(143) || statusCode.equals(124)){
            status=Status.TIMEOUT;
        }
        else{status=Status.RUNTIMEERROR;}
    }

}
