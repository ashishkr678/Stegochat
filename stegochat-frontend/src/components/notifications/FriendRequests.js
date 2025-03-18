import React, { useState, useEffect, useRef } from "react";
import { UserPlus, CheckCircle, XCircle } from "lucide-react";
import { toast } from "react-hot-toast";
import api from "../../services/api";

const FriendRequests = () => {
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await api.get("/notifications");
      const friendRequests = response.data.filter(
        (notification) => notification.type === "FRIEND_REQUEST"
      );
      setNotifications(friendRequests);
    } catch (error) {
      console.error("Error fetching notifications:", error);
    }
  };

  const deleteNotification = async (id) => {
    try {
      await api.delete(`/notifications/${id}`);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    } catch (error) {
      console.error("Error deleting notification:", error);
    }
  };

  const handleAcceptRequest = async (username, id) => {
    try {
      await api.post("/friends/accept-request", { senderUsername: username });
      deleteNotification(id);
      toast.success("Friend request accepted.");
    } catch (error) {
      toast.error("Failed to accept request.");
    }
  };

  const handleRejectRequest = async (username, id) => {
    try {
      await api.post("/friends/reject-request", { senderUsername: username });
      deleteNotification(id);
      toast.success("Friend request rejected.");
    } catch (error) {
      toast.error("Failed to reject request.");
    }
  };

  const toggleDropdown = () => setIsOpen(!isOpen);

  const handleClickOutside = (event) => {
    if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const unreadCount = notifications.length;
  const displayCount = unreadCount > 99 ? "99+" : unreadCount;

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={toggleDropdown}
        className="relative flex items-center justify-center transition-all duration-300 text-gray-700 hover:text-gray-900"
      >
        {isOpen ? (
          <UserPlus size={24} fill="currentColor" className="relative z-10" />
        ) : (
          <UserPlus size={24} className="relative z-10" />
        )}
        {unreadCount > 0 && (
          <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded-full z-0">
            {displayCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-lg rounded-md text-gray-800 z-20 max-h-96 overflow-y-auto">
          <div className="p-3 border-b font-semibold text-gray-900">
            Friend Requests
          </div>
          {notifications.length > 0 ? (
            notifications.map((notification) => (
              <div
                key={notification.id}
                className="flex items-center justify-between px-4 py-3 hover:bg-gray-100"
              >
                <div className="flex flex-col">
                  <span className="font-semibold">
                    {notification.referenceId}
                  </span>
                  <span className="text-sm text-gray-600">
                    Sent you a friend request
                  </span>
                </div>
                <div className="flex space-x-3">
                  <button
                    onClick={() =>
                      handleAcceptRequest(
                        notification.referenceId,
                        notification.id
                      )
                    }
                    className="text-green-500 hover:text-green-600"
                  >
                    <CheckCircle size={24} />
                  </button>
                  <button
                    onClick={() =>
                      handleRejectRequest(
                        notification.referenceId,
                        notification.id
                      )
                    }
                    className="text-red-500 hover:text-red-600"
                  >
                    <XCircle size={24} />
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="px-4 py-3 text-gray-500">No friend requests</div>
          )}
        </div>
      )}
    </div>
  );
};

export default FriendRequests;
