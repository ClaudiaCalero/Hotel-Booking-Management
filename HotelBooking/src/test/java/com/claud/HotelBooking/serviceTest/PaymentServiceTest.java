package com.claud.HotelBooking.serviceTest;

import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.entities.Booking;
import com.claud.HotelBooking.entities.PaymentEntity;
import com.claud.HotelBooking.enums.PaymentStatus;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.payments.stripe.PaymentService;
import com.claud.HotelBooking.payments.stripe.dto.PaymentRequest;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.PaymentRepository;
import com.claud.HotelBooking.services.NotificationService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    class PaymentServiceTest {

        @Mock
        private BookingRepository bookingRepository;

        @Mock
        private PaymentRepository paymentRepository;

        @Mock
        private NotificationService notificationService;

        private PaymentService paymentService;

        @BeforeEach
        void setUp() {
            paymentService = new PaymentService(
                    bookingRepository,
                    paymentRepository,
                    notificationService
            );
            ReflectionTestUtils.setField(paymentService, "secreteKey", "sk_test_dummy");
        }

        private Booking pendingBooking() {
            return Booking.builder()
                    .id(1L)
                    .bookingReference("REF123")
                    .totalPrice(new BigDecimal("300.00"))
                    .paymentStatus(PaymentStatus.PENDING)
                    .guestEmail("guest@test.com")
                    .build();
        }

        private PaymentRequest request(boolean success, String transactionId, String amount) {
            PaymentRequest request = new PaymentRequest();
            request.setBookingReference("REF123");
            request.setAmount(new BigDecimal(amount));
            request.setTransactionId(transactionId);
            request.setSuccess(success);
            request.setFailureReason(success ? null : "Card declined");
            return request;
        }

        // ---------------------------------------------------------
        // Demo mode (verification with Stripe disabled)
        // ---------------------------------------------------------

        @Test
        void demoMode_successfulPayment_shouldCompleteBookingUsingBookingTotal() {
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            // the client reports a ridiculous amount: it must be ignored
            paymentService.updatePaymentBooking(request(true, "tx_1", "0.01"));

            assertEquals(PaymentStatus.COMPLETED, booking.getPaymentStatus());
            verify(bookingRepository).save(booking);

            ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
            verify(paymentRepository).save(captor.capture());
            assertEquals(new BigDecimal("300.00"), captor.getValue().getAmount());
            assertEquals(PaymentStatus.COMPLETED, captor.getValue().getPaymentStatus());

            verify(notificationService).sendEmail(any(NotificationDTO.class));
        }

        @Test
        void demoMode_failedPayment_shouldMarkBookingAsFailed() {
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            paymentService.updatePaymentBooking(request(false, "", "300.00"));

            assertEquals(PaymentStatus.FAILED, booking.getPaymentStatus());

            ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
            verify(paymentRepository).save(captor.capture());
            assertEquals(PaymentStatus.FAILED, captor.getValue().getPaymentStatus());
            assertEquals("Card declined", captor.getValue().getFailureReason());
        }

        @Test
        void alreadyPaidBooking_shouldNotBeTouchedAgain() {
            Booking booking = pendingBooking();
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            paymentService.updatePaymentBooking(request(false, "", "300.00"));

            assertEquals(PaymentStatus.COMPLETED, booking.getPaymentStatus());
            verify(paymentRepository, never()).save(any(PaymentEntity.class));
            verify(bookingRepository, never()).save(any(Booking.class));
            verify(notificationService, never()).sendEmail(any(NotificationDTO.class));
        }

        @Test
        void unknownBooking_shouldThrowNotFound() {
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    NotFoundException.class,
                    () -> paymentService.updatePaymentBooking(request(true, "tx_1", "300.00"))
            );

            verify(paymentRepository, never()).save(any(PaymentEntity.class));
        }

        // ---------------------------------------------------------
        // Verification with Stripe enabled
        // ---------------------------------------------------------

        @Test
        void verifiedMode_paymentSucceededInStripe_shouldCompleteBooking() throws Exception {
            ReflectionTestUtils.setField(paymentService, "verifyWithStripe", true);
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            PaymentIntent intent = mock(PaymentIntent.class);
            when(intent.getStatus()).thenReturn("succeeded");
            when(intent.getMetadata()).thenReturn(Map.of("bookingReference", "REF123"));
            when(intent.getAmountReceived()).thenReturn(30000L);

            try (MockedStatic<PaymentIntent> stripe = mockStatic(PaymentIntent.class)) {
                stripe.when(() -> PaymentIntent.retrieve("pi_123")).thenReturn(intent);

                paymentService.updatePaymentBooking(request(true, "pi_123", "300.00"));
            }

            assertEquals(PaymentStatus.COMPLETED, booking.getPaymentStatus());
        }

        @Test
        void verifiedMode_clientClaimsSuccessButStripeSaysNo_shouldFail() throws Exception {
            ReflectionTestUtils.setField(paymentService, "verifyWithStripe", true);
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            PaymentIntent intent = mock(PaymentIntent.class);
            when(intent.getStatus()).thenReturn("requires_payment_method");

            try (MockedStatic<PaymentIntent> stripe = mockStatic(PaymentIntent.class)) {
                stripe.when(() -> PaymentIntent.retrieve("pi_123")).thenReturn(intent);

                // success = true is a lie from the client
                paymentService.updatePaymentBooking(request(true, "pi_123", "300.00"));
            }

            assertEquals(PaymentStatus.FAILED, booking.getPaymentStatus());
        }

        @Test
        void verifiedMode_amountPaidDoesNotMatchBooking_shouldFail() throws Exception {
            ReflectionTestUtils.setField(paymentService, "verifyWithStripe", true);
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            PaymentIntent intent = mock(PaymentIntent.class);
            when(intent.getStatus()).thenReturn("succeeded");
            when(intent.getMetadata()).thenReturn(Map.of("bookingReference", "REF123"));
            when(intent.getAmountReceived()).thenReturn(50L);

            try (MockedStatic<PaymentIntent> stripe = mockStatic(PaymentIntent.class)) {
                stripe.when(() -> PaymentIntent.retrieve("pi_123")).thenReturn(intent);

                paymentService.updatePaymentBooking(request(true, "pi_123", "300.00"));
            }

            assertEquals(PaymentStatus.FAILED, booking.getPaymentStatus());
        }

        @Test
        void verifiedMode_paymentOfAnotherBooking_shouldFail() throws Exception {
            ReflectionTestUtils.setField(paymentService, "verifyWithStripe", true);
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            PaymentIntent intent = mock(PaymentIntent.class);
            when(intent.getStatus()).thenReturn("succeeded");
            when(intent.getMetadata()).thenReturn(Map.of("bookingReference", "OTHER999"));

            try (MockedStatic<PaymentIntent> stripe = mockStatic(PaymentIntent.class)) {
                stripe.when(() -> PaymentIntent.retrieve("pi_123")).thenReturn(intent);

                paymentService.updatePaymentBooking(request(true, "pi_123", "300.00"));
            }

            assertEquals(PaymentStatus.FAILED, booking.getPaymentStatus());
        }

        @Test
        void verifiedMode_fakeTransactionId_shouldFail() throws Exception {
            ReflectionTestUtils.setField(paymentService, "verifyWithStripe", true);
            Booking booking = pendingBooking();
            when(bookingRepository.findByBookingReference("REF123"))
                    .thenReturn(Optional.of(booking));

            try (MockedStatic<PaymentIntent> stripe = mockStatic(PaymentIntent.class)) {
                stripe.when(() -> PaymentIntent.retrieve("ch_mock_budapest_trans_9999"))
                        .thenThrow(new RuntimeException("No such payment_intent"));

                paymentService.updatePaymentBooking(
                        request(true, "ch_mock_budapest_trans_9999", "300.00"));
            }

            assertEquals(PaymentStatus.FAILED, booking.getPaymentStatus());
        }
    }