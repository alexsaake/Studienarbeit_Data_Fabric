package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.auth.dto.AuthenticationRequestDto;
import com.mse.datafabric.auth.dto.AuthenticationResponseDto;
import com.mse.datafabric.auth.dto.RegisterRequestDto;
import com.mse.datafabric.user.model.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Gateway")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")
public class UserController {

    private final UserService myUserService;

    @ShellMethod( "getUser" )
    @GetMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDto> getUser() {
        return ResponseEntity.ok(myUserService.getCurrentUser());
    }
}
