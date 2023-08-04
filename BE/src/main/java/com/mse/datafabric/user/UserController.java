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

    @GetMapping(
            value = "/User",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDto> getUser() {
        return ResponseEntity.ok(myUserService.getCurrentUser());
    }

    @PutMapping( value = "/User" )
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

    @GetMapping(
            value = "/User/Ratings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getUSerRatings(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myUserService.getUserRatings());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }
}
