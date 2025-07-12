package org.zarhub.authentication.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Getter
@Builder
public class PermissionDto implements DtoConvertible {
    private Long id;
    @NotEmpty(fieldName = "name")
    private String name;
    @NotEmpty(fieldName = "url")
    private String url;
    @NotEmpty(fieldName = "isSensitive")
    private Boolean isSensitive;

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
