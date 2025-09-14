import React from "react";
import { useNavigate, NavLink } from "react-router-dom";
import ApiService from "../../service/ApiService";

function Navbar() {
    // corregido: isAuthenticated con la "u"
    const isAuthenticated = ApiService.isAuthenticated();
    const isCustomer = ApiService.isCustomer();
    const isAdmin = ApiService.isAdmin();

    const navigate = useNavigate();

    const handleLogout = () => {
        const isLogout = window.confirm("Are you sure you want to logout?");
        if (isLogout) {
            ApiService.logout();
            navigate("/home");
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <NavLink to="/home" className="navbar-logo">Onyx Crown Hotel</NavLink>
            </div>

            <ul className="navbar-ul">
                <li><NavLink to="/home" className={({ isActive }) => (isActive ? "active" : "")}>Home</NavLink></li>
                <li><NavLink to="/rooms" className={({ isActive }) => (isActive ? "active" : "")}>Rooms</NavLink></li>
                <li><NavLink to="/find-booking" className={({ isActive }) => (isActive ? "active" : "")}>Find My Bookings</NavLink></li>

                {isCustomer && (<li><NavLink to="/profile" className={({ isActive }) => (isActive ? "active" : "")}>Profile</NavLink></li>)}
                {isAdmin && (<li><NavLink to="/admin" className={({ isActive }) => (isActive ? "active" : "")}>Admin</NavLink></li>)}

                {!isAuthenticated && (<li><NavLink to="/login" className={({ isActive }) => (isActive ? "active" : "")}>Login</NavLink></li>)}
                {!isAuthenticated && (<li><NavLink to="/register" className={({ isActive }) => (isActive ? "active" : "")}>Register</NavLink></li>)}

                {isAuthenticated && <li onClick={handleLogout}>Logout</li>}
            </ul>
        </nav>
    );
}

export default Navbar;
