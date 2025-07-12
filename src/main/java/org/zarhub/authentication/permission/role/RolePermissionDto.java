package org.zarhub.authentication.permission.role;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.model.Role;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;
import org.zarhub.validator.ValidateField;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePermissionDto implements DtoConvertible {
    private final JpaRepository repository;

    public RolePermissionDto(JpaRepository repository) {
        this.repository = repository;
    }

    @Setter
    @Getter
    @NotEmpty(fieldName = "roleId")
    @ValidateField(fieldName = "roleId", entityClass = Role.class)
    private Long roleId;
    @Setter
    @Getter
    @NotEmpty(fieldName = "permissionIds")
    private List<Long> permissionIds;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        return repository.findOne(targetType, roleId);
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        List<T> entities = new ArrayList<>();
        for (Long id : permissionIds) {
            entities.add(repository.findOne(entityClass, id));
        }
        return entities;
    }
}
