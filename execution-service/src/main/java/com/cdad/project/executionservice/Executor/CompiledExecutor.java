package com.cdad.project.executionservice.Executor;

import java.io.IOException;

public interface CompiledExecutor extends Executor {
    void compile() throws IOException, InterruptedException;
}
