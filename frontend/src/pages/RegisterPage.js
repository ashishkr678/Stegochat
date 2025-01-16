import React, { useState } from "react";
import { FaEye, FaEyeSlash, FaGoogle } from "react-icons/fa";

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
      <div className="hidden sm:block w-full max-w-sm bg-gradient-to-br from-blue-100 via-blue-50 to-white shadow-2xl rounded-xl p-6 md:p-8">
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
            isMobile ? "mt-4" : "mb-2"
          }`}
        >
          Welcome Aboard!
        </h1>

        {/* Subtitle */}
        <p className="text-center text-gray-500 mb-4 text-sm">
          Join us and unlock exciting features. Let’s get started!
        </p>

        {/* Form */}
        <div className="space-y-2 px-2">
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
        <div className="mt-4 px-2">
          <button
            onClick={handleRegister}
            className="w-full py-2 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 text-sm"
          >
            Register
          </button>
          <div className="flex items-center my-2">
            <hr className="flex-grow border-gray-300" />
            <span className="px-4 text-gray-500 text-sm">or</span>
            <hr className="flex-grow border-gray-300" />
          </div>
          <button
            onClick={handleRegisterWithGoogle}
            className="w-full py-2 bg-amber-500 text-white flex items-center justify-center rounded-lg shadow hover:bg-amber-600 text-sm"
          >
            <FaGoogle className="mr-2" /> Sign up with Google
          </button>
        </div>
      </div>
    );
  }
};

export default RegisterPage;
