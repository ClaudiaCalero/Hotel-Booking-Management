package com.claud.HotelBooking.securityTest;

import com.claud.HotelBooking.security.AuthFilter;
import com.claud.HotelBooking.security.CustomUserDetailsService;
import com.claud.HotelBooking.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldContinue_whenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        authFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void doFilterInternal_shouldContinue_whenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        authFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenTokenIsValid()
            throws ServletException, IOException {

        String token = "valid-token";
        String email = "test@example.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(email);

        when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtUtils.isTokenValid(token, userDetails))
                .thenReturn(true);

        when(userDetails.getAuthorities())
                .thenReturn(List.of());

        authFilter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);

        assertInstanceOf(
                UsernamePasswordAuthenticationToken.class,
                authentication
        );

        assertEquals(
                userDetails,
                authentication.getPrincipal()
        );

        verify(jwtUtils).getUsernameFromToken(token);
        verify(customUserDetailsService)
                .loadUserByUsername(email);
        verify(jwtUtils)
                .isTokenValid(token, userDetails);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenTokenIsInvalid()
            throws ServletException, IOException {

        String token = "invalid-token";
        String email = "test@example.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(email);

        when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtUtils.isTokenValid(token, userDetails))
                .thenReturn(false);

        authFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(jwtUtils).getUsernameFromToken(token);
        verify(customUserDetailsService)
                .loadUserByUsername(email);
        verify(jwtUtils)
                .isTokenValid(token, userDetails);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotReplaceExistingAuthentication()
            throws ServletException, IOException {

        String token = "valid-token";
        String email = "test@example.com";

        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(email);

        authFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtUtils).getUsernameFromToken(token);

        verifyNoInteractions(customUserDetailsService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldContinue_whenTokenProcessingThrowsException()
            throws ServletException, IOException {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtils.getUsernameFromToken(token))
                .thenThrow(new RuntimeException("Invalid JWT"));

        assertDoesNotThrow(() ->
                authFilter.doFilter(
                        request,
                        response,
                        filterChain
                )
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(jwtUtils).getUsernameFromToken(token);

        verify(filterChain).doFilter(request, response);
    }
}