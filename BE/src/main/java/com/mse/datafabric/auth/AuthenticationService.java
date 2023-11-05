package com.mse.datafabric.auth;

import com.mse.datafabric.auth.models.RefreshToken;
import com.mse.datafabric.auth.payload.request.LoginRequest;
import com.mse.datafabric.auth.payload.request.RefreshRequest;
import com.mse.datafabric.auth.payload.response.JwtResponse;
import com.mse.datafabric.auth.payload.request.RegisterRequest;
import com.mse.datafabric.auth.services.RefreshTokenService;
import com.mse.datafabric.config.JwtService;
import com.mse.datafabric.auth.repositories.UserRepository;
import com.mse.datafabric.user.models.UserRoles;
import com.mse.datafabric.auth.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService;

    public boolean register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRoles.USER)
                .build();

        try {
            userRepository.save(user);
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    public JwtResponse login(LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));

        var user = userRepository.findByUsername(request.getUserName()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new JwtResponse(jwtToken, refreshToken.getToken());
    }

    public JwtResponse refresh(RefreshRequest request) throws Exception {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return new JwtResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new Exception("Refresh token is not in database!"));
    }

    public String getCurrentUserName(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getName();
    }
}
