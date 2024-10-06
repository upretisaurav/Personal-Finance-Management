import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Welcome from "./pages/Welcome";
import Navbar from "./components/common/Navbar";
import ExpenseManagement from "./pages/ExpenseManagement";

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/welcome" element={<Welcome />} />
        <Route path="/dashboard" element={<ExpenseManagement />} />
      </Routes>
    </Router>
  );
}

export default App;
