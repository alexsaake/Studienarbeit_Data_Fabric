package com.mse.datafabric.auth.dto;

import com.mse.datafabric.user.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Dto to authenticate a {@link UserEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {
    private String userName;
    private String password;
}
