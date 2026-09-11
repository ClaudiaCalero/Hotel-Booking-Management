import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import "../../styles/manage-bookings-popup.css"; 

const ManageBookingsPage = () => {
  const { bookingReference } = useParams();
  const navigate = useNavigate();
  const isAdmin = ApiService.isAdmin();

  const [bookingDetails, setBookingDetails] = useState({
    bookingReference: "",
    checkInDate: "",
    checkOutDate: "",
    totalPrice: "",
    paymentStatus: "",
    bookingStatus: "",
    roomNumber: "",
    roomType: "",
    userEmail: ""
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!isAdmin) {
      navigate("/home");
      return;
    }

    const fetchBookingData = async () => {
      try {
        const response = await ApiService.getBookingByReference(bookingReference);
        const b = response.booking;
        
        setBookingDetails({
          bookingReference: b.bookingReference || "",
          checkInDate: b.checkInDate || "",
          checkOutDate: b.checkOutDate || "",
          totalPrice: b.totalPrice || "",
          paymentStatus: b.paymentStatus || "",
          bookingStatus: b.bookingStatus || "",
          roomNumber: b.room?.roomNumber || b.roomNumber || "N/A",
          roomType: b.room?.type || b.roomType || "SINGLE",
          userEmail: b.user?.email || b.userEmail || "N/A"
        });
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error fetching booking details.");
      }
    };
    fetchBookingData();
  }, [bookingReference, isAdmin, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setBookingDetails((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const updatePayload = {
        paymentStatus: bookingDetails.paymentStatus,
        bookingStatus: bookingDetails.bookingStatus
      };
      
      await ApiService.updateBookingStatus(bookingReference, updatePayload);
      setSuccess("Booking updated successfully.");
      setTimeout(() => navigate("/admin/manage-bookings"), 2000);
    } catch (error) {
      setError(error.response?.data?.message || error.message || "Error updating booking.");
    }
  };

  const handleCancelBooking = async () => {
    if (window.confirm("Are you sure you want to CANCEL this booking? This action sets status to CANCELLED.")) {
      try {
        await ApiService.cancelBooking(bookingReference);
        setSuccess("Booking cancelled successfully.");
        setTimeout(() => navigate("/admin/manage-bookings"), 2000);
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error cancelling booking.");
      }
    }
  };

  if (!isAdmin) return null;

  return (
    <div className="booking-modal-overlay">
      <div className="booking-modal-card">
        
        <div className="booking-modal-header">
          <h2>Manage Booking Reference</h2>
          <button type="button" className="booking-modal-close-x" onClick={() => navigate("/admin/manage-bookings")}>×</button>
        </div>

        <div className="booking-modal-body">
          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}

          <form onSubmit={handleUpdate}>
            
            <div className="booking-modal-grid-row">
              <div className="booking-modal-group">
                <label>Booking Reference Code</label>
                <input type="text" value={bookingDetails.bookingReference} className="booking-modal-control disabled-view" disabled />
              </div>
              <div className="booking-modal-group">
                <label>Customer Email</label>
                <input type="text" value={bookingDetails.userEmail} className="booking-modal-control disabled-view" disabled />
              </div>
            </div>

            <div className="booking-modal-grid-row">
              <div className="booking-modal-group">
                <label>Check-In Date</label>
                <input type="text" value={bookingDetails.checkInDate} className="booking-modal-control disabled-view" disabled />
              </div>
              <div className="booking-modal-group">
                <label>Check-Out Date</label>
                <input type="text" value={bookingDetails.checkOutDate} className="booking-modal-control disabled-view" disabled />
              </div>
            </div>

            <div className="booking-modal-grid-row">
              <div className="booking-modal-group">
                <label>Assigned Room</label>
                <input type="text" value={`Room N° ${bookingDetails.roomNumber} (${bookingDetails.roomType})`} className="booking-modal-control disabled-view" disabled />
              </div>
              <div className="booking-modal-group">
                <label>Total Price Agreed</label>
                <input type="text" value={`$${bookingDetails.totalPrice}`} className="booking-modal-control disabled-view" style={{ color: '#b38e5d', fontWeight: 'bold' }} disabled />
              </div>
            </div>

            <div className="booking-modal-grid-row selectable-section-wrapper">
              <div className="booking-modal-group">
                <label>Update Payment Status</label>
                <select name="paymentStatus" value={bookingDetails.paymentStatus} onChange={handleChange} className="booking-modal-control focus-gold">
                  <option value="PENDING">PENDING</option>
                  <option value="SUCCESS">SUCCESS</option>
                  <option value="FAILED">FAILED</option>
                </select>
              </div>

              <div className="booking-modal-group">
                <label>Update Booking Status</label>
                <select name="bookingStatus" value={bookingDetails.bookingStatus} onChange={handleChange} className="booking-modal-control focus-gold">
                  <option value="BOOKED">BOOKED</option>
                  <option value="CONFIRMED">CONFIRMED</option>
                  <option value="CHECKED_IN">CHECKED_IN</option>
                  <option value="CHECKED_OUT">CHECKED_OUT</option>
                  <option value="CANCELLED">CANCELLED</option>
                </select>
              </div>
            </div>

            <div className="booking-modal-footer">
              <button type="submit" className="booking-btn booking-btn-update">Save System Changes</button>
              <button type="button" className="booking-btn booking-btn-cancel-action" onClick={handleCancelBooking}>Cancel This Booking</button>
              <button type="button" className="booking-btn booking-btn-close" onClick={() => navigate("/admin/manage-bookings")}>Close Window</button>
            </div>

          </form>
        </div>

      </div>
    </div>
  );
};

export default ManageBookingsPage;
