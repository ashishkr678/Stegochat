import { createSlice } from "@reduxjs/toolkit";

const resendOtpSlice = createSlice({
  name: "otp",
  initialState: { canResend: true },
  reducers: {
    startCooldown: (state) => {
      state.canResend = false;
    },
    endCooldown: (state) => {
      state.canResend = true;
    },
  },
});

export const { startCooldown, endCooldown } = resendOtpSlice.actions;
export default resendOtpSlice.reducer;
