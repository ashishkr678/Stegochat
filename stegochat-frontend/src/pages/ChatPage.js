import React, { useState } from "react";
import ChatList from "../components/chats/ChatList";
import ChatWindow from "../components/chats/ChatWindow";

const ChatPage = () => {
  const [selectedChat, setSelectedChat] = useState(null);

  return (
    <div className="flex h-[calc(100vh-56px)]">
      
      <ChatList selectedChat={selectedChat} setSelectedChat={setSelectedChat} />
      <ChatWindow selectedChat={selectedChat} />
    
    </div>
  );
};

export default ChatPage;
