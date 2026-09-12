import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import "../../styles/manage-bookings.css";
import "../../styles/manage-bookings-popup.css";

const ManageBookingsPage = () => {
  const [bookings, setBookings] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [paymentStatus, setPaymentStatus] = useState("");
  const [bookingStatus, setBookingStatus] = useState("");

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const data = await ApiService.getAllBookings();

      console.log("Raw server response:", data);

      if (data && Array.isArray(data.bookings)) {
        setBookings(data.bookings);
      } else if (Array.isArray(data)) {
        setBookings(data);
      } else if (data && Array.isArray(data.bookingDTOList)) {
        setBookings(data.bookingDTOList);
      } else if (data && Array.isArray(data.bookingList)) {
        setBookings(data.bookingList);
      } else {
        console.warn(
          "Could not find a 'bookings' collection property in the server response.",
        );
        setBookings([]);
      }
      setLoading(false);
    } catch (err) {
      setError(err.message || "Error loading reservations.");
      setLoading(false);
    }
  };

  const handleManageClick = (booking, refCode, email) => {
    setSelectedBooking({
      ...booking,
      bookingReference:
        booking.bookingReference || booking.bookingReferenceCode || refCode,
      derivedRef: refCode,
      derivedEmail: email,
    });
    setPaymentStatus(booking.paymentStatus || "PENDING");
    setBookingStatus(booking.bookingStatus || "BOOKED");
    setIsModalOpen(true);
  };

  const handleSaveChanges = async () => {
    try {
      const updatedBooking = {
        id: selectedBooking.id,
        bookingReference: selectedBooking.bookingReference,
        bookingStatus: bookingStatus,
        paymentStatus: paymentStatus,
        checkInDate: selectedBooking.checkInDate,
        checkOutDate: selectedBooking.checkOutDate,
        numOfGuests: selectedBooking.numOfGuests || 1,
        roomId:
          selectedBooking.roomId ||
          (selectedBooking.room && selectedBooking.room.id),
      };

      await ApiService.updateBooking(updatedBooking);

      setIsModalOpen(false);
      fetchBookings();
      alert("Reservation updated successfully");
    } catch (err) {
      alert("Error updating reservation: " + err.message);
    }
  };

  const filteredBookings = bookings.filter((booking) => {
    const refCode = booking.bookingReference || "";
    const email =
      booking.customerEmail || (booking.user && booking.user.email) || "";
    return (
      refCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      email.toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  if (loading)
    return (
      <div className="gbh-loading-screen">
        <p>Loading Concierge System...</p>
      </div>
    );

  return (
    <div className="gbh-bookings-page-container">
      <div className="gbh-bookings-panel-wrapper">
        <div className="gbh-bookings-main-card">
          <div className="gbh-bookings-top-bar">
            <h2 className="gbh-bookings-main-title">General Bookings Log</h2>
            <input
              type="text"
              placeholder="Search by code or email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="gbh-bookings-search-input"
            />
          </div>
          {error && <p className="gbh-bookings-error-msg">{error}</p>}
          <div className="gbh-bookings-table-section">
            <div className="gbh-bookings-table-scroll">
              <table className="gbh-custom-data-table">
                <thead>
                  <tr>
                    <th>Reference Code</th>
                    <th>Customer Email</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBookings.map((booking, index) => {
                    const refCode =
                      booking.bookingReferenceCode ||
                      booking.bookingConfirmationCode ||
                      "REG-" + (booking.id || index);
                    const email =
                      booking.customerEmail ||
                      (booking.user && booking.user.email) ||
                      "guest@grandbudapest.com";

                    return (
                      <tr key={booking.id || index} className="gbh-table-row">
                        <td className="gbh-td-code">{refCode}</td>
                        <td>{email}</td>
                        <td>{booking.checkInDate}</td>
                        <td>{booking.checkOutDate}</td>
                        <td>
                          <span
                            className={`gbh-status-badge ${booking.bookingStatus === "BOOKED" ? "gbh-status-booked" : "gbh-status-cancelled"}`}
                          >
                            {booking.bookingStatus || "BOOKED"}
                          </span>
                        </td>
                        <td>
                          <button
                            type="button"
                            onClick={() =>
                              handleManageClick(booking, refCode, email)
                            }
                            className="gbh-btn-manage-action"
                          >
                            Manage
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
          {isModalOpen && selectedBooking && (
            <div className="gbh-popup-overlay">
              <div className="gbh-popup-box">
                <h3 className="gbh-popup-title">Booking Management Panel</h3>
                <div className="gbh-popup-section-title">Identification</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Reference Code</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.bookingReference ||
                        selectedBooking.derivedRef ||
                        "N/A"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Booking ID (Database)</label>
                    <input
                      type="text"
                      value={selectedBooking.id || "N/A"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>
                <div className="gbh-popup-section-title">👤 Guest Details</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Full Name</label>
                    <input
                      type="text"
                      value={
                        `${selectedBooking.user?.firstName || ""} ${selectedBooking.user?.lastName || ""}`.trim() ||
                        selectedBooking.customerName ||
                        "Guest"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Contact Email</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.user?.email ||
                        selectedBooking.customerEmail ||
                        selectedBooking.derivedEmail
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Phone Number</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.user?.phoneNumber ||
                        selectedBooking.customerPhone ||
                        "Not registered"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Total Guests</label>
                    <input
                      type="text"
                      value={selectedBooking.numOfGuests || "1"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>
                <h4 className="gbh-popup-section-title">
                  Room and Stay Period
                </h4>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Room Number / Type</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.room
                          ? `Room Nº ${selectedBooking.room.roomNumber} - ${selectedBooking.room.roomType || selectedBooking.room.type || "Assigned"}`
                          : "Not assigned"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Price per Night</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.room?.roomPrice ||
                        selectedBooking.room?.pricePerNight
                          ? `${selectedBooking.room.roomPrice || selectedBooking.room.pricePerNight} USD`
                          : "N/A"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Check-In Date</label>
                    <input
                      type="text"
                      value={selectedBooking.checkInDate}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Check-Out Date</label>
                    <input
                      type="text"
                      value={selectedBooking.checkOutDate}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>
                <div className="gbh-popup-section-title">State Control</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Booking Status</label>
                    <select
                      value={bookingStatus}
                      onChange={(e) => setBookingStatus(e.target.value)}
                      className="gbh-popup-select-control"
                    >
                      <option value="BOOKED">BOOKED (Confirmed)</option>
                      <option value="CANCELLED">CANCELLED (Cancelled)</option>
                    </select>
                  </div>
                  <div className="gbh-popup-field">
                    <label>Payment Status</label>
                    <select
                      value={paymentStatus}
                      onChange={(e) => setPaymentStatus(e.target.value)}
                      className="gbh-popup-select-control"
                    >
                      <option value="PENDING">PENDING (Pending)</option>
                      <option value="PAID">PAID (Paid)</option>
                      <option value="FAILED">FAILED (Failed)</option>
                    </select>
                  </div>
                </div>
                <div className="gbh-popup-buttons-row">
                  <button
                    type="button"
                    onClick={handleSaveChanges}
                    className="gbh-popup-btn-save"
                  >
                    Save System Changes
                  </button>
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="gbh-popup-btn-close"
                  >
                    Close Window
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ManageBookingsPage;
