import React from "react";
import AppRouter from "./router/AppRouter";
import { useAuth } from "./hooks/useAuth";
import Navbar from "./components/navbar/Navbar";
import GlobalLoader from "./components/GlobalLoader";

const App = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return <GlobalLoader />;

  return (
    <div>
      {isAuthenticated && <Navbar />}
      <AppRouter />
    </div>
  );
};

export default App;
