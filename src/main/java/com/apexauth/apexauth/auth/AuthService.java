package com.apexauth.apexauth.auth;

import com.apexauth.apexauth.auth.dto.AuthResponse;
import com.apexauth.apexauth.auth.dto.LoginRequest;
import com.apexauth.apexauth.auth.dto.RegisterRequest;
import com.apexauth.apexauth.security.JwtService;
import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public void register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already in use");
    }

    String hash = passwordEncoder.encode(request.getPassword());

    User user = new User(
        request.getEmail(),
        hash,
        "ROLE_USER");

    userRepository.save(user);
  }

  public AuthResponse login(LoginRequest request) {
    // Authenticate using identifier (email, username, or phone)
    try {
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          request.getIdentifier(),
          request.getPassword());
      authenticationManager.authenticate(authToken);
    } catch (BadCredentialsException ex) {
      throw new BadCredentialsException("Invalid credentials");
    }

    User user = userRepository.findByIdentifier(request.getIdentifier())
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

    String token = jwtService.generateToken(
        user.getId(),
        user.getEmail(),
        user.getRole());

    return new AuthResponse(token);
  }
}
