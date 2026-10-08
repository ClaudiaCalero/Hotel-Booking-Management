package com.claud.HotelBooking.serviceTest;

import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessDenied_forLoggedUser_shouldReturn403() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com",
                        "password",
                        List.of(new SimpleGrantedAuthority("CUSTOMER"))
                )
        );

        ResponseEntity<Response> response =
                handler.handleAccessDenied(new AccessDeniedException("Access Denied"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void accessDenied_forAnonymousUser_shouldReturn401() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                )
        );

        ResponseEntity<Response> response =
                handler.handleAccessDenied(new AccessDeniedException("Access Denied"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void unknownException_shouldReturn500WithoutExposingTheInternalMessage() {
        ResponseEntity<Response> response =
                handler.handleAllUnknowExceptions(new RuntimeException("boom: secret internal detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void illegalArgument_shouldReturn400WithItsMessage() {
        ResponseEntity<Response> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Only Image files are allowed"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Only Image files are allowed", response.getBody().getMessage());
    }
}