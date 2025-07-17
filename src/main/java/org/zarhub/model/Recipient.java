package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "RECIPIENT")
@Entity(name = "recipient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Recipient extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)", name = "NAME", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_VOUCHER_ID")
    private Voucher voucher;
    @Column(columnDefinition = "NVARCHAR(50)", name = "FIRST_NAME", nullable = false)
    private String firstName;
    @Column(columnDefinition = "NVARCHAR(50)", name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(columnDefinition = "NVARCHAR(10)", name = "NATIONAL_CODE", nullable = false)
    private String nationalCode;
    @Column(columnDefinition = "NVARCHAR(500)", name = "ADDRESS", nullable = false)
    private String address;
    @Column(columnDefinition = "NVARCHAR(50)", name = "MOBILE_NUMBER", nullable = false)
    private String mobileNumber;
    @Column(columnDefinition = "NVARCHAR(50)", name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;
}
