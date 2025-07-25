package com.claud.HotelBooking.services.impl;


import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.entities.Notification;
import com.claud.HotelBooking.enums.NotificationType;
import com.claud.HotelBooking.repositories.NotificationRepository;
import com.claud.HotelBooking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Inside send email");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(notificationDTO.getRecipient());
        simpleMailMessage.setSubject(notificationDTO.getSubject());
        simpleMailMessage.setText(notificationDTO.getBody());

        javaMailSender.send(simpleMailMessage);

        //Save to db
        Notification notificationToSave = Notification.builder()
        .recipient(notificationDTO.getRecipient())
        .subject(notificationDTO.getSubject())
        .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(notificationToSave);
    }

    @Override
    public void sendSms() {

    }

    @Override
    public void sendWhatsapp() {

    }
}
