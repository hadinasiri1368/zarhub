package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "VOUCHER_DETAIL")
@Entity(name = "voucherDetail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class VoucherDetail extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(10)", name = "VOUCHER_DATE", nullable = false)
    private String voucherDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_VOUCHER_ID")
    private Voucher voucher;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PRODUCT_ID")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_STORE_ID")
    private Store store;
    @Column(columnDefinition = "decimal(18,18)", name = "QUANTITY", nullable = false)
    private BigDecimal quantity;
    @Column(columnDefinition = "BIGINT", name = "MARKET_PRICE", nullable = false)
    private Long marketPrice;
    @Column(columnDefinition = "FLOAT", name = "PURCHASE_COEFFICIENT", nullable = false)
    private Float purchaseCoefficient;
    @Column(columnDefinition = "FLOAT", name = "SELL_COEFFICIENT", nullable = false)
    private Float sellCoefficient;
    @Column(columnDefinition = "BIGINT", name = "PURCHASE_MARGIN", nullable = false)
    private Long purchaseMargin;
    @Column(columnDefinition = "BIGINT", name = "SELL_MARGIN", nullable = false)
    private Long sellMargin;
    @Column(columnDefinition = "BIGINT", name = "DISCOUNT_AMOUNT", nullable = false)
    private Long discountAmount;
    @Column(columnDefinition = "BIGINT", name = "PRICE", nullable = false)
    private Long price;
}
