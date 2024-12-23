import React, { useState } from "react";
import Login from "./components/Login";
import Chat from "./components/Chat";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));

  const handleLogin = (token) => {
    setToken(token); // Устанавливаем токен
  };

  return (
    <div className="app">
      {token ? <Chat token={token} /> : <Login onLogin={handleLogin} />}
    </div>
  );
}

export default App;
