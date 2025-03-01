import React from "react";
import { Mail, Phone, Lock, Edit3, UserPlus } from "lucide-react";

const UserProfileCard = ({ user, isCurrentUser }) => {
  return (
    <div className="bg-white p-6 rounded-2xl shadow-lg w-96">
      {/* Profile Picture & Name */}
      <div className="flex flex-col items-center">
        <img
          src={user.avatar}
          alt={user.name}
          className="w-28 h-28 rounded-full border border-gray-300"
        />
        <h2 className="text-2xl font-semibold mt-3">{user.name}</h2>
        <p className="text-gray-500 text-sm">@{user.username}</p>
        <p className="text-gray-600 mt-2">{user.about}</p>
      </div>

      {/* Contact Info (Only for Logged-in User) */}
      {isCurrentUser && (
        <div className="mt-4 space-y-3">
          <div className="flex items-center space-x-2 text-gray-600">
            <Mail className="w-5 h-5" />
            <span>{user.email}</span>
          </div>
          <div className="flex items-center space-x-2 text-gray-600">
            <Phone className="w-5 h-5" />
            <span>{user.phone}</span>
          </div>
          <div className="flex items-center space-x-2 text-gray-600">
            <Lock className="w-5 h-5" />
            <span>********</span>
            <button className="text-blue-500 hover:underline">Change</button>
          </div>
        </div>
      )}

      {/* Buttons */}
      <div className="mt-5 flex justify-between">
        {isCurrentUser ? (
          <button className="px-4 py-2 border rounded-lg text-gray-700 flex items-center gap-1 hover:bg-gray-100">
            <Edit3 className="w-4 h-4" />
            Edit Profile
          </button>
        ) : (
          <button className="px-4 py-2 bg-blue-500 text-white rounded-lg flex items-center gap-1 hover:bg-blue-600">
            <UserPlus className="w-4 h-4" />
            Add Friend
          </button>
        )}
      </div>
    </div>
  );
};

export default UserProfileCard;
