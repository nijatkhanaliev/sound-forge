package com.company.util;

import com.company.models.entity.RefreshToken;
import com.company.models.entity.User;
import com.company.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class RefreshTokenUtils {

    public RefreshToken generateRefreshToken(User user) {
        UUID id = UUID.randomUUID();
        String token = buildToken();
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plusDays(7);

        return RefreshToken.builder()
                .id(id)
                .token(token)
                .expiresAt(expiresAt)
                .issuedAt(issuedAt)
                .revoked(false)
                .user(user)
                .build();
    }

    ;

    private String buildToken() {
        byte[] tokenBytes = new byte[64];
        new SecureRandom().nextBytes(tokenBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public static void revokeAllToken(Long userId, RefreshTokenRepository refreshTokenRepository) {
        if (userId == null) {
            log.warn("revoking all token, userId is null");
            return;
        }

        List<RefreshToken> allToken = refreshTokenRepository.findAllByUserId(userId);

        allToken.stream()
                .filter((t) -> !t.getRevoked())
                .forEach((t) -> t.setRevoked(true));

        if(!allToken.isEmpty()){
            refreshTokenRepository.saveAll(allToken);
        }
    }

}
