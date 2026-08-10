import React from "react";
import { useNavigate, NavLink } from "react-router-dom";
import ApiService from "../../service/ApiService";


function Navbar() {
    const isAuthenticated = ApiService.isAthenticated();
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

    // Función reutilizable para gestionar la clase activa
    const navLinkClass = ({ isActive }) => isActive ? "active" : "";

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <NavLink to="/home"> Onyx Crown Hotel </NavLink>
            </div>

            <ul className="navbar-ul">
                <li><NavLink to="/home" className={navLinkClass}>Home</NavLink></li>
                <li><NavLink to="/rooms" className={navLinkClass}>Rooms</NavLink></li>
                <li><NavLink to="/find-booking" className={navLinkClass}>Find My Bookings</NavLink></li>

                {isCustomer && <li><NavLink to="/profile" className={navLinkClass}>Profile</NavLink></li>}
                {isAdmin && <li><NavLink to="/admin" className={navLinkClass}>Admin</NavLink></li>}

                {!isAuthenticated && <li><NavLink to="/login" className={navLinkClass}>Login</NavLink></li>}
                {!isAuthenticated && <li><NavLink to="/register" className={navLinkClass}>Register</NavLink></li>}

                {isAuthenticated && <li onClick={handleLogout} style={{ cursor: "pointer" }}>Logout</li>}
            </ul>
        </nav>
    );
}

export default Navbar;
