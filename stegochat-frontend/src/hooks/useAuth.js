import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { checkAuth, userLogin, userLogout } from "../redux/slices/authSlice";

export const useAuth = () => {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
  const error = useSelector((state) => state.auth.error);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifyAuth = async () => {
      try {
        await dispatch(checkAuth()).unwrap();
      } catch (err) {
        console.error("Session expired or user not authenticated.");
      } finally {
        setLoading(false);
      }
    };

    verifyAuth();
  }, [dispatch]);

  useEffect(() => {
    if (error && error === "Session expired. Please login again.") {
      dispatch(userLogout());
    }
  }, [error, dispatch]);

  const login = async (credentials) => {
    setLoading(true);
    try {
      await dispatch(userLogin(credentials)).unwrap();
    } catch (err) {
      console.error("Login failed:", err);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setLoading(true);
    dispatch(userLogout());
    setTimeout(() => setLoading(false), 500);
  };

  return { isAuthenticated, login, logout, loading };
};
