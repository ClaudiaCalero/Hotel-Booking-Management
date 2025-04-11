package com.claud.HotelBooking.dtos;

import com.claud.HotelBooking.enums.PaymentGateway;
import com.claud.HotelBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEntityDTO {

    private Long id;
    private BookingDTO bookingDTO;

    private String transactionId;

    private BigDecimal amount;

    private PaymentGateway paymentMethod; //Enum PGateaway -> PAYPAL, STRIPE, PAYSTACK, FLUTTERWAVE


    protected LocalDateTime paymentDate;

    private PaymentStatus status;

    private String bookingReference;
    private String failureReason;

    private String approvalLink; //paypal payment approval url

}
