import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import Pagination from "../common/Pagination";
import RoomResult from "../common/RoomResult";
import { useNavigate } from "react-router-dom";

const ManageRoomPage = () => {
  const navigate = useNavigate();
  const isAdmin = ApiService.isAdmin();
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(8);
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
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const loadInventoryData = async () => {
    try {
      const resp = await ApiService.getAllRooms();

      const roomsData = resp?.rooms || resp?.roomList || resp || [];

      setRooms(roomsData);
      setFilteredRooms(roomsData);
    } catch (error) {
      console.error(error);
      setError("Error loading rooms.");
      setTimeout(() => setError(""), 5000);
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
      } catch (error) {
        console.error("Failed to obtain types from the server:", error);
        setError("Error loading room types.");
        setTimeout(() => {
          setError("");
        }, 5000);
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

  const handleOpenConfirmModal = (e) => {
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
      setTimeout(() => {
        setError("");
      }, 5000);
      return;
    }
    setShowConfirmModal(true);
  };

  const handleExecuteAddRoom = async () => {
    setShowConfirmModal(false);

    try {
      const formData = new FormData();
      formData.append("roomNumber", roomDetails.roomNumber);
      formData.append("type", roomDetails.type);
      formData.append("pricePerNight", roomDetails.pricePerNight);
      formData.append("description", roomDetails.description);
      formData.append("capacity", roomDetails.capacity);

      if (file) {
        formData.append("imageFile", file);
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

        setTimeout(() => {
          setSuccess("");
        }, 4000);
      }
    } catch (error) {
      console.error(error);
      if (error.response && error.response.status === 409) {
        setError(
          `The room number ${roomDetails.roomNumber} is already registered.`,
        );
      } else {
        setError(
          error.response?.data?.message ||
            error.message ||
            "Error adding room.",
        );
      }

      setTimeout(() => {
        setError("");
      }, 5000);
    }
  };

  const handleRoomTypeChange = (e) => {
    filterRoomFunction(e.target.value);
  };

  const filterRoomFunction = (type) => {
    setFilteredRooms(type ? rooms.filter((r) => r.type === type) : rooms);
    setCurrentPage(1);
  };

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
          <h1 className="manage-rooms-title"> Hotel Inventory Console </h1>

          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}

          <div className="inner-management-section">
            <h3>Create New Room Listing</h3>

            <form onSubmit={handleOpenConfirmModal} className="add-room-inline-form" >
              <div className="form-group-file">
                {preview ? (
                  <img
                    src={preview}
                    alt="Room Preview"
                    className="room-preview-thumbnail"
                  />
                ) : (
                  <div className="thumbnail-placeholder">
                    {" "}
                    Select Room Image{" "}
                  </div>
                )}

                <input
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  className="file-input-inline"  />
              </div>

              <div className="form-fields-grid">
                <div className="input-box">
                  <label>Room Type *</label>

                  <select
                    name="type"
                    value={roomDetails.type}
                    onChange={handleInputChange}
                    className="form-control"  >
                    <option value="">Select a room type</option>

                    {roomTypes &&
                      roomTypes.map((type, index) => (
                        <option key={index} value={type}>
                          {type}
                        </option>
                      ))}
                  </select>
                </div>

                <div className="input-box">
                  <label>Room Number *</label>

                  <input
                    type="number"
                    name="roomNumber"
                    value={roomDetails.roomNumber}
                    onChange={handleInputChange}
                    placeholder="e.g., 101"
                    className="form-control"  />
                </div>

                <div className="input-box">
                  <label>Price per Night ($) *</label>

                  <input
                    type="number"
                    name="pricePerNight"
                    value={roomDetails.pricePerNight}
                    onChange={handleInputChange}
                    placeholder="e.g., 120"
                    min="0"
                    step="0.01"
                    className="form-control" />
                </div>
                <div className="input-box">
                  <label>Room Capacity *</label>
                  <input
                    type="number"
                    name="capacity"
                    value={roomDetails.capacity}
                    onChange={handleInputChange}
                    placeholder="e.g., 2"
                    min="1"
                    className="form-control" />
                </div>
              </div>

              <div className="form-group-full">
                <label>Room Description</label>
                <textarea
                  name="description"
                  value={roomDetails.description}
                  onChange={handleInputChange}
                  placeholder="Describe the room features..."
                  rows="2"
                  className="form-control textarea-control" />
              </div>

              <button type="submit" className="action-btn-save"> Save New Room </button>
            </form>
          </div>

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
      {showConfirmModal && (
        <div className="custom-modal-overlay">
          <div className="custom-modal-box">
            <div className="custom-modal-header">
              <h3>Confirm Registration</h3>
            </div>
            <div className="custom-modal-body">
              <p> Are you sure you want to add the room{" "} <strong>#{roomDetails.roomNumber}</strong> to the hotel inventory? </p>
            </div>
            <div className="custom-modal-footer">
              <button className="btn-modal-cancel" onClick={() => setShowConfirmModal(false)}>
                Cancel
              </button>
              <button className="btn-modal-confirm" onClick={handleExecuteAddRoom}> Accept </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ManageRoomPage;
