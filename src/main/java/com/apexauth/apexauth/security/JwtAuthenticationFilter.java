package com.apexauth.apexauth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 1. Extract the Authorization header
    String authHeader = request.getHeader("Authorization");

    // 2. Check if header exists and starts with "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Extract the token (remove "Bearer " prefix)
    String token = authHeader.substring(7);

    try {
      // 4. Extract email from token
      String email = jwtService.extractEmail(token);

      // 5. If token has email and user is not already authenticated
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // 6. Load user details from database
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 7. Validate token
        if (jwtService.isTokenValid(token)) {
          // 8. Create authentication object
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // 9. Set authentication in security context
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (Exception ex) {
      // If token is invalid, just continue without authentication
      logger.error("JWT authentication failed: " + ex.getMessage());
    }

    // 10. Continue the filter chain
    filterChain.doFilter(request, response);
  }
}
