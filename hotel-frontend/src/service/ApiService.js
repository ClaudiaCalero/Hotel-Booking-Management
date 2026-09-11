import axios from "axios";
import CryptoJS from "crypto-js";

export default class ApiService {

    static BASE_URL = "http://localhost:8080/api";
    static ENCRYPTION_KEY = "claud-secrete-key";

    //enctyp token using cruyptojs

    static encrypt(token) {
        return CryptoJS.AES.encrypt(token, this.ENCRYPTION_KEY).toString();
    }

    static decrypt(token) {
        const bytes = CryptoJS.AES.decrypt(token, this.ENCRYPTION_KEY);
        return bytes.toString(CryptoJS.enc.Utf8);
    }


    //save token
    static saveToken(token) {
        const encrytpedToken = this.encrypt(token);
        localStorage.setItem("token", encrytpedToken);
    }

    //retreive token
    static getToken() {
        const encrytpedToken = localStorage.getItem("token");
        if (!encrytpedToken) return null;
        return this.decrypt(encrytpedToken)
    }

    //save role
    static saveRole(role) {
        const encrytpedRole = this.encrypt(role);
        localStorage.setItem("role", encrytpedRole);
    }


    //get role
    static getRole() {
        const encrytpedRole = localStorage.getItem("role");
        if (!encrytpedRole) return null;
        return this.decrypt(encrytpedRole)
    }

    static clearAuth() {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
    }

    static getHeader() {
        const token = this.getToken();
        return {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    }

    /** AUTH AND USERS API METHODS */

    // AUTH
    static async registerUser(registrationData) {
        const resp = await axios.post(`${this.BASE_URL}/auth/register`, registrationData);
        return resp.data;
    }


    static async loginUser(loginData) {
        const resp = await axios.post(`${this.BASE_URL}/auth/login`, loginData);
        return resp.data;
    }

    // USERS
    static async myProfile() {
        const resp = await axios.get(`${this.BASE_URL}/users/account`, {
            headers: this.getHeader()
        })
        return resp.data;
    }

    static async myBookings() {
        const resp = await axios.get(`${this.BASE_URL}/users/bookings`, {
            headers: this.getHeader()
        })
        return resp.data;
    }

    static async deleteAccount() {
        const resp = await axios.delete(`${this.BASE_URL}/users/delete`, {
            headers: this.getHeader()
        })
        return resp.data;
    }

    // ROOMS
    static async addRoom(formData) {
        const baseHeaders = this.getHeader();
        delete baseHeaders["Content-Type"];
        const resp = await axios.post(`${this.BASE_URL}/rooms/add`, formData, {
            headers: baseHeaders
        });
        return resp.data;
    }
    static async getAllRooms() {
        const response = await axios.get(`${this.BASE_URL}/rooms/all`, {
            headers: this.getHeader() // 👈 CORREGIDO: Usa las cabeceras con el token descifrado
        });
        return response.data;
    }

    static async getRoomTypes() {
        const response = await axios.get(`${this.BASE_URL}/rooms/types`, {
            headers: this.getHeader() // 👈 CORREGIDO: Usa las cabeceras con el token descifrado
        });
        return response.data;
    }


    //To get room details
    static async getRoomById(roomId) {
        if (!roomId || roomId === "undefined") {
            console.error("⚠️ ApiService: An attempt was made to find a room, but the ID is null or undefined..");
            return null;
        }

        const response = await axios.get(`${this.BASE_URL}/rooms/${roomId}`, {
            headers: this.getHeader()
        });
        return response.data;
    }

    static async deleteRoom(roomId) {
        const resp = await axios.delete(`${this.BASE_URL}/rooms/delete/${roomId}`, {
            headers: this.getHeader()
        });
        return resp.data;
    }


    static async updateRoom(formData) {
        const baseHeaders = this.getHeader();
        delete baseHeaders["Content-Type"];
        const resp = await axios.put(`${this.BASE_URL}/rooms/update`, formData, {
            headers: baseHeaders
        });
        return resp.data;
    }

    static async getAvailableRooms(checkInDate, checkOutDate, roomType) {
        console.log("Enviando al backend -> CheckIn:", checkInDate, "CheckOut:", checkOutDate);

        const resp = await axios.get(
            `${this.BASE_URL}/rooms/available?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&roomType=${roomType}`
        );
        return resp.data;
    }


    //BOOKINGS
    static async getBookingByReference(bookingReference) {
        if (!bookingReference || bookingReference === "undefined") {
            console.error("⚠️ ApiService: An attempt was made to retrieve a reservation, but the 'bookingReference' is null or undefined.");
            throw new Error("Booking reference is required.");
        }

        const response = await axios.get(`${this.BASE_URL}/bookings/${bookingReference}`, {
            headers: this.getHeader()
        });
        return response.data;
    }

    static async bookRoom(booking) {
        const resp = await axios.post(`${this.BASE_URL}/bookings`, booking, {
            headers: this.getHeader()
        });
        return resp.data;
    }

    static async getAllBookings() {
        const resp = await axios.get(`${this.BASE_URL}/bookings/all`, {
            headers: this.getHeader()
        });
        return resp.data;
    }

    static async updateBooking(booking) {
        const resp = await axios.put(`${this.BASE_URL}/bookings/update`, booking, {
            headers: this.getHeader()
        });
        return resp.data;
    }

    //PAYMMENT 
    //funtion to create payment intent
    static async proceedForPayment(body) {
        const resp = await axios.post(`${this.BASE_URL}/payments/pay`, body, {
            headers: this.getHeader()
        });
        return resp.data; //return the strip transaction id for this transaction
    }

    //TO UPDATE PAYMENT WHEN IT HAS BEEN COMPLETED
    static async updateBookingPaymeent(body) {
        const resp = await axios.put(`${this.BASE_URL}/payments/update`, body, {
            headers: this.getHeader()
        });
        return resp.data;
    }



    //AUTHENTICATION CHECKER
    static logout() {
        this.clearAuth();
    }

    static isAthenticated() {
        const token = this.getToken();
        return !!token;
    }

    static isAdmin() {
        const role = this.getRole();
        return role === "ADMIN";
    }

    static isCustomer() {
        const role = this.getRole();
        return role === "CUSTOMER";
    }



}