import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import { useNavigate, useParams } from "react-router-dom";
import { DayPicker } from "react-day-picker";
import RoomDetailsPage from "../../../../../AppData/Local/Temp/0a4202d7-3e0d-4810-a16e-8fe57a9af885_HBS-react-spring.zip.885/HBS-react-spring/hotel-react-frontend/src/component/booking_rooms/RoomDetailsPage";

const RoomDetailsPage = () => {
  const navigate = useNavigate();
  const { roomId } = useParams();

  //state management
  const [room, setRoom] = useState(null);
  const [checkInDate, setCheckInDate] = useState(null);
  const [checkOutDate, setCheckOutDate] = useState(null);
  const [totalPrice, setTotalPrice] = useState(0);
  const [totalDaysToStay, setTotalDaysToStay] = useState(0);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showBookingPreview, setShowBookingPreview] = useState(false);
  const [showMessage, setShowMessage] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

//fetch room details
useEffect(() => {
    const fetchRoomDetails = async () => {
        try {
            const response = await ApiService.getRoomById(roomId);
            setRoom(response.room);
            console.log(response);

            
        } catch (error) {
            console.log(error)
        }
    };
    fetchRoomDetails();
}, []);

//CAlculate total price
const calculateTotalPrice = () => {
    if(!checkInDate || !checkOutDate) return 0;

    const oneDay = 24 * 60 *60* 1000: //this is number in miliseconds
    const totalDays = Math.round(
        Math.abs((new Date(checkOutDate) - new Date (checkInDate)) / oneDay)
    )
}
; //give the difference in miliseconds
setTotalDaysToStay(totalDays);
return room?.pricePerNight * totalDays || 0;
};

export default RoomDetailsPage;
