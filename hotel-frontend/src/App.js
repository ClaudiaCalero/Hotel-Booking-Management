import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./component/common/Navbar";
import Footer from "./component/common/Footer";
import RegisterPage from "./component/auth/Register";
import LoginPage from "./component/auth/LoginPage";
import HomePage from "./component/home/HomePage";
import AllRoomsPage from "./component/booking_rooms/AllRoomsPage";
import RoomDetailsPage from "../../../AppData/Local/Temp/0a4202d7-3e0d-4810-a16e-8fe57a9af885_HBS-react-spring.zip.885/HBS-react-spring/hotel-react-frontend/src/component/booking_rooms/RoomDetailsPage";
import ProfilePage from "./component/profile/ProfilePage";
import EditProfilePage from "./component/profile/EditProfilePage";


function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/rooms" element={<AllRoomsPage />} />
            <Route path="/find-booking" element={<FindBookingPage />} />
            <Route path="/rooms" element={<RoomDetailsPage />} />

            <Route path="/room-details/:roomId" element={<CustomerRoute element={<RoomDetailsPage />} />} />
            <Route path="/profile" element={<CustomerRoute element={<ProfilePage />} />} />
            <Route path="/edit-profile" element={<CustomerRoute element={<EditProfilePage />} />} />



          </Routes>
        </div>
        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;