package com.cdad.project.executionservice.executor.interpreted;

import com.cdad.project.executionservice.dto.ProgramInput;
import com.cdad.project.executionservice.dto.BuildOutput;
import com.cdad.project.executionservice.dto.TestInput;
import com.cdad.project.executionservice.dto.TestOutput;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;
import com.cdad.project.executionservice.executor.BaseExecutor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PythonExecutor extends BaseExecutor implements InterpretedExecutor {
    //private long memoryConsumption;
    String runCommandString;

    public PythonExecutor(ProgramInput program) throws IOException {
        super(program);
        List<String> command = new ArrayList<>();
        command.add("chroot /jail/");
        command.add("timeout " + this.getTimeout());
        command.add("python3 ." + getBaseExecutionPath() + "Solution.py");
        this.setRunCommandString(String.join(" ", command));
    }

    @Override
    public TestOutput run(String testInput) throws InterruptedException, IOException, CompilationErrorException {
        return super.run(getRunCommandString(), testInput);
    }

    @Override
    public List<TestOutput> run(List<TestInput> testInputs) throws InterruptedException, IOException, CompilationErrorException {
        return super.run(getRunCommandString(), testInputs);
    }
}
