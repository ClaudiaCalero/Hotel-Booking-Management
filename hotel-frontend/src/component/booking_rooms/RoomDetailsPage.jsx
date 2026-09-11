import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import { useNavigate, useParams } from "react-router-dom";
import { DayPicker } from "react-day-picker";

const RoomDetailsPage = () => {
  const navigate = useNavigate();
  const { roomId } = useParams();

  // Gestión de estados originales
  const [room, setRoom] = useState(null);
  const [checkInDate, setCheckInDate] = useState(null);
  const [checkOutDate, setCheckOutDate] = useState(null);
  const [totalPrice, setTotalPrice] = useState(0);
  const [totalDaysToStay, setTotalDaysToStay] = useState(0);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showBookingPreview, setShowBookingPreview] = useState(false);
  const [showMessage, setShowMessage] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    const fetchRoomDetails = async () => {
      try {
        const resp = await ApiService.getRoomById(roomId);
        const roomData = resp?.room || resp;

        if (!roomData || Object.keys(roomData).length === 0) {
          setErrorMessage("This room contains no valid data or was corrupted.");
          return;
        }

        setRoom(roomData);
      } catch (error) {
        console.error("Error en petición de habitación:", error);
        setErrorMessage(error.response?.data?.message || "Error al conectar con el servidor.");
      }
    };
    fetchRoomDetails();
  }, [roomId]);

  const calculateTotalPrice = () => {
    if (!checkInDate || !checkOutDate) return 0;
    const oneDay = 24 * 60 * 60 * 1000;
    const totalDays = Math.round(
      Math.abs((new Date(checkOutDate) - new Date(checkInDate)) / oneDay)
    );
    setTotalDaysToStay(totalDays);
    return (room?.pricePerNight || room?.roomPrice || 0) * totalDays;
  };

  const handleConfirmation = () => {
    if (!checkInDate || !checkOutDate) {
      setErrorMessage("Please select both check-in and check-out dates");
      setTimeout(() => setErrorMessage(null), 5000);
      return;
    }
    setTotalPrice(calculateTotalPrice());
    setShowBookingPreview(true);
  };

  const acceptBooking = async () => {
    try {
      const formattedCheckInDate = checkInDate.toLocaleDateString("en-CA");
      const formatterdCheckOutDate = checkOutDate.toLocaleDateString("en-CA");

      const booking = {
        checkInDate: formattedCheckInDate,
        checkOutDate: formatterdCheckOutDate,
        roomId: room.id,
      };

      const resp = await ApiService.bookRoom(booking);

      if (resp) {
        setShowMessage(
          "Your Booking is Successful. Please proceeed for payment."
        );
        setTimeout(() => {
          setShowMessage(null);
          navigate(`/payment/${resp.bookingConfirmationCode || "CONFIRMED"}/${totalPrice}`);
        }, 3000);
      }
    } catch (error) {
      setErrorMessage(error.response?.data?.message || error.message);
    }
  };

  if (!room) {
    return (
      <div className="room-loading-container">
        <h2>Loading room details...</h2>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
      </div>
    );
  }

  const roomNumber = room.roomNumber || "N/A";
  const type = room.type || room.roomType || "Standard Suite";
  const pricePerNight = room.pricePerNight || room.roomPrice || 0;
  const capacity = room.capacity || 2;
  const description = room.description || room.roomDescription || "No description provided for this room selection.";
  const imageUrl = room.imageUrl || room.roomPhotoUrl || "";

  return (
    <div className="room-details-page-container">
      {showMessage && <p className="success-message">{showMessage}</p>}
      {errorMessage && <p className="error-message">{errorMessage}</p>}

      <h2 className="room-details-main-title">Grand Hotel Budapest Suites</h2>

      <div className="room-details-content-grid">
        
        <div className="room-details-media-gallery">
          <img src={imageUrl || "/images/room-placeholder.png"} alt={type} className="room-details-large-image" />
          <div className="room-details-text-info">
            <h2>{type}</h2>
            <p className="room-details-price"><strong>Price:</strong> ${pricePerNight} / night</p>
            <p className="room-details-info-para"><strong>Room Number:</strong> #{roomNumber} | <strong>Capacity:</strong> {capacity} Guests</p>
            <p className="room-details-description-para">{description}</p>
          </div>
        </div>

        <div className="room-details-booking-card">
          <h3>Reservation Panel</h3>
          
          <div className="booking-info">
            <button
              className="btn-submit-room-booking"
              onClick={() => setShowDatePicker(!showDatePicker)}
            >
              {showDatePicker ? "Hide Calendar" : "Select Stay Dates"}
            </button>

            {showDatePicker && (
              <div className="date-picker-dropdown-box">
                <div className="booking-input-group">
                  <label>Check-in Date</label>
                  <DayPicker
                    selected={checkInDate}
                    onDayClick={setCheckInDate}
                    disabled={(date) => checkOutDate && date > checkOutDate}
                  />
                </div>

                <div className="booking-input-group">
                  <label>Check-out Date</label>
                  <DayPicker
                    selected={checkOutDate}
                    onDayClick={setCheckOutDate}
                    disabled={(date) => checkInDate && date < checkInDate}
                  />
                </div>

                <button className="btn-submit-room-booking btn-verify-booking" onClick={handleConfirmation}>
                  Verify Booking
                </button>
              </div>
            )}

            {showBookingPreview && (
              <div className="booking-preview">
                <h4>Stay Summary</h4>
                <p><strong>Check-in:</strong> {checkInDate?.toLocaleDateString("en-CA")}</p>
                <p><strong>Check-out:</strong> {checkOutDate?.toLocaleDateString("en-CA")}</p>
                <p><strong>Total Days:</strong> {totalDaysToStay}</p>
                <p className="booking-preview-total"><strong>Total Amount:</strong> ${totalPrice}</p>
                
                <div className="booking-preview-actions">
                  <button className="btn-submit-room-booking" onClick={acceptBooking}>Book Now</button>
                  <button className="btn-submit-room-booking btn-cancel-preview" onClick={() => setShowBookingPreview(false)}>Cancel</button>
                </div>
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

export default RoomDetailsPage;
