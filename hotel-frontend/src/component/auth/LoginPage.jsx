import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";

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

  const handleSubmit = async (e) =>{
    e.preventDefault()
    const {email, password} = formData;

    if (!email || !password) {
        setError("Please fill all input")
        setTimeout(() => setError(""), 5000);
        return;
    }

    try {
        setError(""); // Clean previous errors before we try 
        setShowForgotLink(false); // Hide the previous link if exists 
        
        const {status, token, role} = await ApiService.loginUser(formData);
        if (status === 200) {
            ApiService.saveToken(token)
            ApiService.saveRole(role)
            navigate(redirectPath, {replace: true})
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
  }

  return(
    <div className="auth-container">
        {/* Modify the error parragraf to include the link next to it */}
        {error && (
            <p className="error-message">
                {error}
                {showForgotLink && (
                    <a href="/forgot-password" style={{ marginLeft: '8px', color: '#007bff', textDecoration: 'underline' }}>
                        Forgot password?
                    </a>
                )}
            </p>
        )}

        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
            {["email", "password"].map(
                (field) => (
                    <div className="form-group" key={field}>
                        <label>{field.charAt(0).toLocaleUpperCase() + field.slice(1)}: </label>
                        <input type={field} 
                        name={field}
                        value={formData[field]}
                        onChange={handleChange}
                        required
                        />
                    </div>
                )
            )}
            <button type="submit">Login</button>
        </form>
        
        <p className="register-link"> Don't have an account? <a href="/register">Register</a></p>
    </div>
  )
};

export default LoginPage;
