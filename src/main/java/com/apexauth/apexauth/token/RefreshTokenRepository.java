package com.apexauth.apexauth.token;

import com.apexauth.apexauth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  @Modifying
  @Query("DELETE FROM RefreshToken t WHERE t.user = :user")
  void deleteByUser(User user);

  @Modifying
  @Query("DELETE FROM RefreshToken t WHERE t.expiryDate < CURRENT_TIMESTAMP")
  void deleteExpiredTokens();
}
