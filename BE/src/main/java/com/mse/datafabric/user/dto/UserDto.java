package com.mse.datafabric.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"firstName", "lastName", "email"})
public class UserDto {
    private final String firstName;
    private final String lastName;
    private final String email;

    @JsonCreator
    public UserDto(@JsonProperty("firstName")String firstName, @JsonProperty("lastName")String lastName, @JsonProperty("email")String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
