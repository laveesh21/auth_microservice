package com.apexauth.apexauth.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByPhoneNumber(String phoneNumber);

  // Flexible login: find by email OR username OR phone
  @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier OR u.phoneNumber = :identifier")
  Optional<User> findByIdentifier(@Param("identifier") String identifier);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByPhoneNumber(String phoneNumber);
}
