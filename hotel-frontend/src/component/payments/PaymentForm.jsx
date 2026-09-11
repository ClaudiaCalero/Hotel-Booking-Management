import React, { useState } from "react"; 
import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js"; 

const PaymentForm = ({ clientSecret, amount, onPaymentSuccess, onPaymentError }) => {
    const stripe = useStripe();
    const element = useElements();

    const [error, setError] = useState(null);
    const [succeeded, setSucceeded] = useState(false);
    const [processing, setProcessing] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!stripe || !element || processing) return;

        setProcessing(true); 

        const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
            payment_method: {
                card: element.getElement(CardElement)
            },
        });
        
        console.log("PAYMENT IS: ", paymentIntent);

        if (error) {
            setError(error.message);
            setProcessing(false);
            onPaymentError(error); 
            console.log("Error inside PaymentForm is: " + error.message);
        } else if (paymentIntent.status === "succeeded") {
            console.log("PaymentForm is successful: " + paymentIntent.id);
            setSucceeded(true);
            setProcessing(false);
            onPaymentSuccess(paymentIntent.id); 
        }
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

                <button className="payment-button" disabled={processing || !stripe} type="submit">
                    {processing ? "Processing..." : "Pay Now"}
                </button>
            </form>

            {error && <p className="error-message">{error}</p>}
            {succeeded && <p className="success-message">Payment Succeeded: Thank you for your booking.</p>}
        </div>
    );
};

export default PaymentForm;
