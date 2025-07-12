package org.zarhub.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zarhub.authentication.permission.PermissionService;
import org.zarhub.common.JwtUtil;
import org.zarhub.common.Utils;
import org.zarhub.config.authentication.TokenService;
import org.zarhub.config.request.RequestContext;
import org.zarhub.constant.Consts;
import org.zarhub.constant.TimeFormat;
import org.zarhub.dto.ExceptionDto;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final PermissionService permissionService;

    public AuthenticationFilter(TokenService tokenService
            , PermissionService permissionService) {
        this.tokenService = tokenService;
        this.permissionService = permissionService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            setRequest(request);
            String token = Utils.getToken(request);
            if (Utils.isNull(token))
                throw new ZarHubException(AuthenticationExceptionType.TOKEN_IS_NULL);
            if (Utils.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                Users user = tokenService.getTokenData(RequestContext.getTokenId(), token);
                permissionService.validateUserAccess(user, request.getRequestURI());
                UserDetails userDetails = getUserDetails(user);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }
        } catch (ZarHubException e) {
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                    HttpStatus.UNAUTHORIZED, e.getMessage(), currentTime, null);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(convertObjectToJson(ExceptionDto.builder()
                    .code(e.getMessage())
                    .message(Utils.getMessage(e.getMessage(), e.getParams()))
                    .uuid(null)
                    .time(currentTime)
                    .build()));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        try {
            if (request.getMethod().equals("OPTIONS"))
                return true;
            if (permissionService.isBypassedUrl(request.getRequestURI()))
                return true;
            return permissionService.isSensitiveUrl(request.getRequestURI());
        } catch (Exception e) {
            HttpStatus httpStatus = e instanceof ZarHubException ? ((ZarHubException) e).getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                    httpStatus, e.getMessage(), currentTime, null);
            return false;
        }
    }

    private UserDetails getUserDetails(Users user) {
        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("admin"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getIsActive(), false, false, false, auths);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private void printLog(HttpServletRequest request, String uuid, String tenantId) {
        String startTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
        log.info(String.format("RequestURL: %s | Start Date : %s | uuid : %s | schemaId : %s", request.getRequestURL(), startTime, uuid, tenantId));
        String endTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
        log.info(String.format("RequestURL: %s | Start Date : %s | End Date : %s | uuid : %s", request.getRequestURL(), startTime, endTime, uuid));
    }

    private void setRequest(HttpServletRequest request) {
        String token = Utils.getToken(request);
        Users user = (Users) JwtUtil.getTokenData(token);
        RequestContext.setUser(user);
        RequestContext.setToken(token);
    }
}
