import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";
import { showLoading, hideLoading } from "../redux/slices/loadingSlice";
import { userLogin } from "../redux/slices/authSlice";

const LoginPage = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const validateForm = () => {
    let newErrors = {};
    if (!formData.username.trim()) newErrors.username = "Username is required.";
    if (!formData.password.trim()) newErrors.password = "Password is required.";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
    setErrors((prevErrors) => ({ ...prevErrors, [id]: "" }));
    if (errors.general) {
      setErrors((prevErrors) => ({ ...prevErrors, general: "" }));
    }
  };

  const handleLogin = async () => {
    if (!validateForm()) return;

    setIsLoggingIn(true);
    dispatch(showLoading());

    try {
      await dispatch(userLogin(formData)).unwrap();
      toast.success("Login successful!");
      navigate("/");
    } catch (err) {
      setErrors({ general: err || "Invalid username or password." });
    } finally {
      setIsLoggingIn(false);
      dispatch(hideLoading());
    }
  };

  const handleForgotPassword = () => {
    toast.info("Forgot password functionality coming soon!");
  };

  return (
    <div className="h-screen bg-gradient-to-b from-blue-200 via-blue-100 to-white flex items-center justify-center px-4 sm:px-0">
      {/* For larger screens, show the container */}
      <div className="hidden sm:block w-full max-w-sm bg-gradient-to-br from-blue-100 via-blue-50 to-white shadow-2xl rounded-xl p-5">
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
        <h1 className="text-3xl font-extrabold text-center text-gray-800 mt-4">
          Hello Again!
        </h1>

        {/* Subtitle */}
        <p className="text-center text-gray-500 mb-4 text-sm">
          Your safe and private chats are eagerly awaiting. Let’s dive in!
        </p>

        {/* Login Form */}
        <div className={`space-y-2 ${isMobile ? "px-2" : ""}`}>
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
              placeholder="Enter your username"
              className={`w-full px-4 py-2 border ${
                errors.emailOrUsername ? "border-red-500" : "border-gray-300"
              } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400`}
            />
            {errors.username && (
              <p className="text-red-500 text-xs">{errors.username}</p>
            )}
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
                placeholder="Enter your password"
                className={`w-full px-4 py-2 border ${
                  errors.password ? "border-red-500" : "border-gray-300"
                } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400`}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute inset-y-0 right-3 flex items-center text-gray-500"
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </button>
            </div>
            {errors.password && (
              <p className="text-red-500 text-xs">{errors.password}</p>
            )}
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

        {/* General Error (Invalid Credentials) */}
        {errors.general && (
          <p className="text-red-500 text-sm text-center mt-2">
            {errors.general}
          </p>
        )}

        {/* Actions Section */}
        <div className={`mt-4 ${isMobile ? "px-2" : ""}`}>
          <button
            onClick={handleLogin}
            disabled={isLoggingIn}
            className={`w-full py-2 text-white rounded-lg ${
              isLoggingIn
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-blue-500 hover:bg-blue-600"
            }`}
          >
            {isLoggingIn ? "Logging in..." : "Get Started"}
          </button>
        </div>

        {/* Already have an account? Register */}
        <p className="text-center text-gray-600 text-sm mt-4">
          Don't have an account?{" "}
          <Link
            to="/register"
            className="text-blue-600 font-semibold hover:underline"
          >
            Register here
          </Link>
        </p>
      </div>
    );
  }
};

export default LoginPage;
