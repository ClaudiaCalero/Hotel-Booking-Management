import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";

const AddRoomPage = () => {
  const navigate = useNavigate();
  
  const [roomDetails, setRoomDetails] = useState({
    imageUrl: null,
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
  const [roomTypes, setRoomTypes] = useState([]);
  const [newRoomType, setNewRoomType] = useState(""); // State to handle new room type input

  useEffect(() => {
    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.log(error.response?.data?.message || error.message);
      }
    };
    fetchRoomTypes();
  }, []);

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
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
    } else {
      setFile(null);
      setPreview(null);
    }
  };

  const addRoom = async () => {
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

    if (!window.confirm("Do you want to add this room?")) {
      return;
    }

    try {
      const formData = new FormData();
      formData.append("roomNumber", roomDetails.roomNumber);
      formData.append("type", roomDetails.type);
      formData.append("pricePerNight", roomDetails.pricePerNight);
      formData.append("description", roomDetails.description);
      formData.append("capacity", roomDetails.capacity); 

      if (file) {
        formData.append("photo", file); 
      }

      const result = await ApiService.addRoom(formData);
      
      if (result) {
        setSuccess("Room Added successfully.");
        
        setRoomDetails({
          imageUrl: null,
          type: "",
          roomNumber: "",
          pricePerNight: "",
          capacity: "",
          description: "",
        });
        setFile(null);
        setPreview(null);

        setTimeout(() => {
          setSuccess("");
          navigate("/admin"); // Regresa al panel principal
        }, 3000);
      }
    } catch (error) {
      setError(error.response?.data?.message || error.message);
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
          
          <div className="add-room-form">
            <div className="form-group image-upload-group">
              {preview ? (
                <img
                  src={preview}
                  alt="Room Preview"
                  className="room-photo-preview"/>
              ) : (
                <div className="image-placeholder">No image selected</div>
              )}
              <input type="file" name="roomPhoto" onChange={handleFileChange} className="file-input" />
            </div>

            <div className="form-group">
              <label>Room Type *</label>
              <select value={roomDetails.type} onChange={handleRoomTypeChange} className="form-control">
                <option value="">Select a room type</option>
                {roomTypes && roomTypes.map((type) => (
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
                  className="form-control"/>
              </div>

              <div className="form-group half-width">
                <label>Price per Night ($) *</label>
                <input
                  type="number"
                  name="pricePerNight"
                  value={roomDetails.pricePerNight}
                  onChange={handleChange}
                  placeholder="e.g., 200"
                  className="form-control"/>
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
                className="form-control"/>
            </div>

            <div className="form-group">
              <label>Room Description</label>
              <textarea
                name="description"
                value={roomDetails.description}
                onChange={handleChange}
                placeholder="Describe room comforts..."
                rows="3"
                className="form-control textarea-control"></textarea>
            </div>
            <div className="add-room-actions">
              <button className="add-room-submit-btn" onClick={addRoom}>Add Room</button>
              <button className="add-room-back-btn" onClick={() => navigate("/admin")}>Cancel</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddRoomPage;
