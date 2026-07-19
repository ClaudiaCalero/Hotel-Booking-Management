import React, { useState } from "react";
import ApiService from "../../service/ApiService"; // Ajusta la ruta de tu ApiService si es necesario

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      const response = await ApiService.forgotPassword(email);
      // El backend devuelve un Response con el mensaje de éxito
      setMessage(response.message || "A recovery link has been sent to your email.");
    } catch (err) {
      setError(err.response?.data?.message || "An error occurred. Please try again.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Recover Password</h2>
      <p style={{ fontSize: '14px', color: '#666', marginBottom: '15px' }}>
        Enter your email address and we will send you a link to reset your password.
      </p>

      {message && <p className="success-message" style={{ color: 'green' }}>{message}</p>}
      {error && <p className="error-message" style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Email Address: </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="Enter your registered email"
          />
        </div>
        <button type="submit">Send Recovery Link</button>
      </form>
      
      <p className="register-link" style={{ marginTop: '15px' }}>
        <a href="/login">Back to Login</a>
      </p>
    </div>
  );
};

export default ForgotPassword;
