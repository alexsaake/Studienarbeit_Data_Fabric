package com.mse.datafabric.auth.payload.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
