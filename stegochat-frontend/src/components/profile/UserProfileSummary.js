import React, { useEffect, useState } from "react";
import { X, Check, UserMinus, UserPlus, XCircle } from "lucide-react";
import api from "../../services/api";
import toast from "react-hot-toast";

const UserProfileSummary = ({ username, onClose }) => {
  const [userData, setUserData] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [friendStatus, setFriendStatus] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const [userResponse, loggedInResponse] = await Promise.all([
          api.get(`/users/${username}`),
          api.get(`/users/profile`),
        ]);

        if (!userResponse.data || !loggedInResponse.data.user) {
          throw new Error("User not found.");
        }

        setUserData(userResponse.data.username);
        setCurrentUser(loggedInResponse.data.user);

        // Determine friendship status
        const { friends, sentRequests, receivedRequests } =
          loggedInResponse.data.user;
        if (friends.includes(username)) {
          setFriendStatus("friends");
        } else if (sentRequests.includes(username)) {
          setFriendStatus("request_sent");
        } else if (receivedRequests.includes(username)) {
          setFriendStatus("request_received");
        } else {
          setFriendStatus("not_friends");
        }
      } catch (err) {
        setError("User not found.");
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [username]);

  if (!username) return null;

  // 🔹 API Calls for Friend Actions
  const handleSendRequest = async () => {
    try {
      await api.post("/friends/send-request", { receiverUsername: username });
      setFriendStatus("request_sent");
      toast.success("Friend request sent.");
    } catch (error) {
      toast.error("Failed to send request.");
    }
  };

  const handleCancelRequest = async () => {
    try {
      await api.post("/friends/cancel-request", { receiverUsername: username });
      setFriendStatus("not_friends");
      toast.success("Friend request canceled.");
    } catch (error) {
      toast.error("Failed to cancel request.");
    }
  };

  const handleAcceptRequest = async () => {
    try {
      await api.post("/friends/accept-request", { senderUsername: username });
      setFriendStatus("friends");
      toast.success("Friend request accepted.");
    } catch (error) {
      toast.error("Failed to accept request.");
    }
  };

  const handleRejectRequest = async () => {
    try {
      await api.post("/friends/reject-request", { senderUsername: username });
      setFriendStatus("not_friends");
      toast.success("Friend request rejected.");
    } catch (error) {
      toast.error("Failed to reject request.");
    }
  };

  const handleUnfriend = async () => {
    try {
      await api.delete("/friends/remove-friend", {
        data: { friendUsername: username },
      });
      setFriendStatus("not_friends");
      toast.success("Friend removed.");
    } catch (error) {
      toast.error("Failed to remove friend.");
    }
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50 backdrop-blur-sm">
      <div className="relative bg-white shadow-xl rounded-xl p-6 w-96">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-600 hover:text-gray-800"
        >
          <X size={20} />
        </button>

        {loading ? (
          <p className="text-center text-gray-700">Loading...</p>
        ) : error ? (
          <p className="text-center text-red-600">{error}</p>
        ) : (
          <div className="flex flex-col items-center">
            <h3 className="text-lg font-semibold text-gray-700 mb-2">
              Profile
            </h3>
            <img
              src={userData.profilePicture || "https://via.placeholder.com/100"}
              alt="Profile"
              className="w-24 h-24 rounded-full object-cover border-2 border-gray-300"
            />
            <h2 className="mt-3 text-lg font-semibold">
              {userData.firstName} {userData.lastName}
            </h2>
            <p className="text-gray-500">@{userData.username}</p>
            <p className="mt-2 text-sm text-gray-600 text-center">
              {userData.about || "No bio available."}
            </p>

            {/* Friend Actions */}
            <div className="mt-4 flex space-x-3">
              {friendStatus === "friends" && (
                <>
                  <button className="px-4 py-2 bg-green-500 text-white rounded-lg flex items-center space-x-1 cursor-default">
                    <Check size={16} />
                    <span>Friends</span>
                  </button>
                  <button
                    onClick={handleUnfriend}
                    className="px-4 py-2 bg-red-500 text-white rounded-lg flex items-center space-x-1 hover:bg-red-600"
                  >
                    <UserMinus size={16} />
                    <span>Remove Friend</span>
                  </button>
                </>
              )}
              {friendStatus === "request_sent" && (
                <button
                  onClick={handleCancelRequest}
                  className="px-4 py-2 bg-gray-500 text-white rounded-lg flex items-center space-x-1 hover:bg-gray-600"
                >
                  <XCircle size={16} />
                  <span>Cancel Request</span>
                </button>
              )}
              {friendStatus === "request_received" && (
                <>
                  <button
                    onClick={handleAcceptRequest}
                    className="px-4 py-2 bg-green-500 text-white rounded-lg flex items-center space-x-1 hover:bg-green-600"
                  >
                    <Check size={16} />
                    <span>Accept</span>
                  </button>
                  <button
                    onClick={handleRejectRequest}
                    className="px-4 py-2 bg-red-500 text-white rounded-lg flex items-center space-x-1 hover:bg-red-600"
                  >
                    <XCircle size={16} />
                    <span>Reject</span>
                  </button>
                </>
              )}
              {friendStatus === "not_friends" && (
                <button
                  onClick={handleSendRequest}
                  className="px-4 py-2 bg-blue-500 text-white rounded-lg flex items-center space-x-1 hover:bg-blue-600"
                >
                  <UserPlus size={16} />
                  <span>Add Friend</span>
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserProfileSummary;
