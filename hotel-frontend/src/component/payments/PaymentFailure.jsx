import React from "react";
import { useParams, useNavigate } from "react-router-dom";

const PaymentFailure = () => {
  const { bookingReference } = useParams();
  const navigate = useNavigate();

  return (
    <div className="payment-page">
      <div className="payment-form text-center-align">
        <h2 className="payment-status-title failure-border">
          Payment Failed
        </h2>
        
        <div className="amount-display failure-bg">
          <p className="status-alert-text text-dark-red">
            Your payment process for booking reference <strong>{bookingReference}</strong> was aborted or rejected by the bank.
          </p>
        </div>

        <p className="payment-status-instructions">
          Please check your card details, ensure you have sufficient balance and try executing the transaction again.
        </p>

        <div className="payment-actions-column">
          <button 
            type="button" 
            className="payment-button" 
            onClick={() => navigate("/profile")}
          >
            Go to My Bookings
          </button>
          
          <button 
            type="button" 
            className="payment-btn-secondary" 
            onClick={() => navigate("/rooms")}
          >
            Return to Explore Rooms
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentFailure;
