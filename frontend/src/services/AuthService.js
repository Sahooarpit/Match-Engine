import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {
    login(username, password) {
        return axios
            .post(API_URL + 'login', {
                username,
                password
            })
            .then(response => {
                if (response.data.token) {
                    localStorage.setItem('user', JSON.stringify(response.data));
                }
                return response.data;
            });
    }

    logout() {
        localStorage.removeItem('user');
    }

    register(username, password, email) {
        return axios.post(API_URL + 'register', {
            username,
            password,
            email
        });
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
    }

    isAuthenticated() {
        return localStorage.getItem('user') !== null;
    }
}

export default new AuthService();