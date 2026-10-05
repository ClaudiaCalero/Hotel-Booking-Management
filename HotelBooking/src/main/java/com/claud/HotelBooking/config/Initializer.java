package com.claud.HotelBooking.config;

import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   @Value("${HOTEL_ADMIN_PASSWORD:}") String adminPassword) {
        return args -> {
            if (adminPassword.isBlank()) {
                System.out.println("HOTEL_ADMIN_PASSWORD not set: default admin not created.");
                return;
            }

            if (userRepository.findByEmail("admin@hotel.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Monsieur Gustave");
                admin.setLastName("H");
                admin.setEmail("admin@hotel.com");

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                admin.setPassword(passwordEncoder.encode(adminPassword));

                admin.setPhoneNumber("000000000");

                admin.setRole(UserRole.ADMIN);

                userRepository.save(admin);
                System.out.println("Hotel manager initialized successfully (admin@hotel.com).");
            }
        };
    }
}