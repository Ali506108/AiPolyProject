import React, { useState } from "react";
import axios from "axios";
import './Chat.css';

const Chat = ({ token }) => {
  const chatId = 1;
  const [userMessage, setUserMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const [image, setImage] = useState(null);

  const sendMessage = async () => {
    if (!userMessage.trim()) return;

    try {
      const response = await axios.post(
        `http://localhost:7565/chat/${chatId}/send`,
        { userMessage },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // Response should look like:
      // {
      //   "userMessage": { "content": "User's message" },
      //   "aiResponse": { "content": "AI's response" },
      //   "image": "http://example.com/image.png"
      // }
      const { userMessage: userMsg, aiResponse, image: img } = response.data;

      setMessages((prev) => [
        ...prev,
        { content: userMsg.content, isUser: true },
        { content: aiResponse.content, isUser: false },
      ]);

      if (img) {
        setImage(img);
      }

      setUserMessage("");
    } catch (error) {
      console.error("Error sending message:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.reload();
  };

  return (
    <div className="chat-container">
      <h1>AI Chat</h1>
      <button onClick={handleLogout} className="btn logout">
        Logout
      </button>
      <div className="messages">
        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.isUser ? "user" : "ai"}`}>
            <strong>{msg.isUser ? "You:" : "AI:"}</strong> {msg.content}
          </div>
        ))}
      </div>

      {image && (
        <div className="image-container">
          <h3>Generated Image:</h3>
          <img src={image} alt="Generated" />
        </div>
      )}

      <div className="input-container">
        <input
          type="text"
          value={userMessage}
          onChange={(e) => setUserMessage(e.target.value)}
          placeholder="Type your message..."
          className="input"
        />
        <button onClick={sendMessage} className="btn">
          Send
        </button>
      </div>
    </div>
  );
};

export default Chat;
