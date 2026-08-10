import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'http://localhost:8080/api/trade';

class TradeService {
    submitOrder(ticker, side, quantity, price) {
        const user = AuthService.getCurrentUser();
        return axios.post(API_URL, {
            ticker,
            side,
            quantity,
            price
        }, {
            headers: {
                Authorization: 'Bearer ' + user.token
            }
        });
    }
}

export default new TradeService();