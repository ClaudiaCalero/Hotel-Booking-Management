package com.claud.HotelBooking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String token) {

        // CHange port 3000 for the one you use in your front
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cld.d.clr@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Password recovery - Onyx Crown Hotel");

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