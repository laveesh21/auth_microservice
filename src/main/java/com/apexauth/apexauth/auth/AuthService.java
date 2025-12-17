package com.apexauth.apexauth.auth;

import com.apexauth.apexauth.auth.dto.AuthResponse;
import com.apexauth.apexauth.auth.dto.LoginRequest;
import com.apexauth.apexauth.auth.dto.RegisterRequest;
import com.apexauth.apexauth.security.JwtService;
import com.apexauth.apexauth.token.RefreshToken;
import com.apexauth.apexauth.token.RefreshTokenService;
import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import com.apexauth.apexauth.util.LoggingUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public AuthService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      RefreshTokenService refreshTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
  }

  @Transactional
  public void register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      LoggingUtil.logSecurityEvent("Registration Failed", "Email already exists: " + request.getEmail());
      throw new IllegalArgumentException("Email already in use");
    }

    String hash = passwordEncoder.encode(request.getPassword());

    User user = new User(
        request.getEmail(),
        hash,
        "ROLE_USER");

    userRepository.save(user);
    LoggingUtil.logUserAction("User Registered", "Email: " + request.getEmail());
  }

  @Transactional
  public AuthResponse login(LoginRequest request) {
    try {
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          request.getIdentifier(),
          request.getPassword());
      authenticationManager.authenticate(authToken);
    } catch (BadCredentialsException ex) {
      LoggingUtil.logSecurityEvent("Login Failed", "Invalid credentials for: " + request.getIdentifier());
      throw new BadCredentialsException("Invalid credentials");
    }

    User user = userRepository.findByIdentifier(request.getIdentifier())
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

    String accessToken = jwtService.generateToken(
        user.getId(),
        user.getEmail(),
        user.getRole());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    LoggingUtil.logUserAction("User Logged In", "Email: " + user.getEmail() + " | Role: " + user.getRole());

    return new AuthResponse(accessToken, refreshToken.getToken());
  }

  @Transactional
  public AuthResponse refreshToken(String refreshTokenStr) {
    return refreshTokenService.findByToken(refreshTokenStr)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String accessToken = jwtService.generateToken(
              user.getId(),
              user.getEmail(),
              user.getRole());
          
          LoggingUtil.logUserAction("Token Refreshed", "Email: " + user.getEmail());
          
          return new AuthResponse(accessToken, refreshTokenStr);
        })
        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
  }
}
