package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "USER_GROUP")
@Entity(name = "userGroup")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class UserGroup extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR2(50)", name = "NAME", nullable = false)
    private String name;
}
