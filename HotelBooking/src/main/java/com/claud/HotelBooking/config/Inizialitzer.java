package com.claud.HotelBooking.config;

package com.hotel.booking.config; // Ajusta a tu paquete real

import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.repositories.UserRepository;
import com.hotel.booking.model.User; // Ajusta a tus modelos reales
import com.hotel.booking.repository.UserRepository; // Ajusta a tus repositorios reales
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@hotel.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("M.");
                admin.setLastName("Gustave");
                admin.setEmail("admin@hotel.com");
                admin.setPassword(passwordEncoder.encode("AdminHotel2026*"));
                admin.setPhoneNumber("000000000");
                admin.setRole("ADMIN");

                userRepository.save(admin);
                System.out.println("✅ Hotel manager initialized successfully (admin@hotel.com).");
            }
        };
    }
}
