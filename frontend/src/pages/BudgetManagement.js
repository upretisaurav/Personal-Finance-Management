import React, { useState, useEffect } from 'react';
import { PlusCircle, Trash2, PencilLine } from 'lucide-react';
import apiClient from '../api/apiClient';

const BudgetManagement = () => {
  const [budgets, setBudgets] = useState([]);
  const [showBudgetModal, setShowBudgetModal] = useState(false);
  const [currentBudget, setCurrentBudget] = useState({
    category: '',
    targetAmount: '',
    startDate: '',
    endDate: '',
  });
  const [isEditing, setIsEditing] = useState(false);
  const [budgetStatuses, setBudgetStatuses] = useState({});

  const categories = [
    'Food',
    'Transportation',
    'Housing',
    'Utilities',
    'Entertainment',
    'Healthcare',
    'Other'
  ];

  const fetchBudgets = async () => {
    try {
      const response = await apiClient.get('/budgets');
      setBudgets(response.data);
      response.data.forEach((budget) => fetchBudgetStatus(budget.category));
    } catch (error) {
      console.error('Error fetching budgets:', error);
    }
  };

  const fetchBudgetStatus = async (category) => {
    try {
      const response = await apiClient.get(`/budgets/status?category=${category}`);
      setBudgetStatuses((prev) => ({
        ...prev,
        [category]: response.data,
      }));
    } catch (error) {
      console.error(`Error fetching budget status for ${category}:`, error);
    }
  };
  
  useEffect(() => {
    fetchBudgets();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = isEditing ? `/budgets/${currentBudget.id}` : '/budgets';
    const method = isEditing ? 'put' : 'post';

    try {
      await apiClient({
        method,
        url,
        data: currentBudget,
      });

      setShowBudgetModal(false);
      fetchBudgets();
      setCurrentBudget({
        category: '',
        targetAmount: '',
        startDate: '',
        endDate: '',
      });
      setIsEditing(false);
    } catch (error) {
      console.error('Error saving budget:', error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiClient.delete(`/budgets/${id}`);
      fetchBudgets();
    } catch (error) {
      console.error('Error deleting budget:', error);
    }
  };

  const modalStyle = {
    top: '80px', 
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="h4">Budget Management</h2>
        <button
          className="btn btn-primary d-flex align-items-center"
          onClick={() => {
            setIsEditing(false);
            setCurrentBudget({
              category: '',
              targetAmount: '',
              startDate: '',
              endDate: '',
            });
            setShowBudgetModal(true);
          }}
        >
          <PlusCircle size={20} className="me-2" />
          Add Budget
        </button>
      </div>

      <div className="row g-4">
        {budgets.map((budget) => (
          <div key={budget.id} className="col-md-6 col-lg-4">
            <div className="card h-100">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0">{budget.category}</h5>
                <span className="badge bg-primary">${budget.targetAmount}</span>
              </div>
              <div className="card-body">
                <p className="mb-2">Duration: {new Date(budget.startDate).toLocaleDateString()} - {new Date(budget.endDate).toLocaleDateString()}</p>
                {budgetStatuses[budget.category] && (
                  <div>
                  </div>
                )}
                <div className="d-flex justify-content-end gap-2">
                  <button
                    className="btn btn-outline-primary btn-sm"
                    onClick={() => {
                      setCurrentBudget(budget);
                      setIsEditing(true);
                      setShowBudgetModal(true);
                    }}
                  >
                    <PencilLine size={16} />
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDelete(budget.id)}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Budget Modal */}
      <div className={`modal fade ${showBudgetModal ? 'show d-block' : ''}`} tabIndex="-1" style={modalStyle}>
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{isEditing ? 'Edit Budget' : 'Add New Budget'}</h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => setShowBudgetModal(false)}
              ></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="mb-3">
                  <label className="form-label">Category</label>
                  <select
                    className="form-select"
                    value={currentBudget.category}
                    onChange={(e) => setCurrentBudget({ ...currentBudget, category: e.target.value })}
                    required
                  >
                    <option value="">Select category</option>
                    {categories.map((category) => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="form-label">Target Amount</label>
                  <input
                    type="number"
                    className="form-control"
                    value={currentBudget.targetAmount}
                    onChange={(e) => setCurrentBudget({ ...currentBudget, targetAmount: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Start Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={currentBudget.startDate}
                    onChange={(e) => setCurrentBudget({ ...currentBudget, startDate: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">End Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={currentBudget.endDate}
                    onChange={(e) => setCurrentBudget({ ...currentBudget, endDate: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowBudgetModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {isEditing ? 'Update Budget' : 'Add Budget'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      {showBudgetModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default BudgetManagement;