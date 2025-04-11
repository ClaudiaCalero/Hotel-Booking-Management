package com.claud.HotelBooking.dtos;

import com.claud.HotelBooking.enums.BookingStatus;
import com.claud.HotelBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)// When using BookingDTO as a request, it ignores missing fields. And when used as a response, @JsonInclude only ignores fields that are null.
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDTO {

    private Long id;

    private UserDTO user;

    private RoomDTO room;
    private Long roomId;

    private PaymentStatus paymentStatus;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal totalPrice;
    private String bookingReference;
    private LocalDateTime createdAt;

    private BookingStatus bookingStatus;

}
