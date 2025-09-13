import axios from "axios";
import CryptoJS from "crypto-js";

export default class ApiService{
    static BASE_URL = "http://localhost:9090/api"
    static ENCRYPTION_KEY = "claud-secret-key";

    //encrypt data using cryptojs
    static encrypt(token){
        return CryptoJS.AES.encrypt(token, this.ENCRYPTION_KEY.toString());

    }

     //decrypt data using cryptojs
    static decrypt(token){
        const bytes = CryptoJS.AES.decrypt(token, this.ENCRYPTION_KEY.toString());
        return bytes.toString(CryptoJS.enc.Utf8);
    }

    //save token
    static saveToken(token){
        const encryptedToken = this.encrypt(token);
        localStorage.setItem("token", encryptedToken);
    }

    //retrieve token
    static getToken(){
        const encryptedToken = localStorage.getItem("token");
        if(!encryptedToken) return null;
        return this.decrypt(encryptedToken);
    }

    //save role
    static saveRole(role){
        const encryptedRole = this.encrypt(role);
        localStorage.setItem("role", encryptedRole);
    }

     //get role
    static getRole(){
        const encryptedRole= localStorage.getItem("role");
        if(!encryptedRole) return null;
        return this.decrypt(encryptedRole);
    }

    static clearAuth(){
        localStorage.removeItem("token");
        localStorage.removeItem("role");

    }
    static getHeader(){
        const token = this.getToken();
        return {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    }

/* AUTH & USERS API METHODS */


}