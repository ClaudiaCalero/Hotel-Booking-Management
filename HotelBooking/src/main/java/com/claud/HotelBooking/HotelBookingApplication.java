package com.claud.HotelBooking;

import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.enums.NotificationType;
import com.claud.HotelBooking.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HotelBookingApplication implements CommandLineRunner {

	@Autowired
	private NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(HotelBookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		NotificationDTO notificationDTO = NotificationDTO.builder()
				.type(NotificationType.EMAIL)
				.recipient("cyltiainbox@gmail.com")
				.body("I'm testing this from a command line")
				.subject("Testing Email Sending")
				.build();
		notificationService.sendEmail(notificationDTO);
	}
}
