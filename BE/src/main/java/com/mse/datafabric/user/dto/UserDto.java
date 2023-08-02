package com.mse.datafabric.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonPropertyOrder({"firstName", "lastName", "email"})
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;

    @JsonCreator
    public UserDto(@JsonProperty("firstName")String firstName, @JsonProperty("lastName")String lastName, @JsonProperty("email")String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
