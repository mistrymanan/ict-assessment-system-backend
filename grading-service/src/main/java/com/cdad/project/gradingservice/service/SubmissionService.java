package com.cdad.project.gradingservice.service;

import com.cdad.project.gradingservice.dto.SubmissionResult;
import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.entity.SubmissionEntity;
import com.cdad.project.gradingservice.entity.SubmissionStatus;
import com.cdad.project.gradingservice.exchange.PostRunCodeResponse;
import com.cdad.project.gradingservice.exchange.TestResult;
import com.cdad.project.gradingservice.repository.SubmissionRepository;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.TestCase;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    final private ModelMapper modelMapper;
    final private AssignmentServiceClient assignmentServiceClient;
    final private SubmissionRepository submissionRepository;
    final private ExecutionServiceClient executionServiceClient;

    public SubmissionService(ModelMapper modelMapper, AssignmentServiceClient assignmentServiceClient, SubmissionRepository submissionRepository, ExecutionServiceClient executionServiceClient) {
        this.modelMapper = modelMapper;
        this.assignmentServiceClient = assignmentServiceClient;
        this.submissionRepository = submissionRepository;
        this.executionServiceClient = executionServiceClient;
    }

    public SubmissionEntity save(SubmissionEntity submissionEntity){
        return this.submissionRepository.save(submissionEntity);
    }

    public PostRunCodeResponse getExpectedResult(PostRunResponse userResponse, Question question, PostRunRequest request){
        PostRunCodeResponse response=new PostRunCodeResponse();
        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
            response.setOutput(userResponse.getMessage());
            response.setStatus(Status.COMPILE_ERROR);
        }
        else if(userResponse.getStatus().equals(Status.SUCCEED)){
                modelMapper.map(userResponse,response);
            request.setSourceCode(question.getSolutionCode());
            request.setLanguage(Language.valueOf(question.getSolutionLanguage().toUpperCase()));
            PostRunResponse expectedResponse=this.executionServiceClient.postRunCode(request)
                    .block();
            if(expectedResponse.getStatus().equals(Status.SUCCEED)){
                response.setExpectedOutput(expectedResponse.getOutput());
                if(expectedResponse.getOutput().trim().equals(userResponse.getOutput().trim())){
                    response.setStatus(Status.ACCEPTED);
                }
                else{
                    response.setStatus(Status.WRONG_OUTPUT);
                }
            }
            else{
                response.setStatus(Status.UNEXPECTED_ERROR);
            }

        }
        else if(userResponse.getStatus().equals(Status.RUNTIME_ERROR) || userResponse.getStatus().equals(Status.TIMEOUT)){
            request.setSourceCode(question.getSolutionCode());
            request.setLanguage(Language.valueOf(question.getSolutionLanguage().toUpperCase()));
            PostRunResponse expectedResponse=this.executionServiceClient.postRunCode(request)
                    .block();
            modelMapper.map(userResponse,response);
            if(expectedResponse.getStatus().equals(Status.SUCCEED)){
                response.setExpectedOutput(expectedResponse.getOutput());
            }
            else{
                response.setStatus(Status.UNEXPECTED_ERROR);
            }
        }
        response.setOutput(userResponse.getOutput());
        return response;
    }

    public SubmissionResult evaluate(PostBuildResponse userResponse, Question question, Assignment assignment) throws BuildCompilationErrorException {

        SubmissionResult submissionResult=new SubmissionResult();
        submissionResult.setTimeStamp(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        List<TestResult> testResultResponseTestCases =null;

        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
            throw new BuildCompilationErrorException(userResponse.getMessage(),userResponse.getId());
        }
        else if (question.getTestCases() != null && userResponse.getStatus().equals(Status.SUCCEED))
        {
            HashMap<String, TestCase> testCaseHashMap = new HashMap<>();
            question.getTestCases().stream().forEach(testCase -> {
                testCaseHashMap.put(testCase.getId(), testCase);
            });

            testResultResponseTestCases = userResponse.getTestOutputs().stream().map(testCase -> {
                TestCase actualTestCase = testCaseHashMap.get(testCase.getId());
                if (testCase.getStatus().equals(Status.SUCCEED) && testCase.getOutput().trim().equals(actualTestCase.getOutput().trim())) {
                    testCase.setStatus(Status.PASSED);
                } else {
                    submissionResult.setStatus(Status.FAILED);
                    testCase.setStatus(Status.FAILED);
                }

                return new TestResult(testCase.getId(), testCase.getStatus());
            }).collect(Collectors.toList());

            submissionResult.setTestCases(testResultResponseTestCases);
            submissionResult.setScore(Double.valueOf(question.getTotalPoints()));
            submissionResult.setBuildId(userResponse.getId());

            if(submissionResult.getStatus()==null){
                submissionResult.setStatus(Status.SUCCEED);
            }

            if(assignment.isHasDeadline()){
                if(submissionResult.getTimeStamp().isAfter(assignment.getDeadline())){
                    submissionResult.setSubmissionStatus(SubmissionStatus.LATE);
                }
                else {
                    submissionResult.setSubmissionStatus((SubmissionStatus.ON_TIME));
                }
            }
//
//            if(assignment.isTimed()){
//
//            }
            // set submission status based on time
            // postSubmitResponse.setSubmissionStatus();
            //postBuildRequest.setInputs();
        }
        else if (userResponse.getStatus().equals(Status.RUNTIME_ERROR)) {
            modelMapper.map(userResponse,submissionResult);
        }
        return submissionResult;
        }

    }
