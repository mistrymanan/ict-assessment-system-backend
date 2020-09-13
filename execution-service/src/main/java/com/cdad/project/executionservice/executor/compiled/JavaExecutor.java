package com.cdad.project.executionservice.executor.compiled;

import com.cdad.project.executionservice.entity.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.compiled.CompiledExecutor;
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
public class JavaExecutor extends BaseExecutor implements CompiledExecutor {
    //private long memoryConsumption;

    ProcessBuilder processBuilder;

    public JavaExecutor(Program program) throws IOException, InterruptedException {
        super(program);
        this.processBuilder=new ProcessBuilder();
        if(!this.compile().equals(0)){
            this.status=Status.COMPILATIONERROR;
            System.out.println("compilation error");
        }
    }


    @Override
    public Integer compile() throws IOException, InterruptedException {
        List<String> command=new ArrayList<String>();
        command.add("sudo chroot /jail/ echo");
        command.add("cd java/"+getUniquePath()+"");
        command.add("javac Solution.java");
        ProcessBuilder processBuilder=new ProcessBuilder().command("/bin/bash","-c",String.join(";",command))
     //   this.processBuilder.command("/bin/bash","-c","sudo chroot /jail/ echo;cd java/"+this.uniquePath+"/;javac Solution.java;")
                .directory(new File(getJailPath()))
                .inheritIO()
                .redirectError(new File(this.getContainerPath()+"error.txt"));
        Process process=processBuilder.start();
        Integer statusCode=process.waitFor();
        System.out.println(statusCode);
        return statusCode;
    }


    @Override
    public Status run() throws InterruptedException, IOException {
        //if compilation error occurs then send the error code.
        if(this.getStatus()!=null&&this.getStatus().equals(Status.COMPILATIONERROR)) return this.getStatus();

        long startTime=System.nanoTime();
        List<String> command=new ArrayList<String>();
        command.add("sudo chroot /jail/ echo");
        command.add("cd java/"+this.getUniquePath()+"/ ");
        command.add("cat input.txt | timeout "+this.getTimeout()+" java Solution");
        //this.processBuilder.command("/bin/bash","-c","sudo chroot /jail/ pwd ; cd java/"+this.uniquePath+"/ ; cat input.txt | timeout "+this.getTimeout()+" java Solution")
        //this.processBuilder.command("sudo chroot /jail/ echo ; cd java/"+this.uniquePath+"/ ; cat input.txt | timeout "+this.getTimeout()+" java Solution")
        this.processBuilder.command("/bin/bash","-c",String.join(" ; ",command))
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


//    @Override
//    public String getOutput() throws IOException, InterruptedException {
//        if(this.status.equals(Status.TIMEOUT)) {return "TIMEOUT";}
//        else if (this.status.equals(Status.SUCCEED)){return Files.readString(Paths.get(this.getContainerPath()+"output.txt"));}
//        return getErrorMessage();
//    }

//    @Override
//    public String getErrorMessage() throws IOException, InterruptedException {
////        if (this.status.equals(Status.COMPILATIONERROR)) return this.getCompilationErrorMessage();
//        if (this.status.equals(Status.COMPILATIONERROR) || !this.status.equals(Status.SUCCEED)) {
//            return Files.readString(Paths.get(this.getContainerPath()+"error.txt"));
//        }
//        return null;
//    }
//    @Override
//    public String getCompilationErrorMessage() throws IOException, InterruptedException {
//        return Files.readString(Paths.get(this.getContainerPath()+"error.txt"));
//    }
}
