import { createSlice } from "@reduxjs/toolkit";

// Check for existing token in localStorage
const getInitialState = () => {
  const token = localStorage.getItem("authToken");
  const user = localStorage.getItem("authUser");

  if (token && user) {
    return {
      user: JSON.parse(user),
      token,
      isAuthenticated: true,
      error: null,
    };
  }

  return {
    user: null,
    token: null,
    isAuthenticated: false,
    error: null,
  };
};

const authSlice = createSlice({
  name: "auth",
  initialState: getInitialState(),
  reducers: {
    loginSuccess: (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.accessToken;
      state.domain = action.payload.user.domain;
      state.isAuthenticated = true;

      // Persist to localStorage
      localStorage.setItem("authToken", action.payload.accessToken);
      localStorage.setItem("authUser", JSON.stringify(action.payload.user));
    },
    loginFailure: (state, action) => {
      state.error = action.payload;
    },
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      state.error = null;

      // Clear from localStorage
      localStorage.removeItem("authToken");
      localStorage.removeItem("authUser");
    },
  },
});

export const { loginSuccess, loginFailure, logout } = authSlice.actions;

export const selectUser = (state) => state.auth.user;
export const selectToken = (state) => state.auth.token;
export const selectDomain = (state) => state.auth.domain;
export const selectIsAuthenticated = (state) => state.auth.isAuthenticated;
export const selectAuthError = (state) => state.auth.error;

export default authSlice;
