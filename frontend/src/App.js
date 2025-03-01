import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import Navbar from "./components/navbar/Navbar";
import ChatPage from "./pages/ChatPage";
import ProfilePage from "./pages/ProfilePage";

function App() {
  return (
    <Router>
      <Navbar />
      <div className="mt-14 ml-14">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/" element={<ChatPage />} />
          <Route path="/profile/:userId" element={<ProfilePage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
