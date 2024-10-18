import React, { useState, useEffect } from "react";
import { PlusCircle, Trash2, PencilLine, DollarSign } from "lucide-react";
import apiClient from "../api/apiClient";

const InvestmentManagement = () => {
  const [investments, setInvestments] = useState([]);
  const [showInvestmentModal, setShowInvestmentModal] = useState(false);
  const [currentInvestment, setCurrentInvestment] = useState({
    name: "",
    amount: "",
  });
  const [isEditing, setIsEditing] = useState(false);
  const [showCloseModal, setShowCloseModal] = useState(false);
  const [closingAmount, setClosingAmount] = useState("");

  const fetchInvestments = async () => {
    try {
      const response = await apiClient.get("/investments");
      console.log(response.data);
      setInvestments(response.data);
    } catch (error) {
      console.error("Error fetching investments:", error);
    }
  };

  useEffect(() => {
    fetchInvestments();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = isEditing
      ? `/investments/${currentInvestment.id}`
      : "/investments";
    const method = isEditing ? "put" : "post";

    try {
      await apiClient({
        method,
        url,
        data: currentInvestment,
      });

      setShowInvestmentModal(false);
      fetchInvestments();
      setCurrentInvestment({
        name: "",
        amount: "",
      });
      setIsEditing(false);
    } catch (error) {
      console.error("Error saving investment:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiClient.delete(`/investments/${id}`);
      fetchInvestments();
    } catch (error) {
      console.error("Error deleting investment:", error);
    }
  };

  const handleCloseInvestment = async () => {
    try {
      await apiClient.post(`/investments/${currentInvestment.id}/close`, {
        profitLoss: parseFloat(closingAmount) - currentInvestment.amount,
      });
      setShowCloseModal(false);
      fetchInvestments();
    } catch (error) {
      console.error("Error closing investment:", error);
    }
  };

  const modalStyle = {
    top: "80px",
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="h4">Investment Management</h2>
        <button
          className="btn btn-primary d-flex align-items-center"
          onClick={() => {
            setIsEditing(false);
            setCurrentInvestment({
              name: "",
              amount: "",
            });
            setShowInvestmentModal(true);
          }}
        >
          <PlusCircle size={20} className="me-2" />
          Add Investment
        </button>
      </div>

      <div className="row g-4">
        {investments.map((investment) => (
          <div key={investment.id} className="col-md-6 col-lg-4">
            <div className="card h-100">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0">{investment.name}</h5>
                <span className="badge bg-primary">${investment.amount}</span>
              </div>
              <div className="card-body">
                <p className="mb-2">
                  Created:{" "}
                  {new Date(...investment.createdAt).toLocaleDateString()}
                </p>
                {investment.isActive ? (
                  <p className="text-success">Active</p>
                ) : (
                  <div>
                    <p className="text-muted">
                      Closed:{" "}
                      {new Date(...investment.closedAt).toLocaleDateString()}
                    </p>
                    <p
                      className={
                        investment.profitLoss >= 0
                          ? "text-success"
                          : "text-danger"
                      }
                    >
                      Profit/Loss: ${investment.profitLoss}
                    </p>
                  </div>
                )}
                <div className="d-flex justify-content-end gap-2">
                  {investment.isActive && (
                    <>
                      <button
                        className="btn btn-outline-success btn-sm"
                        onClick={() => {
                          setCurrentInvestment(investment);
                          setClosingAmount("");
                          setShowCloseModal(true);
                        }}
                      >
                        <DollarSign size={16} />
                      </button>
                    </>
                  )}
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDelete(investment.id)}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Investment Modal */}
      <div
        className={`modal fade ${showInvestmentModal ? "show d-block" : ""}`}
        tabIndex="-1"
        style={modalStyle}
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                {isEditing ? "Edit Investment" : "Add New Investment"}
              </h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => setShowInvestmentModal(false)}
              ></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="mb-3">
                  <label className="form-label">Name</label>
                  <input
                    type="text"
                    className="form-control"
                    value={currentInvestment.name}
                    onChange={(e) =>
                      setCurrentInvestment({
                        ...currentInvestment,
                        name: e.target.value,
                      })
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Amount</label>
                  <input
                    type="number"
                    className="form-control"
                    value={currentInvestment.amount}
                    onChange={(e) =>
                      setCurrentInvestment({
                        ...currentInvestment,
                        amount: e.target.value,
                      })
                    }
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowInvestmentModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {isEditing ? "Update Investment" : "Add Investment"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Close Investment Modal */}
      <div
        className={`modal fade ${showCloseModal ? "show d-block" : ""}`}
        tabIndex="-1"
        style={modalStyle}
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Close Investment</h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => setShowCloseModal(false)}
              ></button>
            </div>
            <div className="modal-body">
              <p>Initial investment: ${currentInvestment.amount}</p>
              <div className="mb-3">
                <label className="form-label">Closing Amount</label>
                <input
                  type="number"
                  className="form-control"
                  value={closingAmount}
                  onChange={(e) => setClosingAmount(e.target.value)}
                  required
                />
              </div>
              {closingAmount && (
                <p
                  className={
                    parseFloat(closingAmount) >= currentInvestment.amount
                      ? "text-success"
                      : "text-danger"
                  }
                >
                  Profit/Loss: $
                  {(
                    parseFloat(closingAmount) - currentInvestment.amount
                  ).toFixed(2)}
                </p>
              )}
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowCloseModal(false)}
              >
                Cancel
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleCloseInvestment}
              >
                Close Investment
              </button>
            </div>
          </div>
        </div>
      </div>

      {(showInvestmentModal || showCloseModal) && (
        <div className="modal-backdrop fade show"></div>
      )}
    </div>
  );
};

export default InvestmentManagement;