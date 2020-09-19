package com.cdad.project.executionservice.executor;

import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Program;
import com.cdad.project.executionservice.executor.compiled.CExecutor;
import com.cdad.project.executionservice.executor.compiled.JavaExecutor;
import com.cdad.project.executionservice.executor.interpreted.PythonExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class ExecutorFactory {
    public Executor createExecutor(Program program) throws IOException, InterruptedException {
        Executor executor;
        if(program.getLanguage()==Language.JAVA) return new JavaExecutor(program);
        else if(program.getLanguage()==Language.PYTHON) return new PythonExecutor(program);
        else if(program.getLanguage()==Language.C) return new CExecutor(program);
        return null;
    }
}
