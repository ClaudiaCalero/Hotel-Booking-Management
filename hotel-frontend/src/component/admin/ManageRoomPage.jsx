import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination"; // 👈 Restaurado tu componente original
import RoomResult from "../common/RoomResult";

const ManageRoomPage = () => {
  const navigate = useNavigate();
  const isAdmin = ApiService.isAdmin();

  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(3); // Ajustado a tus 8 originales por página
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isAdmin) {
      navigate("/home");
      return;
    }

    const fetchRoomsAndTypes = async () => {
      try {
        const roomsResponse = await ApiService.getAllRooms();
        const typesResponse = await ApiService.getRoomTypes();
        
        // Conserva el mapeo exacto de tu backend
        const roomsData = roomsResponse?.rooms || roomsResponse?.roomList || roomsResponse || [];
        setRooms(roomsData);
        setFilteredRooms(roomsData);
        setRoomTypes(typesResponse || []);
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error fetching data.");
      }
    };
    fetchRoomsAndTypes();
  }, [isAdmin, navigate]);

  const handleRoomTypeChange = (e) => {
    const type = e.target.value;
    setFilteredRooms(type ? rooms.filter((room) => room.type === type) : rooms);
    setCurrentPage(1);
  };

  // Lógica de Paginación exacta a tu archivo de GitHub
  const indexOfLastRoom = currentPage * roomsPerPage;
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  if (!isAdmin) {
    return null;
  }

  return (
    <div className="manage-rooms-container">
      <div className="manage-rooms-wrapper">
        <div className="manage-rooms-card-content">
          <h1 className="manage-rooms-title"> Hotel Inventory Console </h1>

          {error && <p className="error-message">{error}</p>}

          {/* 🌟 MANTENEMOS ESTA SECCIÓN VISUAL: Pero en lugar del formulario largo, 
              colocamos un llamado a la acción limpio hacia AddRoomPage */}
          <div className="inner-management-section">
            <h3>Create New Room Listing</h3>
            <div style={{ padding: "20px 0", textAlign: "center" }}>
              <p style={{ marginBottom: "15px", color: "#555" }}>
                To register a new room with its image, pricing, and specific characteristics:
              </p>
              <button 
                type="button" 
                className="action-btn-save" 
                onClick={() => navigate("/admin/add-room")}
                style={{ display: "inline-block", width: "auto", padding: "10px 25px" }}
              >
                Go to Add Room Page
              </button>
            </div>
          </div>

          {/* SECCIÓN INVENTARIO: Queda exactamente idéntica y funcional */}
          <div className="inner-management-section inventory-list-section">
            <h3>Current Room Inventory</h3>

            <div className="all-room-filter-div">
              <div className="filter-select-div">
                <label>Filter by Type:</label>
                <select
                  onChange={handleRoomTypeChange}
                  className="form-control filter-select" >
                  <option value="">All Rooms</option>
                  {roomTypes &&
                    roomTypes.map((type, index) => (
                      <option key={index} value={type}>
                        {type}
                      </option>
                    ))}
                </select>
              </div>
            </div>

            <div className="room-results-wrapper">
              {currentRooms.length > 0 ? (
                <RoomResult roomSearchResults={currentRooms} />
              ) : (
                <p className="no-rooms-message">No rooms found.</p>
              )}
            </div>

            {filteredRooms.length > roomsPerPage && (
              <Pagination
                roomPerPage={roomsPerPage}
                totalRooms={filteredRooms.length}
                currentPage={currentPage}
                paginate={paginate} />
            )}
            
            <button type="button" className="btn-global-back" onClick={() => navigate("/admin")} >
              Back to Admin Panel
            </button>
          </div>

        </div>
      </div>
    </div>
  );
};

export default ManageRoomPage;
