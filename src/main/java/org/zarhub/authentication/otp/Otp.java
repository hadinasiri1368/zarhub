package org.zarhub.authentication.otp;


import org.zarhub.authentication.otp.constant.OtpStrategyType;

public interface Otp {
    void verifyUserOtp(String identityCode,String otpCode);
    boolean accept(OtpStrategyType type);
    void generateAndSendUserOtp(String identityCode);
}
