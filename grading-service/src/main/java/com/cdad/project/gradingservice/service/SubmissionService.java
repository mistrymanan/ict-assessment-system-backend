package com.cdad.project.gradingservice.service;

import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.dto.QuestionDTO;
import com.cdad.project.gradingservice.entity.*;
import com.cdad.project.gradingservice.exception.RunCodeCompilationErrorException;
import com.cdad.project.gradingservice.exception.SubmissionCompilationErrorException;
import com.cdad.project.gradingservice.exchange.PostRunCodeResponse;
import com.cdad.project.gradingservice.exchange.PostSubmitRequest;
import com.cdad.project.gradingservice.exchange.TestResult;
import com.cdad.project.gradingservice.repository.SubmissionRepository;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.TestCase;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.dto.TestCaseResult;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        if(submissionEntity.getId()==null){
            submissionEntity.setId(UUID.randomUUID());
        }

        return this.submissionRepository.save(submissionEntity);
    }

    public SubmissionEntity getSubmissionEntity(String assignmentId,String email){
        return this.submissionRepository.findByAssignmentIdAndEmail(assignmentId, email);
    }
    public boolean isExist(String assignmentId,String email){
        return this.submissionRepository.existsByAssignmentIdAndEmail(assignmentId,email);
    }

    public SubmissionEntity save(SubmissionEntity submissionEntity,QuestionEntity questionEntity,Assignment assignment){
        int questionCount=assignment.getQuestions().size();
        if(submissionEntity.getQuestionEntities()!=null){
        boolean exist=submissionEntity.getQuestionEntities().stream().anyMatch(questionEntity1 -> {
            return questionEntity1.getQuestionId().equals(questionEntity.getQuestionId());
        });
            if(exist) {
                submissionEntity.getQuestionEntities().forEach(questionEntity1 -> {
                    if(questionEntity1.getQuestionId().equals(questionEntity.getQuestionId())){
                        modelMapper.map(questionEntity,questionEntity1);
                    }
                });
            }
            else{
                submissionEntity.getQuestionEntities().add(questionEntity);
            }
        }
        else{
            List<QuestionEntity> questionEntities=new ArrayList<>();
            questionEntities.add(questionEntity);
            submissionEntity.setQuestionEntities(questionEntities);
        }
       submissionEntity.setSubmissionStatus(getSubmissionStatus(submissionEntity,assignment));
       submissionEntity.setCurrentScore(calculateSubmissionScore(submissionEntity));
        return this.save(submissionEntity);
    }

    public SubmissionStatus getSubmissionStatus(SubmissionEntity submissionEntity,Assignment assignment){
            SubmissionStatus status=SubmissionStatus.IN_PROGRESS;
        if(assignment.getQuestions().size()==submissionEntity.getQuestionEntities().size()){
            boolean allPassed=submissionEntity.getQuestionEntities().stream()
                    .allMatch(questionEntity1 ->
                            questionEntity1.getResultStatus().equals(ResultStatus.PASSED));
            if(allPassed) {
                submissionEntity.setCompletedOn(LocalDateTime.now());
                if(assignment.isTimed() && assignment.isHasDeadline()){
                    if(submissionEntity.getCompletedOn().isAfter(assignment.getDeadline())){
                        status=SubmissionStatus.LATE_SUBMITTED;
                    }
                    else{
                        long actualDuration=ChronoUnit.MINUTES.between(submissionEntity.getStartOn(),submissionEntity.getCompletedOn());
                        if(Double.valueOf(assignment.getDuration())>=actualDuration){
                            status=SubmissionStatus.COMPLETED;
                        }
                        else{
                            status=SubmissionStatus.LATE_SUBMITTED;
                        }
                    }
                }
                else{
                    if(assignment.isTimed()){
                        long actualDuration=ChronoUnit.MINUTES.between(submissionEntity.getStartOn(),submissionEntity.getCompletedOn());
                        if(Double.valueOf(assignment.getDuration())>=actualDuration){
                            status=SubmissionStatus.COMPLETED;
                        }
                        else{
                            status=SubmissionStatus.LATE_SUBMITTED;
                        }
                    }
                    else if(assignment.isHasDeadline()){
                        if(submissionEntity.getCompletedOn().isBefore(assignment.getDeadline())){
                            status=SubmissionStatus.COMPLETED;
                        }
                        else{
                            status=SubmissionStatus.LATE_SUBMITTED;
                        }
                    }
                }
            }
            else{ status=SubmissionStatus.IN_PROGRESS; }
        }
        submissionEntity.setCurrentScore(calculateSubmissionScore(submissionEntity));
        return status;
    }

    public PostRunCodeResponse getExpectedResult(PostRunResponse userResponse, Question question, PostRunRequest request) throws RunCodeCompilationErrorException {
        PostRunCodeResponse response=new PostRunCodeResponse();
        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
            throw new RunCodeCompilationErrorException(userResponse.getMessage(),userResponse.getStatus());
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
    public QuestionDTO evaluate(PostSubmitRequest request, Question question, Assignment assignment) throws SubmissionCompilationErrorException {
        QuestionDTO questionDTO = null;

        PostBuildRequest postBuildRequest = modelMapper.map(request, PostBuildRequest.class);
        postBuildRequest.setInputs(question.getTestCases());

        PostBuildResponse userBuildResponse = null;
        try {
            userBuildResponse = this.executionServiceClient.postBuild(postBuildRequest);
            List<TestResult> testResultResponseTestCases =null;
            questionDTO = assess(userBuildResponse,question,assignment);
            modelMapper.map(request,questionDTO);
        } catch (BuildCompilationErrorException e) {

            SubmissionCompilationErrorException error=new SubmissionCompilationErrorException(e.getMessage());
            modelMapper.map(e,error);
            modelMapper.map(request,error);
            throw error;
        }

        return questionDTO;
    }
    public QuestionDTO assess(PostBuildResponse userResponse, Question question, Assignment assignment) throws BuildCompilationErrorException {
        QuestionDTO questionDTO =modelMapper.map(question,QuestionDTO.class);
        LocalDateTime submissionTime=LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata"));
        questionDTO.setTime(submissionTime);
        questionDTO.setBuildId(userResponse.getId());
        if(userResponse.getStatus().equals(Status.COMPILE_ERROR)){
            System.out.println(question.getId());
            throw new BuildCompilationErrorException(userResponse.getMessage(),userResponse.getId(),question.getId());
        }
        else if (question.getTestCases() != null && (userResponse.getStatus().equals(Status.SUCCEED) || userResponse.getStatus().equals(Status.TEST_FAILED)))
        {
            List<TestResult> testResults =checkTestCases(userResponse,question.getTestCases());
            Double scoreAchieved= calculatedScore(testResults,question);
            questionDTO.setTestResults(testResults);
            questionDTO.setScore(scoreAchieved);
            setQuestionStatus(testResults,questionDTO);
        }
        return questionDTO;
        }

        public Double calculateSubmissionScore(SubmissionEntity submissionEntity){
        return submissionEntity.getQuestionEntities().stream().mapToDouble(QuestionEntity::getScore).sum();
        }
        public Double calculatedScore(List<TestResult> testResults, Question question){
            double scoreForSingleTestCase=question.getTotalPoints().doubleValue()/question.getTestCases().size();
            long count=testResults.stream()
                    .filter(
                            testResult ->
                                    testResult.getStatus().equals(ResultStatus.PASSED))
                    .count();
        return scoreForSingleTestCase*count;
        }

        public void setQuestionStatus(List<TestResult> testResults,QuestionDTO questionDTO){
            boolean anyFailed=isTestResultFailed(testResults);
            if(anyFailed){
                questionDTO.setResultStatus(ResultStatus.FAILED);
                questionDTO.setQuestionStatus(QuestionStatus.IN_PROGRESS);
            }
            else {
                questionDTO.setResultStatus(ResultStatus.PASSED);
                questionDTO.setQuestionStatus(QuestionStatus.COMPLETED);
            }
        }

        public boolean isTestResultFailed(List<TestResult> testResults ){
        return testResults.stream()
                .anyMatch(testResult -> testResult.getStatus().equals(ResultStatus.FAILED)); }

        public List<SubmissionDetailsDTO> getSubmissionDetails(String assignmentId)
        {

             List<SubmissionDetailsDTO> submissionDetailDTOS =this.submissionRepository
                     .findAllByAssignmentId(assignmentId)
                     .stream()
                     .map(submissionEntity -> modelMapper.map(submissionEntity, SubmissionDetailsDTO.class))
                     .collect(Collectors.toList());
            return submissionDetailDTOS;
        }

        public List<TestResult> checkTestCases(PostBuildResponse userResponse,List<TestCase> expectedTestCase){
            HashMap<String, TestCase> testCaseHashMap = new HashMap<>();
            expectedTestCase.stream().forEach(testCase -> {
                testCaseHashMap.put(testCase.getId(), testCase);
            });

           return userResponse.getTestOutputs().stream().map(testCaseResult -> {
                TestCase actualTestCase = testCaseHashMap.get(testCaseResult.getId());

                Reason reason=getReason(testCaseResult,actualTestCase);
                ResultStatus status=getResultStatus(testCaseResult,actualTestCase);

                return new TestResult(testCaseResult.getId(), status,reason);
            }).collect(Collectors.toList());
        }
        public Reason getReason(TestCaseResult testCaseResult, TestCase actualTestCase)
        {
            Reason reason=null;
            if (testCaseResult.getStatus().equals(Status.SUCCEED) && !testCaseResult.getOutput().trim().equals(actualTestCase.getOutput().trim())){

                reason=Reason.WRONG_OUTPUT;
            }
            else if(testCaseResult.getStatus().equals(Status.RUNTIME_ERROR)){
                reason=Reason.RUNTIME_ERROR;
            }
            else if(testCaseResult.getStatus().equals(Status.TIMEOUT)){

                reason=Reason.TIMEOUT;
            }
            return reason;
        }

        public ResultStatus getResultStatus(TestCaseResult testCaseResult, TestCase actualTestCase){
        ResultStatus resultStatus=ResultStatus.FAILED;
            if (testCaseResult.getStatus().equals(Status.SUCCEED) && testCaseResult.getOutput().trim().equals(actualTestCase.getOutput().trim())) {
                resultStatus=ResultStatus.PASSED;
            }
            return resultStatus;
        }
    }
