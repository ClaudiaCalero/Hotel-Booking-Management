package com.claud.HotelBooking.payments.stripe;

import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.PaymentRepository;
import com.claud.HotelBooking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret.key}")
    private String secretKey;

}
