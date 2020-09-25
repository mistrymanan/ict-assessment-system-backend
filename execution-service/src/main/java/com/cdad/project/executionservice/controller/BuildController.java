package com.cdad.project.executionservice.controller;

import com.cdad.project.executionservice.dto.*;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;
import com.cdad.project.executionservice.exchange.PostBuildRequest;
import com.cdad.project.executionservice.exchange.PostRunRequest;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.ExecutorFactory;
import com.cdad.project.executionservice.service.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("")
public class BuildController {
    private final BuildService buildService;
    @Autowired
    private ExecutorFactory executorFactory;
    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping("/builds")
    public BuildOutput postBuild(@RequestBody PostBuildRequest postBuildRequest) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput=new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
//        List<TestInput> testInputs=new LinkedList<>();
//        postBuildRequest.getInputs().forEach(testInput -> {
//            testInputs.add(new TestInput(testInput.getInput()));
//        });
        List<TestInput> testInputs=postBuildRequest.getInputs();
        Executor executor=this.executorFactory.createExecutor(programInput);
        List<TestOutput> testOutputs =executor.run(testInputs);
        executor.clean();

        BuildOutput buildOutput=new BuildOutput();
        buildOutput.setBuildId(executor.getBuildId());
        buildOutput.setTestOutputs(testOutputs);

        return buildOutput;
    }

    @GetMapping("/builds/{buildId}")
    public BuildEntity postBuild(@PathVariable String buildId) {
        return this.buildService.getBuildById(buildId);
    }

    @GetMapping("/builds/all")
    public List<BuildEntity> getBuilds() {
        return this.buildService.getAllBuild();
    }

    @PostMapping("/run")
    public TestOutput postRun(@RequestBody PostRunRequest postRunRequest) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput=new ProgramInput();
        programInput.setSourceCode(postRunRequest.getSourceCode());
        programInput.setLanguage(postRunRequest.getLanguage());
        Executor executor = this.executorFactory.createExecutor(programInput);
        TestOutput testOutput=executor.run(postRunRequest.getInput());
        executor.clean();
        return testOutput;
    }

    @PostMapping("/run-multiple")
    public BuildOutput postRunMultiple(@RequestBody PostBuildRequest postBuildRequest) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput=new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
//        List<TestInput> testInputs=new LinkedList<>();
//        postBuildRequest.getInputs().forEach(testInput -> {
//            testInputs.add(new TestInput(testInput.getInput()));
//        });
        List<TestInput> testInputs=postBuildRequest.getInputs();
        Executor executor=this.executorFactory.createExecutor(programInput);
        List<TestOutput> testOutputs =executor.run(testInputs);
        executor.clean();

        BuildOutput buildOutput=new BuildOutput();
        buildOutput.setBuildId(executor.getBuildId());
        buildOutput.setTestOutputs(testOutputs);

        return buildOutput;
    }


    @ExceptionHandler(CompilationErrorException.class)
    public ErrorResponse handle(Exception e){
        return new ErrorResponse(Status.COMPILE_ERROR,e.getMessage());
    }
}
