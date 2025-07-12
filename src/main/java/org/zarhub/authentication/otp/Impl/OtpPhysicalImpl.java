package org.zarhub.authentication.otp.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zarhub.authentication.otp.OtpAbstract;
import org.zarhub.authentication.otp.OtpService;
import org.zarhub.authentication.otp.constant.OtpStrategyType;
import org.zarhub.common.Utils;
import org.zarhub.constant.Consts;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;

@Service
@Slf4j
public class OtpPhysicalImpl extends OtpAbstract {
    public OtpPhysicalImpl(JpaRepository repository, OtpService otpService) {
        super(repository, otpService);
    }

    @Override
    public void verifyUserOtp(String identityCode, String otpCode) {
    }

    @Override
    public boolean accept(OtpStrategyType type) {
        return OtpStrategyType.PHYSICAL.equals(type);
    }

    @Override
    public void generateAndSendUserOtp(String identityCode) {

    }

    @Override
    public void send(String identityCode, String message, String from, String to) {

    }
}
