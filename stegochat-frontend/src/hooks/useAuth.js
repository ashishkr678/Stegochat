import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { fetchUser, userLogin, userLogout } from "../redux/slices/authSlice";

export const useAuth = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const error = useSelector((state) => state.auth.error);

  useEffect(() => {
    dispatch(fetchUser());
  }, [dispatch]);

  useEffect(() => {
    if (error === "Session expired. Please login again.") {
      dispatch(userLogout());
    }
  }, [error, dispatch]);

  const login = (credentials) => dispatch(userLogin(credentials));
  const logout = () => dispatch(userLogout());

  return { user, login, logout, error };
};
