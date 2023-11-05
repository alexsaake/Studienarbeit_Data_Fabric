package com.mse.datafabric.auth;

import com.mse.datafabric.auth.payload.request.LoginRequest;
import com.mse.datafabric.auth.payload.request.RefreshRequest;
import com.mse.datafabric.auth.payload.response.JwtResponse;
import com.mse.datafabric.auth.payload.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Gateway/Auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/Register")
    public ResponseEntity<Boolean> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/Login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }

    @PostMapping("/Refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshRequest refreshRequest) throws Exception {
        return ResponseEntity.ok(authenticationService.refresh(refreshRequest));
    }
}
