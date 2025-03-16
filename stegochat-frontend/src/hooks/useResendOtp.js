import { useDispatch, useSelector } from "react-redux";
import { startCooldown, endCooldown } from "../redux/slices/resendOtpSlice";
import { resendOtp } from "../services/resendOtpService";

export const useResendOtp = () => {
  const dispatch = useDispatch();
  const canResend = useSelector((state) => state.otp.canResend);

  const resend = async (email, type) => {
    if (!canResend) return;

    await resendOtp(email, type);
    dispatch(startCooldown());

    setTimeout(() => {
      dispatch(endCooldown());
    }, 120000);
  };

  return { canResend, resend };
};
