package com.cdad.project.executionservice.executor.interpreted;

import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.dto.TestCase;
import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PythonExecutor extends BaseExecutor implements InterpretedExecutor {
    //private long memoryConsumption;

    public static void main(String[] args) throws IOException, InterruptedException {
//        System.out.println("Hello World");
//        List<TestCase> testCaseList=new ArrayList<>();
//        TestCase testcase=new TestCase();
//        testcase.setInput("1\n2");
//        testcase.setTestCase("testing");
//        testCaseList.add(testcase);
//        Program program=new Program();
//        //program.setInput("1\n2");
//        program.setLanguage(Language.PYTHON);
//        program.setSourceCode("print(int(input())+int(input()))");
//        //program.setInput("1\n2");
//        program.setTestCasesList(testCaseList);
//
//        BaseExecutor baseExecutor=new PythonExecutor(program);
//        baseExecutor.run();
//        baseExecutor.writeOutput();
//        System.out.println("wrote it.");
//
//
//        //System.out.println(baseExecutor.run());


    }

    public PythonExecutor(Program program) throws IOException {
        super(program);
    }


    @Override
    public Status run() throws InterruptedException, IOException {

        List<String> command=new ArrayList<String>();
        command.add("sudo chroot /jail/");
        command.add("timeout "+this.getTimeout());
        command.add("python ."+getBaseExecutionPath()+"Solution.py");

        if(getProgram().getTestCasesList()==null) {
            long startTime, endTime;
            System.out.println(String.join(" ",command));
            ProcessBuilder processBuilder = new ProcessBuilder()
                    //.command("/bin/bash", "-c", "sudo chroot /jail/ timeout " + getTimeout() + " python3.8 /python/" + getUniquePath() + "/Solution.py")
                   .command("/bin/bash","-c",String.join(" ",command))
                    //.command("/bin/bash","-c","sudo chroot /jail/ /bin/bash -c cd "+getBaseExecutionPath()+"")
                    .directory(new File(this.getContainerPath()))
                    .redirectInput(new File(this.getContainerPath()+"Input.txt"))
                    .redirectOutput(new File(this.getContainerPath() + "Output.txt"))
                    .redirectError(new File(this.getContainerPath() + "Error.txt"));
            startTime = System.nanoTime();
            Process process = processBuilder.start();
            Integer statusCode = process.waitFor();
            endTime = System.nanoTime();
            getProgram().setExecutionTime(endTime - startTime);
            this.setStatusBasedOnStatusCode(statusCode);
            return super.getStatus();
        }
        else{
            long buildStartTime,buildEndTime;
            buildStartTime=System.nanoTime();
            getProgram().getTestCasesList().forEach(testCase -> {
                long startTime, endTime;
                ProcessBuilder processBuilder = new ProcessBuilder()
                        .command("/bin/bash", "-c", "sudo chroot /jail/ timeout " + getTimeout() + " python3.8 /python/" + getUniquePath() + "/Solution.py")
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
//    public void setStatusBasedOnStatusCode(Integer statusCode) {
//        if (statusCode.equals(0)) { status=Status.SUCCEED;}
//        else if(statusCode.equals(143) || statusCode.equals(124)){
//            status=Status.TIMEOUT;
//        }
//        else{status=Status.RUNTIMEERROR;}
//    }
}
