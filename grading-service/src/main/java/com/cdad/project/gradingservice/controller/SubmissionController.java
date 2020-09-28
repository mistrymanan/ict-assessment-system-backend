package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.SubmissionResult;
import com.cdad.project.gradingservice.entity.SubmissionEntity;
import com.cdad.project.gradingservice.exchange.*;
import com.cdad.project.gradingservice.service.SubmissionService;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
public class SubmissionController {

    private final ModelMapper modelMapper;
    private final AssignmentServiceClient assignmentServiceClient;
    private final ExecutionServiceClient executionServiceClient;
    private final SubmissionService submissionService;

    public SubmissionController(ModelMapper modelMapper, AssignmentServiceClient assignmentServiceClient, ExecutionServiceClient executionServiceClient, SubmissionService submissionService) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        this.assignmentServiceClient = assignmentServiceClient;
        this.executionServiceClient = executionServiceClient;
        this.submissionService = submissionService;
    }
    @PostMapping("run-code")
    PostRunCodeResponse postRunCode(@RequestBody PostRunCodeRequest request ){
//        PostRunCodeResponse postRunCode=new PostRunCodeResponse();
//        postRunCode.setInput(request.getInput());

        GetQuestionRequest getQuestionRequest=new GetQuestionRequest();
        modelMapper.map(request,getQuestionRequest);
        Question question=assignmentServiceClient.getQuestion(getQuestionRequest)
                .block();

        PostRunRequest postRunRequest=modelMapper.map(request,PostRunRequest.class);

        PostRunResponse userResponse=this.executionServiceClient.postRunCode(postRunRequest)
                .block();
        PostRunCodeResponse postRunResponse=this.submissionService.getExpectedResult(userResponse,question,postRunRequest);
        modelMapper.map(request,postRunResponse);

        return postRunResponse;
    }

    @PostMapping("/submit")
    PostSubmitResponse submitAssignment(@RequestBody PostSubmitRequest request) throws BuildCompilationErrorException {

        PostSubmitResponse postSubmitResponse = new PostSubmitResponse();

        SubmissionEntity submissionEntity=new SubmissionEntity();
        GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
        modelMapper.map(request, getQuestionRequest);

        Assignment assignment=assignmentServiceClient.getAssignment(request.getAssignmentId()).block();
        Question question = assignmentServiceClient.getQuestion(getQuestionRequest).block();

        PostBuildRequest postBuildRequest = modelMapper.map(request, PostBuildRequest.class);
        postBuildRequest.setInputs(question.getTestCases());

        PostBuildResponse userBuildResponse = this.executionServiceClient.postBuild(postBuildRequest);

        List<TestResult> testResultResponseTestCases =null;

        SubmissionResult submissionResult=this.submissionService.evaluate(userBuildResponse,question,assignment);

        modelMapper.map(request,postSubmitResponse);
        modelMapper.map(submissionResult,postSubmitResponse);
        modelMapper.map(postSubmitResponse,submissionEntity);
        SubmissionEntity entity=this.submissionService.save(submissionEntity);
        postSubmitResponse.setSubmissionId(entity.getId());
        return postSubmitResponse;
    }
}
