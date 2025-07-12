package org.zarhub.config.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.zarhub.authentication.AuthenticationService;
import org.zarhub.common.Utils;
import org.zarhub.constant.Consts;
import org.zarhub.constant.TimeFormat;
import org.zarhub.dto.ExceptionDto;
import org.zarhub.exception.ZarHubException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class Logout implements LogoutHandler {
    private final AuthenticationService authenticationService;

    public Logout(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        try {
            authenticationService.logout(Utils.getToken(request));
        } catch (ZarHubException e) {
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            try {
                writeResponse(response, currentTime, e.getMessage(), e.getParams());
            } catch (Exception exception) {
                log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                        HttpStatus.UNAUTHORIZED, e.getMessage(), currentTime, null);
            }
        } catch (Exception exception) {
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            try {
                writeResponse(response, currentTime, "unhandled_exception.error", null);
            } catch (Exception exception1) {
                log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                        HttpStatus.UNAUTHORIZED, exception1.getMessage(), currentTime, null);
            }
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private void writeResponse(HttpServletResponse response, String currentTime, String message, Object[] params) throws Exception {
        log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                HttpStatus.UNAUTHORIZED, message, currentTime, null);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(convertObjectToJson(ExceptionDto.builder()
                .code(message)
                .message(Utils.getMessage(message, params))
                .uuid(null)
                .time(currentTime)
                .build()));
    }
}
