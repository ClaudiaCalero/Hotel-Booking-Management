import React from "react";
import { useParams, useNavigate } from "react-router-dom";

const PaymentSuccess = () => {
  const { bookingReference } = useParams();
  const navigate = useNavigate();

  return (
    <div className="payment-page">
      <div className="payment-form text-center-align">
        <h2 className="payment-status-title success-border">
          Payment Successful!
        </h2>
        
        <div className="amount-display">
          <p className="status-alert-text text-navy-blue">
            Thank you! Your payment for booking reference <strong>{bookingReference}</strong> was processed successfully.
          </p>
        </div>

        <p className="payment-status-instructions">
          A confirmation mail with your check-in vouchers has been dispatched. We look forward to welcoming you at The Grand Hotel Budapest.
        </p>

        <button 
          type="button" 
          className="payment-button" 
          onClick={() => navigate("/profile")}
        >
          View My Vouchers
        </button>
      </div>
    </div>
  );
};

export default PaymentSuccess;
