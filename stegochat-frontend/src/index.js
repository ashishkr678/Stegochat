import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom"; // ✅ Import BrowserRouter
import { Provider } from "react-redux";
import store from "./redux/store"; // ✅ Ensure correct path
import "./index.css";
import App from "./App";
import { Toaster } from "react-hot-toast";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <Provider store={store}>
    <BrowserRouter>
      <Toaster />
      <App />
    </BrowserRouter>
  </Provider>
);
