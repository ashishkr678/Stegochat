import React, { useState } from "react";
import { MessageCircle, Users, Download } from "lucide-react";
import RecentChat from "../../chats/RecentChat";
import FriendList from "../../FriendList";

const Sidebar = ({ isOpen, selectedChat, setSelectedChat }) => {
  const [selected, setSelected] = useState(1);

  const handleClick = (id) => {
    if (selected !== id) {
      setSelected(id);
    }
  };

  return (
    <div className="flex h-[calc(100vh-56px)] w-1/3">
      {/* Sidebar Navigation */}
      <div
        className={`fixed left-0 top-14 h-full bg-blue-200 shadow-lg transition-all duration-300 ${
          isOpen ? "w-52" : "w-14"
        } flex flex-col z-40`}
      >
        <nav className="flex flex-col space-y-6 w-full mt-4">
          <button
            onClick={() => handleClick(1)}
            className={`flex items-center space-x-3 text-gray-700 hover:bg-blue-300 p-2 rounded-md w-full transition-all duration-300 ${
              isOpen ? "justify-start pl-4" : "justify-center"
            }`}
          >
            <MessageCircle
              size={20}
              className={`${
                selected === 1 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
            {isOpen && <span>Chats</span>}
          </button>

          <button
            onClick={() => handleClick(2)}
            className={`flex items-center space-x-3 text-gray-700 hover:bg-blue-300 p-2 rounded-md w-full transition-all duration-300 ${
              isOpen ? "justify-start pl-4" : "justify-center"
            }`}
          >
            <Users
              size={20}
              className={`${
                selected === 2 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
            {isOpen && <span>Groups</span>}
          </button>
          <button
            onClick={() => handleClick(3)}
            className={`flex items-center space-x-3 text-gray-700 hover:bg-blue-300 p-2 rounded-md w-full transition-all duration-300 ${
              isOpen ? "justify-start pl-4" : "justify-center"
            }`}
          >
            <Download
              size={20}
              className={`${
                selected === 3 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
            {isOpen && <span>Downloads</span>}
          </button>
        </nav>
      </div>

      {/* Recent Chats Panel (Appears Beside Sidebar) */}
      {selected === 1 && (
        <div className="w-full mt-14 ml-14 bg-gray-100 shadow-md transition-all duration-300">
          <RecentChat
            selectedChat={selectedChat}
            setSelectedChat={setSelectedChat}
          />
        </div>
      )}

      {/* Recent Chats Panel (Appears Beside Sidebar) */}
      {selected === 2 && (
        <div className="w-full mt-14 ml-14 bg-gray-100 shadow-md transition-all duration-300">
          <FriendList />
        </div>
      )}
    </div>
  );
};

export default Sidebar;
