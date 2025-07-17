package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "STORE_PRODUCT")
@Entity(name = "storeProduct")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class StoreProduct extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_STORE_ID")
    private Store store;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PRODUCT_ID")
    private Product product;
    @Column(columnDefinition = "BIGINT", name = "AVAILABLE_QUANTITY", nullable = false)
    private Long availableQuantity;

}
