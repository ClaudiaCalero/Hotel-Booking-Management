package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.RoomDTO;
import com.claud.HotelBooking.entities.Room;
import com.claud.HotelBooking.enums.RoomType;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.RoomRepository;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    private static final String IMAGE_DIRECTORY_FRONTEND = "C:\\Users\\Cyltia\\HotelBooking\\hotel-frontend\\public\\images\\hotel\\Rooms\\";

    @Override
    public Response addRoom(RoomDTO roomDTO, List<MultipartFile> imageFiles) {
        Room roomToSave = Room.builder()
                .roomNumber(roomDTO.getRoomNumber())
                .type(roomDTO.getType())
                .pricePerNight(roomDTO.getPricePerNight())
                .capacity(roomDTO.getCapacity())
                .description(roomDTO.getDescription())
                .build();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> paths = new ArrayList<>();
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    paths.add(saveImageToFrontend(file));
                }
            }
            roomToSave.setImageUrls(paths);
        }

        roomRepository.save(roomToSave);

        return Response.builder()
                .status(200)
                .message("Room successfully added")
                .build();
    }

    @Override
    public Response updateRoom(RoomDTO roomDTO, List<MultipartFile> imageFiles) {
        Room existingRoom = roomRepository.findById(roomDTO.getId())
                .orElseThrow(() -> new NotFoundException("Room not found"));

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> paths = new ArrayList<>();
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    paths.add(saveImageToFrontend(file));
                }
            }
            existingRoom.setImageUrls(paths);
        }

        if (roomDTO.getRoomNumber() != null && roomDTO.getRoomNumber() >= 0) {
            existingRoom.setRoomNumber(roomDTO.getRoomNumber());
        }

        if (roomDTO.getPricePerNight() != null && roomDTO.getPricePerNight().compareTo(BigDecimal.ZERO) >= 0) {
            existingRoom.setPricePerNight(roomDTO.getPricePerNight());
        }

        if (roomDTO.getCapacity() != null && roomDTO.getCapacity() > 0) {
            existingRoom.setCapacity(roomDTO.getCapacity());
        }
        if (roomDTO.getType() != null) existingRoom.setType(roomDTO.getType());
        if (roomDTO.getDescription() != null) existingRoom.setDescription(roomDTO.getDescription());

        roomRepository.save(existingRoom);

        return Response.builder()
                .status(200)
                .message("Room updated successfully")
                .build();
    }

    @Override
    public Response getAllRooms() {
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<RoomDTO> roomDTOList = roomList.stream().map(room -> {
            return RoomDTO.builder()
                    .id(room.getId())
                    .roomNumber(room.getRoomNumber())
                    .type(room.getType())
                    .pricePerNight(room.getPricePerNight())
                    .capacity(room.getCapacity())
                    .description(room.getDescription())
                    .imageUrls(room.getImageUrls())
                    .build();
        }).collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("success")
                .rooms(roomDTOList)
                .build();
    }

    @Override
    public Response getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .room(roomDTO)
                .build();
    }

    @Override
    public Response deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("Room not found");
        }
        roomRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Room Deleted Successfully")
                .build();
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        try {
            List<Room> availableRooms = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);

            List<RoomDTO> availableRoomDTOs = availableRooms.stream().map(room -> {
                return RoomDTO.builder()
                        .id(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .type(room.getType())
                        .pricePerNight(room.getPricePerNight())
                        .capacity(room.getCapacity())
                        .description(room.getDescription())
                        .imageUrls(room.getImageUrls())
                        .build();
            }).collect(Collectors.toList());

            return Response.builder()
                    .status(200)
                    .message("Success")
                    .rooms(availableRoomDTOs)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error searching for available rooms: " + e.getMessage())
                    .build();
        }
    }


    @Override
    public List<RoomType> getAllRoomTypes() {
        return Arrays.stream(RoomType.values()).collect(Collectors.toList());
    }

    @Override
    public Response searchRoom(String input) {
        List<Room> roomList = roomRepository.searchRooms(input);
        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .rooms(roomDTOList)
                .build();
    }

    private String saveImageToFrontend(MultipartFile imageFile) {
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only Image files are allowed");
        }

        File directory = new File(IMAGE_DIRECTORY_FRONTEND);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIRECTORY_FRONTEND + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        return "/images/hotel/Rooms/" + uniqueFileName;
    }
}
