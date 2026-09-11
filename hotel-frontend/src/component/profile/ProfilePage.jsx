import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import "../../styles/profile.css";

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [bookings, setBookings] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchUserProfile = async () => {
    try {
      const myProfileResponse = await ApiService.myProfile();
      console.log("Profile data received:", myProfileResponse);
      if (myProfileResponse && myProfileResponse.user) {
        setUser(myProfileResponse.user);
      } else {
        setUser(myProfileResponse);
      }

      const myBookingResponse = await ApiService.myBookings();
      console.log("Booking data received:", myBookingResponse);

      if (myBookingResponse && myBookingResponse.bookingList) {
        setBookings(myBookingResponse.bookingList);
      } else if (myBookingResponse && myBookingResponse.bookings) {
        setBookings(myBookingResponse.bookings);
      } else {
        setBookings(myBookingResponse);
      }
    } catch (error) {
      console.error("Profile page error:", error);
      setError(error.response?.data?.message || error.message);
    }
  };

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const handleCancelBooking = async (booking) => {
  const confirmCancel = window.confirm(`Do you wish to cancel the reservation with code ${booking.bookingReference}?`);
  if (!confirmCancel) return;

  try {
    const cleanedBooking = {
      id: booking.id,
      bookingReference: booking.bookingReference,
      bookingReferenceCode: booking.bookingReference, 
      checkInDate: booking.checkInDate,
      checkOutDate: booking.checkOutDate,
      totalPrice: booking.totalPrice,
      paymentStatus: booking.paymentStatus, 
      bookingStatus: "CANCELLED"            
    };

    console.log("Enviando datos simplificados al backend:", cleanedBooking);

    await ApiService.updateBooking(cleanedBooking);
    alert("Reserva cancelada correctamente.");
    
    fetchUserProfile(); 
  } catch (err) {
    const serverMessage = err.response?.data?.message || err.message;
    alert("Error del servidor (500): " + serverMessage);
  }
};


  const handleLogout = () => {
    ApiService.logout();
    navigate("/home");
  };

  const handleEditProfile = () => {
    navigate("/edit-profile");
  };

  const getBadgeClass = (status) => {
    if (!status) return "status-badge";
    const normalized = status.toUpperCase();
    if (
      normalized === "BOOKED" ||
      normalized === "CONFIRMED" ||
      normalized === "CHECKED_IN"
    )
      return "status-badge status-confirmed";
    if (normalized === "PENDING") return "status-badge status-pending";
    if (normalized === "CANCELLED" || normalized === "REJECTED")
      return "status-badge status-cancelled";
    return "status-badge";
  };

  return (
    <div className="profile-page-container">
      <div className="profile-background-wrapper">
        <div className="profile-card-content">
          {user && (
            <h1 className="welcome-message">Welcome, {user.firstName}</h1>
          )}

          <div className="profile-actions">
            <button
              className="profile-button edit-profile-button"
              onClick={handleEditProfile}>
              Edit Profile
            </button>
            <button
              className="profile-button logout-button"
              onClick={handleLogout}>
              Logout
            </button>
          </div>

          {error && <p className="error-message">{error}</p>}

          {user && (
            <div className="profile-details">
              <h3>My Profile Details</h3>
              <p>
                <strong>Email:</strong> {user.email}
              </p>
              <p>
                <strong>Phone Number:</strong> {user.phoneNumber}
              </p>
            </div>
          )}

          <div className="bookings-inner-card">
            <h3>My Booking History</h3>
            <div className="booking-list">
              {bookings && Array.isArray(bookings) && bookings.length > 0 ? (
                bookings.map((booking) => (
                  <div key={booking.id} className="booking-item">
                    <h4 className="booking-item-title">
                      Booking Code:{" "}
                      <span className="booking-code-text">
                        {booking.bookingReference}
                      </span>
                    </h4>

                    <div className="booking-info-row">
                      <strong>Room Number:</strong>
                      <span>{booking.room?.roomNumber || "N/A"}</span>
                    </div>

                    <div className="booking-info-row">
                      <strong>Room Type:</strong>
                      <span className="room-type-capitalize">
                        {booking.room?.type ||
                          booking.room?.roomType ||
                          "SINGLE"}
                      </span>
                    </div>

                    <div className="booking-info-row">
                      <strong>Check-in Date:</strong>
                      <span>{booking.checkInDate}</span>
                    </div>

                    <div className="booking-info-row">
                      <strong>Check-out Date:</strong>
                      <span>{booking.checkOutDate}</span>
                    </div>

                    <div className="booking-info-row">
                      <strong>Amount:</strong>
                      <span className="booking-price-amount">
                        ${booking.totalPrice}
                      </span>
                    </div>

                    <div className="booking-info-row">
                      <strong>Payment Status:</strong>
                      <span className={getBadgeClass(booking.paymentStatus)}>
                        {booking.paymentStatus || "PENDING"}
                      </span>
                    </div>

                    <div className="booking-info-row final-row">
                      <strong>Booking Status:</strong>
                      <span className={getBadgeClass(booking.bookingStatus)}>
                        {booking.bookingStatus}
                      </span>
                    </div>

                    {booking.bookingStatus !== "CANCELLED" && (
                      <div className="booking-action-row">
                        <button
                          type="button"
                          className="profile-button cancel-booking-button"
                          onClick={() => handleCancelBooking(booking)}>
                          Cancel Booking 
                        </button>
                      </div>
                    )}
                  </div>
                ))
              ) : (
                <p className="no-bookings-text">No bookings found.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
