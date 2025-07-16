package org.zarhub.authentication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDetailDto implements DtoConvertible {

    @NotEmpty(fieldName = "userGroupId")
    private Long userGroupId;
    @NotEmpty(fieldName = "userIds")
    private List<Long> userIds;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        return repository.findOne(targetType, userGroupId);
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        List<T> list = new ArrayList<>();
        for (Long userId : userIds) {
            list.add(repository.findOne(entityClass, userId));
        }
        return list;
    }
}
