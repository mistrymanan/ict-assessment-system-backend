package com.cdad.project.classroomservice.service;

import com.cdad.project.classroomservice.dto.ClassroomAndUserDetailsDTO;
import com.cdad.project.classroomservice.dto.ClassroomDetailsDTO;
import com.cdad.project.classroomservice.entity.Classroom;
import com.cdad.project.classroomservice.entity.CurrentUser;
import com.cdad.project.classroomservice.exceptions.ClassroomAccessForbidden;
import com.cdad.project.classroomservice.exceptions.ClassroomAlreadyExists;
import com.cdad.project.classroomservice.exceptions.ClassroomNotFound;
import com.cdad.project.classroomservice.exchanges.*;
import com.cdad.project.classroomservice.repository.ClassroomRepository;
import com.cdad.project.classroomservice.serviceclient.notificationservice.NotificationServiceClient;
import com.cdad.project.classroomservice.serviceclient.userservice.UserServiceClient;
import com.cdad.project.classroomservice.serviceclient.userservice.dtos.UserDetailsDTO;
import com.cdad.project.classroomservice.serviceclient.userservice.exceptions.UserNotFoundException;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.GetUsersDetailRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.GetUsersDetailsResponse;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClassroomService {
    final private ClassroomRepository classroomRepository;
    final private ModelMapper modelMapper;
    final private UserServiceClient userServiceClient;
    final private NotificationServiceClient notificationServiceClient;
    public ClassroomService(ClassroomRepository classroomRepository, ModelMapper modelMapper, UserServiceClient userServiceClient, NotificationServiceClient notificationServiceClient) {
        this.classroomRepository = classroomRepository;
        this.modelMapper = modelMapper;
        this.userServiceClient = userServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    private String slugify(String str) {
        return String.join("-", str.trim().toLowerCase().split("\\s+"));
    }

    private Classroom saveNew(CreateClassroomRequest request, Jwt jwt) {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = modelMapper.map(request, Classroom.class);
        classroom.setSlug(slugify(request.getTitle()));
        classroom.setOwnerEmail(currentUser.getEmail());
        classroom.setOwnerName(currentUser.getName());
        classroom.setOwnerPicture(currentUser.getPicture());
        HashSet<String> instructors = new HashSet<>();
        HashSet<String> enrolledUsers = new HashSet<>();
        classroom.setInstructors(instructors);
        classroom.setEnrolledUsers(enrolledUsers);
        //instructors.add(currentUser.getEmail());
        return this.classroomRepository.save(classroom);
    }
//    public Classroom getClassroom(String classroomSlug,Jwt jwt) throws ClassroomNotFound {
//        Classroom classroom=classroomRepository.getClassroomBySlug(classroomSlug)
//                .orElseThrow(() -> new ClassroomNotFound("The Classroom that you are trying to delete doesn't exists"));
//        return classroom;
//    }

    public GetClassroomsResponse getUsersClassrooms(Jwt jwt) throws UserNotFoundException {
        CurrentUser user = CurrentUser.fromJwt(jwt);
        UserDetailsDTO userDetailsDTO = userServiceClient.getUserDetails(jwt);
        List<ClassroomDetailsDTO> instructClassrooms = new LinkedList<>();
        List<ClassroomDetailsDTO> enrolledClassrooms = new LinkedList<>();
        if (userDetailsDTO.getInstructClassrooms() != null) {
            userDetailsDTO.getInstructClassrooms().stream().forEach(classroomSlug -> {
                Optional<Classroom> classroom = classroomRepository.getClassroomBySlug(classroomSlug);
                classroom.ifPresent(value -> instructClassrooms.add(modelMapper.map(value, ClassroomDetailsDTO.class)));
            });
        }
        if (userDetailsDTO.getEnrolledClassrooms() != null) {
            userDetailsDTO.getEnrolledClassrooms().stream().forEach(classroomSlug -> {
                Optional<Classroom> classroom = classroomRepository.getClassroomBySlug(classroomSlug);
                classroom.ifPresent(value -> enrolledClassrooms.add(modelMapper.map(value, ClassroomDetailsDTO.class)));
            });
        }

        GetClassroomsResponse response = new GetClassroomsResponse();
        response.setEnrolledClassrooms(enrolledClassrooms);
        response.setInstructClassrooms(instructClassrooms);
        return response;
    }

    public ClassroomAndUserDetailsDTO getClassroom(String classroomSlug, Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        CurrentUser user = CurrentUser.fromJwt(jwt);
        Optional<Classroom> optionalClassroom = classroomRepository.getClassroomBySlug(classroomSlug);
        if (optionalClassroom.isPresent()) {
            Classroom classroom = optionalClassroom.orElseThrow(() -> new ClassroomNotFound("Classroom Not Found."));
            if (classroom.getEnrolledUsers().contains(user.getEmail())
                    || classroom.getInstructors().contains(user.getEmail()) || classroom.getOwnerEmail().equals(user.getEmail())) {
                return getClassroomAndUserDetails(classroom, jwt);
            } else {
                throw new ClassroomAccessForbidden("You are neither Instructor nor Student for Title: "
                        + classroom.getTitle() + " Class.");
            }
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

    private GetUsersDetailsResponse getUsersDetail(HashSet<String> user, Jwt jwt) {
        GetUsersDetailRequest request = new GetUsersDetailRequest();
        request.setUsersEmail(user);
        return this.userServiceClient.getUsersDetails(request, jwt);
    }

    public Classroom addClassroom(CreateClassroomRequest request, Jwt jwt) throws ClassroomAlreadyExists {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        String classroomSlug = slugify(request.getTitle());
        if (!classroomRepository.existsBySlug(classroomSlug)) {
            Classroom classroom = saveNew(request, jwt);
            classroom.getInstructors().add(classroom.getOwnerEmail());
            classroom.getInstructors().addAll(request.getInstructors());
            System.out.println(request.getInstructors());
            userServiceClient.addInstructorToClass(classroom.getSlug(), classroom.getInstructors(), jwt);
            this.classroomRepository.save(classroom);
            return classroom;
        } else {
            throw new ClassroomAlreadyExists(
                    "Classroom With Name:"
                            + request.getTitle()
                            + " already exists. please try again with different name.");
        }
    }

    public void removeClassroom(String classroomSlug, Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom that you are trying to delete doesn't exists"));
        if (classroom.getOwnerEmail().equals(currentUser.getEmail())) {
            classroom.getInstructors().add(classroom.getOwnerEmail());
            userServiceClient.removeInstructorFromClass(classroom.getSlug(), classroom.getInstructors(), jwt);
            userServiceClient.unrollUsersFromClass(classroom.getSlug(), classroom.getEnrolledUsers(), jwt);
            this.classroomRepository.deleteById(classroom.getId());
        } else {
            throw new ClassroomAccessForbidden("You Don't have required Access to Delete this classroom");
        }
    }

    public void addInstructorsToClassroom(AddInstructorsRequest request, String classroomSlug, Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom doesn't exists"));
        if (classroom.getOwnerEmail().equals(currentUser.getEmail())
                || classroom.getInstructors().contains(currentUser.getEmail())) {
            userServiceClient.addInstructorToClass(classroomSlug, request.getInstructors(), jwt);
            classroom.getInstructors().addAll(request.getInstructors());
            classroomRepository.save(classroom);
        } else {
            throw new ClassroomAccessForbidden("you don't have Sufficient Authorization to add instructors.");
        }
    }

    public void removeInstructorsFromClassroom(RemoveInstructorsRequest request, String classroomSlug, Jwt jwt) throws ClassroomAccessForbidden, ClassroomNotFound {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom doesn't exists"));
        if (classroom.getOwnerEmail().equals(currentUser.getEmail())
                || classroom.getInstructors().contains(currentUser.getEmail())) {
            userServiceClient.removeInstructorFromClass(classroomSlug, request.getInstructors(), jwt);
            classroom.getInstructors().removeAll(request.getInstructors());
            classroomRepository.save(classroom);
        } else {
            throw new ClassroomAccessForbidden("you don't have Sufficient Authorization to remove Instructors.");
        }
    }

    public void enrollUsersToClassroom(EnrollUsersRequest request, String classroomSlug, Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom doesn't exists"));
        if (classroom.getOwnerEmail().equals(currentUser.getEmail())
                || classroom.getInstructors().contains(currentUser.getEmail())) {
            userServiceClient.enrollUsersToClass(classroomSlug, request.getUsers(), jwt);
            classroom.getEnrolledUsers().addAll(request.getUsers());
            classroomRepository.save(classroom);
            notificationServiceClient
                    .sendEmailNotification(
                            request.getUsers()
                            ,"Assessment-System:Classroom Enrollment:"
                                    +classroom.getTitle(),
                            "dear student,"
                                    +"\n \t you have been added to Classroom:"+classroom.getTitle()+" by "
                                    +currentUser.getName()+".");

        } else {
            throw new ClassroomAccessForbidden("you don't have Sufficient Authorization to Enroll Users.");
        }

    }

    public void unrollUsersFromClassroom(RemoveUsersRequest request, String classroomSlug, Jwt jwt) throws ClassroomNotFound, ClassroomAccessForbidden {
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        Classroom classroom = classroomRepository.getClassroomBySlug(classroomSlug)
                .orElseThrow(() -> new ClassroomNotFound("The Classroom doesn't exists"));
        if (classroom.getOwnerEmail().equals(currentUser.getEmail())
                || classroom.getInstructors().contains(currentUser.getEmail())) {
            userServiceClient.unrollUsersFromClass(classroomSlug, request.getUsers(), jwt);
            classroom.getEnrolledUsers().removeAll(request.getUsers());
            classroomRepository.save(classroom);
        } else {
            throw new ClassroomAccessForbidden("you don't have Sufficient Authorization to unroll Users.");
        }
    }
}
