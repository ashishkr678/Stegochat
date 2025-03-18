import React from "react";
import { MessageCircle, Smile, Paperclip, Send } from "lucide-react";

const ChatWindow = ({ selectedChat }) => {
  return (
    <div className="flex flex-col flex-grow h-[calc(100vh-56px)] w-full bg-gray-50">
      {!selectedChat ? (
        <div className="flex flex-col items-center justify-center h-full text-gray-400 text-lg">
          <MessageCircle className="w-16 h-16 mb-4" />
          <p>StegoChat - Select a chat to start messaging</p>
        </div>
      ) : (
        <>
          {/* Chat Header */}
          <div className="bg-gray-200 p-3 text-lg font-semibold border-b">
            {selectedChat.name}
          </div>

          {/* Chat Messages (Scrollable) */}
          <div className="flex-grow p-4 overflow-y-auto">
            <p className="text-gray-600">{selectedChat.lastMessage}</p>
          </div>

          {/* Send Message Box */}
          <div className="flex items-center p-3 border-t bg-white">
            <Smile className="text-gray-500 cursor-pointer" />
            <input
              type="text"
              className="flex-grow mx-3 p-2 border rounded-lg outline-none"
              placeholder="Type a message..."
            />
            <Paperclip className="text-gray-500 cursor-pointer" />
            <Send className="text-blue-500 cursor-pointer ml-3" />
          </div>
        </>
      )}
    </div>
  );
};

export default ChatWindow;
