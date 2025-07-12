package org.zarhub.authentication.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.model.Person;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;
import org.zarhub.validator.ValidateField;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements DtoConvertible {
    private Long id;
    @NotEmpty(fieldName = "isActive")
    private Boolean isActive;
    private Person person;
    @NotEmpty(fieldName = "username")
    private String username;
    @NotEmpty(fieldName = "password")
    private String password;

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
