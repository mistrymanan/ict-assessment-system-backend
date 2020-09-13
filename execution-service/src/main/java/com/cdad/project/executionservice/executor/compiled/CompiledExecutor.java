package com.cdad.project.executionservice.executor.compiled;

import com.cdad.project.executionservice.executor.Executor;

import java.io.IOException;

public interface CompiledExecutor extends Executor {
    Integer compile() throws IOException, InterruptedException;
}
