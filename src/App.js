import React from "react";
import { Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Chat from "./components/Chat";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/chat" element={<Chat />} />
      {/* Можно добавить и другие маршруты */}
    </Routes>
  );
}

export default App;
