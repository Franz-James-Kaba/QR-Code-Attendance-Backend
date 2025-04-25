package com.attendance_system.service;

import com.attendance_system.model.Token;
import com.attendance_system.model.User;
import com.attendance_system.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
    }

    @Test
    void testGenerateAndSaveToken_Success() {
        // Act
        String tokenValue = tokenService.generateAndSaveToken(user);

        // Assert
        assertNotNull(tokenValue);
        assertEquals(6, tokenValue.length());
        assertTrue(tokenValue.matches("\\d{6}"));

        // Verify that the token is saved with correct properties
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        Token savedToken = tokenCaptor.getValue();

        assertEquals(tokenValue, savedToken.getToken());
        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getCreatedAt());
        assertNotNull(savedToken.getExpiresAt());

        // Check that expiresAt is 15 minutes after createdAt (allowing a 1-second margin)
        long diff = java.time.Duration.between(savedToken.getCreatedAt(), savedToken.getExpiresAt()).toMinutes();
        assertEquals(15, diff);
    }

    @Test
    void testGenerateAndSaveToken_TokensAreRandom() {
        // Act
        String token1 = tokenService.generateAndSaveToken(user);
        String token2 = tokenService.generateAndSaveToken(user);

        // Assert
        assertNotEquals(token1, token2, "Tokens should be random and not equal");
    }
}
