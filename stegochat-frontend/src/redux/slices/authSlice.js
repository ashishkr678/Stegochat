import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { getCurrentUser, loginUser, logoutUser } from "../../services/authService";

export const fetchUser = createAsyncThunk("auth/fetchUser", async (_, { rejectWithValue }) => {
  try {
    return await getCurrentUser();
  } catch (error) {
    return rejectWithValue("Session expired. Please login again.");
  }
});

export const userLogin = createAsyncThunk("auth/login", async (credentials, { rejectWithValue }) => {
  try {
    return await loginUser(credentials);
  } catch (error) {
    return rejectWithValue(error.response?.data?.message || "Login failed.");
  }
});

export const userLogout = createAsyncThunk("auth/logout", async () => {
  await logoutUser();
});

const authSlice = createSlice({
  name: "auth",
  initialState: { user: null, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchUser.fulfilled, (state, action) => {
        state.user = action.payload;
        state.error = null;
      })
      .addCase(fetchUser.rejected, (state, action) => {
        state.user = null;
        state.error = action.payload;
      })
      .addCase(userLogin.fulfilled, (state, action) => {
        state.user = action.payload;
        state.error = null;
      })
      .addCase(userLogin.rejected, (state, action) => {
        state.error = action.payload;
      })
      .addCase(userLogout.fulfilled, (state) => {
        state.user = null;
      });
  },
});

export default authSlice.reducer;
