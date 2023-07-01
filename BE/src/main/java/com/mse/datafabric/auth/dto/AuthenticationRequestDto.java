package com.mse.datafabric.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Dto to authenticate a {@link com.mse.datafabric.user.model.User}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {
    private String userName;
    private String password;
}
