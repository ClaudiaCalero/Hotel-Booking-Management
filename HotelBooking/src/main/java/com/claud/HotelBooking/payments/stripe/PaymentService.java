package com.claud.HotelBooking.payments.stripe;

import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.entities.Booking;
import com.claud.HotelBooking.entities.PaymentEntity;
import com.claud.HotelBooking.enums.NotificationType;
import com.claud.HotelBooking.enums.PaymentGateway;
import com.claud.HotelBooking.enums.PaymentStatus;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.payments.stripe.dto.PaymentRequest;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.PaymentRepository;
import com.claud.HotelBooking.services.NotificationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret.key}")
    private String secreteKey;

    /**
     * true  -> the result of the payment is verified with Stripe on the server.
     * false -> demo mode: the result reported by the frontend is trusted
     *          (the demo frontend simulates the payment without calling Stripe).
     */
    @Value("${payments.verify-with-stripe:false}")
    private boolean verifyWithStripe;

    public String createPaymentIntent(PaymentRequest paymentRequest) {
        log.info("Inside createPaymentIntent()");
        Stripe.apiKey = secreteKey;
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new NotFoundException("Payment already made for this booking");
        }

        try {
            // The amount always comes from the booking, never from the client
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(toCents(booking.getTotalPrice()))
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();

        } catch (Exception e) {
            log.error("Error creating payment intent", e);
            throw new RuntimeException("Error creating payment intent");
        }
    }

    @Transactional
    public void updatePaymentBooking(PaymentRequest paymentRequest) {

        log.info("Inside updatePaymentBooking()");
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));

        // A booking that is already paid is never touched again
        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            log.info("Booking {} is already paid: update ignored", bookingReference);
            return;
        }

        boolean success = paymentRequest.isSuccess();
        String failureReason = paymentRequest.getFailureReason();

        if (verifyWithStripe) {
            // The server decides: what the client says is not trusted
            String verificationError = verifyPaymentWithStripe(booking, paymentRequest.getTransactionId());
            success = verificationError == null;
            if (!success) {
                failureReason = verificationError;
            }
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(booking.getTotalPrice()); // the booking total, not the client's amount
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());

        if (!success) {
            payment.setFailureReason(failureReason);
        }

        paymentRepository.save(payment); // save payment to database

        // create and send notification
        String recipientEmail;

        if (booking.getUser() != null) {
            recipientEmail = booking.getUser().getEmail();
        } else {
            recipientEmail = booking.getGuestEmail();
        }

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(recipientEmail)
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        log.info("About to send notification inside updatePaymentBooking");

        if (success) {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking); // Update the booking

            notificationDTO.setSubject("Booking Payment Successful");
            notificationDTO.setBody(
                    "Congratulations!! Your payment for booking with reference: " + bookingReference + " is successful");
            notificationService.sendEmail(notificationDTO); // send email

        } else {

            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking); // Update the booking

            notificationDTO.setSubject("Booking Payment Failed");
            notificationDTO.setBody("Your payment for booking with reference: " + bookingReference
                    + " failed with reason: " + failureReason);
            notificationService.sendEmail(notificationDTO); // send email
        }

    }

    /**
     * Checks with Stripe that the payment really succeeded, that it belongs to this
     * booking and that the amount paid matches the booking total.
     *
     * @return null if everything is correct, otherwise the reason of the failure
     */
    private String verifyPaymentWithStripe(Booking booking, String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return "Missing transaction id";
        }

        try {
            Stripe.apiKey = secreteKey;
            PaymentIntent intent = PaymentIntent.retrieve(transactionId);

            if (!"succeeded".equals(intent.getStatus())) {
                return "Stripe payment status: " + intent.getStatus();
            }

            String reference = intent.getMetadata() != null
                    ? intent.getMetadata().get("bookingReference")
                    : null;
            if (!booking.getBookingReference().equals(reference)) {
                return "The payment does not belong to this booking";
            }

            Long received = intent.getAmountReceived();
            if (received == null || received.longValue() != toCents(booking.getTotalPrice())) {
                return "The amount paid does not match the booking total";
            }

            return null;

        } catch (Exception e) {
            log.error("Could not verify the payment with Stripe", e);
            return "The payment could not be verified";
        }
    }

    private long toCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
}
