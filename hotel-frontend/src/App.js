import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

// global import
import './styles/index.css';
import './styles/navbar.css';
import './styles/footer.css';

// Global components
import Navbar from "./component/common/Navbar";
import Footer from "./component/common/Footer";

// Auth components
import RegisterPage from "./component/auth/Register";
import LoginPage from "./component/auth/LoginPage";
import ForgotPassword from "./component/auth/ForgotPassword";
import ResetPassword from "./component/auth/ResetPassword";

// User Components
import HomePage from "./component/home/HomePage";
import AllRoomsPage from "./component/booking_rooms/AllRoomsPage";
import RoomDetailsPage from "./component/booking_rooms/RoomDetailsPage";
import FindBookingPage from "./component/booking_rooms/FindBookingPage";
import ProfilePage from "./component/profile/ProfilePage";
import EditProfilePage from "./component/profile/EditProfilePage";
import PaymentPage from "./component/payments/PaymentPage";
import PaymentSuccess from "./component/payments/PaymentSuccess";
import PaymentFailure from "./component/payments/PaymentFailure";

// Admin components + routes 
import { AdminRoute, CustomerRoute } from "./service/Guard";
import AdminPage from "./component/admin/AdminPage";
import AddRoomPage from "./component/admin/AddRoomPage";
import ManageRoomPage from "./component/admin/ManageRoomPage";
import EditRoomPage from "./component/admin/EditRoomPage";
import AdminAllRoomsPage from "./component/admin/AdminAllRoomsPage";
import ManageBookingsPage from "./component/admin/ManageBookingsPage";
import AdminRegisterPage from "./component/admin/AdminRegisterPage";
import EditBookingPage from "./component/admin/EditBookingPage";



function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />

            <Route path="/reset-password" element={<ResetPassword />} />

            <Route exact path="/home" element={<HomePage />} />
            <Route path="/rooms" element={<AllRoomsPage />} />
            <Route path="/find-booking" element={<FindBookingPage />} />

            <Route path="/room-details/:roomId" element={<CustomerRoute element={<RoomDetailsPage />} />} />
            <Route path="/profile" element={<CustomerRoute element={<ProfilePage />} />} />
            <Route path="/edit-profile" element={<CustomerRoute element={<EditProfilePage />} />} />

            {/* PAYMENT PAGES */}

            <Route path="/payment/:bookingReference/:amount" element={<CustomerRoute element={<PaymentPage />} />} />
            <Route path="/payment-success/:bookingReference" element={<CustomerRoute element={<PaymentSuccess />} />} />
            <Route path="/payment-failed/:bookingReference" element={<CustomerRoute element={<PaymentFailure />} />} />

            {/* ADMIN ROUTES */}

            {/* ADMIN ROUTES */}
                        {/* ADMIN ROUTES */}
            <Route path="/admin" element={<AdminRoute element={<AdminPage />} />} />
            <Route path="/admin/manage-rooms" element={<AdminRoute element={<ManageRoomPage />} />} />
            <Route path="/admin/add-room" element={<AdminRoute element={<AddRoomPage />} />} />
            <Route path="/admin/edit-room/:roomId" element={<AdminRoute element={<EditRoomPage />} />} />
            <Route path="/admin-register" element={<AdminRoute element={<AdminRegisterPage />} />} />
            <Route path="/admin/all-rooms" element={<AdminRoute element={<AdminAllRoomsPage />} />} />
            <Route path="/admin/manage-bookings" element={<AdminRoute element={<ManageBookingsPage />} />} />

            {/* FALLBACK URL */}
            <Route path="*" element={<Navigate to={"/home"} />} />


          </Routes>
        </div>
        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;