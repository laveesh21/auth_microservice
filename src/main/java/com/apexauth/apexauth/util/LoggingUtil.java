package com.apexauth.apexauth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggingUtil {

  private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);

  public static void logUserAction(String action, String details) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = (auth != null && auth.isAuthenticated()) 
        ? auth.getName() 
        : "anonymous";

    logger.info("📋 User Action - User: {} | Action: {} | Details: {}", 
        username, action, details);
  }

  public static void logSecurityEvent(String event, String details) {
    logger.warn("🔒 Security Event - Event: {} | Details: {}", event, details);
  }

  public static void logError(String context, Exception e) {
    logger.error("❌ Error in {} - Message: {}", context, e.getMessage(), e);
  }
}
