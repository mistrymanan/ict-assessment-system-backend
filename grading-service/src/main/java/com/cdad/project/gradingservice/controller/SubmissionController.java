package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.TestInput;
import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.exchange.*;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.TestCase;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
public class SubmissionController {

    private final ModelMapper modelMapper;
    private final AssignmentServiceClient assignmentServiceClient;
    private final ExecutionServiceClient executionServiceClient;

    public SubmissionController(ModelMapper modelMapper, AssignmentServiceClient assignmentServiceClient, ExecutionServiceClient executionServiceClient) {
        this.modelMapper = modelMapper;
        this.assignmentServiceClient = assignmentServiceClient;
        this.executionServiceClient = executionServiceClient;
    }
    @PostMapping("run-code")
    PostRunCodeResponse postRunCode(@RequestBody PostRunCodeRequest request ){
        PostRunCodeResponse postRunCode=new PostRunCodeResponse();
        postRunCode.setInput(request.getInput());

        GetQuestionRequest getQuestionRequest=new GetQuestionRequest();
        modelMapper.map(request,getQuestionRequest);

        Question question=assignmentServiceClient.getQuestion(getQuestionRequest)
                .block();

        PostRunRequest postRunRequest=modelMapper.map(request,PostRunRequest.class);

        PostRunResponse actualResponse=this.executionServiceClient.postRunCode(postRunRequest)
                .block();
        modelMapper.map(actualResponse,postRunCode);
        if(actualResponse.getStatus().equals(Status.COMPILE_ERROR)){
            postRunCode.setOutput(actualResponse.getMessage());
            postRunCode.setStatus(Status.COMPILE_ERROR);
        }
        else if(actualResponse.getStatus().equals(Status.SUCCEED)){
            postRunRequest.setSourceCode(question.getSolutionCode());
            postRunRequest.setLanguage(Language.valueOf(question.getSolutionLanguage().toUpperCase()));
            PostRunResponse expectedResponse=this.executionServiceClient.postRunCode(postRunRequest)
                    .block();
            if(expectedResponse.getStatus().equals(Status.SUCCEED)){
                if(expectedResponse.getOutput().equals(actualResponse.getOutput())){
                    postRunCode.setStatus(Status.ACCEPTED);

                }
                postRunCode.setExpectedOutput(expectedResponse.getOutput());
            }
            else{
                postRunCode.setStatus(expectedResponse.getStatus());
            }
        }
        else{
            modelMapper.map(actualResponse,postRunCode);
        }
        //this.executionServiceClient.postRunCode()
        return postRunCode;
    }

    @PostMapping("/submit")
    PostSubmitResponse submitAssignment(@RequestBody PostSubmitRequest request) {

        PostSubmitResponse postSubmitResponse = new PostSubmitResponse();

        GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
        modelMapper.map(request, getQuestionRequest);

        Question question = assignmentServiceClient.getQuestion(getQuestionRequest)
                .block();


        PostBuildRequest postBuildRequest = modelMapper.map(request, PostBuildRequest.class);
        postBuildRequest.setInputs(question.getTestCases());

        PostBuildResponse userBuildResponse = this.executionServiceClient.postBuild(postBuildRequest).block();

        List<ResponseTestCase> responseTestCases=null;
        if (question.getTestCases() != null && userBuildResponse.getStatus().equals(Status.SUCCEED)) {
            HashMap<String, TestCase> testCaseHashMap = new HashMap<>();

            question.getTestCases().stream().forEach(testCase -> {
                testCaseHashMap.put(testCase.getId(), testCase);
            });

            userBuildResponse.getTestOutputs().stream().forEach(testCase -> {
                TestCase actualTestCase = testCaseHashMap.get(testCase.getId());
                if (testCase.getStatus().equals(Status.SUCCEED)) {
                    if (testCase.getOutput().trim().equals(actualTestCase.getOutput().trim())) {
                        testCase.setStatus(Status.PASSED);
                    } else {
                        postSubmitResponse.setStatus(Status.FAILED);
                        testCase.setStatus(Status.FAILED);
                    }
                }
            });

            responseTestCases = userBuildResponse.getTestOutputs().stream().map(testCase -> {
                return new ResponseTestCase(testCase.getId(), testCase.getStatus());
            }).collect(Collectors.toList());
            postSubmitResponse.setTestCases(responseTestCases);
            postSubmitResponse.setScore(Double.valueOf(question.getTotalPoints()));
            postSubmitResponse.setBuildId(userBuildResponse.getId());
            if(postSubmitResponse.getStatus()==null){
                postSubmitResponse.setStatus(Status.SUCCEED);
            }
            // set submission status based on time
            // postSubmitResponse.setSubmissionStatus();
            //postBuildRequest.setInputs();

        }
        else if(userBuildResponse.getStatus().equals(Status.COMPILE_ERROR)){
            postSubmitResponse.setStatus(Status.COMPILE_ERROR);
        }
        return postSubmitResponse;
    }
}
