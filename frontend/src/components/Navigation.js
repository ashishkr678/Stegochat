import React, { useState } from "react";
import Topbar from "./Navigation/Topbar";
import Sidebar from "./Navigation/Sidebar";

const Navigation = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <>
      {/* Topbar (Fixed and Overlay) */}
      <Topbar toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />

      {/* Sidebar (Fixed and Overlay) */}
      <Sidebar isOpen={isSidebarOpen} />
    </>
  );
};

export default Navigation;
