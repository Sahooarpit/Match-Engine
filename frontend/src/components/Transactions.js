import React, { useState, useEffect } from 'react';
import TransactionService from '../services/TransactionService';
import AuthService from '../services/AuthService';

const Transactions = () => {
    const [transactions, setTransactions] = useState([]);
    const [currentUser, setCurrentUser] = useState(undefined);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
        }

        TransactionService.getTransactions().then((response) => {
            setTransactions(response.data);
        }).catch(error => {
            console.error("Error fetching transactions", error);
        });
    }, []);

    const getSideForTransaction = (tx) => {
        // This logic is a bit tricky since we don't have the username directly.
        // A better approach would be for the backend to return a "side" relative to the current user.
        // For now, we'll assume if the buyClient's ID matches ours, it's a buy, otherwise a sell.
        // This is a simplification and might not be accurate if a user can have multiple clients.
        if (currentUser && tx.buyClient && tx.buyClient.username === currentUser.username) {
            return <span className="badge bg-success">BUY</span>;
        }
        return <span className="badge bg-danger">SELL</span>;
    };

    return (
        <div className="card">
            <div className="card-body">
                <h4 className="card-title">Past Transactions</h4>
                <div className="table-responsive">
                    <table className="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Ticker</th>
                                <th>Side</th>
                                <th>Quantity</th>
                                <th>Price</th>
                                <th>Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            {transactions.map((tx) => (
                                <tr key={tx.tradeId}>
                                    <td>{tx.tradeId.substring(0, 8)}...</td>
                                    <td>{tx.ticker}</td>
                                    <td>{getSideForTransaction(tx)}</td>
                                    <td>{tx.quantity}</td>
                                    <td>${tx.price.toFixed(2)}</td>
                                    <td>{new Date(tx.timestamp).toLocaleString()}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Transactions;