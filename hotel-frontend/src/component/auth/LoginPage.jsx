import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import "../../styles/login.css";

const LoginPage = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  // NEW STATUS: Controls whether the recovery link should be displayed
  const [showForgotLink, setShowForgotLink] = useState(false);

  const navigate = useNavigate();
  const { state } = useLocation();

  const redirectPath = state?.from?.pathname || "/home";

  //handle input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { email, password } = formData;

    if (!email || !password) {
      setError("Please fill all input");
      setTimeout(() => setError(""), 5000);
      return;
    }

    try {
      setError(""); // Clean previous errors before we try
      setShowForgotLink(false); // Hide the previous link if exists

      const { status, token, role } = await ApiService.loginUser(formData);
      if (status === 200) {
        ApiService.saveToken(token);
        ApiService.saveRole(role);
        navigate(redirectPath, { replace: true });
      }
    } catch (error) {
      // Capture the specific error from the backend
      const serverMessage = error.response?.data?.message;

      if (serverMessage === "Password doesn't match") {
        setError("Password incorrect.");
        setShowForgotLink(true); // <--- Activate the recovery link
      } else if (serverMessage === "Email Not Found") {
        setError("This email is not registered.");
      } else {
        setError(serverMessage || error.message);
      }

      // timer to clear the message
      setTimeout(() => {
        setError("");
        setShowForgotLink(false);
      }, 5000);
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-background-wrapper">
        <div className="login-auth-container">
          {error && (
            <div className="error-alert-box">
              <span className="error-text-content">{error}</span>

              {showForgotLink && (
                <a href="/forgot-password" className="forgot-password-link">
                  Forgot your password? <strong>Reset it here</strong>
                </a>
              )}
            </div>
          )}

          <h2>Login</h2>
          <form onSubmit={handleSubmit}>
            {["email", "password"].map((field) => (
              <div className="form-group" key={field}>
                <label htmlFor={field}>
                  {field.charAt(0).toUpperCase() + field.slice(1)}:
                </label>
                <input
                  type={field === "password" ? "password" : "email"}
                  id={field}
                  name={field}
                  value={formData[field]}
                  onChange={handleChange}
                  required
                />
              </div>
            ))}

            <button type="submit" className="login-submit-btn">
              Login
            </button>
          </form>

          <div className="register-link">
            Don't have an account? <a href="/register">Register</a>
          </div>
        </div>
      </div>
    </div> /* ← Cierra el contenedor aquí */
  );
};

export default LoginPage;
