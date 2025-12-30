package com.apexauth.apexauth.config;

import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    @Profile({"mysql", "prod"})  // Only run on production/deployed environments
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if demo users already exist
            if (userRepository.findByEmail("user@demo.com").isEmpty()) {
                // Create demo regular user
                User demoUser = new User(
                        "user@demo.com",
                        passwordEncoder.encode("demo123"),
                        "ROLE_USER"
                );
                demoUser.setUsername("demouser");
                userRepository.save(demoUser);
                System.out.println("✅ Created demo user: user@demo.com / demo123");
            }

            if (userRepository.findByEmail("admin@demo.com").isEmpty()) {
                // Create demo admin user
                User adminUser = new User(
                        "admin@demo.com",
                        passwordEncoder.encode("admin123"),
                        "ROLE_ADMIN"
                );
                adminUser.setUsername("demoadmin");
                userRepository.save(adminUser);
                System.out.println("✅ Created demo admin: admin@demo.com / admin123");
            }

            System.out.println("🎉 Demo accounts ready for testing!");
        };
    }
}
