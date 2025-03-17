import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import otpReducer from "./slices/resendOtpSlice";
import loadingReducer from "./slices/loadingSlice";
import forgotPasswordReducer from "./slices/forgotPasswordSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    otp: otpReducer,
    loading: loadingReducer,
    forgotPassword: forgotPasswordReducer,
  },
});

export default store;
