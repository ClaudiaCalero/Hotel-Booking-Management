package com.claud.HotelBooking.dtos;

import com.claud.HotelBooking.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {

    private Long id;

    private Integer roomNumber;

    private RoomType roomType;

    private BigDecimal pricePerNight;

    private Integer capacity;

    private String description;
    private List<String> imageUrls; //to hold more than 1 picture
    //private String imageUrl; //to hold 1 picture


}
