import axios from "axios";
import CryptoJS from "crypto-js";

export default class ApiService {
    static BASE_URL = "http://localhost:9090/api"
    static ENCRYPTION_KEY = "claud-secret-key";

    //encrypt data using cryptojs
    static encrypt(token) {
        return CryptoJS.AES.encrypt(token, this.ENCRYPTION_KEY.toString());

    }

    //decrypt data using cryptojs
    static decrypt(token) {
        const bytes = CryptoJS.AES.decrypt(token, this.ENCRYPTION_KEY.toString());
        return bytes.toString(CryptoJS.enc.Utf8);
    }

    //save token
    static saveToken(token) {
        const encryptedToken = this.encrypt(token);
        localStorage.setItem("token", encryptedToken);
    }

    //retrieve token
    static getToken() {
        const encryptedToken = localStorage.getItem("token");
        if (!encryptedToken) return null;
        return this.decrypt(encryptedToken);
    }

    //save role
    static saveRole(role) {
        const encryptedRole = this.encrypt(role);
        localStorage.setItem("role", encryptedRole);
    }

    //get role
    static getRole() {
        const encryptedRole = localStorage.getItem("role");
        if (!encryptedRole) return null;
        return this.decrypt(encryptedRole);
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

    /* AUTH & USERS API METHODS */

    //AUTH
    static async registerUser(registrationData) {
        const response = await axios.post(`${this.BASE_URL}/auth/register`, registrationData)
        return response.data;
    }

    static async loginUser(loginData) {
        const response = await axios.post(`${this.BASE_URL}/auth/login`, loginData)
        return response.data;
    }

    //USERS
    static async myProfile() {
        const response = await axios.get(`${this.BASE_URL}/users/account`, {
            headers: this.getHeader()
        })
        return response.data;
    }

    static async myBookings() {
        const response = await axios.get(`${this.BASE_URL}/users/bookings`, {
            headers: this.getHeader()
        })
        return response.data;
    }
    static async deleteAccount() {
        const response = await axios.delete(`${this.BASE_URL}/users/delete`, {
            headers: this.getHeader()
        })
        return response.data;
    }

    //ROOMS
    static async addRoom(formData) {
        const response = await axios.post(`${this.BASE_URL}/rooms/add`, formData, {
            headers: {
                ...this.getHeader(),
                'Content-Type': 'multipart/form-data',
            }
        });
        return response.data;
    }

    //to get room types
    static async getRoomTypes() {
        const response = await axios.get(`${this.BASE_URL}/rooms/types`);
        return response.data;
    }

    //to get all rooms
    static async getAllRooms() {
        const response = await axios.get(`${this.BASE_URL}/rooms/all`);
        return response.data;
    }
   
    //to get room details
    static async getRoomById(roomId) {
        const response = await axios.get(`${this.BASE_URL}/rooms/${roomId}`);
        return response.data;
    }

 //to delete room 
    static async deleteRoom(roomId) {
        const response = await axios.delete(`${this.BASE_URL}/rooms/delete/${roomId}`, {
            headers: this.getHeader()
        });
        return response.data;
    }
    static async updateRoom(formData) {
        const response = await axios.put(`${this.BASE_URL}/rooms/update`, formData, {
            headers: {
                ...this.getHeader(),
                'Content-Type': 'multipart/form-data',
            }
        });
        return response.data;
    }

    static async getAvailableRooms(checkInDate, checkOutDate, roomType){
        const response = await axios.get(`${this.BASE_URL}/rooms/available?checkInDate=${checkInDate}
            &checkOutDate=${checkOutDate}&roomType=${roomType}`);
            return response.data;
    }

//BOOKINGS
static async getBookingByReference(bookingCode){
    const response = await axios.get(`${this.BASE_URL}/bookings/${bookingCode}`);
    return response.data;
}
static async bookRoom(booking){
    const response = await axios.post(`${this.BASE_URL}/bookings`, booking, {
        headers: this.getHeader()
    });
    return response.data;
}

static async getAllBookings(){
    const response = await axios.get(`${this.BASE_URL}/bookings/all`, {
        headers: this.getHeader()
    });
    return response.data;
}

static async updateBooking(booking){
    const response = await axios.put(`${this.BASE_URL}/bookings/update`, booking, {
        headers: this.getHeader()
    });
    return response.data;
}

//PAYMENT
//function to create payment intent

static async proceedForPayment(body){
    const response = await axios.post(`${this.BASE_URL}/payments/pay`, body, {
        headers: this.getHeader()
    });
    return response.data; //return the stripe transaction id for this transaction
}

//to update payment when it has been completed
static async updateBookingPayment(body){
    const response = await axios.put(`${this.BASE_URL}/payments/update`, body, {
        headers: this.getHeader()
    });
    return response.data; 
}

//AUTHENTICATION CHECKER
static logout() {
    this.clearAuth();
}

static isAuthenticated() {
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