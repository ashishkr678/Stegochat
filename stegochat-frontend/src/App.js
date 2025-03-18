import React from "react";
import AppRouter from "./router/AppRouter";
import { useAuth } from "./hooks/useAuth";
import GlobalLoader from "./components/GlobalLoader";

const App = () => {
  const { loading } = useAuth();

  if (loading) return <GlobalLoader />;

  return (
    <div>
      <AppRouter />
    </div>
  );
};

export default App;
