import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import toast from "react-hot-toast";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { registerUser, verifyOtp } from "../services/authService";
import { useResendOtp } from "../hooks/useResendOtp";
import { showLoading, hideLoading } from "../redux/slices/loadingSlice";
import { useDispatch } from "react-redux";

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
  });
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [showPassword, setShowPassword] = useState(false);
  const [step, setStep] = useState(1);
  const [errors, setErrors] = useState({});
  const [countdown, setCountdown] = useState(120);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { canResend, resend } = useResendOtp();
  const [isVerifyingOtp, setIsVerifyingOtp] = useState(false);
  const [isResendingOtp, setIsResendingOtp] = useState(false);

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const passwordRegex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;

  const validateForm = () => {
    let newErrors = {};
    if (!formData.firstName.trim())
      newErrors.firstName = "First name is required.";
    if (!formData.lastName.trim())
      newErrors.lastName = "Last name is required.";
    if (!formData.username.trim()) newErrors.username = "Username is required.";
    if (!formData.email.trim()) newErrors.email = "Email is required.";
    else if (!emailRegex.test(formData.email))
      newErrors.email = "Invalid email format.";
    if (!formData.password.trim()) newErrors.password = "Password is required.";
    else if (!passwordRegex.test(formData.password))
      newErrors.password =
        "Password must be 8-20 chars, include uppercase, lowercase, number & special char.";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
    setErrors((prevErrors) => ({ ...prevErrors, [id]: "" }));
  };

  const handleRegister = async () => {
    if (!validateForm()) return;
    setErrors({});
    dispatch(showLoading());
    try {
      await registerUser(formData);
      toast.success("OTP sent to registered email.");
      setStep(2);
      setCountdown(120);
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Try again later.";

      if (err.response?.status === 409) {
        setErrors((prevErrors) => ({
          ...prevErrors,
          username: errorMsg.includes("Username")
            ? "Username already taken."
            : prevErrors.username,
          email: errorMsg.includes("Email")
            ? "Email already in use."
            : prevErrors.email,
        }));
      } else {
        toast.error(errorMsg);
      }
    } finally {
      dispatch(hideLoading());
    }
  };

  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;
    const updatedOtp = [...otp];
    updatedOtp[index] = value;
    setOtp(updatedOtp);

    if (value && index < 5) {
      document.getElementById(`otp-${index + 1}`).focus();
    }

    if (errors.otp) {
      setErrors((prevErrors) => ({ ...prevErrors, otp: "" }));
    }
  };

  const handleKeyDown = (e, index) => {
    if (e.key === "Backspace") {
      const updatedOtp = [...otp];
      if (!updatedOtp[index] && index > 0) {
        document.getElementById(`otp-${index - 1}`).focus();
      }
      updatedOtp[index] = "";
      setOtp(updatedOtp);
    }
  };

  const handleVerifyOtp = async () => {
    if (otp.some((digit) => digit === "")) {
      setErrors({ otp: "OTP is required." });
      return;
    }
    setIsVerifyingOtp(true);
    try {
      await verifyOtp(formData.email, otp.join(""));
      toast.success("Registration successfully!");
      navigate("/login");
    } catch (err) {
      setErrors({ otp: "Incorrect OTP. Please try again." });
    } finally {
      setIsVerifyingOtp(false);
    }
  };

  const handleResendOtp = async () => {
    if (!canResend) return;
    setIsResendingOtp(true);
    try {
      await resend(formData.email, "REGISTRATION");
      toast.success("OTP sent successfully to your registered email.");
      setCountdown(120);
    } catch (err) {
      toast.error("Try again later.");
    } finally {
      setIsResendingOtp(false);
    }
  };

  useEffect(() => {
    if (countdown > 0 && step === 2) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown, step]);

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
        <h1
          className={`text-3xl font-extrabold text-center text-gray-800 ${
            isMobile ? "mt-4" : ""
          }`}
        >
          {step === 1 ? "Welcome Aboard!" : "Verify Your Email"}
        </h1>

        {/* Subtitle */}
        <p className="text-center text-gray-500 mb-4 text-sm">
          {step === 1
            ? "Join us and unlock exciting features. Let’s get started!"
            : "Enter the OTP sent to your email."}
        </p>

        {step === 1 && (
          <div className={`space-y-4 ${isMobile ? "px-2" : ""}`}>
            <div className="grid grid-cols-2 gap-2">
              {/* First Name */}
              <div className="flex flex-col">
                <label className="text-sm font-semibold text-gray-700">
                  First Name
                </label>
                <input
                  type="text"
                  id="firstName"
                  placeholder="First name"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  className={`w-full px-4 py-2 border ${
                    errors.firstName ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:ring-2 focus:ring-blue-400`}
                />
                {errors.firstName && (
                  <p className="text-red-500 text-xs">{errors.firstName}</p>
                )}
              </div>

              {/* Last Name */}
              <div className="flex flex-col">
                <label className="text-sm font-semibold text-gray-700">
                  Last Name
                </label>
                <input
                  type="text"
                  id="lastName"
                  placeholder="Last name"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  className={`w-full px-4 py-2 border ${
                    errors.lastName ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:ring-2 focus:ring-blue-400`}
                />
                {errors.lastName && (
                  <p className="text-red-500 text-xs">{errors.lastName}</p>
                )}
              </div>

              {/* Username (Full Width) */}
              <div className="flex flex-col col-span-2">
                <label className="text-sm font-semibold text-gray-700">
                  Username
                </label>
                <input
                  type="text"
                  id="username"
                  placeholder="Choose your username"
                  value={formData.username}
                  onChange={handleInputChange}
                  className={`w-full px-4 py-2 border ${
                    errors.username ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:ring-2 focus:ring-blue-400`}
                />
                {errors.username && (
                  <p className="text-red-500 text-xs">{errors.username}</p>
                )}
              </div>

              {/* Email (Full Width) */}
              <div className="flex flex-col col-span-2">
                <label className="text-sm font-semibold text-gray-700">
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  placeholder="Enter your email address"
                  value={formData.email}
                  onChange={handleInputChange}
                  className={`w-full px-4 py-2 border ${
                    errors.email ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:ring-2 focus:ring-blue-400`}
                />
                {errors.email && (
                  <p className="text-red-500 text-xs">{errors.email}</p>
                )}
              </div>

              {/* Password (Full Width) */}
              <div className="flex flex-col col-span-2">
                <label className="text-sm font-semibold text-gray-700">
                  Password
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? "text" : "password"}
                    id="password"
                    placeholder="Create a strong password"
                    value={formData.password}
                    onChange={handleInputChange}
                    className={`w-full px-4 py-2 border ${
                      errors.password ? "border-red-500" : "border-gray-300"
                    } rounded-md focus:ring-2 focus:ring-blue-400`}
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
            </div>

            {/* Register Button */}
            <button
              onClick={handleRegister}
              className="w-full bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700"
            >
              Register
            </button>
            <p className="text-center text-gray-600 text-sm mt-4">
              Already Signed up?{" "}
              <Link
                to="/login"
                className="text-blue-600 font-semibold hover:underline"
              >
                Login here
              </Link>
            </p>
          </div>
        )}

        {step === 2 && (
          <div>
            <p className="text-center text-gray-600 mb-4">
              Enter the 6-digit OTP sent to your registered email.
            </p>

            {/* OTP Input Boxes */}
            <div className="flex justify-center gap-2 mb-4">
              {otp.map((digit, index) => (
                <input
                  key={index}
                  id={`otp-${index}`}
                  type="text"
                  maxLength={1}
                  value={digit}
                  onChange={(e) => handleOtpChange(index, e.target.value)}
                  onKeyDown={(e) => handleKeyDown(e, index)}
                  className={`w-10 h-10 text-center border ${
                    errors.otp ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:ring-2 focus:ring-blue-400`}
                />
              ))}
            </div>

            {/* Display OTP Error */}
            {errors.otp && (
              <p className="text-red-500 text-sm text-center mb-2">
                {errors.otp}
              </p>
            )}

            {/* Resend OTP Button */}
            <div className="text-center mb-2">
              {countdown > 0 ? (
                <p className="text-gray-500 text-sm">
                  Resend OTP in {Math.floor(countdown / 60)}:
                  {("0" + (countdown % 60)).slice(-2)}
                </p>
              ) : (
                <button
                  onClick={handleResendOtp}
                  className={`text-blue-500 text-sm ${
                    isResendingOtp ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                  disabled={isResendingOtp}
                >
                  {isResendingOtp ? "Resending OTP..." : "Resend OTP"}
                </button>
              )}
            </div>

            {/* Verify OTP Button */}
            <button
              onClick={handleVerifyOtp}
              disabled={isVerifyingOtp}
              className={`mt-4 w-full py-2 text-white rounded-lg ${
                isVerifyingOtp
                  ? "bg-gray-400"
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
            >
              {isVerifyingOtp ? "Verifying OTP..." : "Verify OTP"}
            </button>
          </div>
        )}
      </div>
    );
  }
};

export default RegisterPage;
