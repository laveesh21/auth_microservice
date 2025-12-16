package com.apexauth.apexauth.controller;

import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Any authenticated user can access their profile
   */
  @GetMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public User getProfile(Authentication authentication) {
    String email = authentication.getName();
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  /**
   * Users can update their own profile
   */
  @PutMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public String updateProfile(
      Authentication authentication,
      @RequestBody UpdateProfileRequest request) {
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getPhoneNumber() != null) {
      user.setPhoneNumber(request.getPhoneNumber());
    }

    userRepository.save(user);
    return "Profile updated successfully";
  }

  // DTO for profile update
  public static class UpdateProfileRequest {
    private String username;
    private String phoneNumber;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }
  }
}
