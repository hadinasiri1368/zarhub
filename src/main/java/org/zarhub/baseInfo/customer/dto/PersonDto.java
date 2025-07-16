package org.zarhub.baseInfo.customer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.zarhub.dto.DtoConvertible;
import org.zarhub.repository.JpaRepository;
import org.zarhub.validator.NotEmpty;
import org.zarhub.validator.ValidNationalCode;
import org.zarhub.validator.ValidPersianDate;

import java.util.List;

@Getter
@Setter
public class PersonDto implements DtoConvertible {
    private Long id;
    @NotEmpty(fieldName = "name")
    private String name;
    @NotEmpty(fieldName = "family")
    private String family;
    @NotEmpty(fieldName = "birthDate")
    @ValidPersianDate(fieldName = "birthDate")
    private String birthDate;
    @NotEmpty(fieldName = "nationalCode")
    @ValidNationalCode()
    private String nationalCode;
    @NotEmpty(fieldName = "mobileNumber")
    private String mobileNumber;
    @NotEmpty(fieldName = "isForeigners")
    private Boolean isForeigners;
    @NotEmpty(fieldName = "isCompany")
    private Boolean isCompany;
    private String companyNationalCode;
    private String establishDate;
    private String managerName;
    private String managerLastName;
    private String managerNationalCode;
    private String managerBirthDate;
    private String managerMobileNumber;
    private String idNumber;

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
