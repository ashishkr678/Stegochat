import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { useDispatch } from "react-redux";
import { useAuth } from "../hooks/useAuth";
import { useForgotPassword } from "../hooks/useForgotPassword";
import { useResendOtp } from "../hooks/useResendOtp";
import { hideLoading, showLoading } from "../redux/slices/loadingSlice";

const ForgotPasswordPage = () => {
  const dispatch = useDispatch();
  const { sendOtp, verifyOtp, resetPassword, resetState } = useForgotPassword();

  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const { canResend, resend } = useResendOtp();
  const { logout } = useAuth();
  const [isResendingOtp, setIsResendingOtp] = useState(false);
  const [isVerifyingOtp, setIsVerifyingOtp] = useState(false);
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [countdown, setCountdown] = useState(120);

  const validateForm = () => {
    let newErrors = {};
    if (!formData.username.trim()) newErrors.username = "Username is required.";
    if (!formData.email.trim()) newErrors.email = "Email is required.";
    if (step === 3) {
      const passwordRegex =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
      if (!formData.newPassword.trim())
        newErrors.newPassword = "New password is required.";
      else if (!passwordRegex.test(formData.newPassword))
        newErrors.newPassword =
          "Password must be 8+ chars, include uppercase, lowercase, number & special char.";
      if (!formData.confirmPassword.trim())
        newErrors.confirmPassword = "Confirm password is required";
      if (formData.newPassword !== formData.confirmPassword)
        newErrors.confirmPassword = "Passwords do not match.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
    setErrors((prevErrors) => ({ ...prevErrors, [id]: "" }));
  };

  const handleSendOtp = async () => {
    if (!validateForm()) return;
    console.log("Sending OTP for:", formData.username);
    dispatch(showLoading());

    try {
      const response = sendOtp(formData.username);
      console.log("OTP API Response:", response);

      if (!response?.payload?.email)
        throw new Error("Invalid server response.");
      setFormData((prev) => ({ ...prev, email: response.payload.email }));
      toast.success("OTP sent successfully!");
      setStep(2);
      setCountdown(120);
    } catch (error) {
      console.error("OTP Sending Error:", error);
      toast.error(error?.payload || "Try again later.");
    } finally {
      dispatch(hideLoading());
    }
  };

  const handleVerifyOtp = async () => {
    if (otp.some((digit) => digit === "")) {
      setErrors({ otp: "OTP is required." });
      return;
    }
    setIsVerifyingOtp(true);
    try {
      await verifyOtp(formData.username, otp.join(""));
      toast.success("OTP verified successfully!");
      setStep(3);
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
      await resend(formData.email, "PASSWORD_RESET");
      toast.success("OTP sent successfully to your registered email.");
      setCountdown(120);
    } catch (err) {
      toast.error("Try again later.");
    } finally {
      setIsResendingOtp(false);
    }
  };

  useEffect(() => {
    if (step === 2 && countdown > 0) {
      const timer = setInterval(() => {
        setCountdown((prev) => prev - 1);
      }, 1000);
      return () => clearInterval(timer);
    }
  }, [countdown, step]);

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
      setOtp((prevOtp) => {
        const updatedOtp = [...prevOtp];
        if (index > 0 && updatedOtp[index] === "") {
          document.getElementById(`otp-${index - 1}`).focus();
        }
        updatedOtp[index] = "";
        return updatedOtp;
      });
    }
  };

  const handleResetPassword = async () => {
    if (!validateForm()) return;
    dispatch(showLoading());
    try {
      await resetPassword(formData.username, formData.newPassword);
      toast.success("Password updated successfully. Log in again.");

      resetState();
      setFormData({
        username: "",
        email: "",
        newPassword: "",
        confirmPassword: "",
      });
      setOtp(["", "", "", "", "", ""]);
      setStep(1);
      logout();
    } catch (error) {
      toast.error(error?.message || "Failed to reset password. Try again.");
    } finally {
      dispatch(hideLoading());
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-200 via-purple-300 to-pink-200 flex flex-col items-center justify-center p-6">
      <div className="max-w-md w-full bg-white p-6 rounded-lg shadow-md">
        <h2 className="text-2xl font-semibold text-center mb-6">
          {step === 1 && "Forgot Password"}
          {step === 2 && "Verify OTP"}
          {step === 3 && "Reset Password"}
        </h2>

        {step === 1 && (
          <div>
            <label className="block text-gray-700 font-medium mb-2">
              Username <span className="text-red-500">*</span>
            </label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={formData.username}
              onChange={handleInputChange}
              className="w-full px-4 py-2 border rounded-md"
            />
            {errors.username && (
              <p className="text-red-500 text-sm">{errors.username}</p>
            )}

            {/* Extracted Send OTP Button */}
            <div className="mt-4">
              <button
                type="button"
                onClick={handleSendOtp}
                className="w-full py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
              >
                Send OTP
              </button>
            </div>
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
                isVerifyingOtp ? "bg-gray-400" : "bg-blue-500 hover:bg-blue-600"
              }`}
            >
              {isVerifyingOtp ? "Verifying OTP..." : "Verify OTP"}
            </button>
          </div>
        )}

        {step === 3 && (
          <div>
            {/* New Password Field */}
            <label className="block text-gray-700 font-medium mb-2">
              New Password <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                id="newPassword"
                value={formData.newPassword}
                onChange={handleInputChange}
                className="w-full px-4 py-2 border rounded-md"
              />
              <span
                className="absolute top-2.5 right-3 text-gray-600 cursor-pointer"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {errors.newPassword && (
              <p className="text-red-500 text-sm">{errors.newPassword}</p>
            )}

            {/* Confirm Password Field */}
            <label className="block text-gray-700 font-medium mt-4 mb-2">
              Confirm Password <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type={showConfirmPassword ? "text" : "password"}
                id="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                className="w-full px-4 py-2 border rounded-md"
              />
              <span
                className="absolute top-2.5 right-3 text-gray-600 cursor-pointer"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {errors.confirmPassword && (
              <p className="text-red-500 text-sm">{errors.confirmPassword}</p>
            )}

            {/* Reset Password Button */}
            <button
              onClick={handleResetPassword}
              className="mt-4 w-full py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
            >
              Reset Password
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
