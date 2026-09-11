import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import ReactMarkdown from "react-markdown"; 
import "../../styles/room-details.css";

const RoomDetailsCarousel = ({ imageUrls }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  
  const placeholder = "https://unsplash.com"; 

  if (!imageUrls || imageUrls.length === 0) {
    return <img src={placeholder} alt="Room overview" className="room-details-large-image" />;
  }

  const nextSlide = (e) => {
    e.stopPropagation();
    setCurrentIndex((prevIndex) => (prevIndex + 1) % imageUrls.length);
  };

  const prevSlide = (e) => {
    e.stopPropagation();
    setCurrentIndex((prevIndex) => (prevIndex - 1 + imageUrls.length) % imageUrls.length);
  };

  return (
    <div className="details-carousel-container" style={{ position: "relative", width: "100%", height: "400px", overflow: "hidden", borderRadius: "12px", marginBottom: "25px", backgroundColor: "#f5f5f5" }}>
      <img 
        src={imageUrls[currentIndex]} 
        alt={`Room view ${currentIndex + 1}`} 
        style={{ width: "100%", height: "100%", objectFit: "cover" }}
        onError={(e) => {
          e.target.onerror = null;
          e.target.src = placeholder;
        }}
      />
      {imageUrls.length > 1 && (
        <>
          <button type="button" onClick={prevSlide} className="details-carousel-btn prev">‹</button>
          <button type="button" onClick={nextSlide} className="details-carousel-btn next">›</button>
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

  const [room, setRoom] = useState(null);
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const fetchRoomData = async () => {
      try {
        const response = await ApiService.getRoomById(roomId);
        setRoom(response.room);
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error loading room details.");
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

    try {
      setError(""); 
      
      const bookingData = {
        checkInDate,
        checkOutDate,
      };

      const fullBookingPayload = {
        ...bookingData,
        roomId: roomId
      };

      const result = await ApiService.bookRoom(fullBookingPayload);
      
      if (result) {
        setSuccess("Reservation initiative placed successfully!");
        setTimeout(() => {
          navigate("/profile");
        }, 2500);
      }
    } catch (error) {
      console.error("Error devuelto por el servidor:", error.response);
      const backendMsg = error.response?.data?.message || error.response?.data || error.message;
      setError(`Error (500): ${backendMsg}`);
    }
  };

  if (!room) {
    return <div className="room-details-page-container"><p style={{ textAlign: "center", color: "#fff" }}>Loading Grand Budapest Suite Details...</p></div>;
  }

  return (
    <div className="room-details-page-container">
      <h1 className="room-details-main-title">Grand Hotel Budapest Suites</h1>
      
      {success && <p className="success-message" style={{ zIndex: 10, position: "relative" }}>{success}</p>}

      <div className="room-details-content-grid">
        
        <div className="room-details-media-gallery">
          <RoomDetailsCollection imageUrls={room.imageUrls} />

          <div className="room-details-text-info">
            <h2>{room.type} Suite</h2>
            <p className="room-details-price">Price: ${room.pricePerNight} / night</p>
            
            <p style={{ margin: "0 0 20px 0", color: "#666", fontWeight: "600", fontSize: "0.95rem" }}>
              Room Number: #{room.roomNumber} <span style={{ color: "#ccc", margin: "0 8px" }}>|</span> Capacity: {room.capacity} Guests
            </p>
            
            <div className="room-details-description-para">
              <ReactMarkdown>{room.description}</ReactMarkdown>
            </div>
          </div>
        </div>

        <div className="room-details-booking-card">
          <h3>Reservation Panel</h3>
          
          {error && <p className="error-message" style={{ color: "#9c3030", background: "#f8d7da", padding: "10px", borderRadius: "6px", fontSize: "13px" }}>{error}</p>}

          <div className="booking-input-group">
            <label>Check-In Date</label>
            <input 
              type="date" 
              value={checkInDate} 
              onChange={(e) => setCheckInDate(e.target.value)} 
              min={new Date().toISOString().split("T")[0]}
            />
          </div>

          <div className="booking-input-group">
            <label>Check-Out Date</label>
            <input 
              type="date" 
              value={checkOutDate} 
              onChange={(e) => setCheckOutDate(e.target.value)} 
              min={checkInDate || new Date().toISOString().split("T")[0]}
            />
          </div>

          <button type="button" className="btn-submit-room-booking" onClick={handleBooking}>
            Select Stay Dates
          </button>
        </div>

      </div>
    </div>
  );
};

const RoomDetailsCollection = RoomDetailsCarousel;

export default RoomDetailsPage;
