package com.cdad.project.executionservice.executor.compiled;

import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class JavaExecutor extends BaseExecutor implements CompiledExecutor {
    //private long memoryConsumption;

    public JavaExecutor(Program program) throws IOException, InterruptedException {
        super(program);
        if(!this.compile().equals(0)){
            this.status=Status.COMPILATIONERROR;
        }
    }


    @Override
    public Integer compile() throws IOException, InterruptedException {
        List<String> command=new ArrayList<String>();
//        command.add("sudo chroot /jail/");
        //command.add("cd java/"+getUniquePath()+"");
        command.add("javac ./"+getBaseExecutionPath()+"Solution.java");

        ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",String.join(" ",command))
     //   this.processBuilder.command("/bin/bash","-c","sudo chroot /jail/ echo;cd java/"+this.uniquePath+"/;javac Solution.java;")
                .directory(new File(getJailPath()))
                .redirectError(new File(this.getContainerPath()+"Error.txt"));
        Process process=processBuilder.start();
        Integer statusCode=process.waitFor();
        return statusCode;
    }


    @Override
    public Status run() throws InterruptedException, IOException {

        //if compilation error occurs then send the error code.
        if(this.getStatus()!=null&&this.getStatus().equals(Status.COMPILATIONERROR)) return this.getStatus();
        List<String> command=new ArrayList<String>();

        command.add("sudo chroot /jail/");
        command.add("timeout "+this.getTimeout());
        command.add("java -cp ."+getBaseExecutionPath()+" Solution");
        String commandString=String.join(" ",command);

        if(getProgram().getTestCasesList()==null) {
            long startTime, endTime;
            ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",commandString)
                    .directory(new File(getJailPath()))
                    .redirectInput(new File(this.getContainerPath()+"Input.txt"))
                    .redirectOutput(new File(this.getContainerPath() + "Output.txt"))
                    .redirectError(new File(this.getContainerPath() + "Error.txt"));
            startTime = System.nanoTime();
            Process process = processBuilder.start();
            Integer statusCode = process.waitFor();
            endTime = System.nanoTime();
            getProgram().setExecutionTime(endTime - startTime);
            this.setStatusBasedOnStatusCode(statusCode);
            getProgram().setOutput(getOutput());
            return super.getStatus();
        }
        else{
            long buildStartTime,buildEndTime;
            buildStartTime=System.nanoTime();
            getProgram().getTestCasesList().forEach(testCase -> {
                long startTime, endTime;
                ProcessBuilder processBuilder = new ProcessBuilder()
                        .command("/bin/bash", "-c",commandString)
                        .directory(new File(Executor.getJailPath()))
                        .redirectInput(new File(getContainerPath() + "Input-"+testCase.getTestCase()+".txt"))
                        .redirectOutput(new File(getContainerPath() + "Output-"+testCase.getTestCase()+".txt"))
                        .redirectError(new File(getContainerPath() + "Error-"+testCase.getTestCase()+".txt"));
                startTime = System.nanoTime();
                Process process = null;
                Integer statusCode = null;
                try {
                    process = processBuilder.start();
                    statusCode = process.waitFor();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                endTime = System.nanoTime();
                testCase.setExecutionTime(endTime - startTime);
                testCase.setStatus(this.getStatusBasedOnStatusCode(statusCode));
            });
            buildEndTime=System.nanoTime();
            getProgram().setExecutionTime(buildEndTime-buildStartTime);
            writeOutput();
            boolean b = getProgram().getTestCasesList().stream().allMatch(testCase ->
            {
                return testCase.getStatus().equals(Status.SUCCEED);
            });
            return b ? Status.SUCCEED : Status.TESTFAILED ;
        }
    }

}
