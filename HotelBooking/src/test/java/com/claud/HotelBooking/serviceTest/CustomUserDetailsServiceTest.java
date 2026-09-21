package com.claud.HotelBooking.serviceTest;

import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.UserRepository;
import com.claud.HotelBooking.security.AuthUser;
import com.claud.HotelBooking.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.CUSTOMER);
    }

    @Test
    void loadUserByUsername_shouldReturnAuthUser_whenUserExists() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername(
                        "test@example.com"
                );

        assertNotNull(result);
        assertInstanceOf(AuthUser.class, result);

        assertEquals(
                "test@example.com",
                result.getUsername()
        );

        assertEquals(
                "password123",
                result.getPassword()
        );

        assertEquals(
                UserRole.CUSTOMER.name(),
                result.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(
                        "unknown@example.com"
                )
        );

        verify(userRepository).findByEmail("unknown@example.com");
    }
}