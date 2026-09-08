import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [bookings, setBookings] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
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

    fetchUserProfile();
  }, []);

  const handleLogout = () => {
    ApiService.logout();
    navigate("/home");
  };

  const handleEditProfile = () => {
    navigate("/edit-profile");
  };

    return (
    <div className="profile-page-container">
      <div className="profile-background-wrapper">
        
        <div className="profile-card-content">
          {user && <h1 className="welcome-message">Welcome, {user.firstName}</h1>}
          
          <div className="profile-actions">
            <button className="profile-button edit-profile-button" onClick={handleEditProfile}>
              Edit Profile
            </button>
            <button className="profile-button logout-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
          
          {error && <p className="error-message">{error}</p>}
          
          {user && (
            <div className="profile-details">
              <h3>My Profile Details</h3>
              <p><strong>Email:</strong> {user.email}</p>
              <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
            </div>
          )}
          
          <div className="bookings-inner-card">
            <h3>My Booking History</h3>
            <div className="booking-list">
              {bookings && Array.isArray(bookings) && bookings.length > 0 ? (
                bookings.map((booking) => (
                  <div key={booking.id} className="booking-item">
                    <p><strong>Booking Code:</strong> {booking.bookingReference}</p>
                    <p><strong>Check-in Date:</strong> {booking.checkInDate}</p>
                    <p><strong>Check-out Date:</strong> {booking.checkOutDate}</p>
                    <p><strong>Payment Status:</strong> {booking.paymentStatus}</p>
                    <p><strong>Booking Status:</strong> {booking.bookingStatus}</p>
                    <p><strong>Amount:</strong> {booking.totalPrice}</p>
                    <p><strong>Room Number:</strong> {booking.room?.roomNumber}</p>
                    <p><strong>Room Type:</strong> {booking.room?.type || booking.room?.roomType}</p>
                    {booking.room?.imageUrl && (
                      <img src={booking.room.imageUrl} alt="Room" className="room-photo" />
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
