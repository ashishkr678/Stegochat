import React from "react";
import { useParams } from "react-router-dom";
import UserProfileCard from "../components/profile/UserProfileCard";

// Dummy logged-in user (to simulate authentication)
const loggedInUser = {
  id: "1",
  name: "Jane Doe",
  username: "janedoe",
  avatar: "https://via.placeholder.com/150",
  about: "Living the best chat life!",
  email: "janedoe@example.com",
  phone: "+9876543210",
  password: "********",
};

// Dummy other users
const dummyUsers = [
  {
    id: "2",
    name: "John Smith",
    username: "johnsmith",
    avatar: "https://via.placeholder.com/150/FF5733/ffffff?text=JS",
    about: "Exploring the digital world!",
  },
  {
    id: "3",
    name: "Alice Johnson",
    username: "alicej",
    avatar: "https://via.placeholder.com/150/33FF57/ffffff?text=AJ",
    about: "Tech enthusiast and coffee lover ☕",
  },
  {
    id: "4",
    name: "Bob Williams",
    username: "bobw",
    avatar: "https://via.placeholder.com/150/3357FF/ffffff?text=BW",
    about: "Always online, always chatting.",
  },
];

const ProfilePage = () => {
  const { userId } = useParams();

  // Check if the profile is the logged-in user
  const isCurrentUser = userId === loggedInUser.id || !userId;

  // Get user data: Logged-in user or a searched user
  const user = isCurrentUser
    ? loggedInUser
    : dummyUsers.find((u) => u.id === userId) || dummyUsers[0];

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
      <UserProfileCard user={user} isCurrentUser={isCurrentUser} />
    </div>
  );
};

export default ProfilePage;
