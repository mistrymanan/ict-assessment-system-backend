package com.cdad.project.gradingservice.service;

import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.repository.BuildRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildService {

    private final BuildRepository buildRepository;

    public BuildService(BuildRepository buildRepository) {
        this.buildRepository = buildRepository;
    }
    public BuildEntity getBuildById(String id){
        return this.buildRepository.findById(id).get();
    }
    public void save(BuildEntity buildEntity) { this.buildRepository.save(buildEntity); }
    public List<BuildEntity> getAllBuild(){ return  this.buildRepository.findAll();}
}
