package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.user.dto.UserDto;
import com.mse.datafabric.user.model.User;
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
        User currentUser = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        return new UserResponseDto(currentUser.getFirstname(), currentUser.getLastname(), currentUser.getUsername(), currentUser.getEmail());
    }

    public void updateUser(UserDto user) {
        User currentUser = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();
        if(!currentUser.getFirstname().equals(user.getFirstName())) {
            currentUser.setFirstname(user.getFirstName());
        }
        if(!currentUser.getLastname().equals(user.getLastName())) {
            currentUser.setLastname(user.getLastName());
        }
        if(!currentUser.getEmail().equals(user.getEmail())) {
            currentUser.setEmail(user.getEmail());
        }
    }
}
