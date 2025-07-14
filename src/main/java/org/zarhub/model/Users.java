package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "USERS")
@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Users extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "IS_ACTIVE", columnDefinition = "NUMBER(1)", nullable = false)
    private Boolean isActive;
    @Column(name = "IS_ADMIN", columnDefinition = "NUMBER(1)", nullable = false)
    private Boolean isAdmin;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "F_PERSON_ID")
    private Person person;
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(50)", nullable = false, unique = true)
    private String username;
    @Column(name = "PASSWORD", columnDefinition = "NVARCHAR2(200)", nullable = false)
    private String password;
}
