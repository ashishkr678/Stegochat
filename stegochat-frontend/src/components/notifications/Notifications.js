import React, { useState, useEffect, useRef } from "react";
import { Bell, BellOff, Trash2, CheckCircle } from "lucide-react";
import { toast } from "react-hot-toast";
import api from "../../services/api";

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await api.get("/notifications");
      const filteredNotifications = response.data.filter(
        (notification) => notification.type !== "FRIEND_REQUEST"
      );
      setNotifications(filteredNotifications);
    } catch (error) {
      console.error("Error fetching notifications:", error);
    }
  };

  const markAsRead = async (id) => {
    try {
      await api.post(`/notifications/mark-read/${id}`);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch (error) {
      console.error("Error marking notification as read:", error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await api.post("/notifications/mark-all-read");
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
      toast.success("All notifications marked as read.");
    } catch (error) {
      toast.error("Failed to mark all as read.");
    }
  };

  const deleteNotification = async (id) => {
    try {
      await api.delete(`/notifications/${id}`);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
      toast.success("Notification deleted.");
    } catch (error) {
      toast.error("Failed to delete notification.");
    }
  };

  const clearAllNotifications = async () => {
    try {
      await api.delete("/notifications/clear");
      setNotifications([]);
      toast.success("All notifications cleared.");
    } catch (error) {
      toast.error("Failed to clear notifications.");
    }
  };

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleClickOutside = (event) => {
    if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const unreadCount = notifications.filter((n) => !n.read).length;
  const displayCount = unreadCount > 99 ? "99+" : unreadCount;

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={toggleDropdown}
        className="relative flex items-center justify-center transition-all duration-300 text-gray-700 hover:text-gray-900"
      >
        {isOpen ? (
          <BellOff size={24} fill="currentColor" className="relative z-10" />
        ) : (
          <Bell size={24} className="relative z-10" />
        )}
        {unreadCount > 0 && (
          <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded-full z-0">
            {displayCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-lg rounded-md text-gray-800 z-20 max-h-96 overflow-y-auto">
          <div className="p-3 border-b font-semibold flex justify-between items-center">
            <span>Notifications</span>
            <div className="flex space-x-2">
              {notifications.length > 0 && (
                <>
                  <button
                    onClick={markAllAsRead}
                    className="text-blue-500 hover:text-blue-600 text-sm"
                  >
                    Mark All as Read
                  </button>
                  <button
                    onClick={clearAllNotifications}
                    className="text-red-500 hover:text-red-600 text-sm"
                  >
                    Clear All
                  </button>
                </>
              )}
            </div>
          </div>

          {notifications.length > 0 ? (
            notifications.map((notification) => (
              <div
                key={notification.id}
                className={`flex items-center justify-between px-4 py-3 hover:bg-gray-100 ${
                  notification.read ? "text-gray-500" : "text-gray-800 font-semibold"
                }`}
              >
                <div className="flex flex-col">
                  <span>{notification.type.replace(/_/g, " ")}</span>
                  <span className="text-sm text-gray-600">{notification.message}</span>
                </div>
                <div className="flex space-x-2">
                  {!notification.read && (
                    <button
                      onClick={() => markAsRead(notification.id)}
                      className="text-green-500 hover:text-green-600"
                    >
                      <CheckCircle size={20} />
                    </button>
                  )}
                  <button
                    onClick={() => deleteNotification(notification.id)}
                    className="text-red-500 hover:text-red-600"
                  >
                    <Trash2 size={20} />
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="px-4 py-3 text-gray-500">No notifications</div>
          )}
        </div>
      )}
    </div>
  );
};

export default Notifications;
