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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public BuildOutput postBuild(@RequestBody PostBuildRequest postBuildRequest, @AuthenticationPrincipal Jwt jwt) throws IOException, InterruptedException, BuildCompilationErrorException {
        return this.buildService.postBuild(postBuildRequest,jwt);
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
    public TestOutput postRun(@RequestBody PostRunRequest postRunRequest,@AuthenticationPrincipal Jwt jwt) throws IOException, InterruptedException, CompilationErrorException {
        return this.buildService.postRun(postRunRequest,jwt);
    }

    @PostMapping("/run-multiple")
    public BuildOutput postRunMultiple(@RequestBody PostBuildRequest postBuildRequest,@AuthenticationPrincipal Jwt jwt) throws IOException, InterruptedException, CompilationErrorException {
        return this.buildService.postRunMultiple(postBuildRequest,jwt);
    }

    @ExceptionHandler(CompilationErrorException.class)
    public ErrorResponse handle(CompilationErrorException e) {
        return new ErrorResponse(Status.COMPILE_ERROR, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(Exception e) {
        return modelMapper.map(e, ErrorResponse.class);
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
