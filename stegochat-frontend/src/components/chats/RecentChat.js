import React from "react";
import { User } from "lucide-react";

const dummyChats = [
  { id: 1, name: "Alice", lastMessage: "Hey there!", time: "10:30 AM" },
  { id: 2, name: "Bob", lastMessage: "Let's meet today.", time: "09:15 AM" },
  { id: 3, name: "Charlie", lastMessage: "See you later!", time: "Yesterday" },
  { id: 4, name: "David", lastMessage: "Good morning!", time: "08:00 AM" },
  { id: 5, name: "Eve", lastMessage: "How are you?", time: "07:45 AM" },
  { id: 6, name: "Frank", lastMessage: "Call me back.", time: "Monday" },
  { id: 7, name: "Grace", lastMessage: "Thanks!", time: "Sunday" },
  { id: 8, name: "Hannah", lastMessage: "Let's catch up!", time: "Saturday" },
  { id: 9, name: "Ian", lastMessage: "See you soon!", time: "Friday" },
  { id: 10, name: "Jack", lastMessage: "Meeting at 3 PM", time: "Thursday" }
];

const RecentChat = ({ selectedChat, setSelectedChat }) => {
  return (
    <div className="bg-gray-100 border-r h-[calc(100vh-56px)] flex flex-col w-full">
      {/* Fixed Heading */}
      <div className="p-3 border-b bg-gray-100 sticky top-0 z-10">
        <h2 className="text-xl font-semibold">Chats</h2>
      </div>

      {/* Scrollable Chat List */}
      <div className="flex-1 overflow-y-auto p-4">
        {dummyChats.map((chat) => (
          <div
            key={chat.id}
            onClick={() => setSelectedChat(chat)}
            className={`flex items-center p-3 rounded-lg cursor-pointer hover:bg-gray-200 transition ${
              selectedChat?.id === chat.id ? "bg-gray-300" : ""
            }`}
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
    </div>
  );
};

export default RecentChat;
