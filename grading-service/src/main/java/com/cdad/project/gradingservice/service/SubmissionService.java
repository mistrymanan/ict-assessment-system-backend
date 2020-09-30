package com.cdad.project.gradingservice.service;

import com.cdad.project.gradingservice.dto.SubmissionDetails;
import com.cdad.project.gradingservice.dto.SubmissionResult;
import com.cdad.project.gradingservice.entity.*;
import com.cdad.project.gradingservice.exception.RunCodeCompilationError;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
        submissionEntity.setId(UUID.randomUUID());
        return this.submissionRepository.save(submissionEntity);
    }

    public PostRunCodeResponse getExpectedResult(PostRunResponse userResponse, Question question, PostRunRequest request) throws RunCodeCompilationError {
        PostRunCodeResponse response=new PostRunCodeResponse();
        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
//            response.setOutput(userResponse.getMessage());
//            response.setStatus(Status.COMPILE_ERROR);
            throw new RunCodeCompilationError(userResponse.getMessage(),userResponse.getStatus());
        }
        else if(userResponse.getStatus().equals(Status.SUCCEED) ||userResponse.getStatus().equals(Status.RUNTIME_ERROR) || userResponse.getStatus().equals(Status.TIMEOUT) ){
                modelMapper.map(userResponse,response);
            request.setSourceCode(question.getSolutionCode());
            request.setLanguage(Language.valueOf(question.getSolutionLanguage().toUpperCase()));
            PostRunResponse expectedResponse=this.executionServiceClient.postRunCode(request);
            modelMapper.map(userResponse,response);
            if(expectedResponse.getStatus().equals(Status.SUCCEED)){
                response.setExpectedOutput(expectedResponse.getOutput());
                if(userResponse.getStatus().equals(Status.SUCCEED)&&expectedResponse.getOutput().trim().equals(userResponse.getOutput().trim())){
                    response.setStatus(Status.ACCEPTED);
                }
                else if (userResponse.getStatus().equals(Status.SUCCEED)){
                    response.setStatus(Status.WRONG_OUTPUT);
                }
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
        LocalDateTime submissionTime=LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata"));
        //submissionResult.setTime(LocalDateTime.ofInstant(Instant.now(),ZoneOffset.UTC));
        submissionResult.setTime(submissionTime);
        List<TestResult> testResultResponseTestCases =null;
        Double scoreAchieved=0.0;
        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
            throw new BuildCompilationErrorException(userResponse.getMessage(),userResponse.getId());
        }
        else if (question.getTestCases() != null && (userResponse.getStatus().equals(Status.SUCCEED) || userResponse.getStatus().equals(Status.TEST_FAILED)))
        {
            Double scoreForSingleTestCase=question.getTotalPoints().doubleValue()/question.getTestCases().size();
            HashMap<String, TestCase> testCaseHashMap = new HashMap<>();
            question.getTestCases().stream().forEach(testCase -> {
                testCaseHashMap.put(testCase.getId(), testCase);
            });

            testResultResponseTestCases = userResponse.getTestOutputs().stream().map(testCase -> {
                TestCase actualTestCase = testCaseHashMap.get(testCase.getId());
                Reason reason=null;
                ResultStatus status;
                if (testCase.getStatus().equals(Status.SUCCEED) && testCase.getOutput().trim().equals(actualTestCase.getOutput().trim())) {
                    status=ResultStatus.PASSED;
                }
                else if (testCase.getStatus().equals(Status.SUCCEED) && !testCase.getOutput().trim().equals(actualTestCase.getOutput().trim())){
                    status=ResultStatus.FAILED;
                    reason=Reason.WRONG_OUTPUT;
                }
                else if(testCase.getStatus().equals(Status.RUNTIME_ERROR)){
                    status=ResultStatus.FAILED;
                    reason=Reason.RUNTIME_ERROR;
                }
                else if(testCase.getStatus().equals(Status.TIMEOUT)){
                    status=ResultStatus.FAILED;
                    reason=Reason.TIMEOUT;
                }
                else {
                      status=ResultStatus.FAILED;

                }
                return new TestResult(testCase.getId(), status,reason);
            }).collect(Collectors.toList());
            long count=testResultResponseTestCases.stream() .filter(testResult -> testResult.getStatus().equals(ResultStatus.PASSED)).count();
            submissionResult.setTestResults(testResultResponseTestCases);
            submissionResult.setBuildId(userResponse.getId());
            scoreAchieved=scoreForSingleTestCase*count;
            submissionResult.setScore(scoreAchieved);
            boolean anyFailed=testResultResponseTestCases.stream().anyMatch(testResult -> testResult.getStatus().equals(ResultStatus.FAILED));
            if(anyFailed){
                submissionResult.setStatus(ResultStatus.FAILED);
            }
            else {
                submissionResult.setStatus(ResultStatus.PASSED);
            }

            if(assignment.isHasDeadline()){
                if(submissionTime.isAfter(assignment.getDeadline())){
                    submissionResult.setSubmissionStatus(SubmissionStatus.LATE);
                }
                else {
                    submissionResult.setSubmissionStatus((SubmissionStatus.ON_TIME));
                }
            }
            else{submissionResult.setSubmissionStatus(SubmissionStatus.SUBMITTED);}
//
//            if(assignment.isTimed()){
//
//            }
            // set submission status based on time
            // postSubmitResponse.setSubmissionStatus();
            //postBuildRequest.setInputs();
        }

        return submissionResult;
        }

        public List<SubmissionDetails> getSubmissionDetails(String assignmentId, String questionId)
        {

            this.submissionRepository
                    .findAllByAssignmentIdAndQuestionId(assignmentId, questionId)
                    .stream().forEach(System.out::println);

             List<SubmissionDetails> submissionDetails=this.submissionRepository
                     .findAllByAssignmentIdAndQuestionId(assignmentId, questionId)
                     .stream()
                     .map(submissionEntity -> modelMapper.map(submissionEntity,SubmissionDetails.class))
                     .collect(Collectors.toList());
            return submissionDetails;
        }

    }
