package org.zarhub.authentication.permission.role;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleUserGroupDto implements DtoConvertible {
    private final JpaRepository repository;

    public RoleUserGroupDto(JpaRepository repository) {
        this.repository = repository;
    }

    @Setter
    @Getter
    private Long userGroupId;
    @Setter
    @Getter
    private List<Long> roleIds;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        return repository.findOne(targetType, userGroupId);
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        List<T> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            roles.add(repository.findOne(entityClass, roleId));
        }
        return roles;
    }
}
