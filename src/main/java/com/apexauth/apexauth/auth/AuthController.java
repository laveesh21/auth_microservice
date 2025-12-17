package com.apexauth.apexauth.auth;

import com.apexauth.apexauth.auth.dto.AuthResponse;
import com.apexauth.apexauth.auth.dto.LoginRequest;
import com.apexauth.apexauth.auth.dto.RefreshTokenRequest;
import com.apexauth.apexauth.auth.dto.RegisterRequest;
import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

  public AuthController(AuthService authService,
      UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
  })
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok().body("User registered");
  }

  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticate user and receive JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New access token generated"),
      @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
  })
  public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    AuthResponse response = authService.refreshToken(request.getRefreshToken());
    return ResponseEntity.ok(response);
  }

  // very simple /me for now (no JWT filter yet, so this is placeholder)
  @GetMapping("/me")
  @Operation(
      summary = "Get current user", 
      description = "Get authenticated user's profile",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User profile returned"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<?> me(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body("No authenticated user");
    }
    Optional<User> userOpt = userRepository.findByEmail(principal.getName());
    return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
  }
}
