import React, { useState, useEffect } from "react";
import { User, XCircle } from "lucide-react";
import api from "../services/api";
import toast from "react-hot-toast";
import moment from "moment";

const FriendList = () => {
  const [friends, setFriends] = useState([]);
  const [confirmRemove, setConfirmRemove] = useState(null); // Store the friend to be removed

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const response = await api.get("/friends/list");
        setFriends(response.data);
      } catch (error) {
        console.error("Error fetching friends list:", error);
      }
    };

    fetchFriends();
  }, []);

  const handleRemoveFriend = async () => {
    if (!confirmRemove) return;

    try {
      await api.delete("/friends/remove-friend", {
        data: { friendUsername: confirmRemove.username },
      });

      setFriends(friends.filter((friend) => friend.username !== confirmRemove.username));
      setConfirmRemove(null); // Close dialog
      toast.success("User unfriended successfully!");
    } catch (error) {
      toast.error("Failed to unfriend user. Try again later.");
    }
  };

  const formatLastSeen = (lastSeen) => {
    if (!lastSeen) return "Last seen recently";
    const momentLastSeen = moment(lastSeen);
    if (momentLastSeen.isSame(moment(), "day")) {
      return `last seen today at ${momentLastSeen.format("hh:mm A")}`;
    } else if (momentLastSeen.isSame(moment().subtract(1, "day"), "day")) {
      return `last seen yesterday at ${momentLastSeen.format("hh:mm A")}`;
    } else {
      return `last seen on ${momentLastSeen.format("MMM D, YYYY [at] hh:mm A")}`;
    }
  };

  return (
    <div className="bg-gray-100 border-r h-[calc(100vh-56px)] flex flex-col w-full relative">
      {/* Header */}
      <div className="p-3 border-b bg-gray-100 sticky top-0 z-10">
        <h2 className="text-xl font-semibold">Friends</h2>
      </div>

      {/* Scrollable Friend List */}
      <div className="flex-1 overflow-y-auto p-4">
        {friends.length === 0 ? (
          <p className="text-center text-gray-500 mt-10">No friends available</p>
        ) : (
          friends.map((friend) => (
            <div
              key={friend.username}
              className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-200 transition"
            >
              {/* User Avatar & Info */}
              <div className="flex items-center">
                {friend.profilePicture ? (
                  <img
                    src={friend.profilePicture}
                    alt={friend.username}
                    className="w-10 h-10 rounded-full object-cover mr-3"
                  />
                ) : (
                  <User className="w-10 h-10 mr-3 text-gray-600 bg-gray-300 rounded-full p-1" />
                )}
                <div>
                  <h3 className="font-medium">{friend.firstName} {friend.lastName}</h3>
                  <p className="text-sm text-gray-500">
                    {friend.online ? (
                      <span className="text-green-500">Online</span>
                    ) : (
                      formatLastSeen(friend.lastSeen)
                    )}
                  </p>
                </div>
              </div>

              {/* Remove Friend Button */}
              <button
                onClick={() => setConfirmRemove(friend)}
                className="text-red-600 hover:text-red-800 transition-all"
              >
                <XCircle size={20} />
              </button>
            </div>
          ))
        )}
      </div>

      {/* Confirmation Dialog */}
      {confirmRemove && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg text-center">
            <h3 className="text-lg font-semibold">Remove Friend?</h3>
            <p className="text-sm text-gray-600 mt-2">
              Are you sure you want to unfriend <strong>{confirmRemove.firstName} {confirmRemove.lastName}</strong>?
            </p>

            <div className="mt-4 flex justify-center space-x-4">
              <button
                onClick={handleRemoveFriend}
                className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 transition-all"
              >
                Yes
              </button>
              <button
                onClick={() => setConfirmRemove(null)}
                className="bg-gray-300 px-4 py-2 rounded-md hover:bg-gray-400 transition-all"
              >
                No
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FriendList;
