import React, { useState } from "react";
import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";

const PaymentForm = ({
  clientSecret,
  amount,
  onPaymentSuccess,
  onPaymentError,
}) => {
  const stripe = useStripe();
  const element = useElements();

  const [error, setError] = useState(null);
  const [succeeded, setSucceeded] = useState(false);
  const [processing, setProcessing] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (processing) return;

    setProcessing(true);
    setError(null);

    //FULL SIMULATION SHORT-CIRCUIT (Ideal for bypassing CVC or Postal Code errors):
// Force the system to process a successful fake flow without querying Stripe's strict APIs.
    console.log(
      "🛠️ Gateway in forced simulation mode: Processing transaction...",
    );

    setTimeout(() => {
      // FORCED SUCCESS PATH: Sends the user directly to PaymentSuccess.
      onPaymentSuccess("ch_mock_budapest_trans_9999");
      setSucceeded(true);
      setProcessing(false);

      // FORCED FAILURE LINE: Uncomment the two lines below if you want to test PaymentFailure
      // onPaymentError({ message: "Simulated Error: Card was declined by the bank." });
      // setProcessing(false);
    }, 1500);

    /* 
        // --------------------------------------------------------------------------
        // --- Real flow temporarily commented out to prevent crashes caused by CVC ----
        // --------------------------------------------------------------------------
        try {
            const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                payment_method: {
                    card: element.getElement(CardElement)
                },
            });
            
            console.log("PAYMENT INTENT REAL RECIBIDO: ", paymentIntent);

            if (stripeError) {
                setError(stripeError.message);
                setProcessing(false);
                onPaymentError(stripeError); 
                console.log("Error inside PaymentForm is: " + stripeError.message);
            } else if (paymentIntent && paymentIntent.status === "succeeded") {
                console.log("PaymentForm is successful: " + paymentIntent.id);
                setSucceeded(true);
                setProcessing(false);
                onPaymentSuccess(paymentIntent.id); 
            }
        } catch (err) {
            console.error("Unexpected crash:", err);
            onPaymentError({ message: "Transaction timeout." });
            setProcessing(false);
        }
        */
  };

  return (
    <div className="payment-form">
      <h3>Complete Your Payment</h3>
      <div className="amount-display">
        <strong>Amount to Pay: ${parseFloat(amount).toFixed(2)}</strong>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="card-element-container">
          <CardElement />
        </div>

        <button className="payment-button" disabled={processing} type="submit">
          {processing ? "Processing..." : "Pay Now"}
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}
      {succeeded && (
        <p className="success-message">
          Payment Succeeded: Thank you for your booking.
        </p>
      )}
    </div>
  );
};

export default PaymentForm;
