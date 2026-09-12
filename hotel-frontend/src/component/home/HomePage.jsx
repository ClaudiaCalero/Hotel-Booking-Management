import React from "react";
import { useNavigate } from "react-router-dom"; 
import RoomSearch from "../common/RoomSearch";
import '../../styles/home.css';

const HomePage = () => {
  const navigate = useNavigate(); 

  const handleSearchResult = (results, startDate, endDate) => {
    
    if (results && results.length === 1) {
      navigate(`/room-details/${results[0].id}`, {
        state: { 
          initialStartDate: startDate, 
          initialEndDate: endDate 
        }
      });
    } 
    else if (results && results.length > 1) {
      navigate("/rooms", { state: { initialFilteredRooms: results } });
    }
  };

  return (
    <div className="home-background">
      <div className="main-window">
        <section>
          <header className="header-banner">
            <div className="overlay"></div>
            <div className="animated-texts overlay-content">
              <h1>Welcome</h1>
              <br />
              <h3>Step into a haven of comfort and care</h3>
            </div>
            <div className="search-placement">
              <RoomSearch handSearchResult={handleSearchResult} />
            </div>
          </header>
        </section>

        <h4>
          <a className="view-rooms-home" href="/rooms">
            All Rooms
          </a>
        </h4>
        <h2 className="home-services">
          Services at <span className="onyx-color">The Grand Hotel Budapest</span>
        </h2>
        <section className="service-section">
          <div className="service-card">
            <img src="./images/ac.png" alt="Air Conditioning" />
            <div className="service-details">
              <h3 className="service-title">Air Conditioning</h3>
              <p className="service-description">
                Stay cool and comfortable throughout your stay with our
                individually controlled in-room air conditioning.
              </p>
            </div>
          </div>
          <div className="service-card">
            <img src="./images/mini-bar.png" alt="Mini Bar" />
            <div className="service-details">
              <h3 className="service-title">Mini Bar</h3>
              <p className="service-description">
                Enjoy a convenient selection of beverages and snacks stocked in
                your room's mini bar with no additional cost.
              </p>
            </div>
          </div>
          <div className="service-card">
            <img src="./images/parking.png" alt="Parking" />
            <div className="service-details">
              <h3 className="service-title">Parking</h3>
              <p className="service-description">
                We offer on-site parking for your convenience. Please inquire
                about valet parking options if available.
              </p>
            </div>
          </div>
          <div className="service-card">
            <img src="./images/wifi.png" alt="WiFi" />
            <div className="service-details">
              <h3 className="service-title">WiFi</h3>
              <p className="service-description">
                Stay connected throughout your stay with complimentary high-speed
                Wi-Fi access available in all guest rooms and public areas.
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default HomePage;
