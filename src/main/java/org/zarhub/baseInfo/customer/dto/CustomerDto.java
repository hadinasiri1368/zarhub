package org.zarhub.baseInfo.customer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.model.Customer;
import org.zarhub.model.Person;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;

import java.util.List;

@Getter
@Setter
public class CustomerDto implements DtoConvertible {
    private Long id;
    @NotEmpty(fieldName = "code")
    private String code;
    @NotEmpty(fieldName = "person")
    private PersonDto person;
    @NotEmpty(fieldName = "creditAmount")
    private Long creditAmount;

    @Override
    public <T> T toEntity(Class<T> targetType, JpaRepository repository) {
        ObjectMapper objectMapper = new ObjectMapper();
        T entity = objectMapper.convertValue(this, targetType);

        if (entity instanceof Customer customer) {
            customer.setPerson(getPerson(repository));
        }

        return entity;
    }

    @Override
    public <T> List<T> toEntityList(Class<T> entityClass, JpaRepository repository) {
        return List.of();
    }

    private Person getPerson(JpaRepository repository) {
        return person.toEntity(Person.class, repository);
    }
}
