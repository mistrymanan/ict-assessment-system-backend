package com.cdad.project.gradingservice.controller;

import com.cdad.project.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.gradingservice.dto.QuestionDTO;
import com.cdad.project.gradingservice.dto.SubmissionUserDetailsDTO;
import com.cdad.project.gradingservice.entity.*;
import com.cdad.project.gradingservice.exception.*;
import com.cdad.project.gradingservice.exchange.*;
import com.cdad.project.gradingservice.service.SubmissionService;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.QuestionDetails;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    PostRunCodeResponse postRunCode(@RequestBody PostRunCodeRequest request ) throws RunCodeCompilationErrorException {
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
    PostSubmitResponse submitNew(@RequestBody PostSubmitRequest request) throws SubmissionCompilationErrorException, LanguageNotAllowedException, AssignmentNotActiveException, AssignmentNotStartedException {
        if(submissionService.isExist(request.getAssignmentId(), request.getEmail())){
        PostSubmitResponse postSubmitResponse = new PostSubmitResponse();
        modelMapper.map(request,postSubmitResponse);
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(request.getAssignmentId()
                , request.getEmail());
        postSubmitResponse.setSubmissionId(submissionEntity.getId().toString());

        GetQuestionRequest getQuestionRequest = new GetQuestionRequest();
        modelMapper.map(request, getQuestionRequest);

        Assignment assignment=assignmentServiceClient.getAssignment(request.getAssignmentId()).block();
        Question question = assignmentServiceClient.getQuestion(getQuestionRequest).block();
        if(assignment.getStatus().equals("ACTIVE")){
        QuestionDTO questionDTO = null;
            try {
                questionDTO = this.submissionService.evaluate(request,question,assignment);
                postSubmitResponse.setBuildId(questionDTO.getBuildId());
                modelMapper.map(questionDTO,postSubmitResponse);
                postSubmitResponse.setStatus(questionDTO.getResultStatus());
            } catch (SubmissionCompilationErrorException error) {
                error.setSubmissionId(submissionEntity.getId().toString());

                questionDTO=modelMapper.map(error,QuestionDTO.class);
                modelMapper.map(question,questionDTO);
                questionDTO.setScore(0.0);
                questionDTO.setTime(LocalDateTime.now());
                throw error;
            }
            finally {
                QuestionEntity questionEntity=this.modelMapper.map(questionDTO,QuestionEntity.class);
                questionDTO.setTitle(question.getTitle());
                submissionEntity=this.submissionService.save(submissionEntity,questionEntity,assignment);
            }
        modelMapper.map(submissionEntity,postSubmitResponse);
        return postSubmitResponse;
        }
//        else{
//            throw new LanguageNotAllowedException(request.getLanguage()+" Not Allowed for "+question.getTitle()+"'s Submission.");
//        }
        else{
            throw new AssignmentNotActiveException("Assignment:"+assignment.getTitle()+" is Not Active!");
        }
        }
        else{
            throw new AssignmentNotStartedException("You Haven't Started Assignment. Please start the Assignment First");
        }
    }
    @PatchMapping("/submit")
    void startQuestion(@RequestBody StartQuestionRequest request){
        if(!submissionService.isExist(request.getAssignmentId(), request.getEmail())){
            SubmissionEntity submissionEntity=modelMapper.map(request,SubmissionEntity.class);
            Assignment assignment=this.assignmentServiceClient.getAssignment(request.getAssignmentId())
                    .block();
            if(assignment.getQuestions()!=null) {
                submissionEntity.setAssignmentScore(assignment.getQuestions().stream().mapToDouble(QuestionDetails::getTotalPoints).sum());
            }
            else{
                submissionEntity.setAssignmentScore(0.0);
            }
            submissionEntity.setSubmissionStatus(SubmissionStatus.IN_PROGRESS);
            submissionEntity.setStartOn(LocalDateTime.now());
            this.submissionService.save(submissionEntity);
        }
    }
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
//    public ErrorResponse handle(Exception error){
//        ErrorResponse errorResponse=modelMapper.map(error,ErrorResponse.class);
//        return errorResponse;
//    }



    @ExceptionHandler(SubmissionCompilationErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handle(SubmissionCompilationErrorException error){
        ErrorResponse errorResponse=modelMapper.map(error,ErrorResponse.class);
        errorResponse.setStatus(Status.COMPILE_ERROR);
        return errorResponse;
    }

//
//    @ExceptionHandler(LanguageNotAllowedException.class)
//    @ResponseStatus(HttpStatus.OK)
//    public ErrorResponse handle(LanguageNotAllowedException error){
//        ErrorResponse errorResponse=modelMapper.map(error,ErrorResponse.class);
//        return errorResponse;
//    }

    @GetMapping("/{assignmentId}")
    public List<SubmissionDetailsDTO> getSubmissions(@PathVariable String assignmentId){
        //if(this.assignmentServiceClient.getAssignment())
        //need to add a filed to check who have created assignment.
        return this.submissionService.getSubmissionDetails(assignmentId);
    }
    @GetMapping("/{assignmentId}/user")
    public SubmissionUserDetailsDTO getSubmissionUserDetails(@PathVariable String assignmentId, @RequestParam String email){
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(assignmentId, email);
        SubmissionUserDetailsDTO submissionUserDetailsDTO=modelMapper.map(submissionEntity,SubmissionUserDetailsDTO.class);
        return submissionUserDetailsDTO;
    }
    @GetMapping("/{assignmentId}/{questionId}")
    public QuestionDTO getSubmissionUserDetails(@PathVariable String assignmentId,@PathVariable String questionId, @RequestParam String email){
        SubmissionEntity submissionEntity=this.submissionService.getSubmissionEntity(assignmentId, email);
        QuestionEntity questionEntity=submissionEntity
                .getQuestionEntities()
                .stream()
                .filter(questionEntity1 -> questionEntity1.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
        return modelMapper.map(questionEntity,QuestionDTO.class);
    }

    @ExceptionHandler(RunCodeCompilationErrorException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handle(RunCodeCompilationErrorException error){
        return modelMapper.map(error,ErrorResponse.class);
    }
}
