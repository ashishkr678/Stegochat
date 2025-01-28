import React, { useState } from "react";
import { FaEye, FaEyeSlash, FaGoogle } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
  };

  const handleRegister = () => {
    alert("Register functionality to be implemented!");
  };

  const handleRegisterWithGoogle = () => {
    alert("Google register functionality to be implemented!");
  };

  return (
    <div className="h-screen bg-gradient-to-b from-blue-200 via-blue-100 to-white flex items-center justify-center px-4 sm:px-0">
      {/* For larger screens, show the container */}
      <div className="hidden sm:block w-full max-w-sm bg-gradient-to-br from-blue-100 via-blue-50 to-white shadow-2xl rounded-xl p-5">
        {renderContent()}
      </div>

      {/* For mobile view, show content directly */}
      <div className="block sm:hidden w-full">
        {renderContent(true)}
      </div>
    </div>
  );

  function renderContent(isMobile = false) {
    return (
      <div className={isMobile ? "space-y-4" : ""}>
        {/* Title */}
        <h1
          className={`text-3xl font-extrabold text-center text-gray-800 ${
            isMobile ? "mt-4" : ""
          }`}
        >
          Welcome Aboard!
        </h1>

        {/* Subtitle */}
        <p className="text-center text-gray-500 mb-4 text-sm">
          Join us and unlock exciting features. Let’s get started!
        </p>

        {/* Form */}
        <div className={`space-y-2 ${isMobile ? "px-2" : ""}`}>
          {/* First Name and Last Name */}
          <div className="flex space-x-4">
            <div className="w-1/2">
              <h2 className="text-sm font-semibold text-gray-700 mb-1">
                First Name
              </h2>
              <input
                type="text"
                id="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                placeholder="First Name"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
            <div className="w-1/2">
              <h2 className="text-sm font-semibold text-gray-700 mb-1">
                Last Name
              </h2>
              <input
                type="text"
                id="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                placeholder="Last Name"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>
          </div>

          {/* Username */}
          <div>
            <h2 className="text-sm font-semibold text-gray-700 mb-1">
              Username
            </h2>
            <input
              type="text"
              id="username"
              value={formData.username}
              onChange={handleInputChange}
              placeholder="Username"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          {/* Email */}
          <div>
            <h2 className="text-sm font-semibold text-gray-700 mb-1">Email</h2>
            <input
              type="email"
              id="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="Email"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          {/* Password */}
          <div>
            <h2 className="text-sm font-semibold text-gray-700 mb-1">
              Password
            </h2>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                value={formData.password}
                onChange={handleInputChange}
                placeholder="Password"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute inset-y-0 right-3 flex items-center text-gray-500"
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </button>
            </div>
          </div>
        </div>

        {/* Actions Section */}
        <div className={`mt-4 ${isMobile ? "px-2" : ""}`}>
          <button
            onClick={handleRegister}
            className="w-full bg-blue-600 text-white py-[8px] px-[12px] rounded-[8px] font-medium hover:bg-blue-700"
          >
            Register
          </button>
          <div className="flex items-center my-1">
            <hr className="flex-grow border-gray-300" />
            <span className="px-4 text-gray-400 text-sm">or</span>
            <hr className="flex-grow border-gray-300" />
          </div>
          <button
            onClick={handleRegisterWithGoogle}
            className="w-full flex items-center justify-center rounded-[8px] font-medium text-gray-900 border-gray-300 bg-yellow-500 hover:bg-yellow-600 border px-[12px] py-[8px] gap-x-2"
          >
            <FcGoogle className="mr-2" /> Sign up with Google
          </button>
        </div>
      </div>
    );
  }
};

export default RegisterPage;
