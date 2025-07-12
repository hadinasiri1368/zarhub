package org.zarhub.authentication.otp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponseDto implements Serializable, DtoConvertible {
    private String verificationCode;

    private Long expirationTime;

    private Integer numberOfAttempts;

    private Boolean messageSend;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, targetType);
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        return List.of();
    }
}
