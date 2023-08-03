package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.user.dto.UserDto;
import com.mse.datafabric.user.entities.UserEntity;
import com.mse.datafabric.user.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AuthenticationService myAuthenticationService;
    private final UserRepository myUserRepository;

    @Autowired
    public UserService(AuthenticationService authenticationService, UserRepository userRepository) {
        myAuthenticationService = authenticationService;
        myUserRepository = userRepository;
    }

    public UserResponseDto getCurrentUser() {
        UserEntity currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        return new UserResponseDto(currentUserEntity.getFirstname(), currentUserEntity.getLastname(), currentUserEntity.getUsername(), currentUserEntity.getEmail());
    }

    public void updateUser(UserDto user) {
        UserEntity currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        if(!currentUserEntity.getFirstname().equals(user.getFirstName())) {
            currentUserEntity.setFirstname(user.getFirstName());
        }
        if(!currentUserEntity.getLastname().equals(user.getLastName())) {
            currentUserEntity.setLastname(user.getLastName());
        }
        if(!currentUserEntity.getEmail().equals(user.getEmail())) {
            currentUserEntity.setEmail(user.getEmail());
        }

        myUserRepository.save(currentUserEntity);
    }
}
