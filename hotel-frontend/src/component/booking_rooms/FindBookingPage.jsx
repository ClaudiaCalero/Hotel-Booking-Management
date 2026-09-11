import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // 👈 Importamos el hook de navegación
import ApiService from '../../service/ApiService'; 
import '../../styles/find-booking.css';

const FindBookingPage = () => {
    const navigate = useNavigate(); // 👈 Inicializamos el navegador de React Router
    const [confirmationCode, setConfirmationCode] = useState(''); 
    const [bookingDetails, setBookingDetails] = useState(null); 
    const [error, setError] = useState(null); 

    // 🎯 CONTROL DE ACCESO ADMINISTRATIVO:
    useEffect(() => {
        // Si el usuario está logueado y es administrador, se le desvía a su panel
        if (ApiService.isAdmin && ApiService.isAdmin()) {
            navigate('/admin/manage-bookings'); // 👈 Ajusta esta ruta si tu router usa otra diferente
        }
    }, [navigate]);

    const handleSearch = async () => {
        if (!confirmationCode.trim()) {
            setError("Please Enter a booking confirmation code");
            setTimeout(() => setError(''), 5000);
            return;
        }
        try {
            const response = await ApiService.getBookingByReference(confirmationCode);
            setBookingDetails(response.booking);
            setError(null); 
        } catch (error) {
            setError(error.response?.data?.message || error.message);
            setTimeout(() => setError(''), 5000);
        }
    };

    return (
        <div className="find-booking-container">
            <div className="find-booking-card">
                <h2>Find Booking</h2>
                
                <div className="booking-search-box">
                    <input
                        required
                        type="text"
                        placeholder="Enter your booking confirmation code"
                        value={confirmationCode}
                        onChange={(e) => setConfirmationCode(e.target.value)}
                    />
                    <button onClick={handleSearch}>Find</button>
                </div>
                
                {error && <p className="error-message">{error}</p>}

                {bookingDetails && (
                    <div className="booking-details-result">
                        <h3>Booking Details</h3>
                        <p><strong>Booking Code:</strong> {bookingDetails.bookingReference}</p>
                        <p><strong>Check-in Date:</strong> {bookingDetails.checkInDate}</p>
                        <p><strong>Check-out Date:</strong> {bookingDetails.checkOutDate}</p>
                        <p><strong>Payment Status:</strong> {bookingDetails.paymentStatus}</p>
                        <p><strong>Amount:</strong> {bookingDetails.totalPrice}</p>
                        <p>
                            <strong>Booking Status:</strong>{' '}
                            <span className={`status-badge status-${bookingDetails.bookingStatus?.toLowerCase()}`}>
                                {bookingDetails.bookingStatus}
                            </span>
                        </p>

                        <hr className="divider" />
                        
                        <h3>Booker Details</h3>
                        <div className="details-group">
                            <p><strong>First Name:</strong> {bookingDetails.user?.firstName}</p>
                            <p><strong>Last Name:</strong> {bookingDetails.user?.lastName}</p>
                            <p><strong>Email:</strong> {bookingDetails.user?.email}</p>
                            <p><strong>Phone Number:</strong> {bookingDetails.user?.phoneNumber}</p>
                        </div>

                        <hr className="divider" />
                        
                        <h3>Room Details</h3>
                        <div className="details-group">
                            <p><strong>Room Number:</strong> {bookingDetails.room?.roomNumber}</p>
                            <p><strong>Room Type:</strong> {bookingDetails.room?.type || bookingDetails.room?.roomType}</p>
                            <p><strong>Room Capacity:</strong> {bookingDetails.room?.capacity}</p>
                            {bookingDetails.room?.imageUrl && (
                                <img 
                                    className="room-preview-image" 
                                    src={bookingDetails.room.imageUrl} 
                                    alt="Room Preview" 
                                />
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FindBookingPage;
