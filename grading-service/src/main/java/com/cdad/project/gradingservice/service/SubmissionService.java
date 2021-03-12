package com.cdad.project.gradingservice.service;

import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.dto.QuestionDTO;
import com.cdad.project.gradingservice.entity.*;
import com.cdad.project.gradingservice.exception.*;
import com.cdad.project.gradingservice.exchange.*;
import com.cdad.project.gradingservice.repository.SubmissionRepository;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.QuestionDetails;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.TestCase;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.dto.TestCaseResult;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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

    public SubmissionEntity save(SubmissionEntity submissionEntity) {
        if (submissionEntity.getId() == null) {
            submissionEntity.setId(UUID.randomUUID());
        }

        return this.submissionRepository.save(submissionEntity);
    }

    public SubmissionEntity getSubmissionEntity(String classroomSlug, String assignmentId, String email) throws SubmissionEntityNotFoundException {
        SubmissionEntity submissionEntity = this.submissionRepository.findByClassroomSlugAndAssignmentIdAndEmail(classroomSlug, assignmentId, email);
        if (submissionEntity != null) {
            return submissionEntity;
        } else {
            throw new SubmissionEntityNotFoundException("Check AssignmentId");
        }
    }

    public boolean isExist(String classroomSlug, String assignmentId, String email) {
        return this.submissionRepository.existsByClassroomSlugAndAssignmentIdAndEmail(classroomSlug, assignmentId, email);
    }

    public SubmissionEntity save(SubmissionEntity submissionEntity, QuestionEntity questionEntity, Assignment assignment) {
        int questionCount = assignment.getQuestions().size();
        if (submissionEntity.getQuestionEntities() != null) {
            boolean exist = submissionEntity.getQuestionEntities().stream().anyMatch(questionEntity1 -> {
                return questionEntity1.getQuestionId().equals(questionEntity.getQuestionId());
            });
            if (exist) {
                submissionEntity.getQuestionEntities().forEach(questionEntity1 -> {
                    if (questionEntity1.getQuestionId().equals(questionEntity.getQuestionId())) {
                        modelMapper.map(questionEntity, questionEntity1);
                    }
                });
            } else {
                submissionEntity.getQuestionEntities().add(questionEntity);
            }
        } else {
            List<QuestionEntity> questionEntities = new ArrayList<>();
            questionEntities.add(questionEntity);
            submissionEntity.setQuestionEntities(questionEntities);
        }
        submissionEntity.setSubmissionStatus(getSubmissionStatus(submissionEntity, assignment));
        submissionEntity.setCurrentScore(calculateSubmissionScore(submissionEntity));
        return this.save(submissionEntity);
    }

    public PostRunCodeResponse runCode(PostRunCodeRequest request, String classroomSlug, Jwt jwt) throws RunCodeCompilationErrorException {
        PostRunCodeResponse postRunResponse = new PostRunCodeResponse();
        postRunResponse.setInput(request.getInput());

        GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
        modelMapper.map(request, getQuestionRequest);
        Question question = assignmentServiceClient.getQuestion(getQuestionRequest, classroomSlug, jwt.getTokenValue())
                .block();

        PostRunRequest postRunRequest = modelMapper.map(request, PostRunRequest.class);

        PostRunResponse userResponse = this.executionServiceClient.postRunCode(postRunRequest, jwt.getTokenValue());

        if (question.isShowExpectedOutput()) {
            postRunResponse = this.getExpectedResult(userResponse, question, postRunRequest, jwt.getTokenValue());
            modelMapper.map(request, postRunResponse);
        } else {
            modelMapper.map(userResponse, postRunResponse);
        }
        return postRunResponse;
    }

    public PostSubmitResponse submit(PostSubmitRequest request, String classroomSlug, Jwt jwt) throws AssignmentNotStartedException, SubmissionCompilationErrorException, AssignmentNotActiveException, SubmissionEntityNotFoundException {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        if (this.isExist(classroomSlug, request.getAssignmentId(), currentUser.getEmail())) {
            PostSubmitResponse postSubmitResponse = new PostSubmitResponse();
            modelMapper.map(request, postSubmitResponse);
            SubmissionEntity submissionEntity = this.getSubmissionEntity(classroomSlug, request.getAssignmentId()
                    , currentUser.getEmail());
            postSubmitResponse.setSubmissionId(submissionEntity.getId().toString());

            GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
            modelMapper.map(request, getQuestionRequest);
            Assignment assignment = assignmentServiceClient.getAssignment(request.getAssignmentId(), classroomSlug, jwt.getTokenValue()).block();
            System.out.println(assignment);
            Question question = assignmentServiceClient.getQuestion(getQuestionRequest, classroomSlug, jwt.getTokenValue()).block();
            System.out.println(question);
            if (assignment.getStatus().equals("ACTIVE")) {
                QuestionDTO questionDTO = null;
                try {
                    questionDTO = this.evaluate(request, question, jwt.getTokenValue());
                    postSubmitResponse.setBuildId(questionDTO.getBuildId());
                    modelMapper.map(questionDTO, postSubmitResponse);
                    System.out.println(questionDTO);
                    postSubmitResponse.setStatus(questionDTO.getResultStatus());
                } catch (SubmissionCompilationErrorException error) {
                    error.setSubmissionId(submissionEntity.getId().toString());
                    questionDTO = modelMapper.map(error, QuestionDTO.class);
                    modelMapper.map(question, questionDTO);
                    questionDTO.setScore(0.0);
                    questionDTO.setTime(LocalDateTime.now());
                    throw error;
                } finally {
                    QuestionEntity questionEntity = this.modelMapper.map(questionDTO, QuestionEntity.class);
                    questionDTO.setTitle(question.getTitle());
                    submissionEntity = this.save(submissionEntity, questionEntity, assignment);
                    modelMapper.map(submissionEntity, postSubmitResponse);
                    postSubmitResponse.setStatus(questionEntity.getResultStatus());
                }

                return postSubmitResponse;
            }
//        else{
//            throw new LanguageNotAllowedException(request.getLanguage()+" Not Allowed for "+question.getTitle()+"'s Submission.");
//        }
            else {
                throw new AssignmentNotActiveException("Assignment:" + assignment.getTitle() + " is Not Active!");
            }
        } else {
            throw new AssignmentNotStartedException("You Haven't Started Assignment. Please start the Assignment First");
        }
    }

    public void startSubmission(String classroomSlug, String assignmentId, Jwt jwt) throws AssignmentNotFound, AssignmentNotStartedException {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata"));
        if (!this.isExist(classroomSlug, assignmentId, currentUser.getEmail())) {
            // SubmissionEntity submissionEntity=modelMapper.map(request,SubmissionEntity.class);

            Assignment assignment = null;
            assignment = this.assignmentServiceClient.getAssignment(assignmentId, classroomSlug, jwt.getTokenValue())
                    .block();
            if (assignment != null && ((assignment.isHasStartTime() && time.isAfter(assignment.getStartTime())) ||
                    (!assignment.isHasStartTime()))) {
                SubmissionEntity submissionEntity = new SubmissionEntity();
                submissionEntity.setAssignmentId(assignmentId);
                submissionEntity.setEmail(currentUser.getEmail());
                submissionEntity.setClassroomSlug(classroomSlug);
                System.out.println(assignment);
                if (assignment.getQuestions() != null) {
                    submissionEntity.setAssignmentScore(assignment.getQuestions().stream().mapToDouble(QuestionDetails::getTotalPoints).sum());
                } else {
                    submissionEntity.setAssignmentScore(0.0);
                }
                submissionEntity.setSubmissionStatus(SubmissionStatus.IN_PROGRESS);
                submissionEntity.setStartOn(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata")));
                this.save(submissionEntity);
            } else {
                throw new AssignmentNotStartedException("Assignment has not started yet.Please check the start time.");
            }
        }
    }

    public SubmissionStatus getSubmissionStatus(SubmissionEntity submissionEntity, Assignment assignment) {
        SubmissionStatus status = SubmissionStatus.IN_PROGRESS;
        if (assignment.getQuestions().size() == submissionEntity.getQuestionEntities().size()) {
            boolean allPassed = submissionEntity.getQuestionEntities().stream()
                    .allMatch(questionEntity1 ->
                            questionEntity1.getResultStatus().equals(ResultStatus.PASSED));
            if (allPassed) {
                submissionEntity.setCompletedOn(LocalDateTime.now());
                if (assignment.isTimed() && assignment.isHasDeadline()) {
                    if (submissionEntity.getCompletedOn().isAfter(assignment.getDeadline())) {
                        status = SubmissionStatus.LATE_SUBMITTED;
                    } else {
                        long actualDuration = ChronoUnit.MINUTES.between(submissionEntity.getStartOn(), submissionEntity.getCompletedOn());
                        if (Double.valueOf(assignment.getDuration()) >= actualDuration) {
                            status = SubmissionStatus.COMPLETED;
                        } else {
                            status = SubmissionStatus.LATE_SUBMITTED;
                        }
                    }
                } else {
                    if (assignment.isTimed()) {
                        long actualDuration = ChronoUnit.MINUTES.between(submissionEntity.getStartOn(), submissionEntity.getCompletedOn());
                        if (Double.valueOf(assignment.getDuration()) >= actualDuration) {
                            status = SubmissionStatus.COMPLETED;
                        } else {
                            status = SubmissionStatus.LATE_SUBMITTED;
                        }
                    } else if (assignment.isHasDeadline()) {
                        if (submissionEntity.getCompletedOn().isBefore(assignment.getDeadline())) {
                            status = SubmissionStatus.COMPLETED;
                        } else {
                            status = SubmissionStatus.LATE_SUBMITTED;
                        }
                    } else {
                        status = SubmissionStatus.COMPLETED;
                    }
                }
            } else {
                status = SubmissionStatus.IN_PROGRESS;
            }
        }
        submissionEntity.setCurrentScore(calculateSubmissionScore(submissionEntity));
        return status;
    }

    public PostRunCodeResponse getExpectedResult(PostRunResponse userResponse, Question question, PostRunRequest request, String token) throws RunCodeCompilationErrorException {
        PostRunCodeResponse response = new PostRunCodeResponse();
        if (userResponse.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new RunCodeCompilationErrorException(userResponse.getMessage(), userResponse.getStatus());
        } else if (userResponse.getStatus().equals(Status.SUCCEED) || userResponse.getStatus().equals(Status.RUNTIME_ERROR) || userResponse.getStatus().equals(Status.TIMEOUT)) {
            modelMapper.map(userResponse, response);
            request.setSourceCode(question.getSolutionCode());
            request.setLanguage(Language.valueOf(question.getSolutionLanguage().toUpperCase()));
            PostRunResponse expectedResponse = this.executionServiceClient.postRunCode(request, token);
            modelMapper.map(userResponse, response);
            if (expectedResponse.getStatus().equals(Status.SUCCEED)) {
                response.setExpectedOutput(expectedResponse.getOutput());
                if (userResponse.getStatus().equals(Status.SUCCEED) && expectedResponse.getOutput().trim().equals(userResponse.getOutput().trim())) {
                    response.setStatus(Status.ACCEPTED);
                } else if (userResponse.getStatus().equals(Status.SUCCEED)) {
                    response.setStatus(Status.WRONG_OUTPUT);
                }
            } else {
                response.setStatus(Status.UNEXPECTED_ERROR);
            }
        }

        response.setOutput(userResponse.getOutput());
        return response;
    }

    public QuestionDTO evaluate(PostSubmitRequest request, Question question, String token) throws SubmissionCompilationErrorException {
        QuestionDTO questionDTO = null;
        PostBuildRequest postBuildRequest = modelMapper.map(request, PostBuildRequest.class);
        postBuildRequest.setInputs(question.getTestCases());

        PostBuildResponse userBuildResponse = null;
        try {
            userBuildResponse = this.executionServiceClient.postBuild(postBuildRequest, token);
            questionDTO = assess(userBuildResponse, question);
            modelMapper.map(request, questionDTO);
        } catch (BuildCompilationErrorException e) {
            SubmissionCompilationErrorException error = new SubmissionCompilationErrorException(e.getMessage());
            modelMapper.map(e, error);
            modelMapper.map(request, error);
            throw error;
        }

        return questionDTO;
    }

    public QuestionDTO assess(PostBuildResponse userResponse, Question question) throws BuildCompilationErrorException {
        QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
        LocalDateTime submissionTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata"));
        questionDTO.setTime(submissionTime);
        questionDTO.setBuildId(userResponse.getId());
        if (userResponse.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new BuildCompilationErrorException(userResponse.getMessage(), userResponse.getId(), question.getId());
        } else if (question.getTestCases() != null && (userResponse.getStatus().equals(Status.SUCCEED) || userResponse.getStatus().equals(Status.TEST_FAILED))) {
            List<TestResult> testResults = checkTestCases(userResponse, question.getTestCases());
            Double scoreAchieved = calculatedScore(testResults, question);
            questionDTO.setTestResults(testResults);
            questionDTO.setScore(scoreAchieved);
            setQuestionStatus(testResults, questionDTO);
        }
        return questionDTO;
    }

    public Double calculateSubmissionScore(SubmissionEntity submissionEntity) {
        return submissionEntity.getQuestionEntities().stream().mapToDouble(QuestionEntity::getScore).sum();
    }

    public Double calculatedScore(List<TestResult> testResults, Question question) {
        double scoreForSingleTestCase = question.getTotalPoints().doubleValue() / question.getTestCases().size();
        long count = testResults.stream()
                .filter(
                        testResult ->
                                testResult.getStatus().equals(ResultStatus.PASSED))
                .count();
        return scoreForSingleTestCase * count;
    }

    public void setQuestionStatus(List<TestResult> testResults, QuestionDTO questionDTO) {
        boolean anyFailed = isTestResultFailed(testResults);
        if (anyFailed) {
            questionDTO.setResultStatus(ResultStatus.FAILED);
            questionDTO.setQuestionStatus(QuestionStatus.IN_PROGRESS);
        } else {
            questionDTO.setResultStatus(ResultStatus.PASSED);
            questionDTO.setQuestionStatus(QuestionStatus.COMPLETED);
        }
    }

    public boolean isTestResultFailed(List<TestResult> testResults) {
        return testResults.stream()
                .anyMatch(testResult -> testResult.getStatus().equals(ResultStatus.FAILED));
    }

    public List<SubmissionDetailsDTO> getSubmissionDetails(String classroomSlug, String assignmentId) {

        List<SubmissionDetailsDTO> submissionDetailDTOS = this.submissionRepository
                .findAllByClassroomSlugAndAssignmentId(classroomSlug, assignmentId)
                .stream()
                .map(submissionEntity -> modelMapper.map(submissionEntity, SubmissionDetailsDTO.class))
                .collect(Collectors.toList());
        return submissionDetailDTOS;
    }

    public List<TestResult> checkTestCases(PostBuildResponse userResponse, List<TestCase> expectedTestCase) {
        HashMap<String, TestCase> testCaseHashMap = new HashMap<>();
        expectedTestCase.stream().forEach(testCase -> {
            testCaseHashMap.put(testCase.getId(), testCase);
        });

        return userResponse.getTestOutputs().stream().map(testCaseResult -> {
            TestCase actualTestCase = testCaseHashMap.get(testCaseResult.getId());

            Reason reason = getReason(testCaseResult, actualTestCase);
            ResultStatus status = getResultStatus(testCaseResult, actualTestCase);

            return new TestResult(testCaseResult.getId(), status, reason);
        }).collect(Collectors.toList());
    }

    public Reason getReason(TestCaseResult testCaseResult, TestCase actualTestCase) {
        Reason reason = null;
        if (testCaseResult.getStatus().equals(Status.SUCCEED) && !testCaseResult.getOutput().trim().equals(actualTestCase.getOutput().trim())) {

            reason = Reason.WRONG_OUTPUT;
        } else if (testCaseResult.getStatus().equals(Status.RUNTIME_ERROR)) {
            reason = Reason.RUNTIME_ERROR;
        } else if (testCaseResult.getStatus().equals(Status.TIMEOUT)) {

            reason = Reason.TIMEOUT;
        }
        return reason;
    }

    public ResultStatus getResultStatus(TestCaseResult testCaseResult, TestCase actualTestCase) {
        ResultStatus resultStatus = ResultStatus.FAILED;
        if (testCaseResult.getStatus().equals(Status.SUCCEED) && testCaseResult.getOutput().trim().equals(actualTestCase.getOutput().trim())) {
            resultStatus = ResultStatus.PASSED;
        }
        return resultStatus;
    }

    public QuestionEntity getQuestion(String classroomSlug, String email, String assignmentId, String questionId) throws SubmissionEntityNotFoundException, QuestionEntityNotFoundException {
        SubmissionEntity submissionEntity = this.getSubmissionEntity(classroomSlug, assignmentId, email);
        if (submissionEntity.getQuestionEntities() != null) {


            QuestionEntity questionEntity =
                    submissionEntity.getQuestionEntities()
                            .stream()
                            .filter(questionEntity1 -> questionEntity1.getQuestionId().equals(questionId))
                            .findFirst()
                            .orElse(null);

            if (questionEntity == null) {
                throw new QuestionEntityNotFoundException("Not Found");
            }

            return questionEntity;
        } else {
            throw new QuestionEntityNotFoundException("Not Found");
        }
    }
}
