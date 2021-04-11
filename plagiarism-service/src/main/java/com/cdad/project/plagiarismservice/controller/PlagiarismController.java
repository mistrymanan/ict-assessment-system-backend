package com.cdad.project.plagiarismservice.controller;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.config.RabbitMQConfig;
import com.cdad.project.plagiarismservice.dto.PlagiarismDTO;
import com.cdad.project.plagiarismservice.dto.PlagiarismMessageDTO;
import com.cdad.project.plagiarismservice.dto.PlagiarismResultDTO;
import com.cdad.project.plagiarismservice.entity.GraphData;
import com.cdad.project.plagiarismservice.entity.Plagiarism;
import com.cdad.project.plagiarismservice.entity.Result;
import com.cdad.project.plagiarismservice.entity.Status;
import com.cdad.project.plagiarismservice.exchange.CreatePlagiarismReport;
import com.cdad.project.plagiarismservice.service.PlagiarismService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("")
public class PlagiarismController {
    final private PlagiarismService plagiarismService;
    final private RabbitTemplate rabbitTemplate;
    final private ModelMapper modelMapper;
    public PlagiarismController(PlagiarismService plagiarismService, RabbitTemplate rabbitTemplate, ModelMapper modelMapper) {
        this.plagiarismService = plagiarismService;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }
//    @GetMapping("{questionId}")
    @GetMapping("{plagiarismId}")
    public PlagiarismResultDTO getPlagiarism(@PathVariable String plagiarismId){
        return this.plagiarismService.getPlagiarismResultDTOById(plagiarismId);
    }
    @GetMapping("{plagiarismId}/results/{language}")
    public GraphData getPlagiarismResultOfLanguage(@PathVariable String plagiarismId, @PathVariable Language language){
        return this.plagiarismService.getPlagiarismResultByLanguage(plagiarismId,language);
    }

    @GetMapping("{classroomSlug}/{assignmentId}/{questionId}")
    public List<PlagiarismDTO> checkPlagiarism(@PathVariable String classroomSlug, @PathVariable String assignmentId
            , @PathVariable String questionId
            , @AuthenticationPrincipal Jwt jwt){
        return plagiarismService.getPlagiarisms(classroomSlug, assignmentId, questionId);
    }


    @PostMapping("")
    public Plagiarism publishMessage(@RequestBody CreatePlagiarismReport request, @AuthenticationPrincipal Jwt jwt){
        return this.plagiarismService.requestPlagiarismReportGeneration(request.getClassroomSlug()
                ,request.getAssignmentId()
                ,request.getQuestionId(),jwt);
    }
}
