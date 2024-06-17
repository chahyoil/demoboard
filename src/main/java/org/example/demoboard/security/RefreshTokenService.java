package org.example.demoboard.security;


import org.example.demoboard.entity.RefreshToken;
import org.example.demoboard.repository.TokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/*
    * RefreshTokenService class is responsible for creating and saving refresh tokens.
 */
@Service
public class RefreshTokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;

    public RefreshTokenService(TokenRepository tokenRepository, JwtTokenProvider tokenProvider) {
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public RefreshToken createAndSaveRefreshToken(String username) {
        String refreshToken = tokenProvider.createRefreshToken(username);
        Date expiryDate = tokenProvider.getRefreshTokenExpiryDate();

        RefreshToken token = new RefreshToken();
        token.setUsername(username);
        token.setToken(refreshToken);
        token.setExpiryDate(expiryDate);

        return tokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void deleteByUsername(String username) {
        tokenRepository.deleteByUsername(username);
    }
}