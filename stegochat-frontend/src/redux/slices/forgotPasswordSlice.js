import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import ForgotPasswordService from "../../services/forgotPasswordService";

// Async Thunks
export const sendForgotPasswordOtp = createAsyncThunk(
  "forgotPassword/sendOtp",
  async (username, { rejectWithValue }) => {
    try {
      const response = await ForgotPasswordService.sendOtp(username);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || "Failed to send OTP");
    }
  }
);

export const verifyForgotPasswordOtp = createAsyncThunk(
  "forgotPassword/verifyOtp",
  async ({ username, otp }, { rejectWithValue }) => {
    try {
      return await ForgotPasswordService.verifyOtp(username, otp);
    } catch (error) {
      return rejectWithValue(error.response?.data || "OTP verification failed");
    }
  }
);

export const resetPassword = createAsyncThunk(
  "forgotPassword/resetPassword",
  async ({ username, newPassword }, { rejectWithValue }) => {
    try {
      return await ForgotPasswordService.resetPassword(username, newPassword);
    } catch (error) {
      return rejectWithValue(error.response?.data || "Password reset failed");
    }
  }
);

const initialState = {
  otpSent: false,
  otpVerified: false,
  passwordReset: false,
  loading: false,
  error: null,
};

const forgotPasswordSlice = createSlice({
  name: "forgotPassword",
  initialState,
  reducers: {
    resetForgotPasswordState: (state) => {
      state.otpSent = false;
      state.otpVerified = false;
      state.passwordReset = false;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(sendForgotPasswordOtp.pending, (state) => {
        state.loading = true;
      })
      .addCase(sendForgotPasswordOtp.fulfilled, (state) => {
        state.loading = false;
        state.otpSent = true;
      })
      .addCase(sendForgotPasswordOtp.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(verifyForgotPasswordOtp.pending, (state) => {
        state.loading = true;
      })
      .addCase(verifyForgotPasswordOtp.fulfilled, (state) => {
        state.loading = false;
        state.otpVerified = true;
      })
      .addCase(verifyForgotPasswordOtp.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(resetPassword.pending, (state) => {
        state.loading = true;
      })
      .addCase(resetPassword.fulfilled, (state) => {
        state.loading = false;
        state.passwordReset = true;
      })
      .addCase(resetPassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { resetForgotPasswordState } = forgotPasswordSlice.actions;
export default forgotPasswordSlice.reducer;
