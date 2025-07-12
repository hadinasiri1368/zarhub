package org.zarhub.authentication.otp.generator;

import org.zarhub.authentication.otp.dto.OtpResponseDto;

import java.util.Calendar;
import java.util.Random;

public class OtpGenerator {

    public static OtpResponseDto generate(int expirationTime, int numberOfAttempts){
        Integer verificationCode = generateVerificationCode();
        return new OtpResponseDto(verificationCode.toString(), Calendar.getInstance().getTimeInMillis() / 1000L + expirationTime, numberOfAttempts, false);
    }

    public static boolean checkOtpTime(Long otpTime){
        return otpTime > (Calendar.getInstance().getTimeInMillis() / 1000L);
    }

    private static Integer generateVerificationCode() {
        Random rnd = new Random();
        return 10000 + rnd.nextInt(90000);
    }
}
