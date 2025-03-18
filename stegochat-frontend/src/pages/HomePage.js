import React, { useState } from "react";
import ChatWindow from "../components/chats/ChatWindow";
import Topbar from "../components/navbar/modals/Topbar";
import Sidebar from "../components/navbar/modals/Sidebar";

const HomePage = () => {
  const [selectedChat, setSelectedChat] = useState(null);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <div className="flex flex-col h-screen">
      {/* Topbar */}
      <Topbar toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />

      {/* Main Content */}
      <div className="flex ">
        {/* Sidebar + Recent Chats */}
        <Sidebar
          isOpen={isSidebarOpen}
          selectedChat={selectedChat}
          setSelectedChat={setSelectedChat}
        />

        {/* Chat Window (Takes 2/3 or Full Width if RecentChat is closed) */}
        <div className= "w-full transition-all duration-300 mt-14">
          <ChatWindow selectedChat={selectedChat} />
        </div>
      </div>
    </div>
  );
};

export default HomePage;
