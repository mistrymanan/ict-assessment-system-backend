package com.cdad.project.classroomservice.service;

import com.cdad.project.classroomservice.dto.AdminClassroomDetailsDTO;
import com.cdad.project.classroomservice.dto.ClassroomAndUserDetailsDTO;
import com.cdad.project.classroomservice.entity.Classroom;
import com.cdad.project.classroomservice.entity.CurrentUser;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.repository.ClassroomRepository;
import com.cdad.project.classroomservice.serviceclient.userservice.UserServiceClient;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.GetUsersDetailRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.GetUsersDetailsResponse;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminClassroomService {
    final private ClassroomRepository classroomRepository;
    final private ModelMapper modelMapper;
    final private UserServiceClient userServiceClient;

    public AdminClassroomService(ClassroomRepository classroomRepository, ModelMapper modelMapper, UserServiceClient userServiceClient) {
        this.classroomRepository = classroomRepository;
        this.modelMapper = modelMapper;
        this.userServiceClient = userServiceClient;
    }
    public List<AdminClassroomDetailsDTO> getAllClassrooms(){
        List<Classroom> classrooms=this.classroomRepository.findAll();
        return classrooms.stream().map(classroom -> {
            AdminClassroomDetailsDTO adminClassroomDetailsDTO= this.modelMapper.map(classroom,AdminClassroomDetailsDTO.class);
            adminClassroomDetailsDTO.setTotalStudents(classroom.getEnrolledUsers().size());
            adminClassroomDetailsDTO.setTotalInstructors(classroom.getInstructors().size());
            return adminClassroomDetailsDTO;
        }).collect(Collectors.toList());
    }

    public void removeClassroom(String classroomSlug, Jwt jwt) throws ClassroomNotFound{
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom that you are trying to delete doesn't exists"));
        //check admin rights then delete it.
            classroom.getInstructors().add(classroom.getOwnerEmail());
            userServiceClient.removeInstructorFromClass(classroom.getSlug(), classroom.getInstructors(), jwt);
            userServiceClient.unrollUsersFromClass(classroom.getSlug(), classroom.getEnrolledUsers(), jwt);
            this.classroomRepository.deleteById(classroom.getId());
//        else {
//            throw new ClassroomAccessForbidden("You Don't have required Access to Delete this classroom");
//        }
    }

    public ClassroomAndUserDetailsDTO getClassroom(String classroomSlug, Jwt jwt)  throws ClassroomNotFound{
        CurrentUser user = CurrentUser.fromJwt(jwt);
        Optional<Classroom> optionalClassroom = classroomRepository.getClassroomBySlug(classroomSlug);
        if (optionalClassroom.isPresent()) {
            Classroom classroom = optionalClassroom.orElseThrow(() -> new ClassroomNotFound("Classroom Not Found."));
            //check user authorization then send the details
//            if (classroom.getEnrolledUsers().contains(user.getEmail())
//                    || classroom.getInstructors().contains(user.getEmail()) || classroom.getOwnerEmail().equals(user.getEmail())) {
                return getClassroomAndUserDetails(classroom, jwt);
//            } else {
//                throw new ClassroomAccessForbidden("You are neither Instructor nor Student for Title: "
//                        + classroom.getTitle() + " Class.");
//            }
        }
        return null;
    }

    public ClassroomAndUserDetailsDTO getClassroomAndUserDetails(Classroom classroom, Jwt jwt) {
        ClassroomAndUserDetailsDTO classroomAndUserDetailsDTO = this.modelMapper.map(classroom, ClassroomAndUserDetailsDTO.class);
        GetUsersDetailsResponse instructorsDetails;
        GetUsersDetailsResponse enrolledUserDetails;
        if (classroom.getInstructors() != null && classroom.getInstructors().size() > 0) {
            instructorsDetails = getUsersDetail(classroom.getInstructors(), jwt);
            classroomAndUserDetailsDTO.setInstructors(instructorsDetails.getUsersDetail());
        }
        if (classroom.getEnrolledUsers() != null && classroom.getEnrolledUsers().size() > 0) {
            enrolledUserDetails = getUsersDetail(classroom.getEnrolledUsers(), jwt);
            classroomAndUserDetailsDTO.setEnrolledUsers(enrolledUserDetails.getUsersDetail());
        }
        return classroomAndUserDetailsDTO;
    }

    private GetUsersDetailsResponse getUsersDetail(HashSet<String> users, Jwt jwt) {
        GetUsersDetailRequest request = new GetUsersDetailRequest();
        request.setUsersEmail(users);
        return this.userServiceClient.getUsersDetails(request, jwt);
    }

}
