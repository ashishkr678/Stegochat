import React, { useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";

const LoginPage = () => {
  const [formData, setFormData] = useState({
    emailOrUsername: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
  };

  const handleLogin = () => {
    alert("Login functionality to be implemented!");
  };

  const handleLoginWithGoogle = () => {
    alert("Google login functionality to be implemented!");
  };

  const handleForgotPassword = () => {
    alert("Forgot password functionality to be implemented!");
  };

  return (
    <div className="h-screen bg-gradient-to-b from-blue-200 via-blue-100 to-white flex items-center justify-center px-4 sm:px-0">
      {/* For larger screens, show the container */}
      <div className="hidden sm:block w-full max-w-sm bg-gradient-to-br from-blue-100 via-blue-50 to-white shadow-2xl rounded-xl p-5">
        {/* Common Content */}
        {renderContent()}
      </div>

      {/* For mobile view, show content directly */}
      <div className="block sm:hidden w-full">{renderContent(true)}</div>
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
          Hello Again!
        </h1>

        {/* Subtitle */}
        <p className="text-center text-gray-500 mb-4 text-sm">
          Your safe and private chats are eagerly awaiting. Let’s dive in!
        </p>

        {/* Form */}
        <div className={`space-y-2 ${isMobile ? "px-2" : ""}`}>
          {/* Email or Username Input */}
          <div>
            <h2 className="text-sm font-semibold text-gray-700 mb-1">
              Email or Username
            </h2>
            <input
              type="text"
              id="emailOrUsername"
              value={formData.emailOrUsername}
              onChange={handleInputChange}
              placeholder="Email or Username"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          {/* Password Input */}
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
            {/* Forgot Password */}
            <div className="text-right">
              <button
                onClick={handleForgotPassword}
                className="text-gray-500 text-sm font-medium hover:text-gray-700"
              >
                Forgot Password?
              </button>
            </div>
          </div>
        </div>

        {/* Actions Section */}
        <div className={`mt-4 ${isMobile ? "px-2" : ""}`}>
          <button
            onClick={handleLogin}
            className="w-full bg-blue-600 text-white py-[8px] px-[12px] rounded-[8px] font-medium hover:bg-blue-700"
          >
            Get Started
          </button>
          <div className="flex items-center my-1">
            <hr className="flex-grow border-gray-300" />
            <span className="px-4 text-gray-400 font-medium text-sm">or</span>
            <hr className="flex-grow border-gray-300" />
          </div>
          <button
            onClick={handleLoginWithGoogle}
            className="w-full flex items-center justify-center rounded-[8px] font-medium text-gray-900 border-gray-300 bg-yellow-500 hover:bg-yellow-600 border px-[12px] py-[8px] gap-x-2"
          >
            <FcGoogle />
            <p>Sign in with Google</p>
          </button>
        </div>
      </div>
    );
  }
};

export default LoginPage;
