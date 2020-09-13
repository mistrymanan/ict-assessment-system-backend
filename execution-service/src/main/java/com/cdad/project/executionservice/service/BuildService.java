package com.cdad.project.executionservice.service;

import com.cdad.project.executionservice.dto.Build;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.repository.BuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
