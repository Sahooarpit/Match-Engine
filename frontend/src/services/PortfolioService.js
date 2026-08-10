import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'http://localhost:8080/api/portfolio';

class PortfolioService {
    getPortfolio() {
        const user = AuthService.getCurrentUser();
        return axios.get(API_URL, {
            headers: {
                Authorization: 'Bearer ' + user.token
            }
        });
    }
}

export default new PortfolioService();