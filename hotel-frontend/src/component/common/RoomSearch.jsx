import React, { useState, useEffect, useRef } from "react";
import ApiService from "../../service/ApiService";
import { DayPicker } from "react-day-picker";

const RoomSearch = ({ handSearchResult }) => {
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [roomType, setRoomType] = useState("");
  const [roomTypes, setRoomTypes] = useState([]);
  const [error, setError] = useState("");

  //state for controlling calendar visibility
  const [isStartDatePickerVisible, setStartDatePickerVisible] = useState(false);
  const [isEndDatePickerVisible, setEndDatePickerVisible] = useState(false);

  const startDateRef = useRef(null);
  const endDateRef = useRef(null);

  useEffect(() => {
    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.log("Error fetching RoomTypes" + error);
      }
    };
    fetchRoomTypes();
  }, []);
  const handleClickOutside = (event) => {
    if (startDateRef.current && !startDateRef.current.contains(event.target)) {
      setStartDatePickerVisible(false);
    }
    if (endDateRef.current && !endDateRef.current.contains(event.target)) {
      setEndDatePickerVisible(false);
    }
  };
  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);
  const showError = (message, timeout = 5000) => {
    setError(message);
    setTimeout(() => {
      setError("");
    }, timeout);
  };

  //this will fetch the rooms abailable from our api
  const handleInternalSearch = async () => {
    if (!startDate || !endDate || !roomType) {
      showError("Please select fields");
      return false;
    }
    try {
      const formattedStartDate = startDate
        ? startDate.toLocalDateString("en-GB")
        : null;
      const formattedEndDate = endDate
        ? endDate.toLocalDateString("en-GB")
        : null;
      const response = await ApiService.getAvailableRooms(
        formattedStartDate,
        formattedEndDate,
        roomType
      );

      if (response.status === 200) {
        if (response.rooms.length === 0) {
          showError("Room not currently available for the selected date");
          return;
        }
        handSearchResult(response.rooms);
        setError("");
      }
    } catch (error) {
      showError(error?.response?.data?.message || error.message);
    }
  };
};

return (
  <section>
    {/* CHECK IN DATE + CALENDAR DATE */}
    <div className="search-container">
      <div className="search-field" style={{ position: "relative" }}>
        <label>Check-in-Date</label>
        <input
          type="text"
          value={startDate ? startDate.toLocalDateString() : ""}
          placeholder="Select Check-In-Date"
          onFocus={() => setStartDatePickerVisible(true)}
          readOnly
        />
        {isStartDatePickerVisible && (
          <div className="datepicker-container" ref={startDateRef}>
            <DatePicker
              selected={startDate}
              onDayClick={(date) => {
                setStartDate(date);
                setStartDatePickerVisible(false);
              }}
              month={startDate}
            />
          </div>
        )}
      </div>
    </div>
    {/* CHECK OUT DATE + CALENDAR DATE */}
    <div className="search-container">
      <div className="search-field" style={{ position: "relative" }}>
        <label>Check-out-Date</label>
        <input
          type="text"
          value={endDate ? endDate.toLocalDateString() : ""}
          placeholder="Select Check-Out-Date"
          onFocus={() => setEndDatePickerVisible(true)}
          readOnly
        />
        {isEndDatePickerVisible && (
          <div className="datepicker-container" ref={endDateRef}>
            <DatePicker
              selected={endDate}
              onDayClick={(date) => {
                setendDate(date);
                setEndDatePickerVisible(false);
              }}
              month={startDate}
            />
          </div>
        )}
      </div>
      {/* ROOM TYPE SELECTION FIELDS */}
      <div className="search-field">
        <label>Room Type</label>
        <select value={roomType} onChange={(e) => setRoomType(e.target.value)}>
          <option disabled value=""></option>
          {roomTypes.map((roomType) => (
            <option value={roomType} key={roomType}>
              {roomType}
            </option>
          ))}
        </select>
      </div>
      //SEARCH BUTTON
      <button className="home-search-button" onClick={handleInternalSearch}>
        Search Room
      </button>
    </div>
    {error && <p className="error-message">{error}</p>}
  </section>
);
export default RoomSearch;
