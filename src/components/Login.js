import React, { useState } from "react";
import axios from "axios";
import './Login.css';

const Login = ({ onLogin }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    try {
      const response = await axios.post("http://localhost:7990/auth/signIn", {
        email,
        password,
      });

      const { token } = response.data;

      // Сохраняем токен и передаем в родительский компонент
      localStorage.setItem("token", token);
      onLogin(token);
    } catch (error) {
      setError("Invalid email or password");
      console.error("Login error:", error);
    }
  };

  return (
    <div className="login-container">
      <h1>Login</h1>
      {error && <p className="error">{error}</p>}
      <div>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="input"
        />
      </div>
      <div>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="input"
        />
      </div>
      <button onClick={handleLogin} className="btn">
        Login
      </button>
    </div>
  );
};

export default Login;
