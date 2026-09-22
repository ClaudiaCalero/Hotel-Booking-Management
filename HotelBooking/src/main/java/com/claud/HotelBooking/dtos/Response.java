package com.claud.HotelBooking.dtos;

import com.claud.HotelBooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //generic
    private int status;
    private String message;

    //for login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    private UserDTO user;
    private List<UserDTO> users;

    private BookingDTO booking;
    private List<BookingDTO> bookings;

    private RoomDTO room;
    private List<RoomDTO> rooms;

    private PaymentDTO payment;
    private List<PaymentDTO> payments;

    private NotificationDTO notification;
    private List<NotificationDTO> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();


}
