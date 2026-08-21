import React, { useState } from "react";
import ApiService from "../../service/ApiService";
import "../../styles/password-recovery.css";

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

      setMessage(
        response.message ||
          "A recovery link has been sent to your email."
      );
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "An error occurred. Please try again."
      );
    }
  };

  return (
    <div className="password-recovery-page">
      <div className="password-recovery-background">
        <div className="password-recovery-card">

          <h2>Recover Password</h2>

          <p className="password-recovery-description">
            Enter your email address and we will send you a link
            to reset your password.
          </p>

          {message && (
            <p className="success-message">
              {message}
            </p>
          )}

          {error && (
            <p className="error-message">
              {error}
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="email">
                Email Address:
              </label>

              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="Enter your registered email"
              />
            </div>

            <button type="submit">
              Send Recovery Link
            </button>
          </form>

          <p className="password-recovery-link">
            <a href="/login">
              Back to Login
            </a>
          </p>

        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;