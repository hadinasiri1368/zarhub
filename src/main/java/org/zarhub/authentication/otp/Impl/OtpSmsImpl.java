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
import org.zarhub.config.request.RequestContext;
import org.zarhub.constant.Consts;
import org.zarhub.constant.TimeFormat;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.GeneralExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OtpSmsImpl extends OtpAbstract {
    public OtpSmsImpl(JpaRepository repository, OtpService otpService) {
        super(repository, otpService);
    }

    @Override
    public void verifyUserOtp(String identityCode, String otpCode) {
        otpService.verifyCode(identityCode, otpCode);
    }

    @Override
    public boolean accept(OtpStrategyType type) {
        return OtpStrategyType.SMS.equals(type);
    }

    @Override
    public void generateAndSendUserOtp(String identityCode) {
        Users user = getUser(identityCode);
        String mobile = user.getPerson().getCellPhone();
        if (Utils.isNull(mobile) || !Utils.isMobileValid(mobile))
            throw new ZarHubException(AuthenticationExceptionType.USER_MOBILE_IS_NOT_VALID);
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
        send(identityCode, message, null, mobile);
        otpService.updateOtpStatus(identityCode, true);
    }

    @Override
    public void send(String identityCode, String message, String from, String to) {
        Users user = getUser(identityCode);
        try {
            String requestTime = TimeUtils.getNowTime(TimeFormat.HOUR_MINUTE);
            String requestDate = DateUtils.getTodayJalali();
            String sql = "INSERT INTO sms" +
                    "        (sms_id, from_number, TO_NUMBER, content, request_date, request_time, send_status,sms_type)" +
                    " VALUES (hibernate_sequence.nextval, :fromNumber, :toNumber, :content,:requestDate,:requestTime,0,:smsType)";
            Map<String, Object> param = new HashMap<>();
            param.put("fromNumber", from);
            param.put("toNumber", to);
            param.put("content", message);
            param.put("requestDate", requestDate);
            param.put("requestTime", requestTime);
            param.put("smsType", Consts.SMS_TYPE_HIGH_PRIORITY_SENDING);
            repository.nativeExecuteUpdate(sql, param, RequestContext.getUserId(), RequestContext.getUuid());
            log.info("Sending SMS OTP message to username: {} mobile: {} date: {} time: {}", identityCode, to, requestDate, requestTime);
        } catch (Exception e) {
            throw new ZarHubException(GeneralExceptionType.UNKNOWN_ERROR);
        }
    }
}
