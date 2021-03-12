package com.cdad.project.executionservice.controller;

import com.cdad.project.executionservice.dto.*;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.executionservice.exceptions.BuildNotFoundException;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;
import com.cdad.project.executionservice.exchange.PostBuildRequest;
import com.cdad.project.executionservice.exchange.PostRunRequest;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.ExecutorFactory;
import com.cdad.project.executionservice.service.BuildService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("")
public class BuildController {
    private final BuildService buildService;

    private final ModelMapper modelMapper;
    @Autowired
    private ExecutorFactory executorFactory;

    public BuildController(ModelMapper mapper,
                           BuildService buildService) {
        this.modelMapper = mapper;
        this.buildService = buildService;

    }

    @PostMapping("/builds")
    public BuildOutput postBuild(@RequestBody PostBuildRequest postBuildRequest) throws IOException, InterruptedException, BuildCompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
        List<TestInput> testInputs = postBuildRequest.getInputs();
        BaseExecutor executor = (BaseExecutor) this.executorFactory.createExecutor(programInput);
        List<TestOutput> testOutputs = null;
        try {
            testOutputs = executor.run(testInputs);
        } catch (CompilationErrorException e) {
            BuildCompilationErrorException buildCompilationErrorException = new BuildCompilationErrorException(e.getMessage());
            modelMapper.map(programInput, buildCompilationErrorException);
            modelMapper.map(executor, buildCompilationErrorException);

            throw buildCompilationErrorException;
        } finally {
            executor.clean();
        }

        BuildOutput buildOutput = new BuildOutput();
        buildOutput.setId(executor.getBuildId());
        buildOutput.setTestOutputs(testOutputs);
        buildOutput.setStatus(executor.getStatus());
        BuildEntity buildEntity = modelMapper.map(buildOutput, BuildEntity.class);
        buildEntity.setLanguage(programInput.getLanguage());
        buildEntity.setSourceCode(programInput.getSourceCode());
        buildEntity.setTimeStamp(LocalDateTime.now());
        this.buildService.save(buildEntity);


        return buildOutput;
    }

    @GetMapping("/builds/{buildId}")
    public BuildEntity postBuild(@PathVariable String buildId) throws BuildNotFoundException {
        BuildEntity buildEntity = this.buildService.getBuildById(buildId);
        if (buildEntity == null) {
            throw new BuildNotFoundException("Build:" + buildId + " Not Found");
        }
        return this.buildService.getBuildById(buildId);
    }

    @GetMapping("/builds/all")
    public List<BuildEntity> getBuilds() {
        return this.buildService.getAllBuild();
    }

    @PostMapping("/run")
    public TestOutput postRun(@RequestBody PostRunRequest postRunRequest) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postRunRequest.getSourceCode());
        programInput.setLanguage(postRunRequest.getLanguage());
        Executor executor = this.executorFactory.createExecutor(programInput);
        TestOutput testOutput = executor.run(postRunRequest.getInput());
        executor.clean(); // we don't need it in synchronous manner. clean up task can be asynchronous.
        return testOutput;
    }

    @PostMapping("/run-multiple")
    public BuildOutput postRunMultiple(@RequestBody PostBuildRequest postBuildRequest) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
        List<TestInput> testInputs = postBuildRequest.getInputs();
        BaseExecutor executor = (BaseExecutor) this.executorFactory.createExecutor(programInput);
        List<TestOutput> testOutputs = executor.run(testInputs);
        BuildOutput buildOutput = new BuildOutput();
        buildOutput.setId(executor.getBuildId());
        buildOutput.setTestOutputs(testOutputs);
        buildOutput.setStatus(executor.getStatus());
        executor.clean();

        return buildOutput;
    }

    @ExceptionHandler(CompilationErrorException.class)
    public ErrorResponse handle(CompilationErrorException e) {
        return new ErrorResponse(Status.COMPILE_ERROR, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(Exception e) {
        ErrorResponse errorResponse = modelMapper.map(e, ErrorResponse.class);
        return errorResponse;
    }

    @ExceptionHandler(BuildCompilationErrorException.class)
    public BuildErrorResponse handle(BuildCompilationErrorException e) {
        BuildEntity buildEntity = modelMapper.map(e, BuildEntity.class);
        this.buildService.save(buildEntity);

        BuildErrorResponse buildErrorResponse = new BuildErrorResponse();
        modelMapper.map(e, buildErrorResponse);
        return buildErrorResponse;
    }
}
