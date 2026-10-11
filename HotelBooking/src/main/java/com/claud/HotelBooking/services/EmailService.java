package com.claud.HotelBooking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    // The sender is the configured SMTP account (SPRING_MAIL_USERNAME)
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetPasswordEmail(String toEmail, String token) {

        String resetUrl = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password recovery - The Grand Hotel Budapest");

        String emailBody = """
            Hello,
            
            You have requested to reset your password.
            Please click the following link to change it (valid for 15 minutes):
            
            %s
            
            If you did not request this change, you can ignore this email.
            """.formatted(resetUrl);

        message.setText(emailBody);
        mailSender.send(message);
    }
}