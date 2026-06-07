package org.example.task_management_rest_api.service;

import org.example.task_management_rest_api.dto.request.LoginRequest;
import org.example.task_management_rest_api.dto.request.RefreshTokenRequest;
import org.example.task_management_rest_api.dto.request.SignupRequest;
import org.example.task_management_rest_api.dto.response.AuthResponse;
import org.example.task_management_rest_api.exception.InvalidCredentialsException;
import org.example.task_management_rest_api.model.RefreshToken;
import org.example.task_management_rest_api.model.User;
import org.example.task_management_rest_api.security.JwtService;
import org.example.task_management_rest_api.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse signup(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User saved = userService.createUser(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userService.getUserByEmail(request.getEmail());
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        RefreshToken rotated = refreshTokenService.rotateRefreshToken(refreshToken);
        User user = rotated.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(
                accessToken,
                rotated.getToken(),
                jwtService.getAccessTokenExpirationSeconds());
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                jwtService.getAccessTokenExpirationSeconds());
    }
}
