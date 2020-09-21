package com.cdad.project.executionservice.executor.compiled;

import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
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
        List<String> command = new ArrayList<>();
        command.add("chroot /jail/");
        //command.add("cd java/"+getUniquePath()+"");
        command.add("javac ./"+getBaseExecutionPath()+"Solution.java");

        ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",String.join(" ",command))
                .directory(new File(getJailPath()))
                .redirectError(new File(this.getContainerPath()+"Error.txt"));
        Process process=processBuilder.start();
        return process.waitFor();
    }


    @Override
    public Status run() throws InterruptedException, IOException {
        //if compilation error occurs then send the error code.
        if (this.getStatus() != null && this.getStatus().equals(Status.COMPILATIONERROR)) {
            getProgram().setOutput(getOutput());
            return this.getStatus();
        }
        List<String> command = new ArrayList<>();
        command.add("chroot /jail/");
        command.add("timeout " + this.getTimeout());
        command.add("java -cp ." + getBaseExecutionPath() + " Solution");
        String commandString = String.join(" ", command);
        return super.run(commandString);
    }
}
