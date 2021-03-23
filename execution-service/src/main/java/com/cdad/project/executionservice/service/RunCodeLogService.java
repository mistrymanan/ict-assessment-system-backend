package com.cdad.project.executionservice.service;

import com.cdad.project.executionservice.dto.BuildOutput;
import com.cdad.project.executionservice.dto.RunCodeLogsDTO;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.CurrentUser;
import com.cdad.project.executionservice.entity.RunCodeLog;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exchange.PostBuildRequest;
import com.cdad.project.executionservice.exchange.PostRunRequest;
import com.cdad.project.executionservice.repository.BuildRepository;
import com.cdad.project.executionservice.repository.RunCodeLogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RunCodeLogService {
    private final RunCodeLogRepository runCodeLogRepository;
    private final ModelMapper modelMapper;
    public RunCodeLogService(BuildRepository buildRepository, RunCodeLogRepository runCodeLogRepository, ModelMapper modelMapper) {
        this.runCodeLogRepository = runCodeLogRepository;
        this.modelMapper = modelMapper;
    }
    public List<RunCodeLogsDTO> getRunCodeLongs(){
        return this.runCodeLogRepository.findAll().stream()
                .map(runCodeLog -> modelMapper.map(runCodeLog, RunCodeLogsDTO.class))
                .collect(Collectors.toList());
    }

    public RunCodeLog getRunCodeLog(String id){
        return this.runCodeLogRepository.findById(id).orElse(null);
    }

    public void save(BuildEntity buildEntity,CurrentUser currentUser){
        RunCodeLog runCodeLog=this.modelMapper.map(buildEntity,RunCodeLog.class);
        this.save(runCodeLog,currentUser);
    }
    public void save(PostRunRequest request, long executionTime, Status status, CurrentUser currentUser){
        RunCodeLog runCodeLog=this.modelMapper.map(request,RunCodeLog.class);
        runCodeLog.setExecutionTime(executionTime);
        runCodeLog.setStatus(status);
        this.save(runCodeLog,currentUser);
    }
    public void save(PostBuildRequest postBuildRequest, BuildOutput buildOutput, CurrentUser currentUser){
        RunCodeLog runCodeLog=this.modelMapper.map(postBuildRequest,RunCodeLog.class);
        runCodeLog.setStatus(buildOutput.getStatus());
        runCodeLog.setExecutionTime(buildOutput.getExecutionTime());
        this.save(runCodeLog,currentUser);
    }

    public void save(RunCodeLog runCodeLog,CurrentUser currentUser){
        runCodeLog.setEmail(currentUser.getEmail());
        runCodeLog.setName(currentUser.getName());
        runCodeLog.setPicture(currentUser.getPicture());
        runCodeLog.setTimeStamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata")));
        this.runCodeLogRepository.save(runCodeLog);
    }

}
