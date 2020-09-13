package com.cdad.project.executionservice.controller;

import com.cdad.project.executionservice.dto.Build;
import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.Program;
import com.cdad.project.executionservice.entity.Status;
import com.cdad.project.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.executionservice.executor.Executor;
import com.cdad.project.executionservice.executor.ExecutorFactory;
import com.cdad.project.executionservice.service.BuildService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("")
public class BuildController {
    private final BuildService buildService;
    @Autowired
    private ExecutorFactory executorFactory;
    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping("/build")
    public BuildEntity postBuild(@RequestBody Program program) throws IOException, InterruptedException {
//        PostBuildResponse response=new PostBuildResponse();
        Executor executor=this.executorFactory.createExecutor(program);
        Status status=executor.run();
        System.out.println("Execution Status"+status);
        BuildEntity build=new BuildEntity();
        build.setStatus(status);
        build.setOutput(executor.getOutput());
        build.setId(executor.getBuildId());
        this.buildService.save(build);
        executor.clean();
//        response.setBuild(new Build(build));
        return build;
    }


    @GetMapping("/build/{buildId}")
    public BuildEntity postBuild(@PathVariable String buildId) throws IOException, InterruptedException {
        return this.buildService.getBuildById(buildId);
    }



    @PostMapping("/run")
    public BuildEntity postRun(@RequestBody Program program) throws IOException, InterruptedException {
        Executor executor=this.executorFactory.createExecutor(program);
        Status status=executor.run();
        System.out.println("Execution Status"+status);
        BuildEntity build=new BuildEntity();
        build.setStatus(status);
        build.setOutput(executor.getOutput());
        build.setId(executor.getBuildId());
        executor.clean();
        return build;
    }

}
