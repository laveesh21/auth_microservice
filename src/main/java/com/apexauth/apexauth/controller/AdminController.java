package com.apexauth.apexauth.controller;

import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final UserRepository userRepository;

  public AdminController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Returns admin statistics (JSON data for frontend to display)
   */
  @GetMapping("/stats")
  @PreAuthorize("hasRole('ADMIN')")
  public Map<String, Object> getAdminStats() {
    long totalUsers = userRepository.count();
    long activeUsers = userRepository.findAll().stream()
        .filter(User::isActive)
        .count();

    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", totalUsers);
    stats.put("activeUsers", activeUsers);
    stats.put("inactiveUsers", totalUsers - activeUsers);
    return stats;
  }

  /**
   * Get all users - ADMIN only
   * Returns JSON array of users
   */
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Delete a user - ADMIN only
   */
  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Map<String, String> deleteUser(@PathVariable Long id) {
    userRepository.deleteById(id);
    Map<String, String> response = new HashMap<>();
    response.put("message", "User deleted successfully");
    response.put("userId", id.toString());
    return response;
  }
}
