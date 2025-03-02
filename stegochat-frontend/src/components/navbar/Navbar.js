import React, { useState } from "react";
import Topbar from "./modals/Topbar";
import Sidebar from "./modals/Sidebar";

const Navbar = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <>
      {/* Topbar */}
      <Topbar toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />

      {/* Sidebar */}
      <Sidebar isOpen={isSidebarOpen} />
    </>
  );
};

export default Navbar;
