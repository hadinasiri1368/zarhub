package org.zarhub.authentication.otp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;
import org.zarhub.validator.ValidateField;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Getter
@Builder
public class OtpRequestDto implements DtoConvertible {
    @ValidateField(fieldName = "username", entityClass = Users.class)
    private String username;
    @NotEmpty(fieldName = "password")
    private String password;
    @NotEmpty(fieldName = "otpStrategyTypeId")
    private Integer otpStrategyTypeId;

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
