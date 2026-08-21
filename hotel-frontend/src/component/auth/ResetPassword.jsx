import React, { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import "../../styles/password-recovery.css";

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const navigate = useNavigate();

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      const response = await ApiService.resetPassword({
        token: token,
        newPassword: newPassword,
      });

      setMessage(
        response.message ||
          "Password updated successfully!"
      );

      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Invalid or expired token."
      );
    }
  };

  return (
    <div className="password-recovery-page">
      <div className="password-recovery-background">
        <div className="password-recovery-card">

          <h2>Create New Password</h2>

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
              <label htmlFor="newPassword">
                New Password:
              </label>

              <input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) =>
                  setNewPassword(e.target.value)
                }
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">
                Confirm Password:
              </label>

              <input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) =>
                  setConfirmPassword(e.target.value)
                }
                required
              />
            </div>

            <button type="submit">
              Reset Password
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

export default ResetPassword;
