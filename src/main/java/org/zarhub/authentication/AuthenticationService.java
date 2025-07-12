package org.zarhub.authentication;

import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.zarhub.ProfileService.ProfileService;
import org.zarhub.authentication.otp.OtpVisitor;
import org.zarhub.authentication.otp.constant.OtpStrategyType;
import org.zarhub.authentication.otp.dto.OtpRequestDto;
import org.zarhub.common.JwtUtil;
import org.zarhub.common.Utils;
import org.zarhub.config.authentication.TokenService;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final JpaRepository repository;
    private final TokenService tokenService;
    private final ProfileService profileService;
    private final OtpVisitor otpVisitor;
    private final JwtUtil jwtUtil;

    public AuthenticationService(JpaRepository repository
            , TokenService tokenService
            , ProfileService profileService
            , OtpVisitor otpVisitor, JwtUtil jwtUtil) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.profileService = profileService;
        this.otpVisitor = otpVisitor;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginDto loginDto) throws Exception {
        Users user = getUser(loginDto.getUsername(), loginDto.getPassword());
        if (!profileService.isDebugMode()) {
            if (!getOtpStrategyTypeList().isEmpty()) {
                if (Utils.isNull(loginDto.getOtpStrategyTypeId()))
                    throw new ZarHubException(AuthenticationExceptionType.VERIFY_CODE_IS_NOT_VALID);
                OtpStrategyType otpStrategyType = OtpStrategyType.getItemById(loginDto.getOtpStrategyTypeId());
                checkOtpStrategyType(otpStrategyType);
                otpVisitor.verifyAppuserOtp(otpStrategyType
                        , user.getUsername()
                        , loginDto.getOtpCode());
            }
        }
        return tokenService.generateToken(user);
    }

    public String refreshToken(String token) throws Exception {
        Users user = (Users) JwtUtil.getTokenData(token);
        logout(token);
        return tokenService.generateToken(user);
    }

    public void logout(String token) throws Exception {
        if (Utils.isNull(token))
            throw new ZarHubException(AuthenticationExceptionType.TOKEN_IS_NULL);
        tokenService.removeTokenById(((Users) JwtUtil.getTokenData(token)).getId(), token);
    }

    public List<OtpStrategyType> getOtpStrategyTypeList() {
        List<OtpStrategyType> twoFactorTypes = new ArrayList<>();
//        twoFactorTypes.add(OtpStrategyType.SMS);
        return twoFactorTypes;
    }

    public void sendOtpForLogin(OtpRequestDto otpRequestDto) {
        if (profileService.isDebugMode())
            return;
        if (getOtpStrategyTypeList().isEmpty())
            throw new ZarHubException(AuthenticationExceptionType.ALL_OTP_STRATEGY_ARE_DISABLED);
        Users user = getUser(otpRequestDto.getUsername(), otpRequestDto.getPassword());
        OtpStrategyType otpStrategyType = OtpStrategyType.getItemById(otpRequestDto.getOtpStrategyTypeId());
        checkOtpStrategyType(otpStrategyType);
        otpVisitor.generateAndSendUserOtp(otpStrategyType, user.getUsername());
    }

    private Users getUser(String username, String password) {
        Optional<Users> user = repository.findAll(Users.class).stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
        if (!user.isPresent()) {
            throw new ZarHubException(AuthenticationExceptionType.USERNAME_PASSWORD_INVALID);
        }
        if (!profileService.isDebugMode()) {
            boolean validated = Utils.encodePassword(password)
                    .equalsIgnoreCase(user.get().getPassword());
            if (!validated) {
                throw new ZarHubException(AuthenticationExceptionType.USERNAME_PASSWORD_INVALID);
            }
            if (!user.get().getIsActive()) {
                throw new ZarHubException(AuthenticationExceptionType.USER_IS_NOT_ACTIVE);
            }
        }
        return user.get();
    }

    private void checkOtpStrategyType(OtpStrategyType otpStrategyType) {
        if (!getOtpStrategyTypeList().contains(otpStrategyType))
            throw new ZarHubException(AuthenticationExceptionType.OTP_STRATEGY_IS_DISABLED, new Object[]{otpStrategyType.getTitle()});
    }

}
