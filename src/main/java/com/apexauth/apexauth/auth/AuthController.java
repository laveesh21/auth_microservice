package com.apexauth.apexauth.auth;

import com.apexauth.apexauth.auth.dto.AuthResponse;
import com.apexauth.apexauth.auth.dto.LoginRequest;
import com.apexauth.apexauth.auth.dto.RegisterRequest;
import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

  public AuthController(AuthService authService,
      UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok().body("User registered");
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  // very simple /me for now (no JWT filter yet, so this is placeholder)
  @GetMapping("/me")
  public ResponseEntity<?> me(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body("No authenticated user");
    }
    Optional<User> userOpt = userRepository.findByEmail(principal.getName());
    return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
  }
}
