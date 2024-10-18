import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ExpenseManagement from "./pages/ExpenseManagement";
import BudgetManagement from "./pages/BudgetManagement";
import InvestmentManagement from "./pages/InvestmentManagement";
import AuthenticatedLayout from "./pages/AuthenticatedLayout";

function App() {
  const isAuthenticated = !!localStorage.getItem("jwtToken");

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {isAuthenticated ? (
          <Route element={<AuthenticatedLayout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/budgets" element={<BudgetManagement />} />
            <Route path="/expenses" element={<ExpenseManagement />} />
            <Route path="/investments" element={<InvestmentManagement />} />
          </Route>
        ) : (
          <Route path="*" element={<Navigate to="/login" replace />} />
        )}
      </Routes>
    </Router>
  );
}

export default App;