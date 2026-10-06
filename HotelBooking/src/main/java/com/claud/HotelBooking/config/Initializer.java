package com.claud.HotelBooking.config;

import com.claud.HotelBooking.entities.Room;
import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.RoomType;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.repositories.RoomRepository;
import com.claud.HotelBooking.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Configuration
public class Initializer {

    private static final String IMG = "/images/hotel/Rooms/";

    private static final Set<Integer> FEATURED = Set.of(101, 201);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   RoomRepository roomRepository,
                                   @Value("${HOTEL_ADMIN_PASSWORD:}") String adminPassword) {
        return args -> {
            createAdmin(userRepository, adminPassword);
            seedRooms(roomRepository);
        };
    }

    private void createAdmin(UserRepository userRepository, String adminPassword) {
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
    }

    private void seedRooms(RoomRepository roomRepository) {
        List<Room> samples = sampleRooms();

        if (roomRepository.count() == 0) {
            roomRepository.saveAll(samples);
            System.out.println("Sample rooms created.");
            return;
        }

        List<Room> existing = roomRepository.findAll();
        for (Room wanted : samples) {
            if (!FEATURED.contains(wanted.getRoomNumber())) {
                continue;
            }

            Room current = existing.stream()
                    .filter(r -> wanted.getRoomNumber().equals(r.getRoomNumber()))
                    .findFirst()
                    .orElse(null);

            if (current == null) {
                roomRepository.save(wanted);
            } else {
                current.setType(wanted.getType());
                current.setPricePerNight(wanted.getPricePerNight());
                current.setCapacity(wanted.getCapacity());
                current.setDescription(wanted.getDescription());
                current.setImageUrls(wanted.getImageUrls());
                roomRepository.save(current);
            }
        }
    }

    private List<Room> sampleRooms() {
        return List.of(
                room(101, RoomType.STANDARD_ROOM, "250", 1,
                        loadText("room-descriptions/room-101.txt"),
                        IMG + "single/placeholder-room1.jpg",
                        IMG + "single/2.jpg", IMG + "single/3.jpg", IMG + "single/6.jpg",
                        IMG + "single/7.jpg", IMG + "single/4.jpg", IMG + "single/9.jpg",
                        IMG + "single/8.jpg", IMG + "single/5.jpg"),

                room(201, RoomType.DELUXE_SUITE, "480", 2,
                        loadText("room-descriptions/room-201.txt"),
                        IMG + "double/placeholder-room2.jpg",
                        IMG + "double/double201.jpg",
                        IMG + "double/209bddb8b6e656a4b82e35e7e78da48e.jpg",
                        IMG + "double/0b287cec2b918506c5508112f4b4c97f.jpg",
                        IMG + "double/9cde575b54f819a0772e3f7eb6636e09.jpg",
                        IMG + "double/1654a651fbc954472f8104ee7b22cc29.jpg",
                        IMG + "double/7a3bfecc45346447f59b00e191ce1279.jpg",
                        IMG + "double/b893262a7630824b795b5b28c66da656.jpg",
                        IMG + "double/c530d521e704650e724da516c79cbce5.jpg"),
                room(202, RoomType.DELUXE_SUITE, "240", 2,
                        "A bright double suite with a sunlit living area and an arched marble shower.",
                        IMG + "double/798f9797cf4197a8a7b638fcf0db47f1.jpg",
                        IMG + "double/de2df63de260c3623ff6fb159a25271c.jpg",
                        IMG + "double/f31599150b8c058edc411117e671c4f3.jpg")
        );
    }

    private Room room(int number, RoomType type, String price, int capacity,
                      String description, String... images) {
        return Room.builder()
                .roomNumber(number)
                .type(type)
                .pricePerNight(new BigDecimal(price))
                .capacity(capacity)
                .description(description)
                .imageUrls(List.of(images))
                .build();
    }

    private String loadText(String path) {
        try (var in = new ClassPathResource(path).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
    }
}