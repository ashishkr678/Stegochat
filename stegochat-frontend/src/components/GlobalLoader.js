import { CircularProgress } from "@mui/material";
import { useSelector } from "react-redux";

const GlobalLoader = () => {
  const isLoading = useSelector((state) => state.loading.isLoading);

  if (!isLoading) return null;

  return (
    <div className="fixed top-0 left-0 w-full h-full flex items-center justify-center bg-blue-200 bg-opacity-50 backdrop-blur-sm z-50">
      <svg width={0} height={0}>
        <defs>
          <linearGradient id="my_gradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stopColor="#e01cd5" />
            <stop offset="100%" stopColor="#1CB5E0" />
          </linearGradient>
        </defs>
      </svg>
      <CircularProgress
        sx={{ "svg circle": { stroke: "url(#my_gradient)" } }}
        size={60}
        thickness={4}
      />
    </div>
  );
};

export default GlobalLoader;
