import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PaymentForm from "./PaymentForm";
import ApiService from "../../service/ApiService";
import "../../styles/payment.css";

const PaymentPage = () => {
  const { bookingReference, amount } = useParams();
  const [clientSecret, setClientSecret] = useState(null);
  const [error, setError] = useState(null);
  const [paymentStatus, setPaymentStatus] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchClientSecrete = async () => {
      try {
        const paymentData = { bookingReference, amount };
        const responseData = await ApiService.proceedForPayment(paymentData);

        console.log("RESPUESTA COMPLETA DEL BACKEND:", responseData);

        let secretText =
          responseData?.clientSecret ||
          responseData?.intentSecret ||
          responseData;

        if (typeof secretText === 'string') {
            secretText = secretText.replace(/['"]+/g, '').trim();
        }

        console.log("TEXTO DEL SECRETO DESEMPAQUETADO Y LIMPIO:", secretText);
        setClientSecret(secretText);
      } catch (error) {
        console.log(error);
        setError(error.response?.data?.message || error.message);
      }
    };

    fetchClientSecrete(); 
  }, [bookingReference, amount]);

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  const stripePromise = loadStripe(
    "pk_test_51QUUt8HB3OLSUETB41PkCNVZvXQdjyIJx4n7u9EHrMUH0j3R5VAJE76l1fnwQbC3OJlkPwQDIi0KwXGjdU1phB3s00ZJEZOlbv"
  );

  const handlePaymentStatus = async (
    paymentStatus,
    transactionId = "",
    failureReason = "",
  ) => {
    try {
      const paymentData = {
        bookingReference,
        amount,
        transactionId,
        success: paymentStatus === "succeeded",
        failureReason,
      };

      await ApiService.updateBookingPaymeent(paymentData);
      console.log("Payment status was updated");
    } catch (error) {
      console.log(error.message);
    }
  };

  return (
    <div className="payment-page">
      {clientSecret && (
        <Elements stripe={stripePromise}>
          <PaymentForm
            clientSecret={clientSecret}
            amount={amount}
            onPaymentSuccess={(transactionId) => {
              setPaymentStatus("succeeded");
              handlePaymentStatus("succeeded", transactionId);
              navigate(`/payment-success/${bookingReference}`);
            }}
            onPaymentError={(error) => {
              setPaymentStatus("failed");
              handlePaymentStatus("failed", "", error.message);
              navigate(`/payment-failed/${bookingReference}`);
            }}
          />
        </Elements>
      )}

      {paymentStatus && (
        <div className="payment-status-footer-text">
          Payment Status: {paymentStatus}
        </div>
      )}
    </div>
  );
};

export default PaymentPage;
