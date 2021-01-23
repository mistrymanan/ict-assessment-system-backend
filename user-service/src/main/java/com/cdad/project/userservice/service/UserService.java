package com.cdad.project.userservice.service;

import com.cdad.project.userservice.entity.CurrentUser;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.UserNotFoundException;
import com.cdad.project.userservice.exchanges.*;
import com.cdad.project.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {
    final private ModelMapper modelMapper;
    final private UserRepository userRepository;

    public UserService(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public User save(CreateUserRequest userRequest){
        System.out.println(userRequest.getEmailId());
        return this.userRepository.save(modelMapper.map(userRequest,User.class));
    }

    public void enrollUsers(EnrollUsersRequest request){
        String classroomSlug= request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail -> {
            User user1;
            try {
                user1=this.getByEmailId(userEmail);
            }catch (UserNotFoundException e){
                user1=new User();
                user1.setEmailId(userEmail);
            }
            if(user1.getEnrolledClassrooms()==null){
             user1.setEnrolledClassrooms(new HashSet<>());
            }
                user1.getEnrolledClassrooms().add(classroomSlug);
            this.userRepository.save(user1);
        });
    }

    public void unrollUsers(UnrollUsersRequest request){
        String classroomSlug=request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail->{
            User user1 = null;
            try{
                user1=this.getByEmailId(userEmail);
                if(user1.getEnrolledClassrooms()!=null){
                    user1.getEnrolledClassrooms().remove(classroomSlug);
                }
                this.userRepository.save(user1);
            }catch (UserNotFoundException e){
                System.out.println("User is not present so no need to unroll");
            }
        });
    }
    public void addInstructorToClassroom(AddInstructorsRequest request){
        String classroomSlug=request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail ->{
            User user1;
            try {
                user1=this.getByEmailId(userEmail);
            }
            catch (UserNotFoundException e){
                user1=new User();
                user1.setEmailId(userEmail);
            }
            if(user1.getInstructClassrooms()==null){user1.setInstructClassrooms(new HashSet<>());}
            user1.getInstructClassrooms().add(classroomSlug);
            this.userRepository.save(user1);
        });
    };
    public void removeInstructorsFromClassroom(RemoveInstructorsRequest request){
        String classroomSlug=request.getClassroomSlug();
        request.getUsers().stream().forEach(userEmail ->{
            User user1;
            try {
                user1=this.getByEmailId(userEmail);
                user1.getInstructClassrooms().remove(classroomSlug);
                this.userRepository.save(user1);
            }
            catch (UserNotFoundException e){
                System.out.println("User Not Found while removing from classroom");
            }
        });
    }

    public User updateUserMetadata(String emailId, Jwt jwt) throws UserNotFoundException {
        User user=this.getByEmailId(emailId);
        CurrentUser currentUser=CurrentUser.fromJwt(jwt);
        modelMapper.map(currentUser,user);
        return this.userRepository.save(user);
    }


    public User getByEmailId(String emailId) throws UserNotFoundException {
        Optional<User> userOptional=this.userRepository.findById(emailId);
        if(userOptional.isPresent()){
            return userOptional.get();
        }else {
            throw new UserNotFoundException("User with EmailId:"+emailId+" Not Found");
        }
    }

}
