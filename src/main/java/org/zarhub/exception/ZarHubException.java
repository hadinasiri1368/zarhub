package org.zarhub.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public class ZarHubException extends RuntimeException {
    private final HttpStatus status;
    private final Object[] params;

    public ZarHubException(GeneralExceptionType generalExceptionType) {
        super(generalExceptionType.getMessageKey());
        this.status = generalExceptionType.getHttpStatus();
        this.params = null;
    }

    public ZarHubException(GeneralExceptionType generalExceptionType, Object[] params) {
        super(generalExceptionType.getMessageKey());
        this.status = generalExceptionType.getHttpStatus();
        this.params = params;
    }

    public ZarHubException(AuthenticationExceptionType authenticationExceptionType) {
        super(authenticationExceptionType.getMessageKey());
        this.status = authenticationExceptionType.getHttpStatus();
        this.params = null;
    }

    public ZarHubException(AuthenticationExceptionType authenticationExceptionType, Object[] params) {
        super(authenticationExceptionType.getMessageKey());
        this.status = authenticationExceptionType.getHttpStatus();
        this.params = params;
    }

    public ZarHubException(ZarHubExceptionType zarHubExceptionType) {
        super(zarHubExceptionType.getMessageKey());
        this.status = zarHubExceptionType.getHttpStatus();
        this.params = null;
    }

    public ZarHubException(ZarHubExceptionType zarHubExceptionType, Object[] params) {
        super(zarHubExceptionType.getMessageKey());
        this.status = zarHubExceptionType.getHttpStatus();
        this.params = params;
    }

}
