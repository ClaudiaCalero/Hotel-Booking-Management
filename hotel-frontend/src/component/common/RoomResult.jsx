import React, { useState } from "react";
import ApiService from "../../service/ApiService";
import { useNavigate } from "react-router-dom";
import ReactMarkdown from "react-markdown";

const RoomDescription = ({ text }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const characterLimit = 180; 

  if (!text) return <p className="room-desc-empty">No description available.</p>;
  
  if (text.length <= characterLimit) {
    return <div className="room-desc-markdown"><ReactMarkdown>{text}</ReactMarkdown></div>;
  }

  return (
    <div className="room-desc-markdown">
      <ReactMarkdown>
        {isExpanded ? text : `${text.substring(0, characterLimit)}...`}
      </ReactMarkdown>
      <button 
        type="button"
        onClick={() => setIsExpanded(!isExpanded)} 
        className="room-read-more-btn"
      >
        {isExpanded ? "Show Less ↑" : "Read More ↓"}
      </button>
    </div>
  );
};

const RoomImageCarousel = ({ imageUrls }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const placeholder = "/images/hotel/Rooms/placeholder-room.jpg"; 

  if (!imageUrls || imageUrls.length === 0) {
    return <img src={placeholder} alt="Room placeholder" className="room-list-item-image" />;
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
    <div className="room-carousel-main-box">
      <img 
        src={imageUrls[currentIndex]} 
        alt={`Room slide ${currentIndex + 1}`} 
        className="room-list-item-image carousel-img-fluid"
      />
      {imageUrls.length > 1 && (
        <>
          <button type="button" onClick={prevSlide} className="carousel-nav-btn prev-btn">‹</button>
          <button type="button" onClick={nextSlide} className="carousel-nav-btn next-btn">›</button>
          <span className="carousel-counter-tag">
            {currentIndex + 1} / {imageUrls.length}
          </span>
        </>
      )}
    </div>
  );
};

const RoomResult = ({ roomSearchResults }) => {
    const navigate = useNavigate();
    const isAdmin = ApiService.isAdmin();

    return (
        <section className="room-results">
            { roomSearchResults && roomSearchResults.length > 0 && (
            <div className="room-list custom-layout-list">
                {roomSearchResults.map(room => (
                    <div className="room-card-container" key={room.id}>
                        
                        <div className="room-card-left-column">
                            <RoomImageCarousel imageUrls={room.imageUrls} />
                        </div>

                        <div className="room-card-right-column">
                            <div className="room-card-info-top">
                                <div className="room-card-title-row">
                                    <h3 className="room-card-type-title">{room.type}</h3>
                                    <span className="room-card-number-tag">Room N° {room.roomNumber}</span>
                                </div>
                                
                                <p className="room-card-price-line">
                                    Price: ${room.pricePerNight}/Night 
                                    <span className="room-card-divider-bar">|</span> 
                                    <span className="room-card-capacity-text">Capacity: {room.capacity} Guests</span>
                                </p>

                                <RoomDescription text={room.description} />
                            </div>

                            <div className="book-now-div room-card-actions-row">
                                {isAdmin ? (
                                    <button className="edit-room-button" 
                                            onClick={() => navigate(`/admin/edit-room/${room.id}`)}>
                                        Edit Room
                                    </button>
                                ) : (
                                    <button className="book-now-button" 
                                            onClick={() => navigate(`/room-details/${room.id}`)}>
                                        View/Book Now
                                    </button>
                                )}
                            </div>
                        </div>

                    </div>
                ))}
            </div>
            )}
        </section>
    );
};

export default RoomResult;
