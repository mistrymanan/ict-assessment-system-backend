package com.cdad.project.userservice.service;

import com.cdad.project.userservice.dto.AdminUserDetails;
import com.cdad.project.userservice.dto.UserDetails;
import com.cdad.project.userservice.entity.CurrentUser;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.NotAuthorized;
import com.cdad.project.userservice.exceptions.UserNotFoundException;
import com.cdad.project.userservice.exchanges.*;
import com.cdad.project.userservice.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuthException;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    final private ModelMapper modelMapper;
    final private UserRepository userRepository;
    final private FirebaseAdminManagementService firebaseAdminManagementService;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, FirebaseAdminManagementService firebaseAdminManagementService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.firebaseAdminManagementService = firebaseAdminManagementService;
    }

    public User save(CreateUserRequest userRequest) {
        System.out.println(userRequest.getEmailId());
        User user=modelMapper.map(userRequest, User.class);
        user.setAllowedClassroomCreation(false);
        user.setIsAdmin(false);
        return this.userRepository.save(user);
    }

    public void enrollUsers(EnrollUsersRequest request) {
        String classroomSlug = request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail -> {
            User user1;
            try {
                user1 = this.getByEmailId(userEmail);
            } catch (UserNotFoundException e) {
                user1 = new User();
                user1.setEmailId(userEmail);
            }
            if (user1.getEnrolledClassrooms() == null) {
                user1.setEnrolledClassrooms(new HashSet<>());
            }
            user1.getEnrolledClassrooms().add(classroomSlug);
            this.userRepository.save(user1);
        });
    }

    public void unrollUsers(UnrollUsersRequest request) {
        String classroomSlug = request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail -> {
            User user1 = null;
            try {
                user1 = this.getByEmailId(userEmail);
                if (user1.getEnrolledClassrooms() != null) {
                    user1.getEnrolledClassrooms().remove(classroomSlug);
                }
                this.userRepository.save(user1);
            } catch (UserNotFoundException e) {
                System.out.println("User is not present so no need to unroll");
            }
        });
    }

    public void addInstructorToClassroom(AddInstructorsRequest request) {
        String classroomSlug = request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail -> {
            User user1;
            try {
                user1 = this.getByEmailId(userEmail);
            } catch (UserNotFoundException e) {
                user1 = new User();
                user1.setEmailId(userEmail);
            }
            if (user1.getInstructClassrooms() == null) {
                user1.setInstructClassrooms(new HashSet<>());
            }
            user1.getInstructClassrooms().add(classroomSlug);
            this.userRepository.save(user1);
        });
    }

    ;

    public void removeInstructorsFromClassroom(RemoveInstructorsRequest request) {
        String classroomSlug = request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail -> {
            User user1;
            try {
                user1 = this.getByEmailId(userEmail);
                user1.getInstructClassrooms().remove(classroomSlug);
                this.userRepository.save(user1);
            } catch (UserNotFoundException e) {
                System.out.println("User Not Found while removing from classroom");
            }
        });
    }

    public User updateUserMetadata(String emailId, Jwt jwt) throws UserNotFoundException {
        User user = this.getByEmailId(emailId);
        CurrentUser currentUser = CurrentUser.fromJwt(jwt);
        modelMapper.map(currentUser, user);
        return this.userRepository.save(user);
    }


    public User getByEmailId(String emailId) throws UserNotFoundException {
        Optional<User> userOptional = this.userRepository.findById(emailId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("User with EmailId:" + emailId + " Not Found");
        }
    }
    public void toggleAdminRights(String emailId,Jwt jwt) throws UserNotFoundException, NotAuthorized, FirebaseAuthException {
        if(CurrentUser.fromJwt(jwt).getIsAdmin()){
            User user=getByEmailId(emailId);
            Boolean adminStatus=user.getIsAdmin();
            if(adminStatus==null){
                user.setIsAdmin(true);
            }
            else{
            user.setIsAdmin(!adminStatus);
            }
            firebaseAdminManagementService.setRights(user);
            this.userRepository.save(user);
        }
        else{
            throw new NotAuthorized("You don't have access to update Admin Rights");
        }
    }
    public void toggleClassroomCreationPermission(String emailId,Jwt jwt) throws UserNotFoundException, NotAuthorized, FirebaseAuthException {
        if(CurrentUser.fromJwt(jwt).getIsAdmin()){
        User user=getByEmailId(emailId);
        Boolean allowedClassroomCreation=user.getAllowedClassroomCreation();
        if(allowedClassroomCreation==null){
            user.setAllowedClassroomCreation(true);
        }
        else{
            user.setAllowedClassroomCreation(!allowedClassroomCreation);
        }
        firebaseAdminManagementService.setRights(user);
        this.userRepository.save(user);
    }
        else{
        throw new NotAuthorized("You don't have access to update Classroom Creation Rights");
    }
    }

    public GetUsersDetailsResponse getUsersData(GetUsersDetailRequest request) {
        List<UserDetails> usersDetail = new LinkedList<>();
        request.getUsersEmail().forEach(email -> {
            Optional<User> optionalUser = this.userRepository.findById(email);
            if (optionalUser.isPresent()) {
                usersDetail.add(modelMapper.map(optionalUser.get(), UserDetails.class));
            }
        });
        GetUsersDetailsResponse response = new GetUsersDetailsResponse();
        response.setUsersDetail(usersDetail);
        return response;
    }
    public List<AdminUserDetails> getAllUser(Jwt jwt) throws NotAuthorized {
        CurrentUser currentUser=CurrentUser.fromJwt(jwt);
        System.out.println(currentUser.getIsAdmin());
        if (currentUser.getIsAdmin()){
            return this.userRepository.findAll().stream().map(user -> modelMapper
                    .map(user,AdminUserDetails.class))
                    .collect(Collectors.toList());
        }
        else{
            throw new NotAuthorized("You are not Authorized to perform this action.");
        }
    }

}
