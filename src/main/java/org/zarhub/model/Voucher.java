package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "VOUCHER")
@Entity(name = "Voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Voucher extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(10)", name = "VOUCHER_DATE", nullable = false)
    private String voucherDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_VOUCHER_TYPE_ID")
    private VoucherType voucherType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_customer_id")
    private Customer customer;
    @Column(columnDefinition = "BIGINT", name = "DISCOUNT_AMOUNT", nullable = false)
    private Long discountamount;
    @Column(columnDefinition = "BIGINT", name = "DELIVERY_FEE", nullable = false)
    private Long deliveryFee;
    @Column(columnDefinition = "BIGINT", name = "PRICE", nullable = false)
    private Long price;
}
