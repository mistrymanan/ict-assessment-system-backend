package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.exchange.PostRunCodeRequest;
import com.cdad.project.gradingservice.exchange.PostRunCodeResponse;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    @RequestMapping("run-code")
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


}
