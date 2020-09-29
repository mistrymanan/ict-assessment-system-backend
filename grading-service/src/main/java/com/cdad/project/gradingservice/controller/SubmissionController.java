package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.SubmissionDetails;
import com.cdad.project.gradingservice.dto.SubmissionResult;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.entity.SubmissionEntity;
import com.cdad.project.gradingservice.exception.RunCodeCompilationError;
import com.cdad.project.gradingservice.exception.SubmissionCompilationError;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.List;

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
    PostRunCodeResponse postRunCode(@RequestBody PostRunCodeRequest request ) throws RunCodeCompilationError {
        PostRunCodeResponse postRunResponse=new PostRunCodeResponse();
        postRunResponse.setInput(request.getInput());

        GetQuestionRequest getQuestionRequest=new GetQuestionRequest();
        modelMapper.map(request,getQuestionRequest);
        Question question=assignmentServiceClient.getQuestion(getQuestionRequest)
                .block();

        PostRunRequest postRunRequest=modelMapper.map(request,PostRunRequest.class);

        PostRunResponse userResponse=this.executionServiceClient.postRunCode(postRunRequest);

        if(question.isShowExpectedOutput()){
            postRunResponse=this.submissionService.getExpectedResult(userResponse,question,postRunRequest);
        modelMapper.map(request,postRunResponse);
        }
        else{
            modelMapper.map(userResponse,postRunResponse);
        }
        return postRunResponse;
    }

    @PostMapping("/submit")
    PostSubmitResponse submitAssignment(@RequestBody PostSubmitRequest request) throws SubmissionCompilationError {

        PostSubmitResponse postSubmitResponse = new PostSubmitResponse();

        SubmissionEntity submissionEntity=new SubmissionEntity();
        GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
        modelMapper.map(request, getQuestionRequest);

        Assignment assignment=assignmentServiceClient.getAssignment(request.getAssignmentId()).block();
        Question question = assignmentServiceClient.getQuestion(getQuestionRequest).block();

        PostBuildRequest postBuildRequest = modelMapper.map(request, PostBuildRequest.class);
        postBuildRequest.setInputs(question.getTestCases());


        PostBuildResponse userBuildResponse = null;
        SubmissionResult submissionResult= null;
        try {
            userBuildResponse = this.executionServiceClient.postBuild(postBuildRequest);
            List<TestResult> testResultResponseTestCases =null;
            submissionResult = this.submissionService.evaluate(userBuildResponse,question,assignment);
        } catch (BuildCompilationErrorException e) {
            SubmissionCompilationError error=new SubmissionCompilationError(e.getMessage());
            modelMapper.map(e,error);
            modelMapper.map(request,error);
            throw error;
        }
        modelMapper.map(request,postSubmitResponse);
        modelMapper.map(submissionResult,postSubmitResponse);
        modelMapper.map(postSubmitResponse,submissionEntity);
        submissionEntity.setTime(submissionResult.getTime());
        SubmissionEntity entity=this.submissionService.save(submissionEntity);

        postSubmitResponse.setSubmissionId(entity.getId().toString());
        return postSubmitResponse;
    }

    @ExceptionHandler(SubmissionCompilationError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handle(SubmissionCompilationError error){

        SubmissionEntity submissionEntity=modelMapper.map(error,SubmissionEntity.class);
        submissionEntity.setTime(LocalDateTime.now());
        submissionEntity=this.submissionService.save(submissionEntity);
        ErrorResponse errorResponse=modelMapper.map(submissionEntity,ErrorResponse.class);
        modelMapper.map(error,errorResponse);
        return errorResponse;
    }

    @GetMapping("/submissions/{assignmentId}/{questionId}")
    public List<SubmissionDetails> getSubmissions(@PathVariable String assignmentId, @PathVariable String questionId){
        System.out.println(assignmentId);
        return this.submissionService.getSubmissionDetails(assignmentId, questionId);
    }

    @ExceptionHandler(RunCodeCompilationError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handle(RunCodeCompilationError error){
        return modelMapper.map(error,ErrorResponse.class);
    }
}
