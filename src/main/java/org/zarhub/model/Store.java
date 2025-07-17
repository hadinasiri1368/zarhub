package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "STORE")
@Entity(name = "store")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Store extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(500)", name = "NAME", nullable = false)
    private String name;
    @Column(columnDefinition = "NVARCHAR(500)", name = "ADDRESS", nullable = false)
    private String address;
    @Column(columnDefinition = "NVARCHAR(50)", name = "MANAGER_NAME", nullable = false)
    private String managerName;
    @Column(columnDefinition = "NVARCHAR(50)", name = "MOBILE_NUMBER", nullable = false)
    private String mobileNumber;
    @Column(columnDefinition = "NVARCHAR(50)", name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_STORE_STATUS_ID")
    private StoreStatus storeStatus;
    @Column(columnDefinition = "FLOAT", name = "PURCHASE_COEFFICIENT", nullable = false)
    private Float purchaseCoefficient;
    @Column(columnDefinition = "FLOAT", name = "SELL_COEFFICIENT", nullable = false)
    private Float sellCoefficient;
    @Column(columnDefinition = "BIGINT", name = "PURCHASE_MARGIN", nullable = false)
    private Float purchaseMargin;
    @Column(columnDefinition = "BIGINT", name = "SELL_MARGIN", nullable = false)
    private Float sellMargin;
}
