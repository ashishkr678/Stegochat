import api from "./api";

// Register user (Initiate OTP)
export const registerUser = async (userData) => {
  return await api.post("/users/register", userData);
};

// Verify OTP & Complete Registration
export const verifyOtp = async (email, otp) => {
  return await api.post("/users/register/verify-otp", { email, otp });
};

// Login user
export const loginUser = async (credentials) => {
  return await api.post("/users/login", credentials);
};

// Get authenticated user
export const getCurrentUser = async () => {
    return await api.get("/users/profile");
  };

// Logout user
export const logoutUser = async () => {
  return await api.post("/users/logout");
};
