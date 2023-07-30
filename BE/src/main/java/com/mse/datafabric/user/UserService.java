package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.auth.dto.AuthenticationResponseDto;
import com.mse.datafabric.user.model.User;
import com.mse.datafabric.user.model.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService myAuthenticationService;
    private final UserRepository myUserRepository;

    public UserResponseDto getCurrentUser(){
        User currentUser = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        return new UserResponseDto(currentUser.getFirstname(), currentUser.getLastname(), currentUser.getUsername(), currentUser.getEmail());
    }
}
