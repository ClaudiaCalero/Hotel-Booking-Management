package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.entities.Notification;
import com.claud.HotelBooking.enums.NotificationType;
import com.claud.HotelBooking.repositories.NotificationRepository;
import com.claud.HotelBooking.services.NotificationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.sms-number}")
    private String twilioSmsNumber;

    @Value("${twilio.whatsapp-number}")
    private String twilioWhatsappNumber;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO) {

        log.info("Sending email to {}", notificationDTO.getRecipient());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(notificationDTO.getRecipient());
        simpleMailMessage.setSubject(notificationDTO.getSubject());
        simpleMailMessage.setText(notificationDTO.getBody());

        javaMailSender.send(simpleMailMessage);

        Notification notificationToSave = Notification.builder()
                .recipient(notificationDTO.getRecipient())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(notificationToSave);
    }

    @Override
    @Async
    public void sendSms(NotificationDTO notificationDTO) {

        log.info("Sending SMS to {}", notificationDTO.getPhoneNumber());

        Twilio.init(twilioAccountSid, twilioAuthToken);

        Message message = Message.creator(
                new PhoneNumber(notificationDTO.getPhoneNumber()),
                new PhoneNumber(twilioSmsNumber),
                notificationDTO.getBody()
        ).create();

        log.info("SMS sent successfully. SID: {}", message.getSid());

        Notification notificationToSave = Notification.builder()
                .recipient(notificationDTO.getPhoneNumber())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.SMS)
                .build();

        notificationRepository.save(notificationToSave);
    }

    @Override
    @Async
    public void sendWhatsapp(NotificationDTO notificationDTO) {

        log.info("Sending WhatsApp to {}", notificationDTO.getPhoneNumber());

        Twilio.init(twilioAccountSid, twilioAuthToken);

        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + notificationDTO.getPhoneNumber()),
                new PhoneNumber("whatsapp:" + twilioWhatsappNumber),
                notificationDTO.getBody()
        ).create();

        log.info("WhatsApp sent successfully. SID: {}", message.getSid());

        Notification notificationToSave = Notification.builder()
                .recipient(notificationDTO.getPhoneNumber())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.WHATSAPP)
                .build();

        notificationRepository.save(notificationToSave);
    }
}


