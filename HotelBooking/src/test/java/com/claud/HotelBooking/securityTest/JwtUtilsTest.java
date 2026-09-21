package com.claud.HotelBooking.securityTest;

import com.claud.HotelBooking.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        ReflectionTestUtils.setField(
                jwtUtils,
                "secreteJwtString",
                "mySecretKeyForJwtTesting12345678901234567890"
        );

        ReflectionTestUtils.invokeMethod(jwtUtils, "init");
    }

    @Test
    void generateToken_shouldGenerateValidToken() {
        String email = "test@example.com";

        String token = jwtUtils.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void getUsernameFromToken_shouldReturnEmail() {
        String email = "test@example.com";

        String token = jwtUtils.generateToken(email);

        String username = jwtUtils.getUsernameFromToken(token);

        assertEquals(email, username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForMatchingUser() {
        String email = "test@example.com";

        String token = jwtUtils.generateToken(email);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);

        boolean result = jwtUtils.isTokenValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        String token = jwtUtils.generateToken("test@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("other@example.com");

        boolean result = jwtUtils.isTokenValid(token, userDetails);

        assertFalse(result);
    }

    @Test
    void getUsernameFromToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(
                Exception.class,
                () -> jwtUtils.getUsernameFromToken(invalidToken)
        );
    }

    @Test
    void isTokenValid_shouldThrowExceptionForInvalidToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        assertThrows(
                Exception.class,
                () -> jwtUtils.isTokenValid(
                        "invalid.jwt.token",
                        userDetails
                )
        );
    }
}