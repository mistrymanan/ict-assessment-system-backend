package com.cdad.project.executionservice.controller;


import com.cdad.project.executionservice.dto.RunCodeLogsDTO;
import com.cdad.project.executionservice.entity.RunCodeLog;
import com.cdad.project.executionservice.service.RunCodeLogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("logs")
public class RunCodeLogsController {

    final private RunCodeLogService runCodeLogService;

    public RunCodeLogsController(RunCodeLogService runCodeLogService) {
        this.runCodeLogService = runCodeLogService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    List<RunCodeLogsDTO> getAllRunCodeLogs(){
            return runCodeLogService.getRunCodeLongs();
    }
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    RunCodeLog getRunCodeLogById(@PathVariable String id){
        return runCodeLogService.getRunCodeLog(id);
    }

}
