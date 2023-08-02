package com.mse.datafabric.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.user.dto.UserDto;
import com.mse.datafabric.user.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Gateway")
@PreAuthorize("hasAuthority('USER')")
public class UserController {
    private final Logger myLogger;
    private final UserService myUserService;

    @Autowired
    public UserController(UserService userService) {
        myUserService = userService;
        myLogger = LoggerFactory.getLogger(this.getClass().getName());
    }

    @ShellMethod( "getUser" )
    @GetMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDto> getUser() {
        return ResponseEntity.ok(myUserService.getCurrentUser());
    }

    @ShellMethod( "putUser" )
    @PutMapping( value = "/user" )
    public void updateUser(@RequestBody String requestBodyJson) {
        UserDto user = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(requestBodyJson, UserDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        myUserService.updateUser(user);
    }
}
