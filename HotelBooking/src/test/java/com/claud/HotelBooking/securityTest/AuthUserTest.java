package com.claud.HotelBooking.securityTest;

import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthUserTest {

    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.CUSTOMER);

        authUser = AuthUser.builder()
                .user(user)
                .build();
    }

    @Test
    void getAuthorities_shouldReturnUserRole() {
        assertNotNull(authUser.getAuthorities());
        assertEquals(1, authUser.getAuthorities().size());

        assertEquals(
                UserRole.CUSTOMER.name(),
                authUser.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );
    }

    @Test
    void getPassword_shouldReturnUserPassword() {
        assertEquals(
                "password123",
                authUser.getPassword()
        );
    }

    @Test
    void getUsername_shouldReturnUserEmail() {
        assertEquals(
                "test@example.com",
                authUser.getUsername()
        );
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertTrue(authUser.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertTrue(authUser.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertTrue(authUser.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        assertTrue(authUser.isEnabled());
    }
}