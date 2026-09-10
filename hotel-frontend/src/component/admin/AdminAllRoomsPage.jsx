import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination";

const AdminAllRoomsPage = () => {
  const navigate = useNavigate();
  const isAdmin = ApiService.isAdmin();

  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [selectedRoomType, setSelectedRoomType] = useState("");
  
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(6); 
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isAdmin) {
      navigate("/home");
      return;
    }

    const fetchAllData = async () => {
      try {
        const roomsResponse = await ApiService.getAllRooms();
        const typesResponse = await ApiService.getRoomTypes();

        const roomsData = roomsResponse?.rooms || roomsResponse?.roomList || roomsResponse || [];
        setRooms(roomsData);
        setFilteredRooms(roomsData);
        setRoomTypes(typesResponse || []);
      } catch (err) {
        setError(err.response?.data?.message || err.message || "Error loading inventory.");
      }
    };

    fetchAllData();
  }, [isAdmin, navigate]);

  const handleRoomTypeChange = (e) => {
    const type = e.target.value;
    setSelectedRoomType(type);
    setFilteredRooms(type ? rooms.filter((room) => room.type === type) : rooms);
    setCurrentPage(1); 
  };

  const indexOfLastRoom = currentPage * roomsPerPage;
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  if (!isAdmin) return null;

  return (
    <div className="manage-rooms-container">
      <div className="manage-rooms-wrapper admin-all-rooms-wide">
        <div className="manage-rooms-card-content">
          
          <div className="admin-all-rooms-header">
            <h1 className="manage-rooms-title">Full Room Directory</h1>
            <button 
              type="button" 
              className="btn-global-back" 
              onClick={() => navigate("/admin/manage-rooms")}>
              Back to Console
            </button>
          </div>

          {error && <p className="error-message">{error}</p>}

          <div className="all-room-filter-div filter-margin-bottom">
            <div className="filter-select-div">
              <label>Filter inventory by Type:</label>
              <select
                value={selectedRoomType}
                onChange={handleRoomTypeChange}
                className="form-control filter-select">
                <option value="">All Rooms</option>
                {roomTypes.map((type, index) => (
                  <option key={index} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="admin-rooms-grid">
            {currentRooms.length > 0 ? (
              currentRooms.map((room) => (
                <div key={room.id} className="admin-room-item-card">
                  <img 
                    src={room.roomPhotoUrl || "./images/room-placeholder.png"} 
                    alt={room.type} 
                    className="admin-room-card-image"/>
                  <div className="admin-room-card-body">
                    <h3>{room.type}</h3>
                    <p className="admin-room-info">
                      <strong>Room Number:</strong> #{room.roomNumber}
                    </p>
                    <p className="admin-room-info">
                      <strong>Price:</strong> ${room.pricePerNight} / night
                    </p>
                    <p className="admin-room-description">
                      {room.description || "No description provided."}
                    </p>
                    
                    <button
                      onClick={() => navigate(`/admin/edit-room/${room.id}`)}
                      className="action-btn-save admin-edit-btn-full">
                      Edit Room Details
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p className="no-rooms-message full-grid-width">
                No rooms found in the entire directory.
              </p>
            )}
          </div>

          {filteredRooms.length > roomsPerPage && (
            <Pagination
              roomPerPage={roomsPerPage}
              totalRooms={filteredRooms.length}
              currentPage={currentPage}
              paginate={paginate}
            />
          )}

        </div>
      </div>
    </div>
  );
};

export default AdminAllRoomsPage;
