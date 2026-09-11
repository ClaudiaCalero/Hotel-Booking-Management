import React, { useState } from "react";
import ApiService from "../../service/ApiService";
import { useNavigate } from "react-router-dom";
import ReactMarkdown from "react-markdown";

// Componente para limitar y expandir la descripción
const RoomDescription = ({ text }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const characterLimit = 180; 

  if (!text) return <p className="room-desc-empty">No description available.</p>;
  
  if (text.length <= characterLimit) {
    return <div className="room-desc-content"><ReactMarkdown>{text}</ReactMarkdown></div>;
  }

  return (
    <div className="room-desc-content">
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

// Componente para el Carrusel
const RoomImageCarousel = ({ imageUrls }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  
  // 🌟 CORREGIDO: Ahora apunta a un archivo de imagen real de hotel como salvavidas
  const placeholder = "https://unsplash.com"; 

  if (!imageUrls || imageUrls.length === 0) {
    return <img src={placeholder} alt="Room placeholder" className="carousel-img-fluid" />;
  }

  // 🌟 LIMPIEZA DE RUTAS: Aseguramos que la barra inicial sea correcta para el servidor local
  let currentImageUrl = imageUrls[currentIndex];
  if (currentImageUrl && !currentImageUrl.startsWith("http") && !currentImageUrl.startsWith("/")) {
    currentImageUrl = "/" + currentImageUrl;
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
        src={currentImageUrl} 
        alt={`Room slide ${currentIndex + 1}`} 
        className="carousel-img-fluid"
        onError={(e) => {
          // 🌟 Si el compilador de React no encuentra la foto local en su caché estática,
          // carga la foto estética de Unsplash para que la interfaz nunca luzca vacía
          e.target.onerror = null; 
          e.target.src = placeholder; 
        }}
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

// Componente Principal
// Componente Principal
const RoomResult = ({ roomSearchResults }) => {
    const navigate = useNavigate();
    const isAdmin = ApiService.isAdmin();

    // 🌟 AÑADE ESTA LÍNEA DE LOG AQUÍ ABAJO PARA VER LOS DATOS EN LA CONSOLA:
    console.log("Room data received in React:", roomSearchResults);

    return (
        <section className="room-results-section">
            { roomSearchResults && roomSearchResults.length > 0 && (
            <div className="inventory-flex-list">
                {roomSearchResults.map(room => (
                    <div className="modern-room-card" key={room.id}>
                        
                        <div className="modern-room-card-left">
                            <RoomImageCarousel imageUrls={room.imageUrls} />
                        </div>

                        {/* Derecha: Datos */}
                        <div className="modern-room-card-right">
                            <div className="modern-room-info-block">
                                <div className="modern-room-title-line">
                                    <h3 className="modern-room-type">{room.type}</h3>
                                    <span className="modern-room-number">Room N° {room.roomNumber}</span>
                                </div>
                                
                                <p className="modern-room-pricing">
                                    Price: ${room.pricePerNight}/Night 
                                    <span className="modern-room-bar">|</span> 
                                    <span className="modern-room-capacity">Capacity: {room.capacity} Guests</span>
                                </p>

                                <RoomDescription text={room.description} />
                            </div>

                            <div className="modern-room-actions">
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
