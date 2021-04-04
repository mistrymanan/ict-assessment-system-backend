package com.cdad.project.gradingservice.service;

import com.cdad.project.gradingservice.dto.UserQuestionResponseDTO;
import com.cdad.project.gradingservice.entity.SubmissionEntity;
import com.cdad.project.gradingservice.repository.SubmissionRepository;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.ExecutionServiceClient;
import com.cdad.project.gradingservice.serviceclient.executionservice.dto.BuildCode;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.GetBuildsRequest;
import com.cdad.project.gradingservice.serviceclient.userservice.UserServiceClient;
import com.cdad.project.gradingservice.serviceclient.userservice.dtos.UserDetail;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.GetUsersDetailRequest;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.GetUsersDetailsResponse;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubmissionPlagarismService {
    final private ModelMapper modelMapper;
    final private SubmissionRepository submissionRepository;
    final private ExecutionServiceClient executionServiceClient;
    final private UserServiceClient userServiceClient;

    public SubmissionPlagarismService(ModelMapper modelMapper, AssignmentServiceClient assignmentServiceClient, SubmissionRepository submissionRepository, ExecutionServiceClient executionServiceClient, UserServiceClient userServiceClient) {
        this.modelMapper = modelMapper;
        this.submissionRepository = submissionRepository;
        this.executionServiceClient = executionServiceClient;
        this.userServiceClient = userServiceClient;
    }
    public List<UserQuestionResponseDTO> getUsersQuestionResponseDTO(String assignmentId, String questionId, Jwt jwt){
        List<UserQuestionResponseDTO> responseDTOS=new LinkedList<>();
        List<SubmissionEntity> submissionEntities=this.submissionRepository.findAllByAssignmentId(assignmentId);
        HashMap<String,String> userBuildIds=new HashMap<>();
        submissionEntities.forEach(submissionEntity -> {
            submissionEntity.getQuestionEntities().forEach(
                    questionEntity -> {
                        if(questionEntity.getQuestionId().equals(questionId)){
                            userBuildIds.put(submissionEntity.getEmail(), questionEntity.getBuildId());
                        }
                    }
            );
        });
        List<String> buildIds= new LinkedList<>(userBuildIds.values());
        Set<String> set=userBuildIds.keySet();

        GetUsersDetailRequest getUsersDetailRequest=new GetUsersDetailRequest();
        getUsersDetailRequest.setUsersEmail(set);

        GetUsersDetailsResponse getUsersDetailsResponse=this.userServiceClient.getUsersDetails(getUsersDetailRequest,jwt);
        List<UserDetail> userDetails=getUsersDetailsResponse.getUsersDetail();
        GetBuildsRequest request=new GetBuildsRequest(buildIds);
        Map<String, BuildCode> buildCodeMap=this.executionServiceClient.getBuilds(request,jwt).getBuilds();

        userDetails.stream().forEach(userDetail -> {
            String buildId=userBuildIds.get(userDetail.getEmailId());
            BuildCode buildCode= buildCodeMap.get(buildId);
            responseDTOS.add(new UserQuestionResponseDTO(
                    userDetail.getName(),
                    buildCode.getSourceCode(),
                    buildCode.getLanguage()));
        });
        return responseDTOS;
    }


}
