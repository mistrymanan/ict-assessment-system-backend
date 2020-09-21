package com.cdad.project.executionservice.executor.interpreted;

import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.BaseExecutor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PythonExecutor extends BaseExecutor implements InterpretedExecutor {
    //private long memoryConsumption;

    public PythonExecutor(Program program) throws IOException {
        super(program);
    }


    @Override
    public Status run() throws InterruptedException, IOException {
        List<String> command = new ArrayList<>();
        command.add("sudo chroot /jail/");
        command.add("timeout " + this.getTimeout());
        command.add("python3 ." + getBaseExecutionPath() + "Solution.py");
        String commandString = String.join(" ", command);
        return run(commandString);
    }
}
