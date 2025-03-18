import React, { useState, useEffect } from "react";
import { User, ChevronDown, ChevronUp, XCircle } from "lucide-react";
import api from "../services/api";
import toast from "react-hot-toast";

const FriendList = () => {
  const [friends, setFriends] = useState([]);
  const [onlineFriends, setOnlineFriends] = useState([]);
  const [lastSeenMap, setLastSeenMap] = useState({});
  const [dropdownOpen, setDropdownOpen] = useState(null);

  // Fetch Friends List
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

  // Fetch Online Friends
  useEffect(() => {
    const fetchOnlineFriends = async () => {
      try {
        const response = await api.get("/friends/online");
        setOnlineFriends(response.data.map((friend) => friend.username));
      } catch (error) {
        console.error("Error fetching online friends:", error);
      }
    };

    fetchOnlineFriends();
  }, []);

  // Fetch Last Seen Time for Each Friend
  useEffect(() => {
    const fetchLastSeen = async () => {
      const lastSeenData = {};
      for (const friend of friends) {
        try {
          const response = await api.get(`/friends/lastseen/${friend.username}`);
          lastSeenData[friend.username] = response.data;
        } catch (error) {
          console.error(`Error fetching last seen for ${friend.username}:`, error);
        }
      }
      setLastSeenMap(lastSeenData);
    };

    if (friends.length > 0) {
      fetchLastSeen();
    }
  }, [friends]);

  // Remove Friend Function
  const handleRemoveFriend = async (username) => {
    try {
      await api.delete("/friends/remove-friend", {
        data: { friendUsername: username },
      });
      setFriends(friends.filter((friend) => friend.username !== username));
      setDropdownOpen(null);
      toast.success("User unfriended successfully!");
    } catch (error) {
      toast.error("Failed to unfriend user. Try again later.");
    }
  };

  return (
    <div className="bg-gray-100 border-r h-[calc(100vh-56px)] flex flex-col w-full">
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
            <div key={friend.username} className="relative mb-2">
              {/* Friend Tile */}
              <div
                onClick={() => setDropdownOpen(dropdownOpen === friend.username ? null : friend.username)}
                className={`flex items-center p-3 rounded-lg cursor-pointer hover:bg-gray-200 transition ${
                  dropdownOpen === friend.username ? "bg-gray-300" : ""
                }`}
              >
                <User className="w-8 h-8 mr-3 text-gray-600" />
                <div className="flex-1">
                  <h3 className="font-medium">{friend.fullName}</h3>
                  <p className="text-sm text-gray-500">
                    {onlineFriends.includes(friend.username) ? (
                      <span className="text-green-500">Online</span>
                    ) : (
                      `Last seen: ${new Date(lastSeenMap[friend.username]).toLocaleString()}`
                    )}
                  </p>
                </div>
                {dropdownOpen === friend.username ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
              </div>

              {/* Dropdown Menu (Remove Friend) */}
              {dropdownOpen === friend.username && (
                <div className="absolute top-full left-0 w-full bg-white shadow-md rounded-lg mt-1 p-2 z-10">
                  <button
                    onClick={() => handleRemoveFriend(friend.username)}
                    className="flex items-center text-red-600 w-full px-3 py-2 hover:bg-red-100 rounded-md transition-all duration-200"
                  >
                    <XCircle size={18} className="mr-2" />
                    Remove Friend
                  </button>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FriendList;
