import api from "./api";

export const registerUser = async (userData) => {
  return await api.post("/users/register", userData);
};

export const verifyOtp = async (email, otp) => {
  return await api.post("/users/register/verify-otp", { email, otp });
};

export const loginUser = async (credentials) => {
  const response = await api.post("/users/login", credentials);
  return response.data;
};

export const logoutUser = async () => {
  await api.post("/users/logout");
};

export const checkAuthStatus = async () => {
  try {
    const response = await api.get("/users/check-auth");
    return response.data.message === "Authenticated";
  } catch (error) {
    return false;
  }
};
