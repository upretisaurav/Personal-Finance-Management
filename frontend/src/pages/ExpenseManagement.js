import React, { useState, useEffect } from 'react';
import { PlusCircle, Trash2, PencilLine } from 'lucide-react';
import 'bootstrap/dist/css/bootstrap.min.css';
import apiClient from '../api/apiClient';

const ExpenseManagement = () => {
  const [expenses, setExpenses] = useState([]);
  const [showExpenseModal, setShowExpenseModal] = useState(false);
  const [currentExpense, setCurrentExpense] = useState({
    category: '',
    amount: '',
    expenseDate: '',
    description: '',
  });
  const [isEditing, setIsEditing] = useState(false);

  const categories = [
    'Food',
    'Transportation',
    'Housing',
    'Utilities',
    'Entertainment',
    'Healthcare',
    'Other'
  ];

  const fetchExpenses = async () => {
    try {
      const currentDate = new Date();
      const response = await apiClient.get(`/expenses/monthly?year=${currentDate.getFullYear()}&month=${currentDate.getMonth() + 1}`);
      setExpenses(response.data);
    } catch (error) {
      console.error('Error fetching expenses:', error);
    }
  };

  useEffect(() => {
    fetchExpenses();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = isEditing ? `/expenses/${currentExpense.id}` : '/expenses';
    const method = isEditing ? 'put' : 'post';

    try {
      const response = await apiClient[method](url, currentExpense);
      if (response.status === 200 || response.status === 201) {
        setShowExpenseModal(false);
        fetchExpenses();
        setCurrentExpense({
          category: '',
          amount: '',
          expenseDate: '',
          description: '',
        });
        setIsEditing(false);
      }
    } catch (error) {
      console.error('Error saving expense:', error);
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await apiClient.delete(`/expenses/${id}`);
      if (response.status === 200) {
        fetchExpenses();
      }
    } catch (error) {
      console.error('Error deleting expense:', error);
    }
  };

  const modalStyle = {
    top: '80px', 
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="h4 mb-0">Expense Management</h2>
        <button
          className="btn btn-primary d-flex align-items-center"
          onClick={() => setShowExpenseModal(true)}
        >
          <PlusCircle size={20} className="me-2" />
          Add Expense
        </button>
      </div>

      <div className="row g-4">
        {expenses.map((expense) => (
          <div key={expense.id} className="col-md-6 col-lg-4">
            <div className="card h-100">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0">{expense.category}</h5>
                <span className="badge bg-primary">${expense.amount}</span>
              </div>
              <div className="card-body">
                <p className="card-text">{expense.description}</p>
                <p className="text-muted small">
                  {new Date(expense.expenseDate).toLocaleDateString()}
                </p>
                <div className="d-flex justify-content-end gap-2">
                  <button
                    className="btn btn-outline-primary btn-sm"
                    onClick={() => {
                      setCurrentExpense(expense);
                      setIsEditing(true);
                      setShowExpenseModal(true);
                    }}
                  >
                    <PencilLine size={16} />
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDelete(expense.id)}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Bootstrap Modal */}
      <div className={`modal ${showExpenseModal ? 'show d-block' : ''}`} tabIndex="-1" style={modalStyle}>
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                {isEditing ? 'Edit Expense' : 'Add New Expense'}
              </h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => setShowExpenseModal(false)}
              ></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="mb-3">
                  <label className="form-label">Category</label>
                  <select
                    className="form-select"
                    value={currentExpense.category}
                    onChange={(e) => setCurrentExpense({ ...currentExpense, category: e.target.value })}
                    required
                  >
                    <option value="">Select category</option>
                    {categories.map((category) => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="form-label">Amount</label>
                  <input
                    type="number"
                    className="form-control"
                    value={currentExpense.amount}
                    onChange={(e) => setCurrentExpense({ ...currentExpense, amount: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={currentExpense.expenseDate}
                    onChange={(e) => setCurrentExpense({ ...currentExpense, expenseDate: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Description</label>
                  <input
                    type="text"
                    className="form-control"
                    value={currentExpense.description}
                    onChange={(e) => setCurrentExpense({ ...currentExpense, description: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowExpenseModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {isEditing ? 'Update Expense' : 'Add Expense'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      {showExpenseModal && <div className="modal-backdrop show"></div>}
    </div>
  );
};

export default ExpenseManagement;