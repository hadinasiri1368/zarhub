package org.zarhub.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.zarhub.baseInfo.customer.dto.CustomerDto;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "CUSTOMER")
@Entity(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Customer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)", name = "CODE", nullable = false)
    private String code;
    @Column(columnDefinition = "BIGINT", name = "CREDIT_AMOUNT", nullable = false)
    private Long creditAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PERSON_ID")
    private Person person;

    public CustomerDto toDto() {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerDto customerDto = objectMapper.convertValue(this, CustomerDto.class);
        customerDto.setPerson(person.toDto());
        return customerDto;
    }
}
