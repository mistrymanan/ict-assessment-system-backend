package com.cdad.project.executionservice.service;

import com.cdad.project.executionservice.dto.*;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.CurrentUser;
import com.cdad.project.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.executionservice.exceptions.CompilationErrorException;
import com.cdad.project.executionservice.exchange.GetBuildsRequest;
import com.cdad.project.executionservice.exchange.GetBuildsResponse;
import com.cdad.project.executionservice.exchange.PostBuildRequest;
import com.cdad.project.executionservice.exchange.PostRunRequest;
import com.cdad.project.executionservice.executor.BaseExecutor;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.ExecutorFactory;
import com.cdad.project.executionservice.repository.BuildRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuildService {

    private final BuildRepository buildRepository;
    private final ModelMapper modelMapper;
    private final RunCodeLogService runCodeLogService;
    @Autowired
    private ExecutorFactory executorFactory;
    public BuildService(BuildRepository buildRepository, ModelMapper modelMapper, RunCodeLogService runCodeLogService) {
        this.buildRepository = buildRepository;
        this.modelMapper = modelMapper;
        this.runCodeLogService = runCodeLogService;
    }

    public BuildEntity getBuildById(String id) {
        return this.buildRepository.findById(id).get();
    }

    public void save(BuildEntity buildEntity) {
        this.buildRepository.save(buildEntity);
    }

    public List<BuildEntity> getAllBuild() {
        return this.buildRepository.findAll();
    }

    public BuildOutput postBuild(PostBuildRequest postBuildRequest, Jwt jwt)  throws IOException, InterruptedException, BuildCompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
        List<TestInput> testInputs = postBuildRequest.getInputs();
        BaseExecutor executor = (BaseExecutor) this.executorFactory.createExecutor(programInput);
        List<TestOutput> testOutputs = null;
        Instant start = Instant.now();
        Instant end=Instant.now();
        try {
            testOutputs = executor.run(testInputs);
            end = Instant.now();
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
        buildEntity.setExecutionTime(Duration.between(start,end).toMillis());
        this.save(buildEntity);
        this.runCodeLogService.save(buildEntity, CurrentUser.fromJwt(jwt));
        return buildOutput;
    }

    public TestOutput postRun(PostRunRequest postRunRequest,Jwt jwt) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postRunRequest.getSourceCode());
        programInput.setLanguage(postRunRequest.getLanguage());
        Instant start = Instant.now();
        Executor executor = this.executorFactory.createExecutor(programInput);
        TestOutput testOutput = executor.run(postRunRequest.getInput());
        Instant finish = Instant.now();
        executor.clean(); // we don't need it in synchronous manner. clean up task can be asynchronous.
        this.runCodeLogService.save(postRunRequest, Duration.between(start,finish).toMillis(),testOutput.getStatus(), CurrentUser.fromJwt(jwt));
        return testOutput;
    }

    public BuildOutput postRunMultiple(PostBuildRequest postBuildRequest,Jwt jwt) throws IOException, InterruptedException, CompilationErrorException {
        ProgramInput programInput = new ProgramInput();
        programInput.setSourceCode(postBuildRequest.getSourceCode());
        programInput.setLanguage(postBuildRequest.getLanguage());
        List<TestInput> testInputs = postBuildRequest.getInputs();
        BaseExecutor executor = (BaseExecutor) this.executorFactory.createExecutor(programInput);
        Instant start = Instant.now();
        List<TestOutput> testOutputs = executor.run(testInputs);
        Instant end = Instant.now();
        long executionTime=Duration.between(start,end).toMillis();
        BuildOutput buildOutput = new BuildOutput();
        buildOutput.setId(executor.getBuildId());
        buildOutput.setTestOutputs(testOutputs);
        buildOutput.setStatus(executor.getStatus());
        buildOutput.setExecutionTime(executionTime);
        executor.clean();
        this.runCodeLogService.save(postBuildRequest,buildOutput,CurrentUser.fromJwt(jwt));
        return buildOutput;
    }
    public GetBuildsResponse getBuilds(GetBuildsRequest request){
        Map<String, BuildCode> data=this.buildRepository.getBuildEntitiesByIdIn(request.getBuildIds())
                .stream().collect(Collectors.toMap(BuildEntity::getId,buildEntity -> {
                    return this.modelMapper.map(buildEntity,BuildCode.class);
        }));
        GetBuildsResponse getBuildsResponse=new GetBuildsResponse();
        getBuildsResponse.setBuilds(data);
        return getBuildsResponse;
    }
}
