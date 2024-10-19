import React, { useState, useEffect } from 'react';
import apiClient from '../api/apiClient';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

const Dashboard = () => {
  const [balance, setBalance] = useState(0);
  const [amount, setAmount] = useState('');
  const [source, setSource] = useState('');
  const [balanceSources, setBalanceSources] = useState([]);
  const [showAddBalance, setShowAddBalance] = useState(false);
  const [investments, setInvestments] = useState([]);

  useEffect(() => {
    fetchBalance();
    fetchBalanceSources();
    fetchInvestments();
  }, []);

  const fetchBalance = async () => {
    try {
      const response = await apiClient.get('/users/balance');
      setBalance(response.data);
    } catch (error) {
      console.error('Error fetching balance:', error);
    }
  };

  const fetchBalanceSources = async () => {
    try {
      const response = await apiClient.get('/users/balance/sources');
      setBalanceSources(response.data);
    } catch (error) {
      console.error('Error fetching balance sources:', error);
    }
  };

  const fetchInvestments = async () => {
    try {
      const response = await apiClient.get('/investments');
      setInvestments(response.data);
    } catch (error) {
      console.error('Error fetching investments:', error);
    }
  };


  const handleAddBalance = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post('/users/balance/add', null, {
        params: { amount, source }
      });
      fetchBalance();
      setAmount('');
      setSource('');
      setShowAddBalance(false);
    } catch (error) {
      console.error('Error adding balance:', error);
    }
  };

  return (
    <div className="container py-4">
      <h1 className="mb-4">Dashboard</h1>
      <p>Welcome to your personal finance dashboard.</p>
      
      <div className="row">
        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">Current Balance</h5>
              <h2>${balance.toFixed(2)}</h2>
              <button
                className="btn btn-primary mt-3"
                onClick={() => setShowAddBalance(!showAddBalance)}
              >
                {showAddBalance ? 'Cancel' : 'Add Balance'}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">Investment Portfolio Summary</h5>
              <ul className="list-group">
                {investments.map((investment, index) => (
                  <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                    {investment.name}
                    <span className="badge bg-primary rounded-pill">${investment.amount.toFixed(2)}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>

      {showAddBalance && (
        <div className="row">
          <div className="col-12 mb-4">
            <div className="card">
              <div className="card-body">
                <h5 className="card-title">Add Balance</h5>
                <form onSubmit={handleAddBalance}>
                  <div className="mb-3">
                    <label htmlFor="amount" className="form-label">Amount</label>
                    <input
                      type="number"
                      className="form-control"
                      id="amount"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="source" className="form-label">Source</label>
                    <select
                      className="form-select"
                      id="source"
                      value={source}
                      onChange={(e) => setSource(e.target.value)}
                      required
                    >
                      <option value="">Select a source</option>
                      {balanceSources.map((src) => (
                        <option key={src} value={src}>{src}</option>
                      ))}
                    </select>
                  </div>
                  <button type="submit" className="btn btn-success">Add Balance</button>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;