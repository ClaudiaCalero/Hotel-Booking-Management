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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addRoom(@RequestParam Integer roomNumber,
                                            @RequestParam RoomType type,
                                            @RequestParam BigDecimal pricePerNight,
                                            @RequestParam Integer capacity,
                                            @RequestParam String description,
                                            @RequestParam MultipartFile imageFile){
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber(roomNumber)
                .type(type)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();

        return ResponseEntity.ok(roomService.addRoom(roomDTO,imageFile));

    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(@RequestParam (value = "roomNumber", required = false) Integer roomNumber,
                                            @RequestParam (value = "type", required = false) RoomType type,
                                            @RequestParam (value = "pricePerNight", required = false) BigDecimal pricePerNight,
                                            @RequestParam (value = "capacity", required = false) Integer capacity,
                                            @RequestParam (value = "description", required = false) String description,
                                            @RequestParam (value = "imageFile", required = false) MultipartFile imageFile,
                                            @RequestParam (value = "id", required = true) Long id){
        RoomDTO roomDTO = RoomDTO.builder()
                .id(id)
                .roomNumber(roomNumber)
                .type(type)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();

        return ResponseEntity.ok(roomService.updateRoom(roomDTO,imageFile));

    }


}
