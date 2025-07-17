package org.zarhub.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.zarhub.baseInfo.customer.dto.CustomerDto;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "PAYMENT")
@Entity(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Payment extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(10)", name = "PAYMENT_DATE", nullable = false)
    private String paymentDate;
    @Column(columnDefinition = "NVARCHAR(8)", name = "PAYMENT_TIME", nullable = false)
    private String paymentTime;
    @Column(columnDefinition = "BIGINT", name = "PAYMNET_AMOUNT", nullable = false)
    private Long paymnetAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_CUSTOMER_ID")
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PAYMENT_TYPE_ID")
    private PaymentType paymentType;
    @Column(columnDefinition = "BIT", name = "IS_CONFIRM", nullable = false)
    private Boolean isConfirm;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_CONFIRMER_ID")
    private Users confirmer;
}
