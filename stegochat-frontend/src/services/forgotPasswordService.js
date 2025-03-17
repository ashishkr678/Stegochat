import api from "./api";

const ForgotPasswordService = {
    sendOtp: async (username) => {
        const response = await api.post("/users/forgot-password/send-otp", { username });
        return response.data;
    },

    verifyOtp: async (username, otp) => {
        const response = await api.post("/users/forgot-password/verify-otp", { username, otp });
        return response.data;
    },

    resetPassword: async (username, newPassword) => {
        const response = await api.post("/users/forgot-password/reset", { username, newPassword });
        return response.data;
    },
};

export default ForgotPasswordService;
