import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { checkAuthStatus, loginUser, logoutUser } from "../../services/authService";

export const checkAuth = createAsyncThunk("auth/checkAuth", async (_, { rejectWithValue }) => {
  try {
    const isAuthenticated = await checkAuthStatus();
    return isAuthenticated;
  } catch (error) {
    return rejectWithValue(false);
  }
});

export const userLogin = createAsyncThunk("auth/login", async (credentials, { rejectWithValue }) => {
  try {
    const response = await loginUser(credentials);
    return response;
  } catch (error) {
    return rejectWithValue(error.response?.data?.message || "Login failed.");
  }
});

export const userLogout = createAsyncThunk("auth/logout", async () => {
  await logoutUser();
});

const authSlice = createSlice({
  name: "auth",
  initialState: { isAuthenticated: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(checkAuth.fulfilled, (state, action) => {
        state.isAuthenticated = action.payload;
        state.error = null;
      })
      .addCase(checkAuth.rejected, (state) => {
        state.isAuthenticated = false;
      })
      .addCase(userLogin.fulfilled, (state) => {
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(userLogin.rejected, (state, action) => {
        state.error = action.payload;
      })
      .addCase(userLogout.fulfilled, (state) => {
        state.isAuthenticated = false;
      });
  },
});

export default authSlice.reducer;
