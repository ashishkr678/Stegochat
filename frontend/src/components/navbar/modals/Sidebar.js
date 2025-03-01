import React, { useState } from "react";
import { MessageCircle, Users, Download } from "lucide-react";
import { Link } from "react-router-dom";

const Sidebar = ({ isOpen }) => {
  const [selected, setSelected] = useState(null);

  const handleClick = (id) => {
    setSelected(id === selected ? null : id);
  };

  return (
    <div
      className={`fixed left-0 top-14 h-[calc(100vh-56px)] bg-blue-200 shadow-lg transition-all duration-300 ${
        isOpen ? "w-52" : "w-14"
      } flex flex-col items-center z-50`}
    >
      <nav className="flex flex-col space-y-6 w-full mt-4">
        <button
          onClick={() => handleClick(1)}
          className={`flex items-center space-x-3 text-gray-700 hover:bg-blue-300 p-2 rounded-md w-full transition-all duration-300 ${
            isOpen ? "justify-start pl-4" : "justify-center"
          }`}
        >
          <Link to="/">
            <MessageCircle
              size={20}
              className={`${
                selected === 1 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
            {isOpen && <span>Chats</span>}
          </Link>
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
  );
};

export default Sidebar;
