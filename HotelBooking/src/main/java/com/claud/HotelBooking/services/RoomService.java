package com.claud.HotelBooking.services;

import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.RoomDTO;
import com.claud.HotelBooking.enums.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class RoomService {

    Response addRoom(RoomDTO roomDTO, MultipartFile imageFile);

    Response updateRoom(RoomDTO roomDTO, MultipartFile imageFile);

    Response getAllRooms();

    Response getRoomById(Long id);

    Response deleteRoom(Long id);

    Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);

    List<RoomType> getAllRoomTypes();

    Response searchRoom();


}
