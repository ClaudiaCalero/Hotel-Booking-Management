import React from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";

const PaymentSuccess = () => {

  const { bookingReference } = useParams();
  const navigate = useNavigate();

  const [searchParams] = useSearchParams();

  const isGuest =
    searchParams.get("guest") === "true" ||
    localStorage.getItem("guestBooking") === "true";

  const handleContinue = () => {

    if (isGuest) {

      localStorage.removeItem("guestBooking");

      navigate(
        `/find-booking?reference=${bookingReference}`
      );

    } else {

      navigate("/profile");

    }
  };

  return (
    <div className="payment-page">

      <div className="payment-form text-center-align">

        <h2 className="payment-status-title success-border">
          Payment Successful!
        </h2>

        <div className="amount-display">

          <p className="status-alert-text text-navy-blue">

            Thank you! Your payment for booking reference{" "}

            <strong>
              {bookingReference}
            </strong>{" "}

            was processed successfully.

          </p>

        </div>

        <p className="payment-status-instructions">

          A confirmation mail with your check-in vouchers
          has been dispatched. We look forward to welcoming
          you at The Grand Hotel Budapest.

        </p>

        <button
          type="button"
          className="payment-button"
          onClick={handleContinue}
        >

          {isGuest
            ? "Find My Booking"
            : "View My Vouchers"}

        </button>

      </div>

    </div>
  );
};

export default PaymentSuccess;