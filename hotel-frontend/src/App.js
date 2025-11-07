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
import PaymentPage from "../../../AppData/Local/Temp/156e6b96-7589-47dc-82e6-82c1f3c99c26_HBS-react-spring.zip.c26/HBS-react-spring/hotel-react-frontend/src/component/payment/PaymentPage";


function App() {
  return (
    <BrowserRouter>
      <div className="App">
          <Navbar/>
          <div className="content">
              <Routes>
                <Route path="/register" element={<RegisterPage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route exact path="/home" element={<HomePage/>}/>
                <Route path="/rooms" element={<AllRoomsPage/>}/>
                <Route path="/find-booking" element={<FindBookingPage/>}/>

                <Route path="/room-details/:roomId" element={<CustomerRoute element={<RoomDetailsPage/>}/>}/>
                <Route path="/profile" element={<CustomerRoute element={<ProfilePage/>}/>}/>
                <Route path="/edit-profile" element={<CustomerRoute element={<EditProfilePage/>}/>}/>

                {/* PAYMENT PAGES */}

                <Route path="/payment/:bookingReference/:amount" element={<CustomerRoute element={<PaymentPage/>}/>}/>
                <Route path="/payment-success/:bookingReference"  element={<CustomerRoute element={<PaymentSuccess />} />} />
                <Route path="/payment-failed/:bookingReference" element={<CustomerRoute element={<PaymentFailure />} />} />

                {/* ADMIN ROUTES */}
                <Route path="/admin" element={<AdminRoute element={<AdminPage/>}/>}/>
                <Route path="/admin/manage-rooms" element={<AdminRoute element={<ManageRoomPage/>}/>}/>
                <Route path="/admin/add-room" element={<AdminRoute element={<AddRoomPage/>}/>}/>
                <Route path="/admin/edit-room/:roomId" element={<AdminRoute element={<EditRoomPage/>}/>}/>
                <Route path="/admin/manage-bookings" element={<AdminRoute element={<ManageBookingsPage/>}/>}/>
                <Route path="/admin/edit-booking/:bookingCode" element={<AdminRoute element={<EditBookingPage/>}/>}/>


                <Route path="/admin-register" element={<AdminRoute element={<AdminRegisterPage/>}/>}/>


                {/* FALLBACK URL */}
                <Route path="*" element={<Navigate to={"/home"}/>}/>


              </Routes>
          </div>
          <Footer/>
      </div>
    </BrowserRouter>
  );
}

export default App;