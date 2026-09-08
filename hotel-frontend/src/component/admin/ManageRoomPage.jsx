import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination";
import RoomResult from "../common/RoomResult";
import "../../styles/admin.css";

const ManageRoomPage = () => {
  const navigate = useNavigate();

  // Validación de administrador
  const isAdmin = ApiService.isAdmin();

  // Estados del inventario
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(8);

  // Estados del formulario
  const [roomDetails, setRoomDetails] = useState({
    type: "",
    roomNumber: "",
    pricePerNight: "",
    capacity: "",
    description: "",
  });

  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadInventoryData = async () => {
    try {
      const resp = await ApiService.getAllRooms();

      const roomsData = resp?.roomList || resp?.rooms || resp || [];

      setRooms(roomsData);
      setFilteredRooms(roomsData);
      setCurrentPage(1);
    } catch (err) {
      console.error(err);
      setError("Error loading rooms repository.");
    }
  };

  useEffect(() => {
    if (!isAdmin) {
      navigate("/home");
      return;
    }

    const fetchRoomTypes = async () => {
      try {
        const resp = await ApiService.getRoomTypes();
        setRoomTypes(resp || []);
      } catch (err) {
        console.error(err);
        setError("Error loading room types.");
      }
    };

    loadInventoryData();
    fetchRoomTypes();
  }, [isAdmin, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    setRoomDetails((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files?.[0];

    if (selectedFile) {
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
    } else {
      setFile(null);
      setPreview(null);
    }
  };

  const handleAddRoomSubmit = async (e) => {
    e.preventDefault();

    if (
      !roomDetails.type ||
      !roomDetails.pricePerNight ||
      !roomDetails.capacity ||
      !roomDetails.roomNumber
    ) {
      setError(
        "All room details marked with an asterisk (*) must be provided.",
      );

      setTimeout(() => setError(""), 5000);
      return;
    }

    if (
      !window.confirm("Do you want to add this room to the hotel repository?")
    ) {
      return;
    }

    try {
      const formData = new FormData();

      formData.append("roomNumber", roomDetails.roomNumber);
      formData.append("roomType", roomDetails.type);
      formData.append("roomPrice", roomDetails.pricePerNight);
      formData.append("roomDescription", roomDetails.description);
      formData.append("capacity", roomDetails.capacity);

      if (file) {
        formData.append("photo", file);
      }

      const result = await ApiService.addRoom(formData);

      if (result) {
        setSuccess("Room added successfully.");

        setRoomDetails({
          type: "",
          roomNumber: "",
          pricePerNight: "",
          capacity: "",
          description: "",
        });

        setFile(null);
        setPreview(null);

        await loadInventoryData();

        setTimeout(() => setSuccess(""), 4000);
      }
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message || err.message || "Error adding room.",
      );

      setTimeout(() => setError(""), 5000);
    }
  };

  // Eliminar habitación
  const handleDeleteRoom = async (roomId) => {
    if (
      !window.confirm("Are you sure you want to permanently delete this room?")
    ) {
      return;
    }

    try {
      await ApiService.deleteRoom(roomId);

      setSuccess("Room deleted successfully from the inventory.");

      await loadInventoryData();

      setTimeout(() => setSuccess(""), 4000);
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message || err.message || "Error deleting room.",
      );

      setTimeout(() => setError(""), 5000);
    }
  };

  // Filtrar por tipo
  const handleRoomTypeChange = (e) => {
    filterRoomFunction(e.target.value);
  };

  const filterRoomFunction = (type) => {
    if (!type) {
      setFilteredRooms(rooms);
    } else {
      const filtered = rooms.filter(
        (room) => (room.type || room.roomType) === type,
      );

      setFilteredRooms(filtered);
    }

    setCurrentPage(1);
  };

  // Paginación
  const indexOfLastRoom = currentPage * roomsPerPage;
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;

  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

  const paginate = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  if (!isAdmin) {
    return null;
  }

  return (
    <div className="manage-rooms-container">
      <div className="manage-rooms-wrapper">
        <div className="manage-rooms-card-content">
          <h1 className="manage-rooms-title">Hotel Inventory Console</h1>

          {/* Mensajes */}
          {error && <p className="error-message">{error}</p>}

          {success && <p className="success-message">{success}</p>}

          <form
            onSubmit={handleAddRoomSubmit}
            className="inner-management-section"
          >
            <h3>Create New Room Listing</h3>

            <div className="add-room-inline-form">
              <div className="form-group-file">
                {preview ? (
                  <img
                    src={preview}
                    alt="Room preview"
                    className="room-preview-thumbnail"
                  />
                ) : (
                  <div className="thumbnail-placeholder">Select Room Image</div>
                )}

                <input
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  className="file-input-inline"
                />
              </div>

              <div className="form-fields-grid">
                <div className="input-box">
                  <label htmlFor="roomType">Room Type *</label>

                  <select
                    id="roomType"
                    name="type"
                    value={roomDetails.type}
                    onChange={handleInputChange}
                    className="form-control"
                  >
                    <option value="">Select a room type</option>

                    {roomTypes.map((type, index) => (
                      <option key={index} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="input-box">
                  <label htmlFor="roomNumber">Room Number *</label>

                  <input
                    id="roomNumber"
                    type="number"
                    name="roomNumber"
                    value={roomDetails.roomNumber}
                    onChange={handleInputChange}
                    placeholder="e.g., 101"
                    className="form-control"
                  />
                </div>

                {/* Precio */}
                <div className="input-box">
                  <label htmlFor="pricePerNight">Price per Night ($) *</label>

                  <input
                    id="pricePerNight"
                    type="number"
                    name="pricePerNight"
                    value={roomDetails.pricePerNight}
                    onChange={handleInputChange}
                    placeholder="e.g., 120"
                    min="0"
                    step="0.01"
                    className="form-control"
                  />
                </div>

                {/* Capacidad */}
                <div className="input-box">
                  <label htmlFor="capacity">Room Capacity *</label>

                  <input
                    id="capacity"
                    type="number"
                    name="capacity"
                    value={roomDetails.capacity}
                    onChange={handleInputChange}
                    placeholder="e.g., 2"
                    min="1"
                    className="form-control"
                  />
                </div>

                <div className="input-box full-width">
                  <label htmlFor="description">Room Description</label>

                  <textarea
                    id="description"
                    name="description"
                    value={roomDetails.description}
                    onChange={handleInputChange}
                    placeholder="Describe the room features..."
                    className="form-control"
                    rows="2"
                  />
                </div>
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-add-room">
                Save New Room
              </button>
            </div>
          </form>

          {/* =========================
                        SECCIÓN 2: INVENTARIO
                    ========================== */}
          <div className="inner-management-section inventory-list-section">
            <div className="inventory-header">
              <h3>Current Room Inventory</h3>

              <div className="filter-box">
                <label htmlFor="roomFilter">Filter by Type:</label>

                <select
                  id="roomFilter"
                  onChange={handleRoomTypeChange}
                  className="form-control filter-select"
                >
                  <option value="">All Rooms</option>

                  {roomTypes.map((type, index) => (
                    <option key={index} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Habitaciones */}
            <div className="rooms-list">
              {currentRooms.length > 0 ? (
                currentRooms.map((room) => (
                  <RoomResult
                    key={room.id || room.roomId}
                    room={room}
                    onDelete={handleDeleteRoom}
                  />
                ))
              ) : (
                <p className="no-rooms-message">No rooms found.</p>
              )}
            </div>

            {/* Paginación */}
            {filteredRooms.length > roomsPerPage && (
              <Pagination
                roomsPerPage={roomsPerPage}
                totalRooms={filteredRooms.length}
                paginate={paginate}
                currentPage={currentPage}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManageRoomPage;
