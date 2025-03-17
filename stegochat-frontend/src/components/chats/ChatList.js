import React from "react";
import { User } from "lucide-react";

const dummyChats = [
  { id: 1, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 2, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 3, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
  { id: 4, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 5, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 6, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
  { id: 7, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 8, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 9, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
  { id: 10, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 11, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 12, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
  { id: 13, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 14, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 15, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
];

const ChatList = ({ selectedChat, setSelectedChat, isSidebarOpen }) => {
  return (
    <div className={`w-1/4 bg-gray-100 border-r h-[calc(100vh-56px)] p-4 overflow-y-auto ${isSidebarOpen ? "hidden" : "block"}`}>
      <h2 className="text-xl font-semibold mb-4">Chats</h2>
      {dummyChats.map((chat) => (
        <div
          key={chat.id}
          onClick={() => setSelectedChat(chat)}
          className={`flex items-center p-3 rounded-lg cursor-pointer hover:bg-gray-200 ${selectedChat?.id === chat.id ? "bg-gray-300" : ""}`}
        >
          <User className="w-8 h-8 mr-3 text-gray-600" />
          <div className="flex-1">
            <h3 className="font-medium">{chat.name}</h3>
            <p className="text-sm text-gray-500">{chat.lastMessage}</p>
          </div>
          <span className="text-xs text-gray-400">{chat.time}</span>
        </div>
      ))}
    </div>
  );
};

export default ChatList;