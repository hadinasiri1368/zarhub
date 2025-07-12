package org.zarhub.authentication.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class UserRoleDto implements DtoConvertible {
    @NotEmpty(fieldName = "userId")
    private Long userId;
    @NotEmpty(fieldName = "roleIds")
    private List<Long> roleIds;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        return repository.findOne(targetType, userId);
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        List<T> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            list.add(repository.findOne(entityClass, roleId));
        }
        return list;
    }
}
