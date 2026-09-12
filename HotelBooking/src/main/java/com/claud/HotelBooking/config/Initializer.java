package com.claud.HotelBooking.config;

import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole; 
import com.claud.HotelBooking.repositories.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@hotel.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Monsieur Gustave");
                admin.setLastName("H");
                admin.setEmail("admin@hotel.com");

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                admin.setPassword(passwordEncoder.encode("AdminHotel2026*"));

                admin.setPhoneNumber("000000000");

                admin.setRole(UserRole.ADMIN);

                userRepository.save(admin);
                System.out.println("✅ Hotel manager initialized successfully (admin@hotel.com).");
            }
        };
    }
}
