package org.zarhub.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;
import java.util.List;
import org.zarhub.validator.ValidateField;
import org.zarhub.validator.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Getter
@Builder
public class LoginDto implements DtoConvertible {
    @ValidateField(fieldName = "username", entityClass = Users.class)
    private String username;
    @NotEmpty(fieldName = "password")
    private String password;
    private Integer otpStrategyTypeId;
    private String otpCode;

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
