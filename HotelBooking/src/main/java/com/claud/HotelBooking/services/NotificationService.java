package com.claud.HotelBooking.services;

import com.claud.HotelBooking.dtos.NotificationDTO;


public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

    void sendSms();

    void sendWhatsapp();
}
