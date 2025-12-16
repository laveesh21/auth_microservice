package com.apexauth.apexauth.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

  @NotBlank(message = "Username, email, or phone is required")
  private String identifier; // Can be email, username, or phone

  @NotBlank(message = "Password is required")
  private String password;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
