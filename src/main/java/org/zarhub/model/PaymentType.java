package org.zarhub.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.zarhub.baseInfo.customer.dto.CustomerDto;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "payment_type")
@Entity(name = "paymentType")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class PaymentType extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)", name = "NAME", nullable = false)
    private String name;
}
