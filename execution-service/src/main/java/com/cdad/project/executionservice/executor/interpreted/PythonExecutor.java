package com.cdad.project.executionservice.executor.interpreted;

import com.cdad.project.executionservice.entity.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import lombok.Data;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PythonExecutor extends BaseExecutor implements InterpretedExecutor {
    //private long memoryConsumption;


    public PythonExecutor(Program program) throws IOException {
        super(program);
    }

    @Override
    public Status run() throws InterruptedException, IOException {
        long startTime,endTime;
        ProcessBuilder processBuilder=new ProcessBuilder()
                .command("/bin/bash","-c","chroot /jail/ timeout "+getTimeout()+" python3.8 /python/"+getUniquePath()+"/Solution.py")
                .directory(new File(Executor.getJailPath()))
                .redirectInput( new File(getContainerPath()+"input.txt"))
                .redirectOutput(new File(this.getContainerPath()+"output.txt"))
                .redirectError(new File(this.getContainerPath()+"error.txt"));
        startTime=System.nanoTime();
        Process process=processBuilder.start();
        Integer statusCode=process.waitFor();
        endTime=System.nanoTime();
        setExecutionTime(endTime-startTime);
        this.setStatusBasedOnStatusCode(statusCode);
        return super.getStatus();
    }
    public void setStatusBasedOnStatusCode(Integer statusCode) {
        if (statusCode.equals(0)) { status=Status.SUCCEED;}
        else if(statusCode.equals(143) || statusCode.equals(124)){
            status=Status.TIMEOUT;
        }
        else{status=Status.RUNTIMEERROR;}
    }
}
