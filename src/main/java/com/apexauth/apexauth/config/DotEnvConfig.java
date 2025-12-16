package com.apexauth.apexauth.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    try {
      Dotenv dotenv = Dotenv.configure()
          .ignoreIfMissing()
          .load();

      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      Map<String, Object> envMap = new HashMap<>();

      dotenv.entries().forEach(entry -> {
        envMap.put(entry.getKey(), entry.getValue());
      });

      environment.getPropertySources()
          .addFirst(new MapPropertySource("dotenvProperties", envMap));

    } catch (Exception e) {
      // If .env file is not found, continue (useful for production with real env vars)
      System.out.println("No .env file found, using system environment variables");
    }
  }
}
