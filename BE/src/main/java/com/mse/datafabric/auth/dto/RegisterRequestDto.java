package com.mse.datafabric.auth.dto;

import com.mse.datafabric.user.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body to create a {@link UserEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    // ToDo: Add javax.validation.constraints for properties
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
}
