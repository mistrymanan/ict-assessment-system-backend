package com.cdad.project.executionservice.executor.compiled;

import com.cdad.project.executionservice.entity.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CPPExecutor extends BaseExecutor implements CompiledExecutor {
    public CPPExecutor(Program program) throws IOException, InterruptedException {
        super(program);
        if(!this.compile().equals(0)){
            this.status=Status.COMPILATIONERROR;
        }
    }

    @Override
    public Integer compile() throws IOException, InterruptedException {
        List<String> command=new ArrayList<String>();
        //command.add("sudo chroot /jail/ echo");
        command.add("cd /jail"+getBaseExecutionPath()+"/ ");
        command.add("c++ Solution.cpp");
        ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",String.join(";",command))
                .directory(new File(getJailPath()))
                .redirectError(new File(this.getContainerPath()+"error.txt"));
        Process process=processBuilder.start();
        Integer statusCode=process.waitFor();
        return statusCode;
    }

    @Override
    public Status run() throws InterruptedException, IOException {
        if(this.getStatus()!=null&&this.getStatus().equals(Status.COMPILATIONERROR)) return this.getStatus();

        long startTime=System.nanoTime();
        List<String> command=new ArrayList<String>();
        command.add("chroot /jail/ echo");
        command.add("cd c++/"+this.getUniquePath()+"/ ");
        command.add("cat input.txt | timeout "+this.getTimeout()+" ./a.out");
        ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",String.join(" ; ",command))
                .directory(new File(getJailPath()))
                .redirectOutput(new File(getContainerPath()+"output.txt"))
                .redirectError(new File(getContainerPath()+"error.txt"));
        Process process=processBuilder.start();
        Integer statusCode=process.waitFor();
        long endTime=System.nanoTime();
        setExecutionTime(endTime-startTime);
        this.setStatusBasedOnStatusCode(statusCode);
        return this.status;
    }
    public void setStatusBasedOnStatusCode(Integer statusCode) {
        if (statusCode.equals(0)) { this.status=Status.SUCCEED;}
        else if(statusCode.equals(143) || statusCode.equals(124)){
            this.status=Status.TIMEOUT;
        }
        else{this.status=Status.RUNTIMEERROR;}
    }
}
