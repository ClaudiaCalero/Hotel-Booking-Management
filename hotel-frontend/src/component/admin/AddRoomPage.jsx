import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";

const AddRoomPage = () => {
  const navigate = useNavigate();

  const [roomDetails, setRoomDetails] = useState({
    type: "",
    roomNumber: "",
    pricePerNight: "",
    capacity: "",
    description: "",
  });

  const [files, setFiles] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [roomTypes, setRoomTypes] = useState([]);

  useEffect(() => {
    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes();
        setRoomTypes(types || []);
      } catch (error) {
        console.log(error.response?.data?.message || error.message);
      }
    };
    fetchRoomTypes();
  }, []);

  useEffect(() => {
    return () => {
      previews.forEach((url) => URL.revokeObjectURL(url));
    };
  }, [previews]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRoomDetails((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleRoomTypeChange = (e) => {
    setRoomDetails((prevState) => ({
      ...prevState,
      type: e.target.value,
    }));
  };

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    if (selectedFiles.length > 0) {
      const updatedFiles = [...files, ...selectedFiles];
      setFiles(updatedFiles);
      const filePreviews = updatedFiles.map((file) =>
        URL.createObjectURL(file),
      );
      setPreviews(filePreviews);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); 

    if (
      !roomDetails.type ||
      !roomDetails.pricePerNight ||
      !roomDetails.capacity ||
      !roomDetails.roomNumber
    ) {
      setError("All room details must be provided.");
      setTimeout(() => setError(""), 5000);
      return;
    }

    if (!window.confirm("Do you want to add this room with these photos?")) {
      return;
    }

    try {
      const formData = new FormData();
      formData.append("roomNumber", roomDetails.roomNumber);
      formData.append("type", roomDetails.type);
      formData.append("pricePerNight", roomDetails.pricePerNight);
      formData.append("capacity", roomDetails.capacity);
      formData.append("description", roomDetails.description);

      if (files.length > 0) {
        files.forEach((file) => {
          formData.append("imageFiles", file); // Coincide perfectamente con @RequestParam de Java
        });
      }

      const result = await ApiService.addRoom(formData);

      if (result) {
        setSuccess("Room and photos added successfully.");

        setRoomDetails({
          type: "",
          roomNumber: "",
          pricePerNight: "",
          capacity: "",
          description: "",
        });
        setFiles([]);
        setPreviews([]);

        setTimeout(() => {
          setSuccess("");
          navigate("/admin/manage-rooms");
        }, 3000);
      }
    } catch (error) {
      setError(
        error.response?.data?.message || error.message || "Error adding room.",
      );
      setTimeout(() => setError(""), 5000);
    }
  };

  return (
    <div className="add-room-container">
      <div className="add-room-background-wrapper">
        <div className="add-room-card">
          <h1 className="add-room-title">Add New Room</h1>

          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}

          <form className="add-room-form" onSubmit={handleSubmit}>
            <div className="form-group image-upload-group">
              {previews.length > 0 ? (
                <div
                  className="previews-gallery-container"
                  style={{
                    display: "flex",
                    gap: "10px",
                    flexWrap: "wrap",
                    justifyContent: "center",
                    marginBottom: "15px",
                  }}
                >
                  {previews.map((src, index) => (
                    <img
                      key={index}
                      src={src}
                      alt={`Preview ${index + 1}`}
                      className="room-photo-preview"
                      style={{
                        width: "80px",
                        height: "80px",
                        objectFit: "cover",
                        borderRadius: "8px",
                        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
                      }}
                    />
                  ))}
                </div>
              ) : (
                <div className="image-placeholder">No images selected</div>
              )}
              <input
                type="file"
                name="roomPhoto"
                accept="image/*"
                multiple
                onChange={handleFileChange}
                className="file-input"
              />
            </div>

            <div className="form-group">
              <label>Room Type *</label>
              <select
                value={roomDetails.type}
                onChange={handleRoomTypeChange}
                className="form-control"
              >
                <option value="">Select a room type</option>
                {roomTypes &&
                  roomTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group half-width">
                <label>Room Number *</label>
                <input
                  type="number"
                  name="roomNumber"
                  value={roomDetails.roomNumber}
                  onChange={handleChange}
                  placeholder="e.g., 104"
                  className="form-control"
                />
              </div>

              <div className="form-group half-width">
                <label>Price per Night ($) *</label>
                <input
                  type="number"
                  name="pricePerNight"
                  value={roomDetails.pricePerNight}
                  onChange={handleChange}
                  placeholder="e.g., 200"
                  className="form-control"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Capacity (Guests) *</label>
              <input
                type="number"
                name="capacity"
                value={roomDetails.capacity}
                onChange={handleChange}
                placeholder="e.g., 2"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label>Room Description</label>
              <textarea
                name="description"
                value={roomDetails.description}
                onChange={handleChange}
                placeholder="Describe room comforts..."
                rows="3"
                className="form-control textarea-control"
              ></textarea>
            </div>

            <div className="add-room-actions">
              <button type="submit" className="add-room-submit-btn">
                Add Room
              </button>
              <button
                type="button"
                className="add-room-back-btn"
                onClick={() => navigate("/admin/manage-rooms")}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddRoomPage;
