import api from "./api";

const UserService = {
    updateEmail: async (newEmail) => {
        const response = await api.put("/users/update-email", { newEmail });
        return response.data;
    },

    updatePhone: async (newPhoneNumber) => {
        const response = await api.put("/users/update-phone", { newPhoneNumber });
        return response.data;
    },

    changePassword: async (currentPassword, newPassword) => {
        const response = await api.put("/users/change-password", { currentPassword, newPassword });
        return response.data;
    },

    searchUsers: async (query) => {
        const response = await api.get(`/users/search?query=${query}`);
        return response.data;
    },

};

export default UserService;
