import React, { useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WebSocketComponent = () => {
  const [stompClient, setStompClient] = useState(null);

  // ✅ Login Function
  const loginUser1 = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: "adminUser", password: "1234" }),
        credentials: "include",
      });

      const data = await response.json();
      console.log("✅ Login Successful:", data);
    } catch (error) {
      console.error("❌ Login Error:", error);
    }
  };

  // ✅ Login Function
  const loginUser2 = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: "user2", password: "Admin@123" }),
        credentials: "include",
      });

      const data = await response.json();
      console.log("✅ Login Successful:", data);
    } catch (error) {
      console.error("❌ Login Error:", error);
    }
  };

  const fetchUserProfile = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/users/profile", {
        method: "GET",
        credentials: "include",
      });

      const data = await response.json();
      console.log("✅ User Profile Data:", data);
    } catch (error) {
      console.error("Error fetching user profile:", error);
    }
  }

  // ✅ Connect to WebSocket (with Cookies)
  const connectWebSocket = () => {
    const socket = new SockJS("http://localhost:8080/ws", null, {
      withCredentials: true,
    });

    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {},
      onConnect: (frame) => {
        console.log("✅ Connected to WebSocket:", frame);
        alert("Connected to WebSocket!");
        setStompClient(client);
      },
      onStompError: (error) => {
        console.error("❌ WebSocket Connection Error:", error);
      },
    });

    client.activate();
  };

  // ✅ Logout Function (WebSocket disconnect is handled in backend)
  const logout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/users/logout", {
        method: "POST",
        credentials: "include",
      });

      const data = await response.json();
      console.log("✅ Logout Successful:", data);
    } catch (error) {
      console.error("❌ Logout Error:", error);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
      <div className="bg-white shadow-lg rounded-xl p-6 w-full max-w-md">
        <h2 className="text-xl font-bold text-gray-800 text-center mb-4">
          STOMP WebSocket Test (React)
        </h2>

        <div className="flex flex-col gap-4">
          <button
            onClick={loginUser1}
            className="bg-blue-500 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-600 transition"
          >
            Login User 1
          </button>
          <button
            onClick={loginUser2}
            className="bg-blue-500 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-600 transition"
          >
            Login User 2
          </button>
          <button
            onClick={fetchUserProfile}
            className="bg-green-500 text-white font-semibold py-2 px-4 rounded-lg hover:bg-green-600 transition"
          >
            Get user profile
          </button>
          <button
            onClick={connectWebSocket}
            className="bg-green-500 text-white font-semibold py-2 px-4 rounded-lg hover:bg-green-600 transition"
          >
            Connect to WebSocket
          </button>
          <button
            onClick={logout}
            className="bg-gray-700 text-white font-semibold py-2 px-4 rounded-lg hover:bg-gray-800 transition"
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  );
};

export default WebSocketComponent;
