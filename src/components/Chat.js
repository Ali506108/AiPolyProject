import React, { useState, useRef } from "react";
import axios from "axios";
import { TransitionGroup, CSSTransition } from "react-transition-group";
import "./Chat.css";

const Chat = ({ token, onLogout }) => {
  const chatId = 1;
  const [userMessage, setUserMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const [image, setImage] = useState(null);
  const [isLoading, setIsLoading] = useState(false); // Для индикатора загрузки
  const [showImageModal, setShowImageModal] = useState(false);

  // Создаём массив рефов для сообщений
  const messageRefs = useRef([]);

  const handleSendMessage = async () => {
    if (!userMessage.trim()) return;

    setIsLoading(true); // Начало загрузки

    try {
      const response = await axios.post(
        `http://localhost:7565/chat/${chatId}/send`,
        { userMessage }, // Убедитесь, что сервер ожидает JSON-объект
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const { userMessage: userMsg, aiResponse, image: img } = response.data;

      setMessages((prev) => [
        ...prev,
        { content: userMsg.content, isUser: true },
        { content: aiResponse.content, isUser: false },
      ]);

      if (img) setImage(img);
      setUserMessage("");
    } catch (error) {
      console.error("Error sending message:", error);
    } finally {
      setIsLoading(false); // Завершение загрузки
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSendMessage();
    }
  };

  const closeModal = () => {
    setShowImageModal(false);
  };

  const openModal = () => {
    if (image) setShowImageModal(true);
  };

  return (
    <div className="chat-container">
      <div className="header">
        <h2>AI Chat</h2>
        <button onClick={onLogout} className="btn logout">
          Logout
        </button>
      </div>

      {/* Анимация для списка сообщений */}
      <TransitionGroup className="messages">
        {messages.map((msg, index) => {
          // Если реф для сообщения ещё не существует, создаём его
          if (!messageRefs.current[index]) {
            messageRefs.current[index] = React.createRef();
          }
          return (
            <CSSTransition
              key={index}
              nodeRef={messageRefs.current[index]}
              timeout={300}
              classNames="fade"
            >
              <div
                ref={messageRefs.current[index]}
                className={`message ${msg.isUser ? "user" : "ai"}`}
              >
                <strong>{msg.isUser ? "You:" : "AI:"}</strong> {msg.content}
              </div>
            </CSSTransition>
          );
        })}
        {isLoading && (
          <div className="loading-indicator">
            <span>Generating response...</span>
          </div>
        )}
      </TransitionGroup>

      {image && (
        <div className="image-container">
          <h3>Generated Image:</h3>
          <img
            src={image}
            alt="Generated"
            style={{ cursor: "pointer" }}
            onClick={openModal}
          />
        </div>
      )}

      {/* Модальное окно для картинки */}
      {showImageModal && (
        <div className="modal-backdrop" onClick={closeModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <img src={image} alt="Large preview" />
          </div>
        </div>
      )}

      <div className="input-container">
        <input
          type="text"
          value={userMessage}
          onChange={(e) => setUserMessage(e.target.value)}
          placeholder="Type your message..."
          className="input"
          onKeyDown={handleKeyDown}
        />
        <button onClick={handleSendMessage} className="btn send-btn">
          Send
        </button>
      </div>
    </div>
  );
};

export default Chat;
