package com.cdad.project.executionservice.controller;

import com.cdad.project.executionservice.dto.Program;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.ExecutorFactory;
import com.cdad.project.executionservice.service.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public Program postBuild(@RequestBody Program program) throws IOException, InterruptedException {
        Executor executor=this.executorFactory.createExecutor(program);
        Status status=executor.run();
        program.setStatus(status);
        executor.clean();
        return program;
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
    public Program postRun(@RequestBody Program program) throws IOException, InterruptedException {
        Executor executor = this.executorFactory.createExecutor(program);
        Status status = executor.run();
        program.setStatus(status);
        executor.clean();
        return program;
    }

}
