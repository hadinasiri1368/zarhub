package org.zarhub.authentication.otp;

import org.springframework.stereotype.Service;
import org.zarhub.authentication.otp.constant.OtpStrategyType;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;

@Service
public abstract class OtpAbstract implements Otp {
    protected final JpaRepository repository;
    protected final OtpService otpService;
    public OtpAbstract(final JpaRepository repository, OtpService otpService) {
        this.repository = repository;
        this.otpService = otpService;
    }

    public abstract void verifyUserOtp(String identityCode, String otpCode);
    public abstract boolean accept(OtpStrategyType type);
    public abstract void generateAndSendUserOtp(String identityCode);
    public abstract void send(String identityCode, String message, String from, String to);
    protected Users getUser(String identityCode) {
        return repository.findAll(Users.class).stream()
                .filter(a -> a.getUsername().equals(identityCode))
                .findFirst()
                .orElseThrow(() -> new ZarHubException(AuthenticationExceptionType.USER_NOT_FOUND));
    }
}
