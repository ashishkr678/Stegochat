import React from "react";
import AppRouter from "./router/AppRouter"; // ✅ Import AppRouter
import { useAuth } from "./hooks/useAuth";
import Navbar from "./components/navbar/Navbar";
import GlobalLoader from "./components/GlobalLoader";

const App = () => {
  const { user } = useAuth();

  return (
    <div>
      {user && <Navbar />}
      <GlobalLoader />
      <AppRouter />
    </div>
  );
};

export default App;
