package com.cdad.project.userservice.service;

import com.cdad.project.userservice.entity.CurrentUser;
import com.cdad.project.userservice.entity.User;
import com.cdad.project.userservice.exceptions.UserNotFoundException;
import com.cdad.project.userservice.exchanges.CreateUserRequest;
import com.cdad.project.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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
        return this.userRepository.save(modelMapper.map(userRequest,User.class));
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
