package org.zarhub.authentication.otp.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zarhub.authentication.otp.OtpAbstract;
import org.zarhub.authentication.otp.OtpService;
import org.zarhub.authentication.otp.constant.OtpConsumerType;
import org.zarhub.authentication.otp.constant.OtpStrategyType;
import org.zarhub.authentication.otp.dto.OtpResponseDto;
import org.zarhub.authentication.otp.generator.OtpGenerator;
import org.zarhub.common.DateUtils;
import org.zarhub.common.TimeUtils;
import org.zarhub.common.Utils;
import org.zarhub.constant.Consts;
import org.zarhub.constant.TimeFormat;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.GeneralExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;
import org.zarhub.config.request.RequestContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OtpEmailImpl extends OtpAbstract {
    public OtpEmailImpl(JpaRepository repository, OtpService otpService) {
        super(repository, otpService);
    }

    @Override
    public void verifyUserOtp(String identityCode, String otpCode) {
        otpService.verifyCode(identityCode, otpCode);
    }

    @Override
    public boolean accept(OtpStrategyType type) {
        return OtpStrategyType.EMAIL.equals(type);
    }

    @Override
    public void generateAndSendUserOtp(String identityCode) {
        Users user = getUser(identityCode);
        String email = user.getPerson().getEmail();
        if (Utils.isNull(email) || !Utils.isEmailValid(email))
            throw new ZarHubException(AuthenticationExceptionType.EMAIL_IS_NOT_VALID);
        OtpResponseDto otpDto = otpService.checkAndGenerateOtpCode(identityCode, OtpConsumerType.USER);
        if (OtpGenerator.checkOtpTime(otpDto.getExpirationTime())) {
            if (otpDto.getMessageSend()) {
                throw new ZarHubException(AuthenticationExceptionType.OTP_SEND_NOTALLOWED);
            }
        } else {
            otpService.removeOtp(identityCode);
            otpDto = otpService.checkAndGenerateOtpCode(identityCode, OtpConsumerType.USER);
        }
        String message = Utils.getMessage("authorization.otp_message", new Object[]{otpDto.getVerificationCode()});
        send(identityCode, message, Consts.EMAIL_FROM_ADDRESS_NO_REPLY, email);
        otpService.updateOtpStatus(identityCode, true);
    }

    @Override
    public void send(String identityCode, String message, String from, String to) {
        Users user = getUser(identityCode);
        try {
            String requestTime = TimeUtils.getNowTime(TimeFormat.HOUR_MINUTE);
            String requestDate = DateUtils.getTodayJalali();
            String sql = "INSERT INTO EMAIL \n" +
                    "                        (EMAIL_ID,\n" +
                    "                        FROM_ADDRESS,\n" +
                    "                        TO_ADDRESS,\n" +
                    "                        SUBJECT,\n" +
                    "                        CONTENT,\n" +
                    "                        FILENAME,\n" +
                    "                        FILENAME_FRIENDLY,\n" +
                    "                        REQUEST_DATE,\n" +
                    "                        REQUEST_TIME,\n" +
                    "                        SEND_STATUS,\n" +
                    "                        SEND_DATE,\n" +
                    "                        SEND_TIME,\n" +
                    "                        ERROR_MESSAGE,\n" +
                    "                        IS_MANUAL)\n" +
                    "     VALUES (" +
                    "             hibernate_sequence.NEXTVAL,\n" +
                    "             :fromAddress,\n" +
                    "             :toAddress,\n" +
                    "             :subject,\n" +
                    "             :content,\n" +
                    "             null,\n" +
                    "             null,\n" +
                    "             :requestDate,\n" +
                    "             :requestTime,\n" +
                    "             0,\n" +
                    "             null,\n" +
                    "             null,\n" +
                    "             null,\n" +
                    "             1)";

            Map<String, Object> param = new HashMap<>();
            param.put("fromAddress", from);
            param.put("toAddress", to);
            param.put("subject", Consts.ALERT_EMAIL_SUBJECT_VERIFICATION_CODE);
            param.put("content", message);
            param.put("requestDate", requestDate);
            param.put("requestTime", requestTime);
            repository.nativeExecuteUpdate(sql, param, RequestContext.getUserId(), RequestContext.getUuid());
            log.info("Sending EMAIL OTP message to username: {} email: {} date: {} time: {}", identityCode, to, requestDate, requestTime);
        } catch (Exception e) {
            throw new ZarHubException(GeneralExceptionType.UNKNOWN_ERROR);
        }
    }
}
