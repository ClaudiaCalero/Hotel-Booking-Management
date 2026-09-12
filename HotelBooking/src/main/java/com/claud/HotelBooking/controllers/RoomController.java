package com.claud.HotelBooking.controllers;

import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.RoomDTO;
import com.claud.HotelBooking.enums.RoomType;
import com.claud.HotelBooking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addRoom(
            @RequestParam Integer roomNumber,
            @RequestParam String type,
            @RequestParam BigDecimal pricePerNight,
            @RequestParam Integer capacity,
            @RequestParam String description,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ){
        RoomType resolvedType;
        try {
            resolvedType = RoomType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            resolvedType = RoomType.STANDARD_ROOM;
        }

        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber(roomNumber)
                .type(resolvedType)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();

        return ResponseEntity.ok(roomService.addRoom(roomDTO, imageFiles));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(
            @RequestParam (value = "roomNumber", required = false) Integer roomNumber,
            @RequestParam (value = "type", required = false) RoomType type,
            @RequestParam (value = "pricePerNight", required = false) BigDecimal pricePerNight,
            @RequestParam (value = "capacity", required = false) Integer capacity,
            @RequestParam (value = "description", required = false) String  description,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestParam (value = "id", required = true) Long id
    ){
        RoomDTO roomDTO = RoomDTO.builder()
                .id(id)
                .roomNumber(roomNumber)
                .type(type)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();

        return ResponseEntity.ok(roomService.updateRoom(roomDTO, imageFiles));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllRooms(){
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getRoomById(@PathVariable Long id){
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable Long id){
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }


    @GetMapping("/available")
    public ResponseEntity<Response> getAvailableRooms(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @RequestParam(required = false) String roomType
    ){
        if (checkInDate == null || checkOutDate == null) {
            return ResponseEntity.badRequest().build();
        }

        RoomType resolvedType = null;
        if (roomType != null && !roomType.trim().isEmpty()
                && !roomType.equalsIgnoreCase("all")
                && !roomType.equalsIgnoreCase("All Rooms")) {
            try {
                String sanitizedType = roomType.trim().toUpperCase().replace(" ", "_").replace("-", "_");
                resolvedType = RoomType.valueOf(sanitizedType);
            } catch (IllegalArgumentException e) {
                resolvedType = null;
            }
        }

        return ResponseEntity.ok(roomService.getAvailableRooms(checkInDate, checkOutDate, resolvedType));
    }


    @GetMapping("/types")
    public ResponseEntity<List<RoomType>> getAllRoomTypes(){
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchRoom(@RequestParam String input){
        return ResponseEntity.ok(roomService.searchRoom(input));
    }
}
