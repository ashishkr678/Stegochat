import api from "./api";

export const resendOtp = async (email, type) => {
  return await api.post("/users/resend-otp", { email, type });
};
