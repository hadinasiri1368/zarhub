package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;

import java.io.Serializable;

@Table(name = "PERMISSION")
@Entity(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class Permission extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(500)", name = "NAME", nullable = false)
    private String name;
    @Column(columnDefinition = "NVARCHAR(500)", name = "URL", nullable = false)
    private String url;
    @Column(columnDefinition = "NUMBER(1)", name = "IS_SENSITIVE", nullable = false)
    private Boolean isSensitive;
}
