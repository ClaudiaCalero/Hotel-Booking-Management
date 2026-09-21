import React, { useState, useEffect } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import ApiService from "../../service/ApiService";
import ReactMarkdown from "react-markdown";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import "../../styles/room-details.css";

const RoomDetailsCarousel = ({ imageUrls, onImageClick }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const placeholder = "https://unsplash.com";

  if (!imageUrls || imageUrls.length === 0) {
    return (
      <img
        src={placeholder}
        alt="Room overview"
        className="room-details-large-image"
      />
    );
  }

  const nextSlide = (e) => {
    e.stopPropagation();
    setCurrentIndex((prevIndex) => (prevIndex + 1) % imageUrls.length);
  };

  const prevSlide = (e) => {
    e.stopPropagation();
    setCurrentIndex(
      (prevIndex) => (prevIndex - 1 + imageUrls.length) % imageUrls.length,
    );
  };

  return (
    <div
      className="details-carousel-container"
      onClick={() => onImageClick(currentIndex)}
      style={{ cursor: "zoom-in" }}
    >
      <img
        src={imageUrls[currentIndex]}
        alt={`Room view ${currentIndex + 1}`}
        className="details-carousel-img"
        onError={(e) => {
          e.target.onerror = null;
          e.target.src = placeholder;
        }}
      />

      {imageUrls.length > 1 && (
        <>
          <button
            type="button"
            onClick={prevSlide}
            className="details-carousel-btn prev"
          >
            ‹
          </button>

          <button
            type="button"
            onClick={nextSlide}
            className="details-carousel-btn next"
          >
            ›
          </button>

          <span className="details-carousel-counter">
            {currentIndex + 1} / {imageUrls.length}
          </span>
        </>
      )}
    </div>
  );
};

const RoomDetailsPage = () => {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [room, setRoom] = useState(null);
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [error, setError] = useState("");

  const [guestData, setGuestData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
  });

  const [showGuestForm, setShowGuestForm] = useState(false);
  const [zoomImageIndex, setZoomImageIndex] = useState(null);

  useEffect(() => {
    if (location.state) {
      const { initialStartDate, initialEndDate } = location.state;

      if (initialStartDate) {
        const dIn = new Date(initialStartDate);

        const formattedIn = new Date(
          dIn.getTime() - dIn.getTimezoneOffset() * 60000,
        )
          .toISOString()
          .split("T")[0];

        setCheckInDate(formattedIn);
      }

      if (initialEndDate) {
        const dOut = new Date(initialEndDate);

        const formattedOut = new Date(
          dOut.getTime() - dOut.getTimezoneOffset() * 60000,
        )
          .toISOString()
          .split("T")[0];

        setCheckOutDate(formattedOut);
      }
    }
  }, [location.state]);

  useEffect(() => {
    const fetchRoomData = async () => {
      try {
        const response = await ApiService.getRoomById(roomId);
        setRoom(response.room);
      } catch (error) {
        setError(
          error.response?.data?.message ||
            error.message ||
            "Error loading room details.",
        );
      }
    };

    fetchRoomData();
  }, [roomId]);

  const handleBooking = async () => {
    if (!checkInDate || !checkOutDate) {
      setError("Please select both check-in and check-out dates.");
      setTimeout(() => setError(""), 5000);
      return;
    }

    const isLoggedIn = ApiService.isAthenticated();

    // If the user is not logged in, show the guest form first
    if (!isLoggedIn && !showGuestForm) {
      setShowGuestForm(true);
      setError("");
      return;
    }

    // Validate guest information
    if (!isLoggedIn) {
      if (!guestData.firstName.trim()) {
        setError("Please enter your first name.");
        return;
      }

      if (!guestData.lastName.trim()) {
        setError("Please enter your last name.");
        return;
      }

      if (!guestData.email.trim()) {
        setError("Please enter your email.");
        return;
      }

      if (!guestData.phoneNumber.trim()) {
        setError("Please enter your phone number.");
        return;
      }
    }

    try {
      setError("");

      const fullBookingPayload = {
        checkInDate,
        checkOutDate,
        roomId: roomId,
      };

      // Add guest information only for guests
      if (!isLoggedIn) {
        fullBookingPayload.guestFirstName = guestData.firstName;
        fullBookingPayload.guestLastName = guestData.lastName;
        fullBookingPayload.guestEmail = guestData.email;
        fullBookingPayload.guestPhoneNumber = guestData.phoneNumber;
      }

      const result = await ApiService.bookRoom(fullBookingPayload);

      if (result?.booking) {
        const bookingReference = result.booking.bookingReference;

        // Remember that this booking belongs to a guest
        if (!isLoggedIn) {
          localStorage.setItem("guestBooking", "true");
        } else {
          localStorage.removeItem("guestBooking");
        }

        // Go directly to payment
        navigate(
          `/payment/${bookingReference}/${result.booking.totalPrice}${
            !isLoggedIn ? "?guest=true" : ""
          }`,
        );
      }
    } catch (error) {
      const backendMsg =
        error.response?.data?.message ||
        error.response?.data ||
        error.message;

      setError(`Error: ${backendMsg}`);
    }
  };

  const nextPopupSlide = (e) => {
    e.stopPropagation();

    setZoomImageIndex(
      (prev) => (prev + 1) % room.imageUrls.length,
    );
  };

  const prevPopupSlide = (e) => {
    e.stopPropagation();

    setZoomImageIndex(
      (prev) =>
        (prev - 1 + room.imageUrls.length) % room.imageUrls.length,
    );
  };

  if (!room) {
    return (
      <div className="room-details-page-container">
        <p className="room-loading-msg">
          Loading Grand Budapest Suite Details...
        </p>
      </div>
    );
  }

  const isLightboxOpen = zoomImageIndex !== null;

  return (
    <div className="room-details-page-container">
      <h1 className="room-details-main-title">
        Grand Hotel Budapest Suites
      </h1>

      <div className="room-details-content-grid">
        <div className="room-details-media-gallery">
          <RoomDetailsCarousel
            imageUrls={room.imageUrls}
            onImageClick={(index) => setZoomImageIndex(index)}
          />

          <div className="room-details-text-info">
            <h2>{room.type} Suite</h2>

            <p className="room-details-price">
              Price: ${room.pricePerNight} / night
            </p>

            <p className="room-meta-info-text">
              Room Number: #{room.roomNumber}{" "}
              <span className="meta-separator">|</span> Capacity:{" "}
              {room.capacity} Guests
            </p>

            <div className="room-details-description-para">
              <ReactMarkdown>{room.description}</ReactMarkdown>
            </div>
          </div>
        </div>

        <div className="room-details-booking-card">
          <h3>Reservation Panel</h3>

          {error && (
            <p className="error-message error-alert-box">
              {error}
            </p>
          )}

          <div className="booking-input-group">
            <label>Check-In Date</label>

            <DatePicker
              selected={
                checkInDate
                  ? new Date(checkInDate + "T00:00:00")
                  : null
              }
              onChange={(date) => {
                if (date) {
                  const formatted = date
                    .toISOString()
                    .split("T")[0];

                  setCheckInDate(formatted);
                } else {
                  setCheckInDate("");
                }
              }}
              dateFormat="yyyy-MM-dd"
              minDate={new Date()}
              placeholderText="Select Check-In Date"
              className="datepicker-input"
            />
          </div>

          <div className="booking-input-group">
            <label>Check-Out Date</label>

            <DatePicker
              selected={
                checkOutDate
                  ? new Date(checkOutDate + "T00:00:00")
                  : null
              }
              onChange={(date) => {
                if (date) {
                  const formatted = date
                    .toISOString()
                    .split("T")[0];

                  setCheckOutDate(formatted);
                } else {
                  setCheckOutDate("");
                }
              }}
              dateFormat="yyyy-MM-dd"
              minDate={
                checkInDate
                  ? new Date(checkInDate + "T00:00:00")
                  : new Date()
              }
              placeholderText="Select Check-Out Date"
              className="datepicker-input"
            />
          </div>

          {showGuestForm && !ApiService.isAthenticated() && (
            <div className="guest-booking-form">
              <h4>Guest Details</h4>

              <div className="booking-input-group">
                <label>First Name</label>

                <input
                  type="text"
                  value={guestData.firstName}
                  onChange={(e) =>
                    setGuestData({
                      ...guestData,
                      firstName: e.target.value,
                    })
                  }
                  placeholder="Enter your first name"
                />
              </div>

              <div className="booking-input-group">
                <label>Last Name</label>

                <input
                  type="text"
                  value={guestData.lastName}
                  onChange={(e) =>
                    setGuestData({
                      ...guestData,
                      lastName: e.target.value,
                    })
                  }
                  placeholder="Enter your last name"
                />
              </div>

              <div className="booking-input-group">
                <label>Email</label>

                <input
                  type="email"
                  value={guestData.email}
                  onChange={(e) =>
                    setGuestData({
                      ...guestData,
                      email: e.target.value,
                    })
                  }
                  placeholder="Enter your email"
                />
              </div>

              <div className="booking-input-group">
                <label>Phone Number</label>

                <input
                  type="tel"
                  value={guestData.phoneNumber}
                  onChange={(e) =>
                    setGuestData({
                      ...guestData,
                      phoneNumber: e.target.value,
                    })
                  }
                  placeholder="Enter your phone number"
                />
              </div>
            </div>
          )}

          <button
            type="button"
            className="btn-submit-room-booking"
            onClick={handleBooking}
          >
            {!checkInDate || !checkOutDate
              ? "Select Stay Dates"
              : !ApiService.isAthenticated() && !showGuestForm
                ? "Continue as Guest"
                : "Book Room Now"}
          </button>
        </div>
      </div>

      {isLightboxOpen && (
        <div
          className="gbh-single-image-popup-overlay"
          onClick={() => setZoomImageIndex(null)}
        >
          <button
            type="button"
            className="gbh-single-image-popup-close"
            onClick={() => setZoomImageIndex(null)}
          >
            ×
          </button>

          <div
            className="gbh-single-image-popup-content"
            onClick={(e) => e.stopPropagation()}
          >
            <img
              src={room.imageUrls[zoomImageIndex]}
              alt="Zoomed Suite View"
              className="gbh-single-image-popup-element"
            />

            {room.imageUrls.length > 1 && (
              <>
                <button
                  type="button"
                  onClick={prevPopupSlide}
                  className="gbh-popup-carousel-btn prev"
                >
                  ‹
                </button>

                <button
                  type="button"
                  onClick={nextPopupSlide}
                  className="gbh-popup-carousel-btn next"
                >
                  ›
                </button>

                <span className="gbh-popup-carousel-counter">
                  {zoomImageIndex + 1} / {room.imageUrls.length}
                </span>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomDetailsPage;