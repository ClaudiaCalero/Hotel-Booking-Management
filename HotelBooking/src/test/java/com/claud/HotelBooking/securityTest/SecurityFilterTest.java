package com.claud.HotelBooking.securityTest;

import com.claud.HotelBooking.exceptions.CustomAccessDenialHandler;
import com.claud.HotelBooking.exceptions.CustomAuthenticationEntryPoint;
import com.claud.HotelBooking.security.AuthFilter;
import com.claud.HotelBooking.security.SecurityFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private AuthFilter authFilter;
    private CustomAccessDenialHandler customAccessDenialHandler;
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        authFilter = mock(AuthFilter.class);
        customAccessDenialHandler = mock(CustomAccessDenialHandler.class);
        customAuthenticationEntryPoint = mock(CustomAuthenticationEntryPoint.class);

        securityFilter = new SecurityFilter(
                authFilter,
                customAccessDenialHandler,
                customAuthenticationEntryPoint
        );
    }

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder =
                securityFilter.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(
                BCryptPasswordEncoder.class,
                passwordEncoder
        );
    }

    @Test
    void passwordEncoder_shouldEncodeAndMatchPassword() {
        PasswordEncoder passwordEncoder =
                securityFilter.passwordEncoder();

        String rawPassword = "password123";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);

        assertTrue(
                passwordEncoder.matches(
                        rawPassword,
                        encodedPassword
                )
        );

        assertFalse(
                passwordEncoder.matches(
                        "wrongPassword",
                        encodedPassword
                )
        );
    }

    @Test
    void multipartResolver_shouldReturnStandardServletMultipartResolver() {
        StandardServletMultipartResolver resolver =
                securityFilter.multipartResolver();

        assertNotNull(resolver);
        assertInstanceOf(
                StandardServletMultipartResolver.class,
                resolver
        );
    }

    @Test
    void authenticationManager_shouldReturnConfiguredAuthenticationManager()
            throws Exception {

        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);

        AuthenticationConfiguration authenticationConfiguration =
                mock(AuthenticationConfiguration.class);

        when(authenticationConfiguration.getAuthenticationManager())
                .thenReturn(authenticationManager);

        AuthenticationManager result =
                securityFilter.authenticationManager(
                        authenticationConfiguration
                );

        assertNotNull(result);
        assertSame(authenticationManager, result);

        verify(authenticationConfiguration)
                .getAuthenticationManager();
    }
}