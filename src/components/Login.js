import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  
  // Хук для навигации
  const navigate = useNavigate();

  const handleLogin = async () => {
    setError("");

    if (!email.trim() || !password.trim()) {
      setError("Please fill in both fields.");
      return;
    }

    try {
      const response = await axios.post("http://localhost:7990/auth/signIn", {
        email,
        password,
      });
      
      /**
       * ВАЖНО:
       * Убедитесь, что бэкенд при успешном входе возвращает именно поле "token".
       * Например: { token: "jwt_token_here" }
       */
      const { token } = response.data;

      if (!token) {
        // Если token не найден в ответе — показываем ошибку и перенаправляем
        setError("No token received. Check the response structure on the server side.");
        
        // Перенаправляем, например, на страницу регистрации
        setTimeout(() => {
          navigate("/chat");
        }, 2000); // через 2 секунды отправим на /register

        return;
      }

      // Если token получен — сохраняем в localStorage и идём в чат
      localStorage.setItem("token", token);

      // Перенаправляем на /chat
      navigate("/chat");
    } catch (err) {
      console.error("Login error:", err);
      setError("Invalid email or password");
    }
  };

  return (
    <div className="login-container">
      <h2>Login to AI Chat</h2>
      {error && <p className="error">{error}</p>}

      <div>
        <input
          type="email"
          placeholder="Email..."
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="input"
        />
      </div>

      <div>
        <input
          type="password"
          placeholder="Password..."
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="input"
        />
      </div>

      <button onClick={handleLogin} className="btn login-btn">
        Login
      </button>
    </div>
  );
};

export default Login;
