package com.apexauth.apexauth.security;

import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    // Now accepts email, username, OR phone number
    User user = userRepository.findByIdentifier(identifier)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail()) // Use email as principal
        .password(user.getPasswordHash())
        .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
        .accountExpired(false)
        .accountLocked(!user.isActive())
        .credentialsExpired(false)
        .disabled(!user.isActive())
        .build();
  }
}
