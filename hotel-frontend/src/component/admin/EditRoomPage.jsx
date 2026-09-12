import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import "../../styles/manage-room.css";
import "../../styles/edit-room-popup.css";


const EditRoomPage = () => {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const isAdmin = ApiService.isAdmin();

  const [roomDetails, setRoomDetails] = useState({
    roomNumber: "",
    type: "",
    pricePerNight: "",
    capacity: "",
    description: "",
  });

  const [roomTypes, setRoomTypes] = useState([]); 
  const [existingImages, setExistingImages] = useState([]); 
  const [newFiles, setNewFiles] = useState([]); 
  const [previews, setPreviews] = useState([]); 
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!isAdmin) {
      navigate("/home");
      return;
    }

    const fetchData = async () => {
      try {
        const roomResponse = await ApiService.getRoomById(roomId);
        const roomData = roomResponse.room;
        
        setRoomDetails({
          roomNumber: roomData.roomNumber || "",
          type: roomData.type || "",
          pricePerNight: roomData.pricePerNight || "",
          capacity: roomData.capacity || "",
          description: roomData.description || "",
        });

        setExistingImages(roomData.imageUrls || []);

        const typesResponse = await ApiService.getRoomTypes();
        setRoomTypes(typesResponse || []);
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error fetching room data.");
      }
    };
    fetchData();
  }, [roomId, isAdmin, navigate]);

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

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    if (selectedFiles.length > 0) {
      const updatedFiles = [...newFiles, ...selectedFiles];
      setNewFiles(updatedFiles);
      
      const filePreviews = updatedFiles.map((file) => URL.createObjectURL(file));
      setPreviews(filePreviews);
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const formData = new FormData();
      formData.append("id", roomId);
      formData.append("roomNumber", roomDetails.roomNumber);
      formData.append("type", roomDetails.type);
      formData.append("pricePerNight", roomDetails.pricePerNight);
      formData.append("capacity", roomDetails.capacity);
      formData.append("description", roomDetails.description);

      if (newFiles.length > 0) {
        newFiles.forEach((file) => {
          formData.append("imageFiles", file);
        });
      }

      const result = await ApiService.updateRoom(formData);
      if (result) {
        setSuccess("Room updated successfully.");
        setTimeout(() => {
          navigate("/admin/manage-rooms");
        }, 2500);
      }
    } catch (error) {
      setError(error.response?.data?.message || error.message || "Error updating room.");
    }
  };

  const handleDelete = async () => {
    if (window.confirm("Do you want to permanently delete this room?")) {
      try {
        const result = await ApiService.deleteRoom(roomId);
        if (result) {
          setSuccess("Room deleted successfully.");
          setTimeout(() => {
            navigate("/admin/manage-rooms");
          }, 2500);
        }
      } catch (error) {
        setError(error.response?.data?.message || error.message || "Error deleting room.");
      }
    }
  };

  if (!isAdmin) return null;

  return (
    <div className="edit-room-modal-overlay">
      <div className="edit-room-modal-card">
        
        <div className="edit-room-modal-header">
          <h2>Edit Room Properties</h2>
          <button type="button" className="edit-room-modal-close-x" onClick={() => navigate("/admin/manage-rooms")}>×</button>
        </div>

        <div className="edit-room-modal-body">
          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}

          <form onSubmit={handleUpdate}>
            
            <div className="modal-form-grid-row">
              <div className="modal-form-group">
                <label>Room Type *</label>
                <select name="type" value={roomDetails.type} onChange={handleChange} className="modal-form-control">
                  <option value="">Select a type</option>
                  {roomTypes.map((type) => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>

              <div className="modal-form-group">
                <label>Room Number *</label>
                <input type="number" name="roomNumber" value={roomDetails.roomNumber} onChange={handleChange} className="modal-form-control" />
              </div>
            </div>

            <div className="modal-form-grid-row">
              <div className="modal-form-group">
                <label>Price per Night ($) *</label>
                <input type="number" name="pricePerNight" value={roomDetails.pricePerNight} onChange={handleChange} className="modal-form-control" />
              </div>

              <div className="modal-form-group">
                <label>Capacity (Guests) *</label>
                <input type="number" name="capacity" value={roomDetails.capacity} onChange={handleChange} className="modal-form-control" />
              </div>
            </div>

            <div className="modal-form-group" style={{ marginBottom: "20px" }}>
              <label>Room Description</label>
              <textarea name="description" value={roomDetails.description} onChange={handleChange} className="modal-form-control modal-textarea-large" placeholder="Write room description comforts..."></textarea>
            </div>

            <div className="modal-gallery-wrapper">
              <label className="gallery-section-label">Current Gallery Images</label>
              
              {existingImages.length > 0 ? (
                <div className="modal-previews-grid">
                  {existingImages.map((src, index) => (
                    <div className="modal-thumb-box" key={index}>
                      <img src={src} alt={`Source preview ${index + 1}`} className="modal-thumb-img" />
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-photos-alert">No images linked to this room yet.</p>
              )}

              {previews.length > 0 && (
                <div>
                  <label className="gallery-section-label">New Photos to Upload</label>
                  <div className="modal-previews-grid">
                    {previews.map((src, index) => (
                      <div className="modal-thumb-box" key={index}>
                        <img src={src} alt={`New upload thumb ${index + 1}`} className="modal-thumb-img" />
                      </div>
                    ))}
                  </div>
                </div>
              )}

              <div>
                <input type="file" name="imageFiles" accept="image/*" multiple onChange={handleFileChange} />
              </div>
            </div>

            <div className="edit-room-modal-footer">
              <button type="submit" className="modal-btn modal-btn-update">Update Details</button>
              <button type="button" className="modal-btn modal-btn-delete" onClick={handleDelete}>Delete Room</button>
              <button type="button" className="modal-btn modal-btn-cancel" onClick={() => navigate("/admin/manage-rooms")}>Cancel</button>
            </div>

          </form>
        </div>

      </div>
    </div>
  );
};

export default EditRoomPage;
