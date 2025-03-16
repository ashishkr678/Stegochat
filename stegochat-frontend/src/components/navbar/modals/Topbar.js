import React, { useState } from "react";
import { Menu, Users, Bell, Search, LogOut } from "lucide-react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { userLogout } from "../../../redux/slices/authSlice";

const Topbar = ({ toggleSidebar }) => {
  const [selected, setSelected] = useState(null);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleClick = (id) => {
    setSelected(id === selected ? null : id);
  };

  const handleLogout = () => {
    dispatch(userLogout());
    navigate("/login");
  };

  return (
    <header className="bg-blue-200 fixed top-0 left-0 w-full z-50 h-14 flex items-center shadow-md">
      <div className="container mx-auto flex items-center justify-between px-4">
        {/* Left Section: Hamburger Menu + Branding */}
        <div className="flex items-center space-x-4">
          <button
            onClick={toggleSidebar}
            className="transition-transform transform active:scale-90"
          >
            <Menu size={24} className="text-gray-700" />
          </button>
          <h1 className="text-xl font-semibold text-gray-900">StegoChat</h1>
        </div>

        {/* Simple Fully Rounded Search Bar */}
        <div className="relative w-1/3">
          <input
            type="text"
            placeholder="Search"
            className="w-full px-4 py-2 pr-12 rounded-full border border-gray-600 bg-blue-200 focus:outline-none focus:ring-2 focus:ring-blue-300 transition-all"
          />
          {/* Search Icon Inside the Input */}
          <button className="absolute right-3 top-1/2 transform -translate-y-1/2">
            <Search className="text-gray-700" />
          </button>
        </div>

        {/* Right Section: Icons */}
        <div className="flex items-center space-x-6">
          <button
            onClick={() => handleClick(1)}
            className="text-gray-700 hover:text-gray-900 transition-all duration-300"
          >
            <Users
              size={22}
              className={`${
                selected === 1 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
          </button>
          <button
            onClick={() => handleClick(2)}
            className="text-gray-700 hover:text-gray-900 transition-all duration-300"
          >
            <Bell
              size={22}
              className={`${
                selected === 2 ? "fill-current text-gray-700" : "text-gray-700"
              }`}
            />
          </button>
          <img
            src="https://via.placeholder.com/40"
            alt="Profile"
            className="w-10 h-10 rounded-full cursor-pointer transition-transform transform hover:scale-105"
          />

          {/* Logout Button */}
          <button
            onClick={handleLogout}
            className="flex items-center text-red-600 hover:text-red-700 transition-all duration-300"
          >
            <LogOut size={22} className="mr-1" />
            <span className="text-sm font-medium">Logout</span>
          </button>
        </div>
      </div>
    </header>
  );
};

export default Topbar;
