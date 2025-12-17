package com.apexauth.apexauth.token;

import com.apexauth.apexauth.user.User;
import com.apexauth.apexauth.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Value("${app.jwt.refresh-expiration-ms}")
  private Long refreshTokenDurationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                             UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Delete old refresh tokens for this user (one device policy)
    refreshTokenRepository.deleteByUser(user);

    RefreshToken refreshToken = new RefreshToken(
        UUID.randomUUID().toString(),
        user,
        Instant.now().plusMillis(refreshTokenDurationMs)
    );

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Transactional
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.isExpired() || token.isRevoked()) {
      refreshTokenRepository.delete(token);
      throw new RuntimeException("Refresh token expired or revoked. Please login again.");
    }
    return token;
  }

  @Transactional
  public void revokeToken(String token) {
    refreshTokenRepository.findByToken(token)
        .ifPresent(t -> {
          t.setRevoked(true);
          refreshTokenRepository.save(t);
        });
  }

  @Transactional
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteExpiredTokens();
  }
}
