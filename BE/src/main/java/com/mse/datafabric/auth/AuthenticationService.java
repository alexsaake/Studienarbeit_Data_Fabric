package com.mse.datafabric.auth;

import com.mse.datafabric.auth.dto.AuthenticationRequestDto;
import com.mse.datafabric.auth.dto.AuthenticationResponseDto;
import com.mse.datafabric.auth.dto.RegisterRequestDto;
import com.mse.datafabric.config.JwtService;
import com.mse.datafabric.user.UserRepository;
import com.mse.datafabric.user.model.Role;
import com.mse.datafabric.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository myUserRepository;
    private final PasswordEncoder myPasswordEncoder;
    private final JwtService myJwtService;

    private final AuthenticationManager authManager;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        // incorrect username + password will automatically throw an exception
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));

        // ToDo: throw correct exception, but this case is very unlikely because beforehand an error will be thrown
        var user = myUserRepository.findByUsername(request.getUserName()).orElseThrow();

        var jwtToken = myJwtService.generateToken(user);

        return AuthenticationResponseDto.builder().token(jwtToken).build();
    }

    public AuthenticationResponseDto register(RegisterRequestDto request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .username(request.getUserName())
                .password(myPasswordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        var savedUser = myUserRepository.save(user);
        var jwtToken = myJwtService.generateToken(savedUser);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}
