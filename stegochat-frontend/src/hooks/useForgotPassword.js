import { useDispatch, useSelector } from "react-redux";
import { 
    sendForgotPasswordOtp, 
    verifyForgotPasswordOtp, 
    resetPassword, 
    resetForgotPasswordState 
} from "../redux/slices/forgotPasswordSlice"

export const useForgotPassword = () => {
    const dispatch = useDispatch();
    const { otpSent, otpVerified, passwordReset, loading, error } = useSelector(state => state.forgotPassword);

    return {
        otpSent,
        otpVerified,
        passwordReset,
        loading,
        error,
        sendOtp: (username) => dispatch(sendForgotPasswordOtp(username)),
        verifyOtp: (username, otp) => dispatch(verifyForgotPasswordOtp({ username, otp })),
        resetPassword: (username, newPassword) => dispatch(resetPassword({ username, newPassword })),
        resetState: () => dispatch(resetForgotPasswordState()),
    };
};
